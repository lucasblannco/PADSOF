package ventas;

import java.time.LocalDateTime;
import java.util.*;
import es.uam.eps.padsof.telecard.*;

public class Pago {
	private String numeroTarjeta;
	private LocalDateTime fechaTransaccion;
	private Date fechaTarjeta;
	private int CVV;
	private double importe;
	private boolean exito;

	public Pago(String numeroTarjeta, double importe, Date fechaTarjeta, int CVV) {
		this.fechaTransaccion = LocalDateTime.now();
		this.fechaTarjeta = fechaTarjeta;
		this.CVV = CVV;
		this.importe = importe;
		this.numeroTarjeta = numeroTarjeta;
		this.exito = procesarConBanco();
	}

	private boolean procesarConBanco() {
		String cvvString = String.valueOf(this.CVV);// convertimos a string
		Date hoy = new Date();

		if (this.numeroTarjeta.length() != 16) {
			return false;
		}

		if (cvvString.length() != 3) {
			System.out.println("El CVV debe tener 3 dígitos");
			return false;
		}

		if (this.fechaTarjeta.before(hoy)) {
			System.out.println("La tarjeta está caducada");
			return false;
		}
		try {
			TeleChargeAndPaySystem.charge(this.numeroTarjeta, "Pago CheckPoint", this.importe, true);
			return true;

		} catch (InvalidCardNumberException e) {
			System.out.println("Tarjeta inválida");
		} catch (FailedInternetConnectionException e) {
			System.out.println("Error de conexión al procesar el pago");
		} catch (OrderRejectedException e) {
			System.out.println("Pago rechazado por el banco");
		}
		return false;

	}

	public boolean getExito() {
		return this.exito;
	}
}
