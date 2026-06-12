package com.example.demo.dto;

import com.example.demo.enums.TipoSala;

import lombok.Data;

@Data
public class CrearSalaDTO {

    private String sedeId;

    private String nombre;

    private Integer filas;

    private Integer columnas;

    private TipoSala tipoSala;
}