package com.example.demo.models;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcion implements FirestoreEntity{

    private String id;
    private String peliculaId;
    private String salaId;
    private Timestamp fechaHora;
    private double precio;
    private String estado;
}
