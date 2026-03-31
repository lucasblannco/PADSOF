package usuarios;

import tienda.*;
import productos.*;

import java.security.PublicKey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import intercambios.*;
import productos.Producto2Mano;

import ventas.*;

public class Cliente extends UsuarioRegistrado {
	// private double saldoPuntos;

	private List<Pedido> historialPedidos;
	private String dni;
	private Carrito carritoActual;
	private List<Producto2Mano> carteraIntercambio;
	private List<Oferta> ofertasPendientes;
	private List<Oferta> historialIntercambios;
	private List<Reseña> reseñas;
	protected List<Notificacion> notificaciones;
	private PreferenciaNotificacion preferencias;

	// Constructor//
	public Cliente(String nickname, String password, String dni) {
		super(nickname, password);
		this.dni = dni;
		this.historialPedidos = new ArrayList<>();
		this.carteraIntercambio = new ArrayList<>();
		this.ofertasPendientes = new ArrayList<>();
		this.reseñas = new ArrayList<>();
		this.preferencias = new PreferenciaNotificacion();
		this.notificaciones = new ArrayList<>();
	}

	// Ver la cartera de otro usuario
	public List<Producto2Mano> verCarteraCliente(String nickname) {
		if (nickname == null || nickname.isBlank()) {
			System.out.println(
					"El nickname del usuario sobre el que se quiere ver la cartera de objetos de segunda mano no puede estar vacio");
			return null;
		}
		Cliente c = Tienda.getInstancia().buscarClientePorNickname(nickname);
		if (c == null) {
			return new ArrayList<>();
		}
		if (c.equals(this)) {
			System.out.println("Para ver tu propia cartera usa getCarteraIntercambio().");
			return new ArrayList<>();
		}
		List<Producto2Mano> array = new ArrayList<>();
		for (Producto2Mano p : c.getCarteraIntercambio()) {
			if (p.isVisible() && !p.isBloqueado()) {
				array.add(p);
			}
		}
		return array;
	}

	public void subirProducto(String nombre, String descripString, String imagen) {
		Producto2Mano product = new Producto2Mano(this, nombre, descripString, imagen);
		carteraIntercambio.add(product);
		System.out.println("Producto subido correctamente a tu cartera personal de objetos de sgeunda mano.");
	}

	public boolean tieneProductoenSuCartera(Producto2Mano p) {
		if (p == null) {
			return false;
		}
		return this.carteraIntercambio.contains(p);
	}

	public boolean solicitarTasacion(Producto2Mano p, String tarjeta, int CVV, Date caducidad) {
		if (p == null) {
			System.out.println("El producto no puede ser null");
			return false;
		}
		if (!tieneProductoenSuCartera(p)) {
			System.out.println("El producto no está en tu cartera del cliente " + this.getNickname());
			return false;
		}
		if (p.isVisible()) {
			System.out.println("El producto ya ha sido tasado");
			return false;
		}
		Pago pagoValoracionPago = new Pago(tarjeta, Tienda.getInstancia().getPrecioTasacion(), caducidad, CVV);
		if (!pagoValoracionPago.getExito()) {
			this.recibirNotificacionTipo(
					"Pago no aceptado, no se ha podido solicitar la valoracion del producto." + p.getNombre(),
					TipoNotificacion.Pago_FALLIDO);
			return false;
		}
		Estadistica.getInstancia().setnTasacionesCobradas(Estadistica.getInstancia().getnTasacionesCobradas() + 1);
		Tienda.getInstancia().solicitarTasacion(p);
		this.recibirNotificacionTipo("Pago correcto. Tasación solicitada. Esperando a que un empleado tase el producto",
				TipoNotificacion.PAGO_EXITOSO);
		return true;
	}

	// OFERTAS
	// quiero poder disntiguir entre lsa ofertas que tengo que responder y las que
	// me tienen que responder.
	// para no hacer 2 arrays, hago 2 metodos y ya

