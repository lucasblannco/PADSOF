package tienda;

import java.util.*;

import Excepcion.PesosInvalidosException;
import Excepcion.RecomendadorNoActivoException;
import productos.*;
import usuarios.*;
import ventas.*;

public class Recomendador {

	private int limiteMaximo = 5;
	private boolean activo = true;

	// Pesos de cada criterio. Se normalizan en setPesos, deben ser >= 0.
	private double pesoValoracion = 0.34;
	private double pesoCompras = 0.33;
	private double pesoCategorias = 0.33;

	public void imprimirSugerencias(Cliente cliente) throws RecomendadorNoActivoException {
	    List<ProductoVenta> sugerencias = generarSugerencias(cliente);
	    if (sugerencias.isEmpty()) {
	        System.out.println("  No hay sugerencias para " + cliente.getNickname());
	        return;
	    }
	    System.out.println("  Sugerencias para " + cliente.getNickname()
	        + " (" + sugerencias.size() + "):");
	    for (ProductoVenta p : sugerencias) {
	        System.out.println("   - " + p.resumen());
	    }
	}

	public List<ProductoVenta> generarSugerencias(Cliente cliente) throws RecomendadorNoActivoException  {
		if (!activo) {
	        throw new RecomendadorNoActivoException();
	    }
		
		if ( cliente == null) {
			return new ArrayList<>();
		}
		
		

		
		Set<String> excluidos = obtenerIdsExcluidos(cliente);
		//cogemos toda la informacion
		List<ProductoVenta> porValoracion = recomendarPorValoracion(cliente, limiteMaximo, excluidos);
		List<ProductoVenta> porCompras = recomendarPorCompras(cliente, limiteMaximo, excluidos);
		List<ProductoVenta> porCategorias = recomendarPorCategorias(cliente, limiteMaximo, excluidos);

		Map<String, Double> puntuaciones = new HashMap<>();
		Map<String, ProductoVenta> porId = new HashMap<>();
		//añadimos puntuaciones
		acumularPuntuaciones(porValoracion, pesoValoracion, puntuaciones, porId);
		acumularPuntuaciones(porCompras, pesoCompras, puntuaciones, porId);
		acumularPuntuaciones(porCategorias, pesoCategorias, puntuaciones, porId);
		//los resultados son ordenados segun su puntuacion, como siempre
		List<ProductoVenta> resultado = new ArrayList<>(porId.values());
		resultado.sort(
				Comparator.comparingDouble((ProductoVenta p) -> puntuaciones.getOrDefault(p.getId(), 0.0)).reversed());

		return resultado.subList(0, Math.min(limiteMaximo, resultado.size()));// deuvleve solo la lista desde la
																				// posicion 0 hasta el limite o menos, si no
																				// hay n productos
	}

	// Ordena el stock por media de puntuacion descendente,
	// los excluidos son los prpductos comprados o en carrito
	private List<ProductoVenta> recomendarPorValoracion(Cliente cliente, int n, Set<String> excluidos) {
		List<ProductoVenta> candidatos = new ArrayList<>();
		for (ProductoVenta p : Tienda.getInstancia().getStockVentas()) {
			if (!excluidos.contains(p.getId()) && p.getStockDisponible() > 0) {
				candidatos.add(p);
			}
		}
		// ordenamos la lista (sort) en base a (Comparator.comparingDOuble) de un
		// double, que es la ountuacion media.
		// el reserved solo hace q el q mayor puntuacion tenga este Aprimero
		candidatos.sort(Comparator.comparingDouble(ProductoVenta::getMediaPuntuacion).reversed());
		return candidatos.subList(0, Math.min(n, candidatos.size()));// devuelve la li
	}

	// Paco y tu comprais una carta de pokemon, y paco compra un peluche de pikachu,
	// te lo recomiendo
	private List<ProductoVenta> recomendarPorCompras(Cliente cliente, int n, Set<String> excluidos) {

		Set<String> compradosPorCliente = new HashSet<>();
		for (Pedido ped : cliente.getHistorialPedidos()) {
			if (ped.getEstado() != EstadoPedido.CANCELADO) {
				for (LineaPedido l : ped.getLineas()) {
					compradosPorCliente.add(l.getProducto().getId());
				}
			}
		}

		// Contamos cuantas veces aparece cada producto en pedidos de otros
		// clientes que tienen al menos una compra en comun con el nuestro
		Map<String, Integer> frecuencia = new HashMap<>();
		Map<String, ProductoVenta> porId = new HashMap<>(); // relacion de id y PV
		// si un usuario ha comprado un producto que ha comprado el nuestro, se tienen
		// en cuenta todas las cosas que ha comprado
		for (UsuarioRegistrado u : Tienda.getInstancia().getUsuarios()) {
			// q u sea ciente y sea distinto del cliente en cuestion
			if (!(u instanceof Cliente) || u.equals(cliente))
				continue;
			Cliente otro = (Cliente) u;

			boolean tieneEnComun = false;
			for (Pedido ped : otro.getHistorialPedidos()) {
				if (ped.getEstado() == EstadoPedido.CANCELADO)
					continue;
				for (LineaPedido l : ped.getLineas()) {
					if (compradosPorCliente.contains(l.getProducto().getId())) {
						// encontramos que un cliente ha comprado un producto que ha comprado nuestro
						// cliente
						tieneEnComun = true;
						break;
					}
				}
				if (tieneEnComun)
					break;
			}
			if (!tieneEnComun)
				continue;

			for (Pedido ped : otro.getHistorialPedidos()) {
				if (ped.getEstado() == EstadoPedido.CANCELADO)
					continue;
				for (LineaPedido l : ped.getLineas()) {
					ProductoVenta p = l.getProducto();
					// miramos si un producto comprado por este nuevo cliente es valido
					if (!excluidos.contains(p.getId()) && p.getStockDisponible() > 0) {
						frecuencia.merge(p.getId(), 1, Integer::sum);// si no existe, crea una entrada en el hashmap, si
																		// existe, suma uno
						porId.put(p.getId(), p);// relacion nueva
					}
				}
			}
		}
		// nos quedamos con los productos que hemos encontrado
		List<ProductoVenta> candidatos = new ArrayList<>(porId.values());
		candidatos.sort(Comparator.comparingInt((ProductoVenta p) -> frecuencia.getOrDefault(p.getId(), 0)).reversed());
		// ordena nuestra lista de candidatos segun el valor del mapa ( sufrecuecia) de
		// mayor a menor
		return candidatos.subList(0, Math.min(n, candidatos.size()));
	}

