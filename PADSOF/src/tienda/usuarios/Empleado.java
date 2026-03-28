package usuarios;

import tienda.*;
import productos.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import intercambios.*;

import ventas.*;

import java.util.*;

public class Empleado extends UsuarioRegistrado {

	protected List<Notificacion> notificaciones;
	private Set<TipoPermisos> permisos;
	private boolean despedido;
	private List<Valoracion> valoraciones;
	/*
	 * public Empleado(String nickname, String password, String email) {
	 * super(nickname, password, email); this.valoraciones = new ArrayList<>();
	 * this.permisos = new TreeSet<>(); }
	 */

	public Empleado(String nickname, String password) {
		super(nickname, password);
		this.valoraciones = new ArrayList<>();
		this.permisos = new TreeSet<>();
		this.despedido = false;
	}

	@Override
	public void mostrarPanelPrincipal() {
	}

	private boolean puedeRealizarTarea(TipoPermisos permiso) {
		if (this.despedido) {
			System.out.println("Este empleado está dado de baja y no puede realizar acciones.");
			return false;
		}
		if (!this.tienePermiso(permiso)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene el permiso " + permiso);
			return false;
		}
		return true;
	}

	private Producto2Mano buscarProductoPendientePorId(String idProducto) {
	    if (idProducto == null || idProducto.isBlank()) {
	        System.out.println("El id del producto no puede estar vacío.");
	        return null;
	    }
	    for (Producto2Mano p : Tienda.getInstancia().getPendientes_Tasacion()) {
	        if (p.getId().equals(idProducto)) return p;
	    }
	    System.out.println("No existe ningún producto pendiente de tasación con id: " + idProducto);
	    return null;
	}
	
	private Pedido buscarPedidoPorId(String idPedido) {
	    if (idPedido == null || idPedido.isBlank()) {
	        System.out.println("El id del pedido no puede estar vacío.");
	        return null;
	    }
	    for (Pedido p : Tienda.getInstancia().getHistorialVentas()) {
	        if (p.getIdPedido().equals(idPedido)) return p;
	    }
	    System.out.println("No existe ningún pedido con id: " + idPedido);
	    return null;
	}
	
	// si un producto no es aceptado, como borramos ese producto? habria que hacer
	// una funcion en tienda.
	public void tasarProducto(String idProducto, double precio, EstadoProducto estado) {
	    if (!puedeRealizarTarea(TipoPermisos.VALORACION_PRODUCTOS)) return;
	    
	    // Comprobamos que el producto esté pendiente de tasación
	    Producto2Mano p = buscarProductoPendientePorId(idProducto);
	    if (p == null) {
	        System.out.println("El producto " + idProducto + " no está pendiente de tasación.");
	        return;
	    }
	    
	    Tienda.getInstancia().getPendientes_Tasacion().remove(p);
	    
	    if (estado == EstadoProducto.NO_ACEPTADO) {
	        p.getPropietario().recibirNotificacion("El producto " + p.getNombre()
	                + " ha sido rechazado al no cumplir las expectativas suficientes.");
	        return;
	    }
	    
	    Valoracion nuevaVal = new Valoracion(precio, estado, this);
	    p.setValoracion(nuevaVal);
	    p.setVisible(true);
	    this.valoraciones.add(nuevaVal);
	    Tienda.getInstancia().publicarParaIntercambio(p);
	    p.getPropietario().recibirNotificacion("El producto " + p.getNombre()
	            + " ha sido tasado y publicado con éxito.");
	    this.recibirNotificacion("Has completado la valoración del producto "
	            + p.getNombre() + " con éxito.");
	}

