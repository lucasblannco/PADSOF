package tienda.Usuarios;

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
    public boolean tasarProducto(ProductoSegundaMano p, double precio, EstadoProducto estado) {
    	if(this.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
        Valoracion nuevaVal = new Valoracion(precio, estado, this);
        this.valoraciones.add(nuevaVal);
        p.setValoracion(nuevaVal);
        return true;
        }
        return false;
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
