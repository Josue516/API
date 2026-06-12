package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.enums.EstadoFuncion;
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

    private BigDecimal precio;

    private EstadoFuncion estado;

    private Pelicula pelicula;

    private SalaConSedeDTO sala;
}