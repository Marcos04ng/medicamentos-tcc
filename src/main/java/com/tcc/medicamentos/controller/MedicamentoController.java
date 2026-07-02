package com.tcc.medicamentos.controller;

import com.tcc.medicamentos.model.HistoricoUso;
import com.tcc.medicamentos.model.Medicamento;
import com.tcc.medicamentos.model.Usuario;
import com.tcc.medicamentos.repository.HistoricoUsoRepository;
import com.tcc.medicamentos.repository.MedicamentoRepository;
import com.tcc.medicamentos.repository.UsuarioRepository;
import com.tcc.medicamentos.repository.LaboratorioRepository; // Import necessário
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class MedicamentoController {

    @Autowired
    private MedicamentoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LaboratorioRepository laboratorioRepository; // Injetado para os Laboratórios

    @Autowired
    private HistoricoUsoRepository historicoUsoRepository;

    // Método auxiliar privado para buscar o usuário logado
    private Usuario getUsuarioLogado(Principal principal) {
        String login = principal.getName();
        return usuarioRepository.findByLogin(login);
    }

    @GetMapping({"/", "/home"})
    public String exibirHome(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        long totalRemedios = repository.countByUsuario(usuario);
        long estoqueBaixo = repository.countByUsuarioAndQuantidadeLessThan(usuario, 5);
        long vencidos = repository.countByUsuarioAndDataValidadeBefore(usuario, LocalDate.now());

        //Define o início e o fim do dia de hoje para filtrar o histórico
        java.time.LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        java.time.LocalDateTime fimDia = LocalDate.now().atTime(23, 59, 59);

        // Busca apenas os registros que o usuário tomou HOJE
        List<HistoricoUso> historicoHoje = historicoUsoRepository
                .findByUsuarioAndDataHoraBetweenOrderByDataHoraDesc(usuario, inicioDia, fimDia);

        model.addAttribute("total", totalRemedios);
        model.addAttribute("baixo", estoqueBaixo);
        model.addAttribute("vencidos", vencidos);
        model.addAttribute("historicoHoje", historicoHoje); // Envia a lista para o HTML

        return "home";
    }

    @GetMapping("/medicamentos")
    public String listarMedicamentos(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        List<Medicamento> lista = repository.findByUsuario(usuario);
        model.addAttribute("listaMedicamentos", lista);
        return "index";
    }

    @GetMapping("/medicamentos/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("medicamento", new Medicamento());

        // Envia a lista de laboratórios para aparecer no Select do HTML
        model.addAttribute("listaLaboratorios", laboratorioRepository.findAll());

        return "form";
    }

    @PostMapping("/medicamentos/salvar")
    public String salvarMedicamento(Medicamento medicamento, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        medicamento.setUsuario(usuario);

        repository.save(medicamento);
        return "redirect:/medicamentos";
    }

    @GetMapping("/medicamentos/excluir/{id}")
    public String excluirMedicamento(@PathVariable Long id, Principal principal) {
        Medicamento remedio = repository.findById(id).orElse(null);
        Usuario usuario = getUsuarioLogado(principal);

        if (remedio != null && remedio.getUsuario().getId().equals(usuario.getId())) {
            repository.deleteById(id);
        }
        return "redirect:/medicamentos";
    }

    @GetMapping("/medicamentos/editar/{id}")
    public String editarMedicamento(@PathVariable Long id, Model model, Principal principal) {
        Medicamento remedio = repository.findById(id).orElse(null);
        Usuario usuario = getUsuarioLogado(principal);

        if (remedio != null && remedio.getUsuario().getId().equals(usuario.getId())) {
            model.addAttribute("medicamento", remedio);
            model.addAttribute("listaLaboratorios", laboratorioRepository.findAll()); // Envia também na edição
            return "form";
        }
        return "redirect:/medicamentos";
    }

    @GetMapping("/medicamentos/tomar/{id}")
    public String registrarUso(@PathVariable Long id, RedirectAttributes attributes, Principal principal) {
        Medicamento remedio = repository.findById(id).orElse(null);
        Usuario usuario = getUsuarioLogado(principal);

        if (remedio != null && remedio.getUsuario().getId().equals(usuario.getId())) {
            if (remedio.getQuantidade() > 0) {

                // Define o início e o fim do dia de hoje para verificar repetição
                java.time.LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
                java.time.LocalDateTime fimDia = LocalDate.now().atTime(23, 59, 59);

                // Busca se já existe algum registro desse mesmo remédio para esse usuário hoje
                List<HistoricoUso> jáTomouHoje = historicoUsoRepository
                        .findByUsuarioAndDataHoraBetweenOrderByDataHoraDesc(usuario, inicioDia, fimDia);

                // Filtra na lista se o remédio específico já foi tomado hoje
                boolean duplicado = jáTomouHoje.stream()
                        .anyMatch(h -> h.getMedicamento().getId().equals(remedio.getId()));

                // 1. Diminui a quantidade no estoque e grava o histórico normalmente
                remedio.setQuantidade(remedio.getQuantidade() - 1);
                repository.save(remedio);

                HistoricoUso historico = new HistoricoUso();
                historico.setMedicamento(remedio);
                historico.setUsuario(usuario);
                historico.setDataHora(java.time.LocalDateTime.now());
                historicoUsoRepository.save(historico);

                // 2. Define qual mensagem exibir com base na checagem
                if (duplicado) {
                    // Se for a segunda vez ou mais, manda um alerta de atenção!
                    attributes.addFlashAttribute("mensagemAlertaDuplicado",
                            " Atenção: Cuidado, você já tomou o medicamento '" + remedio.getNome() + "' hoje!");
                } else {
                    // Se for a primeira vez do dia, manda a mensagem normal de sucesso
                    attributes.addFlashAttribute("mensagemSucesso", "Dose de " + remedio.getNome() + " registrada com sucesso!");
                }

            } else {
                attributes.addFlashAttribute("mensagemErro", "Atenção: O estoque de " + remedio.getNome() + " já está zerado!");
            }
        }
        return "redirect:/medicamentos";
    }

    @GetMapping("/historico/limpar-hoje")
    public String limparHistoricoHoje(RedirectAttributes attributes, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        // Define o início e o fim do dia de hoje para apagar só o que foi feito hoje
        java.time.LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        java.time.LocalDateTime fimDia = LocalDate.now().atTime(23, 59, 59);

        // Busca os registros de hoje do usuário logado
        List<HistoricoUso> historicoHoje = historicoUsoRepository
                .findByUsuarioAndDataHoraBetweenOrderByDataHoraDesc(usuario, inicioDia, fimDia);

        if (!historicoHoje.isEmpty()) {
            // Deleta todos os registros encontrados da lista
            historicoUsoRepository.deleteAll(historicoHoje);
            attributes.addFlashAttribute("mensagemSucesso", "O histórico de uso de hoje foi limpo com sucesso!");
        } else {
            attributes.addFlashAttribute("mensagemErro", "Não há nenhum registro de histórico para limpar hoje.");
        }

        return "redirect:/home";
    }

}