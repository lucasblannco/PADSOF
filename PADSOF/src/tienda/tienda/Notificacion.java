package tienda;

import java.time.LocalDateTime;

public class Notificacion {
    private String id;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private boolean leida;
    private TipoNotificacion tipo;

    public Notificacion(String mensaje,TipoNotificacion tipo) {
    	Estadistica
    }
    
    
    
    
    
    public Notificacion(String mensaje) {
        this.id = "NOT-" + java.util.UUID.randomUUID().toString().substring(0, 5);
        this.mensaje = mensaje;
        this.fechaEnvio = LocalDateTime.now();
        this.leida = false;
    }

    public void marcarComoLeida() {
        this.leida = true;
    }

    // --- GETTERS Y SETTERS ---
    public String getId() {
        return id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }
}
