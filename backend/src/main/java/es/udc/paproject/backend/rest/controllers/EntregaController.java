package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.exceptions.*;
import es.udc.paproject.backend.model.services.CompraService;
import es.udc.paproject.backend.rest.common.ErrorsDto;
import es.udc.paproject.backend.rest.dtos.EntregarEntradasParamsDto;
import es.udc.paproject.backend.rest.dtos.EntregaResultDto;
import es.udc.paproject.backend.rest.dtos.EntregaConversor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;

@RestController
@RequestMapping("/entregas")
public class EntregaController {

    private final static String INCORRECT_CREDIT_CARD_EXCEPTION_CODE = "project.exceptions.IncorrectCreditCardException";
    private final static String TICKETS_ALREADY_DELIVERED_EXCEPTION_CODE = "project.exceptions.TicketsAlreadyDeliveredException";
    private final static String SESSION_ALREADY_STARTED_EXCEPTION_CODE = "project.exceptions.SessionAlreadyStartedException";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CompraService compraService;

    @ExceptionHandler(IncorrectCreditCardException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorsDto handleIncorrectCreditCardException(IncorrectCreditCardException exception, Locale locale) {

        String errorMessage = messageSource.getMessage(INCORRECT_CREDIT_CARD_EXCEPTION_CODE,
                new Object[]{exception.getMessage()}, INCORRECT_CREDIT_CARD_EXCEPTION_CODE, locale);

        return new ErrorsDto(errorMessage);
    }

    @ExceptionHandler(TicketsAlreadyDeliveredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorsDto handleTicketsAlreadyDeliveredException(TicketsAlreadyDeliveredException exception, Locale locale) {

        String errorMessage = messageSource.getMessage(TICKETS_ALREADY_DELIVERED_EXCEPTION_CODE,
                new Object[]{exception.getMessage()}, TICKETS_ALREADY_DELIVERED_EXCEPTION_CODE, locale);

        return new ErrorsDto(errorMessage);
    }

    @ExceptionHandler(SessionAlreadyStartedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorsDto handleSessionAlreadyStartedException(SessionAlreadyStartedException exception, Locale locale) {

        String errorMessage = messageSource.getMessage(SESSION_ALREADY_STARTED_EXCEPTION_CODE,
                new Object[]{exception.getMessage()}, SESSION_ALREADY_STARTED_EXCEPTION_CODE, locale);

        return new ErrorsDto(errorMessage);
    }


    @PostMapping("/entregar")
    public EntregaResultDto entregarEntradas(
            @RequestAttribute Long userId,
            @Valid @RequestBody EntregarEntradasParamsDto params)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            TicketsAlreadyDeliveredException, SessionAlreadyStartedException {

        compraService.entregarEntradas(params.getCompraId(), params.getTarjetaBancaria());

        return EntregaConversor.toSuccessResult();
    }
}