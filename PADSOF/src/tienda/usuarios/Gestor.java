package usuarios;

import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.IllegalFormatFlagsException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale.Category;

import com.sun.xml.internal.stream.events.AttributeImpl;
import ventas.*;
import productos.Categoria;
import productos.Pack;
import productos.ProductoVenta;
import tienda.*;
import tienda.Ventas.Descuento;

public class Gestor extends UsuarioRegistrado {
	private static final String NICKNAME_INICIAL = "admin_Gestor";
	private static final String PASSWORD_INICIAL = "Admin@1234";

	private Estadistica motorEstadistico;

	// Lo instancia Tienda al arrancar, nadie más
	public Gestor() {
		super(NICKNAME_INICIAL, PASSWORD_INICIAL);
		this.motorEstadistico = Estadistica.getInstancia();
	}

	@Override
	public void mostrarPanelPrincipal() {
		System.out.println("--- PANEL DE CONTROL DEL GESTOR ---");
		System.out.println("1. Gestionar Empleados y Permisos");
		System.out.println("2. Configurar Parámetros del Sistema");
		System.out.println("3. Gestión de Descuentos y Precios");
		System.out.println("4. Ver Estadísticas de Rendimiento");
	}

	// METODOS GLOBALES
	private Empleado buscarEmpleadoporId(String id) {
		if (id == null || id.isBlank()) {
			return null;
		}
		for (UsuarioRegistrado u : Tienda.getInstancia().getUsuarios()) {
			if (u instanceof Empleado && u.getId().equals(id)) {
				return (Empleado) u;
			}
		}
		return null;
	}

	private boolean validarFechas(LocalDateTime inicio, LocalDateTime fin) {
		if (inicio == null || fin == null) {
			System.out.println("Las fechas no pueden ser null");
			return false;
		}
		if (!fin.isAfter(inicio)) {
			System.out.println("La fecha de fin debe ser posterior a la de inicio.");
			return false;
		}
		return true;
	}

	public Categoria buscarCategoriaPorNombre(String name) {
		if (name == null || name.isBlank()) {
			System.out.println("El nombre de la categoria no puede estar vacio");
			return null;
		}
		for (Categoria cat : Tienda.getInstancia().getCategorias()) {
			if (cat.getNombre().equals(name)) {
				return cat;
			}
		}
		System.out.println("No hay ninguna categoria de productos con ese nombre");
		return null;
	}
	/*
	 * private ProductoVenta buscarProductoporID(String id) { if
	 * (id==null||id.isBlank()) { return null; } for (ProductoVenta pro:
	 * Tienda.getInstancia().getStockVentas()) { if (pro.get) {
	 * 
	 * }
	 * 
	 * } }
	 */

	// GESTION DE LOS EMPLEADOS

