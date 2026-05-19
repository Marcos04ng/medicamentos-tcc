package com.tcc.medicamentos;

import com.tcc.medicamentos.model.Usuario;
import com.tcc.medicamentos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MedicamentosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicamentosApplication.class, args);
	}

	// 👇 Cria o usuário "admin" e senha "123456" automaticamente na primeira vez 👇
	@Bean
	public CommandLineRunner criarUsuarioPadrao(UsuarioRepository repository, PasswordEncoder encoder) {
		return args -> {
			if (repository.findByLogin("admin") == null) {
				Usuario admin = new Usuario();
				admin.setLogin("admin");
				admin.setSenha(encoder.encode("123456")); // Senha super segura, só que não kkkk
				repository.save(admin);
			}
		};
	}
}