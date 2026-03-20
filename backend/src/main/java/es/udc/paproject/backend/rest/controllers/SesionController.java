package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.SesionService;
import es.udc.paproject.backend.rest.common.ErrorsDto;
import es.udc.paproject.backend.rest.dtos.SesionDto;
import es.udc.paproject.backend.rest.dtos.SesionConversor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/sesion")
public class SesionController {

    private final static String PAST_DATE_EXCEPTION_CODE = "project.exceptions.PastDateException";

    @Autowired
    private SesionService sesionService;

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

    @GetMapping("/{id}")
    public SesionDto getDetalleSesion(@PathVariable Long id,
                                      HttpServletRequest request)
            throws InstanceNotFoundException, PastDateException {

        // obtener sesion
        Sesion sesion = sesionService.getDetalleSesion(id);

        return SesionConversor.toSesionDto(sesion);
    }
}