	public boolean confirmarIntercambio(Oferta o) {
		if (!puedeRealizarTarea(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			System.out.println(
					"El empleado " + this.getNickname() + "no tiene permisos paara hacer confirmacion de intercambios");
			return false;
		}
		if (o.getEstado() != EstadoOferta.ACEPTADA) {
			this.recibirNotificacion("La oferta no ha sido aceptada por ambos usuarios por lo que no se puede aceptar");
		}
		o.aceptarYEjecutar();
		return true;
	}
//Hay que ver la cantidad supongio que habra una funcion de que si contine sacarlo rapido y ahi modificas la cantidad

	/// Funcion para añadir un nuevo producto a la tienda
	public boolean añadirProducto_nuevo(String letra, String nombre, String descripcion, String imagen,
			double precioOficial, int Stock, ArrayList<Categoria> categorias, int numpaginas, String editorial,
			int añoPublicacion, double altura, double ancho, double largo, String material, String marca,
			int minNumjugadores, int maxNumjugadores, int minEdad, int maxEdad, String Estilo) {

		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {
			System.out.println("No tienes permiso para trabajar con productos");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();

		// 1. Validar atributos básicos
		if (nombre == null || precioOficial <= 0 || Stock <= 0 || descripcion == null || imagen == null) {
			System.out.println("Los atributos de producto deben aparecer correctamente");
			return false;
		}
		if (categorias == null) {
			return false;
		}
		boolean flag = true;

		for (Categoria c : categorias) {
			if (!tienda.getCategorias().contains(c)) {
				flag = false;
				break;
			}
		}

		if (!flag) {
			System.out.println("Las categorias que se introduzcan deben existir en la tienda");
			return false;
		}

		// 2. Validar letra ANTES de comprobar existencia
		if (letra == null || letra.length() != 1) {
			this.recibirNotificacion(
					"El tipo de producto que has intentado crear no es correcta. Deben ser Comics(C), Figuras(F) o Juegos(J)");
			return false;
		}

		switch (letra.toUpperCase()) {
		case "C":
			if (numpaginas <= 0 || editorial == null || añoPublicacion <= 0) {
				System.out.println("Estas añadiendo un comic, los atributos deben cumplir las condiciones necesarias");
				return false;
			}
			ProductoVenta comic = new Comic(nombre, descripcion, imagen, precioOficial, Stock, numpaginas, editorial,
					añoPublicacion);
			tienda.añadirProducto(comic);
			for (Categoria cats : categorias) {

				cats.addProducto(comic);

			}
			this.recibirNotificacion("Has añadido el comic " + comic.getNombre() + " a la tienda");
			return true;

		case "J":
			if (minEdad <= 0) {
				System.out.println("La edad minima del juego tiene que ser mayor que 0");
				return false;
			}
			if (maxEdad <= 0 || maxEdad > 100) {
				System.out.println("La edad maxima del juego debe estar entre 1 y 100 años");
				return false;
			}
			if (minNumjugadores <= 0) {
				System.out.println("El juego tendrá mínimo 1 jugador");
				return false;
			}
			if (maxNumjugadores <= 0) {
				System.out.println("El juego debe tener por lo menos un jugador");
				return false;
			}
			ProductoVenta juego = new JuegoMesa(nombre, descripcion, imagen, precioOficial, Stock, minNumjugadores,
					maxNumjugadores, minEdad, maxEdad, Estilo);
			tienda.añadirProducto(juego);
			for (Categoria cats : categorias) {

				cats.addProducto(juego);

			}
			this.recibirNotificacion("Has añadido el juego " + juego.getNombre() + " a la tienda");
			return true; // <-- faltaba el return Y el break

		case "F":
			if (altura <= 0 || ancho <= 0 || largo <= 0) {
				System.out.println("Las dimensiones deben ser positivas");
				return false;
			}
			if (material == null) {
				System.out.println("Las figuras deben tener material");
				return false; // <-- faltaba el return
			}
			if (marca == null) {
				System.out.println("Las figuras deben tener marca");
				return false; // <-- faltaba el return
			}
			ProductoVenta figura = new Figura(nombre, descripcion, imagen, precioOficial, Stock, altura, ancho, largo,
					material, marca);
			tienda.añadirProducto(figura);
			for (Categoria cats : categorias) {

				cats.addProducto(figura);

			}
			this.recibirNotificacion("Has añadido la figura " + figura.getNombre() + " a la tienda");
			return true;

		default:
			this.recibirNotificacion(
					"El tipo de producto que has intentado crear no es correcta. Deben ser Comics(C), Figuras(F) o Juegos(J)");
			return false; // <-- cambiado de throw a return false, más consistente con el resto
		}
	}

	public boolean añadirUnidadesProductoExistente(String id, int cantidad) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {
			System.out.println("No tienes permiso para trabajar con productos");
			return false;
		}
		if (cantidad <= 0) {
			System.out.println("La cantidad a añadir tiene que ser mayor que 0");
			return false;
		}

		Tienda tienda = Tienda.getInstancia();
		for (ProductoVenta p : tienda.getStockVentas()) {
			if (p.getId().equals(id)) {
				// Caso en el que hayamos encontrado un producto que ya exista
				int unidades = p.getStockDisponible();
				unidades = unidades + cantidad;
				p.setStockDisponible(unidades);
				System.out.println("Se han añadiendo las unidades correctamente. Ahora el producto " + p.getId()
						+ " tiene " + p.getStockDisponible() + ".");
				return true;
			}
		}
		System.out.println("Este producto no existe en la lista de productos de venta de la tienda");
		return false;
	}

	public boolean prepararPedido(String idPedido) {
	    if (!puedeRealizarTarea(TipoPermisos.GESTION_PEDIDOS)) return false;
	    
	    Pedido ped = buscarPedidoPorId(idPedido);
	    if (ped == null) return false;
	    
	    if (ped.getEstado() != EstadoPedido.PAGADO) {
	        System.out.println("El pedido " + idPedido + " no se ha podido preparar.");
	        return false;
	    }
	    
	    boolean ok = ped.marcarPreparado();
	    if (ok) {
	        ped.getCliente().recibirNotificacion("Tu pedido con codigo de recogida "
	                + ped.getCodigoRecogida() + " está preparado. Puedes recogerlo.");
	    }
	    return ok;
	}

	public boolean entregarPedido(String codigoRecogida) {

		if (!puedeRealizarTarea(TipoPermisos.ENTREGA_PEDIDOS)) {
			System.out.println("No tienes permiso para entregar con pedidos");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		for (Pedido ped : tienda.getHistorialVentas()) {
			if (ped.getCodigoRecogida().equals(codigoRecogida) && (ped.getEstado() == EstadoPedido.LISTO_PARA_RECOGER)
					&& ped.isRecogida_solicitada()) {
				ped.marcarEntregado();
				ped.getCliente().recibirNotificacion(
						"Tu pedido con codigo de recogida " + ped.getCodigoRecogida() + " ha sido entregado con exito");
				this.recibirNotificacion("Has entregado corrrectamente el pedido con codigo de recogida"
						+ ped.getCodigoRecogida() + " al cliente " + ped.getCliente().getNickname() + ".");
				return true;
			}
		}
		System.out.println("No se ha podido entregar el pedido correctamente");
		return false;
	}

	public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
	    if (idProducto == null || nombreCat == null) {
	        System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
	        return false;
	    }
	    if (!puedeRealizarTarea(TipoPermisos.GESTION_CATEGORIAS)) {
	        System.out.println("El empleado " + this.getNickname() + " no tiene el permiso de gestion de categorias.");
	        return false;
	    }
	    Tienda tienda = Tienda.getInstancia();
	    
	    ProductoVenta p = tienda.buscarProductoVentaPorId(idProducto);
	    if (p == null) {
	        System.out.println("No existe ningún producto con id: " + idProducto);
	        return false;
	    }
	    Categoria c = tienda.buscarCategoriaPorNombre(nombreCat);
	    if (c == null) {
	        System.out.println("No existe ninguna categoría con nombre: " + nombreCat);
	        return false;
	    }
	    return c.addProducto(p);
	}

	public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
	    if (idProducto == null || nombreCat == null) {
	        System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
	        return false;
	    }
	    if (!puedeRealizarTarea(TipoPermisos.GESTION_CATEGORIAS)) {
	        System.out.println("El empleado " + this.getNickname() + " no tiene el permiso de gestion de categorias.");
	        return false;
	    }
	    Tienda tienda = Tienda.getInstancia();
	    
	    ProductoVenta p = tienda.buscarProductoVentaPorId(idProducto);
	    if (p == null) {
	        System.out.println("No existe ningún producto con id: " + idProducto);
	        return false;
	    }
	    Categoria c = tienda.buscarCategoriaPorNombre(nombreCat);
	    if (c == null) {
	        System.out.println("No existe ninguna categoría con nombre: " + nombreCat);
	        return false;
	    }
	    return c.deleteProducto(p);
	}

