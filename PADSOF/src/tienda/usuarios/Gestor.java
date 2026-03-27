	package usuarios;
	
	import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
	import tienda.*;
	import tienda.Ventas.Descuento;
	
	public class Gestor extends UsuarioRegistrado {
	    private static final String NICKNAME_INICIAL = "admin_Gestor";
	    private static final String PASSWORD_INICIAL = "Admin@1234";

	    private Estadistica motorEstadistico;

	    // Lo instancia Tienda al arrancar, nadie más
	    public Gestor() {
	        super(NICKNAME_INICIAL, PASSWORD_INICIAL);
	        this.motorEstadistico = Estadistica.getInstancia();
	    }
	
	    @Override
	    public void mostrarPanelPrincipal() {
	        System.out.println("--- PANEL DE CONTROL DEL GESTOR ---");
	        System.out.println("1. Gestionar Empleados y Permisos");
	        System.out.println("2. Configurar Parámetros del Sistema");
	        System.out.println("3. Gestión de Descuentos y Precios");
	        System.out.println("4. Ver Estadísticas de Rendimiento");
	    }
	
	    // --- MÉTODOS DE ACCIÓN / GESTIÓN ---
	
	   
	
	    public boolean darDeAltaEmpleados(String nickname, String password) {
	     if (nickname==null||password==null) {
			System.out.println("Error en la creacion del empleado. Los parametros son null.");
			return false;
		}
	   Empleado nuevoEmpleado=new Empleado(nickname, password);
	    	
	    	
	    	
	    	
	    	
	    }
	
	    public void configurarParametrosGlobales() {
	        // Implementación según requisitos
	    }
	
	    public void crearDescuento(String idProducto, double porcentaje, LocalDate inicio, LocalDate fin) {
	        if (porcentaje <= 0 || porcentaje > 100) {
	            System.out.println("Porcentaje no válido");
	            return;
	        }
	        Descuento nuevoDesc = new Descuento(idProducto, porcentaje / 100, inicio, fin);
	        Tienda.getInstancia().agregarDescuento(nuevoDesc);
	    }
	
	    public void modificarPrecio(String idProd, double nuevoPrecio) {
	        // Lógica para buscar producto en Tienda y cambiar precioOficial
	    }
	
	    public void configurarComposicionPack(String idPack, List<String> productos) {
	        // Lógica para definir qué productos forman un pack
	    }
	
	    public void crearCategoria(String nombre) {
	        // Lógica para añadir categorías al sistema
	    }
	
	    public void añadirProductoaCategoria(String idProd, String nombreCat) {
	        // Lógica de organización del catálogo
	    }
	
	    public boolean setTiemposSistema(double tInter, double tCar, double tPago) {
	        // Configura tiempos de expiración de ofertas, carritos, etc.
	        return true;
	    }
	
	    public void modificarPerfil(String nuevoNick, String nuevaPass) {
	        this.nickname = nuevoNick;
	        this.password = nuevaPass;
	        System.out.println("Perfil de administrador actualizado.");
	    }
	
	    // --- MÉTODOS DE CONSULTA (Delegación en Estadistica) ---
	
	    public List<Cliente> verClientesTopCompras() {
	        return motor.obtenerClientesConMasCompras();
	    }
	
	    public List<Cliente> verClientesTopIntercambios() {
	        return motor.obtenerClientesConMasIntercambios();
	    }
	
	    public double consultarIngresos(LocalDate inicio, LocalDate fin) {
	        return motor.calcularIngresosRango(inicio, fin);
	    }
	
	    public double consultarExitoIntercambios() {
	        return motor.calcularTasaExitoIntercambios();
	    }
	    
	    public void configurarSistemaRecomendacion(int nuevoLimite, boolean encender) {
	        // Accede al recomendador a través de la tienda y lo configura
	        Tienda.getInstancia().getRecomendador().setConfiguracion(nuevoLimite, encender);
	        System.out.println("Sistema de recomendaciones actualizado por el Gestor.");
	    }
	}
