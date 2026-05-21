package com.tcc.medicamentos.repository;

import com.tcc.medicamentos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //palavra First pega só 1 e ignorar os clones
    Usuario findFirstByLogin(String login);
}