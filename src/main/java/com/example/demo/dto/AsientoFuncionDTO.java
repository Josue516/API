package com.example.demo.dto;

import com.example.demo.enums.EstadoAsiento;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsientoFuncionDTO {
    private String id;
    private String asientoId;
    private String fila;
    private Integer numero;
    private EstadoAsiento estado;
    private Long reservadoHasta; // nullable
}