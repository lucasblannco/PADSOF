package tienda;

import java.util.List;

import productos.Producto;
import productos.ProductoVenta;

import usuarios.*;

public class pruebaFinalFicheros {
	public static void main(String[] args) {

		Tienda tienda = Tienda.getInstancia();
		Gestor gestor = tienda.getGestor();
		System.out.println("=== CONFIGURANDO EL SISTEMA ===");
		gestor.setTiemposSistema(10, 10, 10);

		// ── 2. Creamos categorías necesarias para el fichero ───────
		System.out.println("\n=== CREANDO CATEGORÍAS ===");
		gestor.crearCategoria("manga", "Cómics de manga japonés");
		gestor.crearCategoria("aventuras", "Productos de aventuras");
		gestor.crearCategoria("estrategia", "Juegos de estrategia");
		gestor.crearCategoria("familiar", "Productos para toda la familia");
		gestor.crearCategoria("funko", "Figuras Funko Pop");
		gestor.crearCategoria("cine", "Productos de cine");

		// ── 3. Damos de alta un empleado con permiso GESTION_STOCK ─
		System.out.println("\n=== DANDO DE ALTA EMPLEADO ===");

		boolean altaOk = gestor.darDeAltaEmpleados_Permisos("emp1", "Empleado@1", List.of(TipoPermisos.GESTION_STOCK));
		System.out.println("Alta empleado: " + altaOk);
		Empleado emp = tienda.loginEmpleado("emp1", "Empleado@1");

		System.out.println("Empleado logueado: " + emp.getNickname());

		// ── 4. Cargamos el fichero ─────────────────────────────────
		System.out.println("\n=== CARGANDO FICHERO ===");
		emp.cargarProductosFicheroTexto("ficheros/productos.txt");

		// ── 5. Mostramos los productos creados ─────────────────────
		System.out.println("\n=== PRODUCTOS EN LA TIENDA ===");
		for (ProductoVenta p : tienda.getStockVentas()) {
			System.out.println(p);
		}

		// ── 6. Cargamos el fichero otra vez para reponer stock ─────
		System.out.println("\n=== REPONIENDO STOCK ===");
		emp.cargarProductosFicheroTexto("ficheros/productos.txt");

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
	}
}
