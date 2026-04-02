package Excepcion;

/**
 * Se lanza cuando se llama a generarSugerencias con el recomendador
 * desactivado. Permite al llamador distinguir "lista vacia porque no
 * hay candidatos" de "lista vacia porque el sistema esta apagado".
 */
public class RecomendadorNoActivoException extends CheckPointException {

    public RecomendadorNoActivoException() {
        super("El recomendador esta desactivado. Contacte con el gestor para activarlo.");
    }
}
