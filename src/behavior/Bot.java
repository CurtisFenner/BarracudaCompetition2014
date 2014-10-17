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

	public static PlayerMessage play(MoveRequestMessage input) {
		if (input.state.opponent_id == 33 || input.state.opponent_id == 18)
			System.out.println("his:" + input.state.opponent_tokens + "\tmy:" + input.state.tokens);
		// always at least one legal move in array
		// choose best
		// always going to choose bottom row for now
		int bestChoice = 0;
		int bestScore = 0;
		int team = input.state.player;;
		Board currentBoard = new Board(input.state.board);
		for (int i = 0; i < input.state.legal_moves.length; i++) {
			Point at = new Point(input.state.legal_moves[i]);
			currentBoard.set(at, team);
			int value = currentBoard.boardScore(team);
			if (value > bestScore) {
				bestChoice = i;
				bestScore = value;
			}
		}

		int[] choice = input.state.legal_moves[bestChoice];


		if (Math.random() < 0.2) {
			return new PlayerWaitMessage(input.id);
		} else {
			return new PlayerMoveMessage(input.id, choice);
		}
	}
}