	// Recomienda productos de la categoria favorita del cliente
	private List<ProductoVenta> recomendarPorCategorias(Cliente cliente, int n, Set<String> excluidos) {
		Categoria favorita = cliente.determinarCategoriaFavorita();
		if (favorita == null) {

			return new ArrayList<>();
		}

		List<ProductoVenta> candidatos = new ArrayList<>();
		for (ProductoVenta p : Tienda.getInstancia().getStockVentas()) {
			if (!excluidos.contains(p.getId()) && p.getStockDisponible() > 0 && p.getCategorias().contains(favorita)) {
				candidatos.add(p);// no esta excluido, esta disponble y esta en la cateoria buscada
			}
		}

		candidatos.sort(Comparator.comparingDouble(ProductoVenta::getMediaPuntuacion).reversed()); // ademas, ordenado
																									// por puntuacion
		return candidatos.subList(0, Math.min(n, candidatos.size()));
	}

	// productos pedidos o en carrito
	private Set<String> obtenerIdsExcluidos(Cliente cliente) {
		Set<String> ids = new HashSet<>();
		for (Pedido ped : cliente.getHistorialPedidos()) {
			if (ped.getEstado() != EstadoPedido.CANCELADO) {
				for (LineaPedido l : ped.getLineas()) {
					ids.add(l.getProducto().getId());// todos los productos pedidos pa dentro
				}
			}
		}
		if (cliente.getCarritoActual() != null) {
			for (LineaCarrito l : cliente.getCarritoActual().getLineas()) {
				ids.add(l.getProducto().getId());// todos los productos en carrito pa dentro
			}
		}
		return ids;
	}

	// por cada peso, lo sumado se calkcula segun su posicion y el peso de la losta en la que ocipa esa posicion
	// peso * (n-i)
	private void acumularPuntuaciones(List<ProductoVenta> lista, double peso, Map<String, Double> puntuaciones,
			Map<String, ProductoVenta> porId) {
		if(peso==0) return; //si no tiene peso, ni lo tenemos en cuenta
		int n = lista.size();
		for (int i = 0; i < n; i++) {
			ProductoVenta p = lista.get(i);
			puntuaciones.merge(p.getId(), peso * (n - i), Double::sum);//lo mismo que antes, añade una entrada al mapa o si ya esta, añade el valor dado
			porId.put(p.getId(), p);
		}
	}

	// ── Configuracion por el Gestor ───────────────────────────────────────────

	public void setConfiguracion(int limite, boolean estado) {
		if (limite <= 0) {
			System.out.println("El limite debe ser mayor que 0.");
			return;
		}
		this.limiteMaximo = limite;
		this.activo = estado;
	}

	
	public void setPesos(double pesoValoracion, double pesoCompras, double pesoCategorias) throws PesosInvalidosException {
		if (pesoValoracion < 0 || pesoCompras < 0 || pesoCategorias < 0 || (pesoValoracion + pesoCompras + pesoCategorias == 0)) {
	        throw new PesosInvalidosException(pesoValoracion, pesoCompras, pesoCategorias);
	    }
		double suma = pesoValoracion + pesoCompras + pesoCategorias;
		if (suma == 0) {
			System.out.println("Al menos un peso debe ser mayor que 0.");
			return;
		}
		//normalizamos los pesos
		this.pesoValoracion = pesoValoracion / suma;
		this.pesoCompras = pesoCompras / suma;
		this.pesoCategorias = pesoCategorias / suma;
	}

	public int getLimiteMaximo() {
		return limiteMaximo;
	}

	public boolean isActivo() {
		return activo;
	}

	public double getPesoValoracion() {
		return pesoValoracion;
	}

	public double getPesoCompras() {
		return pesoCompras;
	}

	public double getPesoCategorias() {
		return pesoCategorias;
	}
}