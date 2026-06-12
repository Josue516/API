package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, String> {
	Optional<Pago> findByReserva_Id(String reservaId);
}