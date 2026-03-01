package tienda.Ventas;

import java.time.LocalDate;

public class Descuento {
    private String idProducto; 
    private double porcentaje;  //0.20 = 20%
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Descuento(String idProducto, double porcentaje, LocalDate inicio, LocalDate fin) {
        this.idProducto = idProducto;
        this.porcentaje = porcentaje;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    // Método para saber si el descuento es válido hoy
    public boolean estaActivo() {
        LocalDate hoy = LocalDate.now();
        return (hoy.isEqual(fechaInicio) || hoy.isAfter(fechaInicio)) && 
               (hoy.isEqual(fechaFin) || hoy.isBefore(fechaFin));
    }

 // --- GETTERS Y SETTERS ---
    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }
    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
