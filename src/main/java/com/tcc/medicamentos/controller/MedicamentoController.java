package com.tcc.medicamentos.controller;

import com.tcc.medicamentos.model.Medicamento;
import com.tcc.medicamentos.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MedicamentoController {

    @Autowired
    private MedicamentoRepository repository;

    // 1. Rota do Menu Principal (Dashboard)
    @GetMapping("/")
    public String menuPrincipal() {
        return "home";
    }

    // 2. Rota da Tabela de Medicamentos
    @GetMapping("/medicamentos")
    public String listarMedicamentos(Model model) {
        List<Medicamento> lista = repository.findAll();
        model.addAttribute("listaMedicamentos", lista);
        return "index";
    }

    // 3. A ROTA QUE ESTAVA A FALTAR! (Abre a tela de formulário)
    @GetMapping("/medicamentos/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("medicamento", new Medicamento());
        return "form";
    }

    // 4. Recebe os dados e Salva no Banco de Dados
    @PostMapping("/medicamentos/salvar")
    public String salvarMedicamento(Medicamento medicamento) {
        repository.save(medicamento);
        return "redirect:/medicamentos";
    }
    // Lembre-se de importar o @PathVariable lá em cima se o IntelliJ pedir (Alt+Enter)
    @GetMapping("/medicamentos/excluir/{id}")
    public String excluirMedicamento(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id); // Manda o cozinheiro apagar a comida da despensa
        return "redirect:/medicamentos"; // Recarrega a página da tabela
    }
    // Rota para abrir o formulário com os dados do remédio selecionado
    @GetMapping("/medicamentos/editar/{id}")
    public String editarMedicamento(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        // Busca o remédio no banco pelo ID
        Medicamento remedio = repository.findById(id).orElse(null);
        // Coloca na bandeja e manda para a tela de formulário
        model.addAttribute("medicamento", remedio);
        return "form";
    }
    // Rota para Registrar o Uso (Diminuir 1 do estoque)
    @GetMapping("/medicamentos/tomar/{id}")
    public String registrarUso(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes attributes) {

        Medicamento remedio = repository.findById(id).orElse(null);

        if (remedio != null) {
            if (remedio.getQuantidade() > 0) {
                // Diminui 1 da quantidade
                remedio.setQuantidade(remedio.getQuantidade() - 1);
                repository.save(remedio);

                // Manda a notificação verde de sucesso para a tela
                attributes.addFlashAttribute("mensagemSucesso", "Dose de " + remedio.getNome() + " registrada! Estoque atualizado.");
            } else {
                // Manda a notificação amarela de erro (já está zerado)
                attributes.addFlashAttribute("mensagemErro", "Atenção: O estoque de " + remedio.getNome() + " já está zerado!");
            }
        }

        return "redirect:/medicamentos";
    }
    // Rota para abrir o nosso Dashboard (Home)
    @org.springframework.web.bind.annotation.GetMapping("/home")
    public String exibirHome(org.springframework.ui.Model model) {
        // 1. Pega o total geral de remédios
        long totalRemedios = repository.count();

        // 2. Pega quantos estão com estoque baixo (menos de 5 unidades)
        long estoqueBaixo = repository.countByQuantidadeLessThan(5);

        // 3. Pega quantos já estão vencidos (comparando com a data de hoje)
        long vencidos = repository.countByDataValidadeBefore(java.time.LocalDate.now());

        // 4. Manda esses números para o HTML
        model.addAttribute("total", totalRemedios);
        model.addAttribute("baixo", estoqueBaixo);
        model.addAttribute("vencidos", vencidos);

        return "home";
    }
}