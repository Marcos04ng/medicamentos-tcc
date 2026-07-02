package com.tcc.medicamentos.repository;

import com.tcc.medicamentos.model.Medicamento;
import com.tcc.medicamentos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {


    List<Medicamento> findByUsuario(Usuario usuario);

    long countByUsuario(Usuario usuario);
    long countByUsuarioAndQuantidadeLessThan(Usuario usuario, int quantidade);
    long countByUsuarioAndDataValidadeBefore(Usuario usuario, LocalDate data);
}