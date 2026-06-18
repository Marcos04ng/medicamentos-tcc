package com.tcc.medicamentos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String dosagem;
    private Integer quantidade;
    private LocalDate dataValidade;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    // --- GETTERS E SETTERS --- //

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDosagem() {
        return dosagem;
    }
    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }
    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}