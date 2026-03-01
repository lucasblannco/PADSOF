package tienda.Usuarios;

import java.util.List;
import java.util.Scanner;

import tienda.Tienda;
import tienda.Productos.ProductoVenta;

public class UsuarioNoRegistrado {
    protected String sessionId;

    public UsuarioNoRegistrado() {
       
        this.sessionId = "GUEST-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    
    public List<ProductoVenta> navegarCatalogoNuevos() {
        System.out.println("Visitante " + sessionId + " consultando catálogo de productos nuevos.");
        return Tienda.getInstancia().getStockNuevos();
    }

    public void consultarSegundaMano() {
        System.out.println("Acceso denegado: Debes estar registrado para ver e intercambiar productos de segunda mano.");
    }
    
    // Un método que represente la intención de registrarse
    public void solicitarRegistro() {
        Scanner sc = new Scanner(System.in);
        System.out.println("--- FORMULARIO DE REGISTRO ---");
        
        System.out.print("Introduce tu Nickname: ");
        String nick = sc.nextLine();
        
        System.out.print("Introduce tu Email: ");
        String email = sc.nextLine();
        
        System.out.print("Introduce tu Password: ");
        String pass = sc.nextLine();

       
        Tienda.getInstancia().registrarNuevoCliente(nick, email, pass);
        
        System.out.println("Registro completado con éxito. Ahora puedes iniciar sesión como Cliente.");
        
    }

    public String getSessionId() {
        return sessionId;
    }
}