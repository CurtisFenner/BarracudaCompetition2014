/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

import com.barracuda.contest2014.ContestBot;
import com.barracuda.contest2014.MoveRequestMessage;
import com.barracuda.contest2014.PlayerMessage;
import com.barracuda.contest2014.PlayerMoveMessage;
import com.barracuda.contest2014.PlayerWaitMessage;

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
		Move waiting = minimax(b, b.opponentOf(team), enemyTokens, tokens + 1, depth - 1 );
		if (-waiting.score > bestPlay.score) {
			return new Move(null,-waiting.score);
		}
		return bestPlay;
	}

	public static Move minimax(Board b, int team, int tokens, int enemyTokens) {
		return minimax(b, team, tokens, enemyTokens, 5);
	}

	public static Move playBest(Board board, int team, int tokens, int enemyTokens) {
		double baseScore = board.boardScore(team);
		double bestScore = 0;
		Point bestChoice = null;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - x - y && layer < tokens; layer++) {
					Point at = new Point(x, y, layer);
					if (board.canPlay(at, team)) {
						board.set(at, team);
						int tokenCost = at.layer;
						double efficiency = (tokens + 1 - tokenCost) / (double) (tokens + 1);
						double value = (board.boardScore(team) - baseScore) * efficiency;
						if (value > bestScore) {
							bestChoice = at;
							bestScore = value;
						}
						board.set(at, 0);
					}
				}
			}
		}
		return new Move(bestChoice, bestScore);
	}

	public static PlayerMessage play(MoveRequestMessage input) {
		// always at least one legal move in array
		// choose best
		// always going to choose bottom row for now
		int team = input.state.player;
		Board currentBoard = new Board(input.state.board);

		Point choicePoint = minimax(currentBoard, team, input.state.tokens, input.state.opponent_tokens).move;

		if (Math.random() < 0.05 || choicePoint == null) {
			return new PlayerWaitMessage(input.id);
		} else {
			return new PlayerMoveMessage(input.id, choicePoint.toArray());
		}
	}
}
