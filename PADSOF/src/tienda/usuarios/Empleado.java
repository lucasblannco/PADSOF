package usuarios;

import tienda.*;
import productos.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import Excepcion.FicheroFormatoInvalidoException;
import Excepcion.TipoProductoDesconocidoException;
import intercambios.*;

import ventas.*;

import java.util.*;

public class Empleado extends UsuarioRegistrado {

	protected List<Notificacion> notificaciones;
	private Set<TipoPermisos> permisos;
	private boolean despedido;
	private List<Valoracion> valoraciones;
	/*
	 * public Empleado(String nickname, String password, String email) {
	 * super(nickname, password, email); this.valoraciones = new ArrayList<>();
	 * this.permisos = new TreeSet<>(); }
	 */

	public Empleado(String nickname, String password) {
		super(nickname, password);
		this.valoraciones = new ArrayList<>();
		this.permisos = new TreeSet<>();
		this.despedido = false;
		this.notificaciones = new ArrayList<>();
	}

	@Override
	public void mostrarPanelPrincipal() {
	}

	private boolean puedeRealizarTarea(TipoPermisos permiso) {
		if (this.despedido) {
			System.out.println("El empleado" + this.getNickname() + " dado de baja y no puede realizar acciones.");
			return false;
		}
		if (!this.tienePermiso(permiso)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene el permiso " + permiso);
			return false;
		}
		return true;
	}

	private Producto2Mano buscarProductoPendientePorId(String idProducto) {
		if (idProducto == null || idProducto.isBlank()) {
			System.out.println("El id del producto no puede estar vacío.");
			return null;
		}
		for (Producto2Mano p : Tienda.getInstancia().getPendientes_Tasacion()) {
			if (p.getId().equals(idProducto))
				return p;
		}
		System.out.println("No existe ningún producto pendiente de tasación con id: " + idProducto);
		return null;
	}

	private Pedido buscarPedidoPorId(String idPedido) {
		if (idPedido == null || idPedido.isBlank()) {
			System.out.println("El id del pedido no puede estar vacío.");
			return null;
		}
		for (Pedido p : Tienda.getInstancia().getHistorialVentas()) {
			if (p.getIdPedido().equals(idPedido))
				return p;
		}
		System.out.println("No existe ningún pedido con id: " + idPedido);
		return null;
	}

	private void actualizarStockDesdeLinea(String id, String[] partes, int numLinea, String linea)
			throws FicheroFormatoInvalidoException {
		ProductoVenta existente = this.buscarProductoPorId(id);
		if (existente == null) {
			throw new FicheroFormatoInvalidoException(numLinea, linea, "No existe ningún producto con ID: " + id);
		}
		try {
			int unidades = Integer.parseInt(partes[3].trim());
			if (unidades <= 0) {
				throw new NumberFormatException();
			}
			existente.setStockDisponible(existente.getStockDisponible() + unidades);

		} catch (NumberFormatException e) {
			throw new FicheroFormatoInvalidoException(numLinea, linea, "Unidades inválidas");
		}
	}

