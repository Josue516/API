package com.example.demo.dto;

import lombok.Data;

@Data
public class CrearUsuarioDTO {

    private String email;
    private String password;

    private String nombres;
    private String apellidos;
    private String telefono;

    private String rol;
}