package pruebas;


import productos.*;
import tienda.*;
import usuarios.*;


public class PruebaFiltros {

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
		 * Montamos productos de venta con distintos precios y puntuaciones,
		 * y productos de segunda mano con distintas valoraciones y estados,
		 * para poder cubrir todos los criterios de ambos filtros.
		 *
		 * Productos de venta:
		 *   comic1  precio=10  puntuacion=9  catComics
		 *   comic2  precio=20  puntuacion=5  catComics
		 *   figura1 precio=35  puntuacion=8  catFiguras
		 *   jm1     precio=50  puntuacion=3  catJuegos
		 *
		 * Productos segunda mano:
		 *   p2m_perfecto   tasacion=15  PERFECTO   visible=true  bloqueado=false
		 *   p2m_bueno      tasacion=8   MUY_BUENO  visible=true  bloqueado=false
		 *   p2m_usado      tasacion=4   MUY_USADO  visible=true  bloqueado=false
		 *   p2m_bloqueado  tasacion=10  MUY_BUENO  visible=true  bloqueado=true
		 *   p2m_sinval     sin valoracion           visible=false bloqueado=true
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		Empleado tasador = new Empleado("tasador", "Tasador@1");
		tasador.asignarPermiso(TipoPermisos.VALORACION_PRODUCTOS);
		tienda.getUsuarios().add(tasador);

		Categoria catComics  = new Categoria("Comics",  "desc");
		Categoria catFiguras = new Categoria("Figuras", "desc");
		Categoria catJuegos  = new Categoria("Juegos",  "desc");
		tienda.getCategorias().add(catComics);
		tienda.getCategorias().add(catFiguras);
		tienda.getCategorias().add(catJuegos);

		Comic     comic1  = new Comic    ("Saga",    "d", "i", 10.00, 20, 200, "Image", 2012);
		Comic     comic2  = new Comic    ("Watchmen","d", "i", 20.00, 10, 400, "DC",    1987);
		Figura    figura1 = new Figura   ("Goku",    "d", "i", 35.00, 15, 20, 15, 12, "PVC", "Bandai");
		JuegoMesa jm1     = new JuegoMesa("Catan",   "d", "i", 50.00, 12, 2, 4, 8, 99, "Euro");
		comic1.addCategoria(catComics);
		comic2.addCategoria(catComics);
		figura1.addCategoria(catFiguras);
		jm1.addCategoria(catJuegos);
		tienda.añadirProducto(comic1);
		tienda.añadirProducto(comic2);
		tienda.añadirProducto(figura1);
		tienda.añadirProducto(jm1);

		// Puntuaciones
		Cliente aux = new Cliente("aux", "Aux@12345", "00000000X");
		new Reseña(aux, comic1,  9.0, "");
		new Reseña(aux, comic2,  5.0, "");
		new Reseña(aux, figura1, 8.0, "");
		new Reseña(aux, jm1,     3.0, "");

		// Productos segunda mano
		Cliente propietario = new Cliente("prop", "Prop@1234", "99999999Z");
		tienda.getUsuarios().add(propietario);

		Producto2Mano p2m_perfecto  = new Producto2Mano(propietario, "Figura rara",  "d", "i");
		Producto2Mano p2m_bueno     = new Producto2Mano(propietario, "Comic usado",  "d", "i");
		Producto2Mano p2m_usado     = new Producto2Mano(propietario, "Juego viejo",  "d", "i");
		Producto2Mano p2m_bloqueado = new Producto2Mano(propietario, "Bloqueado",    "d", "i");
		Producto2Mano p2m_sinval    = new Producto2Mano(propietario, "Sin valorar",  "d", "i");

		p2m_perfecto.valorar(15.0, EstadoProducto.PERFECTO,  tasador);
		p2m_bueno   .valorar( 8.0, EstadoProducto.MUY_BUENO, tasador);
		p2m_usado   .valorar( 4.0, EstadoProducto.MUY_USADO, tasador);
		p2m_bloqueado.valorar(10.0, EstadoProducto.MUY_BUENO, tasador);

