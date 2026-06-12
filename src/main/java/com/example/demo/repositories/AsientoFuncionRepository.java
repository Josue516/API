package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.EstadoAsiento;
import com.example.demo.models.AsientoFuncion;

import jakarta.persistence.LockModeType;

@Repository
public interface AsientoFuncionRepository extends JpaRepository<AsientoFuncion, String> {

    List<AsientoFuncion> findByFuncion_Id(String funcionId);

    Optional<AsientoFuncion> findByFuncion_IdAndAsiento_Id(String funcionId, String asientoId);

    List<AsientoFuncion> findByFuncion_IdAndAsiento_IdIn(String funcionId, List<String> asientoIds);

    List<AsientoFuncion> findByEstadoAndReservadoHastaBefore(EstadoAsiento estado, Long time);

    List<AsientoFuncion> findByReserva_Id(String reservaId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT af 
        FROM AsientoFuncion af 
        WHERE af.funcion.id = :funcionId 
        AND af.asiento.id IN :asientoIds
    """)
    List<AsientoFuncion> findAndLockByFuncionAndAsientos(
            @Param("funcionId") String funcionId,
            @Param("asientoIds") List<String> asientoIds
    );
}