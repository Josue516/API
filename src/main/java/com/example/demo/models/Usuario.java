package com.example.demo.models;

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
    private Long createdAt;
    private Boolean activo;
}
