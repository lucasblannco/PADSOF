package productos;

import usuarios.Cliente;
import usuarios.Empleado;

public class Producto2Mano extends Producto {
	private Valoracion valoracion = null;
	private Cliente propietario = null;
	private boolean bloqueado = true;
	private boolean visible = false;

	public Producto2Mano(String id, String nombre, String descripcion, String imagenRuta, Valoracion valoracion,
			Cliente propietario, boolean bloqueado, boolean visible) {

		super(id, nombre, descripcion, imagenRuta);
		this.valoracion = valoracion;
		this.propietario = propietario;
		this.bloqueado = bloqueado;
		this.visible = visible;
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
}