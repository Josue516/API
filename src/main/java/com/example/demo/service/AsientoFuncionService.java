package com.example.demo.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.enums.EstadoAsiento;
import com.example.demo.models.AsientoFuncion;
import com.example.demo.repositories.AsientoFuncionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsientoFuncionService {

    private final AsientoFuncionRepository asientoFuncionRepository;
    
    public List<AsientoFuncion> obtenerPorFuncion(String funcionId) {

        return asientoFuncionRepository.findByFuncion_Id(funcionId);
    }
    
    public AsientoFuncion obtenerAsiento(String funcionId, String asientoId) {

        return asientoFuncionRepository
                .findByFuncion_IdAndAsiento_Id(funcionId, asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));
    }
    
    @Transactional
    public void bloquearAsientos(String funcionId, List<String> asientoIds) {

        List<AsientoFuncion> asientos =
                asientoFuncionRepository.findByFuncion_IdAndAsiento_IdIn(funcionId, asientoIds);

        long ahora = System.currentTimeMillis();
        long expiracion = ahora + (10 * 60 * 1000); // 10 minutos

        for (AsientoFuncion af : asientos) {

            if (af.getEstado() != EstadoAsiento.DISPONIBLE) {
                throw new RuntimeException("Uno o más asientos no están disponibles");
            }

            af.setEstado(EstadoAsiento.RESERVADO);
            af.setReservadoHasta(expiracion);
        }

        asientoFuncionRepository.saveAll(asientos);
    }
    @Transactional
    public void liberarAsiento(String funcionId, String asientoId) {

        AsientoFuncion af = obtenerAsiento(funcionId, asientoId);

        af.setEstado(EstadoAsiento.DISPONIBLE);
        af.setReservadoHasta(null);
        af.setReserva(null);

        asientoFuncionRepository.save(af);
    }
    @Transactional
    public void ocuparAsientos(String reservaId) {

        List<AsientoFuncion> asientos =
                asientoFuncionRepository.findByReserva_Id(reservaId);

        for (AsientoFuncion af : asientos) {
            af.setEstado(EstadoAsiento.OCUPADO);
            af.setReservadoHasta(null);
        }

        asientoFuncionRepository.saveAll(asientos);
    }
    @Scheduled(fixedRate = 60000) 
    @Transactional
    public void liberarExpirados() {

        long ahora = System.currentTimeMillis();

        List<AsientoFuncion> expirados =
                asientoFuncionRepository.findByEstadoAndReservadoHastaBefore(
                        EstadoAsiento.RESERVADO,
                        ahora
                );

        for (AsientoFuncion af : expirados) {
            af.setEstado(EstadoAsiento.DISPONIBLE);
            af.setReservadoHasta(null);
            af.setReserva(null);
        }

        asientoFuncionRepository.saveAll(expirados);
    }
}