package usuarios;
import tienda.*;

import java.util.List;

import tienda.Productos.*;


import java.util.*;

public class Empleado extends UsuarioRegistrado {
   
    protected List<Notificacion> notificaciones;
    private Set<TipoPermisos> permisos;
    private List<Valoracion> valoraciones;
 

    public Empleado() {
        super();
        this.valoraciones = new ArrayList<>();
        this.permisos = new TreeSet<>();
        }

    @Override
    public void mostrarPanelPrincipal() {
    }
    
    //si un producto no es aceptado, como borramos ese producto? habria que hacer una funcion en tienda.
    public void tasarProducto(Producto2Mano p, double precio, EstadoProducto estado) {
        
        if(estado == EstadoProducto.NO_ACEPTADO) {
        	Tienda.getInstancia().getPendientesTasacion().remove(p);
        	 p.getPropietario().recibirNotificacion("El producto " + p.getNombre() + " ha sido revhazado.");
        }
    	if (this.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
            
            Valoracion nuevaVal = new Valoracion(precio, estado, this);
            
            p.setValoracion(nuevaVal);        
           p.setVisible(true);
           
            this.valoraciones.add(nuevaVal); 
            Tienda.getInstancia().getPendientesTasacion().remove(p);
            Tienda.getInstancia().publicarParaIntercambio(p);
            p.getPropietario().recibirNotificacion("El producto " + p.getNombre() + " ha sido tasado con éxito.");

        } else {
            System.out.println("Error: El empleado no tiene permisos de VALORACION_PRODUCTOS.");
        }
    }
    
    public void asignarPermiso(TipoPermisos p) {
        this.permisos.add(p);
    }

    public void quitarPermiso(TipoPermisos p) {
        this.permisos.remove(p);
    }

    public boolean tienePermiso(TipoPermisos p) {
        return this.permisos.contains(p);
    }

    public void asignarTodosLosPermisos() {
        this.permisos = EnumSet.allOf(Permiso.class);
    }
}
