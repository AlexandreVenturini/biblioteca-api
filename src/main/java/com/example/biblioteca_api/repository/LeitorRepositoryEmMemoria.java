package com.example.biblioteca_api.repository;

import com.example.biblioteca_api.model.Leitor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LeitorRepositoryEmMemoria implements LeitorRepository {

    private final Map<Long, Leitor> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public List<Leitor> findAll() {
        return dados.values().stream()
                .sorted(Comparator.comparing(Leitor::getId))
                .toList();
    }

    @Override
    public Optional<Leitor> findById(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Leitor save(Leitor leitor) {
        if (Objects.isNull(leitor.getId())) {
            leitor.setId(sequencia.incrementAndGet());
        }
        dados.put(leitor.getId(), leitor);

        return leitor;
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
    public boolean existsByEmailIgnoreCase(String email) {
        return dados.values().stream()
                .anyMatch(leitor -> leitor.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id) {
        return dados.values().stream()
                .anyMatch(leitor -> leitor.getEmail().equalsIgnoreCase(email) && !leitor.getId().equals(id));
    }
}
