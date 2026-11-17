package com.example.biblioteca_api.service;

import com.example.biblioteca_api.dto.LivroRequest;
import com.example.biblioteca_api.dto.LivroResponse;
import com.example.biblioteca_api.exception.ConflitoException;
import com.example.biblioteca_api.exception.RecursoNaoEncontradoException;
import com.example.biblioteca_api.model.Livro;
import com.example.biblioteca_api.repository.EmprestimoRepository;
import com.example.biblioteca_api.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    public List<LivroResponse> listar(String autor, Boolean disponivel) {
        return livroRepository.findAll().stream()
                .filter(livro -> Objects.isNull(autor)
                        || livro.getAutor().toLowerCase().contains(autor.toLowerCase()))
                .map(this::paraResponse)
                .filter(livro -> !Boolean.TRUE.equals(disponivel) || livro.quantidadeDisponivel() > 0)
                .toList();
    }

    public LivroResponse buscarPorId(Long id) {
        return paraResponse(buscarLivro(id));
    }

    public LivroResponse cadastrar(LivroRequest request) {
        if (livroRepository.existsByIsbn(request.isbn())) {
            throw new ConflitoException("Já existe um livro com o ISBN " + request.isbn());
        }
        Livro livro = new Livro(null, request.titulo(), request.autor(), request.isbn(),
                request.anoPublicacao(), request.quantidadeTotal());

        return paraResponse(livroRepository.save(livro));
    }

    public LivroResponse atualizar(Long id, LivroRequest request) {
        Livro livro = buscarLivro(id);
        if (livroRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new ConflitoException("Já existe um livro com o ISBN " + request.isbn());
        }
        long ativos = emprestimoRepository.countByLivroIdAndDataDevolucaoIsNull(id);
        if (request.quantidadeTotal() < ativos) {
            throw new ConflitoException("A quantidade total não pode ser menor que os "
                    + ativos + " empréstimos ativos do livro");
        }
        livro.setTitulo(request.titulo());
        livro.setAutor(request.autor());
        livro.setIsbn(request.isbn());
        livro.setAnoPublicacao(request.anoPublicacao());
        livro.setQuantidadeTotal(request.quantidadeTotal());

        return paraResponse(livroRepository.save(livro));
    }

    public void remover(Long id) {
        Livro livro = buscarLivro(id);
        if (emprestimoRepository.existsByLivroIdAndDataDevolucaoIsNull(id)) {
            throw new ConflitoException("O livro " + livro.getTitulo()
                    + " possui empréstimo ativo e não pode ser removido");
        }
        livroRepository.deleteById(id);
    }

    private Livro buscarLivro(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", id));
    }

    private LivroResponse paraResponse(Livro livro) {
        return LivroResponse.de(livro, calcularQuantidadeDisponivel(livro));
    }

    private int calcularQuantidadeDisponivel(Livro livro) {
        long ativos = emprestimoRepository.countByLivroIdAndDataDevolucaoIsNull(livro.getId());

        return (int) (livro.getQuantidadeTotal() - ativos);
    }
}
