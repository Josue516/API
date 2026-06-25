package com.example.demo.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Pago;
import com.example.demo.service.PagoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody Map<String, String> body) {
        String reservaId = body.get("reservaId");
        String orderId = pagoService.crearOrden(reservaId);
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }

    @GetMapping("/reserva/{reservaId}")
    public Pago obtenerPorReserva(@PathVariable String reservaId) {
        return pagoService.obtenerPagoPorReserva(reservaId);
    }
}