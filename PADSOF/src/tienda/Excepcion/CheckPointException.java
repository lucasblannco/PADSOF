package Excepcion;

/**
 * Excepcion base del sistema CheckPoint.
 * Todas las excepciones del proyecto extienden esta clase.
 * Es checked (extiende Exception) para que el compilador
 * obligue a capturarlas o declararlas con throws.
 */
public abstract class CheckPointException extends Exception {

    public CheckPointException(String mensaje) {
        super(mensaje);
    }

    public CheckPointException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
