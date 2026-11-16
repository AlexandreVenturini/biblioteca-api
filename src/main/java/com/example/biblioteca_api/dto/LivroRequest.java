package com.example.biblioteca_api.dto;

public record LivroRequest(
        String titulo,
        String autor,
        String isbn,
        Integer anoPublicacao,
        Integer quantidadeTotal
) {
}