	public boolean darDeAltaEmpleados(String nickname, String password) {
		if (nickname == null || password == null) {
			System.out.println("Error en la creacion del empleado. Los parametros son null.");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		if (tienda.existeUsuarioConNickname(nickname)) {
			System.out.println("Error: El nickname '" + nickname + "' ya está en uso.");
			return false;
		}
		Empleado nuevoEmpleado = new Empleado(nickname, password);

		tienda.getUsuarios().add(nuevoEmpleado);
		System.out.println("Empleado con id " + nuevoEmpleado.getId() + " ha sido dado de alta en la aplicacion");
		return true;
	}

	public boolean darDeAltaEmpleados_Permisos(String nickname, String password, List<TipoPermisos> permisos) {
		if (nickname == null || password == null) {
			System.out.println("Error. El nombre y la contraseña que se le quiere asignar no pueden ser null");
			return false;
		}
		if (nickname.isBlank() || password.isBlank()) {
			System.out.println("Error. La contraseña o el nickname no pueden estar vacios");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		if (tienda.existeUsuarioConNickname(nickname)) {
			System.out.println("Error: El nickname '" + nickname + "' ya está en uso.");
			return false;
		}
		Empleado nuevo = new Empleado(nickname, password);

		if (permisos != null) {
			for (TipoPermisos per : permisos) {
				nuevo.asignarPermiso(per);
			}
		}

		tienda.getUsuarios().add(nuevo);
		System.out.println("Empleado '" + nickname + "' dado de alta con id " + nuevo.getId() + ".");
		return true;
	}

	public boolean darDeBajaAEmpleado(String idEmpleado) {
		Empleado e = buscarEmpleadoporId(idEmpleado);
		if (e == null) {
			System.out.println("El empleado no puede ser null");
			return false;
		}
		if (!Tienda.getInstancia().getUsuarios().contains(e)) {
			System.out.println("El empleado no exiate en el sistema");
			return false;
		}
		e.getPermisos().clear();
		e.setDespedido(true);
		System.out.println("El empleado " + e.getNickname() + "ha sido despedido de la tienda.");
		return true;
	}

	public boolean asignarPermiso(String idEmpleado, TipoPermisos permiso) {
		if (permiso == null) {
			System.out.println("El permiso no puede ser null.");
			return false;
		}
		Empleado empleado = buscarEmpleadoporId(idEmpleado);
		if (empleado == null) {
			System.out.println("No hay ningun empleado con ese id");
			return false;
		}
		if (empleado.getPermisos().contains(permiso)) {
			System.out.println("El empleado con id " + empleado.getId()
					+ " ya tiene el permiso que se le esta intentando asignar. ");
			return false;
		}
		empleado.asignarPermiso(permiso);
		return true;
	}

	public boolean retirarPermiso(String idEmpleado, TipoPermisos permiso) {
		Empleado empleado = buscarEmpleadoporId(idEmpleado);
		if (empleado == null) {
			System.out.println("No hay ningun empleado con ese id");
			return false;
		}
		if (!empleado.getPermisos().contains(permiso)) {
			System.out.println("El empleado con id " + empleado.getId()
					+ " no tiene el permiso que se le esta intentando retirar. ");
			return false;
		}
		empleado.quitarPermiso(permiso);
		return true;
	}

	// GESTION DE LOS TIEMPOS MAXIMOS DE LA APLICACION
	public boolean setTiemposSistema(int tOferta, int tCarrito, int tPago) {
		if (tOferta <= 0 || tCarrito <= 0 || tPago <= 0) {
			System.out.println("Todos los tiempos deben ser mayores que 0");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxCarrito(tCarrito);
		Tienda.getInstancia().setTiempoMaxOferta(tOferta);
		Tienda.getInstancia().setTiempoMaxPago(tPago);
		return true;
	}

	public boolean setTiempoMaxOferta(int tiempo) {
		if (tiempo <= 0) {
			System.out.println("El tiempo debe ser mayor que 0.");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxOferta(tiempo);
		return true;
	}

	// Cambia solo el tiempo de bloqueo de carrito
	public boolean setTiempoMaxCarrito(int tiempo) {
		if (tiempo <= 0) {
			System.out.println("El tiempo debe ser mayor que 0.");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxCarrito(tiempo);
		return true;
	}

	// Cambia solo el tiempo máximo de pago de pedido pendiente
	public boolean setTiempoMaxPago(int tiempo) {
		if (tiempo <= 0) {
			System.out.println("El tiempo debe ser mayor que 0.");
			return false;
		}
		Tienda.getInstancia().setTiempoMaxPago(tiempo);
		return true;
	}

	// MODIFICACION DE LOS PRECIOS

	public boolean modificarPrecioProducto(String idProductoVenta, double nuevoPrecio) {
		if (idProductoVenta == null || idProductoVenta.isBlank()) {
			System.out.println("El id del producto no puede estar vacío.");
			return false;
		}
		if (nuevoPrecio <= 0) {
			System.out.println("El precio de los productos debe ser mayor que 0");
			return false;
		}
		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProductoVenta);
		if (p == null) {
			System.out.println("No existe ningun producto venta con id: " + idProductoVenta);
			return false;
		}
		if (p instanceof Pack) {// Si es un pack comprobamos que el precio sea al menos un euro mas barato que
								// el precio que tendria la suma individual de los productos en el pack
			Pack pack = (Pack) p;
			double sumaProductosindividuales = pack.calcularSumaProductos();
			if (nuevoPrecio >= sumaProductosindividuales - 1) {
				System.out.println(
						"Estas modificando el precio de un pack y el precio de los packs tiene que ser al menos un euro menor que el precio que sumarian individualmente los productos que contiene el pack.La suma indicidual de los productos es : "
								+ sumaProductosindividuales);
				return false;
			}
		}
		p.setPrecioOficial(nuevoPrecio);
		System.out.println("El precio del producto con id: " + idProductoVenta + " y nombre: " + p.getNombre()
				+ " ha sido modificado por el gestor correctamente. Ahora vale " + nuevoPrecio + ". ");
		return true;
	}

	// GESTION DE LOS DESCUENTOS

	public boolean crearDescuentoCantidad(String nombre, String idProducto, int cantidadMinima, double porcentaje,
			LocalDateTime inicio, LocalDateTime fin) {

		if (!validarFechas(inicio, fin)) {
			return false;
		}
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacio");
			return false;
		}
		if (cantidadMinima <= 1) {
			System.out.println("La cantidad minima para poder crear un descuento es de dos unidades");
			return false;
		}
		if (Tienda.getInstancia().buscarProductoVentaPorId(idProducto) == null) {
			return false;
		}

		Descuento d = new DescuentoCantidad(nombre, inicio, fin, cantidadMinima, porcentaje);
		Tienda.getInstancia().agregarDescuento(d);
		System.out
				.println("Descuento por cantidad agregado correctamente sobre el producto con id:" + idProducto + ". ");
		return true;
	}

	public boolean crearDescuentoVolumen(String nombre, double precioMinimo, double porcentaje, LocalDateTime inicio,
			LocalDateTime fin) {
		if (!validarFechas(inicio, fin)) {
			return false;
		}
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacio");
			return false;
		}
		if (precioMinimo <= 20) {// Ponemos minimo 20 euros
			System.out.println(
					"Para crear un descuento por volumen de gasto el precio total de la compra debe ser al menos de 20 euros");
			return false;
		}
		Descuento desc = new DescuentoVolumen(nombre, inicio, fin, precioMinimo, porcentaje);
		Tienda.getInstancia().agregarDescuento(desc);
		System.out.println("Se ha creado un descuento por volumen de gasto superior a " + precioMinimo);
		return true;
	}

	public boolean crearDescuentoCategoria(String nombre, String nombreCategoria, double porcentaje,
			LocalDateTime inicio, LocalDateTime fin) {
		if (!validarFechas(inicio, fin)) {
			return false;
		}
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacio");
			return false;
		}
		if (nombreCategoria == null || nombreCategoria.isBlank()) {
			System.out
					.println("El nombre de la categoria sobre la que se va a plicar el descuento no puede estar vacio");
			return false;
		}
		Categoria cat = buscarCategoriaPorNombre(nombreCategoria);
		if (cat == null) {
			return false;
		}
		Descuento descuento = new DescuentoCategoria(nombreCategoria, inicio, fin, cat, porcentaje);
		Tienda.getInstancia().agregarDescuento(descuento);
		System.out
				.println("Descuento para los productos de la categoria " + cat.getNombre() + " creado correctamente.");
		return true;
	}

	public boolean crearDescuentoRegalo(String nombre, String idProductoRegalado, double gastoNecesario,
			LocalDateTime inicio, LocalDateTime fin) {

		if (!validarFechas(inicio, fin))
			return false;
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre del descuento no puede estar vacío.");
			return false;
		}

		if (gastoNecesario <= 35) {
			System.out.println("El gasto necesario debe ser mayor que 35.");
			return false;
		}

		ProductoVenta productoRegalado = Tienda.getInstancia().buscarProductoVentaPorId(idProductoRegalado);
		if (productoRegalado == null) {
			System.out.println("Error: El producto con ID " + idProductoRegalado + " no existe.");
			return false;
		}

		Descuento d = new Regalo(nombre, inicio, fin, gastoNecesario, productoRegalado);
		Tienda.getInstancia().agregarDescuento(d);
		System.out.println("Descuento regalo '" + nombre + "' creado correctamente.");
		return true;
	}

