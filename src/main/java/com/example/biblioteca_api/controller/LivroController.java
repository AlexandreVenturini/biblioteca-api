package com.example.biblioteca_api.controller;

import com.example.biblioteca_api.dto.LivroRequest;
import com.example.biblioteca_api.dto.LivroResponse;
import com.example.biblioteca_api.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    public ResponseEntity<LivroResponse> cadastrar(@RequestBody LivroRequest request) {
        LivroResponse criado = livroService.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<LivroResponse> listar(@RequestParam(required = false) String autor,
                                      @RequestParam(required = false) Boolean disponivel) {
        return livroService.listar(autor, disponivel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.of(livroService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(@PathVariable Long id, @RequestBody LivroRequest request) {
        return ResponseEntity.of(livroService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (livroService.remover(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
