package intercambios;

import intercambios.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import tienda.Estadistica;
import tienda.Tienda;
import productos.*;
import usuarios.*;

public class Oferta {
	private String id;
	private LocalDateTime fechaOferta;
	private EstadoOferta estado;
	private Cliente origen;
	private Cliente destino;
	private List<Producto2Mano> productosOfertados;
	private List<Producto2Mano> productosSolicitados;

	public Oferta(Cliente origen, Cliente destino, List<Producto2Mano> productosOfertados,
			List<Producto2Mano> productosSolicitados) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "OFER-" + String.valueOf(est.getnIntercambiosFinalizados());
		est.setnIntercambiosFinalizados(est.getnIntercambiosFinalizados() + 1);
		this.fechaOferta = LocalDateTime.now();
		this.estado = EstadoOferta.PENDIENTE;
		this.origen = origen;
		this.destino = destino;
		this.productosOfertados = productosOfertados;
		this.productosSolicitados = productosSolicitados;
	}

	public void rechazar() {
		this.estado = EstadoOferta.RECHAZADA;
		// Importante: desbloqueamos los productos para que vuelvan a estar disponibles
		for (Producto2Mano p : productosOfertados)
			p.setBloqueado(false);// Los productos ofertados por el cliente que ha
		// propuesto la oferta son desbloqueados y despues los vamos a poder usar para
		// mas ofertas
		this.origen.getOfertasPendientes().remove(this);
		this.destino.getOfertasPendientes().remove(this);
		this.origen.recibirNotificacion("Tu oferta con ID " + this.getId() + " ha sido RECHAZADA por el cliente"
				+ this.destino.getNickname() + ".");
	}

	public void aceptarOferta() {
		this.estado = EstadoOferta.ACEPTADA;
	}

	public void aceptarYEjecutar() {
		origen.getHistorialIntercambios().add(this);
		destino.getHistorialIntercambios().add(this);
		origen.getOfertasPendientes().remove(this);
		destino.getOfertasPendientes().remove(this);

		for (Producto2Mano p : this.productosOfertados) {
			origen.getCarteraIntercambio().remove(p);
			p.setBloqueado(false);
			// AHORA SE ENVIARIAN
		}
		for (Producto2Mano p : productosSolicitados) {
			destino.getCarteraIntercambio().remove(p);
			// AHORA SE ENVIARIAN
		}
		Tienda.getInstancia().registrarIntercambioFinalizado(this);
		this.origen.recibirNotificacion("¡Intercambio ID " + this.id + " aceptado por el usuario"
				+ this.getDestino().getNickname() + "! Preparando envío.");
		this.destino.recibirNotificacion("Has aceptado el intercambio con el usuario" + this.origen.getNickname()
				+ ". Los productos han salido de tu inventario.");
		 this.estado = EstadoOferta.REALIZADA; 
	}

	public boolean haCaducado() {
		int tiempoMaxOferta	=Tienda.getInstancia().getTiempoMaxOferta();
		 return LocalDateTime.now().isAfter(fechaOferta.plusMinutes(tiempoMaxOferta));//Comprobamos si el tiempo en el que finaliza la oferta es anterior al tiempo real de ahora
	}

	// Getters y Setters
	public String getId() {
		return id;
	}

	public LocalDateTime getFechaOferta() {
		return fechaOferta;
	}

	public EstadoOferta getEstado() {
		return estado;
	}

	public void setEstado(EstadoOferta estado) {
		this.estado = estado;
	}

	public List<Producto2Mano> getProductosOfertados() {
		return productosOfertados;
	}

	public List<Producto2Mano> getProductosSolicitados() {
		return productosSolicitados;
	}

	public Cliente getOrigen() {
		return this.origen;
	}

	public Cliente getDestino() {
		return this.destino;
	}
}

/*
 * GUARDADO AQUI POR SI ACASO package tienda.Intercambios;
 * 
 * import java.time.LocalDateTime; import java.util.List; import
 * java.util.ArrayList; import tienda.Usuarios.Cliente; import tienda.Tienda;
 * import tienda.Productos.ProductoSegundaMano;
 * 
 * 
 * public class Oferta {     private String id;     private LocalDateTime
 * fechaOferta;     private EstadoOferta estado;           private Cliente
 * origen;      private Cliente destino;           private
 * List<ProductoSegundaMano> productosOfertados;     private
 * List<ProductoSegundaMano> productosSolicitados;
 * 
 *     public Oferta(Cliente origen, Cliente destino,List<ProductoSegundaMano>
 * productosOfertados, List<ProductoSegundaMano> productosSolicitados ) {      
 *   this.id = "OFER-" + java.util.UUID.randomUUID().toString().substring(0,8);
 *         this.fechaOferta = LocalDateTime.now();         this.estado =
 * EstadoOferta.PENDIENTE;         this.origen = origen;         this.destino =
 * destino;         this.productosOfertados = productosOfertados;        
 * this.productosSolicitados = productosSolicitados;     }
 * 
 *     public void rechazar() {         this.estado = EstadoOferta.RECHAZADA;  
 *       // Importante: desbloqueamos los productos para que vuelvan a estar
 * disponibles         for (ProductoSegundaMano p : productosOfertados)
 * p.setBloqueado(false);        
 * this.origen.getOfertasPendientes().remove(this);        
 * this.destino.getOfertasPendientes().remove(this);        
 * this.origen.recibirNotificacion("Tu oferta con ID " + this.getId() +
 * " ha sido RECHAZADA.");        
 * //Tienda.getInstancia().finalizarIntercambio(this);     }                    
 *   public void aceptarYEjecutar() {         this.estado =
 * EstadoOferta.ACEPTADA;                 
 * origen.getHistorialIntercambios().add(this);        
 * destino.getHistorialIntercambios().add(this);                         
 * origen.getOfertasPendientes().remove(this);        
 * destino.getOfertasPendientes().remove(this);         
 * 
 *         for (Producto2Mano p : this.productosOfertados) {        
 * origen.getCarteraIntercambio().remove(p);         p.setBloqueado(false);    
 *     //AHORA SE ENVIARIAN         }         for (Producto2Mano p :
 * productosSolicitados) {         destino.getCarteraIntercambio().remove(p);  
 *       //AHORA SE ENVIARIAN         }        
 * Tienda.getInstancia().registrarIntercambioFinalizado(this);        
 * this.origen.recibirNotificacion("¡Intercambio ID " + this.id +
 * " aceptado! Preparando envío.");         this.destino.
 * recibirNotificacion("Has aceptado el intercambio. Los productos han salido de tu inventario."
 * );     }          // Getters y Setters     public String getId() { return id;
 * }     public LocalDateTime getFechaOferta() { return fechaOferta; }    
 * public EstadoOferta getEstado() { return estado; }     public void
 * setEstado(EstadoOferta estado) { this.estado = estado; }          public
 * List<ProductoSegundaMano> getProductosOfertados() { return
 * productosOfertados; }     public List<ProductoSegundaMano>
 * getProductosSolicitados() { return productosSolicitados; }
 * 
 * public Cliente getOrigen() { // TODO Auto-generated method stub return
 * this.origen; }
 * 
 * public Object getDestino() { // TODO Auto-generated method stub return
 * this.destino; } }
 * 
 * 
 */