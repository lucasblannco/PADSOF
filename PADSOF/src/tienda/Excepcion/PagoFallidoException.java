package Excepcion;

import java.lang.invoke.StringConcatFactory;

public abstract class PagoFallidoException {

	@Override
	public String toString() {
		return "El pago ha fallado.";
	}
}
