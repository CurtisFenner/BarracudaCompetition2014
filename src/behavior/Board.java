/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

/**
 *
 * @author Blu
 */
public class Board {

	private int[][][] board;

	public Board(int[][][] board) {
		this.board = board;
	}

	public int get(Point p) {
		return board[p.x][p.y][p.layer];
	}

	public void set(Point p, int team) {
		board[p.x][p.y][p.layer] = team;
	}

	public int opponentOf(int team) {
		return 3 - team;
	}

	public boolean canPlay(Point p, int team) {
		if (get(p) != 0) {
			return false;
		}
		Tetra t = new TetraDown(p);
		boolean conflict = false;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				for (int layer = 0; layer <= p.layer; layer++) {
					Point k = new Point(x, y, layer);
					if (t.contains(k)) {
						conflict = conflict || opponentOf(team) == get(k);
					}
				}
			}
		}
		return !conflict;
	}
}
