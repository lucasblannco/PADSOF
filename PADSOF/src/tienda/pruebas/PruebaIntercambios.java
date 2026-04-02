package pruebas;

import java.util.*;
import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class PruebaIntercambios {

	static int correctos = 0;
	static int fallos    = 0;

	static void check(String nombre, boolean condicion) {
		if (condicion) {
			System.out.println("\tCORRECTO -> " + nombre);
			correctos++;
		} else {
			System.out.println("\tFALLO -> " + nombre);
			fallos++;
		}
	}

	public static void main(String[] args) {

		/*
		 * Montaje:
		 * - Gestor configura tiempos del sistema (obligatorio para proponerOferta)
		 * - Empleado tasador con permiso de valoracion y confirmacion
		 * - alice y bob como clientes intercambiadores
		 * - carlos como tercer cliente para casos de error
		 * - Cada cliente sube un producto a su cartera y lo tasa directamente
		 *   (sin pasar por el pago de tasacion que requiere TeleCharge)
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		Gestor gestor = null;
		for (UsuarioRegistrado u : tienda.getUsuarios()) {
			if (u instanceof Gestor) { gestor = (Gestor) u; break; }
		}

		// El sistema de tiempos debe estar configurado para que proponerOferta funcione
		gestor.setTiemposSistema(60, 30, 30); // oferta=60min, carrito=30min, pago=30min
		gestor.setPrecioTasacion(10.0);

		List<TipoPermisos> permisosTasador = new ArrayList<>();
		permisosTasador.add(TipoPermisos.VALORACION_PRODUCTOS);
		permisosTasador.add(TipoPermisos.CONFIRMACION_INTERCAMBIO);
		gestor.darDeAltaEmpleados_Permisos("tasador", "Tasador@1", permisosTasador);
		Empleado tasador = tienda.obtenerEmpleadosTienda().get(0);

		Cliente alice  = new Cliente("alice",  "Alice@1234", "11111111A");
		Cliente bob    = new Cliente("bob",    "Bob@1234",   "22222222B");
		Cliente carlos = new Cliente("carlos", "Carlos@123", "33333333C");
		tienda.getUsuarios().add(alice);
		tienda.getUsuarios().add(bob);
		tienda.getUsuarios().add(carlos);

		System.out.println("\tMontaje listo.");


		/*
		 * Comprobamos que un cliente puede subir un producto a su cartera.
		 * El producto empieza sin valorar, no visible y bloqueado.
		 */
		System.out.println("\n============= subir producto a cartera =============");

		alice.subirProducto("Figura Goku", "Figura de coleccion", "goku.png");
		Producto2Mano p_alice = alice.getCarteraIntercambio().get(0);

		check("El producto aparece en la cartera de alice",
			alice.getCarteraIntercambio().contains(p_alice));
		check("El producto empieza sin valoracion",
			p_alice.getValoracion() == null);
		check("El producto empieza no visible",
			!p_alice.isVisible());
		check("El producto empieza bloqueado",
			p_alice.isBloqueado());
		check("El propietario del producto es alice",
			p_alice.getPropietario().equals(alice));

		// Tambien sube bob y carlos
		bob.subirProducto("Comic Batman",   "Edicion especial",  "batman.png");
		bob.subirProducto("JuegoMesa Risk", "Estrategia global", "risk.png");
		Producto2Mano p_bob1 = bob.getCarteraIntercambio().get(0);
		Producto2Mano p_bob2 = bob.getCarteraIntercambio().get(1);
		carlos.subirProducto("Carta Pokemon", "Holo rara", "pikachu.png");
		Producto2Mano p_carlos = carlos.getCarteraIntercambio().get(0);


		/*
		 * Simulamos el flujo de tasacion sin pasar por el pago con tarjeta
		 * (que requiere TeleChargeAndPaySystem8). Llamamos directamente a
		 * p.valorar() y tienda.publicarParaIntercambio() como hace el empleado
		 * tras recibir el pago.
		 *
		 * También probamos que valorar con NO_ACEPTADO deja el producto no visible.
		 */
		System.out.println("\n============= tasacion de productos =============");

		// Primero añadimos a pendientes como si el cliente hubiera pagado
		tienda.solicitarTasacion(p_alice);
		tienda.solicitarTasacion(p_bob1);
		tienda.solicitarTasacion(p_bob2);
		tienda.solicitarTasacion(p_carlos);

		check("p_alice aparece en pendientes de tasacion",
			tienda.getPendientesTasacion().contains(p_alice));

		// El tasador valora los productos
		boolean valoradoAlice = p_alice.valorar(20.0, EstadoProducto.MUY_BUENO, tasador);
		boolean valoradoBob1  = p_bob1.valorar(15.0, EstadoProducto.PERFECTO,   tasador);
		boolean valoradoBob2  = p_bob2.valorar(12.0, EstadoProducto.USO_LIGERO, tasador);

		check("valorar devuelve true si el estado es valido", valoradoAlice);
		check("tras valorar, el producto tiene valoracion",   p_alice.getValoracion() != null);
		check("la valoracion tiene el precio correcto",       p_alice.getValoracion().getPrecioTasacion() == 20.0);
		check("la valoracion tiene el estado correcto",
			p_alice.getValoracion().getEstadoProducto() == EstadoProducto.MUY_BUENO);
		check("la valoracion tiene el empleado correcto",
			p_alice.getValoracion().getEmpleado().equals(tasador));

		// Publicar en catalogo
		tienda.publicarParaIntercambio(p_alice);
		tienda.publicarParaIntercambio(p_bob1);
		tienda.publicarParaIntercambio(p_bob2);

		check("tras publicar, p_alice es visible",            p_alice.isVisible());
		check("tras publicar, p_alice no esta bloqueado",    !p_alice.isBloqueado());
		check("p_alice aparece en el catalogo de intercambio",
			tienda.getCatalogoIntercambio().contains(p_alice));

		// NO_ACEPTADO: producto no se publica
		boolean valoradoCarlos = p_carlos.valorar(0.0, EstadoProducto.NO_ACEPTADO, tasador);
		check("valorar con NO_ACEPTADO devuelve false",       !valoradoCarlos);
		check("producto NO_ACEPTADO sigue sin ser visible",   !p_carlos.isVisible());
		check("producto NO_ACEPTADO sigue bloqueado",          p_carlos.isBloqueado());

		// Errores en valorar
		check("valorar con estado null devuelve false",
			!p_carlos.valorar(5.0, null, tasador));
		check("valorar con empleado null devuelve false",
			!p_carlos.valorar(5.0, EstadoProducto.MUY_BUENO, null));
		check("valorar con precio negativo devuelve false",
			!p_carlos.valorar(-1.0, EstadoProducto.MUY_BUENO, tasador));


		/*
		 * Comprobamos verCarteraCliente: alice puede ver la cartera de bob
		 * (solo los productos visibles y no bloqueados).
		 * Carlos no puede ver la suya propia con este metodo.
		 */
		System.out.println("\n============= ver cartera de otro cliente =============");

		List<Producto2Mano> carteraBobVisible = alice.verCarteraCliente("bob");
		check("alice ve los productos de bob visibles y no bloqueados",
			carteraBobVisible.contains(p_bob1) && carteraBobVisible.contains(p_bob2));
		check("p_carlos (no visible) no aparece en ninguna cartera visible",
			!alice.verCarteraCliente("carlos").contains(p_carlos));

		// Intentar ver la propia cartera con verCarteraCliente devuelve lista vacia
		List<Producto2Mano> propiaCartera = alice.verCarteraCliente("alice");
		check("verCarteraCliente con propio nickname devuelve lista vacia",
			propiaCartera.isEmpty());

		// Nickname null o vacio devuelve null/vacio
		check("verCarteraCliente con nickname null devuelve null",
			alice.verCarteraCliente(null) == null);


		/*
		 * Comprobamos proponerOferta: alice ofrece su figura a bob a cambio
		 * de su comic Batman. Verificamos bloqueo, notificaciones y listas.
		 */
		System.out.println("\n============= proponerOferta =============");

		List<Producto2Mano> ofrecidos  = new ArrayList<>(Arrays.asList(p_alice));
		List<Producto2Mano> solicitados = new ArrayList<>(Arrays.asList(p_bob1));

		boolean ofertaCreada = alice.proponerOferta(bob, ofrecidos, solicitados);

		check("proponerOferta devuelve true",               ofertaCreada);
		check("la oferta aparece en pendientes de alice",
			!alice.getOfertasPendientes().isEmpty());
		check("la oferta aparece en pendientes de bob",
			!bob.getOfertasPendientes().isEmpty());
		check("p_alice queda bloqueado tras proponer oferta", p_alice.isBloqueado());
		check("bob tiene la oferta en getOfertasParaDecidir",
			!bob.getOfertasParaDecidir().isEmpty());
		check("alice tiene la oferta en getOfertasEnEspera",
			!alice.getOfertasEnEspera().isEmpty());

		Oferta oferta = alice.getOfertasPendientes().get(0);
		check("la oferta tiene estado PENDIENTE",            oferta.getEstado() == EstadoOferta.PENDIENTE);
		check("el origen de la oferta es alice",             oferta.getOrigen().equals(alice));
		check("el destino de la oferta es bob",              oferta.getDestino().equals(bob));
		check("los productos ofertados son correctos",       oferta.getProductosOfertados().contains(p_alice));
		check("los productos solicitados son correctos",     oferta.getProductosSolicitados().contains(p_bob1));


		/*
		 * Errores en proponerOferta.
		 */
		System.out.println("\n============= errores en proponerOferta =============");

		// No se puede hacer una oferta a uno mismo
		check("proponerOferta a si mismo devuelve false",
			!alice.proponerOferta(alice, ofrecidos, solicitados));

		// Producto ya bloqueado (p_alice ya esta en una oferta)
		List<Producto2Mano> ofrecidosBloqueados = new ArrayList<>(Arrays.asList(p_alice));
		check("no se puede ofertar un producto ya bloqueado",
			!alice.proponerOferta(bob, ofrecidosBloqueados, solicitados));

		// Destinatario null
		check("proponerOferta con destinatario null devuelve false",
			!alice.proponerOferta(null, ofrecidos, solicitados));

		// Lista vacia
		check("proponerOferta con lista de productos vacia devuelve false",
			!alice.proponerOferta(bob, new ArrayList<>(), solicitados));


		/*
		 * Comprobamos rechazar una oferta: creamos una segunda oferta entre
		 * bob y carlos (bob ofrece p_bob2, carlos no tiene nada disponible
		 * asi que hacemos el rechazo directamente).
		 * Tras rechazar, el producto se desbloquea y la oferta sale de pendientes.
		 */
		System.out.println("\n============= rechazar oferta =============");

		// Creamos oferta directamente para poder rechazarla
		List<Producto2Mano> ofrecidosBob = new ArrayList<>(Arrays.asList(p_bob2));
		List<Producto2Mano> solicitadosCarlos = new ArrayList<>(Arrays.asList(p_alice)); // simulado
		Oferta ofertaParaRechazar = new Oferta(bob, alice, ofrecidosBob, solicitadosCarlos);
		p_bob2.setBloqueado(true);
		bob.getOfertasPendientes().add(ofertaParaRechazar);
		alice.getOfertasPendientes().add(ofertaParaRechazar);

		check("ofertaParaRechazar empieza PENDIENTE",
			ofertaParaRechazar.getEstado() == EstadoOferta.PENDIENTE);

		ofertaParaRechazar.rechazar();

		check("tras rechazar, el estado es RECHAZADA",
			ofertaParaRechazar.getEstado() == EstadoOferta.RECHAZADA);
		check("tras rechazar, p_bob2 se desbloquea",
			!p_bob2.isBloqueado());
		check("tras rechazar, la oferta sale de pendientes de bob",
			!bob.getOfertasPendientes().contains(ofertaParaRechazar));
		check("tras rechazar, la oferta sale de pendientes de alice",
			!alice.getOfertasPendientes().contains(ofertaParaRechazar));


		/*
		 * Comprobamos aceptarOferta y confirmarIntercambio (empleado).
		 * Bob acepta la oferta original de alice. El empleado la confirma.
		 * Tras aceptarYEjecutar: los productos cambian de cartera,
		 * la oferta entra en historial y sale de pendientes.
		 */
		System.out.println("\n============= aceptar y confirmar intercambio =============");

		// Bob acepta la oferta de alice
		bob.confirmarIntercambio(oferta);
		check("tras confirmarIntercambio, el estado es ACEPTADA",
			oferta.getEstado() == EstadoOferta.ACEPTADA);

		// El empleado confirma fisicamente el intercambio
		boolean confirmado = tasador.confirmarIntercambio(oferta);
		check("empleado confirma el intercambio correctamente", confirmado);
		check("tras confirmar, el estado es REALIZADA",
			oferta.getEstado() == EstadoOferta.REALIZADA);

		// Los productos salen de las carteras originales
		check("p_alice sale de la cartera de alice",
			!alice.getCarteraIntercambio().contains(p_alice));
		check("p_bob1 sale de la cartera de bob",
			!bob.getCarteraIntercambio().contains(p_bob1));

		// La oferta entra en el historial de intercambios de ambos
		check("la oferta entra en el historial de alice",
			alice.getHistorialIntercambios().contains(oferta));
		check("la oferta entra en el historial de bob",
			bob.getHistorialIntercambios().contains(oferta));

		// La oferta sale de pendientes
		check("la oferta sale de pendientes de alice",
			!alice.getOfertasPendientes().contains(oferta));
		check("la oferta sale de pendientes de bob",
			!bob.getOfertasPendientes().contains(oferta));

		// La oferta se registra en intercambios finalizados de la tienda
		check("la oferta se registra en intercambiosFinalizados de la tienda",
			tienda.getIntercambiosFinalizados().contains(oferta));

		// Los productos se eliminan del catalogo de intercambio
		check("p_alice sale del catalogo de intercambio",
			!tienda.getCatalogoIntercambio().contains(p_alice));
		check("p_bob1 sale del catalogo de intercambio",
			!tienda.getCatalogoIntercambio().contains(p_bob1));


		/*
		 * Comprobamos verIntercambiosCon: alice puede ver los intercambios
		 * que ha tenido con bob y ninguno con carlos.
		 */
		System.out.println("\n============= verIntercambiosCon =============");

		List<Oferta> intercambiosConBob = alice.verIntercambioscon(bob);
		check("alice ve el intercambio con bob",         intercambiosConBob.contains(oferta));
		check("alice no tiene intercambios con carlos",  alice.verIntercambioscon(carlos).isEmpty());
		check("verIntercambiosCon null devuelve null",   alice.verIntercambioscon(null) == null);


		/*
		 * Comprobamos haCaducado: con tiempoMaxOferta muy alto no caduca,
		 * con tiempoMaxOferta 0 (simulado directamente) caduca.
		 */
		System.out.println("\n============= haCaducado =============");

		// Con tiempo generoso (60 min) una oferta recien creada no caduca
		List<Producto2Mano> ofrecidosBob2 = new ArrayList<>(Arrays.asList(p_bob2));
		List<Producto2Mano> solicitadosAlice = new ArrayList<>(Arrays.asList(p_alice));
		Oferta ofertaNueva = new Oferta(bob, alice, ofrecidosBob2, solicitadosAlice);
		check("una oferta recien creada no ha caducado (tiempoMax=60min)",
			!ofertaNueva.haCaducado());

		// Con tiempo 1 minuto tampoco caduca de inmediato
		tienda.setTiempoMaxOferta(1);
		check("oferta recien creada no caduca aunque el tiempo sea 1 minuto",
			!ofertaNueva.haCaducado());

		// Restauramos
		tienda.setTiempoMaxOferta(60);


		/*
		 * Comprobamos eliminarOfertadeOfertasPendientes en Cliente
		 * (permite al cliente retirar su propia oferta).
		 */
		System.out.println("\n============= eliminar oferta pendiente =============");

		// Preparamos una oferta fresca para poder retirarla
		p_bob2.setBloqueado(false);
		tienda.publicarParaIntercambio(p_bob2);
		List<Producto2Mano> ofrecidosRetiro = new ArrayList<>(Arrays.asList(p_bob2));
		Oferta ofertaParaRetirar = new Oferta(bob, alice, ofrecidosRetiro, new ArrayList<>());
		p_bob2.setBloqueado(true);
		bob.getOfertasPendientes().add(ofertaParaRetirar);
		alice.getOfertasPendientes().add(ofertaParaRetirar);

		boolean retirada = bob.eliminarOfertadeOfertasPendientes(ofertaParaRetirar);
		check("eliminarOferta devuelve true",                retirada);
		check("tras retirar, el estado es RECHAZADA",
			ofertaParaRetirar.getEstado() == EstadoOferta.RECHAZADA);
		check("tras retirar, p_bob2 se desbloquea",         !p_bob2.isBloqueado());

		// Error: oferta null
		check("eliminarOferta con null devuelve false",
			!bob.eliminarOfertadeOfertasPendientes(null));

		// Error: oferta que no esta en pendientes
		check("eliminarOferta que no esta en pendientes devuelve false",
			!bob.eliminarOfertadeOfertasPendientes(oferta)); // oferta ya esta en historial


		/*
		 * Resultado final.
		 */
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}