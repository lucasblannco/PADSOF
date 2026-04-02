package pruebas;

import java.util.*;
import productos.*;
import tienda.*;
import usuarios.*;
import ventas.*;

public class PruebaNotificacion {

	static int correctos = 0;
	static int fallos    = 0;

	static void check(String nombre, boolean condicion) {
		if (condicion) {
			System.out.println("\tCORRECTO -> " + nombre);
			correctos++;
		} else {
			System.out.println("\tFALLO -> " + nombre);
			fallos++;
		}
	}

	public static void main(String[] args) {

		/*
		 * Montamos lo minimo necesario: un cliente y una categoria.
		 * El cliente tiene preferencias por defecto (todas las configurables activas).
		 */
		System.out.println("\n============= MONTAJE =============");

		Tienda tienda = Tienda.getInstancia();

		Cliente alice = new Cliente("alice", "Alice@1234", "11111111A");
		tienda.getUsuarios().add(alice);

		Categoria catComics = new Categoria("Comics", "desc");
		tienda.getCategorias().add(catComics);

		System.out.println("\tMontaje listo.");


		/*
		 * Comprobamos la clase Notificacion directamente:
		 * creacion, estado inicial, marcarComoLeida y toString.
		 */
		System.out.println("\n============= Notificacion =============");

		Notificacion n1 = new Notificacion("Pedido listo para recoger", TipoNotificacion.PEDIDO_LISTO);
		Notificacion n2 = new Notificacion("Mensaje para empleado");

		check("n1 tiene id asignado",          n1.getId() != null && !n1.getId().isEmpty());
		check("n1 empieza como no leida",       !n1.isLeida());
		check("n1 tiene el tipo correcto",      n1.getTipo() == TipoNotificacion.PEDIDO_LISTO);
		check("n1 tiene el mensaje correcto",   n1.getMensaje().equals("Pedido listo para recoger"));
		check("n1 tiene fecha de envio",        n1.getFechaEnvio() != null);
		check("n2 tiene tipo EMPLEADOS por defecto", n2.getTipo() == TipoNotificacion.EMPLEADOS);

		n1.marcarComoLeida();
		check("n1 queda como leida tras marcarComoLeida", n1.isLeida());

		check("toString contiene el id",        n1.toString().contains(n1.getId()));
		check("toString contiene el mensaje",   n1.toString().contains("Pedido listo para recoger"));
		check("toString contiene el tipo",      n1.toString().contains("PEDIDO_LISTO"));
		check("toString indica que esta leida", n1.toString().contains("leida"));

		// Las notificaciones se registran en el historial de la tienda
		check("n1 registrada en historial de tienda",
			tienda.getHistorialNotificaciones().contains(n1));


		/*
		 * Comprobamos recibirNotificacionTipo en Cliente.
		 * Las obligatorias siempre llegan, las configurables dependen de preferencias.
		 */
		System.out.println("\n============= recibirNotificacionTipo =============");

		int antes = alice.getNotificaciones().size();
		alice.recibirNotificacionTipo("Tu pago ha sido procesado", TipoNotificacion.PAGO_EXITOSO);
		check("Notificacion obligatoria (PAGO_EXITOSO) llega siempre",
			alice.getNotificaciones().size() == antes + 1);

		antes = alice.getNotificaciones().size();
		alice.recibirNotificacionTipo("Nuevo descuento disponible", TipoNotificacion.DESCUENTO);
		check("Notificacion configurable (DESCUENTO) llega si esta activa (por defecto activa)",
			alice.getNotificaciones().size() == antes + 1);

		// EMPLEADOS nunca llega a un cliente
		antes = alice.getNotificaciones().size();
		alice.recibirNotificacionTipo("Esto no deberia llegar", TipoNotificacion.EMPLEADOS);
		check("Notificacion tipo EMPLEADOS no llega a un cliente",
			alice.getNotificaciones().size() == antes);


		/*
		 * Comprobamos getNotificacionesNoLeidas en Cliente.
		 * Marcamos una como leida y comprobamos que el filtro funciona.
		 */
		System.out.println("\n============= getNotificacionesNoLeidas =============");

		int totalAntes = alice.getNotificaciones().size();
		int noLeidasAntes = alice.getNotificacionesNoLeidas().size();

		// Marcamos la primera notificacion como leida
		alice.getNotificaciones().get(0).marcarComoLeida();

		check("Despues de marcar 1 como leida, las no leidas bajan en 1",
			alice.getNotificacionesNoLeidas().size() == noLeidasAntes - 1);
		check("El total de notificaciones no cambia",
			alice.getNotificaciones().size() == totalAntes);


		/*
		 * Comprobamos PreferenciaNotificacion: debeRecibirNotificacion,
		 * modificarPreferencia y que las obligatorias no se pueden desactivar.
		 */
		System.out.println("\n============= PreferenciaNotificacion =============");

		PreferenciaNotificacion pref = alice.getPreferencias();

		// Obligatorias siempre devuelven true
		check("CODIGO_RECOGIDA es obligatoria",    pref.debeRecibirNotificacion(TipoNotificacion.CODIGO_RECOGIDA));
		check("PEDIDO_LISTO es obligatoria",        pref.debeRecibirNotificacion(TipoNotificacion.PEDIDO_LISTO));
		check("OFERTA_RECIBIDA es obligatoria",     pref.debeRecibirNotificacion(TipoNotificacion.OFERTA_RECIBIDA));
		check("PAGO_EXITOSO es obligatoria",        pref.debeRecibirNotificacion(TipoNotificacion.PAGO_EXITOSO));
		check("CARRITO_CADUCADO es obligatoria",    pref.debeRecibirNotificacion(TipoNotificacion.CARRITO_CADUCADO));
		check("OFERTA_RECHAZADA es obligatoria",    pref.debeRecibirNotificacion(TipoNotificacion.OFERTA_RECHAZADA));
		check("EMPLEADOS no llega a cliente",      !pref.debeRecibirNotificacion(TipoNotificacion.EMPLEADOS));

		// Configurables activas por defecto
		check("DESCUENTO activo por defecto",           pref.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));
		check("PEDIDO_CADUCADO activo por defecto",     pref.debeRecibirNotificacion(TipoNotificacion.PEDIDO_CADUCADO));
		check("PRODUCTO_INTERCAMBIO_NUEVO activo",      pref.debeRecibirNotificacion(TipoNotificacion.PRODUCTO_INTERCAMBIO_NUEVO));
		check("PEDIDO_ENTREGADO activo por defecto",    pref.debeRecibirNotificacion(TipoNotificacion.PEDIDO_ENTREGADO));
		check("VALORACION_COMPLETADA activo",           pref.debeRecibirNotificacion(TipoNotificacion.VALORACION_COMPLETADA));
		check("OFERTA_CADUCADA activo por defecto",     pref.debeRecibirNotificacion(TipoNotificacion.OFERTA_CADUCADA));

