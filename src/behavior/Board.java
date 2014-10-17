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
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				for (int layer = 0; layer <= p.layer; layer++) {
					Point k = new Point(x, y, layer);
					if (t.contains(k) && opponentOf(team) == get(k)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// Safe means that you're guaranteed this space at the end of the game
	public boolean safe(Point p, int team) {
		if (!canPlay(p, team)) {
			return false;
		}
		Tetra t = new TetraDown(p);
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				Point k = new Point(x, y, 0);
				if (t.contains(k) && get(k) != team) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int boardScore(int team) {
		int score = 0;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - y - x; layer++) {
					Point k = new Point(x,y,layer);
					boolean theyCantWeCan = !canPlay(k,opponentOf(team)) && canPlay(k,team);
					if (safe(k, team) || get(k) == team || theyCantWeCan) {
						score++;
					}
				}
			}
		}
		return score;
	}
}
