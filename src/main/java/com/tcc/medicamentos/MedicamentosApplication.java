package com.tcc.medicamentos;

import com.tcc.medicamentos.model.Usuario;
import com.tcc.medicamentos.model.Laboratorio;
import com.tcc.medicamentos.repository.UsuarioRepository;
import com.tcc.medicamentos.repository.LaboratorioRepository;
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

	@Bean
	public CommandLineRunner criarDadosIniciais(UsuarioRepository usuarioRepo, LaboratorioRepository labRepo, PasswordEncoder encoder) {
		return args -> {
			// 1. Cria o usuário Admin padrão se o banco estiver vazio
			// Usar usuarioRepo em vez de repository
			if (usuarioRepo.count() == 0) {
				Usuario admin = new Usuario();
				admin.setLogin("admin");
				admin.setSenha(encoder.encode("123456"));
				usuarioRepo.save(admin);
			}

			// 2. Cria laboratórios padrão se não houver nenhum
			if (labRepo.count() == 0) {
				String[] laboratorios = {"EMS", "Medley", "Eurofarma", "Aché", "Neo Química"};
				for (String nomeLab : laboratorios) {
					Laboratorio lab = new Laboratorio();
					lab.setNome(nomeLab);
					labRepo.save(lab);
				}
			}
		};
	}
}