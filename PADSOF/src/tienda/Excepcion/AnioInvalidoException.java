package Excepcion;

public class AnioInvalidoException extends CheckPointException {

    private static final long serialVersionUID = 1L;
	private final int anio;

    public AnioInvalidoException(int anio) {
        super("Anio invalido: " + anio + ". El anio debe ser mayor que 0.");
        this.anio = anio;
    }

    public int getAnio() { return anio; }
}