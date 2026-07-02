package com.tcc.medicamentos.repository;

import com.tcc.medicamentos.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {
    // Busca um laboratório pelo nome
    Laboratorio findByNome(String nome);
}