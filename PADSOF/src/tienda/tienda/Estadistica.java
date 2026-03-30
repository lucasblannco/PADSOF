package tienda;

import java.time.LocalDate;
import java.util.List;
import usuarios.Cliente;

public class Estadistica {

	// ── Singleton ──────────────────────────────────────
	private static Estadistica instancia;

	private Estadistica() {
	} // constructor privado, nadie puede hacer new Estadistica()

	public static Estadistica getInstancia() {
		if (instancia == null)
			instancia = new Estadistica();
		return instancia;
	}
	// ───────────────────────────────────────────────────

	private int nProductosVentas = 1;
	private int nUsuarioRegistrado = 1;
	private int nUsuarioNoRegistrado = 1;
	private int nProducto2Mano = 1;
	private int nVentas = 1;
	private int nDescuentos = 1;
	private int nIntercambiosFinalizados = 1;
	private int nCategorias = 1;
	private int nCarritos = 1;
	private int nReseñas = 1;
	private int nNotificaciones = 1;

	public int getnProductosVentas() {
		return nProductosVentas;
	}

	public void setnProductosVentas(int n) {
		this.nProductosVentas = n;
	}

	public int getnNotificaciones() {
		return nNotificaciones;
	}

	public void setnNotificaciones(int n) {
		this.nNotificaciones = n;
	}

	public int getnUsuarioRegistrado() {
		return nUsuarioRegistrado;
	}

	public void setnUsuarioRegistrado(int n) {
		this.nUsuarioRegistrado = n;
	}

	public int getnUsuarioNoRegistrado() {
		return nUsuarioNoRegistrado;
	}

	public void setnUsuarioNoRegistrado(int n) {
		this.nUsuarioNoRegistrado = n;
	}

	public int getnProducto2Mano() {
		return nProducto2Mano;
	}

	public void setnProducto2Mano(int n) {
		this.nProducto2Mano = n;
	}

	public int getnVentas() {
		return nVentas;
	}

	public void setnVentas(int n) {
		this.nVentas = n;
	}

	public int getnDescuentos() {
		return nDescuentos;
	}

	public void setnDescuentos(int n) {
		this.nDescuentos = n;
	}

	public int getnIntercambiosFinalizados() {
		return nIntercambiosFinalizados;
	}

	public void setnIntercambiosFinalizados(int n) {
		this.nIntercambiosFinalizados = n;
	}

	public int getnCategorias() {
		return nCategorias;
	}

	public void setnCategorias(int n) {
		this.nCategorias = n;
	}

	public int getnCarritos() {
		return nCarritos;
	}

	public void setnCarritos(int n) {
		this.nCarritos = n;
	}

	public int getnReseñas() {
		return this.nReseñas;
	}

	public void setnReseñas(int n) {
		this.nReseñas = n;
	}

	// ── Métodos a implementar ──────────────────────────
	public List<Cliente> obtenerClientesConMasCompras() {
		return null;
	}

	public List<Cliente> obtenerClientesConMasIntercambios() {
		return null;
	}

	public double calcularIngresosRango(LocalDate inicio, LocalDate fin) {

	}

	public List<double> calcularIngresosMeses(){
		
	}

	public double calcularIngresosVenta() {

	}

	public double calcularIngresosTasacion() {

	}

}