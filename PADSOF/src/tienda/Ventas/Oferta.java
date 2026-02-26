package tienda.Ventas;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import tienda.Usuarios.Cliente;
import tienda.Productos.ProductoSegundaMano;

public class Oferta {
    private String id;
    private LocalDateTime fechaOferta;
    private EstadoOferta estado; 
    
    private Cliente origen; 
    private Cliente destino; 
    
    private List<ProductoSegundaMano> productosOfertados;
    private List<ProductoSegundaMano> productosSolicitados;

    public Oferta(Cliente origen, Cliente destino,List<ProductoSegundaMano> productosOfertados, List<ProductoSegundaMano> productosSolicitados ) {
        this.id = ---;
        this.fechaOferta = LocalDateTime.now();
        this.estado = EstadoOferta.PENDIENTE;
        this.origen = origen;
        this.destino = destino;
        this.productosOfertados = productosOfertados;
        this.productosSolicitados = productosSolicitados;
    }

    public void aceptarYEjecutar() {
        this.estado = EstadoOferta.ACEPTADA;

        for (ProductoSegundaMano p : this.productosOfertados) {
            p.setPropietario(this.destino);
            p.setBloqueado(false);
        }
        for (ProductoSegundaMano p : productosSolicitados) {
            p.setPropietario(this.origen);
            p.setBloqueado(false);
        }
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public LocalDateTime getFechaOferta() { return fechaOferta; }
    public EstadoOferta getEstado() { return estado; }
    public void setEstado(EstadoOferta estado) { this.estado = estado; }
    
    public List<ProductoSegundaMano> getProductosOfertados() { return productosOfertados; }
    public List<ProductoSegundaMano> getProductosSolicitados() { return productosSolicitados; }
}
