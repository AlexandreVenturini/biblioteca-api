package com.example.biblioteca_api.dto;

import com.example.biblioteca_api.model.Leitor;
import com.example.biblioteca_api.model.TipoLeitor;

public record LeitorResponse(
        Long id,
        String nome,
        String email,
        TipoLeitor tipo
) {

    public static LeitorResponse de(Leitor leitor) {
        return new LeitorResponse(leitor.getId(), leitor.getNome(), leitor.getEmail(), leitor.getTipo());
    }
}
