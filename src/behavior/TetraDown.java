/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

/**
 *
 * @author Blu
 */
public class TetraDown extends Tetra {

	@Override
	public boolean contains(Point p) {
		return contains(vertex, p);
	}

	public TetraDown(Point vertex) {
		super(vertex);
	}

	public static boolean contains(Point vertex, Point p) {
		if (p.x < vertex.x || p.y < vertex.y || p.layer > vertex.layer) {
			return false;
		}
		int layerDifferent = vertex.layer - p.layer;
		int diff = p.x - vertex.x + p.y - vertex.y;
		return diff <= layerDifferent;
	}
}
