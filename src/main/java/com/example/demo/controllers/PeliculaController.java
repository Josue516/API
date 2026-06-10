package com.example.demo.controllers;

import com.example.demo.models.Pelicula;
import com.example.demo.service.FirestoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    @Autowired
    private FirestoreService service;

    @PostMapping
    public String crear(@RequestBody Pelicula p) throws Exception {
        return service.create("peliculas", p);
    }

    @GetMapping
    public List<Pelicula> listar() throws Exception {
        return service.getAll("peliculas", Pelicula.class);
    }
    @GetMapping("/{id}")
    public Pelicula obtenerPorId(@PathVariable String id) throws Exception {
        return service.getById("peliculas", id, Pelicula.class);
    }

    @GetMapping("/cartelera")
    public List<Pelicula> cartelera() throws Exception {
        return service.getByField("peliculas", "activo", true, Pelicula.class);
    }

    @PutMapping("/{id}")
    public void editar(@PathVariable String id,
                       @RequestBody Map<String, Object> data) throws Exception {
        service.update("peliculas", id, data);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) throws Exception {
        service.softDelete(
                "peliculas",
                id,
                "activo",
                false);
    }
}