package es.udc.paproject.backend.model.entities;

import jakarta.persistence.*;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Compra")
public class Compra {

    private Long compraId;
    private User user;
    private Sesion sesion;
    private LocalDateTime fechaRegistroCompra;
    private int numLocalidades;
    private String tarjetaBancaria;
    private boolean entregada;

    @Transient
    private BigDecimal precioTotalCalculado(){
        return sesion.getPrecio().multiply(BigDecimal.valueOf(getNumLocalidades()));
    }

    public Compra(){}

    public Compra(User user, Sesion session,LocalDateTime fechaCompra, int numLocalidades,String tarjeta, boolean entregada){
        this.user = user;
        this.sesion = session;
        this.fechaRegistroCompra = fechaCompra;
        this.numLocalidades = numLocalidades;
        this.tarjetaBancaria = tarjeta;
        this.entregada = entregada;
    }

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    public Long getCompraId() {
        return compraId;
    }
    public void setCompraId(Long id){this.compraId=id;}

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    public User getUser(){
        return user;
    }
    public void setUser(User user){
        this.user = user;
    }

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name="sesionId")
    public Sesion getSesion(){
        return sesion;
    }
    public void setSesion(Sesion sesion){
        this.sesion = sesion;
    }
    public LocalDateTime getFechaRegistroCompra(){
        return fechaRegistroCompra;
    }
    public void setFechaRegistroCompra(LocalDateTime fecha){
        this.fechaRegistroCompra = fecha;
    }
    public int getNumLocalidades(){
        return numLocalidades;
    }
    public void setNumLocalidades(int num){
        this.numLocalidades = num;
    }
    public String getTarjetaBancaria(){
        return tarjetaBancaria;
    }
    public void setTarjetaBancaria(String tarj){
        this.tarjetaBancaria = tarj;
    }
    public boolean getEntregada(){
        return entregada;
    }
    public void setEntregada(boolean entr){
        this.entregada = entr;
    }

}
