package tienda;



import java.util.*;
import tienda.Usuarios.*;
import tienda.Productos.*;
import tienda.Intercambios.*;
import tienda.Ventas.*;

public class Tienda {
    private String nombre;
    private List<UsuarioRegistrado> usuarios;
    private List<ProductoVenta> stockNuevos;
    private List<Producto2Mano> catalogoIntercambio; 
    private List<Pedido> historialVentas;
    private List<Descuento> descuentosActivos = new ArrayList<>();
    private List<Oferta> intercambiosFinalizados = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();
    private Recomendador recomendador;
    //private List<Producto2Mano> pendientesTasacion = new ArrayList<>();

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
    
    //--- añadir cosas
    
    public Cliente registrarNuevoCliente(String nickname, String email, String password) {
        
        Cliente nuevoCliente = new Cliente();
      
        nuevoCliente.login(nickname, password, email);
        
   
        this.usuarios.add(nuevoCliente);
        
        nuevoCliente.recibirNotificacion("¡Bienvenido a CheckPoint, " + nickname + "!");
        return nuevoCliente;
    }
    
    public void registrarIntercambioFinalizado(Oferta oferta) {
        this.intercambiosFinalizados.add(oferta);
   
        this.catalogoIntercambio.removeAll(oferta.getProductosOfertados());
        this.catalogoIntercambio.removeAll(oferta.getProductosSolicitados());
    }
    public void solicitarTasacion(Producto2Mano p) {
        this.pendientesTasacion.add(p);
    }
    // - DESCUENTOS
    
    public void agregarDescuento(Descuento d) {
        this.descuentosActivos.add(d);
    }

    /*public double buscarDescuentoParaProducto(String idProducto) {
        for (Descuento d : descuentosActivos) {
            if (d.getIdProducto().equals(idProducto) && d.estaActivo()) {
                return d.getPorcentaje();
            }
        }
        return 0.0; // Sin descuento
    }*/
    
    // --- LÓGICA DE INTERCAMBIO 

    //añadir producto de segunda mano a la red global
    public void publicarParaIntercambio(ProductoSegundaMano p) {
        if (p.getValoracion() != null) {
            p.setBloqueado(false);
            this.catalogoIntercambio.add(p);
        }
    }

    //buscar productos de segunda mano, pero que no esten bloqueados
    public List<ProductoSegundaMano> buscarSegundaMano(String query) {
        List<ProductoSegundaMano> -ultados = new ArrayList<>();
        for (ProductoSegundaMano p : catalogoIntercambio) {
            // AHORA FILTRAMOS TAMBIÉN POR VISIBLE
            if (p.isVisible() && !p.isBloqueado() && p.getNombre().toLowerCase().contains(query.toLowerCase())) {
                resultados.add(p);
            }
        }
        return resultados;
    }
    
    METODO INCOMPLETO
    //suponemos que el orden de prioridad es segun se meten a la array
    public void aplicarDescuentoPrioritario(Carrito carrito) {
        
        List<Descuento> listaD = Tienda.getInstancia().getDescuentos()
;        
        for (Descuento d : listaD) {
           
            if (d.estaActivo()) {
                
               
                double ahorro = d.calcularDescuento(carrito);
                
                if (ahorro > 0) {
                   
                    carrito.setDescuento(d); 
                    carrito.setTotal(ahorro);
                    
                    System.out.println("Aplicado descuento: " + d.getNombre());
                    return; // Este 'return' es el que cumple la regla de "No acumulable"
                }
            }
        }
    }
    // --- GESTIÓN DE VENTAS NUEVAS 
    
    private List<Descuento> getDescuentos() {
		// TODO Auto-generated method stub
		return this.descuentosActivos;
	}

	public List<Producto2Mano> getProductosSinTasar(){
   
    }

    public void registrarVenta(Pedido pedido) {
        this.historialVentas.add(pedido);
    }

    // GETTERS
    public List<ProductoVenta> getStockNuevos() { return stockNuevos; }
    public List<UsuarioRegistrado> getUsuarios() { return usuarios; }
    public List<ProductoSegundaMano> getPendientesTasacion() {
        return pendientesTasacion;
    }
    public Recomendador getRecomendador() {
    	return this.recomendador;
    }
}
