package productos;

public abstract class Producto {
    protected String id;
    protected String nombre;
    protected String descripcion;
    protected String imagenRuta;
    
    public Producto(String id, String nombre, String descripcion, String imagenRuta) {
        this.id = id;
        this.nombre=nombre;
        this.descripcion=descripcion;  
        this.imagenRuta = imagenRuta;
    } 
    
}
