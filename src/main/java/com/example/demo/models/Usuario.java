package com.example.demo.models;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements FirestoreEntity{

    private String id;
    private String email;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String rol;
    private Timestamp createdAt;
    private Boolean activo;
}
