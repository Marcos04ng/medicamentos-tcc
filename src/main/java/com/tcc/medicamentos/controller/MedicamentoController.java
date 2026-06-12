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


    @GetMapping("/")
    public String menuPrincipal() {
        return "home";
    }


    @GetMapping("/medicamentos")
    public String listarMedicamentos(Model model) {
        List<Medicamento> lista = repository.findAll();
        model.addAttribute("listaMedicamentos", lista);
        return "index";
    }


    @GetMapping("/medicamentos/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("medicamento", new Medicamento());
        return "form";
    }


    @PostMapping("/medicamentos/salvar")
    public String salvarMedicamento(Medicamento medicamento) {
        repository.save(medicamento);
        return "redirect:/medicamentos";
    }

    @GetMapping("/medicamentos/excluir/{id}")
    public String excluirMedicamento(@org.springframework.web.bind.annotation.PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/medicamentos";
    }

    @GetMapping("/medicamentos/editar/{id}")
    public String editarMedicamento(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {

        Medicamento remedio = repository.findById(id).orElse(null);
        model.addAttribute("medicamento", remedio);
        return "form";
    }

    @GetMapping("/medicamentos/tomar/{id}")
    public String registrarUso(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes attributes) {

        Medicamento remedio = repository.findById(id).orElse(null);

        if (remedio != null) {
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

    @org.springframework.web.bind.annotation.GetMapping("/home")
    public String exibirHome(org.springframework.ui.Model model) {
        // Pega o total geral de remédios
        long totalRemedios = repository.count();

        // Pega quantos estão com estoque baixo (menos de 5 unidades)
        long estoqueBaixo = repository.countByQuantidadeLessThan(5);

        // Pega quantos já estão vencidos (comparando com a data de hoje)
        long vencidos = repository.countByDataValidadeBefore(java.time.LocalDate.now());

        // Manda esses números para o HTML
        model.addAttribute("total", totalRemedios);
        model.addAttribute("baixo", estoqueBaixo);
        model.addAttribute("vencidos", vencidos);

        return "home";
    }
}