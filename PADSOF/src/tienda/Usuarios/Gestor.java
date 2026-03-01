package tienda.Usuarios;


import java.util.List;
import tienda.Tienda;
import tienda.Ventas.Descuento;

public class Gestor extends UsuarioRegistrado {

    public Gestor() {
        super();
    }

    @Override
    public void mostrarPanelPrincipal() {
        System.out.println("--- PANEL DE CONTROL DEL GESTOR ---");
        System.out.println("1. Gestionar Empleados y Permisos");
        System.out.println("2. Configurar Precios Globales");
        System.out.println("3. Crear Descuentos y Packs");
    }
    
    public void gestionarEmpleado(Empleado e, String accion, TipoPermisos permiso) {
        switch (accion.toUpperCase()) {
            case "DAR_PERMISO":
                e.asignarPermiso(permiso);
                break;
            case "QUITAR_PERMISO":
                e.quitarPermiso(permiso);
                break;
            case "DESPEDIR":
                Tienda.getInstancia().getUsuarios().remove(e);
                System.out.println("Empleado eliminado del sistema.");
                break;
        }
    }

    public void configurarParametrosGlobales() {
    }

 // En Gestor.java
    public void crearDescuento(String idProducto, double porcentaje, java.time.LocalDate inicio, java.time.LocalDate fin) {
        // 1. Validamos datos básicos
        if (porcentaje <= 0 || porcentaje > 100) {
            System.out.println("Porcentaje no válido");
            return;
        }

       
        Descuento nuevoDesc = new Descuento(idProducto, porcentaje / 100, inicio, fin);

      
        tienda.Tienda.getInstancia().agregarDescuento(nuevoDesc);

        
    }
    
    public void modificarPerfil(String nuevoNick, String nuevaPass) {
        this.nickname = nuevoNick;
        this.password = nuevaPass;
        System.out.println("Perfil de administrador actualizado.");
    }
}