	public boolean crearPack(String nombre, String descripcion, String imagen, double precioOficial, int stock,
			ArrayList<ProductoVenta> productos) {

		if (!this.getPermisos().contains(TipoPermisos.GESTION_PACKS)) {
			System.out.println("No tienes permiso para gestionar packs");
			return false;
		}

		if (nombre == null || descripcion == null || imagen == null) {
			System.out.println("El nombre, descripción e imagen no pueden ser null");
			return false;
		}

		if (productos == null || productos.size() <= 1) {
			System.out.println("El pack debe tener al menos dos producto");
			return false;
		}

		if (stock <= 0) {
			System.out.println("El stock debe ser mayor que 0");
			return false;
		}

		Tienda tienda = Tienda.getInstancia();

		// Comprobar que todos los productos existen en la tienda
		for (ProductoVenta p : productos) {
			if (p == null || !tienda.getStockVentas().contains(p)) {
				System.out.println("Algún producto de los que has solicitado no existe en la tienda");
				return false;
			}
		}
		// comprobamos que el numero de unidades de cada pack comprovbamos que haya
		// suficientes
		int stockMinimo = Integer.MAX_VALUE;
		for (ProductoVenta p : productos) {
			if (p.getStockDisponible() < stockMinimo) {
				stockMinimo = p.getStockDisponible();
			}
		}

		if (stock > stockMinimo) {
			System.out.println("El stock del pack no puede superar el mínimo de sus productos (" + stockMinimo + ")");
			return false;
		}

		double suma = 0;
		for (ProductoVenta pv : productos) {
			suma += pv.getPrecioVenta();
		}

		if (precioOficial >= suma - 1) {
			System.out.println(
					"El precio del pack tiene que ser menor al menos un eruro que la suma de los productos que contenga");
		}
		Pack pack = new Pack(nombre, descripcion, imagen, precioOficial, stock, productos);

		pack.setStockDisponible(stock);

		for (ProductoVenta pr : productos) {
			int unidades = pr.getStockDisponible();
			pr.setStockDisponible(unidades - stock);
		}
		tienda.añadirProducto(pack);
		this.recibirNotificacion("Has creado el pack " + nombre + " correctamente con el id " + pack.getId());
		return true;
	}

