package pruebas;
import java.util.List;
import productos.ProductoVenta;
import tienda.Notificacion;
import tienda.Tienda;
import usuarios.*;
public class pruebaFinalFicheros {

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
		Tienda tienda = Tienda.getInstancia();
		Gestor gestor = tienda.getGestor();

		System.out.println("=== CONFIGURANDO EL SISTEMA ===");
		check("Configurar tiempos del sistema", gestor.setTiemposSistema(10, 10, 10));

		// ── 2. Creamos categorías necesarias para el fichero ───────
		System.out.println("\n=== CREANDO CATEGORÍAS ===");
		check("Crear categoria manga",      gestor.crearCategoria("manga",      "Cómics de manga japonés"));
		check("Crear categoria aventuras",  gestor.crearCategoria("aventuras",  "Productos de aventuras"));
		check("Crear categoria estrategia", gestor.crearCategoria("estrategia", "Juegos de estrategia"));
		check("Crear categoria familiar",   gestor.crearCategoria("familiar",   "Productos para toda la familia"));
		check("Crear categoria funko",      gestor.crearCategoria("funko",      "Figuras Funko Pop"));
		check("Crear categoria cine",       gestor.crearCategoria("cine",       "Productos de cine"));

		// ── 3. Damos de alta un empleado con permiso GESTION_STOCK ─
		System.out.println("\n=== DANDO DE ALTA EMPLEADO ===");
		boolean altaOk = gestor.darDeAltaEmpleados_Permisos("emp1", "Empleado@1", List.of(TipoPermisos.GESTION_STOCK));
		check("Alta empleado", altaOk);
		Empleado emp = tienda.loginEmpleado("emp1", "Empleado@1");
		check("Empleado logueado correctamente", emp != null);
		if (emp != null) System.out.println("Empleado logueado: " + emp.getNickname());

		// ── 4. Cargamos el fichero ─────────────────────────────────
		System.out.println("\n=== CARGANDO FICHERO ===");
		check("Cargar fichero de productos", emp.cargarProductosFicheroTexto("ficheros/productos.txt"));

		// ── 5. Mostramos los productos creados ─────────────────────
		System.out.println("\n=== PRODUCTOS EN LA TIENDA ===");
		check("La tienda tiene productos tras cargar el fichero", !tienda.getStockVentas().isEmpty());
		for (ProductoVenta p : tienda.getStockVentas()) {
			System.out.println(p);
		}

		// ── 6. Cargamos el fichero otra vez para reponer stock ─────
		System.out.println("\n=== REPONIENDO STOCK ===");
		check("Reponer stock desde fichero", emp.cargarProductosFicheroTexto("ficheros/productos.txt"));

		// ── 7. Mostramos el stock actualizado ─────────────────────
		System.out.println("\n=== STOCK ACTUALIZADO ===");
		for (ProductoVenta p : tienda.getStockVentas()) {
			System.out.println(p);
		}

		// ── 8. Probamos errores del fichero ────────────────────────
		// La línea C;PV-2;Error de Tipo debería dar error porque PV-2 es Monopoly (J)
		// y estamos diciendo que es C
		System.out.println("\n=== NOTIFICACIONES DEL EMPLEADO ===");
		for (Notificacion n : emp.getNotificaciones()) {
			System.out.println(n);
		}

		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}