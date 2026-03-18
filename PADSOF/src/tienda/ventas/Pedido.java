package ventas;

import java.util.*;

import usuario.Clientes;
import productos.*;

import java.time.*;
import tienda.Usuarios.Cliente;
import tienda.Productos.*; 

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
    private Descuento descuentoAplicado;

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
        
    	Pago nuevoPago = new Pago(tarjeta, this.total, caducidad, cvv);
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
    
    public boolean productoPertenece(ProductoVenta p) {
    	
    	for(LineaPedido l : lineas) {
    		if(l.productoPertence(p)==true) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public List<LineaPedido> getLineas(){
    	return this.lineas;
    }
    
    public int contarUnidadesDe(String idProductoBuscado) {
        int totalUnidades = 0;
        
        for (LineaPedido linea : this.lineas) {
            // Comparamos el ID del producto de la línea con el que buscamos
            if (linea.getProducto().getId().equals(idProductoBuscado)) {
                totalUnidades += linea.getCantidad();
            }
        }
        
        return totalUnidades;
    }
    
    public double getPrecioDeProducto(String idProductoBuscado) {
        // Recorremos todas las líneas que el usuario ha añadido al carrito
        for (LineaPedido linea : this.lineas) {
            
            // Obtenemos el producto de esa línea y comparamos su ID
            if (linea.getProducto().getId().equals(idProductoBuscado)) {
                
                // Si lo encontramos, devolvemos el precio que tiene el producto
                return linea.getProducto().getPrecioOficial();
            }
        }
        
        // Si terminamos el bucle y no hemos encontrado nada, devolvemos 0
        return 0.0;
    }
    
    public double getTotalBruto() {
        double total = 0;
        for (LineaPedido linea : this.lineas) {
            total += (linea.getCantidad()*(linea.getProducto().getPrecioOficial())); // Subtotal suele ser precio * cantidad
        }
        return total;
    }
}