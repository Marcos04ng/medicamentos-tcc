package com.tcc.medicamentos.repository;

import com.tcc.medicamentos.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    long countByQuantidadeLessThan(int quantidade);
    long countByDataValidadeBefore(LocalDate data);

    long countByQuantidadeLessThanEqual(int i);
}
