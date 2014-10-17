/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

/**
 *
 * @author Blu
 */
public class TetraUp extends Tetra {

	@Override
	public boolean contains(Point p) {
		return contains(vertex, p);
	}
	
	public static boolean contains(Point vertex,Point p) {
		return TetraDown.contains(p,vertex);
	}

	public TetraUp(Point vertex) {
		super(vertex);
	}
}
