package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.SesionService;
import es.udc.paproject.backend.rest.dtos.SesionDto;
import es.udc.paproject.backend.rest.dtos.SesionConversor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sesion")
public class SesionController {

    @Autowired
    private SesionService sesionService;

    @GetMapping("/{id}")
    public SesionDto getDetalleSesion(@PathVariable Long id,
                                      HttpServletRequest request)
            throws InstanceNotFoundException {

        // obtener sesion
        Sesion sesion = sesionService.getDetalleSesion(id);

        //usuario autenticado (JwtFilter añade "userId" si hay token válido)
        Boolean usuarioAutenticado = request.getAttribute("userId") != null;

        return SesionConversor.toSesionDto(sesion, usuarioAutenticado);
    }
}