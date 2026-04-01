package pruebas;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class PruebaMotorEstadistico {

	static int correctos = 0;
	static int fallos = 0;

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
		 * Montamos la tienda con todos los datos necesarios para la prueba.
		 * Creamos un empleado tasador, cuatro clientes con distinto nivel de
		 * actividad, productos de venta, pedidos (algunos cancelados),
		 * productos de segunda mano tasados e intercambios finalizados.
		 * El gestor ya existe porque la Tienda lo crea en su constructor.
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		// El gestor ya existe en la tienda, lo recuperamos
		Gestor gestor = null;
		for (UsuarioRegistrado u : tienda.getUsuarios()) {
			if (u instanceof Gestor) {
				gestor = (Gestor) u;
				break;
			}
		}

		Empleado tasador = new Empleado("tasador", "pass");
		tasador.asignarPermiso(TipoPermisos.VALORACION_PRODUCTOS);
		tienda.getUsuarios().add(tasador);

		// alice:  3 pedidos validos + 1 cancelado, 2 intercambios
		// bob:    2 pedidos validos,                1 intercambio
		// carlos: 1 pedido  valido,                 1 intercambio
		// diana:  0 pedidos validos + 1 cancelado,  0 intercambios
		Cliente alice  = new Cliente("alice",  "p1", "11111111A");
		Cliente bob    = new Cliente("bob",    "p2", "22222222B");
		Cliente carlos = new Cliente("carlos", "p3", "33333333C");
		Cliente diana  = new Cliente("diana",  "p4", "44444444D");
		tienda.getUsuarios().add(alice);
		tienda.getUsuarios().add(bob);
		tienda.getUsuarios().add(carlos);
		tienda.getUsuarios().add(diana);

		Comic     comic1  = new Comic    ("Saga Vol.1", "Sci-fi",      "saga.png",  12.50, 20, 200, "Image", 2012);
		Comic     comic2  = new Comic    ("Watchmen",   "Clasico",     "watch.png", 15.00, 10, 400, "DC",    1987);
		Figura    figura1 = new Figura   ("Goku SSJ",   "Dragon Ball", "goku.png",  35.00, 15, 20, 15, 12, "PVC", "Bandai");
		JuegoMesa jm1     = new JuegoMesa("Catan",      "Estrategia",  "catan.png", 45.00, 12, 2, 4, 8, 99, "Eurogame");
		JuegoMesa jm2     = new JuegoMesa("Pandemic",   "Cooperativo", "pand.png",  38.00, 10, 2, 4, 8, 99, "Cooperativo");
		tienda.añadirProducto(comic1);
		tienda.añadirProducto(comic2);
		tienda.añadirProducto(figura1);
		tienda.añadirProducto(jm1);
		tienda.añadirProducto(jm2);

		// Pedidos validos: alice 25+35+45=105, bob 15+38=53, carlos 12.50 -> total ventas 170.50
		Carrito ca1 = new Carrito(alice);  ca1.añadirProducto(comic1,  2); Pedido pa1 = new Pedido(alice,  ca1); alice.getHistorialPedidos().add(pa1);  tienda.registrarVenta(pa1);
		Carrito ca2 = new Carrito(alice);  ca2.añadirProducto(figura1, 1); Pedido pa2 = new Pedido(alice,  ca2); alice.getHistorialPedidos().add(pa2);  tienda.registrarVenta(pa2);
		Carrito ca3 = new Carrito(alice);  ca3.añadirProducto(jm1,     1); Pedido pa3 = new Pedido(alice,  ca3); alice.getHistorialPedidos().add(pa3);  tienda.registrarVenta(pa3);
		Carrito cb1 = new Carrito(bob);    cb1.añadirProducto(comic2,  1); Pedido pb1 = new Pedido(bob,    cb1); bob.getHistorialPedidos().add(pb1);    tienda.registrarVenta(pb1);
		Carrito cb2 = new Carrito(bob);    cb2.añadirProducto(jm2,     1); Pedido pb2 = new Pedido(bob,    cb2); bob.getHistorialPedidos().add(pb2);    tienda.registrarVenta(pb2);
		Carrito cc1 = new Carrito(carlos); cc1.añadirProducto(comic1,  1); Pedido pc1 = new Pedido(carlos, cc1); carlos.getHistorialPedidos().add(pc1); tienda.registrarVenta(pc1);

		// Pedidos cancelados
		Carrito cac = new Carrito(alice); cac.añadirProducto(comic2, 1); Pedido pac = new Pedido(alice, cac); alice.getHistorialPedidos().add(pac); tienda.registrarVenta(pac); pac.cancelarPedido();
		Carrito cdc = new Carrito(diana); cdc.añadirProducto(comic1, 1); Pedido pdc = new Pedido(diana, cdc); diana.getHistorialPedidos().add(pdc); tienda.registrarVenta(pdc); pdc.cancelarPedido();

		// Tasaciones: 5+8 en catalogo, 3.50 en pendientes -> total 16.50
		Producto2Mano p2m_alice1 = new Producto2Mano(alice,  "Naruto Vol.3",    "Usado",           "naruto.png");
		Producto2Mano p2m_alice2 = new Producto2Mano(alice,  "Figura Pikachu",  "Buen estado",     "pika.png");
		Producto2Mano p2m_bob    = new Producto2Mano(bob,    "One Piece Vol.1", "Algo desgastado", "op.png");
		Producto2Mano p2m_carlos = new Producto2Mano(carlos, "DBZ Manga",       "Bueno",           "dbz.png");
		p2m_alice1.valorar(5.00, EstadoProducto.MUY_BUENO, tasador);
		p2m_alice2.valorar(8.00, EstadoProducto.MUY_BUENO, tasador);
		p2m_bob.valorar(3.50,    EstadoProducto.MUY_BUENO, tasador);
		p2m_carlos.valorar(4.00, EstadoProducto.MUY_BUENO, tasador);
		tienda.publicarParaIntercambio(p2m_alice1);
		tienda.publicarParaIntercambio(p2m_alice2);
		tienda.getPendientesTasacion().add(p2m_bob);

		// Intercambios: alice<->bob (1), alice<->carlos (2)
		alice.getCarteraIntercambio().add(p2m_alice1);
		alice.getCarteraIntercambio().add(p2m_alice2);
		bob.getCarteraIntercambio().add(p2m_bob);
		carlos.getCarteraIntercambio().add(p2m_carlos);
		p2m_alice1.setBloqueado(false);
		Oferta oferta1 = new Oferta(alice, bob, Arrays.asList(p2m_alice1), Arrays.asList(p2m_bob));
		oferta1.aceptarOferta();
		tienda.registrarIntercambioFinalizado(oferta1);
		p2m_alice2.setBloqueado(false);
		Oferta oferta2 = new Oferta(alice, carlos, Arrays.asList(p2m_alice2), Arrays.asList(p2m_carlos));
		oferta2.aceptarOferta();
		tienda.registrarIntercambioFinalizado(oferta2);

		LocalDate hoy        = LocalDate.now();
		int       anioActual = hoy.getYear();


		/*
		 * Comprobamos que el ranking de compras ordena correctamente los clientes
		 * segun sus pedidos validos (PAGADO, LISTO_PARA_RECOGER, ENTREGADO).
		 * Los pedidos cancelados no deben contar. Diana tiene solo cancelados
		 * y debe quedar ultima.
		 */
		System.out.println("\n============= verClientesTopCompras =============");

		List<Cliente> topCompras = gestor.verClientesTopCompras();

		check("La lista no es null", topCompras != null);
		check("Hay 4 clientes en el ranking", topCompras.size() == 4);
		check("alice lidera con 3 pedidos validos", topCompras.get(0).getNickname().equals("alice"));
		check("bob es segundo con 2 pedidos", topCompras.get(1).getNickname().equals("bob"));
		check("carlos es tercero con 1 pedido", topCompras.get(2).getNickname().equals("carlos"));
		check("diana es ultima con 0 pedidos validos", topCompras.get(3).getNickname().equals("diana"));


		/*
		 * Comprobamos que el ranking de intercambios ordena segun el numero
		 * de intercambios finalizados en los que ha participado cada cliente,
		 * ya sea como origen o como destino.
		 */
		System.out.println("\n============= verClientesTopIntercambios =============");

		List<Cliente> topIntercambios = gestor.verClientesTopIntercambios();

		check("La lista no es null", topIntercambios != null);
		check("Hay 4 clientes en el ranking", topIntercambios.size() == 4);
		check("alice lidera con 2 intercambios", topIntercambios.get(0).getNickname().equals("alice"));
		check("diana es ultima con 0 intercambios", topIntercambios.get(topIntercambios.size() - 1).getNickname().equals("diana"));


		/*
		 * Comprobamos que el ranking de pedidos cancelados ordena correctamente.
		 * Alice y diana tienen 1 cancelado cada una, bob y carlos tienen 0.
		 */
		System.out.println("\n============= verClientesConMasPedidosCancelados =============");

		List<Cliente> topCancelados = gestor.verClientesConMasPedidosCancelados();

		check("La lista no es null", topCancelados != null);
		check("Hay 4 clientes en el ranking", topCancelados.size() == 4);
		check("El primero tiene 1 pedido cancelado",
			topCancelados.get(0).getHistorialPedidos().stream()
				.filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count() == 1);
		check("bob tiene 0 cancelados",
			bob.getHistorialPedidos().stream()
				.filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count() == 0);
		check("carlos tiene 0 cancelados",
			carlos.getHistorialPedidos().stream()
				.filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count() == 0);


		/*
		 * Comprobamos los ingresos totales por ventas (pedidos), sin contar tasaciones.
		 * alice 25+35+45=105, bob 15+38=53, carlos 12.50 -> 170.50
		 */
		System.out.println("\n============= consultarIngresosVenta =============");

		double ingresosVenta = gestor.consultarIngresosVenta();

		check("Ingresos venta mayores que 0", ingresosVenta > 0);
		check("Ingresos venta = 170.50", ingresosVenta == 170.50);


		/*
		 * Comprobamos los ingresos por tasacion. El metodo usa nTasacionesCobradas
		 * multiplicado por el precio de tasacion configurado en la tienda (10 euros por defecto).
		 */
		System.out.println("\n============= consultarIngresosTasacion =============");

		double ingresosTasacion = gestor.consultarIngresosTasacion();

		check("Ingresos tasacion mayor o igual que 0", ingresosTasacion >= 0);


		/*
		 * Comprobamos calcularIngresosRangoFechas, que suma ventas y tasaciones
		 * del rango. Como todos los datos son de hoy, el rango del anio completo
		 * y el de un solo dia (hoy) deben dar el mismo resultado: 187.00.
		 * Un rango sin actividad debe devolver 0.0.
		 */
		System.out.println("\n============= consultarIngresosRango =============");

		check("Rango anio completo = ventas (170.50) + tasaciones en rango (16.50) = 187.00",
			gestor.consultarIngresosRango(
				LocalDate.of(anioActual, 1, 1),
				LocalDate.of(anioActual, 12, 31)) == 187.00);
		check("Rango de un solo dia (hoy) = 187.00",
			gestor.consultarIngresosRango(hoy, hoy) == 187.00);
		check("Rango anio anterior (sin actividad) = 0.0",
			gestor.consultarIngresosRango(
				LocalDate.of(anioActual - 1, 1, 1),
				LocalDate.of(anioActual - 1, 12, 31)) == 0.0);
		check("Rango futuro (2099) = 0.0",
			gestor.consultarIngresosRango(
				LocalDate.of(2099, 1, 1),
				LocalDate.of(2099, 12, 31)) == 0.0);
		check("inicio null devuelve 0.0",  gestor.consultarIngresosRango(null, hoy) == 0.0);
		check("fin null devuelve 0.0",     gestor.consultarIngresosRango(hoy, null) == 0.0);
		check("fin < inicio devuelve 0.0",
			gestor.consultarIngresosRango(
				LocalDate.of(2025, 6, 1),
				LocalDate.of(2025, 1, 1)) == 0.0);
		check("inicio == fin (hoy) es valido", gestor.consultarIngresosRango(hoy, hoy) > 0.0);


		/*
		 * Comprobamos que consultarIngresosPorMesesActual devuelve un array de 12
		 * posiciones donde solo el mes actual tiene ingresos, ya que todos los
		 * pedidos y tasaciones son de hoy. La suma de los 12 meses debe coincidir
		 * con el total del anio.
		 */
		System.out.println("\n============= consultarIngresosPorMesesActual =============");

		double[] porMesActual = gestor.consultarIngresosPorMesesActual();
		int mesIdx = hoy.getMonthValue() - 1;

		check("Array tiene 12 posiciones", porMesActual != null && porMesActual.length == 12);
		check("Mes actual tiene 187.00", porMesActual[mesIdx] == 187.00);

		double sumaMeses = 0.0;
		for (double v : porMesActual) sumaMeses += v;
		check("Suma de los 12 meses = 187.00", sumaMeses == 187.00);

		boolean otrosMesesVacios = true;
		for (int i = 0; i < 12; i++) {
			if (i != mesIdx && porMesActual[i] != 0.0) { otrosMesesVacios = false; break; }
		}
		check("El resto de meses tienen 0.0", otrosMesesVacios);


		/*
		 * Comprobamos consultarIngresosPorMeses con un anio concreto.
		 * Con el anio actual debe dar el mismo resultado que el metodo anterior.
		 * Con el anio anterior y el 2099 todos los meses deben ser 0.
		 * Con un anio invalido (<= 0) debe devolver un array de 12 ceros.
		 */
		System.out.println("\n============= consultarIngresosPorMeses (año dado) =============");

		double[] porMesAnioActual = gestor.consultarIngresosPorMeses(anioActual);

		check("Array del anio actual tiene 12 posiciones", porMesAnioActual != null && porMesAnioActual.length == 12);
		check("consultarIngresosPorMeses(anioActual) coincide con consultarIngresosPorMesesActual()",
			Arrays.equals(porMesActual, porMesAnioActual));

		double[] porMesAnioAnterior = gestor.consultarIngresosPorMeses(anioActual - 1);
		boolean anioAnteriorVacio = true;
		for (double v : porMesAnioAnterior) { if (v != 0.0) { anioAnteriorVacio = false; break; } }
		check("Todos los meses del anio anterior son 0.0", anioAnteriorVacio);

		double[] porMes2099 = gestor.consultarIngresosPorMeses(2099);
		boolean anio2099Vacio = true;
		for (double v : porMes2099) { if (v != 0.0) { anio2099Vacio = false; break; } }
		check("Todos los meses de 2099 son 0.0", anio2099Vacio);

		double[] arrayInvalido = gestor.consultarIngresosPorMeses(0);
		check("Anio 0 devuelve array de 12 posiciones", arrayInvalido.length == 12);
		boolean todosCeros = true;
		for (double v : arrayInvalido) { if (v != 0.0) { todosCeros = false; break; } }
		check("Anio 0 devuelve todos 0.0", todosCeros);

		double[] arrayNegativo = gestor.consultarIngresosPorMeses(-99);
		boolean negativosCeros = true;
		for (double v : arrayNegativo) { if (v != 0.0) { negativosCeros = false; break; } }
		check("Anio negativo devuelve todos 0.0", negativosCeros);


		/*
		 * Imprimimos el resultado del test.
		 */
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}