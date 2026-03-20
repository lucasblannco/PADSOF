package productos;

import java.sql.Date;
import java.util.ArrayList;

import tienda.*;
import tienda.Tienda;
import usuarios.*;
import usuarios.Empleado;

public class PruebaEmpleadoSubirProductoNuevo {
	public static void main(String[] args) {

	    Tienda tienda = Tienda.getInstancia();

	    // Crear y registrar empleado
	    Empleado e1 = new Empleado("paco", "1234", "paco@gmail.com");
	    tienda.getUsuarios().add(e1);

	    // Darle permisos para añadir productos (si es necesario)
	    e1.asignarPermiso(TipoPermisos.GESTION_STOCK);

	    // Probar añadir un COMIC
	    System.out.println("=== Prueba Comic ===");
	    boolean r1 = e1.añadirProducto_nuevo("C", "Spiderman Vol1", "Comic de spiderman", "img.jpg",
	            15.99, 10, new ArrayList<>(), 200, "Marvel", 2020,
	            0, 0, 0, null, null, 0, 0, 0, 0, null);
	    System.out.println("Comic añadido: " + r1);

	    // Probar añadir un JUEGO
	    System.out.println("=== Prueba Juego ===");
	    boolean r2 = e1.añadirProducto_nuevo("J", "Catan", "Juego de mesa", "catan.jpg",
	            45.00, 5, new ArrayList<>(), 0, null, 0,
	            0, 0, 0, null, null, 2, 4, 8, 99, "Estrategia");
	    System.out.println("Juego añadido: " + r2);

	    // Probar añadir una FIGURA
	    System.out.println("=== Prueba Figura ===");
	    boolean r3 = e1.añadirProducto_nuevo("F", "Figura Goku", "Figura de dragon ball", "goku.jpg",
	            29.99, 3, new ArrayList<>(), 0, null, 0,
	            20.0, 10.0, 8.0, "PVC", "Bandai", 0, 0, 0, 0, null);
	    System.out.println("Figura añadida: " + r3);

	    // Probar producto duplicado
	    System.out.println("=== Prueba Duplicado ===");
	    boolean r4 = e1.añadirProducto_nuevo("C", "Spiderman Vol1", "Otro comic", "img2.jpg",
	            12.00, 5, new ArrayList<>(), 150, "DC", 2021,
	            0, 0, 0, null, null, 0, 0, 0, 0, null);
	    System.out.println("Duplicado añadido (debe ser false): " + r4);

	    // Probar letra incorrecta
	    System.out.println("=== Prueba Letra Incorrecta ===");
	    boolean r5 = e1.añadirProducto_nuevo("X", "Producto raro", "desc", "img.jpg",
	            10.00, 1, new ArrayList<>(), 0, null, 0,
	            0, 0, 0, null, null, 0, 0, 0, 0, null);
	    System.out.println("Letra incorrecta añadido (debe ser false): " + r5);

	    // Ver stock final
	    System.out.println("=== Stock final: " + tienda.getStockVentas().size() + " productos ===");
	    for (ProductoVenta p : tienda.getStockVentas()) {
	        System.out.println(" - " + p.getNombre());
	    }
	
}
}