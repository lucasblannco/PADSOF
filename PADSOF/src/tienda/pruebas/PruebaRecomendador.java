package pruebas;

import java.util.*;
import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class PruebaRecomendador {

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
		 * Montamos una tienda limpia con productos, categorias, clientes
		 * y pedidos para poder probar los tres criterios del recomendador.
		 *
		 * Productos:
		 *   comic1, comic2  -> categoria Comics  (puntuaciones 9 y 7)
		 *   figura1, figura2 -> categoria Figuras (puntuaciones 8 y 6)
		 *   jm1             -> categoria Juegos   (puntuacion 5)
		 *
		 * alice: compro comic1 -> categoria favorita Comics
		 * bob:   compro comic1 y figura1 (mismo producto que alice -> tiene en comun)
		 * carlos: sin historial (caso borde)
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();
		Recomendador rec = tienda.getRecomendador();

		Categoria catComics  = new Categoria("Comics",  "desc");
		Categoria catFiguras = new Categoria("Figuras", "desc");
		Categoria catJuegos  = new Categoria("Juegos",  "desc");
		tienda.getCategorias().add(catComics);
		tienda.getCategorias().add(catFiguras);
		tienda.getCategorias().add(catJuegos);

		Comic     comic1  = new Comic    ("Saga Vol.1", "desc", "img", 12.50, 20, 200, "Image", 2012);
		Comic     comic2  = new Comic    ("Watchmen",   "desc", "img", 15.00, 10, 400, "DC",    1987);
		Figura    figura1 = new Figura   ("Goku SSJ",   "desc", "img", 35.00, 15, 20, 15, 12, "PVC", "Bandai");
		Figura    figura2 = new Figura   ("Link",       "desc", "img", 40.00,  8, 18, 12, 10, "PVC", "Nintendo");
		JuegoMesa jm1     = new JuegoMesa("Catan",      "desc", "img", 45.00, 12, 2, 4, 8, 99, "Eurogame");

		comic1.addCategoria(catComics);
		comic2.addCategoria(catComics);
		figura1.addCategoria(catFiguras);
		figura2.addCategoria(catFiguras);
		jm1.addCategoria(catJuegos);

		tienda.añadirProducto(comic1);
		tienda.añadirProducto(comic2);
		tienda.añadirProducto(figura1);
		tienda.añadirProducto(figura2);
		tienda.añadirProducto(jm1);

		// Resenas para que getMediaPuntuacion funcione
		// comic1=9, comic2=7, figura1=8, figura2=6, jm1=5
		Cliente aux = new Cliente("aux", "aux", "00000000X");
		new Reseña(aux, comic1,  9.0, "Genial");
		new Reseña(aux, comic2,  7.0, "Bien");
		new Reseña(aux, figura1, 8.0, "Muy bueno");
		new Reseña(aux, figura2, 6.0, "Regular");
		new Reseña(aux, jm1,     5.0, "Pasable");

		Cliente alice  = new Cliente("alice",  "p1", "11111111A");
		Cliente bob    = new Cliente("bob",    "p2", "22222222B");
		Cliente carlos = new Cliente("carlos", "p3", "33333333C");
		tienda.getUsuarios().add(alice);
		tienda.getUsuarios().add(bob);
		tienda.getUsuarios().add(carlos);

		// alice compro comic1 -> categoria favorita Comics
		Carrito ca = new Carrito(alice); ca.añadirProducto(comic1, 1);
		Pedido  pa = new Pedido(alice, ca);
		alice.getHistorialPedidos().add(pa);
		tienda.registrarVenta(pa);

		// bob compro comic1 (en comun con alice) y figura1
		Carrito cb1 = new Carrito(bob); cb1.añadirProducto(comic1,  1);
		Pedido  pb1 = new Pedido(bob, cb1);
		bob.getHistorialPedidos().add(pb1); tienda.registrarVenta(pb1);
		Carrito cb2 = new Carrito(bob); cb2.añadirProducto(figura1, 1);
		Pedido  pb2 = new Pedido(bob, cb2);
		bob.getHistorialPedidos().add(pb2); tienda.registrarVenta(pb2);

		// carlos sin historial


		/*
		 * Comprobamos el recomendador por valoracion.
		 * Debe devolver los productos mejor valorados que alice no ha comprado,
		 * es decir comic1 esta excluido. El primero debe ser figura1 (8.0).
		 */
		System.out.println("\n============= recomendarPorValoracion =============");

		rec.setPesos(1, 0, 0); // solo valoracion
		List<ProductoVenta> sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		check("No incluye comic1 (ya comprado por alice)",
			sugerencias.stream().noneMatch(p -> p.getNombre().equals("Saga Vol.1")));
		check("El primero es figura1 (puntuacion 8.0, la mas alta disponible)",
			sugerencias.get(0).getNombre().equals("Goku SSJ"));
		check("No supera el limite maximo (" + rec.getLimiteMaximo() + ")",
			sugerencias.size() <= rec.getLimiteMaximo());


		/*
		 * Comprobamos el recomendador por compras.
		 * Bob compro comic1 (en comun con alice) y figura1.
		 * El recomendador debe sugerirle figura1 a alice ya que bob,
		 * que compro lo mismo que ella, tambien compro figura1.
		 */
		System.out.println("\n============= recomendarPorCompras =============");

		rec.setPesos(0, 1, 0); // solo compras
		sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		check("No incluye comic1 (ya comprado por alice)",
			sugerencias.stream().noneMatch(p -> p.getNombre().equals("Saga Vol.1")));
		check("Incluye figura1 (bob la compro y tiene comic1 en comun con alice)",
			sugerencias.stream().anyMatch(p -> p.getNombre().equals("Goku SSJ")));


		/*
		 * Comprobamos el recomendador por categorias.
		 * Alice compro comic1 -> categoria favorita es Comics.
		 * Debe recomendarle comic2, que es de Comics y no lo ha comprado.
		 * No debe recomendarle comic1 (ya comprado).
		 */
		System.out.println("\n============= recomendarPorCategorias =============");

		rec.setPesos(0, 0, 1); // solo categorias
		sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		check("Incluye comic2 (misma categoria favorita, no comprado)",
			sugerencias.stream().anyMatch(p -> p.getNombre().equals("Watchmen")));
		check("No incluye comic1 (ya comprado)",
			sugerencias.stream().noneMatch(p -> p.getNombre().equals("Saga Vol.1")));
		check("Solo recomienda productos de la categoria Comics",
			sugerencias.stream().allMatch(p -> p.getCategorias().contains(catComics)));


		/*
		 * Comprobamos el recomendador ponderado combinando los tres criterios.
		 * Con pesos iguales todos los criterios contribuyen. El resultado debe
		 * seguir excluyendo los productos comprados y respetar el limite.
		 */
		System.out.println("\n============= recomendador ponderado =============");

		rec.setPesos(1, 1, 1); // pesos iguales, se normalizan a 0.33 cada uno
		sugerencias = rec.generarSugerencias(alice);

		check("Devuelve sugerencias no vacias", sugerencias != null && !sugerencias.isEmpty());
		check("No incluye comic1 (ya comprado)",
			sugerencias.stream().noneMatch(p -> p.getNombre().equals("Saga Vol.1")));
		check("No supera el limite maximo", sugerencias.size() <= rec.getLimiteMaximo());


		/*
		 * Comprobamos el caso borde de carlos, que no tiene historial.
		 * El recomendador por categorias devuelve lista vacia (sin categoria favorita).
		 * El ponderado y el de valoracion deben seguir funcionando.
		 */
		System.out.println("\n============= carlos sin historial (caso borde) =============");

		rec.setPesos(0, 0, 1); // solo categorias
		sugerencias = rec.generarSugerencias(carlos);
		check("Sin historial, recomendador por categorias devuelve lista vacia",
			sugerencias.isEmpty());

		rec.setPesos(1, 0, 0); // solo valoracion
		sugerencias = rec.generarSugerencias(carlos);
		check("Sin historial, recomendador por valoracion devuelve productos",
			!sugerencias.isEmpty());
		check("Sin historial no hay excluidos, puede recomendar cualquier producto",
			sugerencias.size() <= rec.getLimiteMaximo());


		/*
		 * Comprobamos que los productos en el carrito tambien se excluyen.
		 * Ponemos comic2 en el carrito de alice y comprobamos que no aparece.
		 */
		System.out.println("\n============= exclusion por carrito =============");

		Carrito carritoAlice = new Carrito(alice);
		carritoAlice.añadirProducto(comic2, 1);
		alice.setCarritoActual(carritoAlice);

		rec.setPesos(1, 0, 0);
		sugerencias = rec.generarSugerencias(alice);
		check("comic2 en carrito no aparece en sugerencias",
			sugerencias.stream().noneMatch(p -> p.getNombre().equals("Watchmen")));

		alice.setCarritoActual(null); // limpiamos


		/*
		 * Comprobamos el control de errores de configuracion.
		 */
		System.out.println("\n============= CONTROL DE ERRORES =============");

		int limiteAntes = rec.getLimiteMaximo();
		rec.setConfiguracion(0, true);
		check("setConfiguracion con limite 0 no cambia el limite",
			rec.getLimiteMaximo() == limiteAntes);

		rec.setConfiguracion(-3, true);
		check("setConfiguracion con limite negativo no cambia el limite",
			rec.getLimiteMaximo() == limiteAntes);

		double pv = rec.getPesoValoracion();
		double pc = rec.getPesoCompras();
		double pcat = rec.getPesoCategorias();
		rec.setPesos(-1, 0.5, 0.5);
		check("setPesos con peso negativo no cambia los pesos",
			rec.getPesoValoracion() == pv && rec.getPesoCompras() == pc);

		rec.setPesos(0, 0, 0);
		check("setPesos todos a 0 no cambia los pesos",
			rec.getPesoValoracion() == pv);

		rec.setConfiguracion(1, true);
		sugerencias = rec.generarSugerencias(alice);
		check("Con limite 1 solo devuelve 1 sugerencia",
			sugerencias.size() == 1);

		rec.setConfiguracion(5, false);
		sugerencias = rec.generarSugerencias(alice);
		check("Con recomendador desactivado devuelve lista vacia",
			sugerencias.isEmpty());

		check("generarSugerencias con cliente null devuelve lista vacia",
			rec.generarSugerencias(null).isEmpty());


		/*
		 * Imprimimos el resultado del test.
		 */
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}