package pruebas;

import java.util.ArrayList;

import productos.Categoria;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.TipoPermisos;


public class pruebaEmpleado {

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

        Empleado emp = new Empleado("juan", "Juan@1234");
        emp.asignarPermiso(TipoPermisos.GESTION_STOCK);
        emp.asignarPermiso(TipoPermisos.GESTION_CATEGORIAS);
        emp.asignarPermiso(TipoPermisos.GESTION_PEDIDOS);
        emp.asignarPermiso(TipoPermisos.ENTREGA_PEDIDOS);

        Categoria catComics = new Categoria("Comics", "Categoria de comics");
        Categoria catJuegos = new Categoria("Juegos", "Categoria de juegos");
        tienda.getCategorias().add(catComics);
        tienda.getCategorias().add(catJuegos);

        // =============================================
        System.out.println(" PRUEBA AÑADIR PRODUCTOS NUEVOS");
        // =============================================

        ArrayList<Categoria> cats = new ArrayList<>();
        cats.add(catComics);

        boolean resultado = emp.añadirProducto_nuevo("C", "Spiderman", "El asombroso Spiderman", "spiderman.jpg",
                12.99, 10, cats, 200, "Marvel", 2020, 0, 0, 0, null, null, 0, 0, 0, 0, null);
        check("Producto Spiderman creado correctamente en la categoria Comics", resultado);

        resultado = emp.añadirProducto_nuevo("C", "Spiderman", "Segunda edicion", "spiderman2.jpg",
                12.99, 10, cats, 200, "Marvel", 2021, 0, 0, 0, null, null, 0, 0, 0, 0, null);
        check("Se puede crear un producto con el mismo nombre pero distinto contenido", resultado);

        resultado = emp.añadirProducto_nuevo("X", "Producto", "Desc", "img.jpg",
                5.0, 1, cats, 0, null, 0, 0, 0, 0, null, null, 0, 0, 0, 0, null);
        check("No se puede crear un producto con tipo incorrecto", !resultado);

        // =============================================
        System.out.println("\n PRUEBA GESTIÓN DE CATEGORÍAS ");
        // =============================================
        ProductoVenta spiderman = tienda.getStockVentas().get(0);

        resultado = emp.añadirProductoACategoria(spiderman.getId(), catJuegos.getNombre());
        check("Spiderman añadido a Juegos correctamente", resultado);

        resultado = emp.añadirProductoACategoria(spiderman.getId(), catJuegos.getNombre());
        check("No se puede añadir un producto que ya está en la categoría", !resultado);

        resultado = emp.eliminarProductoDeCategoria(spiderman.getId(), catJuegos.getNombre());
        check("Spiderman eliminado de Juegos correctamente", resultado);

        resultado = emp.eliminarProductoDeCategoria(spiderman.getId(), catJuegos.getNombre());
        check("No se puede eliminar un producto que no está en la categoría", !resultado);

        Empleado empSinPermiso = new Empleado("pedro", "Pedro@5678");
        resultado = empSinPermiso.añadirProductoACategoria(spiderman.getId(), catComics.getNombre());
        check("Un empleado sin permiso no puede gestionar categorías", !resultado);

        // =============================================
        System.out.println("\n PRUEBA AÑADIR UNIDADES ");
        // =============================================

        resultado = emp.reponerStockProducto(spiderman.getId(), 5);
        check("Añadidas 5 unidades a Spiderman correctamente, stock actual: "
            + tienda.getStockVentas().get(0).getStockDisponible(), resultado);

        resultado = emp.reponerStockProducto(spiderman.getId(), -1);
        check("No se pueden añadir unidades negativas", !resultado);

        resultado = emp.reponerStockProducto("ID_FALSO", 5);
        check("No se pueden añadir unidades a un producto que no existe", !resultado);

        System.out.println("\n==============================================");
        System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
        System.out.println("==============================================");
    }
}