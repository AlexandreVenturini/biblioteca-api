package com.example.biblioteca_api.dto;

import com.example.biblioteca_api.model.Emprestimo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmprestimoResponse(
        Long id,
        Long livroId,
        String titulo,
        Long leitorId,
        String leitor,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        BigDecimal multa
) {

    public static EmprestimoResponse de(Emprestimo emprestimo, String titulo, String leitor) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getLivroId(),
                titulo,
                emprestimo.getLeitorId(),
                leitor,
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDataDevolucao(),
                emprestimo.getMulta()
        );
    }
}
