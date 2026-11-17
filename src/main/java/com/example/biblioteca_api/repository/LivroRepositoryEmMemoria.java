package com.example.biblioteca_api.repository;

import com.example.biblioteca_api.model.Livro;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LivroRepositoryEmMemoria implements LivroRepository {

    private final Map<Long, Livro> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<Livro> findAll() {
        return dados.values().stream()
                .sorted(Comparator.comparing(Livro::getId))
                .toList();
    }

    @Override
    public Optional<Livro> findById(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Livro save(Livro livro) {
        if (Objects.isNull(livro.getId())) {
            livro.setId(sequencia.incrementAndGet());
        }
        dados.put(livro.getId(), livro);

        return livro;
    }

    @Override
    public void deleteById(Long id) {
        dados.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return dados.containsKey(id);
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return dados.values().stream()
                .anyMatch(livro -> livro.getIsbn().equals(isbn));
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        return dados.values().stream()
                .anyMatch(livro -> livro.getIsbn().equals(isbn) && !livro.getId().equals(id));
    }
}
