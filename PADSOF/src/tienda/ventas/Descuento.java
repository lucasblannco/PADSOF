package ventas;
import java.time.LocalDateTime;

public abstract class Descuento {
    protected String id;
    protected String nombre;
    protected LocalDateTime fechaInicio;
    protected LocalDateTime fechaFin;

    public Descuento(String id, String nombre, LocalDateTime inicio, LocalDateTime fin) {
        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    public boolean estaActivo() {
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin);
    }

    public abstract double calcularDescuento(Carrito carrito);


 // --- GETTERS Y SETTERS ---

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
}
