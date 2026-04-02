package Excepcion;


public class OfertaNoDisponibleException extends CheckPointException {

    private static final long serialVersionUID = 1L;
	private final String idOferta;

    public OfertaNoDisponibleException(String idOferta) {
        super("La oferta " + idOferta + " no esta disponible: ya ha sido resuelta, caducada o no pertenece a este usuario.");
        this.idOferta = idOferta;
    }

    public String getIdOferta() { return idOferta; }
}