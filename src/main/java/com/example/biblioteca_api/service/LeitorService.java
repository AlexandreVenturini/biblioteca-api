package com.example.biblioteca_api.service;

import com.example.biblioteca_api.dto.LeitorRequest;
import com.example.biblioteca_api.dto.LeitorResponse;
import com.example.biblioteca_api.exception.ConflitoException;
import com.example.biblioteca_api.exception.RecursoNaoEncontradoException;
import com.example.biblioteca_api.model.Leitor;
import com.example.biblioteca_api.model.TipoLeitor;
import com.example.biblioteca_api.repository.EmprestimoRepository;
import com.example.biblioteca_api.repository.LeitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LeitorService {

    private final LeitorRepository leitorRepository;
    private final EmprestimoRepository emprestimoRepository;

    public List<LeitorResponse> listar(TipoLeitor tipo) {
        return leitorRepository.findAll().stream()
                .filter(leitor -> Objects.isNull(tipo) || leitor.getTipo() == tipo)
                .map(LeitorResponse::de)
                .toList();
    }

    public LeitorResponse buscarPorId(Long id) {
        return LeitorResponse.de(buscarLeitor(id));
    }

    public LeitorResponse cadastrar(LeitorRequest request) {
        if (leitorRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflitoException("Já existe um leitor com o e-mail " + request.email());
        }
        Leitor leitor = new Leitor(null, request.nome(), request.email(), request.tipo());

        return LeitorResponse.de(leitorRepository.save(leitor));
    }

    public LeitorResponse atualizar(Long id, LeitorRequest request) {
        Leitor leitor = buscarLeitor(id);
        if (leitorRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ConflitoException("Já existe um leitor com o e-mail " + request.email());
        }
        leitor.setNome(request.nome());
        leitor.setEmail(request.email());
        leitor.setTipo(request.tipo());

        return LeitorResponse.de(leitorRepository.save(leitor));
    }

    public void remover(Long id) {
        Leitor leitor = buscarLeitor(id);
        if (emprestimoRepository.existsByLeitorIdAndDataDevolucaoIsNull(id)) {
            throw new ConflitoException("O leitor " + leitor.getNome()
                    + " possui empréstimo ativo e não pode ser removido");
        }
        leitorRepository.deleteById(id);
    }

    private Leitor buscarLeitor(Long id) {
        return leitorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", id));
    }
}
