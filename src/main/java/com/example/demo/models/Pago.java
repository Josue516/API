package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    private String paypalOrderId;
    private String paypalCaptureId;
    private double monto;
    private String moneda;
    private String estado;
    private Long fechaPago;
}
