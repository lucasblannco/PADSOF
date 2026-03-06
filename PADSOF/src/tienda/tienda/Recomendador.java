package tienda;

import java.util.*;
import java.util.stream.Collectors;
import tienda.Productos.*;
import tienda.Usuarios.Cliente;
import tienda.Ventas.*;

public class Recomendador {
    private int limiteMaximo = 5; // Configurable por el Gestor
    private boolean activo = true; // El Gestor puede apagarlo

    public List<ProductoVenta> generarSugerencias(Cliente cliente) {
        List<ProductoVenta> sugerencias = new ArrayList<>();

        if (!this.activo) return sugerencias;

        
        Categoria favorita = cliente.determinarCategoriaFavorita();

        // 2. Buscamos productos que tengan ESA categoría entre sus muchas categorías
        List<ProductoVenta> stock = Tienda.getInstancia().getStockNuevos();
        for (ProductoVenta p : stock) {
            
            // Comprobamos si la "favorita" está en la lista de categorías del producto
            if (p.getCategorias().contains(favorita) && p.getMediaPuntuacion() >= 8 && p.getPromocionable()==true) { //buscamos productos con esa categoria y bien valorados
                sugerencias.add(p);
            }

            if (sugerencias.size() >= this.limiteMaximo) break; //si llenamos las sugerencias ya
        }

        // 3. Relleno si no hay nada (igual que antes)
        if (sugerencias.isEmpty()) {
            for (int i = 0; i < stock.size() && i < limiteMaximo; i++) {
                sugerencias.add(stock.get(i));
            }
        }

        return sugerencias;
    }

    // Métodos para el Gestor
    public void setConfiguracion(int limite, boolean estado) {
        this.limiteMaximo = limite;
        this.activo = estado;
    }
}