package tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import Excepcion.*;
import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class TestCliente {
	private Tienda tienda;
	private Cliente alice;
	private Cliente bob;
	private Empleado tasador;
	private Gestor gestor;

	@BeforeEach
	void setUp() {
		tienda = Tienda.getInstancia();
		tienda.vaciarTienda();
		tienda.registrarNuevoCliente("alice", "Alice@1234", "11111111A");
		tienda.registrarNuevoCliente("bob", "Bob@1234", "22222222B");
		alice = tienda.loginCliente("alice", "Alice@1234");
		bob = tienda.loginCliente("bob", "Bob@1234");

		Gestor gestor = tienda.getGestor();
		gestor.configurarTiemposSistema(60, 60, 60);
		gestor.darDeAltaEmpleados_Permisos("tasador", "Tasador@1",
				List.of(TipoPermisos.VALORACION_PRODUCTOS, TipoPermisos.GESTION_STOCK));
		tasador = tienda.loginEmpleado("tasador", "Tasador@1");

	}

	@Test
	@DisplayName("El metodo registrarNuevoCliente crea el cliente correctamente")
	void testRegistro() {
		tienda.registrarNuevoCliente("carlos", "Carlos@123", "33333333C");
		assertNotNull(tienda.buscarClientePorNickname("carlos"));
	}

	@Test
	@DisplayName("El metodo registrarNuevoCliente con DNI duplicado devuelve null")
	void testRegistroDniDuplicado() {
		assertNull(tienda.registrarNuevoCliente("otro", "Otro@1234", "11111111A"));
	}

	@Test
	@DisplayName("El metodo registrarNuevoCliente con nickname duplicado devuelve null")
	void testRegistroNicknameDuplicado() {
		assertNull(tienda.registrarNuevoCliente("alice", "Alice@9999", "99999999Z"));
	}

	@Test
	@DisplayName("El metodo registrarNuevoCliente con DNI invalido devuelve null")
	void testRegistroDniInvalido() {
		assertNull(tienda.registrarNuevoCliente("nuevo", "Nuevo@1234", "INVALIDO"));
	}

	@Test
	@DisplayName("El metodo loginCliente con credenciales correctas devuelve el cliente")
	void testLoginCorrecto() {
		assertNotNull(alice);
		assertEquals("alice", alice.getNickname());
	}

	@Test
	@DisplayName("El metodo loginCliente con password incorrecta devuelve null")
	void testLoginIncorrecto() {
		assertNull(tienda.loginCliente("alice", "wrongpass"));
	}

	@Test
	@DisplayName("El metodo añadirProductoCarrito con producto valido creado por el empleado funciona")
	void testAñadirProductoCarritoOk() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		assertTrue(alice.añadirProductoCarrito(p, 1));
		assertNotNull(alice.getCarritoActual());
	}

	@Test
	@DisplayName("El metodo añadirProductoCarrito con producto null devuelve false")
	void testAñadirProductoCarritoNull() {
		assertFalse(alice.añadirProductoCarrito(null, 1));
	}

	@Test
	@DisplayName("El metodo añadirProductoCarrito con cantidad negativa devuelve false")
	void testAñadirProductoCarritoCantidadNegativa() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		assertFalse(alice.añadirProductoCarrito(p, -1));
	}

	@Test
	@DisplayName("El metodo añadirProductoCarrito con stock insuficiente devuelve false")
	void testAñadirProductoCarritoSinStock() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 2, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);// Creamos un carrito con stock 2
		ProductoVenta p = tienda.getStockVentas().get(0);
		assertFalse(alice.añadirProductoCarrito(p, 99));// pedimos 99. Como solo hay 2 tiene que dar error.
	}

	@Test
	@DisplayName("El metodo reservarCarrito sin productos devuelve false")
	void testReservarCarritoVacio() {
		assertFalse(alice.reservarCarrito());
	}

	@Test
	@DisplayName("El metodo reservarCarrito con productos crea pedido y vacia el carrito")
	void testReservarCarritoOk() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		alice.añadirProductoCarrito(p, 1);
		assertTrue(alice.reservarCarrito());
		assertEquals(1, alice.getHistorialPedidos().size());
		assertNull(alice.getCarritoActual());// UNa vez que hemos pedido el carrito se pone a null
	}

	// metodos de pago al usar la libreria que nos proporcionan no podemos saber si
	// se crean correctamente o no
	@Test
	@DisplayName("El metodo pagarCarrito con pedido null devuelve false")
	void testPagarCarritoPedidoNull() {
		assertFalse(alice.pagarCarrito(null, "1234567890123456",
				new java.sql.Date(System.currentTimeMillis() + 100000000L), 123));
	}

	@Test
	@DisplayName("El metodo pagarCarrito con pedido que no pertenece a un usuario devuelve false(no puedes pagar algo que no es tuyo)")
	void testPagarCarritoPedidoAjeno() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		bob.añadirProductoCarrito(p, 1);
		bob.reservarCarrito();
		Pedido pedidoBob = bob.getHistorialPedidos().get(0);

		assertFalse(alice.pagarCarrito(pedidoBob, "1234567890123456",
				new java.sql.Date(System.currentTimeMillis() + 100000000L), 123));
	}

	@Test
	@DisplayName("El metodo pagarCarrito con pedido cancelado devuelve false")
	void testPagarCarritoPedidoCancelado() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		alice.añadirProductoCarrito(p, 1);
		alice.reservarCarrito();
		Pedido pedido = alice.getHistorialPedidos().get(0);
		pedido.cancelarPedido();

		assertFalse(alice.pagarCarrito(pedido, "1234567890123456",
				new java.sql.Date(System.currentTimeMillis() + 100000000L), 123));
	}

	@Test
	@DisplayName("El metodo pagarCarrito con tarjeta de longitud incorrecta devuelve false")
	void testPagarCarritoTarjetaInvalida() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		alice.añadirProductoCarrito(p, 1);
		alice.reservarCarrito();
		Pedido pedido = alice.getHistorialPedidos().get(0);

		// Tarjeta con menos de 16 digitos — falla en Pago antes de llamar a TeleCharge
		assertFalse(alice.pagarCarrito(pedido, "123", new java.sql.Date(System.currentTimeMillis() + 100000000L), 123));
	}

	@Test
	@DisplayName("El metodo pagarCarrito con tarjeta caducada devuelve false")
	void testPagarCarritoTarjetaCaducada() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		alice.añadirProductoCarrito(p, 1);
		alice.reservarCarrito();
		Pedido pedido = alice.getHistorialPedidos().get(0);

		// Fecha en el pasado — falla en Pago antes de llamar a TeleCharge
		assertFalse(alice.pagarCarrito(pedido, "1234567890123456",
				new java.sql.Date(System.currentTimeMillis() - 100000000L), 123));
	}

	@Test
	@DisplayName("El metodo subirProducto añade el producto a la cartera del cliente")
	void testSubirProducto() {
		alice.subirProducto("Figura Goku", "desc", "img.png");
		assertEquals(1, alice.getCarteraIntercambio().size());
	}

	@Test
	@DisplayName("Metodo para comprobar que el producto subido empieza bloqueado, no visible y sin valoracion")
	void testProductoSubidoEstadoInicial() {
		alice.subirProducto("Figura Goku", "desc", "img.png");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		assertTrue(p.isBloqueado());
		assertFalse(p.isVisible());
		assertNull(p.getValoracion());
	}

	@Test
	@DisplayName("El metodo tieneProductoenSuCartera devuelve true si el producto pertenece al cliente")
	void testTieneProductoEnCartera() {
		alice.subirProducto("Figura", "desc", "img.png");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		assertTrue(alice.tieneProductoenSuCartera(p));
	}

	@Test
	@DisplayName("El metodo tieneProductoenSuCartera devuelve false con null")
	void testTieneProductoEnCarteraNull() {
		assertFalse(alice.tieneProductoenSuCartera(null));
	}

	@Test
	@DisplayName("El metodo verCarteraCliente devuelve solo productos visibles y no bloqueados de otro cliente")
	void testVerCarteraCliente() {
		bob.subirProducto("Comic", "desc", "img.png");
		Producto2Mano p = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(p);
		tasador.tasarProducto(p.getId(), 10.0, EstadoProducto.MUY_BUENO);

		List<Producto2Mano> cartera = alice.verCarteraCliente("bob");
		assertTrue(cartera.contains(p));
	}

	@Test
	@DisplayName("El metodo verCarteraCliente con nickname null devuelve null")
	void testVerCarteraClienteNull() {
		assertNull(alice.verCarteraCliente(null));
	}

	@Test
	@DisplayName("El metodo verCarteraCliente con propio nickname devuelve lista vacia")
	void testVerCarteraClientePropioNickname() {
		assertTrue(alice.verCarteraCliente("alice").isEmpty());
	}

	@Test
	@DisplayName("El metodo solicitarTasacion con producto null devuelve false")
	void testSolicitarTasacionNull() {
		assertFalse(alice.solicitarTasacion(null, "1234567890123456", 123,
				new java.sql.Date(System.currentTimeMillis() + 100000000L)));
	}

	@Test
	@DisplayName("solicitarTasacion con producto que no es tuyo devuelve false")
	void testSolicitarTasacionProductoAjeno() {
		bob.subirProducto("Comic", "desc", "img.png");
		Producto2Mano p = bob.getCarteraIntercambio().get(0);

		assertFalse(alice.solicitarTasacion(p, "1234567890123456", 123,
				new java.sql.Date(System.currentTimeMillis() + 100000000L)));
	}

	@Test
	@DisplayName("solicitarTasacion con producto ya tasado devuelve false")
	void testSolicitarTasacionYaTasado() {
		alice.subirProducto("Figura", "desc", "img.png");
		Producto2Mano p = alice.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(p);
		tasador.tasarProducto(p.getId(), 10.0, EstadoProducto.MUY_BUENO);
		// Ya esta tasado y visible
		assertFalse(alice.solicitarTasacion(p, "1234567890123456", 123,
				new java.sql.Date(System.currentTimeMillis() + 100000000L)));
	}

	@Test
	@DisplayName("solicitarRecogidaPedido con codigo null lanza NullPointerException")
	void testSolicitarRecogidaCodigoNull() {
		assertThrows(NullPointerException.class, () -> alice.solicitarRecogidaPedido(null));
	}

	@Test
	@DisplayName("solicitarRecogidaPedido con codigo incorrecto devuelve false")
	void testSolicitarRecogidaCodigoIncorrecto() {
		assertFalse(alice.solicitarRecogidaPedido("CODIGO_FALSO"));
	}

	@Test
	@DisplayName("solicitarRecogidaPedido con pedido en PENDIENTE_PAGO devuelve false porque codigoRecogida es null")
	void testSolicitarRecogidaPedidoNoPagado() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		alice.añadirProductoCarrito(p, 1);
		alice.reservarCarrito();

		assertFalse(alice.solicitarRecogidaPedido("CODIGO_FALSO"));
	}

	@Test
	@DisplayName("solicitarRecogidaPedido del pedido de otro cliente devuelve false")
	void testSolicitarRecogidaPedidoAjeno() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		bob.añadirProductoCarrito(p, 1);
		bob.reservarCarrito();

		// Alice intenta recoger con un codigo que no es suyo
		assertFalse(alice.solicitarRecogidaPedido("CODIGO_FALSO"));
	}

	@Test
	@DisplayName("solicitarRecogidaPedido con pedido LISTO_PARA_RECOGER devuelve true")
	void testSolicitarRecogidaOk() {
		tasador.añadirProducto_nuevo("C", "Saga", "desc", "img", 10.0, 5, new ArrayList<>(), 100, "Ed", 2020, 0, 0, 0,
				null, null, 0, 0, 0, 0, null);
		ProductoVenta p = tienda.getStockVentas().get(0);
		alice.añadirProductoCarrito(p, 1);
		alice.reservarCarrito();
		Pedido pedido = alice.getHistorialPedidos().get(0);

		// Forzamos el estado sin pasar por TeleCharge
		pedido.setEstado(EstadoPedido.LISTO_PARA_RECOGER);
		pedido.setCodigoRecogida("PICK-TEST");

		assertTrue(alice.solicitarRecogidaPedido("PICK-TEST"));
		assertTrue(pedido.isRecogida_solicitada());
	}

	@Test
	@DisplayName("proponerOferta con productos tasados devuelve true y bloquea el producto")
	void testProponerOfertaOk() {
		alice.subirProducto("A", "desc", "img.png");
		bob.subirProducto("B", "desc", "img.png");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tienda.solicitarTasacion(pa);
		tienda.solicitarTasacion(pb);
		tasador.tasarProducto(pa.getId(), 10.0, EstadoProducto.MUY_BUENO);
		tasador.tasarProducto(pb.getId(), 10.0, EstadoProducto.MUY_BUENO);

		List<Producto2Mano> misProductos = new ArrayList<>();
		List<Producto2Mano> susProductos = new ArrayList<>();

		misProductos.add(pa);
		susProductos.add(pb);

		boolean result = alice.proponerOferta(bob, misProductos, susProductos);

		assertTrue(result);
		assertTrue(pa.isBloqueado());
		assertFalse(alice.getOfertasPendientes().isEmpty());
		assertFalse(bob.getOfertasPendientes().isEmpty());
	}

	@Test
	@DisplayName("El metodo proponerOferta a si mismo(mismo cliente) devuelve false")
	void testProponerOfertaASiMismo() {

		alice.subirProducto("A", "desc", "img.png");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		String id = pa.getId();
		tasador.tasarProducto(id, 10, EstadoProducto.MUY_BUENO);

		List<Producto2Mano> misProductos = new ArrayList<>();
		misProductos.add(pa);

		assertFalse(alice.proponerOferta(alice, misProductos, misProductos));
	}

	@Test
	@DisplayName("El metodo proponerOferta con destinatario null devuelve false")
	void testProponerOfertaDestinatarioNull() {

		alice.subirProducto("A", "desc", "img.png");
		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		tasador.tasarProducto(pa.getId(), 10.0, EstadoProducto.MUY_BUENO);
		List<Producto2Mano> misProductos = new ArrayList<>();
		misProductos.add(pa);

		assertFalse(alice.proponerOferta(null, misProductos, new ArrayList<>()));
	}

	@Test
	@DisplayName("El metodo proponerOferta sin productos ofrecidos devuelve false")
	void testProponerOfertaListaVacia() {

		bob.subirProducto("B", "desc", "img.png");
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tasador.tasarProducto(pb.getId(), 10.0, EstadoProducto.MUY_BUENO);
		List<Producto2Mano> susProductos = new ArrayList<>();
		susProductos.add(pb);
		assertFalse(alice.proponerOferta(bob, new ArrayList<>(), susProductos));
	}

	@Test
	@DisplayName("El metodo proponerOferta bloquea los productos ofertados")
	void testProponerOfertaBloqueaProductos() {

		alice.subirProducto("A", "desc", "img.png");
		bob.subirProducto("B", "desc", "img.png");

		Producto2Mano pa = alice.getCarteraIntercambio().get(0);
		Producto2Mano pb = bob.getCarteraIntercambio().get(0);
		tasador.tasarProducto(pa.getId(), 10.0, EstadoProducto.MUY_BUENO);
		tasador.tasarProducto(pb.getId(), 10.0, EstadoProducto.MUY_BUENO);

		List<Producto2Mano> misP = new ArrayList<>();
		misP.add(pa);
		List<Producto2Mano> susP = new ArrayList<>();
		susP.add(pb);

		alice.proponerOferta(bob, misP, susP);
		assertTrue(pa.isBloqueado(), "El producto ofrecido debe quedar bloqueado");
	}

	@Test
	@DisplayName("El metodo eliminarOfertadeOfertasPendientes(rechaazar oferta) retira la oferta y desbloquea")
	void testEliminarOfertaPendiente() {
	    alice.subirProducto("A", "desc", "img.png");
	    bob.subirProducto("B", "desc", "img.png");
	    Producto2Mano pa = alice.getCarteraIntercambio().get(0);
	    Producto2Mano pb = bob.getCarteraIntercambio().get(0);

	    // Tasamos los productos para que se puedan ofertar
	    tienda.solicitarTasacion(pa);
	    tienda.solicitarTasacion(pb);
	    tasador.tasarProducto(pa.getId(), 10.0, EstadoProducto.MUY_BUENO);
	    tasador.tasarProducto(pb.getId(), 10.0, EstadoProducto.MUY_BUENO);

	    // Comprobamos que los productos siguen en la cartera tras tasar
	    assertTrue(alice.tieneProductoenSuCartera(pa));
	    assertTrue(bob.tieneProductoenSuCartera(pb));

	    // Alice propone la oferta a bob
	    List<Producto2Mano> misProductos = new ArrayList<>();
	    misProductos.add(pa);
	    List<Producto2Mano> susProductos = new ArrayList<>();
	    susProductos.add(pb);
	    boolean propuesta = alice.proponerOferta(bob, misProductos, susProductos);
	    assertTrue(propuesta);

	    // Bob la rechaza
	    Oferta o = bob.getOfertasParaDecidir().get(0);
	    assertTrue(bob.eliminarOfertadeOfertasPendientes(o));
	    assertFalse(pa.isBloqueado());
	    assertTrue(bob.getOfertasPendientes().isEmpty());
	    assertTrue(alice.getOfertasPendientes().isEmpty());
	}

	@Test
	@DisplayName("EL metodo recibirNotificacionTipo obligatoria siempre llega")
	void testNotificacionObligatoriaLlega() {
		alice.recibirNotificacionTipo("msg", TipoNotificacion.PAGO_EXITOSO);
		assertEquals(1, alice.getNotificaciones().size());
	}

	@Test
	@DisplayName("El metodo recibirNotificacionTipo EMPLEADOS nunca llega a cliente")
	void testNotificacionEmpleadosNoLlega() {
		alice.recibirNotificacionTipo("msg", TipoNotificacion.EMPLEADOS);
		assertTrue(alice.getNotificaciones().isEmpty());
	}

	@Test
	@DisplayName("El metodo getNotificacionesNoLeidas filtra correctamente")
	void testGetNotificacionesNoLeidas() {
		alice.recibirNotificacionTipo("msg1", TipoNotificacion.PAGO_EXITOSO);
		alice.recibirNotificacionTipo("msg2", TipoNotificacion.PAGO_EXITOSO);
		alice.getNotificaciones().get(0).marcarComoLeida();
		assertEquals(1, alice.getNotificacionesNoLeidas().size());
	}

	@Test
	@DisplayName("El metodo configurarPreferenciaNotificacion desactiva notificacion configurable")
	void testConfigurarPreferenciaDesactivar() {
		alice.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, false);
		alice.recibirNotificacionTipo("desc", TipoNotificacion.DESCUENTO);
		assertTrue(alice.getNotificaciones().isEmpty());
	}

	@Test
	@DisplayName("El metodo configurarPreferenciaNotificacion con tipo obligatorio devuelve false")
	void testConfigurarPreferenciaObligatoriaDevuelveFalse() {
		assertFalse(alice.configurarPreferenciaNotificacion(TipoNotificacion.PAGO_EXITOSO, false));
	}

	@Test
	@DisplayName("El metodo añadirCategoriaInteresParaRecibirInfo con categoria existente devuelve true")
	void testAñadirCategoriaInteresOk() {
		gestor.crearCategoria("Comics", "desc");
		assertTrue(alice.añadirCategoriaInteresParaRecibirInfo("Comics"));
	}

	@Test
	@DisplayName("El metodo añadirCategoriaInteresParaRecibirInfo con categoria inexistente devuelve false")
	void testAñadirCategoriaInteresNoExiste() {
		assertFalse(alice.añadirCategoriaInteresParaRecibirInfo("CategoriaFalsa"));
	}

	@Test
	@DisplayName("El metodo añadirCategoriaInteresParaRecibirInfo con null devuelve false")
	void testAñadirCategoriaInteresNull() {
		assertFalse(alice.añadirCategoriaInteresParaRecibirInfo(null));
	}

	@Test
	@DisplayName("EL metodo modificarPerfil con datos validos devuelve true")
	void testModificarPerfilOk() {
		assertTrue(alice.modificarPerfil("alicia", "Alicia@5678"));
		assertEquals("alicia", alice.getNickname());
	}

	@Test
	@DisplayName("El metodo modificarPerfil con nickname ya existente devuelve false")
	void testModificarPerfilNicknameOcupado() {
		assertFalse(alice.modificarPerfil("bob", "Alice@5678"));
	}

	@Test
	@DisplayName("EL metodo modificarPerfil con nickname null devuelve false")
	void testModificarPerfilNicknameNull() {
		assertFalse(alice.modificarPerfil(null, "Alice@5678"));
	}

	@Test
	@DisplayName("El metodo modificarPerfil con password invalida devuelve false")
	void testModificarPerfilPasswordInvalida() {
		assertFalse(alice.modificarPerfil("alicia", "1234"));
	}
}
