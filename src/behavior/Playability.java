/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

/**
 *
 * @author Blu
 */
public class Playability {

	public final boolean[][][] grid;

	public Playability(Board b, int team) {
		grid = new boolean[10][10][10];
		int opponent = b.opponentOf(team);
		for (int layer = 0; layer < 10; layer++) {
			for (int x = 0; x < 10 - layer; x++) {
				for (int y = 0; y < 10 - layer - x; y++) {
					if (b.get(new Point(x, y, layer)) == opponent) {
						grid[x][y][layer] = false;
					} else {
						if (layer > 0) {
							grid[x][y][layer] = grid[x + 1][y][layer - 1] && grid[x][y + 1][layer - 1] && grid[x][y][layer - 1];
						} else {
							grid[x][y][layer] = true;
						}
					}
				}
			}
		}
	}
	
	public boolean get(Point at) {
		return grid[at.x][at.y][at.layer];
	}
}
