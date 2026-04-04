package usuarios;

import java.util.List;

import productos.Categoria;
import productos.EstadoProducto;
import productos.Producto2Mano;
import productos.ProductoVenta;
import productos.Reseña;

import java.io.FileReader;
import java.lang.annotation.ElementType;
import java.security.PublicKey;
import java.util.ArrayList;

import tienda.*;

public abstract class UsuarioRegistrado {

	protected String id;
	protected String nickname;
	protected String password;
	protected boolean sesionIniciada;
	private FiltroVenta filtroVenta;
	private FiltroSegundaMano filtro2Mano;

	/*
	 * public UsuarioRegistrado(String nickname, String password, String email) {
	 * Estadistica est = Estadistica.getInstancia(); this.id = "USERREG-" +
	 * String.valueOf(est.getnUsuarioRegistrado()); this.nickname = nickname;
	 * this.password = password; this.email = email;
	 * est.setnUsuarioRegistrado(est.getnUsuarioRegistrado() + 1); }
	 */

	public UsuarioRegistrado(String nickname, String password) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "USERREG-" + String.valueOf(est.getnUsuarioRegistrado());
		this.nickname = nickname;
		this.password = password;
		est.setnUsuarioRegistrado(est.getnUsuarioRegistrado() + 1);
		this.sesionIniciada = false;
		this.filtro2Mano = new FiltroSegundaMano();
		this.filtroVenta = new FiltroVenta();
	}

	/*
	 * public List<ProductoVenta> navegarCatalogoNuevos() {
	 * System.out.println("Visitante " + sessionId +
	 * " consultando catálogo de productos nuevos."); return
	 * Tienda.getInstancia().getStockNuevos(); }
	 */
	public void logout() {
		this.sesionIniciada = false;
		this.filtroVenta = new FiltroVenta();
		this.filtro2Mano = new FiltroSegundaMano();
		Tienda.getInstancia().getUsuariosConSesionActiva().remove(this);
		System.out.println("El usuario con id: " + id + " ha cerrado sesion correctamente.");
	}

	public static boolean validarPassword(String pass) {
		if (pass == null || pass.length() < 8// longitud minimo 8
				|| !pass.matches(".*[A-Z].*")// busca al menos una letra mayuscula
				|| !pass.matches(".*[a-z].*")// busca al menos una letra minuscula
				|| !pass.matches(".*\\d.*")// busca al menos un digito numerico
				|| !pass.matches(".*[^a-zA-Z0-9].*")) {// buscamos al menos unn caracter especial{
			System.out.println("La contraseña debe tener al menos 8 caracteres, "
					+ "una mayúscula, una minúscula, un número y un carácter especial.");
			return false;
		}
		return true;
	}

	public boolean login(String password) {
		if (!this.password.equals(password)) {
			System.out.println("Contraseña Incorrecta.");
			return false;
		}
		this.sesionIniciada = true;
		Tienda.getInstancia().getUsuariosConSesionActiva().add(this);
		return true;
	}

	public List<Reseña> verReseñasProducto(ProductoVenta p) {
		if (p == null) {
			System.out.println("El producto no puede ser null.");
			return null;
		}
		if (p.getReseñas().isEmpty()) {
			System.out.println("El producto '" + p.getNombre() + "' no tiene reseñas.");
			return new ArrayList<>();
		}
		System.out.println(" Reseñas de '" + p.getNombre() + "' " + " | Media: "
				+ String.format("%.1f", p.getMediaPuntuacion()) + "/10");
		for (Reseña r : p.getReseñas()) {
			System.out.println("  " + r);
		}
		return p.getReseñas();
	}

	// busqueda
	public List<ProductoVenta> buscarProductos() {
		return Tienda.getInstancia().buscarProductoVenta();
	}

	public List<ProductoVenta> buscarProductosPorNombre(String nombre) {
		List<ProductoVenta> resultado = Tienda.getInstancia().buscarproductoPorNombre(nombre);
		if (resultado == null || resultado.isEmpty()) {
			System.out.println("  No se encontraron productos con el nombre '" + nombre + "'");
			return resultado;
		}
		System.out.println("  Resultados para '" + nombre + "' (" + resultado.size() + "):");
		for (ProductoVenta p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	public ProductoVenta buscarProductoPorId(String id) {
		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(id);
		if (p == null) {
			System.out.println("  No se encontro ningun producto con id '" + id + "'");
			return null;
		}
		System.out.println("  Producto encontrado:");
		System.out.println("  " + p.resumen());
		return p;
	}

	public List<ProductoVenta> buscarProductosPorCategoria(String nombreCategoria) {
		List<ProductoVenta> resultado = Tienda.getInstancia().buscarProductoPorCategoria(nombreCategoria);
		if (resultado == null || resultado.isEmpty()) {
			System.out.println("  No se encontraron productos en la categoria '" + nombreCategoria + "'");
			return resultado;
		}
		System.out.println("  Resultados categoria '" + nombreCategoria + "' (" + resultado.size() + "):");
		for (ProductoVenta p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	public List<ProductoVenta> buscarProductosVentaFiltrados() {
		List<ProductoVenta> resultado = Tienda.getInstancia().buscarProductosFiltrados(filtroVenta);
		if (resultado.isEmpty()) {
			System.out.println("  Ningun producto cumple el filtro: " + filtroVenta);
			return resultado;
		}
		for (ProductoVenta p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	public void filtrarPorPrecio(double min, double max) {
		filtroVenta.resetear();
		filtroVenta.setPrecioMinimo(min);
		filtroVenta.setPrecioMaximo(max);
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}

	public void filtrarPorCategoria(String nombreCategoria) {
		filtroVenta.resetear();
		Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (c == null) {
			System.out.println("  Categoria '" + nombreCategoria + "' no encontrada.");
			return;
		}
		filtroVenta.añadirCategoria(c);
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}

	public void filtrarPorPuntuacion(double puntuacionMinima) {
		filtroVenta.resetear();
		filtroVenta.setPuntuacionMinima(puntuacionMinima);
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}

	public void filtrarProductos(double precioMin, double precioMax, double puntuacionMin, String... categorias) {
		filtroVenta.resetear();
		filtroVenta.setPrecioMinimo(precioMin);
		filtroVenta.setPrecioMaximo(precioMax);
		filtroVenta.setPuntuacionMinima(puntuacionMin);
		for (String nombreCat : categorias) {
			Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCat);
			if (c != null)
				filtroVenta.añadirCategoria(c);
		}
		System.out.println("  Filtro aplicado: " + filtroVenta);
		buscarProductosVentaFiltrados();
		filtroVenta.resetear();
	}

	public List<Producto2Mano> buscarProductosSegundaMano() {
		List<Producto2Mano> resultado = Tienda.getInstancia().buscarSegundaMano();
		if (resultado.isEmpty()) {
			System.out.println("  No hay productos de segunda mano disponibles.");
			return resultado;
		}
		System.out.println("  Productos de segunda mano disponibles (" + resultado.size() + "):");
		for (Producto2Mano p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	public List<Producto2Mano> buscarProducto2ManoNombre(String nombre) {
		List<Producto2Mano> resultado = Tienda.getInstancia().buscarSegundaManoPorNombre(nombre);
		if (resultado == null || resultado.isEmpty()) {
			System.out.println("  No se encontraron productos de segunda mano con el nombre '" + nombre + "'");
			return resultado;
		}
		System.out.println("  Resultados para '" + nombre + "' (" + resultado.size() + "):");
		for (Producto2Mano p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	public Producto2Mano buscarProducto2ManoPorid(String id) {
		Producto2Mano p = Tienda.getInstancia().buscarSegundaManoPorId(id);
		if (p == null) {
			System.out.println("  No se encontro ningun producto de segunda mano con id '" + id + "'");
			return null;
		}
		System.out.println("  Producto encontrado:");
		System.out.println("  " + p.resumen());
		return p;
	}

	public List<Producto2Mano> buscarProductos2ManoFiltrados() {
		List<Producto2Mano> resultado = Tienda.getInstancia().buscarSegundaManoFiltrado(filtro2Mano);
		if (resultado.isEmpty()) {
			System.out.println("  Ningun producto de segunda mano cumple el filtro: " + filtro2Mano);
			return resultado;
		}
	
		for (Producto2Mano p : resultado) {
			System.out.println("  " + p.resumen());
		}
		return resultado;
	}

	public void filtrar2ManoPorValor(double min, double max) {
		filtro2Mano.resetear();
		filtro2Mano.setValorMinimo(min);
		filtro2Mano.setValorMaximo(max);
		System.out.println("  Filtro aplicado: " + filtro2Mano);
		buscarProductos2ManoFiltrados();
		filtro2Mano.resetear();
	}

	public void filtrar2ManoPorEstado(EstadoProducto estadoMinimo) {
		filtro2Mano.resetear();
		filtro2Mano.setEstadoMinimo(estadoMinimo);
		System.out.println("  Filtro aplicado: " + filtro2Mano);
		buscarProductos2ManoFiltrados();
		filtro2Mano.resetear();
	}

	public void filtrar2Mano(double min, double max, EstadoProducto estadoMinimo) {
		filtro2Mano.resetear();
		filtro2Mano.setValorMinimo(min);
		filtro2Mano.setValorMaximo(max);
		filtro2Mano.setEstadoMinimo(estadoMinimo);
		System.out.println("  Filtro aplicado: " + filtro2Mano);
		buscarProductos2ManoFiltrados();
		filtro2Mano.resetear();
	}
	//ver la cartera de un clinte
	public List<Producto2Mano> verCarteraCliente(String nickname) {
	    if (nickname == null || nickname.isBlank()) {
	        System.out.println("  El nickname no puede estar vacio.");
	        return null;
	    }
	    Cliente c = Tienda.getInstancia().buscarClientePorNickname(nickname);
	    if (c == null) {
	    	
	        return new ArrayList<>();
	       
	    }
	    if (c.getNickname().equalsIgnoreCase(this.nickname)) {
	        System.err.println("  Para ver tu propia cartera usa verMiCartera().");
	        return new ArrayList<>();
	    }
	    List<Producto2Mano> resultado = new ArrayList<>();
	    for (Producto2Mano p : c.getCarteraIntercambio()) {
	        if (p.isVisible()) {
	            resultado.add(p);
	        }
	    }
	    if (resultado.isEmpty()) {
	        System.out.println("  " + nickname + " no tiene productos visibles en su cartera.");
	        return resultado;
	    }
	    System.out.println("  Cartera visible de " + nickname + " (" + resultado.size() + " productos):");
	    for (Producto2Mano p : resultado) {
	        System.out.println("  " + p.resumen());
	    }
	    return resultado;
	}

//Getters públicos: Todos necesitan saber quién es quién [cite: 325]
	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	protected void setNickname(String nickname) {
		this.nickname = nickname;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return this.id;
	}

	public boolean isSesionIniciada() {
		return sesionIniciada;
	}

	public void setSesionIniciada(boolean sesionIniciada) {
		this.sesionIniciada = sesionIniciada;
	}

	public void mostrarPanelPrincipal() {
		return;

	}

}