package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.EstadoFuncion;
import com.example.demo.models.Funcion;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, String> {
    List<Funcion> findByEstado(EstadoFuncion estado);
    List<Funcion> findByPelicula_Id(String peliculaId);
    boolean existsBySala_IdAndEstado(String salaId, EstadoFuncion estado);
    
    @Query("SELECT COUNT(f) > 0 FROM Funcion f WHERE f.sala.id = :salaId " +
    	       "AND f.estado = 'ACTIVA' " +
    	       "AND :nuevaFechaInicio < (f.fechaHora + ((f.pelicula.duracionMinutos + 15) * 60000)) " +
    	       "AND (:nuevaFechaInicio + ((:duracionMinutos + 15) * 60000)) > f.fechaHora")
    	boolean existeSolapamiento(
    	    @Param("salaId") String salaId,
    	    @Param("nuevaFechaInicio") Long nuevaFechaInicio,
    	    @Param("duracionMinutos") Integer duracionMinutos
    	);
}