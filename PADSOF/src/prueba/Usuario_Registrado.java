package prueba;

public abstract class Usuario_Registrado extends Usuario {

	private String nickname;
	private String password;
	private String mail;
	
	
	public Usuario_Registrado (String nickname, String password, String mail) {
		this.nickname=nickname;
		this.mail=mail;
		this.password=password;
	}


	 public abstract void iniciarSesion(String password);
	    public abstract void cerrarSesion();	   
	    public abstract void verNotificaciones();
	    public abstract void verRecomendaciones();
	}


