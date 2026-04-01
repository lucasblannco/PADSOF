package Excepcion;

//Cuando se intenta hace runa oferta con un producto bloqueado
public class ProductoBloqueadoException {
	private String idProducto;

	public ProductoBloqueadoException(String idProducto) {
		this.idProducto = idProducto;
	}

	public String toString() {
		return "El producto " + idProducto + " esta bloqueado porque esta ofrecido en otra oferta";
	}
}
