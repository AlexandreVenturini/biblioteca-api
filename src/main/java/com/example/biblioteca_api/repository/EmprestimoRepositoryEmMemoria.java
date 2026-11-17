package com.example.biblioteca_api.repository;

import com.example.biblioteca_api.model.Emprestimo;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EmprestimoRepositoryEmMemoria implements EmprestimoRepository {

    private final Map<Long, Emprestimo> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<Emprestimo> findAll() {
        return dados.values().stream()
                .sorted(Comparator.comparing(Emprestimo::getId))
                .toList();
    }

    @Override
    public Optional<Emprestimo> findById(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Emprestimo save(Emprestimo emprestimo) {
        if (Objects.isNull(emprestimo.getId())) {
            emprestimo.setId(sequencia.incrementAndGet());
        }
        dados.put(emprestimo.getId(), emprestimo);

        return emprestimo;
    }

    @Override
    public long countByLivroIdAndDataDevolucaoIsNull(Long livroId) {
        return dados.values().stream()
                .filter(emprestimo -> emprestimo.getLivroId().equals(livroId) && emprestimo.isAtivo())
                .count();
    }

    @Override
    public long countByLeitorIdAndDataDevolucaoIsNull(Long leitorId) {
        return dados.values().stream()
                .filter(emprestimo -> emprestimo.getLeitorId().equals(leitorId) && emprestimo.isAtivo())
                .count();
    }

    @Override
    public boolean existsByLivroIdAndDataDevolucaoIsNull(Long livroId) {
        return dados.values().stream()
                .anyMatch(emprestimo -> emprestimo.getLivroId().equals(livroId) && emprestimo.isAtivo());
    }

    @Override
    public boolean existsByLeitorIdAndDataDevolucaoIsNull(Long leitorId) {
        return dados.values().stream()
                .anyMatch(emprestimo -> emprestimo.getLeitorId().equals(leitorId) && emprestimo.isAtivo());
    }

    @Override
    public boolean existsByLeitorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(Long leitorId, LocalDate data) {
        return dados.values().stream()
                .anyMatch(emprestimo -> emprestimo.getLeitorId().equals(leitorId) && emprestimo.isAtrasado(data));
    }
}
