package productos;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tienda.*;
import tienda.Tienda;
import usuarios.*;
import usuarios.Empleado;

public class PruebaEmpleadoSubirProductoNuevo {
	public static void main(String[] args) {

		Tienda tienda = Tienda.getInstancia();

		// Categorías
		Categoria catAjedrez = new Categoria("Ajedrez", "Productos relacionados con el ajedrez");
		Categoria catAnime = new Categoria("Anime", "Productos relacionados con el anime");
		Categoria catFantasia = new Categoria("Fantasia", "Productos de fantasia y rol");

		tienda.getCategorias().add(catAjedrez);
		tienda.getCategorias().add(catAnime);
		tienda.getCategorias().add(catFantasia);

		// Empleado
		Empleado e1 = new Empleado("paco", "1234", "paco@gmail.com");
		tienda.getUsuarios().add(e1);
		e1.asignarPermiso(TipoPermisos.GESTION_STOCK);

		// Listas de categorías
		ArrayList<Categoria> catsAnime = new ArrayList<>();
		catsAnime.add(catAnime);

		ArrayList<Categoria> catsAjedrez = new ArrayList<>();
		catsAjedrez.add(catAjedrez);

		ArrayList<Categoria> catsFantasia = new ArrayList<>();
		catsFantasia.add(catFantasia);

		// Prueba Comic
		System.out.println("=== Prueba Comic ===");
		boolean r1 = e1.añadirProducto_nuevo("C", "Dragon Ball Vol1", "Comic de dragon ball", "db.jpg", 15.99, 10,
				catsAnime, 200, "Shonen Jump", 1995, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		System.out.println("Comic añadido (debe ser true): " + r1);

		// Prueba Juego
		System.out.println("=== Prueba Juego ===");
		boolean r2 = e1.añadirProducto_nuevo("J", "Catan", "Juego de mesa", "catan.jpg", 45.00, 5, catsAjedrez, 0, null,
				0, 0, 0, 0, null, null, 2, 4, 8, 99, "Estrategia");
		System.out.println("Juego añadido (debe ser true): " + r2);

		// Prueba Figura
		System.out.println("=== Prueba Figura ===");
		boolean r3 = e1.añadirProducto_nuevo("F", "Figura Goku", "Figura de dragon ball", "goku.jpg", 29.99, 3,
				catsAnime, 0, null, 0, 20.0, 10.0, 8.0, "PVC", "Bandai", 0, 0, 0, 0, null);
		System.out.println("Figura añadida (debe ser true): " + r3);

		// Prueba Duplicado
		System.out.println("=== Prueba Duplicado ===");
		boolean r4 = e1.añadirProducto_nuevo("C", "Dragon Ball Vol1", "Otro comic", "img2.jpg", 12.00, 5, catsAnime,
				150, "DC", 2021, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		System.out.println("Duplicado (debe ser false): " + r4);

		// Prueba Letra Incorrecta
		System.out.println("=== Prueba Letra Incorrecta ===");
		boolean r5 = e1.añadirProducto_nuevo("X", "Producto raro", "desc", "img.jpg", 10.00, 1, catsFantasia, 0, null,
				0, 0, 0, 0, null, null, 0, 0, 0, 0, null);
		System.out.println("Letra incorrecta (debe ser false): " + r5);

		// Stock final
		System.out.println("=== Stock final: " + tienda.getStockVentas().size() + " productos ===");
		for (ProductoVenta p : tienda.getStockVentas()) {
			System.out.println(" - " + p.getId() + p.getNombre() + " " + p.getStockDisponible());
		}

		// Ver el empleado que hemos creado a ver si se ha añadido bien
		List<Empleado> empleados_tienda = tienda.obtenerEmpleadosTienda();
		for (Empleado e : empleados_tienda) {

			System.out.println(e.toString());
		}

	}
}