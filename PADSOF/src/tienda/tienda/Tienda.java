package tienda;

import java.util.*;

import intercambios.Oferta;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;
import usuarios.UsuarioRegistrado;
import ventas.Carrito;
import ventas.Descuento;
import ventas.Pedido;
import ventas.Regalo;
import productos.*;

public class Tienda {
	private String nombre;
	private List<UsuarioRegistrado> usuarios;
	private List<ProductoVenta> stockVentas;
	private List<Producto2Mano> catalogoIntercambio;
	private List<Pedido> historialVentas;
	private List<Producto2Mano> pendientes_Tasacion; // Productos que no han sido tasados
	private List<Descuento> descuentosActivos = new ArrayList<>();
	private List<Descuento> historialDescuentos = new ArrayList<>();
	private List<Oferta> intercambiosFinalizados = new ArrayList<>();
	private List<Categoria> categorias = new ArrayList<>();
	private Recomendador recomendador;
	private int tiempoMaxCarrito;
	private int tiempoMaxOferta;
	private int tiempoMaxPago;
	private double precioValoracion;
	private List<UsuarioRegistrado> usuariosConSesionActiva = new ArrayList<>();
	private List<Notificacion> historialNotificaciones = new ArrayList<>();

	// esta variable estatica, el constructor privado y el segundo metodo
	// sirven para asegurar la existencia de una tienda unica y comun.
	private static Tienda instancia;

	private Tienda() {
		this.nombre = "CheckPoint";
		this.usuarios = new ArrayList<>();
		this.stockVentas = new ArrayList<>();
		this.catalogoIntercambio = new ArrayList<>();
		this.historialVentas = new ArrayList<>();
		this.pendientes_Tasacion = new ArrayList<>();
		this.descuentosActivos = new ArrayList<>();
		this.historialDescuentos = new ArrayList<>();
		this.intercambiosFinalizados = new ArrayList<>();
		this.categorias = new ArrayList<>();
		this.recomendador = new Recomendador();
		this.tiempoMaxCarrito = 0;
		this.tiempoMaxOferta = 0;
		this.tiempoMaxPago = 0;
		this.precioValoracion = 10;
		// El gestor es el primer usuario del sistema, siempre tendrá id USR-1
		Gestor gestor = new Gestor();
		this.usuarios.add(gestor);
		this.usuariosConSesionActiva = new ArrayList<>();
		this.usuariosConSesionActiva.add(gestor);
		this.historialNotificaciones = new ArrayList<>();
	}

	public static Tienda getInstancia() {
		if (instancia == null)
			instancia = new Tienda();
		return instancia;
	}

	// METODOS GLOBALES
	// --- añadir cosas

	public Gestor getGestor() {
		if (usuarios != null && !usuarios.isEmpty()) {
			UsuarioRegistrado u = usuarios.get(0);
			if (u instanceof Gestor) {
				return (Gestor) u;
			}
		}
		System.err.println("Error: No se ha encontrado al Gestor en el sistema.");
		return null;
	}