	private boolean procesarNuevoProducto(String tipo, String nombre, String[] partes, int numLinea, String linea)
			throws FicheroFormatoInvalidoException {
		try {
			double precio = Double.parseDouble(partes[4].trim());
			int unidadesIniciales = Integer.parseInt(partes[5].trim());
			if (precio <= 0 || unidadesIniciales < 0) {
				throw new NumberFormatException();
			}
			ArrayList<Categoria> categorias = new ArrayList<>();
			if (!partes[6].isBlank()) {
				for (String nombreCat : partes[6].split(",")) {
					Categoria categoria = Tienda.getInstancia().buscarCategoriaPorNombre(nombreCat);
					if (categoria != null) {
						categorias.add(categoria);
					}
				}
				boolean creado;
				switch (tipo) {

				case "C":
					if (partes.length < 10)
						throw new FicheroFormatoInvalidoException(numLinea, linea, "Faltan campos Comic");

					creado = añadirProducto_nuevo("C", nombre, partes[3].trim(), "", precio, unidadesIniciales,
							categorias, Integer.parseInt(partes[7].trim()), partes[8].trim(),
							Integer.parseInt(partes[9].trim()), 0, 0, 0, null, null, 0, 0, 0, 0, null);
					break;

				case "J":
					if (partes.length < 15)
						throw new FicheroFormatoInvalidoException(numLinea, linea, "Faltan campos Juego");

					creado = añadirProducto_nuevo("J", nombre, partes[3].trim(), "", precio, unidadesIniciales,
							categorias, 0, null, 0, 0, 0, 0, null, null, Integer.parseInt(partes[10].trim()),
							Integer.parseInt(partes[11].trim()), Integer.parseInt(partes[12].trim()),
							Integer.parseInt(partes[13].trim()), partes[14].trim());
					break;

				case "F":
					if (partes.length < 20)
						throw new FicheroFormatoInvalidoException(numLinea, linea, "Faltan campos Figura");

					creado = añadirProducto_nuevo("F", nombre, partes[3].trim(), "", precio, unidadesIniciales,
							categorias, 0, null, 0, Double.parseDouble(partes[15].trim()),
							Double.parseDouble(partes[16].trim()), Double.parseDouble(partes[17].trim()),
							partes[18].trim(), partes[19].trim(), 0, 0, 0, 0, null);
					break;

				default:
					throw new TipoProductoDesconocidoException(numLinea, linea, tipo);
				}
				if (!creado) {
					throw new FicheroFormatoInvalidoException("Error interno al crear producto", numLinea, linea);
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// si un producto no es aceptado, como borramos ese producto? habria que hacer
	// una funcion en tienda.
	public void tasarProducto(String idProducto, double precio, EstadoProducto estado) {
		if (!puedeRealizarTarea(TipoPermisos.VALORACION_PRODUCTOS))
			return;

		// Comprobamos que el producto esté pendiente de tasación
		Producto2Mano p = buscarProductoPendientePorId(idProducto);
		if (p == null) {
			System.out.println("El producto " + idProducto + " no está pendiente de tasación.");
			return;
		}

		Tienda.getInstancia().getPendientes_Tasacion().remove(p);

		if (estado == EstadoProducto.NO_ACEPTADO) {
			p.getPropietario().recibirNotificacionTipo(
					"El producto " + p.getNombre() + " ha sido rechazado al no cumplir las expectativas suficientes.",
					TipoNotificacion.VALORACION_COMPLETADA);
			return;
		}

		Valoracion nuevaVal = new Valoracion(precio, estado, this);
		p.setValoracion(nuevaVal);
		p.setVisible(true);
		this.valoraciones.add(nuevaVal);
		Tienda.getInstancia().publicarParaIntercambio(p);
		p.getPropietario().recibirNotificacionTipo(
				"El producto " + p.getNombre() + " ha sido tasado y publicado con éxito.",
				TipoNotificacion.VALORACION_COMPLETADA);
		this.recibirNotificacion("Has completado la valoración del producto " + p.getNombre() + " con éxito.");
	}

	public boolean confirmarIntercambio(Oferta o) {
		if (!puedeRealizarTarea(TipoPermisos.CONFIRMACION_INTERCAMBIO)) {
			System.out.println(
					"El empleado " + this.getNickname() + "no tiene permisos paara hacer confirmacion de intercambios");
			return false;
		}
		if (o.getEstado() != EstadoOferta.ACEPTADA) {
			this.recibirNotificacion("La oferta no ha sido aceptada por ambos usuarios por lo que no se puede aceptar");
		}
		o.aceptarYEjecutar();
		return true;
	}
//Hay que ver la cantidad supongio que habra una funcion de que si contine sacarlo rapido y ahi modificas la cantidad

	/// Funcion para añadir un nuevo producto a la tienda
	public boolean añadirProducto_nuevo(String letra, String nombre, String descripcion, String imagen,
			double precioOficial, int Stock, ArrayList<Categoria> categorias, int numpaginas, String editorial,
			int añoPublicacion, double altura, double ancho, double largo, String material, String marca,
			int minNumjugadores, int maxNumjugadores, int minEdad, int maxEdad, String Estilo) {

		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {
			System.out.println("No tienes permiso para trabajar con productos");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();

		// 1. Validar atributos básicos
		if (nombre == null || precioOficial <= 0 || Stock <= 0 || descripcion == null || imagen == null) {
			System.out.println("Los atributos de producto deben aparecer correctamente");
			return false;
		}
		if (categorias == null) {
			return false;
		}
		boolean flag = true;

		for (Categoria c : categorias) {
			if (!tienda.getCategorias().contains(c)) {
				flag = false;
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

		switch (letra.toUpperCase()) {
		case "C":
			if (numpaginas <= 0 || editorial == null || añoPublicacion <= 0) {
				System.out.println("Estas añadiendo un comic, los atributos deben cumplir las condiciones necesarias");
				return false;
			}
			ProductoVenta comic = new Comic(nombre, descripcion, imagen, precioOficial, Stock, numpaginas, editorial,
					añoPublicacion);
			tienda.añadirProducto(comic);
			for (Categoria cats : categorias) {

				cats.addProducto(comic);

			}
			this.recibirNotificacion("Has añadido el comic " + comic.getNombre() + " a la tienda");
			return true;

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
			for (Categoria cats : categorias) {

				cats.addProducto(juego);

			}
			this.recibirNotificacion("Has añadido el juego " + juego.getNombre() + " a la tienda");
			return true; // <-- faltaba el return Y el break

		case "F":
			if (altura <= 0 || ancho <= 0 || largo <= 0) {
				System.out.println("Las dimensiones deben ser positivas");
				return false;
			}
			if (material == null) {
				System.out.println("Las figuras deben tener material");
				return false;
			}
			if (marca == null) {
				System.out.println("Las figuras deben tener marca");
				return false;
			}
			ProductoVenta figura = new Figura(nombre, descripcion, imagen, precioOficial, Stock, altura, ancho, largo,
					material, marca);
			tienda.añadirProducto(figura);
			for (Categoria cats : categorias) {

				cats.addProducto(figura);

			}
			this.recibirNotificacion("Has añadido la figura " + figura.getNombre() + " a la tienda");
			return true;

		default:
			this.recibirNotificacion(
					"El tipo de producto que has intentado crear no es correcta. Deben ser Comics(C), Figuras(F) o Juegos(J)");
			return false;
		}
	}

	public boolean reponerStockProducto(String id, int cantidad) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {
			System.out.println("No tienes permiso para trabajar con productos");
			return false;
		}
		if (cantidad <= 0) {
			System.out.println("La cantidad a añadir tiene que ser mayor que 0");
			return false;
		}

		Tienda tienda = Tienda.getInstancia();
		for (ProductoVenta p : tienda.getStockVentas()) {
			if (p.getId().equals(id)) {
				// Caso en el que hayamos encontrado un producto que ya exista
				int unidades = p.getStockDisponible();
				unidades = unidades + cantidad;
				p.setStockDisponible(unidades);
				System.out.println("Se han añadiendo las unidades correctamente. Ahora el producto " + p.getId()
						+ " tiene " + p.getStockDisponible() + ".");
				return true;
			}
		}
		System.out.println("Este producto no existe en la lista de productos de venta de la tienda");
		return false;
	}

	public boolean cargarProductosFicheroTexto(String path) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_STOCK)) {
			return false;
		}
		if (path == null || path.isBlank()) {
			System.out.println("La ruta no puede estar vacia");
			return false;
		}
		int productos_nuevos_creados = 0;
		int productos_actualizados_stock = 0;
		int numLinea = 0; // 1. Creamos el contador de líneas

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String linea;
			boolean primeraLiena = true;

			while ((linea = br.readLine()) != null) {// leemos el archivo linea por linea. Hasta el final
				numLinea++;
				// Nos olvidamos de la primera linea. Esa sera el cabecero informativo
				if (primeraLiena) {
					primeraLiena = false;
					continue;
				}
				// ignoramos lineas vacias
				if (linea.isBlank()) {
					continue;
				}
				// Divide la línea en trozos cada vez que encuentra un ";".
				// El -1 permite que si al final hay campos vacíos, no se ignoren.
				String[] partes = linea.split(";", -1);

				// El caso en el que menos necesitaremos sera en el que el producto ya exista y
				// estemos rellenando stock. Necesitaremos tipo de producto id nombre y unidades
				if (partes.length < 4) {// OMITIMOS ESTA LINEA
					continue;
				}
				String tipo = partes[0].trim().toUpperCase();// Limpiamos los espacios en blanco de despues antes del
																// separador
				String id = partes[1].trim();
				String nombre = partes[2].trim();
				if (nombre.isBlank()) {
					// El nombre no puede estar vacio en ningun caso luego se omite la linea
					continue;
				}

				// Reponer Stock(Id NO vacio) FORMATO Tipo;id;nombre;unidades
				if (!id.isEmpty()) {

					// Necesitamos 4 campos: TIPO;ID;NOMBRE;UNIDADES
					if (partes.length < 4) {
						// ignoramos linea
						continue;
					}
					// comprobamos que existe:
					ProductoVenta existente = this.buscarProductoPorId(id);
					if (existente == null) {
						// Ignoramos la linea
						continue;
					}
					int unidades;
					try {
						unidades = Integer.parseInt(partes[3].trim());
					} catch (NumberFormatException e) {
						// Saltamos linea
						continue;
					}
					if (unidades <= 0) {
						// saltamos linea
						continue;
					}
					existente.setStockDisponible(existente.getStockDisponible() + unidades);
					System.out.println("Stock actualizado: " + existente.getNombre() + ": +" + unidades + " unidades.");
					productos_actualizados_stock++;
					continue;
				}

				// Para crear cualquier producto, necesitamos los 7 campos base comunes
				if (partes.length < 7) {
					System.out.println("Faltan datos base (precio/unidades/cat) para crear: " + nombre);
					continue;
				}
				try {
					double precio;
					int unidadesIni;
					try {
						precio = Double.parseDouble(partes[4].trim());
						unidadesIni = Integer.parseInt(partes[5].trim());
						if (precio <= 0 || unidadesIni < 0)
							throw new NumberFormatException();
					} catch (NumberFormatException e) {
						System.out.println("Precio o unidades inválidos en producto nuevo: " + nombre);
						continue;
					}
					ArrayList<Categoria> categorias = new ArrayList<>();
					if (!partes[6].isBlank()) {
						// si hay mas de una las separamos por ,
						for (String catNombre : partes[6].split(",")) {
							Categoria c = Tienda.getInstancia().buscarCategoriaPorNombre(catNombre.trim());
							if (c != null)
								categorias.add(c);
						}
					}
					try {

					} catch (Exception e) {
						// TODO: handle exception
					}
					ProductoVenta nuevo = null;
					boolean exito = false;
					switch (tipo) {
					case "C": // COMIC
						// Validamos que la línea llegue al menos hasta la columna 9 (Año)
						if (partes.length < 10) {
							// Faltan campos obligatorios para Comic (Págs/Ed/Año)
							continue;
						}

						int pags = Integer.parseInt(partes[7].trim());
						String ed = partes[8].trim();
						int año = Integer.parseInt(partes[9].trim());

						exito = añadirProducto_nuevo("C", nombre, partes[3].trim(), "", precio, unidadesIni, categorias,
								pags, ed, año, 0, 0, 0, null, null, 0, 0, 0, 0, null);

						break;

					case "J": // JUEGO DE MESA
						// Validamos que llegue hasta la columna 14 (Tipo Juego)
						if (partes.length < 15) {
							// Faltan campos obligatorios para Juego de Mesa
							continue;
						}

						int minJ = Integer.parseInt(partes[10].trim());
						int maxJ = Integer.parseInt(partes[11].trim());
						int minE = Integer.parseInt(partes[12].trim());
						int maxE = Integer.parseInt(partes[13].trim());
						String tJuego = partes[14].trim();
						exito = añadirProducto_nuevo("J", nombre, partes[3].trim(), "", precio, unidadesIni, categorias,
								0, null, 0, 0, 0, 0, null, null, minJ, maxJ, minE, maxE, tJuego);
						break;

					case "F": // FIGURA
						// Validamos que llegue hasta la columna 19 (Marca)
						if (partes.length < 20) {
							// Faltan campos obligatorios para Figura (Dim/Mat/Marca)
							continue;
						}

						double alt = Double.parseDouble(partes[15].trim());
						double anc = Double.parseDouble(partes[16].trim());
						double lar = Double.parseDouble(partes[17].trim());
						String mat = partes[18].trim();
						String marca = partes[19].trim();
						exito = añadirProducto_nuevo("F", nombre, partes[3].trim(), "", precio, unidadesIni, categorias,
								0, null, 0, alt, anc, lar, mat, marca, 0, 0, 0, 0, null);

						break;

					default:
						System.out.println("Tipo de producto desconocido: " + tipo);
					}
					if (exito) {
						productos_nuevos_creados++;
					}
				} catch (NumberFormatException e) {
					System.out.println("Error de formato numérico en la línea: " + numLinea);
				}

			}
			this.recibirNotificacion("Fichero procesado. Creados: " + productos_nuevos_creados + ", Actualizados: "
					+ productos_actualizados_stock);
			return true;
		} catch (IOException e) {
			System.out.println("Error al leer el fichero: " + e.getMessage());
			return false;
		}

	}

	public boolean prepararPedido(String idPedido) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PEDIDOS))
			return false;

