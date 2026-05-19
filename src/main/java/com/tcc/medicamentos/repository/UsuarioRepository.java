package com.tcc.medicamentos.repository;

import com.tcc.medicamentos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O Spring é tão inteligente que só de escrevermos isso, ele já sabe como buscar no banco!
    Usuario findByLogin(String login);
}