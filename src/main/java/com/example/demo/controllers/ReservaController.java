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
import com.example.demo.dto.ReservaDetalleDTO;
import com.example.demo.dto.SalaConSedeDTO;
import com.example.demo.models.Funcion;
import com.example.demo.models.Pelicula;
import com.example.demo.models.Reserva;
import com.example.demo.models.Sala;
import com.example.demo.models.Sede;
import com.example.demo.models.Usuario;
import com.example.demo.service.FirestoreService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

	@Autowired
	private FirestoreService service;

	@GetMapping
	public List<Reserva> listar() throws Exception {
		return service.getAll("reservas", Reserva.class);
	}
	@PostMapping
	public ResponseEntity<Map<String, String>> crear(@RequestBody Reserva r) throws Exception {
	    String id = service.create("reservas", r);
	    return ResponseEntity.ok(Map.of("id", id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> actualizar(
	        @PathVariable String id,
	        @RequestBody Map<String, Object> data) throws Exception {
	    service.update("reservas", id, data);
	    return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable String id) throws Exception {
	    service.softDelete("reservas", id, "estado", "CANCELADA");
	    return ResponseEntity.noContent().build();
	}
	@GetMapping("/con-detalles")
	public List<ReservaDetalleDTO> listarConDetalles() throws Exception {
	    List<Reserva> reservas = service.getAll("reservas", Reserva.class);
	    List<ReservaDetalleDTO> resultado = new ArrayList<>();

	    for (Reserva r : reservas) {
	        Usuario usuario = service.getById("usuarios", r.getUsuarioId(), Usuario.class);
	        Funcion funcion = service.getById("funciones", r.getFuncionId(), Funcion.class);
	        Pelicula pelicula = service.getById("peliculas", funcion.getPeliculaId(), Pelicula.class);
	        Sala sala = service.getById("salas", funcion.getSalaId(), Sala.class);
	        Sede sede = service.getById("sedes", sala.getSedeId(), Sede.class);

	        SalaConSedeDTO salaDto = new SalaConSedeDTO();
	        salaDto.setId(sala.getId());
	        salaDto.setNombre(sala.getNombre());
	        salaDto.setCapacidad(sala.getCapacidad());
	        salaDto.setTipoSala(sala.getTipoSala());
	        salaDto.setActivo(sala.getActivo());
	        salaDto.setSede(sede);

	        FuncionConDetallesDTO funcionDto = new FuncionConDetallesDTO();
	        funcionDto.setId(funcion.getId());
	        funcionDto.setFechaHora(funcion.getFechaHora());
	        funcionDto.setPrecio(funcion.getPrecio());
	        funcionDto.setEstado(funcion.getEstado());
	        funcionDto.setPelicula(pelicula);
	        funcionDto.setSala(salaDto);

	        ReservaDetalleDTO dto = new ReservaDetalleDTO();
	        dto.setId(r.getId());
	        dto.setUsuario(usuario);
	        dto.setFuncion(funcionDto);
	        dto.setCantidadBoletos(r.getCantidadBoletos());
	        dto.setTotal(r.getTotal());
	        dto.setEstado(r.getEstado());
	        dto.setCreatedAt(r.getCreatedAt());
	        dto.setPago(r.getPago());

	        resultado.add(dto);
	    }

	    return resultado;
	}
}
