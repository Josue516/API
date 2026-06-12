package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.enums.RolUsuario;
import com.example.demo.models.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public String crearUsuario(
            String email,
            String password,
            Usuario usuario) throws Exception {

        String uid = null;

        try {

            UserRecord.CreateRequest request =
                    new UserRecord.CreateRequest()
                            .setEmail(email)
                            .setPassword(password);

            UserRecord userRecord =
                    FirebaseAuth.getInstance()
                            .createUser(request);

            uid = userRecord.getUid();

            usuario.setId(uid);
            usuario.setEmail(email);
            if (usuario.getRol() == null) {
                usuario.setRol(RolUsuario.CLIENTE);
            }
            usuario.setActivo(true);
            usuario.setCreatedAt(System.currentTimeMillis());

            usuarioRepository.save(usuario);

            return uid;

        } catch (Exception e) {

            // Si Firebase creó el usuario pero MySQL falló,
            // eliminamos el usuario de Firebase para mantener consistencia.
            if (uid != null) {
                try {
                    FirebaseAuth.getInstance().deleteUser(uid);
                } catch (Exception deleteException) {
                    // Opcional: registrar en logs
                }
            }

            throw e;
        }
    }
}