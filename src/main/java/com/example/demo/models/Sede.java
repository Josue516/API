package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sede implements FirestoreEntity{

    private String id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String telefono;
    private boolean activo;
}