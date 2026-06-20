package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CrearSalaDTO;
import com.example.demo.models.Sala;
import com.example.demo.repositories.SalaRepository;
import com.example.demo.service.SalaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;
    private final SalaRepository salaRepository;
    
    @PostMapping
    public Map<String, String> crear(@RequestBody CrearSalaDTO dto) {
        String id = salaService.crearSala(dto);
        return Map.of("id", id);
    }
    
    @GetMapping
    public List<Sala> listar() {
        return salaRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Sala obtenerPorId(@PathVariable String id) {

        return salaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
    }
    
    @GetMapping("/activas")
    public List<Sala> activas() {
        return salaRepository.findByActivoTrue();
    }
    @PutMapping("/{id}")
    public Sala editar(
            @PathVariable String id,
            @RequestBody Sala nueva
    ) {

        return salaService.actualizar(id, nueva);
    }
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        salaService.desactivar(id);
    }
    @GetMapping("/sede/{sedeId}")
    public List<Sala> porSede(@PathVariable String sedeId) {
        return salaRepository.findBySede_Id(sedeId);
    }
    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> toggleActivo(@PathVariable String id) {
        Sala sala = salaService.toggleActivo(id);
        return ResponseEntity.ok(sala);
    }
}