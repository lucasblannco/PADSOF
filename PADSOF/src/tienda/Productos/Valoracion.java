package tienda.Productos;

import java.time.LocalDateTime;

import tienda.Usuarios.Empleado;

public class Valoracion {
    private LocalDateTime fecha;
    private double precioTasacion;
    private EstadoProducto estado;
    private Empleado empleado;

    public Valoracion(double precioTasacion, EstadoProducto estado, Empleado empleado){
        this.fecha = LocalDateTime.now();
        this.precioTasacion = precioTasacion;
        this.estado = estado;
        this.empleado = empleado;
    }

    // Getters y Setters
    public double getPrecioTasacion() { return precioTasacion; }
    public EstadoProducto getEstado() { return estado; }
}
