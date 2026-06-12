package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class CrearReservaDTO {
    private String funcionId;

    private List<String> asientoIds;
}