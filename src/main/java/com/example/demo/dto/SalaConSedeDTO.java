package com.example.demo.dto;

import com.example.demo.enums.TipoSala;
import com.example.demo.models.Sede;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaConSedeDTO {

    private String id;

    private String nombre;

    private Integer filas;

    private Integer columnas;

    private Integer capacidad;

    private TipoSala tipoSala;

    private Boolean activo;

    private Sede sede;
}
