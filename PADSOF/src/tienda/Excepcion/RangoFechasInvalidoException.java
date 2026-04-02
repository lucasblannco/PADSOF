package Excepcion;

import java.time.LocalDate;


public class RangoFechasInvalidoException extends CheckPointException {

    private static final long serialVersionUID = 1L;
	private final LocalDate inicio;
    private final LocalDate fin;

    public RangoFechasInvalidoException(LocalDate inicio, LocalDate fin) {
        super("Rango de fechas invalido: inicio=" + inicio + ", fin=" + fin
                + ". La fecha de fin debe ser igual o posterior a la de inicio, y ninguna puede ser null.");
        this.inicio = inicio;
        this.fin    = fin;
    }

    public LocalDate getInicio() { return inicio; }
    public LocalDate getFin()    { return fin; }
}