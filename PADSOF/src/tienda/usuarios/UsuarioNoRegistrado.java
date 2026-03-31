package usuarios;

import java.util.List;
import java.util.ArrayList;
import tienda.Estadistica;
import tienda.Tienda;
import tienda.FiltroVenta;
import productos.Producto2Mano;
import productos.ProductoVenta;

public class UsuarioNoRegistrado {
	protected String sessionId;

	public UsuarioNoRegistrado() {
		Estadistica est = Estadistica.getInstancia();
		this.sessionId = "INVITADO-" + String.valueOf(est.getnUsuarioNoRegistrado());
		est.setnUsuarioNoRegistrado(est.getnUsuarioNoRegistrado() + 1);
	}

	public List<ProductoVenta> buscarProductos() {
		return Tienda.getInstancia().buscarProductoVenta();
	}

	public List<ProductoVenta> buscarProductosPorNombre(String nombre) {
		return Tienda.getInstancia().buscarproductoPorNombre(nombre);
	}

	public ProductoVenta buscarProductoPorId(String id) {
		return Tienda.getInstancia().buscarProductoVentaPorId(id);
	}

	public List<ProductoVenta> buscarProductosPorCategoria(String nombreCategoria) {
		return Tienda.getInstancia().buscarProductoPorCategoria(nombreCategoria);
	}

	public List<Producto2Mano> buscarProductosSegundaMano() {
		return Tienda.getInstancia().buscarSegundaMano();
	}

	public Cliente registrarse(String nickname, String password, String dni) {
		return Tienda.getInstancia().registrarNuevoCliente(nickname, password, dni);
	}

	public String getSessionId() {
		return sessionId;
	}
}