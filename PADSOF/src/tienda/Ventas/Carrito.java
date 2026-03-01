package tienda.Ventas;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.time.LocalDateTime;
import tienda.Productos.ProductoVenta;

public class Carrito {
    private String idCarrito;
    private List<LineaCarrito> lineas;
    private LocalDateTime fechaCreacion;

    public Carrito(String idCliente) {
        this.idCarrito = "CART-" + System.currentTimeMillis();
        this.lineas = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
    }

   
    public void añadirProducto(ProductoVenta p, int cant) {
        for (LineaCarrito linea : lineas) {
            if (linea.getProducto().getId().equals(p.getId())) {
                linea.setCantidad(linea.getCantidad() + cant);
                return;
            }
        }
        // Si no existe, creamos una nueva línea
        this.lineas.add(new LineaCarrito(p, cant));
    }

    
    public void eliminarProducto(String idProducto) {
        Iterator<LineaCarrito> it = lineas.iterator();
        while (it.hasNext()) {
            if (it.next().getProducto().getId().equals(idProducto)) {
                it.remove();
                break;
            }
        }
    }

   
    public double calcularTotal() {
        double total = 0.0;
        for (LineaCarrito linea : lineas) {
            total += linea.obtenerSubtotal();
        }
        return total;
    }

    public boolean haExpirado(int minutosLimite) {
        return LocalDateTime.now().isAfter(fechaCreacion.plusMinutes(minutosLimite));
    }

    // --- GETTERS Y SETTERS ---

    public String getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(String idCarrito) {
        this.idCarrito = idCarrito;
    }

    public List<LineaCarrito> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaCarrito> lineas) {
        this.lineas = lineas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}