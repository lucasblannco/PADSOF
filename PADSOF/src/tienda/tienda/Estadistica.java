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

	 public int getnProductosVentas() {
		return nProductosVentas;
	}

	public void setnProductosVentas(int nProductosVentas) {
		this.nProductosVentas = nProductosVentas;
	}

	public int getnUsuarioRegistrado() {
		return nUsuarioRegistrado;
	}

	public void setnUsuarioRegistrado(int nUsuarioRegistrado) {
		this.nUsuarioRegistrado = nUsuarioRegistrado;
	}

	public int getnProducto2Mano() {
		return nProducto2Mano;
	}

	public void setnProducto2Mano(int nProducto2Mano) {
		this.nProducto2Mano = nProducto2Mano;
	}

	public int getnVentas() {
		return nVentas;
	}

	public void setnVentas(int nVentas) {
		this.nVentas = nVentas;
	}

	public int getnDescuentos() {
		return nDescuentos;
	}

	public void setnDescuentos(int nDescuentos) {
		this.nDescuentos = nDescuentos;
	}

	public int getnIntercambiosFinalizados() {
		return nIntercambiosFinalizados;
	}

	public void setnIntercambiosFinalizados(int nIntercambiosFinalizados) {
		this.nIntercambiosFinalizados = nIntercambiosFinalizados;
	}

	public int getnCategorias() {
		return nCategorias;
	}

	public void setnCategorias(int nCategorias) {
		this.nCategorias = nCategorias;
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
