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
    private Descuento descuentoAplicado;
    private double total;

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
    
    public int contarUnidadesDe(String idProductoBuscado) {
        int totalUnidades = 0;
        
        for (LineaCarrito linea : this.lineas) {
            // Comparamos el ID del producto de la línea con el que buscamos
            if (linea.getProducto().getId().equals(idProductoBuscado)) {
                totalUnidades += linea.getCantidad();
            }
        }
        
        return totalUnidades;
    }
    
    public double getPrecioDeProducto(String idProductoBuscado) {
        // Recorremos todas las líneas que el usuario ha añadido al carrito
        for (LineaCarrito linea : this.lineas) {
            
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
        for (LineaCarrito linea : this.lineas) {
            total += (linea.getCantidad()*(linea.getProducto().getPrecioOficial())); // Subtotal suele ser precio * cantidad
        }
        return total;
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
    
    public Descuento getDescuento(){
    	return this.descuentoAplicado;
    }
    
    public void setDescuento(Descuento d){
    	this.descuentoAplicado = d;
    }
    
    public double getTotal(){
    	return this.total;
    }
    public void setTotal(double t) {
    	this.total = t;
    }
  }