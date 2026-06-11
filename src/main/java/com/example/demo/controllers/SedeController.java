package com.example.demo.controllers;

import java.util.ArrayList;
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

import com.example.demo.dto.SedeConSalasDTO;
import com.example.demo.models.Sala;
import com.example.demo.models.Sede;
import com.example.demo.service.FirestoreService;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    @Autowired
    private FirestoreService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@RequestBody Sede s) throws Exception {
        String id = service.create("sedes", s);
        return ResponseEntity.ok(Map.of("id", id));
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
    public ResponseEntity<Void> editar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data) throws Exception {
        service.update("sedes", id, data);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) throws Exception {
        service.softDelete("sedes", id, "activo", false);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/con-salas")
    public List<SedeConSalasDTO> listarConSalas() throws Exception {
        List<Sede> sedes = service.getAll("sedes", Sede.class);

        List<SedeConSalasDTO> resultado = new ArrayList<>();

        for (Sede sede : sedes) {
            List<Sala> salas = service.getByField("salas", "sedeId", sede.getId(), Sala.class);

            SedeConSalasDTO dto = new SedeConSalasDTO();
            dto.setId(sede.getId());
            dto.setNombre(sede.getNombre());
            dto.setDireccion(sede.getDireccion());
            dto.setCiudad(sede.getCiudad());
            dto.setTelefono(sede.getTelefono());
            dto.setActivo(sede.getActivo());
            dto.setSalas(salas);

            resultado.add(dto);
        }

        return resultado;
    }
}