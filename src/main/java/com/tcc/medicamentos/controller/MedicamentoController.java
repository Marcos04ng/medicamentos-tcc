package com.tcc.medicamentos.controller;

import com.tcc.medicamentos.model.Medicamento;
import com.tcc.medicamentos.model.Usuario;
import com.tcc.medicamentos.repository.MedicamentoRepository;
import com.tcc.medicamentos.repository.UsuarioRepository;
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

        model.addAttribute("total", totalRemedios);
        model.addAttribute("baixo", estoqueBaixo);
        model.addAttribute("vencidos", vencidos);

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
                remedio.setQuantidade(remedio.getQuantidade() - 1);
                repository.save(remedio);
                attributes.addFlashAttribute("mensagemSucesso", "Dose de " + remedio.getNome() + " registrada! Estoque atualizado.");
            } else {
                attributes.addFlashAttribute("mensagemErro", "Atenção: O estoque de " + remedio.getNome() + " já está zerado!");
            }
        }
        return "redirect:/medicamentos";
    }
}