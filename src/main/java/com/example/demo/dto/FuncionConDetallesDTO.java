package com.example.demo.dto;

import com.example.demo.models.Pelicula;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionConDetallesDTO {
    private String id;
    private Long fechaHora;
    private double precio;
    private String estado;
    private Pelicula pelicula;
    private SalaConSedeDTO sala;
}