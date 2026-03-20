package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Compra;

import java.util.List;
import java.util.stream.Collectors;

public class CompraConversor {

    private CompraConversor(){}

    public final static CompraDto toCompraDto(Compra compra){
        return new CompraDto(
                compra.getCompraId(),
                UserConversor.toUserDto(compra.getUser()),
                SesionConversor.toSesionDto(compra.getSesion()),
                compra.getFechaRegistroCompra(),
                compra.getNumLocalidades(),
                compra.getTarjetaBancaria(),
                compra.getEntregada()
        );
    }

    public final static List<CompraDto> toCompraDtos(List<Compra> compras){
        return compras.stream().map(CompraConversor::toCompraDto).collect(Collectors.toList());
    }
}
