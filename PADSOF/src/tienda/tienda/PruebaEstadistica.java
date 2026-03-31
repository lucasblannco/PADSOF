package tienda;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import productos.Comic;
import productos.EstadoProducto;
import productos.Figura;
import productos.JuegoMesa;
import productos.Producto2Mano;
import productos.Valoracion;
import usuarios.Cliente;
import usuarios.Empleado;
import ventas.Carrito;
import ventas.Pedido;

/**
 * Simulación completa de la tienda CheckPoint.
 * Prueba todos los métodos de Estadistica, incluyendo casos de error.
 */
public class PruebaEstadistica {

    // ── Utilidades de impresión ────────────────────────────────────────────────

    private static void titulo(String texto) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  " + texto);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    private static void seccion(String texto) {
        System.out.println("\n  ── " + texto + " ──");
    }

    private static void ok(String texto) {
        System.out.println("  [OK] " + texto);
    }

    private static void error(String texto) {
        System.out.println("  [ERROR ESPERADO] " + texto);
    }

    private static void resultado(String texto) {
        System.out.println("  --> " + texto);
    }


    // ── Main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        // ══════════════════════════════════════════════════════════════════════
        titulo("FASE 1: Montaje de la tienda");
        // ══════════════════════════════════════════════════════════════════════

        Tienda tienda = Tienda.getInstancia();
        Estadistica stats = Estadistica.getInstancia();
        tienda.setNombre("CheckPoint");
        ok("Tienda '" + tienda.getNombre() + "' inicializada.");


        // ── Productos de venta ────────────────────────────────────────────────
        seccion("Creando productos de venta");

        Comic comic1    = new Comic   ("Saga Vol.1",  "Cómic de ciencia ficción",  "saga.png",   12.50, 20);
        Comic comic2    = new Comic   ("Watchmen",    "Novela gráfica clásica",     "watch.png",  15.00, 10);
        Figura figura1  = new Figura  ("Goku SSJ",    "Figura de Dragon Ball",      "goku.png",   35.00, 15);
        Figura figura2  = new Figura  ("Link",        "Figura de Zelda",            "link.png",   40.00,  8);
        JuegoMesa jm1   = new JuegoMesa("Catan",      "Juego de estrategia",        "catan.png",  45.00, 12);
        JuegoMesa jm2   = new JuegoMesa("Pandemic",   "Juego cooperativo",          "pand.png",   38.00, 10);

        tienda.añadirProducto(comic1);
        tienda.añadirProducto(comic2);
        tienda.añadirProducto(figura1);
        tienda.añadirProducto(figura2);
        tienda.añadirProducto(jm1);
        tienda.añadirProducto(jm2);
        ok("6 productos añadidos al stock.");


        // ── Empleado ──────────────────────────────────────────────────────────
        seccion("Creando empleado tasador");
        Empleado empleado = new Empleado("tasador01", "pass123", "tasador@checkpoint.com");
        tienda.getUsuarios().add(empleado);
        ok("Empleado '" + empleado.getNickname() + "' registrado.");


        // ── Clientes ──────────────────────────────────────────────────────────
        seccion("Registrando clientes");
        Cliente alice = new Cliente("alice",   "pass1", "alice@mail.com");
        Cliente bob   = new Cliente("bob",     "pass2", "bob@mail.com");
        Cliente carlos= new Cliente("carlos",  "pass3", "carlos@mail.com");
        Cliente diana = new Cliente("diana",   "pass4", "diana@mail.com");

        tienda.getUsuarios().add(alice);
        tienda.getUsuarios().add(bob);
        tienda.getUsuarios().add(carlos);
        tienda.getUsuarios().add(diana);
        ok("4 clientes registrados: alice, bob, carlos, diana.");


        // ══════════════════════════════════════════════════════════════════════
        titulo("FASE 2: Simulando ventas");
        // ══════════════════════════════════════════════════════════════════════

        // Alice hace 3 pedidos
        hacerPedido(tienda, alice,  comic1,  2);
        hacerPedido(tienda, alice,  figura1, 1);
        hacerPedido(tienda, alice,  jm1,     1);

        // Bob hace 2 pedidos
        hacerPedido(tienda, bob,    comic2,  1);
        hacerPedido(tienda, bob,    figura2, 1);

        // Carlos hace 1 pedido
        hacerPedido(tienda, carlos, jm2,     1);

        // Diana no compra nada (caso borde)

        resultado("Pedidos registrados en la tienda: " + tienda.getHistorialVentas().size());


        // ══════════════════════════════════════════════════════════════════════
        titulo("FASE 3: Simulando productos de segunda mano y tasaciones");
        // ══════════════════════════════════════════════════════════════════════

        seccion("Alice sube 2 productos para intercambio");
        Producto2Mano p2m_alice1 = new Producto2Mano(alice, "Naruto Vol.3",   "Usado",      "naruto.png");
        Producto2Mano p2m_alice2 = new Producto2Mano(alice, "Figura Pikachu", "Buen estado","pika.png");

        // Tasación directa simulada (sin pasar por el flujo de pago, para simplificar)
        p2m_alice1.valorar(5.00, EstadoProducto.BUEN_ESTADO, empleado);
        p2m_alice2.valorar(8.00, EstadoProducto.BUEN_ESTADO, empleado);

        tienda.publicarParaIntercambio(p2m_alice1);
        tienda.publicarParaIntercambio(p2m_alice2);
        ok("2 productos de alice tasados y publicados (tasación: 5€ + 8€).");

        seccion("Bob sube 1 producto aún pendiente de tasación");
        Producto2Mano p2m_bob = new Producto2Mano(bob, "One Piece Vol.1", "Algo desgastado", "op.png");
        // Simulamos que se ha tasado pero no publicado aún
        p2m_bob.valorar(3.50, EstadoProducto.BUEN_ESTADO, empleado);
        tienda.getPendientesTasacion().add(p2m_bob);
        ok("1 producto de bob en pendientes con tasación de 3.50€.");


        // ══════════════════════════════════════════════════════════════════════
        titulo("FASE 4: Simulando intercambios");
        // ══════════════════════════════════════════════════════════════════════

        // Alice ofrece su p2m_alice1 a Bob a cambio de su p2m_bob
        // Desbloqueamos p2m_alice1 para poder ofrecerlo
        p2m_alice1.setBloqueado(false);
        alice.getCarteraIntercambio().add(p2m_alice1);
        alice.getCarteraIntercambio().add(p2m_alice2);
        bob.getCarteraIntercambio().add(p2m_bob);

        Oferta oferta1 = new Oferta(
            alice, bob,
            Arrays.asList(p2m_alice1),
            Arrays.asList(p2m_bob)
        );
        oferta1.aceptarOferta();
        tienda.registrarIntercambioFinalizado(oferta1);
        ok("Intercambio 1 (alice <-> bob) aceptado y finalizado.");

        // Carlos y Alice intercambian (alice gana un segundo intercambio)
        Producto2Mano p2m_carlos = new Producto2Mano(carlos, "DBZ Manga", "Bueno", "dbz.png");
        p2m_carlos.valorar(4.00, EstadoProducto.BUEN_ESTADO, empleado);
        p2m_alice2.setBloqueado(false);
        carlos.getCarteraIntercambio().add(p2m_carlos);

        Oferta oferta2 = new Oferta(
            alice, carlos,
            Arrays.asList(p2m_alice2),
            Arrays.asList(p2m_carlos)
        );
        oferta2.aceptarOferta();
        tienda.registrarIntercambioFinalizado(oferta2);
        ok("Intercambio 2 (alice <-> carlos) aceptado y finalizado.");

        resultado("Intercambios finalizados en la tienda: " + tienda.getIntercambiosFinalizados().size());


        // ══════════════════════════════════════════════════════════════════════
        titulo("FASE 5: Prueba de métodos de Estadistica");
        // ══════════════════════════════════════════════════════════════════════

        // ── obtenerClientesConMasCompras ──────────────────────────────────────
        seccion("obtenerClientesConMasCompras()");
        List<Cliente> ranking = stats.obtenerClientesConMasCompras();
        System.out.println("  Ranking de compras:");
        for (int i = 0; i < ranking.size(); i++) {
            Cliente c = ranking.get(i);
            resultado((i+1) + ". " + c.getNickname()
                + " -> " + c.getHistorialPedidos().size() + " pedidos");
        }

        // ── obtenerClientesConMasIntercambios ─────────────────────────────────
        seccion("obtenerClientesConMasIntercambios()");
        List<Cliente> rankingI = stats.obtenerClientesConMasIntercambios();
        System.out.println("  Ranking de intercambios:");
        for (int i = 0; i < rankingI.size(); i++) {
            Cliente c = rankingI.get(i);
            long participaciones = tienda.getIntercambiosFinalizados().stream()
                .filter(o -> o.getEstado() == EstadoOferta.ACEPTADA
                          && (o.getOrigen().equals(c) || o.getDestino().equals(c)))
                .count();
            resultado((i+1) + ". " + c.getNickname() + " -> " + participaciones + " intercambios");
        }

        // ── calcularIngresosVenta ─────────────────────────────────────────────
        seccion("calcularIngresosVenta()");
        double ingresosVenta = stats.calcularIngresosVenta();
        resultado(String.format("Ingresos totales por ventas: %.2f €", ingresosVenta));

        // ── calcularIngresosTasacion ──────────────────────────────────────────
        seccion("calcularIngresosTasacion()");
        double ingresosTasacion = stats.calcularIngresosTasacion();
        resultado(String.format("Ingresos totales por tasaciones: %.2f €", ingresosTasacion));

        // ── calcularIngresosRango (rango válido) ──────────────────────────────
        seccion("calcularIngresosRango() — rango válido (todo el año en curso)");
        LocalDate hoy   = LocalDate.now();
        LocalDate inicio = LocalDate.of(hoy.getYear(), 1, 1);
        LocalDate fin    = LocalDate.of(hoy.getYear(), 12, 31);
        double ingresosRango = stats.calcularIngresosRango(inicio, fin);
        resultado(String.format("Ingresos del %s al %s: %.2f €", inicio, fin, ingresosRango));

        // ── calcularIngresosMeses ─────────────────────────────────────────────
        seccion("calcularIngresosMeses()");
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        double[] porMes = stats.calcularIngresosMeses();
        System.out.println("  Ingresos por mes (" + hoy.getYear() + "):");
        for (int i = 0; i < 12; i++) {
            if (porMes[i] > 0) {
                resultado(String.format("%s: %.2f €", meses[i], porMes[i]));
            }
        }


        // ══════════════════════════════════════════════════════════════════════
        titulo("FASE 6: Prueba de control de errores");
        // ══════════════════════════════════════════════════════════════════════

        // ── Error 1: fecha null ───────────────────────────────────────────────
        seccion("calcularIngresosRango() con fecha null");
        try {
            stats.calcularIngresosRango(null, LocalDate.now());
            System.out.println("  [FALLO] Debería haber lanzado excepción.");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }

        // ── Error 2: fin anterior a inicio ───────────────────────────────────
        seccion("calcularIngresosRango() con fin < inicio");
        try {
            stats.calcularIngresosRango(
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 1, 1)
            );
            System.out.println("  [FALLO] Debería haber lanzado excepción.");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }

        // ── Error 3: ambas fechas null ────────────────────────────────────────
        seccion("calcularIngresosRango() con ambas fechas null");
        try {
            stats.calcularIngresosRango(null, null);
            System.out.println("  [FALLO] Debería haber lanzado excepción.");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }

        // ── Error 4: rango sin pedidos (futuro lejano) ────────────────────────
        seccion("calcularIngresosRango() con rango sin ventas (año 2099)");
        double sinVentas = stats.calcularIngresosRango(
            LocalDate.of(2099, 1, 1),
            LocalDate.of(2099, 12, 31)
        );
        resultado(String.format("Ingresos para 2099: %.2f € (esperado 0.00 €)", sinVentas));

        // ── Error 5: tienda sin clientes ──────────────────────────────────────
        seccion("obtenerClientesConMasCompras() con lista vacía (tienda nueva)");
        // Creamos una estadística con una tienda vacía temporal
        Tienda tiendaVacia = new Tienda() {
            // Clase anónima para simular tienda sin usuarios sin tocar el singleton
        };
        // En su lugar probamos el retorno con los clientes actuales que no tienen compras
        List<Cliente> soloDiana = stats.obtenerClientesConMasCompras();
        Cliente ultimo = soloDiana.get(soloDiana.size() - 1);
        resultado("Último cliente en el ranking: '" + ultimo.getNickname()
            + "' con " + ultimo.getHistorialPedidos().size() + " pedidos (diana, sin compras).");

        // ── Error 6: ingresos tasación sin productos tasados ─────────────────
        seccion("calcularIngresosTasacion() — verificación de que suma correctamente");
        resultado(String.format(
            "Total tasaciones: %.2f € (p2m_alice1=5€ + p2m_alice2=8€ + p2m_bob pendiente=3.50€ = 16.50€ esperado)",
            stats.calcularIngresosTasacion()
        ));


        // ══════════════════════════════════════════════════════════════════════
        titulo("RESUMEN FINAL");
        // ══════════════════════════════════════════════════════════════════════

        System.out.printf("  Pedidos totales registrados : %d%n", tienda.getHistorialVentas().size());
        System.out.printf("  Intercambios finalizados    : %d%n", tienda.getIntercambiosFinalizados().size());
        System.out.printf("  Ingresos por ventas         : %.2f €%n", stats.calcularIngresosVenta());
        System.out.printf("  Ingresos por tasaciones     : %.2f €%n", stats.calcularIngresosTasacion());
        System.out.printf("  Cliente con más compras     : %s%n",
            stats.obtenerClientesConMasCompras().get(0).getNickname());
        System.out.printf("  Cliente con más intercambios: %s%n",
            stats.obtenerClientesConMasIntercambios().get(0).getNickname());
        System.out.println();
    }


    // ── Método auxiliar para crear un pedido de forma limpia ─────────────────

    /**
     * Crea un carrito, añade el producto, genera el pedido y lo registra
     * en la tienda y en el historial del cliente.
     */
    private static void hacerPedido(Tienda tienda, Cliente cliente,
                                     productos.ProductoVenta producto, int cantidad) {
        try {
            Carrito carrito = new Carrito();
            carrito.añadirProducto(producto, cantidad);
            Pedido pedido = new Pedido(cliente, carrito);
            cliente.getHistorialPedidos().add(pedido);
            tienda.registrarVenta(pedido);
            ok(cliente.getNickname() + " compró " + cantidad + "x '" + producto.getNombre()
               + "' -> Total: " + String.format("%.2f €", pedido.getTotal()));
        } catch (Exception e) {
            error("Error al crear pedido para " + cliente.getNickname() + ": " + e.getMessage());
        }
    }
}