package pruebas;

import java.util.*;
import java.sql.Date;
import Excepcion.*;
import intercambios.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class DemostradorMain {

	static void imprimirEmpleado(Empleado e) {
	    System.out.println(" Empleado: " + e.getNickname()
	        + " | id: " + e.getId()
	        + " | permisos: " + e.getPermisos().size());
	    for (TipoPermisos p : e.getPermisos()) {
	        System.out.println("   -> " + p);
	    }
	}
	public static void main(String[] args) {
		System.out.println("Bienvenido al demostrado de la tienda CHECKPOINT");
		Tienda tienda = Tienda.getInstancia();
		Gestor gestor = tienda.getGestor();
		System.out.println("CONFIGURACION DE LOS TIEMPOS DE LA APLICACION");

		gestor.configurarTiemposSistema(60, 30, 30);
		System.out.println(" Tiempos configurados -> Carrito: " + tienda.getTiempoMaxCarrito() + "min | Oferta: "
				+ tienda.getTiempoMaxOferta() + "min | Pago: " + tienda.getTiempoMaxPago() + "min");

		gestor.setPrecioTasacion(10);
		System.out.println(" Precio de las valoraciones: " + tienda.getPrecioTasacion() + "€");

		System.out.println("\nCREACION DE CATEGORIAS");
		
		gestor.crearCategoria("Familiar", "Tematica para realizar en familia");
        gestor.crearCategoria("Accion", "Tematica de accion");
        gestor.crearCategoria("Ciencia-ficcion ", "Productos de ciencia ficcion");
        gestor.crearCategoria("Anime", "Productos de anime y manga");
		gestor.crearCategoria("Replicas", "Objetos de coleccionista a escala real");
		gestor.crearCategoria("Retro-Gaming", "Consolas clásicas, cartuchos originales y accesorios de sistemas de videojuegos antiguos.");
		
		String nombres = "";
		for (Categoria c : tienda.getCategorias()) {
			if (!nombres.equals("")) {
				nombres += ", ";
			}
			nombres += c.getNombre();
		}
		System.out.println("Categorias creadas: " + tienda.getCategorias().size() + " -> " + nombres);

		System.out.println("\nDAR DE ALTA EMPLEADOS EN LA TIENDA:");
		
		List<TipoPermisos> permisosStock = gestor.crearListaPermisos(
			    TipoPermisos.GESTION_STOCK,
			    TipoPermisos.GESTION_CATEGORIAS,
			    TipoPermisos.GESTION_PACKS,
			    TipoPermisos.MODIFICAR_PRODUCTO);
			gestor.darDeAltaEmpleados_Permisos("emp_stock", "Stock@1234", permisosStock);

			List<TipoPermisos> permisosTasador = gestor.crearListaPermisos(
			    TipoPermisos.VALORACION_PRODUCTOS,
			    TipoPermisos.CONFIRMACION_INTERCAMBIO);
			gestor.darDeAltaEmpleados_Permisos("emp_tasador", "Tasador@1234", permisosTasador);

			List<TipoPermisos> permisosPedidos = gestor.crearListaPermisos(
			    TipoPermisos.GESTION_PEDIDOS,
			    TipoPermisos.ENTREGA_PEDIDOS);
			gestor.darDeAltaEmpleados_Permisos("emp_pedidos", "Pedidos@1234", permisosPedidos);
			
			
			Empleado empStock   = tienda.loginEmpleado("emp_stock",   "Stock@1234");
	        Empleado empTasador = tienda.loginEmpleado("emp_tasador", "Tasador@1234");
	        Empleado empPedidos = tienda.loginEmpleado("emp_pedidos", "Pedidos@1234");
			
	        System.out.println("RESUMEN EMPLEADOS:");
	        imprimirEmpleado(empStock);
	        imprimirEmpleado(empTasador);
	        imprimirEmpleado(empPedidos);
	        System.out.println(" Total empleados en tienda: " + tienda.obtenerEmpleadosTienda().size());
			
	        
	        System.out.println("\n CARGA DE PRODUCTOS:");
	        System.out.println("→ Cargando productos desde fichero...");
	        empStock.cargarProductosFicheroTexto("ficheros/productos.txt");
	        System.out.println("  Productos tras fichero: " + tienda.getStockVentas().size());
	        
	        empStock.añadirProducto_nuevo("C", "Watchmen", "Clasico del comic", "watchmen.jpg",
	                15.00, 10, tienda.seleccionarCategorias("Accion","Anime"),
	                400, "DC Comics", 1987,
	                0, 0, 0, null, null, 0, 0, 0, 0, null);

	            empStock.añadirProducto_nuevo("J", "Catan", "Juego de estrategia", "catan.jpg",
	                45.00, 8, tienda.seleccionarCategorias("Familiar", "Estrategia"),
	                0, null, 0, 0, 0, 0, null, null,
	                2, 4, 8, 99, "Estrategia");

	            empStock.añadirProducto_nuevo("F", "Figura Goku SSJ", "Figura de Dragon Ball", "goku.jpg",
	                35.00, 5, tienda.seleccionarCategorias("Anime", "Replicas"),
	                0, null, 0, 20.0, 15.0, 12.0, "PVC", "Bandai",
	                0, 0, 0, 0, null);

	            empStock.añadirProducto_nuevo("C", "Akira Vol.1", "Manga de ciencia ficcion", "akira.jpg",
	                12.99, 20, tienda.seleccionarCategorias("Anime", "Ciencia-ficcion"),
	                350, "Kodansha", 1982,
	                0, 0, 0, null, null, 0, 0, 0, 0, null);
	        
	         System.out.println("PROBAMOS CASO DE FALLO CON UN EMPLEADO QUE NO TIENE PERMISOS DE GESTION DE STOCK");
	         
	         empPedidos.añadirProducto_nuevo("C", "Akira Vol.1", "Manga de ciencia ficcion", "akira.jpg",
		                12.99, 20, tienda.seleccionarCategorias("Anime", "Ciencia-ficcion"),
		                350, "Kodansha", 1982,
		                0, 0, 0, null, null, 0, 0, 0, 0, null);
	        
	         System.out.println("  Total productos en tienda: " + tienda.getStockVentas().size());
	         for (ProductoVenta p : tienda.getStockVentas()) {
	        	    System.out.println("  " + p.resumen());
	        	}
	         System.out.println("REPONER STOCK:");
	         ProductoVenta watchmen = tienda.buscarproductoPorNombre("Watchmen").get(0);
	         empStock.reponerStockProducto(watchmen.getId(), 5);
	         System.out.println("MODIFICAR LA DESCRIPCION DE UN PRODUCTO:");
	         empStock.modificarDescripcionProducto(watchmen.getId(),
	                 "La obra maestra del noveno arte");
	         
	         System.out.println("AÑADIR PRODUCTOS A CATEGORIA:");
	         empStock.añadirProductoACategoria(watchmen.getId(), "Retro-Gaming");
	         empStock.añadirProductoACategoria(watchmen.getId(), "Familiar");
	         System.out.println("  Categorias tras añadir:");
	         watchmen.imprimirCategorias();
	         System.out.println("ELIMINAR CATEGORIAS DE PRODUCTOS");
	         empStock.eliminarProductoDeCategoria(watchmen.getId(), "Anime");
	         System.out.println("  Categorias tras eliminar Anime:");
	         watchmen.imprimirCategorias();
	         
	}

}
