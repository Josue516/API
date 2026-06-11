package com.example.demo.dto;

import com.example.demo.models.Pago;
import com.example.demo.models.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDetalleDTO {
    private String id;
    private Usuario usuario;
    private FuncionConDetallesDTO funcion;
    private int cantidadBoletos;
    private double total;
    private String estado;
    private Long createdAt;
    private Pago pago;
}