	private void modificarProductosPackCreado() {

	}

	public boolean modificarDescripcionProducto(String idProducto, String descripcion) {
	    if (idProducto == null || descripcion == null) {
	        System.out.println("El id del producto o la descripción no pueden ser null.");
	        return false;
	    }
	    if (!puedeRealizarTarea(TipoPermisos.MODIFICAR_PRODUCTO)) return false;
	    
	    ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
	    if (p == null) return false;
	    
	    p.setDescripcion(descripcion);
	    System.out.println("Descripción del producto " + idProducto + " modificada correctamente.");
	    return true;
	}

	public boolean modificarImagenProducto(String idProducto, String imagen) {
	    if (idProducto == null || imagen == null) {
	        System.out.println("El id del producto o la imagen no pueden ser null.");
	        return false;
	    }
	    if (!puedeRealizarTarea(TipoPermisos.MODIFICAR_PRODUCTO)) return false;
	    
	    ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
	    if (p == null) return false;
	    
	    p.setImagenRuta(imagen);
	    System.out.println("Imagen del producto " + idProducto + " modificada correctamente.");
	    return true;
	}
	
	@Override
	public boolean login(String nickname, String password) {
	    if (this.despedido) {
	        System.out.println("Este empleado está dado de baja y no puede iniciar sesión.");
	        return false;
	    }
	    return super.login(nickname, password);
	}
	public void asignarPermiso(TipoPermisos p) {
		this.permisos.add(p);
	}

	public void quitarPermiso(TipoPermisos p) {
		this.permisos.remove(p);
	}

	public boolean tienePermiso(TipoPermisos p) {
		return this.permisos.contains(p);
	}

	public void recibirNotificacion(String mensaje) {
		if (this.notificaciones == null) {
			this.notificaciones = new ArrayList<>();
		}
		// Si aún no has creado la clase Notificacion, puedes pasarle un String
		// o crear el objeto aquí mismo si ya la tienes.
		this.notificaciones.add(new Notificacion(mensaje));
		System.out.println("[Notificación Empleado]: " + mensaje);
	}

	/*
	 * public void asignarTodosLosPermisos() { this.permisos =
	 * EnumSet.allOf(Permiso.class); }
	 */
	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(List<Notificacion> notificaciones) {
		this.notificaciones = notificaciones;
	}

	public Set<TipoPermisos> getPermisos() {
		return permisos;
	}

	public void setPermisos(Set<TipoPermisos> permisos) {
		this.permisos = permisos;
	}

	public List<Valoracion> getValoraciones() {
		return valoraciones;
	}

	public void setValoraciones(List<Valoracion> valoraciones) {
		this.valoraciones = valoraciones;
	}

	public boolean isDespedido() {
		return despedido;
	}

	public void setDespedido(boolean despedido) {
		this.despedido = despedido;
	}

	@Override
	public String toString() {
		return "Empleado [id=" + getId() + ", nickname=" + getNickname() + "]";
	}
}
