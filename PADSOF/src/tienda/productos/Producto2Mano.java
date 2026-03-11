package productos;

import usuarios.Cliente;
import usuarios.Empleado;
import java.util.*;

public class Producto2Mano extends Producto {
	private Valoracion valoracion = null;
	private Cliente propietario = null;
	private boolean bloqueado = true;
	private boolean visible = false;

	public Producto2Mano( String nombre, String descripcion, String imagenRuta, Valoracion valoracion,
			Cliente propietario, boolean bloqueado, boolean visible) {
		super(nombre, descripcion, imagenRuta);
		this.valoracion = valoracion;
		this.propietario = propietario;
		this.bloqueado = bloqueado;
		this.visible = visible;
	}

	public Producto2Mano(Cliente propietario, String nombre, String descripcion, String imagenRuta) {
		super(nombre, descripcion, imagenRuta);
		this.valoracion =null;
		this.propietario = propietario;
		this.bloqueado = false;
		this.visible = false;
	}

	public boolean valoracion(double precioTasacion, EstadoProducto estado, Empleado empleado) {
		if (estado == EstadoProducto.NO_ACEPTADO) {
			return false;
		}

		this.valoracion = new Valoracion(precioTasacion, estado, empleado);
		this.visible = true;
		this.bloqueado = false;

		return true;
	}

	public boolean isBloqueado() {
		return this.bloqueado;
	}
	
	public Valoracion getValoracion() {
		return this.valoracion;
	}
	
	public void setBloqueado(boolean bloqueado) {
		this.bloqueado= bloqueado;
	}
	
	public boolean getVisible() {
		return this.visible;
	}
}