/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

/**
 *
 * @author Blu
 */
public class Point {
	public final int x;
	public final int y;
	public final int layer;
	
	public Point(int[] arr) {
		this.x = arr[0];
		this.y = arr[1];
		this.layer = arr[2];
	}
	
	public Point(int x,int y,int layer) {
		this.x = x;
		this.y = y;
		this.layer = layer;
	}
	
	public int[] toArray() {
		return new int[] {x,y,layer};
	}
}
