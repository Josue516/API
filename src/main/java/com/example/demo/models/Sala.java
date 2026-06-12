package com.example.demo.models;

import com.example.demo.enums.TipoSala;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "salas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sedeId", nullable = false)
    private Sede sede;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "filas", nullable = false)
    private Integer filas;

    @Column(name = "columnas", nullable = false)
    private Integer columnas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoSala", nullable = false)
    private TipoSala tipoSala;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
    
    @Transient
    public Integer getCapacidad() {
        return filas * columnas;
    }
}