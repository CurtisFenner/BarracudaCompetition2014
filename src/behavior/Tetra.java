/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

/**
 *
 * @author Blu
 */
public abstract class Tetra {

	public abstract boolean contains(Point p);
	public final Point vertex;

	public Tetra(Point vertex) {
		this.vertex = vertex;
	}
	public static final Tetra boardTetra = new TetraDown(new Point(0, 0, 10));
}