		// Publicamos los validos y dejamos bloqueado y sin valoracion fuera
		tienda.publicarParaIntercambio(p2m_perfecto);
		tienda.publicarParaIntercambio(p2m_bueno);
		tienda.publicarParaIntercambio(p2m_usado);
		// p2m_bloqueado: visible pero bloqueado manualmente
		p2m_bloqueado.setVisible(true);
		// p2m_sinval: sin valoracion, bloqueado y no visible (estado inicial)

		System.out.println("\tMontaje listo.");


		// =============================================
		System.out.println("\n============= FiltroVenta - estado inicial =============");
		// =============================================

		/*
		 * Con filtro por defecto (sin restricciones) todos los productos
		 * con stock pasan. Comprobamos tambien toString y resetear.
		 */
		FiltroVenta fv = new FiltroVenta();

		check("Filtro por defecto: comic1 (precio 10) pasa",     fv.productoCumpleFiltro(comic1));
		check("Filtro por defecto: figura1 (precio 35) pasa",    fv.productoCumpleFiltro(figura1));
		check("Filtro con null devuelve false",                   !fv.productoCumpleFiltro(null));
		check("toString contiene 'FiltroVenta'",                  fv.toString().contains("FiltroVenta"));


		// =============================================
		System.out.println("\n============= FiltroVenta - precio =============");
		// =============================================

		/*
		 * Filtramos por rango de precio y comprobamos que solo pasan
		 * los productos dentro del rango, incluyendo los extremos.
		 */
		fv.setPrecioMinimo(15);
		fv.setPrecioMaximo(40);

		check("precio 10 (comic1) no pasa rango [15,40]",  !fv.productoCumpleFiltro(comic1));
		check("precio 20 (comic2) pasa rango [15,40]",      fv.productoCumpleFiltro(comic2));
		check("precio 35 (figura1) pasa rango [15,40]",     fv.productoCumpleFiltro(figura1));
		check("precio 50 (jm1) no pasa rango [15,40]",     !fv.productoCumpleFiltro(jm1));

		// Extremos del rango
		fv.setPrecioMinimo(20);
		fv.setPrecioMaximo(35);
		check("precio exactamente igual al minimo (20) pasa", fv.productoCumpleFiltro(comic2));
		check("precio exactamente igual al maximo (35) pasa", fv.productoCumpleFiltro(figura1));


		// =============================================
		System.out.println("\n============= FiltroVenta - puntuacion =============");
		// =============================================

		fv.resetear();
		fv.setPuntuacionMinima(7.0);

		check("puntuacion 9 (comic1) pasa minimo 7",   fv.productoCumpleFiltro(comic1));
		check("puntuacion 8 (figura1) pasa minimo 7",  fv.productoCumpleFiltro(figura1));
		check("puntuacion 5 (comic2) no pasa minimo 7",!fv.productoCumpleFiltro(comic2));
		check("puntuacion 3 (jm1) no pasa minimo 7",  !fv.productoCumpleFiltro(jm1));


		// =============================================
		System.out.println("\n============= FiltroVenta - categorias =============");
		// =============================================

		fv.resetear();
		fv.añadirCategoria(catComics);

		check("comic1 (Comics) pasa filtro por categoria Comics",   fv.productoCumpleFiltro(comic1));
		check("comic2 (Comics) pasa filtro por categoria Comics",   fv.productoCumpleFiltro(comic2));
		check("figura1 (Figuras) no pasa filtro por Comics",       !fv.productoCumpleFiltro(figura1));
		check("jm1 (Juegos) no pasa filtro por Comics",            !fv.productoCumpleFiltro(jm1));

		// Añadir segunda categoria: pasan Comics O Figuras
		fv.añadirCategoria(catFiguras);
		check("figura1 pasa con categorias [Comics, Figuras]",      fv.productoCumpleFiltro(figura1));
		check("jm1 sigue sin pasar con categorias [Comics, Figuras]",!fv.productoCumpleFiltro(jm1));

