package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.models.Sede;
import com.example.demo.repositories.SedeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SedeService {

    private final SedeRepository sedeRepository;
    public String crearSede(Sede sede) {

        sede.setId(UUID.randomUUID().toString());
        sede.setActivo(true);

        sedeRepository.save(sede);

        return sede.getId();
    }
    
    public List<Sede> obtenerSedes() {
        return sedeRepository.findAll();
    }
    
    public Sede obtenerPorId(String id) {

        return sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }
    
    public List<Sede> obtenerActivas() {
        return sedeRepository.findByActivoTrue();
    }
    
    public Sede actualizar(String id, Sede nueva) {

        Sede sede = obtenerPorId(id);

        sede.setNombre(nueva.getNombre());
        sede.setDireccion(nueva.getDireccion());
        sede.setCiudad(nueva.getCiudad());
        sede.setTelefono(nueva.getTelefono());

        return sedeRepository.save(sede);
    }
    public void desactivar(String id) {

        Sede sede = obtenerPorId(id);

        sede.setActivo(false);

        sedeRepository.save(sede);
    }
    
}