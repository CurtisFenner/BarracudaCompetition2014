/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

import com.barracuda.contest2014.ContestBot;

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

	public Board copy() {
		int[][][] c = new int[10][10][10];
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - x - y; layer++) {
					c[x][y][layer] = board[x][y][layer];
				}
			}
		}
		return new Board(c);
	}

	public void playAt(Point p, int team) {
		int x = p.x;
		int y = p.y;
		int layer = p.layer;
		for (int a = 0; a <= layer; a++) {
			for (int b = 0; b <= layer - a; b++) {
				for (int c = 0; c <= layer - a - b; c++) {
					set(new Point(x + a, y + b, c), team);
				}
			}
		}
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
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer <= p.layer && layer < 10 - x - y; layer++) {
					Point at = new Point(x, y, layer);
					if (TetraDown.contains(p, at) && opponentOf(team) == get(at)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public double safeness(Point p, int team, Playability playability) {
		double mine = 0;
		double not = 0;
		if (!playability.get(p)) {
			return 0;
		}
		Tetra t = new TetraDown(p);
		for (int x = p.x; x < 10; x++) {
			for (int y = p.y; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - x - y && layer <= p.layer; layer++) {
					Point at = new Point(x, y, layer);
					if (t.contains(at)) {
						if (get(at) == team) {
							mine++;
						} else {
							not++;
						}
					}
				}
			}
		}
		if (not == 0) {
			return 1;
		}
		double power = 3;
		return Math.pow(mine * 1.0 / (mine + not), power);
		/*
		 double size = mine + not;
		 double peak = (size - 1) / size;
		 return Math.pow(mine / (mine + not), power) / (1 - Math.pow(peak, power));
		 */
	}

	public double safeness(int team) {
		Playability playability = new Playability(this, team);
		double total = 0;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - x - y; layer++) {
					total += safeness(new Point(x, y, layer), team, playability);
				}
			}
		}
		return total;
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

	public double boardScore(int team) {
		return safeness(team) - safeness(opponentOf(team)) * 1.5;
	}
}
