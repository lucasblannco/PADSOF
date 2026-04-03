package tienda;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.Categoria;
import productos.ProductoVenta;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.TipoPermisos;

class RecomendadorTest {

    private Tienda tienda;
    private Gestor gestor;
    private Empleado empleado;

    @BeforeEach
    void setUp() {
        tienda = Tienda.getInstancia();
        tienda.vaciarTienda();

        gestor = tienda.getGestor();

        // Crear empleado con permisos
        gestor.darDeAltaEmpleados("emp1", "1234");
        empleado = tienda.obtenerEmpleadosTienda().get(0);
        gestor.asignarPermiso(empleado.getId(), TipoPermisos.GESTION_STOCK);
    }
}