		Pedido ped = buscarPedidoPorId(idPedido);
		if (ped == null)
			return false;

		if (ped.getEstado() != EstadoPedido.PAGADO) {
			System.out.println("El pedido " + idPedido + " no se ha podido preparar.");
			return false;
		}

		boolean ok = ped.marcarPreparado();
		if (ok) {
			ped.getCliente().recibirNotificacionTipo("Tu pedido con codigo de recogida " + ped.getCodigoRecogida()
					+ " está preparado. Puedes recogerlo.", TipoNotificacion.PEDIDO_LISTO);
		}
		return ok;
	}

	public boolean entregarPedido(String codigoRecogida) {

		if (!puedeRealizarTarea(TipoPermisos.ENTREGA_PEDIDOS)) {
			System.out.println("No tienes permiso para entregar con pedidos");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();
		for (Pedido ped : tienda.getHistorialVentas()) {
			if (ped.getCodigoRecogida().equals(codigoRecogida) && (ped.getEstado() == EstadoPedido.LISTO_PARA_RECOGER)
					&& ped.isRecogida_solicitada()) {
				ped.marcarEntregado();
				ped.getCliente().recibirNotificacionTipo(
						"Tu pedido con codigo de recogida " + ped.getCodigoRecogida() + " ha sido entregado con exito",
						TipoNotificacion.PEDIDO_ENTREGADO);
				this.recibirNotificacion("Has entregado corrrectamente el pedido con codigo de recogida"
						+ ped.getCodigoRecogida() + " al cliente " + ped.getCliente().getNickname() + ".");
				return true;
			}
		}
		System.out.println("No se ha podido entregar el pedido correctamente");
		return false;
	}

	public boolean añadirProductoACategoria(String idProducto, String nombreCat) {
		if (idProducto == null || nombreCat == null) {
			System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.GESTION_CATEGORIAS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene el permiso de gestion de categorias.");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();

		ProductoVenta p = tienda.buscarProductoVentaPorId(idProducto);
		if (p == null) {
			System.out.println("No existe ningún producto con id: " + idProducto);
			return false;
		}
		Categoria c = tienda.buscarCategoriaPorNombre(nombreCat);
		if (c == null) {
			System.out.println("No existe ninguna categoría con nombre: " + nombreCat);
			return false;
		}

		boolean añadido = c.addProducto(p);
		if (añadido) {
			for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {

				cliente.notificarProductoNuevoCategoria(
						"Nuevo producto en la categoria" + c.getNombre() + ": " + p.getNombre() + ".", nombreCat);
			}
		}
		return añadido;

	}

	public boolean eliminarProductoDeCategoria(String idProducto, String nombreCat) {
		if (idProducto == null || nombreCat == null) {
			System.out.println("El id del producto o el nombre de la categoría no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.GESTION_CATEGORIAS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene el permiso de gestion de categorias.");
			return false;
		}
		Tienda tienda = Tienda.getInstancia();

		ProductoVenta p = tienda.buscarProductoVentaPorId(idProducto);
		if (p == null) {
			System.out.println("No existe ningún producto con id: " + idProducto);
			return false;
		}
		Categoria c = tienda.buscarCategoriaPorNombre(nombreCat);
		if (c == null) {
			System.out.println("No existe ninguna categoría con nombre: " + nombreCat);
			return false;
		}

		boolean eliminado = c.deleteProducto(p);
		if (eliminado) {
			for (Cliente cliente : Tienda.getInstancia().obtenerClientesTienda()) {

				cliente.notificarProductoNuevoCategoria(
						"Se ha eliminado el producto " + p.getNombre() + " de la categoria " + nombreCat + ".",
						nombreCat);
			}
		}
		return eliminado;
	}

	public boolean crearPack(String nombre, String descripcion, String imagen, double precioOficial, int stock,
			ArrayList<LineaPack> lineas) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene permiso para trabajar con packs.");
			return false;
		}
		if (nombre == null || nombre.isBlank() || descripcion == null || descripcion.isBlank() || imagen == null) {
			System.out.println("El nombre, descripcion o imagen no pueden estar vacios");
			return false;
		}
		if (stock <= 0) {
			System.out.println("El stock debe ser mayor que 0");
			return false;
		}
		if (lineas == null || lineas.size() <= 1) {
			System.out.println("Para crear un pack minimo tiene que haber dos productos distintos");
			return false;
		}
		Pack p = new Pack(nombre, descripcion, imagen, precioOficial, stock, lineas);
		Tienda.getInstancia().añadirProducto(p);
		this.recibirNotificacion("Has creado el pack " + nombre + " correctamente.");
		return true;
	}

	public boolean añadirProductoaPack(String idProducto, String idPack, int unidades) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS)) {
			System.out.println("El empleado " + this.getNickname() + " no tiene permiso para trabajar con packs.");
			return false;
		}
		if (idPack == null || idPack.isBlank()) {
			System.out.println("el id del pack no puede estar vacio");
			return false;
		}
		if (idProducto == null || idProducto.isBlank()) {
			System.out.println("el id del producto no puede estar vacio");
			return false;
		}
		if (unidades <= 0) {
			System.out.println("El minimo de unidades tiene que ser 1.");
			return false;
		}
		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idPack);
		if (pack == null) {

			System.out.println("No existe ningun pack de productos en la tienda con id " + idPack + ".");
			return false;
		}
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (producto == null) {
			System.out.println("No exxiste ningun producto en el catalogo de la tienda con id " + idProducto + ".");
			return false;
		}
		return ((Pack) pack).addProducto(producto, unidades);
	}

	public boolean eliminarProductoDePack(String idPack, String idProducto) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS))
			return false;

		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idPack);
		if (pack == null || !(pack instanceof Pack)) {
			System.out.println("No existe ningún pack con id: " + idPack);
			return false;
		}
		ProductoVenta producto = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (producto == null) {
			System.out.println("No existe ningún producto con id: " + idProducto);
			return false;
		}
		return ((Pack) pack).eliminarLinea(producto);
	}

	public boolean modificarPrecioPack(String idPack, double nuevoPrecio) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS))
			return false;

		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idPack);
		if (pack == null || !(pack instanceof Pack)) {
			System.out.println("No existe ningún pack con id: " + idPack);
			return false;
		}
		return ((Pack) pack).setPrecioOficial(nuevoPrecio);
	}

	public boolean eliminarPack(String idpack) {
		if (!puedeRealizarTarea(TipoPermisos.GESTION_PACKS)) {
			System.out.println("El empleado " + nickname + " no pude modificar packs");
		}
		ProductoVenta pack = Tienda.getInstancia().buscarProductoVentaPorId(idpack);
		if (pack == null || !(pack instanceof Pack)) {
			System.out.println("No existe ningún pack con id: " + idpack);
			return false;
		}
		// Liberamos el stock de los productos del pack
		((Pack) pack).getLineas().forEach(lp -> {
			lp.getProducto().setStockDisponible(
					lp.getProducto().getStockDisponible() + lp.getUnidades() * pack.getStockDisponible());
		});
		Tienda.getInstancia().getStockVentas().remove(pack);
		System.out.println("Pack " + idpack + " eliminado correctamente.");
		return true;
	}

	public boolean modificarDescripcionProducto(String idProducto, String descripcion) {
		if (idProducto == null || descripcion == null) {
			System.out.println("El id del producto o la descripción no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.MODIFICAR_PRODUCTO))
			return false;

		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (p == null)
			return false;

		p.setDescripcion(descripcion);
		System.out.println("Descripción del producto " + idProducto + " modificada correctamente.");
		return true;
	}

	public boolean modificarImagenProducto(String idProducto, String imagen) {
		if (idProducto == null || imagen == null) {
			System.out.println("El id del producto o la imagen no pueden ser null.");
			return false;
		}
		if (!puedeRealizarTarea(TipoPermisos.MODIFICAR_PRODUCTO))
			return false;

		ProductoVenta p = Tienda.getInstancia().buscarProductoVentaPorId(idProducto);
		if (p == null)
			return false;

		p.setImagenRuta(imagen);
		System.out.println("Imagen del producto " + idProducto + " modificada correctamente.");
		return true;
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

	public void recibirNotificacion(String mensaje) {
		this.notificaciones.add(new Notificacion(mensaje));
		System.out.println("[Notificación Empleado]: " + mensaje);
	}

	/*
	 * public void asignarTodosLosPermisos() { this.permisos =
	 * EnumSet.allOf(Permiso.class); }
	 */
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

	public boolean isDespedido() {
		return despedido;
	}

	public void setDespedido(boolean despedido) {
		this.despedido = despedido;
	}

	@Override
	public String toString() {
		return "Empleado [id=" + getId() + ", nickname=" + getNickname() + "]";
	}
}
