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

import com.example.demo.dto.FuncionConDetallesDTO;
import com.example.demo.dto.SalaConSedeDTO;
import com.example.demo.models.Funcion;
import com.example.demo.models.Pelicula;
import com.example.demo.models.Sala;
import com.example.demo.models.Sede;
import com.example.demo.service.FirestoreService;

@RestController
@RequestMapping("/api/funciones")
public class FuncionController {

    @Autowired
    private FirestoreService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@RequestBody Funcion f) throws Exception {
        String id = service.create("funciones", f);
        return ResponseEntity.ok(Map.of("id", id));
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
    public ResponseEntity<Void> actualizar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data) throws Exception {
        service.update("funciones", id, data);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) throws Exception {
        service.softDelete("funciones", id, "estado", "INACTIVA");
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/con-detalles")
    public List<FuncionConDetallesDTO> listarConDetalles() throws Exception {
        List<Funcion> funciones = service.getAll("funciones", Funcion.class);

        List<FuncionConDetallesDTO> resultado = new ArrayList<>();

        for (Funcion f : funciones) {
            Pelicula pelicula = service.getById("peliculas", f.getPeliculaId(), Pelicula.class);

            Sala sala = service.getById("salas", f.getSalaId(), Sala.class);
            Sede sede = service.getById("sedes", sala.getSedeId(), Sede.class);

            SalaConSedeDTO salaDto = new SalaConSedeDTO();
            salaDto.setId(sala.getId());
            salaDto.setNombre(sala.getNombre());
            salaDto.setCapacidad(sala.getCapacidad());
            salaDto.setTipoSala(sala.getTipoSala());
            salaDto.setActivo(sala.getActivo());
            salaDto.setSede(sede);

            FuncionConDetallesDTO dto = new FuncionConDetallesDTO();
            dto.setId(f.getId());
            dto.setFechaHora(f.getFechaHora());
            dto.setPrecio(f.getPrecio());
            dto.setEstado(f.getEstado());
            dto.setPelicula(pelicula);
            dto.setSala(salaDto);

            resultado.add(dto);
        }

        return resultado;
    }
}