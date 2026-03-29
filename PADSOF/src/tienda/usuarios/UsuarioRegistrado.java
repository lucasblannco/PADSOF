package usuarios;

import java.util.List;

import productos.ProductoVenta;

import java.util.ArrayList;

import tienda.Tienda;
import tienda.Estadistica;
import tienda.Productos.*;

public abstract class UsuarioRegistrado {

	protected String id;
	protected String nickname;
	protected String password;
	protected boolean sesionIniciada;

	/*
	 * public UsuarioRegistrado(String nickname, String password, String email) {
	 * Estadistica est = Estadistica.getInstancia(); this.id = "USERREG-" +
	 * String.valueOf(est.getnUsuarioRegistrado()); this.nickname = nickname;
	 * this.password = password; this.email = email;
	 * est.setnUsuarioRegistrado(est.getnUsuarioRegistrado() + 1); }
	 */

	public UsuarioRegistrado(String nickname, String password) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "USERREG-" + String.valueOf(est.getnUsuarioRegistrado());
		this.nickname = nickname;
		this.password = password;
		est.setnUsuarioRegistrado(est.getnUsuarioRegistrado() + 1);
		this.sesionIniciada = false;
	}

	/*
	 * public List<ProductoVenta> navegarCatalogoNuevos() {
	 * System.out.println("Visitante " + sessionId +
	 * " consultando catálogo de productos nuevos."); return
	 * Tienda.getInstancia().getStockNuevos(); }
	 */
	public void logout() {
	this.sesionIniciada=false;
	Tienda.getInstancia()getUsuariosConSesionActiva().remove(this);
	System.out.println("El usuario con id: "+ id+" ha cerrado sesion correctamente.");
	}

	public static boolean validarPassword(String pass) {
		if (pass == null || pass.length() < 8// longitud minimo 8
				|| !pass.matches(".*[A-Z].*")// busca al menos una letra mayuscula
				|| !pass.matches(".*[a-z].*")// busca al menos una letra minuscula
				|| !pass.matches(".*\\d.*")// busca al menos un digito numerico
				|| !pass.matches(".*[^a-zA-Z0-9].*")) {// buscamos al menos unn caracter especial{
			System.out.println("La contraseña debe tener al menos 8 caracteres, "
					+ "una mayúscula, una minúscula, un número y un carácter especial.");
			return false;
		}
		return true;
	}

	public boolean comprobarCredenciales(String nickname, String password) {
		return this.nickname.equals(nickname) && this.password.equals(password);
	}

//Getters públicos: Todos necesitan saber quién es quién [cite: 325]
	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	protected void setNickname(String nickname) {
		this.nickname = nickname;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return this.id;
}

	public boolean isSesionIniciada() {
		return sesionIniciada;
	}

	public void setSesionIniciada(boolean sesionIniciada) {
		this.sesionIniciada = sesionIniciada;
	}
	
}