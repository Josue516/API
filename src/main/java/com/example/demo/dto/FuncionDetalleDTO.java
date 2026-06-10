package com.example.demo.dto;

import com.example.demo.models.Pelicula;
import com.example.demo.models.Sala;

import lombok.Data;

@Data
public class FuncionDetalleDTO {
    public String id;
    public Pelicula pelicula;
    public Sala sala;
    public String fechaHora;
    public double precio;
}