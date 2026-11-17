package com.example.biblioteca_api.controller;

import com.example.biblioteca_api.dto.EmprestimoRequest;
import com.example.biblioteca_api.dto.EmprestimoResponse;
import com.example.biblioteca_api.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponse> realizar(@Valid @RequestBody EmprestimoRequest request) {
        EmprestimoResponse criado = emprestimoService.realizar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<EmprestimoResponse> listar(@RequestParam(required = false) Long leitorId,
                                           @RequestParam(required = false) Boolean ativos) {
        return emprestimoService.listar(leitorId, ativos);
    }

    @GetMapping("/{id}")
    public EmprestimoResponse buscar(@PathVariable Long id) {
        return emprestimoService.buscarPorId(id);
    }
}
