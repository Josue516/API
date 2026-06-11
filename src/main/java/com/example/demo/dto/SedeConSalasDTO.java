package com.example.demo.dto;

import java.util.List;

import com.example.demo.models.Sala;

import lombok.Data;
@Data
public class SedeConSalasDTO {
    private String id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String telefono;
    private boolean activo;
    private List<Sala> salas;
}
