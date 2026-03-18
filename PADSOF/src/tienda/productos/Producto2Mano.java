package productos;

import usuarios.Cliente;
import usuarios.Empleado;

public class Producto2Mano extends Producto {
	private Valoracion valoracion = null;
	private Cliente propietario = null;
	private boolean bloqueado = true;
	private boolean visible = false;

	public Producto2Mano(String nombre, String descripcion, String imagenRuta, Valoracion valoracion,
			Cliente propietario, boolean bloqueado, boolean visible) {
		super(nombre, descripcion, imagenRuta);
		this.valoracion = valoracion;
		this.propietario = propietario;
		this.bloqueado = bloqueado;
		this.visible = visible;
	}

	public Producto2Mano(Cliente propietario, String nombre, String descripcion, String imagenRuta) {
		super(nombre, descripcion, imagenRuta);
		this.valoracion = null;
		this.propietario = propietario;
		this.bloqueado = true;
		this.visible = false;
	}

	public boolean valorar(double precioTasacion, EstadoProducto estado, Empleado empleado) {
		if (estado == null || empleado == null || precioTasacion <= 0) {
			return false;
		}

		this.valoracion = new Valoracion(precioTasacion, estado, empleado);

		if (estado == EstadoProducto.NO_ACEPTADO) {
			this.visible = false;
			this.bloqueado = true;
			return false;
		}

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
		this.bloqueado = bloqueado;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public Cliente getPropietario() {
		return this.propietario;
	}
}