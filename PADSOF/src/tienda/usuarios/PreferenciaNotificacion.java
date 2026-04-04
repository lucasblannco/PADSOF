package usuarios;

import java.security.Identity;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import productos.Categoria;

import tienda.Tienda;
import tienda.TipoNotificacion;

public class PreferenciaNotificacion {
	private boolean descuentos;
	private boolean pedidosCaducados;
	private boolean nuevos_Intercambios;
	private boolean pedido_entregado;
	private boolean valoracion_completada;
	private boolean oferta_caducada;
	private List<Categoria> categoriasInteres;

	public PreferenciaNotificacion() {
		this.descuentos = true;
		this.pedidosCaducados = true;
		this.nuevos_Intercambios = true;
		this.pedido_entregado = true;
		this.valoracion_completada = true;
		this.oferta_caducada = true; // Inicializamos las categorias de interes a nulll. El cliente las podra meter
		this.categoriasInteres = new ArrayList<>();
	}

	public boolean debeRecibirNotificacion(TipoNotificacion tipo) {
		switch (tipo) {
		// Obligatorias
		case CODIGO_RECOGIDA:
		case PEDIDO_LISTO:
		case OFERTA_RECIBIDA:
		case PAGO_EXITOSO:
		case Pago_FALLIDO:
		case CARRITO_CADUCADO:
		case OFERTA_RECHAZADA:
		case INTERCAMBIO_REALIZADO:
		case CATEGORIA_INTERES:
		case CONFIRMACION_RESERVA_CARRITO:
			return true;
	
		case EMPLEADOS://el cliente no recibe notificaciones de empleaos
			return false;
			// Configurables
		case DESCUENTO:
			return descuentos;
		case PEDIDO_CADUCADO:
			return pedidosCaducados;
		case PRODUCTO_INTERCAMBIO_NUEVO:
			return nuevos_Intercambios;
		case PEDIDO_ENTREGADO:
			return pedido_entregado;
		case VALORACION_COMPLETADA:
			return valoracion_completada;
		case OFERTA_CADUCADA:
			return oferta_caducada;
		default:
			return false;
		}
	}

	public void modificarPreferencia(TipoNotificacion tipo, boolean valor) {
		switch (tipo) {
		case DESCUENTO:
			this.descuentos = valor;
			break;
		case PEDIDO_CADUCADO:
			this.pedidosCaducados = valor;
			break;
		case PRODUCTO_INTERCAMBIO_NUEVO:
			this.nuevos_Intercambios = valor;
			break;
		case PEDIDO_ENTREGADO:
			this.pedido_entregado = valor;
			break;
		case VALORACION_COMPLETADA:
			this.valoracion_completada = valor;
			break;
		case OFERTA_CADUCADA:
			this.oferta_caducada = valor;
			break;
		default:
			System.out.println("Esta notificación es obligatoria y no se puede desactivar.");
			break;
		}
	}

	public boolean añadirCategoriaInteres(String nombreCategoria) {
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			System.out.println("El nombre de la categoria no puede estar vacio");
			return false;
		}
		Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (categoria == null) {
			System.out.println("No hay ninguna categoria llamada " + nombreCategoria + ".");
			return false;
		}
		categoriasInteres.add(categoria);
		System.out.println("Categoría '" + nombreCategoria + "' añadida a tus intereses.");
		return true;
	}

	public boolean NotificacionesProductosNUevosCategoriasInteres(String nombreCategoria) {
		if (categoriasInteres.isEmpty()) {
			return false;
		}
		Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (c == null) {
			return false;
		}
		return (this.categoriasInteres.contains(c));

	
}

	public boolean eliminarCategoriaInteres(String nombreCategoria) {
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			System.out.println("El nombre de la categoria no puede estar vacio");
			return false;
		}
		Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCategoria);
		if (categoria == null) {
			System.out.println("No hay ninguna categoria llamada " + nombreCategoria + ".");
			return false;
		}
		if (!categoriasInteres.contains(categoria)) {
			System.out.println(
					"La categoria con nombre " + nombreCategoria + " no estaba entre tus categorias de interes.");
			return false;
		}
		boolean quitar = categoriasInteres.remove(categoria);
		if (quitar) {
			System.out.println("Categoría '" + nombreCategoria + "' eliminada de tus intereses.");
		}
		return quitar;
	}

	public boolean isDescuentos() {
		return descuentos;
	}

	public void setDescuentos(boolean descuentos) {
		this.descuentos = descuentos;
	}

	public boolean isPedidosCaducados() {
		return pedidosCaducados;
	}

	public void setPedidosCaducados(boolean pedidosCaducados) {
		this.pedidosCaducados = pedidosCaducados;
	}

	public boolean isNuevos_Intercambios() {
		return nuevos_Intercambios;
	}

	public void setNuevos_Intercambios(boolean nuevos_Intercambios) {
		this.nuevos_Intercambios = nuevos_Intercambios;
	}

	public boolean isPedido_entregado() {
		return pedido_entregado;
	}

	public void setPedido_entregado(boolean pedido_entregado) {
		this.pedido_entregado = pedido_entregado;
	}

	public boolean isValoracion_completada() {
		return valoracion_completada;
	}

	public void setValoracion_completada(boolean valoracion_completada) {
		this.valoracion_completada = valoracion_completada;
	}

	public boolean isOferta_caducada() {
		return oferta_caducada;
	}

	public void setOferta_caducada(boolean oferta_caducada) {
		this.oferta_caducada = oferta_caducada;
	}

	public List<Categoria> getCategoriasInteres() {
		return categoriasInteres;
	}

	public void setCategoriasInteres(List<Categoria> categoriasInteres) {
		this.categoriasInteres = categoriasInteres;
	}
	 @Override
	    public String toString() {
		 ArrayList<String> nombresCats = new ArrayList<>();
		 for (Categoria c : categoriasInteres) {
		     nombresCats.add(c.getNombre());
		 }

		 //  Los unimos con una coma y un espacio
		 String cats = String.join(", ", nombresCats);

		 return 
		     "Descuentos: " + (descuentos ? "Activado" : "Desactivado") + "\n"
		     + "Pedidos caducados: " + (pedidosCaducados ? "Activado" : "Desactivado") + "\n"
		     + "Nuevos intercambios: " + (nuevos_Intercambios ? "Activado" : "Desactivado") + "\n"
		     + "Pedido entregado: " + (pedido_entregado ? "Activado" : "Desactivado") + "\n"
		     + "Valoración completada: " + (valoracion_completada ? "Activado" : "Desactivado") + "\n"
		     + "Oferta caducada: " + (oferta_caducada ? "Activado" : "Desactivado") + "\n"
		     + "Categorías de interés sobre las que recibir informacion respecto a los productos: " 
		     + (cats.isEmpty() ? "Ninguna" : cats);}

}
