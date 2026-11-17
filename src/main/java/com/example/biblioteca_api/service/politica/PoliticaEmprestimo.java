package com.example.biblioteca_api.service.politica;

import com.example.biblioteca_api.model.TipoLeitor;

import java.math.BigDecimal;

public interface PoliticaEmprestimo {

    TipoLeitor tipo();

    int prazoEmDias();

    int limiteEmprestimos();

    BigDecimal calcularMulta(long diasAtraso);
}
