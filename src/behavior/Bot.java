/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

import com.barracuda.contest2014.MoveRequestMessage;
import com.barracuda.contest2014.PlayerMessage;
import com.barracuda.contest2014.PlayerMoveMessage;
import com.barracuda.contest2014.PlayerWaitMessage;

/**
 *
 * @author Blu
 */
public class Bot {

	public static Board simulatePlay(Board original, int team, int tokens, int count) {
		Board board = original.copy();
		for (int iterations = 0; iterations < count; iterations++) {
			Point bestMove = null;
			double baseScore = board.boardScore(team);
			double bestDifference = 0;
			//
			Playability playability = new Playability(board, team);
			for (int x = 0; x < 10; x++) {
				for (int y = 0; y < 10 - x; y++) {
					for (int layer = 0; layer < 10 - x - y; layer++) {
						Point at = new Point(x, y, layer);
						if (playability.get(at) && board.get(at) == 0) {
							Board b = board.copy();
							b.playAt(at, team);
							double score = b.boardScore(team);
							double efficiency = 1.0 / (1.0 + layer);
							double difference = score - baseScore;
							difference *= efficiency;
							if (bestMove == null || difference > bestDifference) {
								bestDifference = difference;
								bestMove = at;
							}
						}
					}
				}
			}
			if (bestMove == null) { // end of game
				return board;
			}
			if (bestMove.layer < tokens) {
				// purchase it
				tokens -= bestMove.layer + 1;
				board.playAt(bestMove, team);
			} else {
				// wait once
				tokens++;
			}
		}
		return board;
	}

	public static PlayerMessage play(MoveRequestMessage input) {
		int team = input.state.player;
		Board board = new Board(input.state.board);
		int tokens = input.state.tokens;
		//
		Point bestMove = null;
		double baseScore = board.boardScore(team);
		double bestDifference = 0;
		//
		Playability playability = new Playability(board, team);
		for (int layer = 0; layer < 10; layer++) {
			
			for (int x = 0; x < 10 - layer; x++) {
				for (int y = 0; y < 10 - x - layer; y++) {
					Point at = new Point(x, y, layer);
					if (playability.get(at) && board.get(at) == 0) {
						Board b = board.copy();
						b.playAt(at, team);
						double score = b.boardScore(team);
						double efficiency = 1.0 / (1.0 + layer);
						double difference = score - baseScore;
						difference *= efficiency;
						double failureReduction = 0.2;

						if (bestMove == null || difference > bestDifference) {
							bestDifference = difference;
							bestMove = at;
						}
					}

				}
			}
		}
		if (bestMove.layer < tokens) {
			return new PlayerMoveMessage(input.id, bestMove.toArray());
		} else {
			return new PlayerWaitMessage(input.id);
		}
		//
	}
}