	public boolean eliminarDescuento(String idDescuento) {
		if (idDescuento == null || idDescuento.isBlank())
			return false;

		List<Descuento> lista = Tienda.getInstancia().getDescuentosActivos();
		boolean eliminado = lista.removeIf(d -> d.getId().equals(idDescuento));
		if (eliminado) {
			System.out.println("Descuento " + idDescuento + " eliminado correctamente.");
		} else {
			System.out.println("No se encontró el descuento.");
		}
		return eliminado;
	}

	/// METODOS RELACIONADOS CON LAS CATEGORIAS
	public boolean crearCategoria(String nombre, String descripcion) {
		if (nombre == null || nombre.isBlank()) {
			System.out.println("El nombre no puede estar vacio");
		}
		if (descripcion == null || descripcion.isBlank()) {
			System.out.println("La descripcion no puede estar vacia");
			return false;
		}
		Categoria c = new Categoria(nombre, descripcion);
		Tienda.getInstancia().getCategorias().add(c);
		System.out.println("La categoria " + nombre + " ha sifo creada y añadida correctamente.");
		return true;
	}

	public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
		if (idProducto == null || nombreCat == null) {
			System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
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

	// ----------------------------------------------------------------
	// PERFIL DEL GESTOR
	// El gestor SÍ puede modificar sus credenciales (requisito 2.1.1.9)
	// ----------------------------------------------------------------

	/**
	 * Modifica el perfil del gestor (nickname y contraseña).
	 * 
	 * @param nuevoNickname El nuevo nombre de usuario.
	 * @param nuevoPass     La nueva contraseña que debe cumplir requisitos de
	 *                      seguridad.
	 * @return true si se cambió con éxito, false si los datos no son válidos.
	 */
	public boolean modificarPerfil(String nuevoNickname, String nuevoPass) {

		if (nuevoNickname == null || nuevoNickname.isBlank()) {
			System.out.println("El nuevo nickname no puede estar vacío");
			return false;
		}
		// puede cquerer cambiar solo la contraseÑa y dejarse el mismo nombre, entonces
		// si el ya tiene ese nombre va a existir un usuario con ese nombre que es el.
		if (!nuevoNickname.equalsIgnoreCase(this.getNickname())
				&& Tienda.getInstancia().existeUsuarioConNickname(nuevoNickname)) {
			System.out.println("Error: El nickname '" + nuevoNickname + "' ya está siendo usado por otro usuario.");
			return false;
		}
		// Validar que la nueva contraseña cumpla la seguridad
		if (!validarPassword(nuevoPass)) {
			return false;
		}
		this.setNickname(nuevoNickname);
		this.setPassword(nuevoPass); // Recuerda tener estos métodos en la clase padre

		System.out.println("Perfil del gestor actualizado con éxito.");
		return true;
	}

	// ----------------------------------------------------------------
	// ESTADÍSTICAS
	// ----------------------------------------------------------------

	public List<Cliente> verClientesTopCompras() {
		return motorEstadistico.obtenerClientesConMasCompras();
	}

	public List<Cliente> verClientesTopIntercambios() {
		return motorEstadistico.obtenerClientesConMasIntercambios();
	}

	public double consultarIngresosRango(LocalDate inicio, LocalDate fin) {
		return motorEstadistico.calcularIngresosRango(inicio, fin);
	}

	public double[] consultarIngresosPorMeses() {
		return motorEstadistico.calcularIngresosMeses();
	}

	public double consultarIngresosVenta() {
		return motorEstadistico.calcularIngresosVenta();
	}

	public double consultarIngresosTasacion() {
		return motorEstadistico.calcularIngresosTasacion();
	}

}
