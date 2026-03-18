package usuarios;

import tienda.*;
import productos.*;

import java.security.PublicKey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import intercambios.*;
import productos.Producto2Mano;

import ventas.*;

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

	// si un producto no es aceptado, como borramos ese producto? habria que hacer
	// una funcion en tienda.
	public void tasarProducto(Producto2Mano p, double precio, EstadoProducto estado) {
		// En cuanto un empleado empieza la valoracion del producto se borra por que los
		// demas no puedan hacerlo
		Tienda.getInstancia().getPendientes_Tasacion().remove(p);

		if (this.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {

			if (estado == EstadoProducto.NO_ACEPTADO) {
				p.getPropietario()
						.recibirNotificacion("El producto " + p.getNombre() + " ha sido rechazado al no cumplir .");
			}

			Valoracion nuevaVal = new Valoracion(precio, estado, this);
			p.setValoracion(nuevaVal);
			p.setVisible(true);
			this.valoraciones.add(nuevaVal);
			Tienda.getInstancia().publicarParaIntercambio(p);
			p.getPropietario()
					.recibirNotificacion("El producto " + p.getNombre() + " ha sido tasado  y publicadocon éxito .");
			this.recibirNotificacion("Has completado la valoracion del producto" + p.getNombre() + " con exito");
		} else {
			System.out
					.println("Error: El empleado" + this.getNickname() + "no tiene permisos de VALORACION_PRODUCTOS.");
		}

	}

	public void confirmarIntercambio(Oferta o) {
		if (!this.getPermisos().contains(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			System.out.println(
					"El empleado " + this.getNickname() + "no tiene permisos paara hacer confirmacion de intercambios");
		}
		if (o.getEstado() != EstadoOferta.PENDIENTE) {
			this.recibirNotificacion("La oferta no ha sido aceptada por ambos usuarios por lo que no se puede aceptar");
		}

		o.aceptarYEjecutar();

	}

	public void añadirProducto_nuevo(ProductoVenta p) {
		Tienda.getInstancia().getStockNuevos()
	}
	
	
	
	
	
	
	
	
//-Esra debe sobrar creo
	public void asignarPermiso(TipoPermisos p) {
		this.permisos.add(p);
	}

	public void quitarPermiso(TipoPermisos p) {
		this.permisos.remove(p);
	}

	public boolean tienePermiso(TipoPermisos p) {
		return this.permisos.contains(p);
	}

	public void recibirNotificacion(String mensaje) {
		if (this.notificaciones == null) {
			this.notificaciones = new ArrayList<>();
		}
		// Si aún no has creado la clase Notificacion, puedes pasarle un String
		// o crear el objeto aquí mismo si ya la tienes.
		this.notificaciones.add(new Notificacion(mensaje));
		System.out.println("[Notificación Empleado]: " + mensaje);
	}

	public void asignarTodosLosPermisos() {
		this.permisos = EnumSet.allOf(Permiso.class);
	}

	public List<Notificacion> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(List<Notificacion> notificaciones) {
		this.notificaciones = notificaciones;
	}

	public Set<TipoPermisos> getPermisos() {
		return permisos;
	}

	public void setPermisos(Set<TipoPermisos> permisos) {
		this.permisos = permisos;
	}

	public List<Valoracion> getValoraciones() {
		return valoraciones;
	}

	public void setValoraciones(List<Valoracion> valoraciones) {
		this.valoraciones = valoraciones;
	}
}
