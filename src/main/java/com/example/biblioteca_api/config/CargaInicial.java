package com.example.biblioteca_api.config;

import com.example.biblioteca_api.model.Emprestimo;
import com.example.biblioteca_api.model.Leitor;
import com.example.biblioteca_api.model.Livro;
import com.example.biblioteca_api.model.TipoLeitor;
import com.example.biblioteca_api.repository.EmprestimoRepository;
import com.example.biblioteca_api.repository.LeitorRepository;
import com.example.biblioteca_api.repository.LivroRepository;
import com.example.biblioteca_api.service.politica.PoliticaEmprestimoProfessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CargaInicial implements CommandLineRunner {

    private final LivroRepository livroRepository;
    private final LeitorRepository leitorRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final PoliticaEmprestimoProfessor politicaProfessor;
    private final Clock clock;

    @Override
    public void run(String... args) {
        livroRepository.save(new Livro(null, "Código Limpo", "Robert C. Martin", "9788576082675", 2009, 3));
        Livro refatoracao = livroRepository.save(
                new Livro(null, "Refatoração", "Martin Fowler", "9788575227244", 2020, 2));
        livroRepository.save(new Livro(null, "Arquitetura Limpa", "Robert C. Martin", "9788550804606", 2019, 1));

        Leitor professor = leitorRepository.save(
                new Leitor(null, "Carlos Pereira", "carlos.pereira@ifes.edu.br", TipoLeitor.PROFESSOR));
        leitorRepository.save(new Leitor(null, "Ana Souza", "ana.souza@ifes.edu.br", TipoLeitor.ALUNO));

        LocalDate dataEmprestimo = LocalDate.now(clock).minusDays(20);
        emprestimoRepository.save(new Emprestimo(null, refatoracao.getId(), professor.getId(), dataEmprestimo,
                dataEmprestimo.plusDays(politicaProfessor.prazoEmDias()), null,
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)));

        log.info("Carga inicial concluída: 3 livros, 2 leitores e 1 empréstimo atrasado");
    }
}
