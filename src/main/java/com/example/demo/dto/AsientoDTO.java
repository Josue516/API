package com.example.demo.dto;

import com.example.demo.enums.EstadoAsiento;

import lombok.Data;

@Data
public class AsientoDTO {

    private String asientoId;

    private String fila;

    private Integer numero;

    private EstadoAsiento estado;
}