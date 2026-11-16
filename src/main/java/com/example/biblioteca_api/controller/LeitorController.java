package com.example.biblioteca_api.controller;

import com.example.biblioteca_api.dto.LeitorRequest;
import com.example.biblioteca_api.dto.LeitorResponse;
import com.example.biblioteca_api.model.TipoLeitor;
import com.example.biblioteca_api.service.LeitorService;
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
@RequestMapping("/leitores")
@RequiredArgsConstructor
public class LeitorController {

    private final LeitorService leitorService;

    @PostMapping
    public ResponseEntity<LeitorResponse> cadastrar(@RequestBody LeitorRequest request) {
        LeitorResponse criado = leitorService.cadastrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<LeitorResponse> listar(@RequestParam(required = false) TipoLeitor tipo) {
        return leitorService.listar(tipo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeitorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.of(leitorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeitorResponse> atualizar(@PathVariable Long id, @RequestBody LeitorRequest request) {
        return ResponseEntity.of(leitorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (leitorService.remover(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
