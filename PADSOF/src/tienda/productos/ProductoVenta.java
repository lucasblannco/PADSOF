package productos;



import java.util.List;
import java.util.ArrayList;

public abstract class ProductoVenta extends Producto {
    protected double precioOficial;
    protected int stockDisponible;
    protected boolean enOferta;
    protected List<Reseña> reseñas;
    protected boolean promocionable;
    

    public ProductoVenta() {
        super();
        this.reseñas = new ArrayList<>();
    }

    public double getMediaPuntuacion() {
        
        if (this.reseñas == null || this.reseñas.isEmpty()) {
            return 0.0;
        }

       
        double suma = 0;
        for (Reseña r : this.reseñas) {
            suma += r.getPuntuacion() ;
        }

       
        return suma / this.reseñas.size();
    }
    
    /*el descuento se calcula en el pedido o carrito
     * public double calcularPrecioFinal() {
    	
        double descuento = tienda.Tienda.getInstancia().buscarDescuentoParaProducto(this.id);
   
        return this.precioOficial * (1- descuento);
    }*/

    public double getPrecioOficial() {
        return precioOficial;
    }

    public void setPrecioOficial(double precioOficial) {
        this.precioOficial = precioOficial;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(int stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public boolean isEnOferta() {
        return enOferta;
    }

    public void setEnOferta(boolean enOferta) {
        this.enOferta = enOferta;
    }

    public List<Reseña> getReseñas() {
        return reseñas;
    }

    public void setReseñas(List<Reseña> reseñas) {
        this.reseñas = reseñas;
    }
    
    public boolean getPromocionable() {
    	return this.promocionable;
    }
   
    public void setPromocionable(boolean p) {
    	this.promocionable = p;
    }
}
