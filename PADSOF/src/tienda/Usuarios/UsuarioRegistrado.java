package tienda.Usuarios;

import java.util.List;
import java.util.ArrayList;
import tienda.Productos.*;
public abstract class UsuarioRegistrado {
    protected String id;
    protected String nickname;
    protected String password;
    protected String email;
    protected boolean sesionIniciada;
    //protected List<String> notificaciones; el gestor no tiene notifiaciones

    public UsuarioRegistrado() {
        //this.notificaciones = new ArrayList<>();
    }

    public void login(String nickname, String password) {
    }

    public void logout() {
    }
    
    public abstract void mostrarPanelPrincipal();
}
