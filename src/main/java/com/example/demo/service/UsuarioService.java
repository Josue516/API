package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.enums.RolUsuario;
import com.example.demo.models.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public String crearUsuario(
            String email,
            String password,
            Usuario usuario) {

        String uid = null;

        try {

            UserRecord.CreateRequest request =
                    new UserRecord.CreateRequest()
                            .setEmail(email)
                            .setPassword(password);

            UserRecord userRecord =
                    FirebaseAuth.getInstance().createUser(request);

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

        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Error al crear usuario en Firebase: " + e.getMessage());

        } catch (Exception e) {
            // El usuario sí se creó en Firebase pero falló en MySQL
            if (uid != null) {
                try {
                    FirebaseAuth.getInstance().deleteUser(uid);
                } catch (Exception deleteException) {
                    log.error("No se pudo revertir el usuario {} en Firebase tras fallo en MySQL", uid, deleteException);
                }
            }
            throw new RuntimeException("Error al guardar usuario en la base de datos: " + e.getMessage());
        }
    }
}