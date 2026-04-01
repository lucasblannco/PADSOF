package tienda;



import productos.Producto;
import productos.ProductoVenta;

import usuarios.*;
public class pruebaFinalFicheros {
	public static void main(String[] args) {
        // 1. Instancia de la Tienda
        Tienda checkpoint = Tienda.getInstancia();

        // 2. El Gestor prepara las categorías (requisito de tu método añadirProducto_nuevo)
        Gestor adminGlobal = (Gestor) checkpoint.getUsuarios().get(0);
        adminGlobal.crearCategoria("manga", "Comics japoneses");
        adminGlobal.crearCategoria("familiar", "Juegos para todos");
        adminGlobal.crearCategoria("funko", "Coleccionables");

        // 3. Crear el Empleado SIN permisos iniciales
        // Usamos el método de Gestor pero pasando null en la lista de permisos
        adminGlobal.darDeAltaEmpleados_Permisos("paco_stock", "Paco@1234", null);
        
        Empleado paco = checkpoint.loginEmpleado("paco_stock", "Paco@1234");

        if (paco != null) {
            System.out.println("--- INTENTO 1: CARGA SIN PERMISOS ---");
            // Esto imprimirá: "El empleado paco_stock no tiene el permiso GESTION_STOCK"
            boolean intento1 = paco.cargarProductosFicheroTexto("productos.txt");
            System.out.println("¿Carga exitosa?: " + intento1);

            System.out.println("\n--- ASIGNANDO PERMISOS INDIVIDUALMENTE ---");
            // El Gestor asigna el permiso manualmente
            adminGlobal.asignarPermiso(paco.getId(), TipoPermisos.GESTION_STOCK);

            System.out.println("--- INTENTO 2: CARGA CON PERMISOS ---");
            boolean intento2 = paco.cargarProductosFicheroTexto("productos.txt");
            
            if (intento2) {
                System.out.println("\n=== INVENTARIO FINAL EN TIENDA ===");
                for (ProductoVenta p : checkpoint.getStockVentas()) {
                    System.out.println("ID: " + p.getId() + 
                                       " | Producto: " + p.getNombre() + 
                                       " | Stock: " + p.getStockDisponible() + " unidades.");
                }
            }
        } else {
            System.err.println("Error: No se pudo loguear al empleado.");
        }
    }
}