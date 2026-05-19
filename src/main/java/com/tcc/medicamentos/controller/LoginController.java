package com.tcc.medicamentos.controller;

import com.tcc.medicamentos.model.Usuario;
import com.tcc.medicamentos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Construtor para o Java injetar as ferramentas necessárias
    public LoginController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String exibirTelaCadastro() {
        return "cadastro"; // Vai procurar o arquivo cadastro.html
    }

    @PostMapping("/cadastro/salvar")
    public String salvarNovoUsuario(Usuario usuario, RedirectAttributes attributes) {
        // 1. Criptografa a senha antes de salvar!
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        // 2. Salva no banco de dados
        usuarioRepository.save(usuario);

        // 3. Avisa que deu certo e volta para o login
        attributes.addFlashAttribute("mensagemSucesso", "Conta criada com sucesso! Faça seu login.");
        return "redirect:/login";
    }
}