package ventas;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Iterator;
import java.time.LocalDateTime;
import productos.ProductoVenta;

public class Carrito {
	private String idCarrito;
	private List<LineaCarrito> lineas;
	private LocalDateTime fechaCreacion;
	private Descuento descuentoAplicado;
	private double total;

	public Carrito(List<LineaCarrito> lineas, LocalDateTime fechaCreacion, Descuento descuentoAplicado, double total) {

		this.idCarrito = UUID.randomUUID().toString().substring(0, 8);
		this.lineas = lineas;
		this.fechaCreacion = fechaCreacion;
		this.descuentoAplicado = descuentoAplicado;
		this.total = total;
	}

	public boolean añadirProducto (ProductoVenta p, int cantidad) {
		if(p==null || cantidad<1 || p.getStockDisponible() < cantidad) {
			return false;
		}
		
		for(LineaCarrito l: this.lineas) {
			if(l.productoPertence(p) == true) {
				l.setCantidad( cantidad + l.getCantidad());
				p.setStockDisponible(p.getStockDisponible() - cantidad);
				return true;
			}
		}
		
		LineaCarrito lc = new LineaCarrito(p, cantidad);
		if(this.lineas.add(lc)== false) {
		return false;
		}
		p.setStockDisponible(p.getStockDisponible() - cantidad);
		
		return true;
	}

	public boolean eliminarProducto (ProductoVenta p) {
		if(p==null) {
			return false;
		}
		
	}
}}

si el carrito tiene un elementos e crea, si llamas a eliminar producto estando solo uno 


}