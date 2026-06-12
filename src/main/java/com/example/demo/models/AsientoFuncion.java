package com.example.demo.models;

import com.example.demo.enums.EstadoAsiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "asientosFuncion",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"funcionId", "asientoId"}
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoFuncion {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionId", nullable = false)
    private Funcion funcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asientoId", nullable = false)
    private Asiento asiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAsiento estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservaId")
    private Reserva reserva;

    @Column(name = "reservadoHasta")
    private Long reservadoHasta;
}