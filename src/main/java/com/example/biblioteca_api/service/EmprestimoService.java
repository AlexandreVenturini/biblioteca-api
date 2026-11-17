package com.example.biblioteca_api.service;

import com.example.biblioteca_api.dto.EmprestimoRequest;
import com.example.biblioteca_api.dto.EmprestimoResponse;
import com.example.biblioteca_api.exception.ConflitoException;
import com.example.biblioteca_api.exception.RecursoNaoEncontradoException;
import com.example.biblioteca_api.model.Emprestimo;
import com.example.biblioteca_api.model.Leitor;
import com.example.biblioteca_api.model.Livro;
import com.example.biblioteca_api.model.TipoLeitor;
import com.example.biblioteca_api.repository.EmprestimoRepository;
import com.example.biblioteca_api.repository.LeitorRepository;
import com.example.biblioteca_api.repository.LivroRepository;
import com.example.biblioteca_api.service.politica.PoliticaEmprestimo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final LeitorRepository leitorRepository;
    private final Map<TipoLeitor, PoliticaEmprestimo> politicas;
    private final Clock clock;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             LivroRepository livroRepository,
                             LeitorRepository leitorRepository,
                             List<PoliticaEmprestimo> politicas,
                             Clock clock) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.leitorRepository = leitorRepository;
        this.politicas = politicas.stream()
                .collect(Collectors.toMap(PoliticaEmprestimo::tipo, Function.identity()));
        this.clock = clock;
    }

    public EmprestimoResponse realizar(EmprestimoRequest request) {
        Livro livro = livroRepository.findById(request.livroId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro", request.livroId()));
        Leitor leitor = leitorRepository.findById(request.leitorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", request.leitorId()));
        PoliticaEmprestimo politica = politicas.get(leitor.getTipo());
        LocalDate hoje = LocalDate.now(clock);

        if (emprestimoRepository.existsByLeitorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(leitor.getId(), hoje)) {
            throw new ConflitoException("O leitor " + leitor.getNome()
                    + " possui empréstimo atrasado e não pode fazer novo empréstimo");
        }
        if (emprestimoRepository.countByLeitorIdAndDataDevolucaoIsNull(leitor.getId()) >= politica.limiteEmprestimos()) {
            throw new ConflitoException("O leitor " + leitor.getNome() + " atingiu o limite de "
                    + politica.limiteEmprestimos() + " empréstimos ativos");
        }
        if (emprestimoRepository.countByLivroIdAndDataDevolucaoIsNull(livro.getId()) >= livro.getQuantidadeTotal()) {
            throw new ConflitoException("O livro " + livro.getTitulo() + " não possui exemplar disponível");
        }

        Emprestimo emprestimo = new Emprestimo(null, livro.getId(), leitor.getId(), hoje,
                hoje.plusDays(politica.prazoEmDias()), null, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        return paraResponse(emprestimoRepository.save(emprestimo));
    }

    public List<EmprestimoResponse> listar(Long leitorId, Boolean ativos) {
        return emprestimoRepository.findAll().stream()
                .filter(emprestimo -> Objects.isNull(leitorId) || emprestimo.getLeitorId().equals(leitorId))
                .filter(emprestimo -> !Boolean.TRUE.equals(ativos) || emprestimo.isAtivo())
                .map(this::paraResponse)
                .toList();
    }

    public EmprestimoResponse buscarPorId(Long id) {
        return paraResponse(buscarEmprestimo(id));
    }

    public EmprestimoResponse devolver(Long id) {
        Emprestimo emprestimo = buscarEmprestimo(id);
        if (!emprestimo.isAtivo()) {
            throw new ConflitoException("O empréstimo " + id + " já foi devolvido");
        }
        Leitor leitor = leitorRepository.findById(emprestimo.getLeitorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitor", emprestimo.getLeitorId()));
        PoliticaEmprestimo politica = politicas.get(leitor.getTipo());
        LocalDate hoje = LocalDate.now(clock);
        long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataPrevistaDevolucao(), hoje);

        emprestimo.setDataDevolucao(hoje);
        emprestimo.setMulta(politica.calcularMulta(diasAtraso));

        return paraResponse(emprestimoRepository.save(emprestimo));
    }

    public List<EmprestimoResponse> listarAtrasados() {
        return emprestimoRepository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now(clock))
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    private Emprestimo buscarEmprestimo(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo", id));
    }

    private EmprestimoResponse paraResponse(Emprestimo emprestimo) {
        String titulo = livroRepository.findById(emprestimo.getLivroId())
                .map(Livro::getTitulo)
                .orElse(null);
        String leitor = leitorRepository.findById(emprestimo.getLeitorId())
                .map(Leitor::getNome)
                .orElse(null);

        return EmprestimoResponse.de(emprestimo, titulo, leitor);
    }
}
