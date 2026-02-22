package prueba;

import java.util.*;

public class Gestor extends Usuario_Registrado {

    private ArrayList<Producto> catalogo;
    private ArrayList<Empleado> empleados;

    public Gestor(String nickname, String password, String mail) {
        super(nickname, password, mail);
        this.catalogo = new ArrayList<>();
        this.empleados = new ArrayList<>();
    }

    
    public void añadirProductoalSistema(Producto p) {};
    public void verCatalogo() {};
    public void eliminarEmpleado(Empleado e) {} ;
    public void verEstadisticas() {};
    public void gestionarEmpleados() {};
    public void configurarTiempoBloqueo() {};
    public void aplicarDescuentos() {};
    public void gestionPrecios() {};
}
    