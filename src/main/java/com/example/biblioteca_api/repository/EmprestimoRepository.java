package com.example.biblioteca_api.repository;

import com.example.biblioteca_api.model.Emprestimo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository {

    List<Emprestimo> findAll();

    Optional<Emprestimo> findById(Long id);

    Emprestimo save(Emprestimo emprestimo);

    long countByLivroIdAndDataDevolucaoIsNull(Long livroId);

    long countByLeitorIdAndDataDevolucaoIsNull(Long leitorId);

    boolean existsByLivroIdAndDataDevolucaoIsNull(Long livroId);

    boolean existsByLeitorIdAndDataDevolucaoIsNull(Long leitorId);

    boolean existsByLeitorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(Long leitorId, LocalDate data);
}
