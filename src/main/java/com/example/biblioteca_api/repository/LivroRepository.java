package com.example.biblioteca_api.repository;

import com.example.biblioteca_api.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroRepository {

    List<Livro> findAll();

    Optional<Livro> findById(Long id);

    Livro save(Livro livro);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
