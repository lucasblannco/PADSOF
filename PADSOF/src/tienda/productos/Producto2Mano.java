package productos;

import usuarios.*;

public class Producto2Mano extends Producto {
    private Valoracion valoracion = null;     
    private Cliente propietario;       
    private boolean bloqueado;
    private boolean visible;
    

    public Producto2Mano( Cliente propietario, String nombre, String descp) {
    	this.id = "USED-" + base.getId() + "-" + System.currentTimeMillis();
    	this.nombre = nombre;
    	this.descripcion = descp;
        this.propietario = propietario;
      //Ver bien esto porque igual es es mejor pasarle los atributos 
    	public void subirProducto(Producto2Mano p) {
    		Producto2Mano product= new Producto2Mano(null, 
        this.bloqueado = false;
        this.visible = false;
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

	public void setBloqueado(boolean b) {
		// TODO Auto-generated method stub
		this.bloqueado= b;
		
	}

	public String getNombre() {
		// TODO Auto-generated method stub
		return this.nombre;
	}
	public Cliente getPropietario() {
		return this.propietario;
	}
	
	public boolean isVisible() { return visible; }
	public void setVisible(boolean visible) { this.visible = visible; }

	public boolean isBloqueado() {
		// TODO Auto-generated method stub
		return t;
	}
}