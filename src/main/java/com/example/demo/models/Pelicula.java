package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

import com.example.demo.enums.ClasificacionPelicula;
import com.example.demo.enums.Genero;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "peliculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pelicula {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "sinopsis", columnDefinition = "TEXT")
    private String sinopsis;

    @Column(name = "duracionMinutos", nullable = false)
    private Integer duracionMinutos;

    @Enumerated(EnumType.STRING)
    @Column(name = "clasificacion", nullable = false)
    private ClasificacionPelicula clasificacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "peliculaGeneros",
        joinColumns = @JoinColumn(name = "peliculaId")
    )
    @Column(name = "genero")
    private Set<Genero> generos;

    @Column(name = "imagenUrl", columnDefinition = "TEXT")
    private String imagenUrl;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "createdAt", nullable = false)
    private Long createdAt;

    @Column(name = "fechaEstreno")
    private Long fechaEstreno;
}