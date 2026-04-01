package pruebas;

import java.util.ArrayList;

import productos.Categoria;
import productos.ProductoVenta;
import tienda.Tienda;
import usuarios.Empleado;
import usuarios.TipoPermisos;
import ventas.EstadoPedido;
import ventas.Pedido;

public class pruebaEmpleado {
    public static void main(String[] args) {

        Tienda tienda = Tienda.getInstancia();

        Empleado emp = new Empleado("juan", "1234", "juan@checkpoint.es");
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
        if (resultado) {
            System.out.println("Producto Spiderman creado correctamente en la categoria Comics");
        } else {
            System.out.println("Error al crear el producto Spiderman");
        }

        resultado = emp.añadirProducto_nuevo("C", "Spiderman", "Duplicado", "spiderman.jpg",
                12.99, 10, cats, 200, "Marvel", 2020, 0, 0, 0, null, null, 0, 0, 0, 0, null);
        if (!resultado) {
            System.out.println("No se puede crear un producto que ya existe, comportamiento correcto");
        }

        resultado = emp.añadirProducto_nuevo("X", "Producto", "Desc", "img.jpg",
                5.0, 1, cats, 0, null, 0, 0, 0, 0, null, null, 0, 0, 0, 0, null);
        if (!resultado) {
            System.out.println("No se puede crear un producto con tipo incorrecto, comportamiento correcto");
        }

        // =============================================
        System.out.println("\n PRUEBA GESTIÓN DE CATEGORÍAS ");
        // =============================================
        ProductoVenta spiderman = tienda.getStockVentas().get(0);
        resultado = emp.añadirProductoACategoria(spiderman, catJuegos);
        if (resultado) {
            System.out.println("Spiderman añadido a Juegos correctamente");
        } else {
            System.out.println("Error al añadir Spiderman a Juegos");
        }

        resultado = emp.añadirProductoACategoria(spiderman, catJuegos);
        if (!resultado) {
            System.out.println("No se puede añadir un producto que ya está en la categoría, comportamiento correcto");
        }

        resultado = emp.eliminarProductoDeCategoria(spiderman, catJuegos);
        if (resultado) {
            System.out.println("Spiderman eliminado de Juegos correctamente");
        } else {
            System.out.println("Error al eliminar Spiderman de Juegos");
        }

        resultado = emp.eliminarProductoDeCategoria(spiderman, catJuegos);
        if (!resultado) {
            System.out.println("No se puede eliminar un producto que no está en la categoría, comportamiento correcto");
        }

        Empleado empSinPermiso = new Empleado("pedro", "5678", "pedro@checkpoint.es");
        resultado = empSinPermiso.añadirProductoACategoria(spiderman, catComics);
        if (!resultado) {
            System.out.println("Un empleado sin permiso no puede gestionar categorías, comportamiento correcto");
        }

        // =============================================
        System.out.println("\n PRUEBA AÑADIR UNIDADES ");
        // =============================================

        resultado = emp.añadirUnidadesProductoExistente(spiderman.getId(), 5);
        if (resultado) {
            System.out.println("Añadidas 5 unidades a Spiderman correctamente, stock actual: " + tienda.getStockVentas().get(0).getStockDisponible());
        } else {
            System.out.println("Error al añadir unidades");
        }

        resultado = emp.añadirUnidadesProductoExistente(spiderman.getId(), -1);
        if (!resultado) {
            System.out.println("No se pueden añadir unidades negativas, comportamiento correcto");
        }

        resultado = emp.añadirUnidadesProductoExistente("ID_FALSO", 5);
        if (!resultado) {
            System.out.println("No se pueden añadir unidades a un producto que no existe, comportamiento correcto");
        }
    }
}