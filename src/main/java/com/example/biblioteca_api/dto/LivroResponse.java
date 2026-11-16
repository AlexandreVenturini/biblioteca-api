package com.example.biblioteca_api.dto;

import com.example.biblioteca_api.model.Livro;

public record LivroResponse(
        Long id,
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Integer quantidadeTotal,
        Integer quantidadeDisponivel
) {

    public static LivroResponse de(Livro livro, int quantidadeDisponivel) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getAnoPublicacao(),
                livro.getQuantidadeTotal(),
                quantidadeDisponivel
        );
    }
}
