package com.example.demo.controllers;

import com.example.demo.models.Pelicula;
import com.example.demo.repositories.PeliculaRepository;
import com.example.demo.service.PeliculaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
public class PeliculaController {

    private final PeliculaService peliculaService;
    private final PeliculaRepository peliculaRepository;
    
    @PostMapping
    public String crear(@RequestBody Pelicula pelicula) {

        return peliculaService.crearPelicula(pelicula);
    }
    
    @GetMapping
    public List<Pelicula> listar() {
        return peliculaRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Pelicula obtenerPorId(@PathVariable String id) {

        return peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
    }
    
    @GetMapping("/cartelera")
    public List<Pelicula> cartelera() {
        return peliculaRepository.findByActivoTrue();
    }
    
    @PutMapping("/{id}")
    public Pelicula editar(
            @PathVariable String id,
            @RequestBody Pelicula nueva
    ) {

        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        pelicula.setTitulo(nueva.getTitulo());
        pelicula.setSinopsis(nueva.getSinopsis());
        pelicula.setDuracionMinutos(nueva.getDuracionMinutos());
        pelicula.setClasificacion(nueva.getClasificacion());
        pelicula.setGeneros(nueva.getGeneros());
        pelicula.setImagenUrl(nueva.getImagenUrl());
        pelicula.setFechaEstreno(nueva.getFechaEstreno());

        return peliculaRepository.save(pelicula);
    }
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {

        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        pelicula.setActivo(false);

        peliculaRepository.save(pelicula);
    }
    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> toggleActivo(@PathVariable String id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
        pelicula.setActivo(!pelicula.getActivo());
        peliculaRepository.save(pelicula);
        return ResponseEntity.ok(pelicula);
    }
}