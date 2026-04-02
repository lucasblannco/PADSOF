package pruebas;

import productos.Categoria;
import productos.Comic;
import productos.EstadoProducto;
import productos.JuegoMesa;
import productos.Pack;
import productos.Producto2Mano;
import productos.Reseña;
import usuarios.Cliente;
import usuarios.Empleado;

public class PruebaProductos {

	static int correctos = 0;
	static int fallos = 0;

	static void check(String nombre, boolean condicion) {
		if (condicion) {
			System.out.println("	CORRECTO -> " + nombre);
			correctos++;
		} else {
			System.out.println("	FALLO -> " + nombre);
			fallos++;
		}
	}

	public static void main(String[] args) {

		/*
		 * Miramos que funciona la asignacion de ids para diferentes productos de clases
		 * producto venta y producto segunda mano.
		 */
		System.out.println("\n PRUEBA IDS UNICOS");
		Cliente usuario = new Cliente("lucasblannco", "Lucas@1234", "00000000L");
		Comic c1 = new Comic("ObjetpVenta1", "descripcion1", "imagen1", 20, 10, 200, "Alfaguara", 2020);
		Comic c2 = new Comic("ObjetoVenta2", "descripcion2", "imagen2", 10, 5, 150, "Santillana", 2016);
		Comic c3 = new Comic("ObjetoVenta3", "descripcion3", "imagen3", 15, 8, 180, "Santillana", 2018);
		Producto2Mano p2mano = new Producto2Mano(usuario, "Objeto2Mano", "Objeto prueba segunda mano", "imagen 2 mano");
		JuegoMesa j1 = new JuegoMesa("ObjetoVenta3", "descripcion3", "imagen3", 40, 3, 2, 4, 3, 99, "Estrategia");

		check("c1 tiene id PV", c1.getId().startsWith("PV"));
		check("p2m tiene id P2M", p2mano.getId().startsWith("P2M"));
		check("j1 tiene id PV", j1.getId().startsWith("PV"));
		check("IDs distintos entre c1 y c2", !c1.getId().equals(c2.getId()));

		System.out.println("\nCATEGORIAS");
		System.out.println("\n");
		Categoria catComics = new Categoria("Comics", "desc");
		Categoria catJuegos = new Categoria("Juegos", "desc");

		check("Añadir c1 a categoría Comic", catComics.addProducto(c1));
		check("Añadir otra vez falla", !catComics.addProducto(c1));
		check("c1 guarda categoría Comic en él", c1.getCategorias().contains(catComics));

		check("Añadir c1 a Juegos también", catJuegos.addProducto(c1));
		check("c1 tiene 2 categorias", c1.getCategorias().size() == 2);

		check("Eliminar c1 de Comics", catComics.deleteProducto(c1));
		check("c1 ya no tiene que tener Comics", !c1.getCategorias().contains(catComics));
		check("Eliminar si no existe falla", !catComics.deleteProducto(c1));
		check("Null en addProducto falla", !catComics.addProducto(null));

		/*
		 * En este apartado gestionamos que los stock de los productos individuales y
		 * packs funcione correctamente. Además miramos que cuando se añade un producto
		 * a un pack este actualiza su stock en base al numero de packs que hay
		 * (entonces se restan los productos que se necesitan). Por último comprobamos
		 * que funcionan correctamente el calcular los precios en los packs, teniendo en
		 * cuenta que pueden haber packs dentro de packs.
		 */
		System.out.println("\n GESTION DE STOCKS Y PRECIOS ");

		Pack pk = new Pack("Pack1", "descripcionPack1", "imagenPack1", 0, 3);
		Pack pack = new Pack("Pack2", "descripcionPack2", "imagenPack2", 0, 2);

		c1.setStockDisponible(-5);
		check("Stock negativo de c1 ignorado", c1.getStockDisponible() == 10);

		pk.addProducto_conunaUnidad(c1);
		pk.addProducto_conunaUnidad(c3);
		check("Añadir null al pack falla", !pk.addProducto_conunaUnidad(null));
		check("Añadir duplicado falla", !pk.addProducto_conunaUnidad(c1));

		check("El pack pequeño contiene c1", pk.contieneProducto(c1));

		check("Stock de c1 baja de 10 a 7", c1.getStockDisponible() == 7);

		check("Precio c1 es 20€", c1.getPrecioOficial() == 20);
		check("Suma productos del pack pequeño = 35", pk.calcularSumaProductos() == 35);
		check("Precio final del pack pequeño = 0", pk.calcularPrecioFinal() == 0);

		check("No permite subir precio del pack pequeño a 40", !pk.setPrecioOficial(40));
		check("Permite cambiar precio del pack pequeño a 25", pk.setPrecioOficial(25));
		check("Precio final del pack pequeño = 25", pk.calcularPrecioFinal() == 25);

		pack.addProducto_conunaUnidad(c2);
		pack.addProducto_conunaUnidad(j1);
		check("Suma productos del pack interior = 50", pack.calcularSumaProductos() == 50);
		check("Permite cambiar precio del pack interior a 45", pack.setPrecioOficial(45));
		check("Precio final del pack interior = 45", pack.calcularPrecioFinal() == 45);
		check("Stock de j1 baja de 3 a 1", j1.getStockDisponible() == 1);

		pk.addProducto_conunaUnidad(pack);
		check("Precio final del pack pequeño sigue siendo 25", pk.calcularPrecioFinal() == 25);

		/*
		 * check("Eliminar c3 del pack pequeño falla porque lo dejaría inválido",
		 * !pk.eliminarProducto(c3));
		 * check("Eliminar pack interior del pack pequeño falla porque lo dejaría inválido"
		 * , !pk.eliminarProducto(pack));
		 */

		/*
		 * Comprobamos que las reseñas se añaden correctamente a los productos
		 * pertinentes, de forma que se calcula además su puntuación media bien. Además
		 * prueba los casos límite de la puntuación. Además comprobamos que un mismo
		 * cliente no puede reseñar el mismo objeto más de una vez.
		 */
		System.out.println("\n RESEÑAS ");
		Cliente cli = new Cliente("danisaa", "Dani@1234", "11111111D");
		Cliente cli2 = new Cliente("antoal", "Anto@1234", "22222222A");
		Reseña r1 = new Reseña(cli, c2, 8.5, "Muy bueno");
		Reseña r2 = new Reseña(cli2, c2, 6.0, "Regular");

		check("c2 tiene 2 reseñas", c2.getReseñas().size() == 2);
		check("Media puntuación correcta", c2.getMediaPuntuacion() == 7.25);
		check("Puntuacion >10 se limita", new Reseña(cli, c1, 999, "").getPuntuacion() == 10.0);
		check("Puntuacion <0 se limita", new Reseña(cli2, c1, -1, "").getPuntuacion() == 0.0);
		check("Reseña null falla", !c2.addReseña(null));
		check("Mismo cliente no puede reseñar dos veces", !c2.addReseña(new Reseña(cli, null, 5.0, "otro")));

		/**/
		System.out.println("\n SEGUNDA MANO ");

		/* Miramos si los estados de segunda mano funciona el paso de uno a otro o no */
		System.out.println("\n============= SEGUNDA MANO =============");

		Empleado emp = new Empleado("ana", "Ana@1234");
		Producto2Mano p2m = new Producto2Mano("ProductoSegunda1", "desc", "", null, cli, true, false);

		check("Producto bloqueado al principio", p2m.isBloqueado());
		check("Producto no visible al principio", !p2m.isVisible());
		boolean valorado = p2m.valorar(15.0, EstadoProducto.MUY_BUENO, emp);
		check("Valoración correcta", valorado);
		check("Producto ya es visible tras valoracion correcta", p2m.isVisible());

		Producto2Mano p2mRechazado = new Producto2Mano("ProductoSegunda2", "desc", "", null, cli, true, false);
		p2mRechazado.valorar(0.0, EstadoProducto.NO_ACEPTADO, emp);
		check("Producto no aceptado lo mantiene no visible", !p2mRechazado.isVisible());

		check("Valorar con null falla", !p2m.valorar(10.0, null, emp));
		check("Valorar con precio 0 falla", !p2m.valorar(-2.5, EstadoProducto.PERFECTO, emp));

		/*
		 * Imprimimos el resultado del test en el que mostramos cuantos se han pasado y
		 * cuantos no al final.
		 */
		System.out.println("\n");
		System.out.println("	RESULTADO: " + correctos + " CORRECTOS  |  " + fallos + " FALLOS");
		System.out.println("");
	}
}