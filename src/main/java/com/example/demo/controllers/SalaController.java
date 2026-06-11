package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Sala;
import com.example.demo.service.FirestoreService;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    @Autowired
    private FirestoreService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@RequestBody Sala s) throws Exception {
        if (s.getActivo() == null) s.setActivo(true);
        String id = service.create("salas", s);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping
    public List<Sala> listar() throws Exception {
        return service.getAll("salas", Sala.class);
    }

    @GetMapping("/{id}")
    public Sala obtenerPorId(@PathVariable String id) throws Exception {
        return service.getById("salas", id, Sala.class);
    }

    @GetMapping("/activas")
    public List<Sala> activas() throws Exception {
        return service.getByField(
                "salas",
                "activo",
                true,
                Sala.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data) throws Exception {
        service.update("salas", id, data);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) throws Exception {
        service.softDelete("salas", id, "activo", false);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/sede/{sedeId}")
    public List<Sala> porSede(@PathVariable String sedeId) throws Exception {
        return service.getByField(
                "salas",
                "sedeId",
                sedeId,
                Sala.class);
    }
}