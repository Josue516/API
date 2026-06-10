package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CrearUsuarioDTO;
import com.example.demo.models.Usuario;
import com.example.demo.service.FirestoreService;
import com.example.demo.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private FirestoreService service;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public Map<String, Object> crear(@RequestBody CrearUsuarioDTO dto) throws Exception {

        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol());

        String uid = usuarioService.crearUsuario(
                dto.getEmail(),
                dto.getPassword(),
                usuario
        );

        return Map.of(
                "uid", uid,
                "message", "Usuario creado correctamente"
        );
    }
    @GetMapping
    public List<Usuario> listar() throws Exception {
        return service.getAll("usuarios", Usuario.class);
    }

    @GetMapping("/activos")
    public List<Usuario> activos() throws Exception {
        return service.getByField(
                "usuarios",
                "activo",
                true,
                Usuario.class);
    }

    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable String id) throws Exception {
        return service.getById("usuarios", id, Usuario.class);
    }

    @PutMapping("/{id}")
    public void editar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data) throws Exception {

        service.update("usuarios", id, data);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) throws Exception {

        service.softDelete(
                "usuarios",
                id,
                "activo",
                false);
    }
}