		// Eliminar una categoria
		fv.eliminarCategoria(catComics);
		check("comic1 no pasa tras eliminar Comics del filtro",    !fv.productoCumpleFiltro(comic1));
		check("figura1 sigue pasando solo con Figuras",             fv.productoCumpleFiltro(figura1));

		// Añadir categoria null no falla
		fv.añadirCategoria(null);
		check("añadir null no añade nada al filtro", fv.getCategorias().size() == 1);


		// =============================================
		System.out.println("\n============= FiltroVenta - combinado =============");
		// =============================================

		/*
		 * Precio [10,20], puntuacion >= 7, categoria Comics.
		 * Solo comic1 (precio 10, puntuacion 9, Comics) debe pasar.
		 * comic2 tiene puntuacion 5 y no llega al minimo.
		 */
		fv.resetear();
		fv.setPrecioMinimo(10);
		fv.setPrecioMaximo(20);
		fv.setPuntuacionMinima(7.0);
		fv.añadirCategoria(catComics);

		check("Solo comic1 pasa el filtro combinado",  fv.productoCumpleFiltro(comic1));
		check("comic2 no pasa (puntuacion 5 < 7)",    !fv.productoCumpleFiltro(comic2));
		check("figura1 no pasa (precio 35 > 20)",     !fv.productoCumpleFiltro(figura1));


		// =============================================
		System.out.println("\n============= FiltroVenta - control errores setters =============");
		// =============================================

		fv.resetear();
		fv.setPrecioMinimo(-5);
		check("setPrecioMinimo negativo no cambia el valor", fv.getPrecioMinimo() == 0);

		fv.setPrecioMaximo(100);
		fv.setPrecioMinimo(200);
		check("setPrecioMinimo > precioMaximo no cambia el valor", fv.getPrecioMinimo() == 0);

		fv.setPrecioMaximo(50);
		fv.setPrecioMinimo(100);
		check("setPrecioMaximo < precioMinimo no cambia el valor", fv.getPrecioMaximo() == 50);

		fv.setPuntuacionMinima(-1);
		check("setPuntuacionMinima negativa no cambia el valor", fv.getPuntuacionMinima() == 0);

		fv.setPuntuacionMinima(11);
		check("setPuntuacionMinima > 10 no cambia el valor", fv.getPuntuacionMinima() == 0);


		// =============================================
		System.out.println("\n============= FiltroVenta - resetear =============");
		// =============================================

		fv.setPrecioMinimo(10);
		fv.setPrecioMaximo(30);
		fv.setPuntuacionMinima(5);
		fv.añadirCategoria(catComics);
		fv.resetear();

		check("Tras resetear, precioMinimo = 0",                  fv.getPrecioMinimo() == 0);
		check("Tras resetear, precioMaximo = MAX",                 fv.getPrecioMaximo() == Double.MAX_VALUE);
		check("Tras resetear, puntuacionMinima = 0",              fv.getPuntuacionMinima() == 0);
		check("Tras resetear, categorias vacia",                   fv.getCategorias().isEmpty());
		check("Tras resetear, comic1 vuelve a pasar",              fv.productoCumpleFiltro(comic1));


		// =============================================
		System.out.println("\n============= FiltroSegundaMano - estado inicial =============");
		// =============================================

		/*
		 * Con filtro por defecto pasan todos los productos visibles,
		 * no bloqueados y con valoracion. Los bloqueados y sin valoracion no.
		 */
		FiltroSegundaMano fsm = new FiltroSegundaMano();

		check("Filtro por defecto: p2m_perfecto pasa",   fsm.cumpleFiltro(p2m_perfecto));
		check("Filtro por defecto: p2m_bueno pasa",      fsm.cumpleFiltro(p2m_bueno));
		check("Filtro por defecto: p2m_usado pasa",      fsm.cumpleFiltro(p2m_usado));
		check("p2m_bloqueado no pasa (bloqueado=true)",  !fsm.cumpleFiltro(p2m_bloqueado));
		check("p2m_sinval no pasa (sin valoracion)",     !fsm.cumpleFiltro(p2m_sinval));
		check("null devuelve false",                     !fsm.cumpleFiltro(null));
		check("toString contiene 'FiltroSegundaMano'",    fsm.toString().contains("FiltroSegundaMano"));


