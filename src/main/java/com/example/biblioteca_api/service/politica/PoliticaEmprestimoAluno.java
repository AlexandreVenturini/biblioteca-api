package com.example.biblioteca_api.service.politica;

import com.example.biblioteca_api.model.TipoLeitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PoliticaEmprestimoAluno implements PoliticaEmprestimo {

    private final int prazoEmDias;
    private final int limiteEmprestimos;
    private final BigDecimal multaDiaria;

    public PoliticaEmprestimoAluno(@Value("${biblioteca.politicas.aluno.prazo-dias}") int prazoEmDias,
                                   @Value("${biblioteca.politicas.aluno.limite-emprestimos}") int limiteEmprestimos,
                                   @Value("${biblioteca.politicas.aluno.multa-diaria}") BigDecimal multaDiaria) {
        this.prazoEmDias = prazoEmDias;
        this.limiteEmprestimos = limiteEmprestimos;
        this.multaDiaria = multaDiaria;
    }

    @Override
    public TipoLeitor tipo() {
        return TipoLeitor.ALUNO;
    }

    @Override
    public int prazoEmDias() {
        return prazoEmDias;
    }

    @Override
    public int limiteEmprestimos() {
        return limiteEmprestimos;
    }

    @Override
    public BigDecimal calcularMulta(long diasAtraso) {
        return multaDiaria.multiply(BigDecimal.valueOf(Math.max(diasAtraso, 0)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
