package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Pelicula;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, String> {
	List<Pelicula> findByActivoTrue();
	//ESTO PARA UN FILTRO DE BUSQUEDA PARA EL CLIENTE
	@Query("SELECT p FROM Pelicula p WHERE p.activo = true")
	List<Pelicula> findPeliculasActivas();
}