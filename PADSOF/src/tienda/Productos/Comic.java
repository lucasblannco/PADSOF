package tienda.Productos;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class Comic extends ProductoVenta {
    private List<String> autores;
    private int numeroPaginas;
    private String editorial;
    private Date añoPublicacion;

    public Comic() {
        super();
        this.autores = new ArrayList<>();
    }

    @Override
    public double calcularPrecioFinal() {
        return 0.0;
    }
}