	// Esta es la que tengo que contestar yo//
	public List<Oferta> getOfertasParaDecidir() {
		List<Oferta> paraDecidir = new ArrayList<>();
		for (Oferta o : ofertasPendientes) {
			// Si el destino de la oferta soy yo, es que tengo que contestar por lo que son
			// ofertas pendientes
			if (o.getDestino().equals(this)) {//
				paraDecidir.add(o);
			}
		}
		return paraDecidir;
	}

//Ofertas que yo he mandado
	public List<Oferta> getOfertasEnEspera() {
		List<Oferta> enEspera = new ArrayList<>();
		for (Oferta o : ofertasPendientes) {
			if (o.getOrigen().equals(this)) {// Si yo soy el origen de la oferta, la añado a las ofertas que he hecho
												// que no me han contestado.
				enEspera.add(o);
			}
		}
		return enEspera;
	}

	public boolean proponerOferta(Cliente destinatario, List<Producto2Mano> misProductos,
			List<Producto2Mano> susProductos) {
		if (!Tienda.getInstancia().isSistemaTiemposConfigurando()) {
			System.out.println("El sistema no está configurado aún. Contacte con el gestor.");
			return false;
		}
		if (destinatario == null || misProductos == null || susProductos == null) {
			System.out.println("Los parámetros no pueden ser null.");
			return false;
		}
		if (destinatario.equals(this)) {
			System.out.println("No puedes hacerte una oferta a ti mismo.");
			return false;
		}
		if (misProductos.isEmpty() || susProductos.isEmpty()) {
			System.out.println("Debes ofrecer al menos un producto.");
			return false;
		}
		for (Producto2Mano p : misProductos) {

			if (!tieneProductoenSuCartera(p)) {
				System.out.println("El producto " + p.getId() + " no está en tu cartera.");
				return false;
			}
			if (p.isBloqueado()) {
				System.out.println("El producto " + p.getId() + " está bloqueado en otra oferta.");
				return false;
			}
		}
		for (Producto2Mano p : susProductos) {
			if (!destinatario.tieneProductoenSuCartera(p)) {
				System.out.println("El producto " + p.getId() + " no está en la cartera del destinatario.");
				return false;
			}
			if (p.isBloqueado()) {
				System.out.println("El producto " + p.getId() + " está bloqueado en otra oferta.");
				return false;
			}
		}
		Oferta nuevaOferta = new Oferta(this, destinatario, misProductos, susProductos);
		this.ofertasPendientes.add(nuevaOferta);
		destinatario.getOfertasPendientes().add(nuevaOferta);
		destinatario.recibirNotificacionTipo("Has recibido una propuesta de intercambio de " + this.getNickname(),
				TipoNotificacion.OFERTA_RECIBIDA);
		for (Producto2Mano p : misProductos)
			p.setBloqueado(true);
		return true;
	}

//aceptar Oferta
	public void confirmarIntercambio(Oferta oferta) {
		if (this.getOfertasParaDecidir().contains(oferta)) {
			// oferta.aceptarYEjecutar();
			oferta.aceptarOferta();
			return;
		}
		System.out.println("Esta oferta no la tienes disponible");
		return;
	}

	public List<Oferta> verIntercambioscon(Cliente c) {
		List<Oferta> intercambios = new ArrayList<>();
		if (c == null) {
			return null;
		}
		for (Oferta o : historialIntercambios) {// comprobamos que el cliente c sea el origen o el destino de la oferta
			if ((o.getDestino() == c && o.getOrigen() == this) || (o.getOrigen() == c && o.getDestino() == this)) {
				intercambios.add(o);
			}
		}
		return intercambios;
	}

	public boolean productoHasidoPedidoYentregado(ProductoVenta p) {
		if (p == null) {
			return false;
		}
		for (Pedido ped : historialPedidos) {
			if (ped.productoPertenece(p) == true && ped.getEstado() == EstadoPedido.ENTREGADO) {
				return true;
			}
		}
		return false;
	}

	public boolean escribirReseña(ProductoVenta p, int pts, String texto) {
		if (this.productoHasidoPedidoYentregado(p)) {
			Reseña res = new Reseña(this, p, pts, texto);
			this.reseñas.add(res);
			p.getReseñas().add(res);
			System.out.println("Reseña creada y añadida con exito ");
			return true;
		}
		System.out.println("No ha sido posible crear la reseña.");
		return false;
	}

