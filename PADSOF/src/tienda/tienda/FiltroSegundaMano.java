package tienda;

import productos.EstadoProducto;
import productos.Producto2Mano;

public class FiltroSegundaMano {

	private double valorMinimo;
	private double valorMaximo;
	private EstadoProducto estadoMinimo;
	
	public FiltroSegundaMano() {
		this.valorMinimo=0;
		this.valorMaximo=Double.MAX_VALUE;
		this.estadoMinimo=null;
	}
	
	public boolean cumpleFiltro(Producto2Mano p) {
	    if (p == null) return false;
	    if (!p.isVisible() || p.isBloqueado()) return false;
	  
	    double valorTasacion = p.getValoracion().getPrecioTasacion();
	    if (valorTasacion < valorMinimo || valorTasacion > valorMaximo) return false;
	    
	    
	    if (estadoMinimo != null) {
	        if (p.getValoracion().getEstadoProducto() == EstadoProducto.NO_ACEPTADO) 
	            return false;
	        if (p.getValoracion().getEstadoProducto().ordinal() > estadoMinimo.ordinal()) 
	            return false;
	    }
	    
	    return true;
	}
	
	  public double getValorMinimo() { return valorMinimo; }
	    public double getValorMaximo() { return valorMaximo; }
	    public EstadoProducto getEstadoMinimo() { return estadoMinimo; }

	    public void setValorMinimo(double valorMinimo) { 
	        this.valorMinimo = valorMinimo; 
	    }
	    public void setValorMaximo(double valorMaximo) { 
	        this.valorMaximo = valorMaximo; 
	    }
	    public void setEstadoMinimo(EstadoProducto estadoMinimo) { 
	        this.estadoMinimo = estadoMinimo; 
	    }
	
	
	
	
}
