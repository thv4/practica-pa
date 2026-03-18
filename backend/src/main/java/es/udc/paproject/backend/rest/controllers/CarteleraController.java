package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.CarteleraService;
import es.udc.paproject.backend.rest.dtos.CarteleraItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.udc.paproject.backend.rest.dtos.PeliculaConversor.toPeliculaDto;

@RestController
@RequestMapping("/carteleras")
public class CarteleraController {

    @Autowired
    private CarteleraService carteleraService;

    @GetMapping("/cartelera")
    public List<CarteleraItemDto> getCartelera(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha)
            throws PastDateException {

        // 1. Llamamos al servicio
        Map<Pelicula, List<Sesion>> mapaCartelera = carteleraService.getCartelera(fecha);

        // 2. Convertimos el Mapa a una Lista de DTOs manteniendo el orden
        return mapaCartelera.entrySet().stream()
                .map(entry -> new CarteleraItemDto(
                        toPeliculaDto(entry.getKey()),
                        toSesionDto(entry.getValue())
                ))
                .collect(Collectors.toList());
    }
}

