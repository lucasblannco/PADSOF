package tienda.Productos;



import java.util.List;
import java.util.ArrayList;

public abstract class ProductoVenta extends Producto {
    protected double precioOficial;
    protected int stockDisponible;
    protected boolean enOferta;
    protected List<Reseña> reseñas;
    protected double descuento;

    public ProductoVenta() {
        super();
        this.reseñas = new ArrayList<>();
    }

    public abstract double calcularPrecioFinal();

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

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
}
