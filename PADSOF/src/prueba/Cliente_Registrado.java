package prueba;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Cliente_Registrado extends Usuario_Registrado{

private String DNI;
private Carrito carrito;
private Cartera cartera;   // productos que posee
private List <Pedido> pedidos=new ArrayList<>();

// nOTIFICACIONES

public Cliente_Registrado(String DNI,String nickname, String password, String mail) {
	super(nickname, password, mail);
	this.DNI=DNI;

  
}


@Override
public void iniciarSesion(String password) {
    
}
@Override
public void cerrarSesion() {
    // Código para cerrar sesión
}
@Override
public void verNotificaciones() { 
}
@Override
public void verRecomendaciones() {   
}
public void aÑadirProductoAlCarrito(Producto p) {
}
public void eliminarProducto(Producto p) {
}
public void pagarCarrito() {}
;
public void subirProductoCartera() {}
public void SolicitarRevision() {};
public void verCartera() {}
	
}
}