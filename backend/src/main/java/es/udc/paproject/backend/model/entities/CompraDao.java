package es.udc.paproject.backend.model.entities;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompraDao extends CrudRepository<Compra,Long>, PagingAndSortingRepository<Compra,Long>, JpaRepository<Compra,Long> {

    Slice<Compra> findByUser_IdOrderByFechaRegistroCompraDesc(Long usuarioId, Pageable pageable);

}
