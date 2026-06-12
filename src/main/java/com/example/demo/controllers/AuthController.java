package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.enums.RolUsuario;
import com.example.demo.models.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);

            String uid = decoded.getUid();

            if (!usuarioRepository.existsById(uid)) {
                Usuario usuario = Usuario.builder()
                        .id(uid)
                        .email(decoded.getEmail())
                        .nombres("")
                        .apellidos("")
                        .rol(RolUsuario.CLIENTE)
                        .activo(true)
                        .createdAt(System.currentTimeMillis())
                        .build();
                usuarioRepository.save(usuario);
            }

            return ResponseEntity.ok(usuarioRepository.findById(uid));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}