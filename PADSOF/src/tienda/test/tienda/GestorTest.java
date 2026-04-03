package tienda; 
import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.BeforeEach;        
import org.junit.jupiter.api.Test;             
import java.util.Arrays;
import java.util.List;

import tienda.Tienda;
import usuarios.Gestor;
import usuarios.Empleado;
import usuarios.TipoPermisos;
import productos.Categoria;

public class GestorTest {
    private Tienda tienda;
    private Gestor gestor;

    @BeforeEach
    void setUp() {
        tienda = Tienda.getInstancia();
        tienda.vaciarTienda();
        gestor = new Gestor();
    }
    
    @Test
    void testDarDeBaja_Errores() {
        // Caso: ID nulo o vacío (Cubre el primer if de buscarEmpleadoporId)
        assertFalse(gestor.darDeBajaAEmpleado(null), "No debe aceptar IDs nulos");
        
        //  Caso: Empleado que no existe (Cubre el if 'e == null')
        assertFalse(gestor.darDeBajaAEmpleado("ID-INEXISTENTE"), "Debe fallar si el empleado no existe");
        
        // Caso: Usuario que existe pero NO es un empleado (Cubre el instanceof en buscar)
        tienda.registrarNuevoCliente("paco_cliente", "Pass@1234", "87654321X");
        String idCliente = tienda.buscarClientePorNickname("paco_cliente").getId();
        assertFalse(gestor.darDeBajaAEmpleado(idCliente), "No debe dejar dar de baja a un Cliente como empleado");
        
    }
    
    
    @Test
    void testDarDeBaja_ExitoCompletoYRetirarPermisos() {
      
        gestor.darDeAltaEmpleados_Permisos("empleado_baja", "Pass@1234", 
            Arrays.asList(TipoPermisos.GESTION_STOCK));
        Empleado emp = tienda.loginEmpleado("empleado_baja", "Pass@1234");
        String id = emp.getId();

      
        assertTrue(tienda.getUsuariosConSesionActiva().contains(emp), "Debe tener sesión activa al inicio");
        assertFalse(emp.getPermisos().isEmpty(), "Debe tener permisos al inicio");

        gestor.retirarPermiso(id, TipoPermisos.GESTION_STOCK);
        assertTrue(emp.getPermisos().isEmpty(), "Debe tener la lista de permisos vacia porque se ha retirado"); 
        boolean respuesta= gestor.retirarPermiso(id, TipoPermisos.GESTION_CATEGORIAS);
        assertFalse(respuesta, "Debe devolver false si intentas quitar un permiso que no posee");
        respuesta=gestor.retirarPermiso("PV-12",TipoPermisos.CONFIRMACION_INTERCAMBIO);
        assertFalse(respuesta, "Debe ser falso porque no se puede quitar permisos a un emleado que no existe");
        boolean resultado = gestor.darDeBajaAEmpleado(id);
        assertTrue(resultado, "La función debe devolver true al finalizar con éxito");
        assertTrue(emp.isDespedido(), "El atributo isDespedido debe ser true");
        assertTrue(emp.getPermisos().isEmpty(), "Los permisos deben haberse limpiado (.clear())");
        
       
        assertFalse(tienda.getUsuariosConSesionActiva().contains(emp), "La sesión debe haberse cerrado automáticamente");
    }
    
    
    
    
    @Test
    public void testCrearCategoria() {
        boolean res = gestor.crearCategoria("Manga", "Comics de Japon");
        
       
        assertTrue(res, "La categoría debería crearse bien");
        assertEquals(1, tienda.getCategorias().size(), "Debería haber 1 categoría");
        assertEquals("Manga", tienda.getCategorias().get(0).getNombre());
    }

    @Test
    public void testDarAltaEmpleadoYPermisos() {
        List<TipoPermisos> perms = Arrays.asList(TipoPermisos.GESTION_STOCK);
        boolean ok = gestor.darDeAltaEmpleados_Permisos("empleado1", "Pass@1234", perms);
        
        assertTrue(ok);
        
        Empleado e = tienda.loginEmpleado("empleado1", "Pass@1234");
        assertNotNull(e, "El empleado debería existir");
        assertTrue(e.getPermisos().contains(TipoPermisos.GESTION_STOCK));
    }
    
    
    @Test
    void testDarDeAltaEmpleadoNicknameDuplicado() {
        gestor.darDeAltaEmpleados("empleado1", "pass123");

        boolean resultado = gestor.darDeAltaEmpleados("empleado1", "123_Pass");

        assertFalse(resultado);
    }
    @Test
    void testAsignarPermisoEmpleado() {
        gestor.darDeAltaEmpleados("empleado2", "pass");
        Empleado e = (Empleado) Tienda.getInstancia().getUsuarios().get(1);

        boolean resultado = gestor.asignarPermiso(e.getId(), TipoPermisos.GESTION_STOCK);

        assertTrue(resultado);
        assertTrue(e.getPermisos().contains(TipoPermisos.GESTION_STOCK));
    }

    @Test
    void testModificarPrecioProductoInvalido() {
        boolean resultado = gestor.modificarPrecioProducto("idInexistente", 10);

        assertFalse(resultado);
    }
    @Test
    void testSetTiempoMaxCarritoValido() {
        boolean resultado = gestor.setTiempoMaxCarrito(30);

        assertTrue(resultado);
    }
    @Test
    void testCrearCategoriaCorrecta() {
        boolean resultado = gestor.crearCategoria("Comics", "Categoria de comics");

        assertTrue(resultado);
    }

 
    @Test
    void testCrearCategoriaNombreVacio() {
        boolean resultado = gestor.crearCategoria("", "desc");

        assertFalse(resultado);
    }
    @Test
    void testSetTiempoMaxCarritoInvalido() {
        boolean resultado = gestor.setTiempoMaxCarrito(-5);

        assertFalse(resultado);
    }
    @Test
    void testDarDeAltaEmpleadoNull() {
        boolean resultado = gestor.darDeAltaEmpleados(null, "passW23_");

        assertFalse(resultado);
    }
    
    @Test
    public void testNoDuplicarNickname() {
        gestor.darDeAltaEmpleados("juan", "Juan@1234");
        boolean repetido = gestor.darDeAltaEmpleados("juan", "Otra@5678");
        
        assertFalse(repetido, "No debería dejar repetir el nickname");
    }
}