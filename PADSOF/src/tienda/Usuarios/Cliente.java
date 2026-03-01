package tienda.Usuarios;

import java.util.List;

import tienda.*;
import tienda.Intercambios.Oferta;
import tienda.Productos.*;
import tienda.Ventas.*;
import java.util.ArrayList;
import java.util.Date;

public class Cliente extends UsuarioRegistrado {
    //private double saldoPuntos;
    private List<Pedido> historialPedidos;
    private Carrito carritoActual;
    private List<ProductoSegundaMano> carteraIntercambio;
    private List<Oferta> ofertasPendientes; 
    private List<Oferta> historialIntercambios;
    private List<Reseña> reseñas;
    protected List<Notificacion> notificaciones;

    public Cliente() {
        super();
        this.historialPedidos = new ArrayList<>();
        this.carteraIntercambio = new ArrayList<>();
        this.ofertasPendientes = new ArrayList<>();
        this.reseñas = new ArrayList<>();
    }

    @Override
    public void mostrarPanelPrincipal() {
    }
    

    public void subirProductoParaIntercambio(ProductoSegundaMano p, String tarjeta, int CVV, Date caducidad) {
    	p.getValoracion().setEstadoValoracion(EstadoValoracion.PENDIENTE_DE_PAGO);
    	if(p.getValoracion().pagar(tarjeta, CVV, caducidad)==false) {
    		 this.recibirNotificacion("Pago no aceptado");
    		 return;
    		
    	 }
    	
    	this.carteraIntercambio.add(p);
        p.setBloqueado(true);
        p.setVisible(false);
       
      
        
        // Notificamos a la tienda que hay un nuevo producto pendiente de tasar
        Tienda.getInstancia().solicitarTasacion(p); 
        System.out.println("Producto subido. Esperando a que un empleado lo tase.");
    }
    
    
    
//OFERTAS
    //quiero poder disntiguir entre lsa ofertas que tengo que responder y las que me tienen que responder.
    //para no hacer 2 arrays, hago 2 metodos y ya
    public List<Oferta> getOfertasParaDecidir() {
        List<Oferta> paraDecidir = new ArrayList<>();
        for (Oferta o : ofertasPendientes) {
            // Si el destino soy yo, es que tengo que contestar
            if (o.getDestino().equals(this)) {
                paraDecidir.add(o);
            }
        }
        return paraDecidir;
    }

    public List<Oferta> getOfertasEnEspera() {
        List<Oferta> enEspera = new ArrayList<>();
        for (Oferta o : ofertasPendientes) {
           
            if (o.getOrigen().equals(this)) {
                enEspera.add(o);
            }
        }
        return enEspera;
    }
    public void proponerOferta(Cliente destinatario, List<ProductoSegundaMano> misProductos, List<ProductoSegundaMano> susProductos) {
       
        Oferta nuevaOferta = new Oferta(this, destinatario, misProductos, susProductos);      
     
        this.ofertasPendientes.add(nuevaOferta);
        
        destinatario.getOfertasPendientes().add(nuevaOferta);       
        destinatario.recibirNotificacion("Has recibido una propuesta de intercambio de " + this.nickname);
           
        for (ProductoSegundaMano p : misProductos) p.setBloqueado(true);
    }
    
    public void confirmarIntercambio(Oferta oferta) {
        
        oferta.aceptarYEjecutar(); 
        

        
    }
    
    public void procesarRechazo(Oferta oferta) {
        // 1. Liberar productos        
    	oferta.rechazar();
    }
    
    public void escribirReseña(ProductoVenta p, int pts, String texto) {
        Reseña nueva = new Reseña(this, p, pts, texto);
        this.reseñas.add(nueva); // El cliente la guarda
        p.getReseñas().add(nueva);  // El producto también la recibe
    }

    
    
   
    	public void addProducto2Mano (ProductoSegundaMano p) {
    		this.carteraIntercambio.add(p);
    	}
    	
    
        
     // --- GETTERS ---
       

        public List<Pedido> getHistorialPedidos() {
            return historialPedidos;0
        }

        public Carrito getCarritoActual() {
            return carritoActual;
        }

        public List<ProductoSegundaMano> getCarteraIntercambio() {
            return carteraIntercambio;
        }

        public List<Oferta> getOfertasPendientes() {
            return ofertasPendientes;
        }

        public List<Oferta> getHistorialIntercambios() {
            return historialIntercambios;
        }

        public List<Reseña> getReseñas() {
            return reseñas;
        }

        public List<Notificacion> getNotificaciones() {
            return notificaciones;
        }

        // --- SETTERS ---
        

        public void setCarritoActual(Carrito carritoActual) {
            this.carritoActual = carritoActual;
        }
        
        
     // 1. Creamos el nuevo objeto Cliente
        // --- MÉTODOS DE APOYO (Necesarios para que el código compile) ---

        //REVISAR
        public void recibirNotificacion(String mensaje) {
            if (this.notificaciones == null) {
                this.notificaciones = new ArrayList<>();
            }
            // Si aún no has creado la clase Notificacion, puedes pasarle un String 
            // o crear el objeto aquí mismo si ya la tienes.
            this.notificaciones.add(new Notificacion(mensaje));
            System.out.println("[Notificación Cliente]: " + mensaje);
        }
}