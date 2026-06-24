package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {
	List<Reserva> findByUsuario_Id(String usuarioId);
}