package usuarios;

import java.util.List;

import productos.ProductoVenta;

import java.util.ArrayList;

import tienda.Tienda;
import tienda.Estadistica;
import tienda.Productos.*;
public abstract class UsuarioRegistrado {

    protected String id;
    protected String nickname;
    protected String password;

    protected boolean sesionIniciada;
    //protected List<String> notificaciones; el gestor no tiene notifiaciones

    
/*
    public UsuarioRegistrado(String nickname, String password, String email) { 
        Estadistica est = Estadistica.getInstancia();
        this.id = "USERREG-" + String.valueOf(est.getnUsuarioRegistrado());
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        est.setnUsuarioRegistrado(est.getnUsuarioRegistrado() + 1);
    }
  */  
    
    public UsuarioRegistrado(String nickname,String password) {
    	Estadistica est=Estadistica.getInstancia();
    	this.id = "USERREG-" + String.valueOf(est.getnUsuarioRegistrado());
        this.nickname = nickname;
        this.password=password;
        est.setnUsuarioRegistrado(est.getnUsuarioRegistrado()+1);
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
    public String getId() {
    	return this.id;
    }
    
    public abstract void mostrarPanelPrincipal();
}
