package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Compra;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CompraConversor {

    private CompraConversor(){}

    public final static CompraDto toCompraDto(Compra compra){
        BigDecimal precioTotal = compra.getSesion().getPrecio()
                .multiply(BigDecimal.valueOf(compra.getNumLocalidades()));

        return new CompraDto(
                compra.getCompraId(),
                compra.getSesion().getId(),
                compra.getSesion().getPelicula().getTitulo(),
                compra.getSesion().getFechaHora(),
                compra.getSesion().getSala().getNombre(),
                compra.getFechaRegistroCompra(),
                compra.getNumLocalidades(),
                precioTotal,
                compra.getEntregada()
        );
    }

    public final static List<CompraDto> toCompraDtos(List<Compra> compras){
        return compras.stream().map(CompraConversor::toCompraDto).collect(Collectors.toList());
    }
}

