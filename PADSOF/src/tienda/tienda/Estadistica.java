package tienda;

import java.util.List;

import usuarios.Cliente;

public class Estadistica {
	
	private int nProductosVentas =1;
	private int nUsuarioRegistrado =1;
	private int nProducto2Mano = 1;
	private int nVentas =1;
	private int nDescuentos =1;
	private int nIntercambiosFinalizados =1;
	private int nCategorias =1;
	
	public int getNumPV() {
		return this.nProductosVentas;
	}
	
	public int getNumUR() {
		return this.nUsuarioRegistrado;
	}
	
	private int getNumP2Mano() {
		return this.nProducto2Mano;
	}
	
	private int getNumVentas() {
		return this.nVentas;
	}
	
	private int getNumDescuentos() {
		return this.nDescuentos;
	}
	
	private int getNumIF() {
		return this.nIntercambiosFinalizados;
	}
	
	private int getNumCategorias() {
		return this.nCategorias;
	}

	 public List<Cliente> obtenerClientesConMasCompras(){
		 
	 }
	 
	 public List<Cliente> obtenerClientesConMasIntercambios(){
		 
	 }
	 public double calcularIngresosRango(LocalDate inicio,  LocalDate fin) {
		 
	 }
	 public List<double> calcularIngresosMeses(){
		 
	 }
	 public doublecalcularIngresosVenta() {
		 
	 }
	 public double calcularIngresosTasacion() {
		 
	 }
}