	public boolean existeUsuarioConNickname(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			System.out.println("El nickname no puede estar vacio.");
			return false;
		}
		for (UsuarioRegistrado u : usuarios) {
			if (u.getNickname().equalsIgnoreCase(nickname)) {
				return true;
			}
		}
		return false;
	}

	public Cliente buscarCLientePorId(String id) {
		if (id == null || id.isBlank()) {
			System.out.println("El id no puede estar vacio");
			return null;
		}
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente && u.getId().equals(id)) {
				return (Cliente) u;
			}
		}
		System.out.println("No existe ningún cliente con id: " + id);
		return null;
	}

	public Cliente buscarClientePorNickname(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			return null;
		}
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente && u.getNickname().equalsIgnoreCase(nickname)) {
				return (Cliente) u;
			}
		}
		System.out.println("No existe ningún cliente con nickname: " + nickname);
		return null;
	}

	public boolean existeUsuarioConDNI(String dni) {
		if (dni == null || dni.isBlank())
			return false;
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente) {
				Cliente c = (Cliente) u;
				if (c.getDni().equalsIgnoreCase(dni)) {
					return true;
				}
			}
		}
		return false;
	}

	// FUNCIONES DE BUSQUEDA.
	public List<ProductoVenta> buscarProductoVenta() {
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta p : stockVentas) {
			if (p.getStockDisponible() > 0) {
				productos.add(p);
			}
		}
		return productos;
	}

	// Busca por id iterando la lista — robusto aunque haya huecos en los indices
	public ProductoVenta buscarProductoVentaPorId(String idProducto) {
		if (idProducto == null || idProducto.isBlank())
			return null;
		for (ProductoVenta p : stockVentas) {
			if (p.getId().equals(idProducto))
				return p;
		}
		return null;
	}

	public Categoria buscarCategoriaPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank())
			return null;
		for (Categoria c : this.categorias) {
			if (c.getNombre().equalsIgnoreCase(nombre))
				return c;
		}
		System.out.println("No existe ninguna categoria en la tienda con el nombre " + nombre + ".");
		return null;
	}

	public List<ProductoVenta> buscarproductoPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre no puede estar vacio.");
			return null;
		}
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta p : stockVentas) {
			if (p.getStockDisponible() > 0 && p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
				productos.add(p);
			}
		}
		return productos;
	}

	public List<ProductoVenta> buscarProductoPorCategoria(String nombreCategoria) {
		Categoria cat = buscarCategoriaPorNombre(nombreCategoria);
		if (cat == null) {
			return new ArrayList<>();
		}
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta productoVenta : cat.getProductos()) {
			productos.add(productoVenta);
		}
		return productos;
	}

	// BuscarSegundaMano
	public List<Producto2Mano> buscarSegundaMano() {
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : catalogoIntercambio) {
			if (p.isVisible() && !p.isBloqueado())
				resultado.add(p);
		}
		return resultado;
	}

	public List<Producto2Mano> buscarSegundaManoPorNombre(String nombre) {
		if (nombre == null || nombre.isBlank())
			return new ArrayList<>();
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : catalogoIntercambio) {
			if (p.isVisible() && !p.isBloqueado() && p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
				resultado.add(p);
			}
		}
		return resultado;
	}

	// Busca por id iterando la lista — robusto aunque haya huecos en los indices
	public Producto2Mano buscarSegundaManoPorId(String id) {
		if (id == null || id.isBlank())
			return null;
		for (Producto2Mano p : catalogoIntercambio) {
			if (p.getId().equals(id))
				return p;
		}
		return null;
	}

	// BuscarConFiltros
	public List<ProductoVenta> buscarProductosFiltrados(FiltroVenta filtro) {
		List<ProductoVenta> productos = new ArrayList<>();
		for (ProductoVenta productoVenta : stockVentas) {
			if (productoVenta.getStockDisponible() > 0 && filtro.productoCumpleFiltro(productoVenta)) {
				productos.add(productoVenta);
			}
		}
		return productos;
	}

	public List<Producto2Mano> buscarSegundaManoFiltrado(FiltroSegundaMano filtro) {
		List<Producto2Mano> resultado = new ArrayList<>();
		for (Producto2Mano p : catalogoIntercambio) {
			if (filtro.cumpleFiltro(p)) {
				resultado.add(p);
			}
		}
		return resultado;
	}

	public UsuarioRegistrado login(String nickname, String password, String tipo) {
		for (UsuarioRegistrado u : usuarios) {
			if (u.getNickname().equals(nickname)) {
				switch (tipo.toUpperCase()) {
				case "EMPLEADO":
					if (u instanceof Empleado) {
						if (((Empleado) u).isDespedido()) {
							System.out.println("Este empleado está dado de baja.");
							return null;
						}
						return u.login(password) ? u : null;
					}
					break;
				case "CLIENTE":
					if (u instanceof Cliente) {
						return u.login(password) ? u : null;
					}
					break;
				case "GESTOR":
					if (u instanceof Gestor) {
						return u.login(password) ? u : null;
					}
					break;
				default:
					System.out.println("Tipo de usuario no reconocido.");
					return null;
				}
			}
		}
		System.out.println("Usuario no encontrado o tipo incorrecto.");
		return null;
	}

	// metodos para evitar casteos en el main
	public Gestor loginGestor(String nickname, String password) {
		UsuarioRegistrado u = login(nickname, password, "GESTOR");
		if (u instanceof Gestor) {
			return (Gestor) u;
		}
		return null;
	}

	public Empleado loginEmpleado(String nickname, String password) {
		UsuarioRegistrado u = login(nickname, password, "EMPLEADO");
		if (u instanceof Empleado) {
			return (Empleado) u;
		}
		return null;
	}

	public Cliente loginCliente(String nickname, String password) {
		UsuarioRegistrado u = login(nickname, password, "CLIENTE");
		if (u instanceof Cliente) {
			return (Cliente) u;
		}
		return null;
	}

	// Notificaciones
	public void registrarNotificacion(Notificacion n) {
		historialNotificaciones.add(n);
	}

	public List<Notificacion> getNotificacionesNoLeidas() {
		List<Notificacion> resultado = new ArrayList<>();
		for (Notificacion n : historialNotificaciones) {
			if (!n.isLeida())
				resultado.add(n);
		}
		return resultado;
	}

	public List<Notificacion> getNotificacionesPorTipo(TipoNotificacion tipo) {
		List<Notificacion> resultado = new ArrayList<>();
		for (Notificacion n : historialNotificaciones) {
			if (n.getTipo() == tipo)
				resultado.add(n);
		}
		return resultado;
	}

	// Bug corregido: getPreferencias() -> verPreferencias()
	public void notificarDescuento(Descuento d) {
		for (Cliente c : obtenerClientesTienda()) {
			if (c.getPreferencias().debeRecibirNotificacion(TipoNotificacion.DESCUENTO)) {
				c.recibirNotificacionTipo("Nuevo descuento disponible " + d.getNombre(), TipoNotificacion.DESCUENTO);
			}
		}
	}

	// Bug corregido: recibirNotificacion() no existe en Cliente ->
	// usar recibirNotificacionTipo con tipo obligatorio CONFIRMACION_RESERVA_CARRITO
	public Cliente registrarNuevoCliente(String nickname, String password, String dni) {
		if (!dni.matches("\\d{8}[A-Za-z]")) {
			System.out.println("El DNI no tiene el formato correcto (8 dígitos y 1 letra).");
			return null;
		}
		if (this.existeUsuarioConDNI(dni)) {
			System.out.println("Error: Ya existe un cliente registrado con el DNI: " + dni);
			return null;
		}
		if (this.existeUsuarioConNickname(nickname)) {
			System.out.println("Ya existe un usuario con el nickname: " + nickname);
			return null;
		}
		if (!UsuarioRegistrado.validarPassword(password))
			return null;

		Cliente nuevo = new Cliente(nickname, password, dni);
		this.usuarios.add(nuevo);
		nuevo.recibirNotificacionTipo("¡Bienvenido a CheckPoint, " + nickname
				+ "! Te has registrado correctamente, ahora podrás consultar nuestra tienda.",
				TipoNotificacion.CONFIRMACION_RESERVA_CARRITO);
		return nuevo;
	}

	public void registrarIntercambioFinalizado(Oferta oferta) {
		this.intercambiosFinalizados.add(oferta);
		this.catalogoIntercambio.removeAll(oferta.getProductosOfertados());
		this.catalogoIntercambio.removeAll(oferta.getProductosSolicitados());
	}

	public List<Empleado> obtenerEmpleadosTienda() {
		List<Empleado> listaEmpleados = new ArrayList<>();
		for (UsuarioRegistrado usuario : usuarios) {
			if (usuario instanceof Empleado) {
				listaEmpleados.add((Empleado) usuario);
			}
		}
		return listaEmpleados;
	}

	public List<Cliente> obtenerClientesTienda() {
		List<Cliente> listaClientes = new ArrayList<>();
		for (UsuarioRegistrado u : usuarios) {
			if (u instanceof Cliente) {
				listaClientes.add((Cliente) u);
			}
		}
		return listaClientes;
	}

	public void añadirProducto(ProductoVenta nuevo) {
		if (this.getStockVentas().contains(nuevo))
			return;
		this.getStockVentas().add(nuevo);

		for (Categoria c : nuevo.getCategorias()) {
			for (Cliente cl : obtenerClientesTienda()) {
				cl.notificarProductoNuevoCategoria("Nuevo producto en " + c.getNombre() + ": " + nuevo.getNombre(),
						c.getNombre());
			}
		}
	}

	public void solicitarTasacion(Producto2Mano p) {
		this.pendientes_Tasacion.add(p);
		for (Empleado empleado : this.obtenerEmpleadosTienda()) {
			if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
				empleado.recibirNotificacion("Hay un nuevo producto para valorar: " + p.getNombre());
			}
		}
	}

	// - DESCUENTOS
	public void agregarDescuento(Descuento d) {
		this.descuentosActivos.add(d);
		this.historialDescuentos.add(d);
	}

	public void limpiarDescuentosCaducados() {
		List<Descuento> descuentos_finalizados = new ArrayList<>();
		for (Descuento d : this.descuentosActivos) {
			if (!d.estaActivo()) {
				descuentos_finalizados.add(d);
			}
		}
		this.descuentosActivos.removeAll(descuentos_finalizados);
	}

	// --- LÓGICA DE INTERCAMBIO
	public void publicarParaIntercambio(Producto2Mano p) {
		if (p.getValoracion() != null && !this.getCatalogoIntercambio().contains(p)) {
			p.setBloqueado(false);
			this.catalogoIntercambio.add(p);
		}
	}

	// buscar productos de segunda mano, pero que no esten bloqueados
	/*
	 * public List<Producto2Mano> buscarSegundaMano(String query) {
	 * List<Producto2Mano> -ultados = new ArrayList<>(); for (Producto2Mano p :
	 * catalogoIntercambio) { // AHORA FILTRAMOS TAMBIÉN POR VISIBLE if
	 * (p.isVisible() && !p.isBloqueado() &&
	 * p.getNombre().toLowerCase().contains(query.toLowerCase())) {
	 * resultados.add(p); } } return resultados; }
	 */

	public void aplicarDescuentoPrioritario(Carrito carrito) {
		for (Descuento descuento : this.descuentosActivos) {
			if (!descuento.estaActivo()) {
				continue;
			}

			if (descuento instanceof Regalo) {
				Regalo regalo = (Regalo) descuento;
				if (regalo.aplicaRegalo(carrito)) {
					carrito.setDescuentoAplicado(descuento);
					return;
				}
			} else {
				double totalConDescuento = descuento.aplicarDescuento(carrito);
				if (totalConDescuento < carrito.calcularSubtotal()) {
					carrito.setDescuentoAplicado(descuento);
					return;
				}
			}
		}
		carrito.setDescuentoAplicado(null);
	}

	// --- GESTIÓN DE VENTAS NUEVAS
	public void registrarVenta(Pedido pedido) {
		this.historialVentas.add(pedido);
	}

	// GETTERS Y SETTERS

	public List<UsuarioRegistrado> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<UsuarioRegistrado> usuarios) {
		this.usuarios = usuarios;
	}

	// getPendientesTasacion eliminado por ser duplicado de getPendientes_Tasacion
	public List<Producto2Mano> getPendientesTasacion() {
		return pendientes_Tasacion;
	}

	public void setPendientes_Tasacion(List<Producto2Mano> pendientes_Tasacion) {
		this.pendientes_Tasacion = pendientes_Tasacion;
	}

	public Recomendador getRecomendador() {
		return this.recomendador;
	}

	public void setRecomendador(Recomendador recomendador) {
		this.recomendador = recomendador;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Producto2Mano> getCatalogoIntercambio() {
		return catalogoIntercambio;
	}

	public void setCatalogoIntercambio(List<Producto2Mano> catalogoIntercambio) {
		this.catalogoIntercambio = catalogoIntercambio;
	}

	public List<Pedido> getHistorialVentas() {
		return historialVentas;
	}

	public void setHistorialVentas(List<Pedido> historialVentas) {
		this.historialVentas = historialVentas;
	}

	public List<Descuento> getDescuentosActivos() {
		return descuentosActivos;
	}

	public void setDescuentosActivos(List<Descuento> descuentosActivos) {
		this.descuentosActivos = descuentosActivos;
	}

	public List<Oferta> getIntercambiosFinalizados() {
		return intercambiosFinalizados;
	}

	public void setIntercambiosFinalizados(List<Oferta> intercambiosFinalizados) {
		this.intercambiosFinalizados = intercambiosFinalizados;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public static void setInstancia(Tienda instancia) {
		Tienda.instancia = instancia;
	}

	public List<ProductoVenta> getStockVentas() {
		return stockVentas;
	}

	public void setStockVentas(List<ProductoVenta> stockVentas) {
		this.stockVentas = stockVentas;
	}

	public int getTiempoMaxCarrito() {
		return tiempoMaxCarrito;
	}

	public void setTiempoMaxCarrito(int tiempoMaxCarrito) {
		this.tiempoMaxCarrito = tiempoMaxCarrito;
	}

	public int getTiempoMaxOferta() {
		return tiempoMaxOferta;
	}

	public void setTiempoMaxOferta(int tiempoMaxOferta) {
		this.tiempoMaxOferta = tiempoMaxOferta;
	}

	public int getTiempoMaxPago() {
		return tiempoMaxPago;
	}

	public void setTiempoMaxPago(int tiempoMaxPago) {
		this.tiempoMaxPago = tiempoMaxPago;
	}

	public List<Descuento> getHistorialDescuentos() {
		return historialDescuentos;
	}

	public void setHistorialDescuentos(List<Descuento> historialDescuentos) {
		this.historialDescuentos = historialDescuentos;
	}

	public boolean isSistemaTiemposConfigurando() {
		return this.tiempoMaxCarrito > 0 && this.tiempoMaxOferta > 0 && this.tiempoMaxPago > 0;
	}

	public List<UsuarioRegistrado> getUsuariosConSesionActiva() {
		return usuariosConSesionActiva;
	}

	public void setUsuariosConSesionActiva(List<UsuarioRegistrado> usuariosConSesionActiva) {
		this.usuariosConSesionActiva = usuariosConSesionActiva;
	}

	public List<Notificacion> getHistorialNotificaciones() {
		return historialNotificaciones;
	}

	public double getPrecioTasacion() {
		return precioValoracion;
	}

	public void setPrecioTasacion(double precioTasacion) {
		if (precioTasacion <= 5) {
			return;
		}
		this.precioValoracion = precioTasacion;
	}
	
	//Metodo para los test
	public void vaciarTienda() {
	    // 1. Limpiamos las listas de usuarios y sesiones
	    if (this.usuarios != null) {
	        this.usuarios.clear();
	        // RE-CREAMOS EL GESTOR: Al ser el "USR-1", los tests lo necesitan vivo
	        Gestor gestor = new Gestor();
	        this.usuarios.add(gestor);
	        
	        // También reseteamos las sesiones activas
	        if (this.usuariosConSesionActiva != null) {
	            this.usuariosConSesionActiva.clear();
	            this.usuariosConSesionActiva.add(gestor);
	        }
	    }

	    // 2. Limpiamos el stock y catálogos
	    if (this.stockVentas != null) this.stockVentas.clear();
	    if (this.catalogoIntercambio != null) this.catalogoIntercambio.clear();
	    if (this.pendientes_Tasacion != null) this.pendientes_Tasacion.clear();
	    
	    // 3. Limpiamos categorías y descuentos
	    if (this.categorias != null) this.categorias.clear();
	    if (this.descuentosActivos != null) this.descuentosActivos.clear();
	    if (this.historialDescuentos != null) this.historialDescuentos.clear();
	    
	    // 4. Limpiamos historial de ventas y notificaciones
	    if (this.historialVentas != null) this.historialVentas.clear();
	    if (this.historialNotificaciones != null) this.historialNotificaciones.clear();
	    if (this.intercambiosFinalizados != null) this.intercambiosFinalizados.clear();

	    // 5. Opcional: Resetear tiempos si quieres que cada test los configure
	    this.tiempoMaxCarrito = 0;
	    this.tiempoMaxOferta = 0;
	    this.tiempoMaxPago = 0;

	    System.out.println("[SISTEMA] Tienda reseteada para nueva prueba unitaria.");
	}
}