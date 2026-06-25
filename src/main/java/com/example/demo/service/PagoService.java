package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.EstadoAsiento;
import com.example.demo.enums.EstadoPago;
import com.example.demo.enums.EstadoReserva;
import com.example.demo.models.AsientoFuncion;
import com.example.demo.models.Pago;
import com.example.demo.models.Reserva;
import com.example.demo.repositories.AsientoFuncionRepository;
import com.example.demo.repositories.PagoRepository;
import com.example.demo.repositories.ReservaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final AsientoFuncionRepository asientoFuncionRepository;
    @Autowired
    private PaypalService paypalService;
    public String crearOrden(String reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("La reserva no está en estado válido para pago");
        }

        return paypalService.crearOrden(reserva.getTotal(), "USD");
    }
    @Transactional
    public String registrarPago(
            String reservaId,
            String paypalOrderId,
            String paypalCaptureId
    ) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("La reserva no está en estado válido para pago");
        }

        // 1. Crear pago
        Pago pago = Pago.builder()
                .id(UUID.randomUUID().toString())
                .reserva(reserva)
                .paypalOrderId(paypalOrderId)
                .paypalCaptureId(paypalCaptureId)
                .monto(reserva.getTotal())
                .estado(EstadoPago.COMPLETADO)
                .fechaPago(System.currentTimeMillis())
                .build();

        pagoRepository.save(pago);

        // 2. Actualizar reserva
        reserva.setEstado(EstadoReserva.PAGADA);
        reservaRepository.save(reserva);

        // 3. Confirmar asientos (RESERVADO → OCUPADO)
        List<AsientoFuncion> asientos =
                asientoFuncionRepository.findByReserva_Id(reservaId);

        for (AsientoFuncion af : asientos) {
            af.setEstado(EstadoAsiento.OCUPADO);
            af.setReservadoHasta(null);
        }

        asientoFuncionRepository.saveAll(asientos);

        return pago.getId();
    }
    public Pago obtenerPagoPorReserva(String reservaId) {

        return pagoRepository.findByReserva_Id(reservaId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }
    @Transactional
    public void reembolsarPago(String reservaId) {

        Pago pago = pagoRepository.findByReserva_Id(reservaId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 1. Estado del pago
        pago.setEstado(EstadoPago.REEMBOLSADO);
        pagoRepository.save(pago);

        // 2. Estado de la reserva
        reserva.setEstado(EstadoReserva.REEMBOLSADA);
        reservaRepository.save(reserva);

        // 3. Liberar asientos
        List<AsientoFuncion> asientos =
                asientoFuncionRepository.findByReserva_Id(reservaId);

        for (AsientoFuncion af : asientos) {
            af.setEstado(EstadoAsiento.DISPONIBLE);
            af.setReserva(null);
            af.setReservadoHasta(null);
        }

        asientoFuncionRepository.saveAll(asientos);
    }
}