package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.models.Usuario;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

@Service
public class UsuarioService {

    @Autowired
    private Firestore firestore;

    public String crearUsuario(
            String email,
            String password,
            Usuario usuario) throws Exception {

        UserRecord.CreateRequest request =
                new UserRecord.CreateRequest()
                        .setEmail(email)
                        .setPassword(password);

        UserRecord userRecord =
                FirebaseAuth.getInstance()
                        .createUser(request);

        String uid = userRecord.getUid();

        usuario.setId(uid);
        usuario.setEmail(email);
        usuario.setActivo(true);
        usuario.setCreatedAt(Timestamp.now());

        firestore.collection("usuarios")
                .document(uid)
                .set(usuario)
                .get();

        return uid;
    }
}