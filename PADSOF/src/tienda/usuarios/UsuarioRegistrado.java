package usuarios;

import java.util.List;
import java.util.ArrayList;

import tienda.Tienda;
import tienda.Productos.*;
public abstract class UsuarioRegistrado {
	protected String name;
    protected String id;
    protected String nickname;
    protected String password;
    protected String email;
    protected boolean sesionIniciada;
    //protected List<String> notificaciones; el gestor no tiene notifiaciones

    public UsuarioRegistrado() {
        //this.notificaciones = new ArrayList<>();
    }

    public  UsuarioRegistrado(String nickname, String password,String email) {
    	//HAY QUE COMPROBAR QUE EL USUARIO EXISTE
    	this.id = "USERREG-" + java.util.UUID.randomUUID().toString().substring(0,8);
    	this.nickname = nickname;
    	this.password = password;
    	this.email = email;
    	
    }
    
    public List<ProductoVenta> navegarCatalogoNuevos() {
        System.out.println("Visitante " + sessionId + " consultando catálogo de productos nuevos.");
        return Tienda.getInstancia().getStockNuevos();
    }
    
    public void logout() {
    }
    
    public List<Producto> buscarProducto (String nombre){
    	
    }
    
   public List<Producto> buscarProducto (String id){
    	
    }
   
   
    
    public String getNickname() {
    	return this.nickname;
    }
    
    public abstract void mostrarPanelPrincipal();
}
