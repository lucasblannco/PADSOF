package tienda;

import java.security.PublicKey;
import java.util.*;

import intercambios.Oferta;
import productos.Producto2Mano;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.TipoPermisos;
import usuarios.UsuarioRegistrado;
import ventas.Carrito;
import ventas.Descuento;
import ventas.Pedido;
import productos.*;

public class Tienda {
    private String nombre;
    private List<UsuarioRegistrado> usuarios;
    private List<ProductoVenta> stockVentas;
    private List<Producto2Mano> catalogoIntercambio; 
    private List<Pedido> historialVentas;
    private List<Producto2Mano>  pendientes_Tasacion; //Productos que no han sido tasados
    private List<Descuento> descuentosActivos = new ArrayList<>();
    private List<Oferta> intercambiosFinalizados = new ArrayList<>();
    private List<Categoria> categorias = new ArrayList<>();
    private Recomendador recomendador;
    private int tiempoMaxCarrito;
    private int tiempoMaxOferta;
    private int tiempoMaxPago;
    
   
    //private List<Producto2Mano> pendientesTasacion = new ArrayList<>();
    //esta variable estatica, el constructor privado y el segundo metodo
    //sirven para asegurar la existencia de una tienda unica y comun.
    
    private static Tienda instancia;

    private Tienda() {
        this.nombre = "CheckPoint";
        this.usuarios = new ArrayList<>();
        this.stockVentas = new ArrayList<>();
        this.catalogoIntercambio = new ArrayList<>();
        this.historialVentas = new ArrayList<>();
        this.pendientes_Tasacion=new ArrayList<>();
    }

    public static Tienda getInstancia() {
        if (instancia == null) instancia = new Tienda();
        return instancia;
    }
    
    //--- añadir cosas
    
    public Cliente registrarNuevoCliente(String nickname, String email, String password) {
        
        Cliente nuevoCliente = new Cliente();
      
        //nuevoCliente.login(nickname, password, email);
        
   
        this.usuarios.add(nuevoCliente);
        nuevoCliente.recibirNotificacion("¡Bienvenido a CheckPoint, " + nickname + "!");
        return nuevoCliente;
    }
    
    public void registrarIntercambioFinalizado(Oferta oferta) {
        this.intercambiosFinalizados.add(oferta);
        this.catalogoIntercambio.removeAll(oferta.getProductosOfertados());
        this.catalogoIntercambio.removeAll(oferta.getProductosSolicitados());
    }
    
    public List<Empleado> obtenerEmpleadosTienda() {
        List<Empleado> listaEmpleados = new ArrayList<>();
        for (UsuarioRegistrado usuario : usuarios) {
            if (usuario instanceof Empleado) {
                listaEmpleados.add((Empleado) usuario);
            }
        }
        return listaEmpleados;
    }
    
    public void añadirProducto(ProductoVenta nuevo) {
        if (this.getStockVentas().contains(nuevo)) return;
        this.getStockVentas().add(nuevo);
    }
    public void solicitarTasacion(Producto2Mano p) {
        this.pendientes_Tasacion.add(p);
        
        List <Empleado> listaEmpleados=new ArrayList<>();
        listaEmpleados=this.obtenerEmpleadosTienda();
        for (Empleado empleado: listaEmpleados) {
        	if (empleado.tienePermiso(TipoPermisos.VALORACION_PRODUCTOS)) {
        		empleado.recibirNotificacion("Hay un nuevo producto para valorar");
			}
		}
    }
    // - DESCUENTOS
    
    public void agregarDescuento(Descuento d) {
        this.descuentosActivos.add(d);
    }

    /*public double buscarDescuentoParaProducto(String idProducto) {
        for (Descuento d : descuentosActivos) {
            if (d.getIdProducto().equals(idProducto) && d.estaActivo()) {
                return d.getPorcentaje();
            }
        }
        return 0.0; // Sin descuento
    }*/
    
    // --- LÓGICA DE INTERCAMBIO 

    //añadir producto de segunda mano a la red global
   
    
    
    
    //Añadimos un producto ya valorado al catalogo de productos de segunda mano.
    public void publicarParaIntercambio(Producto2Mano p) {
        if (p.getValoracion() != null && !this.getCatalogoIntercambio().contains(p)) {
            p.setBloqueado(false);
            this.catalogoIntercambio.add(p);//Lo añadimos al catalogo de productos de segunda mano
        }
    }

    
    
    
    
