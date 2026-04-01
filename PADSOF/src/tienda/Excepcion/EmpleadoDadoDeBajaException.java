package Excepcion;

public class EmpleadoDadoDeBajaException {
	private String nickname;

	public EmpleadoDadoDeBajaException(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return "El empleado " + nickname + " está dado de baja y no puede iniciar sesion en la aplicacion.";
	}

}
