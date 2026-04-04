package productos;

import java.util.ArrayList;

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

	// AñADIR OTRO TIPO DE PRODUCTO AL PACK
	public boolean addLinea(LineaPack lp) {
		if (lp == null) {
			return false;
		}
		if (contieneProducto(lp.getProducto())) {
			return false;
		}
		if (lp.getProducto() == this) {
			return false;
		}
		if (lp.getProducto().getStockDisponible() < lp.getUnidades() * this.stockDisponible) {
			return false;
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
		if (p == null || nuevasUnidades < 0) {
			return false;
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
				return false;
			}
		}

		lineaActual.setUnidades(nuevasUnidades);

		if (this.precioOficial >= calcularSumaProductos() - 1) {
			lineaActual.setUnidades(unidadesActuales);
			return false;
		}

		int ajusteStock = diferencia * this.stockDisponible;
		p.setStockDisponible(p.getStockDisponible() - ajusteStock);
		return true;
	}

	public boolean addProducto(ProductoVenta p, int unidades) {
		if (p == null || unidades <= 0) {
			return false;
		}
		if (p == this) {
			return false;
		}
		if (contieneProducto(p)) {
			return false;
		}

		LineaPack lp = new LineaPack(p, unidades);
		return addLinea(lp);
	}

	public boolean addProducto_conunaUnidad(ProductoVenta p) {
		if (p == null || p == this || contieneProducto(p)) {
			return false;
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
		if (nuevoPrecio <= 0)
			return false;
		if (!this.lineas.isEmpty() && nuevoPrecio >= calcularSumaProductos() - 1) {
			System.out.println(
					"El precio del pack debe ser al menos un euro menor que la suma (" + calcularSumaProductos() + ")");
			return false;
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
	    
	    // Calculamos el ahorro (opcional, pero queda muy bien)
	    double ahorro = this.calcularSumaProductos() - this.calcularPrecioFinal();
	    System.out.printf(" Ahorro total:   %.2f€\n", ahorro);
	  
	}
}
