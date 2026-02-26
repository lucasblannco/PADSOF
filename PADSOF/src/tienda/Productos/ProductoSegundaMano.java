package tienda.Productos;


import tienda.Usuarios.Cliente;
import tienda.Usuarios.Empleado;

public class ProductoSegundaMano extends Producto {
    private Valoracion valoracion = null;     
    private Cliente propietario;       
    private boolean bloqueado;
    

    public ProductoSegundaMano(ProductoVenta base, Cliente propietario) {

        this.propietario = propietario;
        this.bloqueado = false;
    }
    
    public void valoracion(double precioTasacion, EstadoProducto estado, Empleado empleado) {
    	Valoracion v = new Valoracion(precioTasacion,estado,empleado);
    	this.valoracion = v ;
    	}
    
    public Valoracion getValoracion() {
        return valoracion;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;

    }
    public void setPropietario(Cliente c) {
    	this.propietario.getCarteraIntercambio().remove(this);
    	
    }
   
    public EstadoProducto getEstado() { return this.valoracion.getEstado(); }
    public double getValorTasacion() { return this.valoracion.getPrecioTasacion(); }
}