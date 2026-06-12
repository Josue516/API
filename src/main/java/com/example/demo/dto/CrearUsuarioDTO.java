package com.example.demo.dto;

import com.example.demo.enums.RolUsuario;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class CrearUsuarioDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    private String telefono;

    private RolUsuario rol;
}