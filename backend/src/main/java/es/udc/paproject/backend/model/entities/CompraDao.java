package es.udc.paproject.backend.model.entities;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompraDao extends CrudRepository<Compra,Long>, PagingAndSortingRepository<Compra,Long> {

    Slice<Compra> getHistoricoCompras(Long usuarioId, Pageable pageable);



}
