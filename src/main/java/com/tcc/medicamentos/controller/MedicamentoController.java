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
}