	public Categoria determinarCategoriaFavorita() {
		int maxApariciones = 0;
		Categoria favorita = null;

		Map<Categoria, Integer> contador = new HashMap<>();

		for (Pedido p : this.getHistorialPedidos()) {
			for (LineaPedido linea : p.getLineas()) {
				for (Categoria cat : linea.getProducto().getCategorias()) {
					int n = contador.getOrDefault(cat, 0) + 1; // getOrdefault, devuelve el numero de la categoria cat
																// si existe, y cero sino. Si la categoría ya estaba en
																// el mapa, le suma 1; si es la primera vez que la ve,
																// empieza en 1.
					contador.put(cat, n); // a la clave cat le metemos el nuevo numero de apariciones

					if (n > maxApariciones) {
						maxApariciones = n;
						favorita = cat; // guardamos la categoria. Se ira actualizando cada vez que se supere el numero
										// de apariciones
					}
				}
			}
		}
		return favorita;
	}

	// Notificaciones de tipoq ue se envian dependiendo de si el cliente quiere que
	// se le envien o no

	public void recibirNotificacionTipo(String mensaje, TipoNotificacion tipo) {
		if (!this.preferencias.debeRecibirNotificacion(tipo)) {
			return;
		}
		this.notificaciones.add(new Notificacion(mensaje, tipo));
		System.out.println("[Notificación Cliente]: " + mensaje);

	}

	public void notificarProductoNuevoCategoria(String mensaje, String nombreCategoria) {
		if (!this.preferencias.NotificacionesProductosNUevosCategoriasInteres(nombreCategoria)) {
			return;
		}
		if (this.notificaciones == null)
			this.notificaciones = new ArrayList<>();
		this.notificaciones.add(new Notificacion(mensaje, TipoNotificacion.CATEGORIA_INTERES));
		System.out.println("[Notificación Cliente]: " + mensaje);
		return;
	}

	public List<Notificacion> getNotificacionesNoLeidas() {
		List<Notificacion> resultado = new ArrayList<>();
		for (Notificacion n : notificaciones) {
			if (!n.isLeida())
				resultado.add(n);
		}
		return resultado;
	}

	public void verNotificacion(Notificacion n) {
		if (n == null) {
			return;
		}
		if (!this.notificaciones.contains(n)) {
			System.out.println("No se puede leer una notificacion que no es tuya");
			return;
		}
		n.marcarComoLeida();
		return;
	}

	public List<Notificacion> getNotificacionesdeTipo(TipoNotificacion tipo) {
		List<Notificacion> notif = new ArrayList<>();
		for (Notificacion n : notificaciones) {
			if (n.getTipo() == tipo)
				notif.add(n);
		}
		return notif;
	}

	// La borras del cliente el ya no la podra ver pero en la tienda sigue presente,
	public boolean eliminarNotifacion(Notificacion n) {
		if (n == null) {
			return false;
		}
		if (!this.notificaciones.contains(n)) {
			System.out.println("No se puede borrar una notificacion que no es tuya");
			return false;
		}
		this.notificaciones.remove(n);
		return true;
	}

	// Se desbloquean los productos y se quita la oferta del cliente.
	public boolean eliminarOfertadeOfertasPendientes(Oferta o) {
		if (o == null || !this.getOfertasPendientes().contains(o)) {
			return false;
		}
		o.rechazar();
		return true;
	}

	public boolean eliminarProductodeCategoria(Producto2Mano p) {
		if (!this.getCarteraIntercambio().contains(p)) {
			return false;
		}
		this.getCarteraIntercambio().remove(p);
		return true;
	}

	public boolean añadirProductoCarrito(ProductoVenta p, int cantidad) {
		if (!Tienda.getInstancia().isSistemaTiemposConfigurando()) {
			System.out.println(
					"Error. Los aprametros del sistema de tiempos no está configurados. Hay que esperar hasta que el gestor de la tienda lo configure");
			return false;
		}

		if (p == null) {
			System.out.println("El producto no puede ser null.");
			return false;
		}
		if (cantidad <= 0) {
			System.out.println("La cantidad debe ser mayor que 0.");
			return false;
		}
		if (p.getStockDisponible() < cantidad) {
			System.out.println("No hay suficientes unidades en la tienda de este producto");
			return false;
		}
		if (this.carritoActual == null) {
			this.carritoActual = new Carrito(this);
		}
		this.getCarritoActual().añadirProducto(p, cantidad);
		return true;
	}

