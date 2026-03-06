package productos;



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
       
    }
// --- GETTERS ---
    
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // --- SETTERS ---

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public List<Categoria> getCategorias(){
    	return this.categorias;
    }
    
}
