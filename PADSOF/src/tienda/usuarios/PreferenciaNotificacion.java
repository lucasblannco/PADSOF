package usuarios;

import java.util.List;

import productos.Categoria;

public class PreferenciaNotificacion {
	private boolean Descuentos;
	private boolean Pago;
	private boolean PedidosCaducados;
	private boolean OfertasRecibidas;
	private boolean OfertasContestadas;
	private List<Categoria>  categoriasInteres;
	
	
	public PreferenciaNotificacion() {
		this.Descuentos=true;
		this.PedidosCaducados=true;
		this.Pago=true;
		this.OfertasContestadas=true;
		this.OfertasRecibidas=true;
		this.categoriasInteres=null; // Inicializamos las categorias de interes a nulll. El cliente las podra meter
	}


	public boolean isDescuentos_activado() {
		return Descuentos;
	}


	public void setDescuentos(boolean flag) {
		Descuentos = flag;
	}


	public boolean isPedidosPagados_activado() {
		return Pago;
	}


	public void setPedidosPagados(boolean flag) {
		Pago = flag;
	}


	public boolean isPedidosCaducados_activado() {
		return PedidosCaducados;
	}


	public void setPedidosCaducados(boolean flag) {
		PedidosCaducados = flag;
	}


	public boolean isOfertasRecibidas_activado() {
		return OfertasRecibidas;
	}


	public void setOfertasRecibidas(boolean ofertasRecibidas) {
		OfertasRecibidas = ofertasRecibidas;
	}


	public boolean isOfertasContestadas_activado() {
		return OfertasContestadas;
	}


	public void setOfertasContestadas(boolean ofertasContestadas) {
		OfertasContestadas = ofertasContestadas;
	}


	public List<Categoria> getCategoriasInteres() {
		return categoriasInteres;
	}

	public void añadirCategoriasInteres(List<Categoria> categoriasInteres) {
		this.categoriasInteres = categoriasInteres;
	}

}
