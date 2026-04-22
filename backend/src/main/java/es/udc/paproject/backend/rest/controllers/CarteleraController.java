package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.InvalidPost6DaysDateException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.CarteleraService;
import es.udc.paproject.backend.rest.common.ErrorsDto;
import es.udc.paproject.backend.rest.dtos.CarteleraItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static es.udc.paproject.backend.rest.dtos.PeliculaResumenConversor.toPeliculaResumenDto;
import static es.udc.paproject.backend.rest.dtos.SesionResumenConversor.toSesionResumenDto;

@RestController
@RequestMapping("/carteleras")
public class CarteleraController {

    private final static String PAST_DATE_EXCEPTION_CODE = "project.exceptions.PastDateException";
    private final static String INVALID_POST_6_DAYS_DATE_EXCEPTION_CODE = "project.exceptions.InvalidPost6DaysDateException";

    @Autowired
    private CarteleraService carteleraService;

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(PastDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorsDto handlePastDateException(PastDateException exception, Locale locale) {
        String errorMessage = messageSource.getMessage(PAST_DATE_EXCEPTION_CODE,
                null, PAST_DATE_EXCEPTION_CODE, locale);

        return new ErrorsDto(errorMessage);
    }

    @ExceptionHandler(InvalidPost6DaysDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorsDto handleInvalidPost6DaysDateException(InvalidPost6DaysDateException exception, Locale locale) {
        String errorMessage = messageSource.getMessage(INVALID_POST_6_DAYS_DATE_EXCEPTION_CODE,
                null, INVALID_POST_6_DAYS_DATE_EXCEPTION_CODE, locale);

        return new ErrorsDto(errorMessage);
    }

    @GetMapping("/cartelera")
    public List<CarteleraItemDto> getCartelera(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
            throws PastDateException, InvalidPost6DaysDateException {

        // 1. Llamamos al servicio
        Map<Pelicula, List<Sesion>> mapaCartelera = carteleraService.getCartelera(fecha);

        // 2. Convertimos el Mapa a una Lista de DTOs manteniendo el orden
        return mapaCartelera.entrySet().stream()
                .map(entry -> new CarteleraItemDto(
                        toPeliculaResumenDto(entry.getKey()),
                        toSesionResumenDto(entry.getValue())
                ))
                .collect(Collectors.toList());
    }
}

