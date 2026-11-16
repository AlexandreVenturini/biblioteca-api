package com.example.biblioteca_api.service;

import com.example.biblioteca_api.dto.LivroRequest;
import com.example.biblioteca_api.dto.LivroResponse;
import com.example.biblioteca_api.model.Livro;
import com.example.biblioteca_api.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public List<LivroResponse> listar(String autor, Boolean disponivel) {
        return livroRepository.findAll().stream()
                .filter(livro -> Objects.isNull(autor)
                        || livro.getAutor().toLowerCase().contains(autor.toLowerCase()))
                .map(this::paraResponse)
                .filter(livro -> !Boolean.TRUE.equals(disponivel) || livro.quantidadeDisponivel() > 0)
                .toList();
    }

    public Optional<LivroResponse> buscarPorId(Long id) {
        return livroRepository.findById(id).map(this::paraResponse);
    }

    public LivroResponse cadastrar(LivroRequest request) {
        if (livroRepository.existsByIsbn(request.isbn())) {
            throw new IllegalStateException("Já existe um livro com o ISBN " + request.isbn());
        }
        Livro livro = new Livro(null, request.titulo(), request.autor(), request.isbn(),
                request.anoPublicacao(), request.quantidadeTotal());

        return paraResponse(livroRepository.save(livro));
    }

    public Optional<LivroResponse> atualizar(Long id, LivroRequest request) {
        return livroRepository.findById(id)
                .map(livro -> {
                    if (livroRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
                        throw new IllegalStateException("Já existe um livro com o ISBN " + request.isbn());
                    }
                    livro.setTitulo(request.titulo());
                    livro.setAutor(request.autor());
                    livro.setIsbn(request.isbn());
                    livro.setAnoPublicacao(request.anoPublicacao());
                    livro.setQuantidadeTotal(request.quantidadeTotal());

                    return paraResponse(livroRepository.save(livro));
                });
    }

    public boolean remover(Long id) {
        if (!livroRepository.existsById(id)) {
            return false;
        }
        livroRepository.deleteById(id);

        return true;
    }

    private LivroResponse paraResponse(Livro livro) {
        return LivroResponse.de(livro, calcularQuantidadeDisponivel(livro));
    }

    private int calcularQuantidadeDisponivel(Livro livro) {
        return livro.getQuantidadeTotal();
    }
}
