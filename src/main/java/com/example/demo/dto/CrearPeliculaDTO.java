package com.example.demo.dto;

import java.util.Set;

import com.example.demo.enums.ClasificacionPelicula;
import com.example.demo.enums.Genero;

import lombok.Data;

@Data
public class CrearPeliculaDTO {

    private String titulo;

    private String sinopsis;

    private Integer duracionMinutos;

    private ClasificacionPelicula clasificacion;

    private Set<Genero> generos;

    private String imagenUrl;

    private Long fechaEstreno;
}