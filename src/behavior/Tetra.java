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
}
