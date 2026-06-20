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

import com.example.demo.dto.SalaConSedeDTO;
import com.example.demo.dto.SedeConSalasDTO;
import com.example.demo.models.Sala;
import com.example.demo.models.Sede;
import com.example.demo.repositories.SalaRepository;
import com.example.demo.repositories.SedeRepository;
import com.example.demo.service.SedeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeRepository sedeRepository;
    private final SalaRepository salaRepository;
    private final SedeService sedeService;
    
    @PostMapping
    public Map<String, String> crear(@RequestBody Sede sede) {

        String id = sedeService.crearSede(sede);

        return Map.of("id", id);
    }
    
    @GetMapping
    public List<Sede> listar() {
        return sedeRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Sede obtenerPorId(@PathVariable String id) {

        return sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }
    
    @GetMapping("/activas")
    public List<Sede> activas() {
        return sedeRepository.findByActivoTrue();
    }
    @PutMapping("/{id}")
    public Sede editar(
            @PathVariable String id,
            @RequestBody Sede nueva
    ) {

        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        sede.setNombre(nueva.getNombre());
        sede.setDireccion(nueva.getDireccion());
        sede.setCiudad(nueva.getCiudad());
        sede.setTelefono(nueva.getTelefono());

        return sedeRepository.save(sede);
    }
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        sedeService.desactivar(id);
    }
    @PatchMapping("/{id}/activo")
    public ResponseEntity<?> toggleActivo(@PathVariable String id) {
        Sede sede = sedeService.toggleActivo(id);
        return ResponseEntity.ok(sede);
    }
    @GetMapping("/con-salas")
    public List<SedeConSalasDTO> listarConSalas() {

        List<Sede> sedes = sedeRepository.findAll();

        return sedes.stream().map(sede -> {

            List<Sala> salas = salaRepository.findBySede_Id(sede.getId());

            SedeConSalasDTO dto = new SedeConSalasDTO();
            dto.setId(sede.getId());
            dto.setNombre(sede.getNombre());
            dto.setDireccion(sede.getDireccion());
            dto.setCiudad(sede.getCiudad());
            dto.setTelefono(sede.getTelefono());
            dto.setActivo(sede.getActivo());
            List<SalaConSedeDTO> salasDTO = salas.stream()
                    .map(sala -> {
                        SalaConSedeDTO s = new SalaConSedeDTO();
                        s.setId(sala.getId());
                        s.setNombre(sala.getNombre());
                        s.setCapacidad(sala.getCapacidad());
                        s.setTipoSala(sala.getTipoSala());
                        s.setActivo(sala.getActivo());
                        s.setSede(dto.getId() != null ? null : null);
                        return s;
                    })
                    .toList();

            dto.setSalas(salasDTO);

            return dto;

        }).toList();
    }
}