package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.dto.FuncionConDetallesDTO;
import com.example.demo.dto.ReservaDetalleDTO;
import com.example.demo.dto.SalaConSedeDTO;
import com.example.demo.enums.EstadoAsiento;
import com.example.demo.enums.EstadoPago;
import com.example.demo.enums.EstadoReserva;
import com.example.demo.models.AsientoFuncion;
import com.example.demo.models.Funcion;
import com.example.demo.models.Pago;
import com.example.demo.models.Pelicula;
import com.example.demo.models.Reserva;
import com.example.demo.models.Sala;
import com.example.demo.models.Sede;
import com.example.demo.models.Usuario;
import com.example.demo.repositories.AsientoFuncionRepository;
import com.example.demo.repositories.FuncionRepository;
import com.example.demo.repositories.PagoRepository;
import com.example.demo.repositories.ReservaRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.repositories.PeliculaRepository;
import com.example.demo.repositories.SalaRepository;
import com.example.demo.repositories.SedeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final FuncionRepository funcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsientoFuncionRepository asientoFuncionRepository;
    private final PagoRepository pagoRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;
    private final SedeRepository sedeRepository;
    @Autowired
    private PaypalService paypalService;
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }
    @Transactional
    public Reserva actualizar(String id, Map<String, Object> data) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (data.containsKey("estado") && data.get("estado") != null) {
            try {
                String estadoTexto = data.get("estado").toString().toUpperCase();
                reserva.setEstado(EstadoReserva.valueOf(estadoTexto));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("El estado proporcionado no es válido");
            }
        }


        if (data.containsKey("cantidadBoletos") && data.get("cantidadBoletos") != null) {
            try {
                reserva.setCantidadBoletos(
                        Integer.parseInt(data.get("cantidadBoletos").toString())
                );
            } catch (NumberFormatException e) {
                throw new RuntimeException("La cantidad de boletos debe ser un número entero válido");
            }
        }

        if (data.containsKey("total") && data.get("total") != null) {
            try {
                reserva.setTotal(new BigDecimal(data.get("total").toString()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("El total debe ser un valor decimal válido");
            }
        }

        return reservaRepository.save(reserva);
    }
    
    @Transactional
    public String crearReserva(String usuarioId, String funcionId, List<String> asientoIds) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Funcion funcion = funcionRepository.findById(funcionId)
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));
        List<AsientoFuncion> asientos =
                asientoFuncionRepository.findAndLockByFuncionAndAsientos(funcionId, asientoIds);
        if (asientos.size() != asientoIds.size()) {
            throw new RuntimeException("Algunos asientos no existen en esta función");
        }

        // Validar disponibilidad
        for (AsientoFuncion af : asientos) {
            if (af.getEstado() != EstadoAsiento.DISPONIBLE) {
                throw new RuntimeException("Uno o más asientos no están disponibles");
            }
        }

        long now = System.currentTimeMillis();
        long expiracion = now + (10 * 60 * 1000);
        BigDecimal total = funcion.getPrecio()
                .multiply(BigDecimal.valueOf(asientoIds.size()));

        Reserva reserva = Reserva.builder()
                .id(UUID.randomUUID().toString())
                .usuario(usuario)
                .funcion(funcion)
                .cantidadBoletos(asientoIds.size())
                .total(total)
                .estado(EstadoReserva.PENDIENTE)
                .createdAt(now)
                .build();
        reservaRepository.save(reserva);
        // Bloquear asientos
        for (AsientoFuncion af : asientos) {
            af.setEstado(EstadoAsiento.RESERVADO);
            af.setReserva(reserva);
            af.setReservadoHasta(expiracion);
        }

        asientoFuncionRepository.saveAll(asientos);
        System.out.println("FuncionId: " + funcionId);
        System.out.println("AsientoIds: " + asientoIds);
        return reserva.getId();
    }
    @Transactional
    public void confirmarPago(String reservaId, String paypalOrderId, String captureId) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        String captureIdReal = paypalService.capturarOrden(paypalOrderId);

        reserva.setEstado(EstadoReserva.PAGADA);

        Pago pago = Pago.builder()
                .id(UUID.randomUUID().toString())
                .reserva(reserva)
                .paypalOrderId(paypalOrderId)
                .paypalCaptureId(captureIdReal)
                .monto(reserva.getTotal())
                .estado(EstadoPago.COMPLETADO)
                .fechaPago(System.currentTimeMillis())
                .build();

        pagoRepository.save(pago); 
       List<AsientoFuncion> asientos = asientoFuncionRepository.findByReserva_Id(reservaId);
        for (AsientoFuncion af : asientos) {
            af.setEstado(EstadoAsiento.OCUPADO);
            af.setReservadoHasta(null);
        } 
    }
    @Transactional
    public void cancelarReserva(String reservaId) {

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstado(EstadoReserva.CANCELADA);

        liberarAsientos(reservaId);

        reservaRepository.save(reserva);
    }
    private void liberarAsientos(String reservaId) {

        List<AsientoFuncion> asientos = asientoFuncionRepository
                .findByReserva_Id(reservaId);

        for (AsientoFuncion af : asientos) {
            af.setEstado(EstadoAsiento.DISPONIBLE);
            af.setReserva(null);
            af.setReservadoHasta(null);
        }

        asientoFuncionRepository.saveAll(asientos);
    }
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void liberarReservasExpiradas() {

        long now = System.currentTimeMillis();

        List<AsientoFuncion> expirados =
                asientoFuncionRepository.findByEstadoAndReservadoHastaBefore(
                        EstadoAsiento.RESERVADO,
                        now
                );

        for (AsientoFuncion af : expirados) {
            af.setEstado(EstadoAsiento.DISPONIBLE);
            af.setReserva(null);
            af.setReservadoHasta(null);
        }

        asientoFuncionRepository.saveAll(expirados);
    }
    public List<ReservaDetalleDTO> obtenerConDetalles() {

        List<Reserva> reservas = reservaRepository.findAll();

        List<ReservaDetalleDTO> resultado = new ArrayList<>();

        for (Reserva r : reservas) {

            Usuario usuario = usuarioRepository.findById(r.getUsuario().getId())
                    .orElse(null);

            Funcion funcion = funcionRepository.findById(r.getFuncion().getId())
                    .orElse(null);

            Pelicula pelicula = peliculaRepository.findById(funcion.getPelicula().getId())
                    .orElse(null);

            Sala sala = salaRepository.findById(funcion.getSala().getId())
                    .orElse(null);

            Sede sede = sedeRepository.findById(sala.getSede().getId())
                    .orElse(null);
            int capacidadCalculada = sala.getFilas() * sala.getColumnas();
            SalaConSedeDTO salaDto = new SalaConSedeDTO(
            	    sala.getId(),          // 1. id
            	    sala.getNombre(),      // 2. nombre
            	    sala.getFilas(),       // 3. filas
            	    sala.getColumnas(),    // 4. columnas
            	    capacidadCalculada,    // 5. capacidad
            	    sala.getTipoSala(),    // 6. tipoSala (Ambos son del Enum 'TipoSala')
            	    sala.getActivo(),      // 7. activo
            	    sede                   // 8. sede
            	);

            FuncionConDetallesDTO funcionDto = new FuncionConDetallesDTO(
                    funcion.getId(),
                    funcion.getFechaHora(),
                    funcion.getPrecio(),
                    funcion.getEstado(),
                    pelicula,
                    salaDto
            );

            ReservaDetalleDTO dto = new ReservaDetalleDTO(
                    r.getId(),
                    usuario,
                    funcionDto,
                    r.getCantidadBoletos(),
                    r.getTotal(),
                    r.getEstado(),
                    r.getCreatedAt(),
                    r.getPago()
            );

            resultado.add(dto);
        }

        return resultado;
    }
    public List<Reserva> obtenerPorUsuario(String usuarioId) {
        return reservaRepository.findByUsuario_Id(usuarioId);
    }
    public void cambiarEstado(String id, String estadoStr) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        EstadoReserva nuevoEstado;
        
        // Traducimos "CONFIRMADA" (Web) a "PAGADA" (Tu Enum)
        if ("CONFIRMADA".equalsIgnoreCase(estadoStr)) {
            nuevoEstado = EstadoReserva.PAGADA;
        } else {
            // Para cualquier otro estado (CANCELADA, REEMBOLSADA, PENDIENTE) hacemos la conversión normal
            nuevoEstado = EstadoReserva.valueOf(estadoStr.toUpperCase());
        }

        reserva.setEstado(nuevoEstado);
        reservaRepository.save(reserva);
    }
    @Transactional
    public void procesarReembolso(String reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + reservaId));
        if (reserva.getEstado() != EstadoReserva.PAGADA) { 
            throw new IllegalStateException("Solo se pueden reembolsar reservas con estado PAGADA");
        }
        reserva.setEstado(EstadoReserva.REEMBOLSADA);
        List<AsientoFuncion> asientosDeLaFuncion = asientoFuncionRepository.findByReserva_Id(reservaId);
        for (AsientoFuncion af : asientosDeLaFuncion) {
            af.setEstado(EstadoAsiento.DISPONIBLE); 
            af.setReservadoHasta(null); 
            af.setReserva(null); // Rompe la relación limpiamente
        }
        asientoFuncionRepository.saveAll(asientosDeLaFuncion);
        reservaRepository.save(reserva); // 🟢 Al estar al final, JPA procesa los estados de manera coherente
    }
}