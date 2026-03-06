package productos;

import java.time.LocalDateTime;
import java.util.Date;

import tienda.Usuarios.Empleado;
import tienda.Ventas.EstadoPedido;
import tienda.Ventas.Pago;

public class Valoracion {
    private LocalDateTime fecha;
    private double precioTasacion;
    private EstadoProducto estadoProducto;
    private EstadoValoracion estadoValoracion ;
    private Empleado empleado;
    private Pago pago;

    public Valoracion(double precioTasacion, EstadoProducto estado, Empleado empleado){
        this.fecha = LocalDateTime.now();
        this.precioTasacion = precioTasacion;
        this.estadoProducto = estado;
        this.empleado = empleado;
    }

public boolean pagar(String tarjeta, int cvv, Date caducidad) {
        
    	Pago nuevoPago = new Pago(tarjeta, TASA_VALORACION, caducidad, cvv);
        this.pago = nuevoPago;

        if (nuevoPago.isExito()) {
            this.estadoValoracion = EstadoValoracion.PENDIENTE;

            return true;
        }
        return false;
    }
    
    // Getters y Setters
    public double getPrecioTasacion() { return precioTasacion; }
    public EstadoProducto getEstadoProducto() { return estadoProducto; }
    
    public void setEstadoValoracion(EstadoValoracion ev) { this.estadoValoracion = ev;}
    public EstadoValoracion getEstadoValoracion() {return this.estadoValoracion;}
}