		// =============================================
		System.out.println("\n============= FiltroSegundaMano - precio =============");
		// =============================================

		fsm.setValorMinimo(5);
		fsm.setValorMaximo(12);

		check("tasacion 15 (p2m_perfecto) no pasa rango [5,12]",  !fsm.cumpleFiltro(p2m_perfecto));
		check("tasacion 8  (p2m_bueno) pasa rango [5,12]",         fsm.cumpleFiltro(p2m_bueno));
		check("tasacion 4  (p2m_usado) no pasa rango [5,12]",     !fsm.cumpleFiltro(p2m_usado));

		// Extremos
		fsm.setValorMinimo(8);
		fsm.setValorMaximo(15);
		check("tasacion exactamente igual al minimo (8) pasa",  fsm.cumpleFiltro(p2m_bueno));
		check("tasacion exactamente igual al maximo (15) pasa", fsm.cumpleFiltro(p2m_perfecto));


		// =============================================
		System.out.println("\n============= FiltroSegundaMano - estado =============");
		// =============================================

		/*
		 * estadoMinimo indica la calidad minima: PERFECTO(0) es el mejor,
		 * DAÑADO(5) el peor aceptado. Un producto pasa si su estado
		 * tiene ordinal <= estadoMinimo.ordinal() (es igual o mejor).
		 */
		fsm.resetear();
		fsm.setEstadoMinimo(EstadoProducto.MUY_BUENO); // acepta PERFECTO y MUY_BUENO

		check("PERFECTO pasa con estadoMinimo MUY_BUENO",  fsm.cumpleFiltro(p2m_perfecto));
		check("MUY_BUENO pasa con estadoMinimo MUY_BUENO", fsm.cumpleFiltro(p2m_bueno));
		check("MUY_USADO no pasa con estadoMinimo MUY_BUENO", !fsm.cumpleFiltro(p2m_usado));

		fsm.setEstadoMinimo(EstadoProducto.PERFECTO); // solo acepta PERFECTO
		check("Solo PERFECTO pasa con estadoMinimo PERFECTO",   fsm.cumpleFiltro(p2m_perfecto));
		check("MUY_BUENO no pasa con estadoMinimo PERFECTO",   !fsm.cumpleFiltro(p2m_bueno));

		fsm.setEstadoMinimo(null); // sin restriccion de estado
		check("Con estadoMinimo null todos los estados pasan",  fsm.cumpleFiltro(p2m_usado));


		// =============================================
		System.out.println("\n============= FiltroSegundaMano - control errores setters =============");
		// =============================================

		fsm.resetear();
		fsm.setValorMinimo(-1);
		check("setValorMinimo negativo no cambia el valor", fsm.getValorMinimo() == 0);

		fsm.setValorMaximo(20);
		fsm.setValorMinimo(30);
		check("setValorMinimo > valorMaximo no cambia el valor", fsm.getValorMinimo() == 0);

		fsm.setValorMinimo(5);
		fsm.setValorMaximo(3);
		check("setValorMaximo < valorMinimo no cambia el valor", fsm.getValorMaximo() == 20);


		// =============================================
		System.out.println("\n============= FiltroSegundaMano - resetear =============");
		// =============================================

		fsm.setValorMinimo(5);
		fsm.setValorMaximo(10);
		fsm.setEstadoMinimo(EstadoProducto.PERFECTO);
		fsm.resetear();

		check("Tras resetear, valorMinimo = 0",              fsm.getValorMinimo() == 0);
		check("Tras resetear, valorMaximo = MAX",            fsm.getValorMaximo() == Double.MAX_VALUE);
		check("Tras resetear, estadoMinimo = null",          fsm.getEstadoMinimo() == null);
		check("Tras resetear, p2m_usado vuelve a pasar",     fsm.cumpleFiltro(p2m_usado));


		// =============================================
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}