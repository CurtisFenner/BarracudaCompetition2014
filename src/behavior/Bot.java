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


	public static Point playBest(Board board, int team, int tokens, int enemyTokens) {
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
		return bestChoice;
	}

	public static PlayerMessage play(MoveRequestMessage input) {
		// always at least one legal move in array
		// choose best
		// always going to choose bottom row for now
		int team = input.state.player;
		Board currentBoard = new Board(input.state.board);

		Point choicePoint = playBest(currentBoard, team, input.state.tokens, input.state.opponent_tokens);

		if (Math.random() < 0.2 || choicePoint == null) {
			return new PlayerWaitMessage(input.id);
		} else {
			return new PlayerMoveMessage(input.id, choicePoint.toArray());
		}
	}
}
