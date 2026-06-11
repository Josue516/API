package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva implements FirestoreEntity{

    private String id;
    private String usuarioId;
    private String funcionId;
    private int cantidadBoletos;
    private double total;
    private String estado;
    private Long createdAt;
    private Pago pago;
}
