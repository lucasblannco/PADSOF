package usuarios;

import java.time.LocalDate;
import java.util.IllegalFormatFlagsException;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.internal.stream.events.AttributeImpl;

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

	private ProductoVenta buscarProductoVentaPorId(String id) {
		if (id == null || id.isBlank()) {
			return null;
		}
		try {
			int numero = Integer.parseInt(id.substring(2));// devuelve lo que hay a partir de la segunda letra
			int indice = numero - 1;// Segun lo tenemos implementado el primer producto tiene id 1 entonces ese
									// ocupara la posicion 0, retsamos 1
			if (indice >= 0 && indice < Tienda.getInstancia().getStockVentas().size()) {
				return Tienda.getInstancia().getStockVentas().get(indice);
			}
		} catch (NumberFormatException e) {

			return null;
		}
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
		Empleado nuevoEmpleado = new Empleado(nickname, password);
		Tienda tienda = Tienda.getInstancia();
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
		Empleado nuevo = new Empleado(nickname, password);

		if (permisos != null) {
			for (TipoPermisos per : permisos) {
				nuevo.asignarPermiso(per);
			}
		}
		Tienda tienda = Tienda.getInstancia();
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
		ProductoVenta p = buscarProductoVentaPorId(idProductoVenta);
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

	public void configurarParametrosGlobales() {
		// Implementación según requisitos
	}

	public void crearDescuento(String idProducto, double porcentaje, LocalDate inicio, LocalDate fin) {
		if (porcentaje <= 0 || porcentaje > 100) {
			System.out.println("Porcentaje no válido");
			return;
		}
		Descuento nuevoDesc = new Descuento(idProducto, porcentaje / 100, inicio, fin);
		Tienda.getInstancia().agregarDescuento(nuevoDesc);
	}

	public void modificarPrecio(String idProd, double nuevoPrecio) {
		// Lógica para buscar producto en Tienda y cambiar precioOficial
	}

	public void configurarComposicionPack(String idPack, List<String> productos) {
		// Lógica para definir qué productos forman un pack
	}

	public void crearCategoria(String nombre) {
		// Lógica para añadir categorías al sistema
	}

	public void añadirProductoaCategoria(String idProd, String nombreCat) {
		// Lógica de organización del catálogo
	}

	public boolean setTiemposSistema(double tInter, double tCar, double tPago) {
		// Configura tiempos de expiración de ofertas, carritos, etc.
		return true;
	}

	public void modificarPerfil(String nuevoNick, String nuevaPass) {
		this.nickname = nuevoNick;
		this.password = nuevaPass;
		System.out.println("Perfil de administrador actualizado.");
	}

	// --- MÉTODOS DE CONSULTA (Delegación en Estadistica) ---

	public List<Cliente> verClientesTopCompras() {
		return motor.obtenerClientesConMasCompras();
	}

	public List<Cliente> verClientesTopIntercambios() {
		return motor.obtenerClientesConMasIntercambios();
	}

	public double consultarIngresos(LocalDate inicio, LocalDate fin) {
		return motor.calcularIngresosRango(inicio, fin);
	}

	public double consultarExitoIntercambios() {
		return motor.calcularTasaExitoIntercambios();
	}

	public void configurarSistemaRecomendacion(int nuevoLimite, boolean encender) {
		// Accede al recomendador a través de la tienda y lo configura
		Tienda.getInstancia().getRecomendador().setConfiguracion(nuevoLimite, encender);
		System.out.println("Sistema de recomendaciones actualizado por el Gestor.");
	}
}
