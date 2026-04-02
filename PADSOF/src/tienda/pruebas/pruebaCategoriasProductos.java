package pruebas;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.TipoPermisos;
import java.util.ArrayList;
import productos.Categoria;
import productos.ProductoVenta;
public class pruebaCategoriasProductos {

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
        // Crear empleado con permisos
        Empleado emp = new Empleado("juan", "Juan@1234");
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
        check("Crear Comic Spiderman",
            emp.añadirProducto_nuevo("C", "Spiderman", "El asombroso Spiderman", "spiderman.jpg",
                12.99, 10, categoriasComic,
                200, "Marvel", 2020,
                0, 0, 0, null, null,
                0, 0, 0, 0, null));
        check("Crear Juego Catan",
            emp.añadirProducto_nuevo("J", "Catan", "El juego de los colonos", "catan.jpg",
                39.99, 5, categoriasJuego,
                0, null, 0,
                0, 0, 0, null, null,
                3, 4, 10, 99, "Estrategia"));
        // Obtener productos creados
        ProductoVenta spiderman = tienda.getStockVentas().get(0);
        ProductoVenta catan = tienda.getStockVentas().get(1);
        // Añadir a categoría
        System.out.println("\n=== AÑADIR PRODUCTO A CATEGORÍA YA ASIGNADA (debe fallar) ");
        check("Añadir Spiderman a Comics (ya está) devuelve false",
            !emp.añadirProductoACategoria(spiderman.getId(), catComics.getNombre()));

        System.out.println("\n=== AÑADIR PRODUCTO A OTRA CATEGORÍA (debe funcionar) ===");
        check("Añadir Spiderman a Juegos devuelve true",
            emp.añadirProductoACategoria(spiderman.getId(), catJuegos.getNombre()));

        System.out.println("\n CASOS DE ERROR(nulls)");
        check("Añadir null a Comics devuelve false",
            !emp.añadirProductoACategoria(null, catComics.getNombre()));
        System.out.println("creamos una categoria pero no la añadimos a la tienda");
        Categoria catFalsa = new Categoria("Falsa", "No existe en tienda");
        check("Añadir Catan a categoría que no existe devuelve false",
            !emp.añadirProductoACategoria(catan.getId(), catFalsa.getNombre()));
        Empleado empSinPermiso = new Empleado("pedro", "Pedro@5678");
        check("Empleado sin permiso añade producto devuelve false",
            !empSinPermiso.añadirProductoACategoria(catan.getId(), catJuegos.getNombre()));

        System.out.println("\n ELIMINAR PRODUCTO DE CATEGORÍA ");
        check("Eliminar Spiderman de Juegos devuelve true",
            emp.eliminarProductoDeCategoria(spiderman.getId(), catJuegos.getNombre()));
        check("Eliminar Spiderman de Juegos otra vez (ya no está) devuelve false",
            !emp.eliminarProductoDeCategoria(spiderman.getId(), catJuegos.getNombre()));

        System.out.println("\n==============================================");
        System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
        System.out.println("==============================================");
    }
}