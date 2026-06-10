package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Reserva;
import com.example.demo.service.FirestoreService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private FirestoreService service;

    @GetMapping
    public List<Reserva> listar() throws Exception {
        return service.getAll("reservas", Reserva.class);
    }
}
