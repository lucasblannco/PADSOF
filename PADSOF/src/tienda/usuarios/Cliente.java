package usuarios;

import tienda.*;
import productos.*;

import java.security.PublicKey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import intercambios.*;
import productos.Producto2Mano;

import ventas.*;

public class Cliente extends UsuarioRegistrado {
	// private double saldoPuntos;

	private List<Pedido> historialPedidos;
	private Carrito carritoActual;
	private List<Producto2Mano> carteraIntercambio;
	private List<Oferta> ofertasPendientes;
	private List<Oferta> historialIntercambios;
	private List<Reseña> reseñas;
	protected List<Notificacion> notificaciones; //Posible cambio  a un hashmap no? Es para organizarlas segun el tipo de notificacion
	private PreferenciaNotificacion preferencias;

	// Constructor//
	public Cliente() {
		super();
		this.historialPedidos = new ArrayList<>();
		this.carteraIntercambio = new ArrayList<>();
		this.ofertasPendientes = new ArrayList<>();
		this.reseñas = new ArrayList<>();
		this.preferencias = new PreferenciaNotificacion();
	}

	@Override
	public void mostrarPanelPrincipal() {
	}

	public void subirProducto(String nombre, String descripString, String imagen) {

		Producto2Mano product = new Producto2Mano(this, nombre, descripString, imagen);// Creamos el producto mediante
																						// // el //
		// contructor
		carteraIntercambio.add(product);
	}

	public boolean tieneProductoenSuCartera(Producto p) {
		if (p == null) {
			return false;
		}
		return this.carteraIntercambio.contains(p);
	}

	public void solicitarTasacion(Producto2Mano p, String tarjeta, int CVV, Date caducidad) {
		if (tieneProductoenSuCartera(p) && (p.getVisible() == false)) {// Comprobamos que ese producto este en la
																		// cartera del usuario y
																		// que ese producto no tenga una hecha una
																		// valoracion. Si un producto ya esta
																		// valorado ya estara en la cartera del
																		// usuario.
			p.getValoracion().setEstadoValoracion(EstadoValoracion.PENDIENTE_DE_PAGO);

			if (p.getValoracion().pagar(tarjeta, CVV, caducidad) == false) {
				this.recibirNotificacion("Pago no aceptado");
				return;
			}
			// Notificamos a la tienda que hay un nuevo producto pendiente de tasar //Esto
			// cuidado//
			Tienda.getInstancia().solicitarTasacion(p);
			this.recibirNotificacion("Valoracion Solicitada. Esperando a que un empleado lo tase.");
		}
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

		for (Producto2Mano p : misProductos) {

			if (tieneProductoenSuCartera(p) || p.isBloqueado() == true) { // No puedes proponer productos que no esten
																			// en tu cartera ni tampoco productos
				return false;
			}

			if (p.isBloqueado() == true) {
				return false; // Si uno de los productos de mi oferta esta bloqueado(eso es que lo hemos
				// ofercido para otro intercambio) no lo puedo ofrecer para este intercambio.
			}
		}
		// Una vez que hemos comprobado que ningunoi de los productos que hemos ofrecido
		// esta bloqueado ya si se crea la oferta.
		Oferta nuevaOferta = new Oferta(this, destinatario, misProductos, susProductos);
		this.ofertasPendientes.add(nuevaOferta);

		destinatario.getOfertasPendientes().add(nuevaOferta);// Añadimos al destinatario de la oferta esta oferta.
		destinatario.recibirNotificacion("Has recibido una propuesta de intercambio de " + this.nickname);
		for (Producto2Mano p : misProductos)// Todos los productos ofrecidos en mi oferta pasan a estar bloqueados.
			p.setBloqueado(true);
		return true;
	}
	
	
	
//aceptar Oferta
	public void confirmarIntercambio(Oferta oferta) {
		if (this.getOfertasParaDecidir().contains(oferta)) {
			//oferta.aceptarYEjecutar();
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

	
	// REVISAR.METER A TIENDA.
	public void recibirNotificacion(String mensaje) {
		if (this.notificaciones == null) {
			this.notificaciones = new ArrayList<>();
		}
		// Si aún no has creado la clase Notificacion, puedes pasarle un String
		// o crear el objeto aquí mismo si ya la tienes.
		this.notificaciones.add(new Notificacion(mensaje));
		System.out.println("[Notificación Cliente]: " + mensaje);
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
		if (p == null) {
			return false;
		}
		if (p.getStockDisponible() < cantidad) {
			System.out.println("No hay suficientes unidades en la tienda de este producto");
			return false;
		}
		if (this.carritoActual == null) {
			this.carritoActual = new Carrito();
		}
		this.getCarritoActual().añadirProducto(p, cantidad);
		return true;
	}

	public boolean comprarCarrito() {
		{
			if (carritoActual == null) {
				System.out.println("No tienes productos en el carrito. Añade productos para poder comprarlo");
				return false;
			}
			Pedido pedido = new Pedido(this, this.carritoActual);
			this.getHistorialPedidos().add(pedido);
			this.carritoActual = null;

			//// VER COMO SE BORRA el carrito
			///
			return true;
		}
	}

	public boolean pagar(Pedido p, String numeroTarjeta, Date fechaTarjeta, int CVV) {
		if (!this.getHistorialPedidos().contains(p)) {
			return false;
		}
		if (p.getEstado() != EstadoPedido.PENDIENTE_PAGO) {
			return false;
		}
		Pago pago = new Pago(numeroTarjeta, p.calcularTotal(), fechaTarjeta, CVV);
		if (pago.getExito() == false) {
			return false;
		}
		return true;
	}

	public void establecerPreferenciasNotificaciones() {
		
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
}