package com.example.biblioteca_api.dto;

import com.example.biblioteca_api.model.TipoLeitor;

public record LeitorRequest(
        String nome,
        String email,
        TipoLeitor tipo
) {
}