		// Desactivar una configurable
		alice.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, false);
		check("DESCUENTO desactivado tras modificarPreferencia",
			!pref.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));

		// Verificar que ya no llega al cliente
		antes = alice.getNotificaciones().size();
		alice.recibirNotificacionTipo("Oferta especial", TipoNotificacion.DESCUENTO);
		check("Con DESCUENTO desactivado la notificacion no llega",
			alice.getNotificaciones().size() == antes);

		// Reactivar
		alice.configurarPreferenciaNotificacion(TipoNotificacion.DESCUENTO, true);
		check("DESCUENTO activo de nuevo tras reactivar",
			pref.debeRecibirNotificacion(TipoNotificacion.DESCUENTO));

		// Intentar desactivar una obligatoria no tiene efecto
		alice.configurarPreferenciaNotificacion(TipoNotificacion.PAGO_EXITOSO, false);
		check("Intentar desactivar PAGO_EXITOSO (obligatoria) no tiene efecto",
			pref.debeRecibirNotificacion(TipoNotificacion.PAGO_EXITOSO));


		/*
		 * Comprobamos categorias de interes en PreferenciaNotificacion.
		 * Añadir, recibir notificacion de categoria y eliminar.
		 */
		System.out.println("\n============= categorias de interes =============");

		check("Sin categorias de interes, notificarProductoNuevoCategoria no llega",
			!pref.NotificacionesProductosNUevosCategoriasInteres("Comics"));

		alice.añadirCategoriaInteresParaRecibirInfo("Comics");
		check("Tras añadir Comics, notificacion de Comics llega",
			pref.NotificacionesProductosNUevosCategoriasInteres("Comics"));

		// La notificacion llega al cliente
		antes = alice.getNotificaciones().size();
		alice.notificarProductoNuevoCategoria("Nuevo comic disponible", "Comics");
		check("notificarProductoNuevoCategoria llega si la categoria esta en intereses",
			alice.getNotificaciones().size() == antes + 1);

		// Una categoria que no esta en intereses no llega
		tienda.getCategorias().add(new Categoria("Juegos", "desc"));
		antes = alice.getNotificaciones().size();
		alice.notificarProductoNuevoCategoria("Nuevo juego disponible", "Juegos");
		check("notificarProductoNuevoCategoria no llega si la categoria no esta en intereses",
			alice.getNotificaciones().size() == antes);

		alice.eliminarCategoriaInteres("Comics");
		check("Tras eliminar Comics de intereses, ya no llega",
			!pref.NotificacionesProductosNUevosCategoriasInteres("Comics"));

		// Eliminar una categoria que no existe
		check("Eliminar categoria no existente devuelve false",
			!alice.eliminarCategoriaInteres("Comics"));

		// Añadir categoria null o vacia
		check("Añadir categoria con nombre null devuelve false",
			!alice.añadirCategoriaInteresParaRecibirInfo(null));
		check("Añadir categoria con nombre vacio devuelve false",
			!alice.añadirCategoriaInteresParaRecibirInfo(""));
		check("Añadir categoria que no existe en tienda devuelve false",
			!alice.añadirCategoriaInteresParaRecibirInfo("CategoriaInexistente"));


		/*
		 * Comprobamos toString de PreferenciaNotificacion.
		 */
		System.out.println("\n============= toString PreferenciaNotificacion =============");

		String prefStr = pref.toString();
		check("toString contiene informacion de Descuentos",  prefStr.contains("Descuentos"));
		check("toString contiene informacion de Intercambios", prefStr.contains("intercambios"));


		/*
		 * Imprimimos el resultado del test.
		 */
		System.out.println("\n==============================================");
		System.out.println("\tRESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("==============================================");
	}
}