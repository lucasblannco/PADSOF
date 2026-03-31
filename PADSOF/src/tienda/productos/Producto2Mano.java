package productos;

import usuarios.Cliente;
import usuarios.Empleado;
import tienda.Estadistica;

public class Producto2Mano extends Producto {
	private Valoracion valoracion = null;
	private Cliente propietario = null;
	private boolean bloqueado = true;
	private boolean visible = false;

	public Producto2Mano(String nombre, String descripcion, String imagenRuta, Valoracion valoracion,
			Cliente propietario, boolean bloqueado, boolean visible) {
		super(nombre, descripcion, imagenRuta);
		Estadistica est = Estadistica.getInstancia();
		this.id = "P2M-" + String.valueOf(est.getnProducto2Mano());
		this.valoracion = valoracion;
		this.propietario = propietario;
		this.bloqueado = bloqueado;
		this.visible = visible;
		est.setnProducto2Mano(est.getnProducto2Mano() + 1);
	}

	public Producto2Mano(Cliente propietario, String nombre, String descripcion, String imagenRuta) {
		super(nombre, descripcion, imagenRuta);
		Estadistica est = Estadistica.getInstancia();
		this.id = "P2M" + String.valueOf(est.getnProducto2Mano());
		est.setnProducto2Mano(est.getnProducto2Mano() + 1);
		this.valoracion = null;
		this.propietario = propietario;
		this.bloqueado = true;
		this.visible = false;
	}

	public boolean valorar(double precioTasacion, EstadoProducto estado, Empleado empleado) {
		if (estado == null || empleado == null || precioTasacion < 0) {
			return false;
		}

		this.valoracion = new Valoracion(precioTasacion, estado, empleado);

		if (estado == EstadoProducto.NO_ACEPTADO) {
			this.visible = false;
			this.bloqueado = true;
			return false;
		}

		this.visible = true;
		this.bloqueado = true;
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

	public void setValoracion(Valoracion v) {
		this.valoracion = v;
	}

	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public String toString() {
		String nickPropietario = (this.propietario != null) ? this.propietario.getNickname() : "Sin propietario";
		String estadoValoracion = (this.valoracion != null) ? this.valoracion.getEstadoProducto().toString()
				: "Sin valorar";

		return super.toString() + " | Propietario: " + nickPropietario + " | Estado: " + estadoValoracion
				+ " | Disponible: " + (!this.bloqueado ? "Sí" : "No") + " |";
	}

}