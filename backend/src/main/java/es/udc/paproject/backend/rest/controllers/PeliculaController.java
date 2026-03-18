package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PeliculaService;
import es.udc.paproject.backend.rest.dtos.PeliculaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.udc.paproject.backend.rest.dtos.PeliculaConversor.toPeliculaDto;

@RestController
@RequestMapping("/pelicula")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    @GetMapping("/peliculas/{id}")
    public PeliculaDto  getPelicula(@PathVariable Long id) throws InstanceNotFoundException {
        return toPeliculaDto(peliculaService.getDetallePelicula(id));
    }
}
