package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CrearUsuarioDTO;
import com.example.demo.enums.RolUsuario;
import com.example.demo.models.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    
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
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
    @GetMapping("/activos")
    public List<Usuario> activos() {
        return usuarioRepository.findByActivoTrue();
    }
    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable String id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    @PutMapping("/{id}")
    public Usuario editar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data
    ) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (data.containsKey("nombres"))
            usuario.setNombres((String) data.get("nombres"));

        if (data.containsKey("apellidos"))
            usuario.setApellidos((String) data.get("apellidos"));

        if (data.containsKey("telefono"))
            usuario.setTelefono((String) data.get("telefono"));

        if (data.containsKey("rol"))
            usuario.setRol(RolUsuario.valueOf(((String) data.get("rol")).toUpperCase()));

        if (data.containsKey("activo"))
            usuario.setActivo((Boolean) data.get("activo"));

        return usuarioRepository.save(usuario);
    }
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);

        usuarioRepository.save(usuario);
    }
}