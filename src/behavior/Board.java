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
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				Point at = new Point(x, y, 0);
				if (t.contains(at)) {
					if (get(at) == team) {
						mine++;
					} else {
						not++;
					}
				}
			}
		}
		return Math.pow(mine / (mine + not), 3);
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
		return boardValue(team);
	}

	private double boardScoreSub(int team) {
		int score = 0;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - y - x; layer++) {
					Point k = new Point(x, y, layer);
					boolean theyCantWeCan = !canPlay(k, opponentOf(team));
					theyCantWeCan = theyCantWeCan && canPlay(k, team);
					if (safe(k, team) || get(k) == team || theyCantWeCan) {
						score++;
					}
				}
			}
		}
		return score;
	}

	private double boardValue(int team) {
		int opponent = opponentOf(team);
		final double A = 0.5;
		final double S = 1.5;
		double score = 0;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - x - y; layer++) {
					Point at = new Point(x, y, layer);
					if (get(at) == team) {
						score++;
						continue;
					}
					if (get(at) == opponent) {
						score--;
						continue;
					}
					if (safe(at, team)) {
						continue;
					}
					if (safe(at, opponent)) {
						continue;
					}
					if (canPlay(at, team) && canPlay(at, opponent)) {
						continue;
					}
					if (canPlay(at, team)) {
						score += A;
						continue;
					}
					if (canPlay(at, opponent)) {
						score -= A;
						continue;
					}
				}
			}
		}
		return score + (safeness(team) - safeness(opponent)) * S;
	}
}
