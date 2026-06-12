package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.enums.EstadoFuncion;

import lombok.Data;

@Data
public class CrearFuncionDTO {
    private String peliculaId;
    private String salaId;
    private Long fechaHora;
    private BigDecimal precio;
    private EstadoFuncion estado;
}