	public boolean reservarCarrito() {
		{
			if (!Tienda.getInstancia().isSistemaTiemposConfigurando()) {
				System.out
						.println("El sistema  de tiempos no está configurado aún. Espere a que el gestor lo configure");
				return false;
			}
			if (carritoActual == null || carritoActual.estaVacio()) {
				System.out.println("No tienes productos en el carrito. Añade productos para poder comprarlo");
				return false;
			}
			if (carritoActual.estaCaducado()) {
				carritoActual.vaciarCarrito();
				this.carritoActual = null;
				System.out.println("El carrito ha caducado, no se puede reservar");
				return false;
			}
			Pedido pedido = new Pedido(this, this.carritoActual);
			this.getHistorialPedidos().add(pedido);
			Tienda.getInstancia().registrarVenta(pedido);
			this.carritoActual = null;
			this.recibirNotificacionTipo(
					"Pedido creado correctamente. Tienes " + Tienda.getInstancia().getTiempoMaxPago()
							+ " minutos para pagarlo y completar la reserva.",
					TipoNotificacion.CONFIRMACION_RESERVA_CARRITO);
			return true;
		}
	}

	public boolean pagarCarrito(Pedido p, String numeroTarjeta, Date fechaTarjeta, int CVV) {
		if (p == null) {
			System.out.println("El pedido no puede ser null");
			return false;
		}

		if (!this.historialPedidos.contains(p)) {
			System.out.println("Este pedido no es tuyo");
			return false;
		}
		if (p.isCaducado()) {
			p.cancelarPedido();
			System.out.println(
					"El tiempo maximo para pagar el pedido ya expiro. Se han devuelto los productos del pedido al stock de la tienda. Disculpe las molestias.");
			return false;
		}
		if (p.getEstado() != EstadoPedido.PENDIENTE_PAGO) {
			System.out.println("Este pedido no está pendiente de pago");
			return false;
		}
		return p.pagar(numeroTarjeta, CVV, fechaTarjeta);
	}

	public boolean solicitarRecogidaPedido(String codigoRecogida) {
		Tienda tienda = Tienda.getInstancia();

		for (Pedido ped : tienda.getHistorialVentas()) {
			if (ped.getCliente().equals(this) && codigoRecogida.equals(ped.getCodigoRecogida())
					&& ped.getEstado() == EstadoPedido.LISTO_PARA_RECOGER) {

				ped.setRecogida_solicitada(true);
				return true;
			}
		}
		System.out.println("Error en la solicitud de recogida de pedido");
		return false;
	}

	// MEWTODOS DE PREFERENCIA DE NOTIFICACIONES

	public boolean configurarPreferenciaNotificacion(TipoNotificacion tipo, boolean valor) {
		if (tipo == null) {
			System.out.println("El tipo de notificación no puede ser null.");
			return false;
		}
		this.preferencias.modificarPreferencia(tipo, valor);
		return true;
	}

	public boolean añadirCategoriaInteresParaRecibirInfo(String nombreCategoria) {
		return this.preferencias.añadirCategoriaInteres(nombreCategoria);
	}

	public boolean eliminarCategoriaInteres(String nombreCategoria) {
		return this.preferencias.eliminarCategoriaInteres(nombreCategoria);
	}

	public PreferenciaNotificacion verPreferencias() {
		return preferencias;
	}

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

		System.out.println("Perfil del cliente actualizado con éxito.");
		return true;
	}

	// --- GETTERS ---

	public List<Pedido> getHistorialPedidos() {
		return this.historialPedidos;
	}

	public Carrito getCarritoActual() {
		return carritoActual;
	}

	public List<Producto2Mano> getCarteraIntercambio() {
		return carteraIntercambio;
	}

	public List<Oferta> getOfertasPendientes() {
		return ofertasPendientes;
	}

	public List<Oferta> getHistorialIntercambios() {
		return historialIntercambios;
	}

	public List<Reseña> getReseñas() {
		return reseñas;
	}

	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	// --- SETTERS ---

	public void setCarritoActual(Carrito carritoActual) {
		this.carritoActual = carritoActual;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public void setPreferencias(PreferenciaNotificacion preferencias) {
		this.preferencias = preferencias;
	}

}