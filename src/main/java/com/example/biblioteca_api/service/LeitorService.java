package com.example.biblioteca_api.service;

import com.example.biblioteca_api.dto.LeitorRequest;
import com.example.biblioteca_api.dto.LeitorResponse;
import com.example.biblioteca_api.model.Leitor;
import com.example.biblioteca_api.model.TipoLeitor;
import com.example.biblioteca_api.repository.LeitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeitorService {

    private final LeitorRepository leitorRepository;

    public List<LeitorResponse> listar(TipoLeitor tipo) {
        return leitorRepository.findAll().stream()
                .filter(leitor -> Objects.isNull(tipo) || leitor.getTipo() == tipo)
                .map(LeitorResponse::de)
                .toList();
    }

    public Optional<LeitorResponse> buscarPorId(Long id) {
        return leitorRepository.findById(id).map(LeitorResponse::de);
    }

    public LeitorResponse cadastrar(LeitorRequest request) {
        if (leitorRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalStateException("Já existe um leitor com o e-mail " + request.email());
        }
        Leitor leitor = new Leitor(null, request.nome(), request.email(), request.tipo());

        return LeitorResponse.de(leitorRepository.save(leitor));
    }

    public Optional<LeitorResponse> atualizar(Long id, LeitorRequest request) {
        return leitorRepository.findById(id)
                .map(leitor -> {
                    if (leitorRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
                        throw new IllegalStateException("Já existe um leitor com o e-mail " + request.email());
                    }
                    leitor.setNome(request.nome());
                    leitor.setEmail(request.email());
                    leitor.setTipo(request.tipo());

                    return LeitorResponse.de(leitorRepository.save(leitor));
                });
    }

    public boolean remover(Long id) {
        if (!leitorRepository.existsById(id)) {
            return false;
        }
        leitorRepository.deleteById(id);

        return true;
    }
}
