package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.MaxLocalidadesExceedException;
import es.udc.paproject.backend.model.exceptions.SesionExpiredException;
import es.udc.paproject.backend.model.services.Block;
import es.udc.paproject.backend.model.services.CompraService;
import es.udc.paproject.backend.rest.common.ErrorsDto;
import es.udc.paproject.backend.rest.dtos.BlockDto;
import es.udc.paproject.backend.rest.dtos.CompraConversor;
import es.udc.paproject.backend.rest.dtos.CompraDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private final static String MAX_LOCALIDADES_EXCEED_EXCEPTION_CODE = "project.exceptions.MaxLocalidadesExceedException";
    private final static String SESION_EXPIRED_EXCEPTION = "project.exceptions.SesionExpiredException";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CompraService compraService;

    @ExceptionHandler(MaxLocalidadesExceedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorsDto handleMaxLocalidadesExceedException(MaxLocalidadesExceedException exception, Locale locale){
        String errorMessage = messageSource.getMessage(MAX_LOCALIDADES_EXCEED_EXCEPTION_CODE,
                null,MAX_LOCALIDADES_EXCEED_EXCEPTION_CODE,locale);

        return new ErrorsDto(errorMessage);
    }

    @ExceptionHandler(SesionExpiredException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorsDto handleSesionExpiredException(SesionExpiredException exception, Locale locale){

        String errorMessage = messageSource.getMessage(SESION_EXPIRED_EXCEPTION,null,SESION_EXPIRED_EXCEPTION,locale);

        return new ErrorsDto(errorMessage);
    }

    @PostMapping("/buy")
    public CompraDto buy(@RequestAttribute Long userId, @Validated @RequestBody CompraDto params) throws InstanceNotFoundException, MaxLocalidadesExceedException,SesionExpiredException{

        Compra compra = compraService.comprarEntradas(params.getSesion().getId(),userId, params.getNumLocalidades(), params.getTarjetaBancaria());

        return CompraConversor.toCompraDto(compra);
    }

    @GetMapping("/compras")
    public BlockDto<CompraDto> getHistory(@RequestAttribute Long userId, @RequestParam(defaultValue = "0") int page) throws InstanceNotFoundException {
        Slice<Compra> slice = compraService.findHistoricoCompras(userId,page,2);

        List<CompraDto> compraDtos = CompraConversor.toCompraDtos(slice.getContent());

        return new BlockDto<>(compraDtos, slice.hasNext());
    }
}
