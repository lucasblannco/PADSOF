package ventas;
import java.time.LocalDateTime;

public abstract class Descuento {
    protected String id;
    protected String nombre;
    protected LocalDateTime fechaInicio;
    protected LocalDateTime fechaFin;
    protected int ordenPrioridad; // El gestor decide el orden ??

    public Descuento(String id, String nombre, LocalDateTime inicio, LocalDateTime fin, int prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        this.ordenPrioridad = prioridad;
    }

    // Método clave: ¿Está el descuento vigente hoy?
    public boolean estaActivo() {
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin);
    }

    // Método abstracto que cada hijo implementará a su manera
    public abstract double calcularDescuento(Carrito carrito);
}

 // --- GETTERS Y SETTERS ---

    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
}
