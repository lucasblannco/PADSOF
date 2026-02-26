package tienda.Usuarios;

import java.util.List;
import tienda.Productos.*;
import tienda.Ventas.*;
import java.util.ArrayList;

public class Cliente extends UsuarioRegistrado {
    private double saldoPuntos;
    private List<Pedido> historialPedidos;
    private Carrito carritoActual;
    private List<ProductoSegundaMano> carteraIntercambio;
    private List<Reseña> reseñas;
    protected List<Notificacion> notificaciones;

    public Cliente() {
        super();
        this.historialPedidos = new ArrayList<>();
        this.carteraIntercambio = new ArrayList<>();
        this.reseñas = new ArrayList<>();
    }

    @Override
    public void mostrarPanelPrincipal() {
    }

    public void escribirReseña(ProductoVenta p, int pts, String texto) {
        Reseña nueva = new Reseña(this, p, pts, texto);
        this.reseñas.add(nueva); // El cliente la guarda
        p.getReseñas().add(nueva);  // El producto también la recibe
    }
    
    	public List<ProductoSegundaMano>  getCarteraIntercambio(){
    		return this.carteraIntercambio;
    	}
    	public void addProducto2Mano (ProductoSegundaMano p) {
    		this.carteraIntercambio
    	}
}