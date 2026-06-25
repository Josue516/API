package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.CrearReservaDTO;
import com.example.demo.dto.ReservaDetalleDTO;
import com.example.demo.models.Reserva;
import com.example.demo.service.ReservaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

	@Autowired
    private ReservaService reservaService;
    
    @GetMapping
    public List<Reserva> listar() {
        return reservaService.obtenerTodas();
    }
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearReservaDTO dto) {
        String uid = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String id = reservaService.crearReserva(uid, dto.getFuncionId(), dto.getAsientoIds());
        return ResponseEntity.ok(Map.of("id", id));
    }
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable String id, 
            @RequestParam(name = "estado") String estado) { 
        try {
            Reserva reservaActualizada = reservaService.cambiarEstado(id, estado);
            return ResponseEntity.ok(reservaActualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public void actualizar(
            @PathVariable String id,
            @RequestBody Map<String, Object> data
    ) {
        reservaService.actualizar(id, data);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable String id) {

        reservaService.cancelarReserva(id);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/con-detalles")
    public List<ReservaDetalleDTO> conDetalles() {
        return reservaService.obtenerConDetalles();
    }
    @GetMapping("/mis-reservas")
    public List<Reserva> misReservas() {
        String uid = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return reservaService.obtenerPorUsuario(uid);
    }
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPago(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String paypalOrderId = body.get("paypalOrderId");
        String captureId = body.get("captureId");
        reservaService.confirmarPago(id, paypalOrderId, captureId);
        return ResponseEntity.ok(Map.of("mensaje", "Pago confirmado"));
    }
}