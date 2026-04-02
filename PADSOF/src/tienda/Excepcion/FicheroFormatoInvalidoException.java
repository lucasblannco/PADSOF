package Excepcion;

public class FicheroFormatoInvalidoException extends Exception { 
    private final int numeroLinea;
    private final String linea;

    public FicheroFormatoInvalidoException(int numeroLinea, String linea, String mensaje) {
        super(mensaje);
        this.numeroLinea = numeroLinea;
        this.linea = linea;
    }

    @Override
    public String getMessage() {
        return "Error en línea " + numeroLinea + ": " + super.getMessage() + " -> " + linea;
    }
}