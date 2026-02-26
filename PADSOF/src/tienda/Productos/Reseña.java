package tienda.Productos;

import tienda.Usuarios.*;

import java.time.LocalDate;

public class Reseña {
    private String idReseña;
    private Cliente autor;
    private ProductoVenta producto;
    private double puntuacion; // 0-10
    private String comentario;
    private LocalDate fecha;

    public Reseña(Cliente autor,ProductoVenta p , int puntuacion, String comentario) {
        this.autor = autor;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fecha = LocalDate.now();
    }
}
