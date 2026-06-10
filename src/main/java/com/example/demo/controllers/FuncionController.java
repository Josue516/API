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

import com.example.demo.models.Funcion;
import com.example.demo.service.FirestoreService;

@RestController
@RequestMapping("/api/funciones")
public class FuncionController {

    @Autowired
    private FirestoreService service;

    @PostMapping
    public String crear(@RequestBody Funcion f) throws Exception {
        return service.create("funciones", f);
    }

    @GetMapping
    public List<Funcion> listar() throws Exception {
        return service.getAll("funciones", Funcion.class);
    }

    @GetMapping("/activas")
    public List<Funcion> activas() throws Exception {
        return service.getByField("funciones", "estado", "ACTIVA", Funcion.class);
    }
    @GetMapping("/{id}")
    public Funcion obtenerPorId(@PathVariable String id) throws Exception {
        return service.getById("funciones", id, Funcion.class);
    }

    @PutMapping("/{id}")
    public String actualizar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data) throws Exception {

        service.update("funciones", id, data);

        return "Función actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable String id) throws Exception {

        service.softDelete(
                "funciones",
                id,
                "estado",
                "INACTIVA");

        return "Función desactivada correctamente";
    }
}