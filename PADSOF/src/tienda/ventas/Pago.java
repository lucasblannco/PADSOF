package ventas;

import java.time.LocalDateTime;
import java.util.*;

public class Pago {
    private String numeroTarjeta;
    private LocalDateTime fechaTransaccion;
    private Date fechaTarjeta;
    private int CVV;
    private double importe;
    private boolean exito;

    public Pago(String numeroTarjeta, double importe, Date fechaTarjeta,int CVV) {
        this.fechaTransaccion = LocalDateTime.now();
        this.fechaTarjeta = fechaTarjeta;
        this.CVV = CVV;
        this.importe = importe;
        this.numeroTarjeta = numeroTarjeta;
        this.exito = procesarConBanco();
    }

    private boolean procesarConBanco() {
    	String cvvString;
		Date hoy = new Date();

		if (this.numeroTarjeta.length() != 16) {
			return false;
		}

		cvvString = String.valueOf(this.CVV);
		if (cvvString.length() != 3) {
			return false;
		}

		if (this.fechaTarjeta.before(hoy)) {
			return false;
		}
        return true; 
    }
    
    public boolean getExito() {
    	return this.exito;
    }
}
