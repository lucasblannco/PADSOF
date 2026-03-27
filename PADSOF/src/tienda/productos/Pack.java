package productos;

import java.util.ArrayList;

public class Pack extends ProductoVenta {
	private ArrayList<ProductoVenta> productosIncluidos;

	public Pack(String nombre, String descripcion, String imagenRuta, double precioOficial, int stockDisponible) {
		super(nombre, descripcion, imagenRuta, precioOficial, stockDisponible);
		this.productosIncluidos = new ArrayList<>();
	}

	public boolean addProducto(ProductoVenta p) {
		if (p == null || contieneProducto(p)) {
			return false;
		}

		if (p.getStockDisponible() < this.stockDisponible) {
			return false;
		}

		this.productosIncluidos.add(p);

		if (this.precioOficial >= calcularSumaProductos()) {
			this.productosIncluidos.remove(p);
			return false;
		}

		p.setStockDisponible(p.getStockDisponible() - this.stockDisponible);
		return true;
	}

	public boolean eliminarProducto(ProductoVenta p) {
		if (p == null || !contieneProducto(p)) {
			return false;
		}

		this.productosIncluidos.remove(p);

		if (!this.productosIncluidos.isEmpty() && this.precioOficial >= calcularSumaProductos()) {
			this.productosIncluidos.add(p);
			return false;
		}

		p.setStockDisponible(p.getStockDisponible() + this.stockDisponible);
		return true;
	}

	public double calcularSumaProductos() {
		double suma = 0;
		for (ProductoVenta p : this.productosIncluidos) {
			suma += p.getPrecioVenta();
		}
		return suma;
	}

	public double calcularPrecioFinal() {
		return this.precioOficial;
	}

	public boolean contieneProducto(ProductoVenta p) {
		if (p == null) {
			return false;
		}

		for (ProductoVenta pv : this.productosIncluidos) {
			if (pv.getId().equals(p.getId())) {
				return true;
			}
		}
		return false;
	}

	public boolean setPrecioOficial(double nuevoPrecio) {
		if (nuevoPrecio <= 0) {
			return false;
		}

		if (!this.productosIncluidos.isEmpty() && nuevoPrecio >= calcularSumaProductos()) {
			System.out.println("El precio del pack debe ser menor que la suma (" + calcularSumaProductos() + ")");
			return false;
		}

		this.precioOficial = nuevoPrecio;
		return true;
	}

	@Override
	public double getPrecioVenta() {
		return calcularPrecioFinal();
	}

	@Override
	public String toString() {
		String s = "";

		for (ProductoVenta pv : this.productosIncluidos) {
			s += pv.toString() + "\n";
		}

		return super.toString() + "\n" + s;
	}
}
