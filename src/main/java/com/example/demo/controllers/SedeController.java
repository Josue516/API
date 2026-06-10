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

import com.example.demo.models.Sede;
import com.example.demo.service.FirestoreService;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    @Autowired
    private FirestoreService service;

    @PostMapping
    public String crear(@RequestBody Sede s) throws Exception {
        return service.create("sedes", s);
    }

    @GetMapping
    public List<Sede> listar() throws Exception {
        return service.getAll("sedes", Sede.class);
    }

    @GetMapping("/{id}")
    public Sede obtenerPorId(@PathVariable String id) throws Exception {
        return service.getById("sedes", id, Sede.class);
    }

    @GetMapping("/activas")
    public List<Sede> activas() throws Exception {
        return service.getByField(
                "sedes",
                "activo",
                true,
                Sede.class);
    }

    @PutMapping("/{id}")
    public void editar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data) throws Exception {

        service.update("sedes", id, data);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) throws Exception {

        service.softDelete(
                "sedes",
                id,
                "activo",
                false);
    }
}