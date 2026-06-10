package com.example.demo.dto;

import com.example.demo.models.Funcion;
import com.example.demo.models.Pago;
import com.example.demo.models.Pelicula;
import com.example.demo.models.Sala;
import com.example.demo.models.Usuario;

import lombok.Data;

@Data
public class ReservaDetalleDTO {

    private String id;

    private Usuario usuario;
    private Funcion funcion;
    private Pelicula pelicula;
    private Sala sala;

    private int cantidadBoletos;
    private double total;
    private String estado;
    private String createdAt;

    private Pago pago;

    public ReservaDetalleDTO() {}
}
