package tienda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import intercambios.EstadoOferta;
import intercambios.Oferta;
import usuarios.Cliente;
import usuarios.UsuarioRegistrado;
import ventas.Pedido;

public class Estadistica {

    private static Estadistica instancia;

    private Estadistica() {}

    public static Estadistica getInstancia() {
        if (instancia == null)
            instancia = new Estadistica();
        return instancia;
    }

    private int nProductosVentas = 1;
    private int nUsuarioRegistrado = 1;
    private int nUsuarioNoRegistrado = 1;
    private int nProducto2Mano = 1;
    private int nVentas = 1;
    private int nDescuentos = 1;
    private int nIntercambiosFinalizados = 1;
    private int nCategorias = 1;
    private int nCarritos = 1;
    private int nReseñas = 1;

    public int getnProductosVentas() { return nProductosVentas; }
    public void setnProductosVentas(int n) { this.nProductosVentas = n; }

    public int getnUsuarioRegistrado() { return nUsuarioRegistrado; }
    public void setnUsuarioRegistrado(int n) { this.nUsuarioRegistrado = n; }

    public int getnUsuarioNoRegistrado() { return nUsuarioNoRegistrado; }
    public void setnUsuarioNoRegistrado(int n) { this.nUsuarioNoRegistrado = n; }

    public int getnProducto2Mano() { return nProducto2Mano; }
    public void setnProducto2Mano(int n) { this.nProducto2Mano = n; }

    public int getnVentas() { return nVentas; }
    public void setnVentas(int n) { this.nVentas = n; }

    public int getnDescuentos() { return nDescuentos; }
    public void setnDescuentos(int n) { this.nDescuentos = n; }

    public int getnIntercambiosFinalizados() { return nIntercambiosFinalizados; }
    public void setnIntercambiosFinalizados(int n){ this.nIntercambiosFinalizados = n; }

    public int getnCategorias() { return nCategorias; }
    public void setnCategorias(int n) { this.nCategorias = n; }

    public int getnCarritos() { return nCarritos; }
    public void setnCarritos(int n) { this.nCarritos = n; }

    public int getnReseñas() { return nReseñas; }
    public void setnReseñas(int n) { this.nReseñas = n; }

    public List<Cliente> obtenerClientesConMasCompras() {
        Tienda tienda = Tienda.getInstancia();

        if (tienda == null) {
            System.out.println("La tienda no ha sido inicializada.");
            return Collections.emptyList();
        }

        List<UsuarioRegistrado> usuarios = tienda.getUsuarios();

        List<Cliente> clientes = new ArrayList<>();
        for (UsuarioRegistrado u : usuarios) {
            if (u instanceof Cliente) {
                clientes.add((Cliente) u);
            }
        }

        if (clientes.isEmpty()) {
            return Collections.emptyList();
        }

        clientes.sort(Comparator.comparingInt(
            (Cliente c) -> c.getHistorialPedidos().size()
        ).reversed());

        return clientes;
    }

    public List<Cliente> obtenerClientesConMasIntercambios() {
        Tienda tienda = Tienda.getInstancia();

        if (tienda == null) {
            System.out.println("La tienda no ha sido inicializada.");
            return Collections.emptyList();
        }

        List<UsuarioRegistrado> usuarios = tienda.getUsuarios();
        List<Oferta> intercambios = tienda.getIntercambiosFinalizados();

        List<Cliente> clientes = new ArrayList<>();
        for (UsuarioRegistrado u : usuarios) {
            if (u instanceof Cliente) {
                clientes.add((Cliente) u);
            }
        }

        if (clientes.isEmpty()) {
            return Collections.emptyList();
        }

        clientes.sort(Comparator.comparingInt((Cliente c) -> {
            int count = 0;
            for (Oferta o : intercambios) {
                if (o.getEstado() == EstadoOferta.ACEPTADA
                        && (o.getOrigen().equals(c) || o.getDestino().equals(c))) {
                    count++;
                }
            }
            return count;
        }).reversed());

        return clientes;
    }

    public double calcularIngresosRango(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            System.out.println("Las fechas de inicio y fin no pueden ser null.");
            return 0.0;
        }

        if (fin.isBefore(inicio)) {
            System.out.println("La fecha de fin no puede ser anterior a la de inicio.");
            return 0.0;
        }

        Tienda tienda = Tienda.getInstancia();
        if (tienda == null) {
            System.out.println("La tienda no ha sido inicializada.");
            return 0.0;
        }

        double total = 0.0;
        for (Pedido p : tienda.getHistorialVentas()) {
            LocalDate fechaPedido = p.getFechaCreacion().toLocalDate();
            if (!fechaPedido.isBefore(inicio) && !fechaPedido.isAfter(fin)) {
                total += p.getTotal();
            }
        }
        return total;
    }

    public double[] calcularIngresosMeses() {
        Tienda tienda = Tienda.getInstancia();
        if (tienda == null) {
            System.out.println("La tienda no ha sido inicializada.");
            return new double[12];
        }

        double[] ingresosPorMes = new double[12];
        int añoActual = LocalDateTime.now().getYear();

        for (Pedido p : tienda.getHistorialVentas()) {
            LocalDateTime fecha = p.getFechaCreacion();
            if (fecha != null && fecha.getYear() == añoActual) {
                int mes = fecha.getMonthValue() - 1;
                ingresosPorMes[mes] += p.getTotal();
            }
        }
        return ingresosPorMes;
    }

    public double calcularIngresosVenta() {
        Tienda tienda = Tienda.getInstancia();
        if (tienda == null) {
            System.out.println("La tienda no ha sido inicializada.");
            return 0.0;
        }

        double total = 0.0;
        for (Pedido p : tienda.getHistorialVentas()) {
            total += p.getTotal();
        }
        return total;
    }

    public double calcularIngresosTasacion() {
        Tienda tienda = Tienda.getInstancia();
        if (tienda == null) {
            System.out.println("La tienda no ha sido inicializada.");
            return 0.0;
        }

        double total = 0.0;

        for (var p : tienda.getCatalogoIntercambio()) {
            if (p.getValoracion() != null) {
                total += p.getValoracion().getPrecioTasacion();
            }
        }

        for (var p : tienda.getPendientesTasacion()) {
            if (p.getValoracion() != null) {
                total += p.getValoracion().getPrecioTasacion();
            }
        }

        return total;
    }
}