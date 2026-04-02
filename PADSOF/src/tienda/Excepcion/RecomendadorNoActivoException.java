package Excepcion;


public class RecomendadorNoActivoException extends CheckPointException {

    private static final long serialVersionUID = 1L;//version 1

	public RecomendadorNoActivoException() {
        super("El recomendador esta desactivado. Contacte con el gestor para activarlo.");
    }
}
