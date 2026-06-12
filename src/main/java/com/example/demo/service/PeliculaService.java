package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.models.Pelicula;
import com.example.demo.repositories.PeliculaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public String crearPelicula(Pelicula pelicula) {

        pelicula.setId(UUID.randomUUID().toString());

        pelicula.setActivo(true);

        pelicula.setCreatedAt(System.currentTimeMillis());

        peliculaRepository.save(pelicula);

        return pelicula.getId();
    }

    public List<Pelicula> obtenerPeliculas() {
        return peliculaRepository.findAll();
    }

    public Pelicula obtenerPeliculaPorId(String id) {

        return peliculaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Película no encontrada"));
    }

    public void desactivarPelicula(String id) {

        Pelicula pelicula = obtenerPeliculaPorId(id);

        pelicula.setActivo(false);

        peliculaRepository.save(pelicula);
    }
    public List<Pelicula> obtenerPeliculasActivas() {
        return peliculaRepository.findByActivoTrue();
    }
}
