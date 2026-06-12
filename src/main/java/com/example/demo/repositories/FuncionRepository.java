package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.EstadoFuncion;
import com.example.demo.models.Funcion;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, String> {
    List<Funcion> findByEstado(EstadoFuncion estado);
    List<Funcion> findByPelicula_Id(String peliculaId);
    boolean existsBySala_IdAndFechaHora(String salaId, Long fechaHora);
}