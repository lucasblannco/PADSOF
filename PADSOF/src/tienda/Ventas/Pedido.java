package tienda.Ventas;

import java.util.*;

import java.time.*;
import tienda.Usuarios.Cliente;

public class Pedido {
    private String idPedido;
    private LocalDateTime fechaCreacion; //cuando se ha pedido
    private LocalDateTime fechaPreparado;
    private LocalDateTime fechaEntregado;
    private Cliente cliente;
    private List<LineaPedido> lineas;
    private Pago pago;
    private double total;
    private EstadoPedido estado; 
    private String codigoRecogida;

    public Pedido(Cliente cliente, Carrito carrito) {

    	this.idPedido = "ORDER-" + java.util.UUID.randomUUID().toString().substring(0,8);
        this.cliente = cliente;
        this.estado = EstadoPedido.PENDIENTE_PAGO;
        this.fechaCreacion = LocalDateTime.now();
        this.lineas = new ArrayList<>();
        
        
        // Pasamos lo que hay en el carrito a líneas de pedido fijas
        for (LineaCarrito linea : carrito.getLineas()) {
            this.lineas.add(new LineaPedido(linea.getProducto(), linea.getCantidad(),linea.getProducto().calcularPrecioFinal()));
        }
        this.total = calcularTotal();
    }

    public void actualizarEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public EstadoPedido getEstado() {
        return estado;
    }
    
    public boolean pagar(String tarjeta, int cvv, Date caducidad) {
        
        Pago nuevoPago = new Pago(tarjeta, this.total);
        this.pago = nuevoPago;

        if (nuevoPago.isExito()) {
            this.estado = EstadoPedido.PAGADO;
            this.codigoRecogida = "PICK-" + idPedido;
            return true;
        }
        return false;
    }

    public double calcularTotal() {
    	double t=0;
        for(LineaPedido l: this.lineas) {
        	t+=l.precio();
        }
        return t;
    }
}