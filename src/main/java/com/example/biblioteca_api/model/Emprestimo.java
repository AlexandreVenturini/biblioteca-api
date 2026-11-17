package com.example.biblioteca_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {

    private Long id;
    private Long livroId;
    private Long leitorId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private BigDecimal multa;

    public boolean isAtivo() {
        return Objects.isNull(dataDevolucao);
    }

    public boolean isAtrasado(LocalDate hoje) {
        return isAtivo() && hoje.isAfter(dataPrevistaDevolucao);
    }
}
