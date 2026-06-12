package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class FuncionAsientosDTO {

    private String funcionId;

    private String peliculaTitulo;

    private Long fechaHora;

    private List<AsientoDTO> asientos;
}