package productos; 
import tienda.Tienda;

import usuarios.Empleado;
import usuarios.TipoPermisos;

import java.util.ArrayList;

import productos.Categoria;
import productos.ProductoVenta;

public class pruebaCategoriasProductos {
    public static void main(String[] args) {

        Tienda tienda = Tienda.getInstancia();

        // Crear empleado con permisos
        Empleado emp = new Empleado("juan", "1234", "juan@checkpoint.es");
        emp.asignarPermiso(TipoPermisos.GESTION_STOCK);
        emp.asignarPermiso(TipoPermisos.GESTION_CATEGORIAS);

        // Crear categorías (lo haría el gestor)
        Categoria catComics = new Categoria("Comics", "Categoría de cómics");
        Categoria catJuegos = new Categoria("Juegos", "Categoría de juegos de mesa");
        tienda.getCategorias().add(catComics);
        tienda.getCategorias().add(catJuegos);

        // Listas de categorías para cada producto
		ArrayList<Categoria> categoriasComic = new ArrayList<>();
        categoriasComic.add(catComics);

        ArrayList<Categoria> categoriasJuego = new ArrayList<>();
        categoriasJuego.add(catJuegos);

        // --- CREAR PRODUCTOS ---
        System.out.println("=== CREAR PRODUCTOS ===");
        emp.añadirProducto_nuevo("C", "Spiderman", "El asombroso Spiderman", "spiderman.jpg",
                12.99, 10, categoriasComic,
                200, "Marvel", 2020,
                0, 0, 0, null, null,
                0, 0, 0, 0, null);

        emp.añadirProducto_nuevo("J", "Catan", "El juego de los colonos", "catan.jpg",
                39.99, 5, categoriasJuego,
                0, null, 0,
                0, 0, 0, null, null,
                3, 4, 10, 99, "Estrategia");

        // Obtener productos creados
        ProductoVenta spiderman = tienda.getStockVentas().get(0);
        ProductoVenta catan = tienda.getStockVentas().get(1);

        // Añadir a categoría
        System.out.println("\n=== AÑADIR PRODUCTO A CATEGORÍA YA ASIGNADA (debe fallar) ===");
        System.out.println("Añadir Spiderman a Comics (ya está): " + emp.añadirProductoACategoria(spiderman, catComics)); // false

        System.out.println("\n=== AÑADIR PRODUCTO A OTRA CATEGORÍA (debe funcionar) ===");
        System.out.println("Añadir Spiderman a Juegos: " + emp.añadirProductoACategoria(spiderman, catJuegos)); // true

        System.out.println("\n CASOS DE ERROR(nulls)");
        System.out.println("Añadir null a Comics: " + emp.añadirProductoACategoria(null, catComics)); // false, null
        System.out.println("creamos una categoria pero no la añadimos a la tienda");
        Categoria catFalsa = new Categoria("Falsa", "No existe en tienda");
        System.out.println("Añadir Catan a categoría que no existe: " + emp.añadirProductoACategoria(catan, catFalsa)); // false

        Empleado empSinPermiso = new Empleado("pedro", "5678", "pedro@checkpoint.es");
        System.out.println("Empleado sin permiso añade producto: " + empSinPermiso.añadirProductoACategoria(catan, catJuegos)); // false

      
        System.out.println("\n ELIMINAR PRODUCTO DE CATEGORÍA ");
        System.out.println("Eliminar Spiderman de Juegos: " + emp.eliminarProductoDeCategoria(spiderman, catJuegos)); // true
        System.out.println("Eliminar Spiderman de Juegos otra vez (ya no está): " + emp.eliminarProductoDeCategoria(spiderman, catJuegos)); // false
  
    }
}