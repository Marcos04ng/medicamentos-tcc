package com.tcc.medicamentos.repository;

import com.tcc.medicamentos.model.HistoricoUso;
import com.tcc.medicamentos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricoUsoRepository extends JpaRepository<HistoricoUso, Long> {

    // Busca o histórico do usuário logado ordenando do mais recente para o mais antigo
    List<HistoricoUso> findByUsuarioOrderByDataHoraDesc(Usuario usuario);

    // Busca o histórico filtrado por data/hora (ideal para pegar "só os de hoje")
    List<HistoricoUso> findByUsuarioAndDataHoraBetweenOrderByDataHoraDesc(
            Usuario usuario, LocalDateTime inicio, LocalDateTime fim);
}