    //buscar productos de segunda mano, pero que no esten bloqueados
    /*public List<Producto2Mano> buscarSegundaMano(String query) {
        List<Producto2Mano> -ultados = new ArrayList<>();
        for (Producto2Mano p : catalogoIntercambio) {
            // AHORA FILTRAMOS TAMBIÉN POR VISIBLE
            if (p.isVisible() && !p.isBloqueado() && p.getNombre().toLowerCase().contains(query.toLowerCase())) {
                resultados.add(p);
            }
        }
        return resultados;
    }
    */
  
    //suponemos que el orden de prioridad es segun se meten a la array
    /*public void aplicarDescuentoPrioritario(Carrito carrito) {
        
        List<Descuento> listaD = Tienda.getInstancia().getDescuentos()
;        
        for (Descuento d : listaD) {
           
            if (d.estaActivo()) {
                
               
                double ahorro = d.calcularDescuento(carrito);
                
                if (ahorro > 0) {
                   
                    carrito.setDescuento(d); 
                    carrito.setTotal(ahorro);
                    
                    System.out.println("Aplicado descuento: " + d.getNombre());
                    return; // Este 'return' es el que cumple la regla de "No acumulable"
                }
            }
        }
    }*/
    // --- GESTIÓN DE VENTAS NUEVAS 
    
    private List<Descuento> getDescuentos() {
		// TODO Auto-generated method stub
		return this.descuentosActivos;
	}



    public void registrarVenta(Pedido pedido) {
        this.historialVentas.add(pedido);
    }

    // GETTERS
   
    public List<UsuarioRegistrado> getUsuarios() { return usuarios; }
    public List<Producto2Mano> getPendientesTasacion() {
        return pendientes_Tasacion;
    }
    public Recomendador getRecomendador() {
    	return this.recomendador;
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Producto2Mano> getCatalogoIntercambio() {
		return catalogoIntercambio;
	}

	public void setCatalogoIntercambio(List<Producto2Mano> catalogoIntercambio) {
		this.catalogoIntercambio = catalogoIntercambio;
	}

	public List<Pedido> getHistorialVentas() {
		return historialVentas;
	}

	public void setHistorialVentas(List<Pedido> historialVentas) {
		this.historialVentas = historialVentas;
	}

	public List<Producto2Mano> getPendientes_Tasacion() {
		return pendientes_Tasacion;
	}

	public void setPendientes_Tasacion(List<Producto2Mano> pendientes_Tasacion) {
		this.pendientes_Tasacion = pendientes_Tasacion;
	}

	public List<Descuento> getDescuentosActivos() {
		return descuentosActivos;
	}

	public void setDescuentosActivos(List<Descuento> descuentosActivos) {
		this.descuentosActivos = descuentosActivos;
	}

	public List<Oferta> getIntercambiosFinalizados() {
		return intercambiosFinalizados;
	}

	public void setIntercambiosFinalizados(List<Oferta> intercambiosFinalizados) {
		this.intercambiosFinalizados = intercambiosFinalizados;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}


	public void setUsuarios(List<UsuarioRegistrado> usuarios) {
		this.usuarios = usuarios;
	}

	

	public void setRecomendador(Recomendador recomendador) {
		this.recomendador = recomendador;
	}

	public static void setInstancia(Tienda instancia) {
		Tienda.instancia = instancia;
	}

	public List<ProductoVenta> getStockVentas() {
		return stockVentas;
	}

	public void setStockVentas(List<ProductoVenta> stockVentas) {
		this.stockVentas = stockVentas;
	}

	public int getTiempoMaxCarrito() {
		return tiempoMaxCarrito;
	}

	public void setTiempoMaxCarrito(int tiempoMaxCarrito) {
		this.tiempoMaxCarrito = tiempoMaxCarrito;
	}

	public int getTiempoMaxOferta() {
		return tiempoMaxOferta;
	}

	public void setTiempoMaxOferta(int tiempoMaxOferta) {
		this.tiempoMaxOferta = tiempoMaxOferta;
	}

	public int getTiempoMaxPago() {
		return tiempoMaxPago;
	}

	public void setTiempoMaxPago(int tiempoMaxPago) {
		this.tiempoMaxPago = tiempoMaxPago;
	}
}
