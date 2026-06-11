package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula implements FirestoreEntity{
    private String id; 
    private String titulo;
    private String sinopsis;
    private Integer duracionMinutos;
    private String clasificacion;
    private List<String> generos; 
    private String imagenUrl;
    private Boolean activo;
    private Long createAt;
    private String fechaEstreno;
}