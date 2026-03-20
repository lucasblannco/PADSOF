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
import sun.security.provider.JavaKeyStore.CaseExactJKS;
import ventas.*;

import java.util.*;

public class Empleado extends UsuarioRegistrado {

	protected List<Notificacion> notificaciones;
	private Set<TipoPermisos> permisos;
	private List<Valoracion> valoraciones;

	public Empleado(String nombre, String password, String email) {
		super(nombre, password, email);
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
//Hay que ver la cantidad supongio que habra una funcion de que si continee scarlo rapido y ahi modificas la cantidad

	public boolean añadirProducto_nuevo(String letra, String nombre, String descripcion, String imagen,
			double precioOficial, int Stock, ArrayList<Categoria> categorias, int numpaginas, String editorial,
			int añoPublicacion, double altura, double ancho, double largo, String material, String marca,
			int minNumjugadores, int maxNumjugadores, int minEdad, int maxEdad, String Estilo) {

		if (!this.getPermisos().contains(TipoPermisos.GESTION_STOCK)) {
			System.out.println("No tienes permiso para trabajar con pedidos");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();

		// 1. Validar atributos básicos
		if (nombre == null || precioOficial <= 0 || Stock <= 0 || descripcion == null || imagen == null) {
			System.out.println("Los atributos de producto deben aparecer correctamente");
			return false;
		}
		if (categorias==null) {
			return false;
		}
		boolean flag=true;
		
		
		for(Categoria c: categorias) {
			if (!tienda.getCategorias().contains(categorias)) {
				flag= false; 
				break;
			}
		}
		
		if (!flag) {
			System.out.println("Las categorias que se introduzcan deben existir en la tienda");
			return false;
		}
		// 2. Validar letra ANTES de comprobar existencia
		if (letra == null || letra.length() != 1) {
			this.recibirNotificacion(
					"El tipo de producto que has intentado crear no es correcta. Deben ser Comics(C), Figuras(F) o Juegos(J)");
			return false;
		}

		// 3. Comprobar si ya existe
		for (ProductoVenta p : tienda.getStockVentas()) {
			if (p.getNombre().equalsIgnoreCase(nombre)) {
				this.recibirNotificacion("El producto '" + nombre + "' ya existe en la tienda.");
				return false;
			}
		}

		switch (letra.toUpperCase()) {

		case "C":
			if (numpaginas <= 0 || editorial == null || añoPublicacion <= 0) {
				System.out.println("Estas añadiendo un comic, los atributos deben cumplir las condiciones necesarias");
				return false;
			}
			ProductoVenta comic = new Comic(nombre, descripcion, imagen, precioOficial, Stock, numpaginas, editorial,
					añoPublicacion);
			tienda.añadirProducto(comic);
			this.recibirNotificacion("Has añadido el comic " + comic.getNombre() + " a la tienda");
			return true; // <-- faltaba

		case "J":
			if (minEdad <= 0) {
				System.out.println("La edad minima del juego tiene que ser mayor que 0");
				return false;
			}
			if (maxEdad <= 0 || maxEdad > 100) {
				System.out.println("La edad maxima del juego debe estar entre 1 y 100 años");
				return false;
			}
			if (minNumjugadores <= 0) {
				System.out.println("El juego tendrá mínimo 1 jugador");
				return false;
			}
			if (maxNumjugadores <= 0) {
				System.out.println("El juego debe tener por lo menos un jugador");
				return false;
			}
			ProductoVenta juego = new JuegoMesa(nombre, descripcion, imagen, precioOficial, Stock, minNumjugadores,
					maxNumjugadores, minEdad, maxEdad, Estilo);
			tienda.añadirProducto(juego);
			this.recibirNotificacion("Has añadido el juego " + juego.getNombre() + " a la tienda");
			return true; // <-- faltaba el return Y el break

		case "F":
			if (altura <= 0 || ancho <= 0 || largo <= 0) {
				System.out.println("Las dimensiones deben ser positivas");
				return false;
			}
			if (material == null) {
				System.out.println("Las figuras deben tener material");
				return false; // <-- faltaba el return
			}
			if (marca == null) {
				System.out.println("Las figuras deben tener marca");
				return false; // <-- faltaba el return
			}
			ProductoVenta figura = new Figura(nombre, descripcion, imagen, precioOficial, Stock, altura, ancho, largo,
					material, marca);
			tienda.añadirProducto(figura);
			this.recibirNotificacion("Has añadido la figura " + figura.getNombre() + " a la tienda");
			return true;

		default:
			this.recibirNotificacion(
					"El tipo de producto que has intentado crear no es correcta. Deben ser Comics(C), Figuras(F) o Juegos(J)");
			return false; // <-- cambiado de throw a return false, más consistente con el resto
		}
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
