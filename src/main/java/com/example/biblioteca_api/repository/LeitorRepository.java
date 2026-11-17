package com.example.biblioteca_api.repository;

import com.example.biblioteca_api.model.Leitor;

import java.util.List;
import java.util.Optional;

public interface LeitorRepository {

    List<Leitor> findAll();

    Optional<Leitor> findById(Long id);

    Leitor save(Leitor leitor);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
