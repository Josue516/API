package com.example.demo.controllers;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AsientoFuncionDTO;
import com.example.demo.dto.CrearFuncionDTO;
import com.example.demo.dto.FuncionConDetallesDTO;
import com.example.demo.models.Funcion;
import com.example.demo.service.FuncionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class FuncionController {

    private final FuncionService funcionService;
    
    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@RequestBody CrearFuncionDTO dto) {
        String id = funcionService.crearFuncion(dto);
        return ResponseEntity.ok(Map.of("id", id));
    }
    
    @GetMapping
    public List<Funcion> listar() {
        return funcionService.obtenerTodas();
    }
    @GetMapping("/{id}")
    public Funcion obtener(@PathVariable String id) {
        return funcionService.obtenerPorId(id);
    }
    @GetMapping("/activas")
    public List<Funcion> activas() {
        return funcionService.obtenerActivas();
    }
    @PutMapping("/{id}")
    public Funcion actualizar(
            @PathVariable String id,
            @RequestBody CrearFuncionDTO dto
    ) {
        return funcionService.actualizar(id, dto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {

        funcionService.cancelarFuncion(id);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/con-detalles")
    public List<FuncionConDetallesDTO> conDetalles() {
        return funcionService.obtenerConDetalles();
    }
    @GetMapping("/{id}/asientos")
    public List<AsientoFuncionDTO> asientos(@PathVariable String id) {
        return funcionService.obtenerAsientosPorFuncion(id);
    }
}