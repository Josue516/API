package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SedeConSalasDTO {

    private String id;

    private String nombre;

    private String direccion;

    private String ciudad;

    private String telefono;

    private Boolean activo;

    private List<SalaConSedeDTO> salas;
}