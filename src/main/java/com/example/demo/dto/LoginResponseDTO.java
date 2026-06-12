package com.example.demo.dto;

import com.example.demo.enums.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String uid;

    private String email;

    private RolUsuario rol;
}