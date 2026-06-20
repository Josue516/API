package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.dto.CrearSalaDTO;
import com.example.demo.enums.EstadoFuncion;
import com.example.demo.models.Asiento;
import com.example.demo.models.Sala;
import com.example.demo.models.Sede;
import com.example.demo.repositories.AsientoRepository;
import com.example.demo.repositories.FuncionRepository;
import com.example.demo.repositories.SalaRepository;
import com.example.demo.repositories.SedeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;
    private final SedeRepository sedeRepository;
    private final FuncionRepository funcionRepository;
    
    
    public String crearSala(CrearSalaDTO dto) {
        Sede sede = sedeRepository.findById(dto.getSedeId())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        Sala sala = Sala.builder()
                .id(UUID.randomUUID().toString())
                .sede(sede)
                .nombre(dto.getNombre())
                .filas(dto.getFilas())
                .columnas(dto.getColumnas())
                .tipoSala(dto.getTipoSala())
                .activo(true)
                .build();

        salaRepository.save(sala);
        generarAsientos(sala);
        return sala.getId();
    }
    private void generarAsientos(Sala sala) {

        List<Asiento> asientos = new ArrayList<>();

        for (int fila = 1; fila <= sala.getFilas(); fila++) {
            for (int numero = 1; numero <= sala.getColumnas(); numero++) {

                Asiento asiento = Asiento.builder()
                        .id(UUID.randomUUID().toString())
                        .sala(sala)
                        .fila(String.valueOf((char) ('A' + fila - 1)))
                        .numero(numero)
                        .activo(true)
                        .build();

                asientos.add(asiento);
            }
        }

        asientoRepository.saveAll(asientos);
    }
    
    public List<Sala> obtenerSalas() {
        return salaRepository.findAll();
    }
    
    public Sala obtenerPorId(String id) {

        return salaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
    }
    public List<Sala> obtenerPorSede(String sedeId) {
        return salaRepository.findBySede_Id(sedeId);
    }
    public Sala actualizar(String id, Sala nueva) {

        Sala sala = obtenerPorId(id);

        sala.setNombre(nueva.getNombre());
        sala.setFilas(nueva.getFilas());
        sala.setColumnas(nueva.getColumnas());
        sala.setTipoSala(nueva.getTipoSala());

        return salaRepository.save(sala);
    }
    public void desactivar(String id) {
        Sala sala = obtenerPorId(id);

        boolean tieneFuncionesActivas = funcionRepository
                .existsBySala_IdAndEstado(id, EstadoFuncion.ACTIVA);

        if (tieneFuncionesActivas) {
            throw new RuntimeException("No se puede desactivar la sala porque tiene funciones activas");
        }

        sala.setActivo(false);
        salaRepository.save(sala);
    }
    public Sala toggleActivo(String id) {
        Sala sala = obtenerPorId(id);

        if (sala.getActivo()) {
            // Se está desactivando - validar
            boolean tieneFuncionesActivas = funcionRepository
                    .existsBySala_IdAndEstado(id, EstadoFuncion.ACTIVA);

            if (tieneFuncionesActivas) {
                throw new RuntimeException("No se puede desactivar la sala porque tiene funciones activas");
            }
        }

        sala.setActivo(!sala.getActivo());
        return salaRepository.save(sala);
    }
}