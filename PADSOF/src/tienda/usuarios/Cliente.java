package usuarios;

import java.util.List;
import java.util.Map;

import tienda.*;
import tienda.Intercambios.Oferta;
import tienda.Productos.*;
import tienda.Ventas.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Cliente extends UsuarioRegistrado {
    //private double saldoPuntos;

    private List<Pedido> historialPedidos;
    private Carrito carritoActual;
    private List<Producto2Mano> carteraIntercambio;
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
    
    public boolean subirProducto() {
    	
    }
    
    public void solicitarTasacion(Producto2Mano p, String tarjeta, int CVV, Date caducidad) {
    	
    	p.getValoracion().setEstadoValoracion(EstadoValoracion.PENDIENTE_DE_PAGO);
    	if(p.getValoracion().pagar(tarjeta, CVV, caducidad)==false) {
    		 this.recibirNotificacion("Pago no aceptado");
    		 return;
    		
    	 }
    	
    	//this.carteraIntercambio.add(p); ya estaba añadido
        p.setBloqueado(false);
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
    public void proponerOferta(Cliente destinatario, List<Producto2Mano> misProductos, List<Producto2Mano> susProductos) {
       
        Oferta nuevaOferta = new Oferta(this, destinatario, misProductos, susProductos);      
     
        this.ofertasPendientes.add(nuevaOferta);
        
        destinatario.getOfertasPendientes().add(nuevaOferta);       
        destinatario.recibirNotificacion("Has recibido una propuesta de intercambio de " + this.nickname);
           
        for (Producto2Mano p : misProductos) p.setBloqueado(true);
    }
    
    
    
    public void confirmarIntercambio(Oferta oferta) {
        
        oferta.aceptarYEjecutar(); 
        

        
    }
    
    public void procesarRechazo(Oferta oferta) {
        // 1. Liberar productos        
    	oferta.rechazar();
    }
    
    public boolean productoHasidoPedidoYentregado(ProductoVenta p) {
    	for(Pedido ped: historialPedidos) {
    		if(ped.productoPertenece(p)==true && ped.getEstado()== EstadoPedido.ENTREGADO) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public void escribirReseña(ProductoVenta p, int pts, String texto) {
    	if((this.productoHasidoPedidoYentregado(p)==true){
    		Reseña nueva = new Reseña(this, p, pts, texto);
            this.reseñas.add(nueva); // El cliente la guarda
            p.getReseñas().add(nueva);  // El producto también la recibe
    	}
    			
    		
    	}
        
    

    
    
   
    	public void addProducto2Mano (ProductoSegundaMano p) {
    		this.carteraIntercambio.add(p);
    	}
    	
    	public Categoria determinarCategoriaFavorita() {
    		int maxApariciones =0;
    		Categoria favorita = null;
    		
    		Map<Categoria, Integer> contador = new HashMap<>();

            for (Pedido p : this.getHistorialPedidos()) {
                for (LineaPedido linea : p.getLineas()) {
                   
                    for (Categoria cat : linea.getProducto().getCategorias()) {
                        int n = contador.getOrDefault(cat, 0) + 1; // getOrdefault, devuelve el numero de la categoria cat si existe, y cero sino
                        contador.put(cat, n); //a la clave cat le metemos el nuevo numero de apariciones
                        
                        if (n > maxApariciones) {
                            maxApariciones = n;
                            favorita = cat; //guardamos la fav
                        }
                    }
                }
            }
            return favorita;
    	}
        
     // --- GETTERS ---
       

        public List<Pedido> getHistorialPedidos() {
            return historialPedidos
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