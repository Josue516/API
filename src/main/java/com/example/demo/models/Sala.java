package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sala implements FirestoreEntity{

    private String id;
    private String sedeId;
    private String nombre;
    private int capacidad;
    private String tipoSala;
    private Boolean activo;
}
