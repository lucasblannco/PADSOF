package productos;

import java.util.ArrayList;

import Excepcion.ProductoInvalidoException;
import Excepcion.ProductoYaEnPackException;
import Excepcion.StockInsuficienteParaPackException;

public class Pack extends ProductoVenta {
	private ArrayList<LineaPack> lineas;

	public Pack(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.lineas = new ArrayList<>();
	}

	public Pack(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible,
			ArrayList<LineaPack> lineas) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.lineas = new ArrayList<>();

		for (LineaPack lp : lineas) {
			this.addLinea(lp);
		}
	}

	// AÑADIR OTRO TIPO DE PRODUCTO AL PACK
	public boolean addLinea(LineaPack lp) {
		if (lp == null) {
			throw new ProductoInvalidoException("La línea del pack no puede ser null.");
		}
		if (contieneProducto(lp.getProducto())) {
			throw new ProductoYaEnPackException(
					"El producto " + lp.getProducto().getNombre() + " ya está incluido en el pack.");
		}
		if (lp.getProducto() == this) {
			throw new ProductoInvalidoException("Un pack no puede contenerse a sí mismo.");
		}
		if (lp.getProducto().getStockDisponible() < lp.getUnidades() * this.stockDisponible) {
			throw new StockInsuficienteParaPackException(
					"No hay stock suficiente para añadir " + lp.getProducto().getNombre() + " al pack.");
		}

		this.lineas.add(lp);

		lp.getProducto()
				.setStockDisponible(lp.getProducto().getStockDisponible() - lp.getUnidades() * this.stockDisponible);
		return true;
	}

	public boolean eliminarLinea(ProductoVenta p) {
		if (p == null)
			return false;

		LineaPack lineaAEliminar = null;
		for (LineaPack lp : this.lineas) {
			if (lp.getProducto().getId().equals(p.getId())) {
				lineaAEliminar = lp;
				break;
			}
		}

		if (lineaAEliminar == null)
			return false;

		this.lineas.remove(lineaAEliminar);
		lineaAEliminar.getProducto().setStockDisponible(lineaAEliminar.getProducto().getStockDisponible()
				+ lineaAEliminar.getUnidades() * this.stockDisponible);
		return true;
	}

	public boolean modificarUnidades(ProductoVenta p, int nuevasUnidades) {
		if (p == null) {
			throw new ProductoInvalidoException("El producto no puede ser null.");
		}
		if (nuevasUnidades < 0) {
			throw new ProductoInvalidoException("Las nuevas unidades no pueden ser negativas.");
		}

		if (nuevasUnidades == 0) {
			return eliminarLinea(p);
		}

		LineaPack lineaActual = null;
		for (LineaPack lp : this.lineas) {
			if (lp.getProducto().getId().equals(p.getId())) {
				lineaActual = lp;
				break;
			}
		}

		if (lineaActual == null) {
			return false;
		}

		int unidadesActuales = lineaActual.getUnidades();
		int diferencia = nuevasUnidades - unidadesActuales;

		if (diferencia == 0)
			return true;

		if (diferencia > 0) {
			int stockNecesario = diferencia * this.stockDisponible;
			if (p.getStockDisponible() < stockNecesario) {
				throw new StockInsuficienteParaPackException(
						"No hay stock suficiente para modificar las unidades de " + p.getNombre() + " en el pack.");
			}
		}

		lineaActual.setUnidades(nuevasUnidades);

		if (this.precioOficial >= calcularSumaProductos() - 1) {
			lineaActual.setUnidades(unidadesActuales);
			throw new ProductoInvalidoException(
					"El precio del pack debe ser al menos un euro menor que la suma de sus productos.");
		}

		int ajusteStock = diferencia * this.stockDisponible;
		p.setStockDisponible(p.getStockDisponible() - ajusteStock);
		return true;
	}

	public boolean addProducto(ProductoVenta p, int unidades) {
		if (p == null) {
			throw new ProductoInvalidoException("El producto no puede ser null.");
		}
		if (unidades <= 0) {
			throw new ProductoInvalidoException("Las unidades deben ser mayores que 0.");
		}
		if (p == this) {
			throw new ProductoInvalidoException("Un pack no puede contenerse a sí mismo.");
		}
		if (contieneProducto(p)) {
			throw new ProductoYaEnPackException("El producto " + p.getNombre() + " ya está incluido en el pack.");
		}

		LineaPack lp = new LineaPack(p, unidades);
		return addLinea(lp);
	}

	public boolean addProducto_conunaUnidad(ProductoVenta p) {
		if (p == null) {
			throw new ProductoInvalidoException("El producto no puede ser null.");
		}
		if (p == this) {
			throw new ProductoInvalidoException("Un pack no puede contenerse a sí mismo.");
		}
		if (contieneProducto(p)) {
			throw new ProductoYaEnPackException("El producto " + p.getNombre() + " ya está incluido en el pack.");
		}
		return addProducto(p, 1);
	}

	public boolean contieneProducto(ProductoVenta p) {
		if (p == null)
			return false;
		for (LineaPack lp : this.lineas) {
			if (lp.getProducto().getId().equals(p.getId()))
				return true;
		}
		return false;
	}

	public double calcularSumaProductos() {
		double suma = 0;
		for (LineaPack lp : this.lineas) {
			suma += lp.getSubtotal();
		}
		return suma;
	}

	public double calcularPrecioFinal() {
		return this.precioOficial;
	}

	public boolean setPrecioOficial(double nuevoPrecio) {
		if (nuevoPrecio <= 0) {
			throw new ProductoInvalidoException("El precio del pack debe ser mayor que 0.");
		}
		if (!this.lineas.isEmpty() && nuevoPrecio >= calcularSumaProductos() - 1) {
			throw new ProductoInvalidoException(
					"El precio del pack debe ser al menos un euro menor que la suma (" + calcularSumaProductos() + ")");
		}
		this.precioOficial = nuevoPrecio;
		return true;
	}

	@Override
	public double getPrecioOficial() {
		return calcularPrecioFinal();
	}

	@Override
	public String toString() {
		String textoLineas = this.lineas.isEmpty() ? "sin productos" : "";

		for (LineaPack lp : this.lineas) {
			textoLineas += lp.getProducto().getNombre() + " x" + lp.getUnidades() + " = " + lp.getSubtotal() + "€; ";
		}

		return super.toString() + " | Precio pack: " + this.precioOficial + "€" + " | Stock pack: "
				+ this.stockDisponible + " | Suma productos: " + this.calcularSumaProductos() + "€" + " | Líneas: "
				+ textoLineas + "|";
	}

	public ArrayList<LineaPack> getLineas() {
		return lineas;
	}

	public void setLineas(ArrayList<LineaPack> lineas) {
		this.lineas = lineas;
	}

	public void resumenPrecios() {
		System.out.println("Resumen de precios:");
		System.out.println(" Suma productos: " + this.calcularSumaProductos() + "€");
		System.out.println(" Precio actual:  " + this.calcularPrecioFinal() + "€");

		double ahorro = this.calcularSumaProductos() - this.calcularPrecioFinal();
		System.out.printf(" Ahorro total:   %.2f€\n", ahorro);
	}
}
