package Excepcion;


public class CheckPointException extends Exception {
    private static final long serialVersionUID = 1L;
    public CheckPointException(String mensaje) {
        super(mensaje);
    }
}