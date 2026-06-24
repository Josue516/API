package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.enums.EstadoFuncion;
import com.example.demo.enums.EstadoReserva;
import com.example.demo.models.Asiento;
import com.example.demo.models.AsientoFuncion;
import com.example.demo.models.Funcion;
import com.example.demo.models.Pelicula;
import com.example.demo.models.Reserva;
import com.example.demo.models.Sala;
import com.example.demo.models.Sede;
import com.example.demo.repositories.AsientoFuncionRepository;
import com.example.demo.repositories.AsientoRepository;
import com.example.demo.repositories.FuncionRepository;
import com.example.demo.repositories.PeliculaRepository;
import com.example.demo.repositories.SalaRepository;
import com.example.demo.repositories.SedeRepository;
import com.example.demo.dto.AsientoFuncionDTO;
import com.example.demo.dto.CrearFuncionDTO;
import com.example.demo.dto.FuncionConDetallesDTO;
import com.example.demo.dto.SalaConSedeDTO;
//import com.example.demo.repositories.PeliculaRepository;
//import com.example.demo.repositories.SalaRepository;
import com.example.demo.enums.EstadoAsiento;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;
    private final AsientoFuncionRepository asientoFuncionRepository;
    private final SedeRepository SedeRepository;
    
    public List<Funcion> obtenerTodas() {
        return funcionRepository.findAll();
    }
    
    public Funcion obtenerPorId(String id) {

        return funcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));
    }
    public List<Funcion> obtenerActivas() {
        return funcionRepository.findByEstado(EstadoFuncion.ACTIVA);
    }
    
    @Transactional
    public String crearFuncion(CrearFuncionDTO dto) {

        Pelicula pelicula = peliculaRepository.findById(dto.getPeliculaId())
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
        if (!pelicula.getActivo()) {
            throw new RuntimeException("No se puede crear una función con una película inactiva");
        }
        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
        if (!sala.getActivo()) {
            throw new RuntimeException("No se puede crear una función en una sala inactiva");
        }
        boolean solapamiento = funcionRepository.existeSolapamiento(
                dto.getSalaId(),
                dto.getFechaHora(),
                pelicula.getDuracionMinutos()
        );

        if (solapamiento) {
            throw new RuntimeException("Ya existe una función en esa sala que se solapa con el horario indicado");
        }

        Funcion funcion = Funcion.builder()
                .id(UUID.randomUUID().toString())
                .pelicula(pelicula)
                .sala(sala)
                .fechaHora(dto.getFechaHora())
                .precio(dto.getPrecio())
                .estado(EstadoFuncion.ACTIVA)
                .build();

        Funcion savedFuncion = funcionRepository.save(funcion);

        List<Asiento> asientos = asientoRepository.findBySalaId(sala.getId());

        List<AsientoFuncion> asientosFuncion = asientos.stream()
                .map(asiento -> AsientoFuncion.builder()
                        .id(UUID.randomUUID().toString())
                        .funcion(savedFuncion)
                        .asiento(asiento)
                        .estado(EstadoAsiento.DISPONIBLE)
                        .reservadoHasta(null)
                        .reserva(null)
                        .build()
                )
                .toList();

        asientoFuncionRepository.saveAll(asientosFuncion);

        return savedFuncion.getId();
    }
    public Funcion actualizar(String id, CrearFuncionDTO dto) {

        Funcion funcion = obtenerPorId(id);

        // Cambio de película
        if (!funcion.getPelicula().getId().equals(dto.getPeliculaId())) {
            Pelicula pelicula = peliculaRepository.findById(dto.getPeliculaId())
                    .orElseThrow(() -> new RuntimeException("Película no encontrada"));
            funcion.setPelicula(pelicula);
        }

        // Cambio de sala — validar reservas
        if (!funcion.getSala().getId().equals(dto.getSalaId())) {
            boolean tieneReservas = asientoFuncionRepository
                    .findByFuncion_Id(id)
                    .stream()
                    .anyMatch(af -> af.getReserva() != null);

            if (tieneReservas) {
                throw new RuntimeException("No se puede cambiar la sala, ya hay reservas");
            }

            Sala sala = salaRepository.findById(dto.getSalaId())
                    .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
            funcion.setSala(sala);
        }

        funcion.setFechaHora(dto.getFechaHora());
        funcion.setPrecio(dto.getPrecio());
        funcion.setEstado(dto.getEstado());

        return funcionRepository.save(funcion);
    }
    public Funcion obtenerFuncion(String id) {

        return funcionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Función no encontrada"));
    }
    public List<Funcion> obtenerFuncionesActivas() {
        return funcionRepository.findByEstado(EstadoFuncion.ACTIVA);
    }
    public List<Funcion> obtenerPorPelicula(String peliculaId) {
        return funcionRepository.findByPelicula_Id(peliculaId);
    }
    @Transactional
    public void cancelarFuncion(String id) {

        Funcion funcion = obtenerFuncion(id);

        funcion.setEstado(EstadoFuncion.CANCELADA);
        funcionRepository.save(funcion);

        List<AsientoFuncion> asientos =
                asientoFuncionRepository.findByFuncion_Id(id);

        for (AsientoFuncion af : asientos) {
            af.setEstado(EstadoAsiento.DISPONIBLE);
            af.setReservadoHasta(null);

            if (af.getReserva() != null) {
                Reserva reserva = af.getReserva();
                if (reserva.getEstado() == EstadoReserva.PAGADA) {
                    reserva.setEstado(EstadoReserva.REEMBOLSADA); // marcamos como reembolsada
                } else {
                    reserva.setEstado(EstadoReserva.CANCELADA); // las pendientes se cancelan
                }
                af.setReserva(null);
            }
        }

        asientoFuncionRepository.saveAll(asientos);
    }
    public List<FuncionConDetallesDTO> obtenerConDetalles() {

        List<Funcion> funciones = funcionRepository.findAll();

        return funciones.stream().map(f -> {

            Pelicula pelicula = peliculaRepository.findById(f.getPelicula().getId())
                    .orElseThrow();

            Sala sala = salaRepository.findById(f.getSala().getId())
                    .orElseThrow();

            Sede sede = SedeRepository.findById(sala.getSede().getId())
                    .orElseThrow();

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

            return dto;

        }).toList();
    }
    public List<AsientoFuncionDTO> obtenerAsientosPorFuncion(String funcionId) {
        return asientoFuncionRepository.findByFuncion_Id(funcionId)
                .stream()
                .map(af -> new AsientoFuncionDTO(
                        af.getId(),
                        af.getAsiento().getId(),
                        af.getAsiento().getFila(),
                        af.getAsiento().getNumero(),
                        af.getEstado(),
                        af.getReservadoHasta()
                ))
                .toList();
    }
}