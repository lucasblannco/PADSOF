


import java.util.*;
import tienda.Usuarios.*;
import tienda.Productos.*;
import tienda.Intercambios.*;
import tienda.Ventas.*;

public class Tienda {
    private String nombre;
    private List<UsuarioRegistrado> usuarios;
    private List<ProductoVenta> stockNuevos;
    private List<ProductoSegundaMano> catalogoIntercambio; 
    private List<Pedido> historialVentas;

    //esta variable estatica, el constructor privado y el segundo metodo
    //sirven para asegurar la existencia de una tienda unica y comun.
    
    private static Tienda instancia;

    private Tienda() {
        this.nombre = "CheckPoint";
        this.usuarios = new ArrayList<>();
        this.stockNuevos = new ArrayList<>();
        this.catalogoIntercambio = new ArrayList<>();
        this.historialVentas = new ArrayList<>();
    }

    public static Tienda getInstancia() {
        if (instancia == null) instancia = new Tienda();
        return instancia;
    }

    // --- LÓGICA DE INTERCAMBIO 

    //añadir producto de segunda mano a la red global
    public void publicarParaIntercambio(ProductoSegundaMano p) {
        if (p.getValoracion() != null) {
            p.setVisible(true);
            this.catalogoIntercambio.add(p);
        }
    }

    //buscar productos de segunda mano, pero que no esten bloqueados
    public List<ProductoSegundaMano> buscarSegundaMano(String query) {
        List<ProductoSegundaMano> resultados = new ArrayList<>();
        for (ProductoSegundaMano p : catalogoIntercambio) {
            if (p.getNombre().toLowerCase().contains(query.toLowerCase()) && !p.isBloqueado()) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    //al intercambiar, se eliminan los productos
    public void finalizarIntercambio(Oferta oferta) {
        this.catalogoIntercambio.removeAll(oferta.getProductosOfertados());
        this.catalogoIntercambio.removeAll(oferta.getProductosSolicitados());
    }

    // --- GESTIÓN DE VENTAS NUEVAS 

    public void registrarVenta(Pedido pedido) {
        this.historialVentas.add(pedido);
    }


    public List<ProductoVenta> getStockNuevos() { return stockNuevos; }
    public List<UsuarioRegistrado> getUsuarios() { return usuarios; }
}
