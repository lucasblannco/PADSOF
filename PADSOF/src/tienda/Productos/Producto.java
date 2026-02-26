package tienda.Productos;



import java.util.List;
import java.util.ArrayList;

public abstract class Producto {
    protected String id;
    protected String nombre;
    protected String descripcion;
    protected IMAGEN imagenRuta;
    protected List<Categoria> categorias;
    //protected List<Reseña> reseñas; LOS PRODUCTOS DE SEGUNDA MANO NO TIENEN RESEÑA

    public Producto() {
        this.categorias = new ArrayList<>();
        this.reseñas = new ArrayList<>();
    }
}
