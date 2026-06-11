package com.example.demo.controllers;

import com.example.demo.models.Pelicula;
import com.example.demo.service.FirestoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;

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
    public ResponseEntity<Void> editar(@PathVariable String id,
                                       @RequestBody Pelicula p) throws Exception {
        service.update("peliculas", id, p);
        return ResponseEntity.noContent().build(); // 204
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) throws Exception {
        service.softDelete("peliculas", id, "activo", false);
        return ResponseEntity.noContent().build();
    }
}