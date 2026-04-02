package pruebas;

import java.util.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class PruebaRecomendador {

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
		 * Montamos la tienda con productos, categorias, clientes y pedidos El gestor ya
		 * existe en la tienda (lo crea el constructor de Tienda). -Valoraciones a poner
		 * comic1=9, comic2=7 -> Comics figura1=8, figura2=6 -> Figuras jm1=5 -> Juegos
		 * 
		 * alice: compro comic1 -> categoria favorita Comics bob: compro comic1 y
		 * figura1 (tiene comic1 en comun con alice) carlos: sin historial (caso borde)
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();
		Recomendador rec = tienda.getRecomendador();

		// recordar q el gestor se crea solo, lo buscamos
		Gestor gestor = null;
		for (UsuarioRegistrado u : tienda.getUsuarios()) {
			if (u instanceof Gestor) {
				gestor = (Gestor) u;
				break;
			}
		}

		// el gestor configura los tiempos del sistema (necesario para el carrito)
		gestor.setTiemposSistema(60, 60, 60);

		// el gestor crea un empleado q pueda tasar
		List<TipoPermisos> permisosT = new ArrayList<>();
		permisosT.add(TipoPermisos.VALORACION_PRODUCTOS);
		permisosT.add(TipoPermisos.GESTION_STOCK);
		gestor.darDeAltaEmpleados_Permisos("tasador", "Tasador@1", permisosT);
		Empleado tasador = tienda.loginEmpleado("tasador", "Tasador@1");

		// ctegorias
		Categoria catComics = new Categoria("Comics", "desc");
		Categoria catFiguras = new Categoria("Figuras", "desc");
		Categoria catJuegos = new Categoria("Juegos", "desc");
		tienda.getCategorias().add(catComics);
		tienda.getCategorias().add(catFiguras);
		tienda.getCategorias().add(catJuegos);

		// el empleado sube los productos
		ArrayList<Categoria> catsComics  = new ArrayList<>(Arrays.asList(catComics));
		ArrayList<Categoria> catsFiguras = new ArrayList<>(Arrays.asList(catFiguras));
		ArrayList<Categoria> catsJuegos  = new ArrayList<>(Arrays.asList(catJuegos));

		tasador.añadirProducto_nuevo("C", "Saga Vol.1", "desc", "img", 12.50, 20, catsComics,  200, "Image",   2012, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		tasador.añadirProducto_nuevo("C", "Watchmen",   "desc", "img", 15.00, 10, catsComics,  400, "DC",      1987, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		tasador.añadirProducto_nuevo("F", "Goku SSJ",   "desc", "img", 35.00, 15, catsFiguras, 0,   null,      0,    20, 15, 12, "PVC", "Bandai",   0, 0, 0, 0, null);
		tasador.añadirProducto_nuevo("F", "Link",       "desc", "img", 40.00,  8, catsFiguras, 0,   null,      0,    18, 12, 10, "PVC", "Nintendo", 0, 0, 0, 0, null);
		tasador.añadirProducto_nuevo("J", "Catan",      "desc", "img", 45.00, 12, catsJuegos,  0,   null,      0,    0,  0,  0,  null,  null,       2, 4, 8, 99, "Eurogame");

		ProductoVenta comic1  = tienda.getStockVentas().get(0);
		ProductoVenta comic2  = tienda.getStockVentas().get(1);
		ProductoVenta figura1 = tienda.getStockVentas().get(2);
		ProductoVenta figura2 = tienda.getStockVentas().get(3);
		ProductoVenta jm1     = tienda.getStockVentas().get(4);

		// clientes se registran y hacen login
		tienda.registrarNuevoCliente("alice",  "Alice@1234", "11111111A");
		tienda.registrarNuevoCliente("bob",    "Bob@1234",   "22222222B");
		tienda.registrarNuevoCliente("carlos", "Carlos@123", "33333333C");
		Cliente alice  = tienda.loginCliente("alice",  "Alice@1234");
		Cliente bob    = tienda.loginCliente("bob",    "Bob@1234");
		Cliente carlos = tienda.loginCliente("carlos", "Carlos@123");

		// Resenas: comic1=9, comic2=7, figura1=8, figura2=6, jm1=5
		new Reseña(alice, comic1, 9.0, "Genial");
		new Reseña(alice, comic2, 7.0, "Bien");
		new Reseña(bob, figura1, 8.0, "Muy bueno");
		new Reseña(bob, figura2, 6.0, "Regular");
		new Reseña(carlos, jm1, 5.0, "Pasable");

		// alice compro comic1 -> categoria favorita Comics
		alice.añadirProductoCarrito(comic1, 1);
		alice.reservarCarrito();
		Pedido pa = alice.getHistorialPedidos().get(0);
		alice.pagarCarrito(pa, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L), 123);

		// bob compro comic1 (en comun con alice) y figura1
		bob.añadirProductoCarrito(comic1, 1);
		bob.reservarCarrito();
		Pedido pb1 = bob.getHistorialPedidos().get(0);
		bob.pagarCarrito(pb1, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L), 123);

		bob.añadirProductoCarrito(figura1, 1);
		bob.reservarCarrito();
		Pedido pb2 = bob.getHistorialPedidos().get(1);
		bob.pagarCarrito(pb2, "1234567890123456", new java.sql.Date(System.currentTimeMillis() + 100000000L), 123);

		// carlos no hace nada

		/*
		 * Comprobamos el recomendador por valoracion. setPesos(1,0,0). comic1 esta
		 * excluido (alice ya lo compro). El primero debe ser figura1 con puntuacion
		 * 8.0.
		 */
		System.out.println("\n============= recomendarPorValoracion =============");

		rec.setPesos(1, 0, 0);
		List<ProductoVenta> sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		boolean resultado = true;

		for (Producto p : sugerencias) {
			if (p.getNombre().equals("Saga Vol.1")) {
				resultado = false;
				break;
			}
		}
		check("No incluye comic1 (ya comprado por alice)", resultado);
		check("El primero es figura1 (puntuacion 8.0, la mas alta disponible)",
				sugerencias.get(0).getNombre().equals("Goku SSJ"));
		check("No supera el limite maximo", sugerencias.size() <= rec.getLimiteMaximo());

		/*
		 * Comprobamos el recomendador por compras. Bob compro comic1 (en comun con
		 * alice) y figura1. Debe sugerirle figura1 a alice.
		 */
		System.out.println("\n============= recomendarPorCompras =============");

		rec.setPesos(0, 1, 0);
		sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		resultado = true;

		for (Producto p : sugerencias) {
			if (p.getNombre().equals("Saga Vol.1")) {
				resultado = false;
				break;
			}
		}
		check("No incluye comic1 (ya comprado por alice)", resultado);

		resultado = false;

		for (Producto p : sugerencias) {
			if (p.getNombre().equals("Goku SSJ")) {
				resultado = true;
				break;
			}
		}
		check("Incluye figura1 (bob la compro y tiene comic1 en comun con alice)", resultado);

		/*
		 * Comprobamos el recomendador por categorias. Categoria favorita de alice es
		 * Comics (solo compro comic1). Debe recomendarle comic2 y solo productos de
		 * Comics.
		 */
		System.out.println("\n============= recomendarPorCategorias =============");

		rec.setPesos(0, 0, 1);
		sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		resultado = false;

		for (Producto p : sugerencias) {
			if (p.getNombre().equals("Watchmen")) {
				resultado = true;
				break;
			}
		}
		check("Incluye comic2 (misma categoria favorita, no comprado)", resultado);

		resultado = true;

		for (Producto p : sugerencias) {
			if (p.getNombre().equals("Saga Vol.1")) {
				resultado = false;
				break;
			}
		}
		check("No incluye comic1 (ya comprado)", resultado);

		check("Solo incluye un elemento, comic2", sugerencias.size() == 1);

		/*
		 * Comprobamos el recomendador con los tres criterios activos.
		 */
		System.out.println("\n============= recomendador ponderado =============");

		rec.setPesos(1, 1, 1);
		sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		resultado = true;

		for (Producto p : sugerencias) {
			if (p.getNombre().equals("Saga Vol.1")) {
				resultado = false;
				break;
			}
		}
		check("No incluye comic1 (ya comprado)",
				resultado);
		check("No supera el limite maximo", sugerencias.size() <= rec.getLimiteMaximo());

		/*
		 * Caso extremo: carlos sin historial.
		 */
		System.out.println("\n============= carlos sin historial (caso extremo) =============");

		rec.setConfiguracion(rec.getLimiteMaximo(), true); 
		rec.setPesos(0, 0, 1);
		sugerencias = rec.generarSugerencias(carlos);
		check("Sin historial, recomendador por categorias devuelve lista vacia", sugerencias.isEmpty());

		rec.setPesos(1, 0, 0);
		sugerencias = rec.generarSugerencias(carlos);
		check("Sin historial, recomendador por valoracion devuelve productos", !sugerencias.isEmpty());
		check("Sin historial no hay excluidos, devuelve hasta el limite", sugerencias.size() <= rec.getLimiteMaximo());
		
		rec.setPesos(0,1,0);
		sugerencias = rec.generarSugerencias(carlos);
		check("Sin historial, recomendador por compras comun devuelve lista vacia",sugerencias.isEmpty());

		/*
		 * Comprobamos que los productos en el carrito tambien se excluyen.
		 */
		System.out.println("\n============= exclusion por carrito =============");

		alice.añadirProductoCarrito(comic2, 1);

		rec.setPesos(1, 0, 0);
		sugerencias = rec.generarSugerencias(alice);
		resultado = true;

		for (Producto p : sugerencias) {
			if (p.getNombre().equals("Watchmen")) {
				resultado = false;
				break;
			}
		}
		check("comic2 en carrito no aparece en sugerencias",
				resultado);

		alice.setCarritoActual(null);

		/*
		 * Comprobamos el control de errores de configuracion del recomendador.
		 */
		System.out.println("\n============= CONTROL DE ERRORES =============");

		int limiteAntes = rec.getLimiteMaximo();
		rec.setConfiguracion(0, true);
		check("setConfiguracion con limite 0 no cambia el limite", rec.getLimiteMaximo() == limiteAntes);

		rec.setConfiguracion(-3, true);
		check("setConfiguracion con limite negativo no cambia el limite", rec.getLimiteMaximo() == limiteAntes);

		double pv = rec.getPesoValoracion();
		double pc = rec.getPesoCompras();
		rec.setPesos(-1, 0.5, 0.5);
		check("setPesos con peso negativo no cambia los pesos",
				rec.getPesoValoracion() == pv && rec.getPesoCompras() == pc);

		rec.setPesos(0, 0, 0);
		check("setPesos todos a 0 no cambia los pesos", rec.getPesoValoracion() == pv);

		rec.setConfiguracion(1, true);
		rec.setPesos(1, 0, 0);
		sugerencias = rec.generarSugerencias(alice);
		check("Con limite 1 solo devuelve 1 sugerencia", sugerencias.size() == 1);

		rec.setConfiguracion(5, false);
		sugerencias = rec.generarSugerencias(alice);
		check("Con recomendador desactivado devuelve lista vacia", sugerencias.isEmpty());

		check("generarSugerencias con cliente null devuelve lista vacia", rec.generarSugerencias(null).isEmpty());

		/*
		 * Imprimimos el resultado del test.
		 */
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}