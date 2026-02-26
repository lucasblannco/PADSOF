package tienda.Ventas;

import java.time.LocalDateTime;
import java.util.*;

public class Pago {
    private String numeroTarjeta;
    private LocalDateTime fechaTransaccion;
    private Date fechaTarjeta;
    private int CVV;
    private double importe;
    private boolean exito;
    private String transaccionId;

    public Pago(String numeroTarjeta, double importe, Date fechaTarjeta,int CVV) {
        this.fechaTransaccion = LocalDateTime.now();
        this.fechaTarjeta = fechaTarjeta;
        this.CVV = CVV;
        this.importe = importe;
        this.transaccionId = "TXN-" + System.currentTimeMillis();
        this.numeroTarjeta = numeroTarjeta;
        this.exito = procesarConBanco();
    }

    private boolean procesarConBanco() {
        // Simulación de pasarela de pago
        return true; 
    }

    public boolean isExito() { return exito; }
}
