package tienda;

import java.time.LocalDateTime;

public class Notificacion {

	private String id;
	private String mensaje;
	private LocalDateTime fechaEnvio;
	private boolean leida;
	private TipoNotificacion tipo;

	public Notificacion(String mensaje, TipoNotificacion tipo) {
		Estadistica est = Estadistica.getInstancia();
		this.id = "NOTIF" + est.getnNotificaciones();
		est.setnNotificaciones(est.getnNotificaciones() + 1);
		this.mensaje = mensaje;
		this.fechaEnvio = LocalDateTime.now();
		this.leida = false;
		this.tipo = tipo;
		Tienda.getInstancia().registrarNotificacion(this);
	}

	// Para los empleados que siempre reciben las notificaciones
	public Notificacion(String mensaje) {
		this(mensaje, TipoNotificacion.EMPLEADOS);
	}

	public void marcarComoLeida() {
		this.leida = true;
	}

	// --- GETTERS Y SETTERS ---

	public String getId() { return id; }

	public String getMensaje() { return mensaje; }
	public void   setMensaje(String mensaje) { this.mensaje = mensaje; }

	public LocalDateTime getFechaEnvio() { return fechaEnvio; }

	public boolean isLeida() { return leida; }
	public void    setLeida(boolean leida) { this.leida = leida; }

	public TipoNotificacion getTipo() { return tipo; }
	public void             setTipo(TipoNotificacion tipo) { this.tipo = tipo; }

	@Override
	public String toString() {
		return "[" + id + "] "
			+ "[" + tipo + "] "
			+ (leida ? "(leida)  " : "(no leida) ")
			+ mensaje
			+ "  [" + fechaEnvio.toLocalDate() + " " + fechaEnvio.toLocalTime().withNano(0) + "]";
	}
}