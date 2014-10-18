/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

import com.barracuda.contest2014.MoveRequestMessage;
import com.barracuda.contest2014.PlayerMessage;
import com.barracuda.contest2014.PlayerMoveMessage;
import com.barracuda.contest2014.PlayerWaitMessage;
import java.util.ArrayList;

/**
 *
 * @author Blu
 */
public class Bot {

	public static class Move {

		public final Point move;
		public final double score;

		public Move(Point move, double score) {
			this.move = move;
			this.score = score;
		}
	}

	public static Move minimax(Board b, int team, int tokens, int enemyTokens, int depth) {
		if (tokens <= 0) {
			Move enemy = minimax(b, b.opponentOf(team), enemyTokens, tokens + 1, depth);
			return new Move(null, -enemy.score);
		}
		Move bestPlay = playBest(b, team, tokens, enemyTokens);
		if (bestPlay.move == null) {
			Move enemy = minimax(b, b.opponentOf(team), enemyTokens, tokens + 1, depth);
			return new Move(null, -enemy.score);
		}
		// Below, Move is not forced.
		if (depth <= 1) {
			return bestPlay;
		}
		Move waiting = minimax(b, b.opponentOf(team), enemyTokens, tokens + 1, depth - 1);
		if (-waiting.score > bestPlay.score) {
			return new Move(null, -waiting.score);
		}
		return bestPlay;
	}

	public static Move minimax(Board b, int team, int tokens, int enemyTokens) {
		return minimax(b, team, tokens, enemyTokens, 5);
	}

	public static Move playBest(Board board, int team, int tokens, int enemyTokens) {
		double bestScore = board.boardScore(team);
		Point bestChoice = null;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				int layer = 0;
				Point at = new Point(x, y, layer);
				if (board.get(at) == 0) {
					board.set(at, team);
					double value = board.boardScore(team);
					if (value > bestScore) {
						bestChoice = at;
						bestScore = value;
					}
					board.set(at, 0);
				}

			}
		}
		return new Move(bestChoice, bestScore);
	}

	public static final int MOVES = 5;

	public static Move chooseBest(Board board,int team,int depth) {
		ArrayList<Point> valid = new ArrayList<Point>();
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				Point at = new Point(x,y,0);
				if (board.get(at) == 0) {
					valid.add(at);
				}
			}
		}
		if (valid.isEmpty()) {
			return new Move( null, board.boardScore(team) );
		}
		if (depth <= 0) {
			// try five moves and choose move with best score
			Move best = null;
			for (int i = 0; i < MOVES; i++) {
				Point choice = valid.get((int)(Math.random() * valid.size()));
				board.set(choice, team);
				double score = board.boardScore(team);
				board.set(choice, 0);
				if (best == null || best.score < score) {
					best = new Move(choice,score);
				}
			}
			return best;
		}

		Move best = null;
		for (int i = 0; i < MOVES; i++) {
			Point choice = valid.get((int)(Math.random() * valid.size()));
			board.set(choice, team);
			Move bestOpponentMove = chooseBest( board, board.opponentOf(team), depth-1 );
			board.set(choice, 0);
			if (best == null || best.score < -bestOpponentMove.score) {
				best = new Move( choice, -bestOpponentMove.score );
			}
		}
		return best;
	}

	public static Point chooseBest(Board board,int team) {
		return chooseBest(board, team, 5).move;
	}

	public static int waiting = 0;
	public static int playing = 0;

	public static PlayerMessage play(MoveRequestMessage input) {
		// always at least one legal move in array
		// choose best
		// always going to choose bottom row for now
		int team = input.state.player;
		Board currentBoard = new Board(input.state.board);

		Point choicePoint = playBest(currentBoard, team, input.state.tokens, input.state.opponent_tokens).move;

		if (Math.random() < 0.00 || choicePoint == null) {
			waiting++;
			return new PlayerWaitMessage(input.id);
		} else {
			playing++;
			return new PlayerMoveMessage(input.id, choicePoint.toArray());
		}
	}
}
