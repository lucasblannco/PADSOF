package tienda.Ventas;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Carrito {
    private String idCarrito;
   //private String idCliente; mejor  guardar solo el id si es queremos guardar algo, para evitar bucles infinitos
    private List<LineaCarrito> lineas;
    private LocalDateTime fechaCreacion;

    public Carrito(String idCliente) {
        this.idCarrito = "CART-" + System.currentTimeMillis();
        //this.idCliente = idCliente;
        this.lineas = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
    }

    public void añadirProducto(ProductoVenta p, int cant) {
    }

    public void eliminarProducto(String idProducto) {
    }

    public double calcularTotal() {
        return 0.0;
    }

    public boolean haExpirado(int minutosLimite) {
        return LocalDateTime.now().isAfter(fechaCreacion.plusMinutes(minutosLimite));
    }

    public List<LineaCarrito> getLineas() {
        return lineas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
