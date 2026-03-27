package usuarios;

import java.util.List;
import java.util.Scanner;

import tienda.Tienda;
import tienda.Estadistica;
import tienda.Productos.ProductoVenta;

public class UsuarioNoRegistrado {
	protected String sessionId;

	public UsuarioNoRegistrado() {
		Estadistica est = Estadistica.getInstancia();
		this.sessionId = "INVITADO-" + String.valueOf(est.getnUsuarioNoRegistrado());
		est.setnUsuarioNoRegistrado(est.getnUsuarioNoRegistrado() + 1);
	}

	public List<ProductoVenta> navegarCatalogoNuevos() {
		System.out.println("Visitante " + sessionId + " consultando catálogo de productos nuevos.");
		return Tienda.getInstancia().getStockNuevos();
	}

	public void consultarSegundaMano() {
		System.out
				.println("Acceso denegado: Debes estar registrado para ver e intercambiar productos de segunda mano.");
	}

	// Un método que represente la intención de registrarse
	public void solicitarRegistro() {
		Scanner sc = new Scanner(System.in);
		System.out.println("--- FORMULARIO DE REGISTRO ---");

		System.out.print("Introduce tu Nickname: ");
		String nick = sc.nextLine();

		System.out.print("Introduce tu Email: ");
		String email = sc.nextLine();

		System.out.print("Introduce tu Password: ");
		String pass = sc.nextLine();

		Tienda.getInstancia().registrarNuevoCliente(nick, email, pass);

		System.out.println("Registro completado con éxito. Ahora puedes iniciar sesión como Cliente.");

	}

	public String getSessionId() {
		return sessionId;
	}
}