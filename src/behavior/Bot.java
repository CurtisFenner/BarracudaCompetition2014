/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

import com.barracuda.contest2014.MoveRequestMessage;
import com.barracuda.contest2014.PlayerMessage;
import com.barracuda.contest2014.PlayerMoveMessage;
import com.barracuda.contest2014.PlayerWaitMessage;

import com.barracuda.contest2014.ContestBot;

/**
 *
 * @author Blu
 */
public class Bot {

	public static int tokens_flag = 0;

	public static Board simulatePlay(Board original, int team, int tokens) {
		Board board = original.copy();
		Point bestMove = null;
		double baseScore = board.boardScore(team);
		double bestDifference = 0;
		//
		Playability playability = new Playability(board, team);
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10 - x; y++) {
				for (int layer = 0; layer < 10 - x - y && layer < 7; layer++) {
					Point at = new Point(x, y, layer);
					if (playability.get(at) && board.get(at) == 0) {
						Board b = board.copy();
						b.playAt(at, team);
						double score = b.boardScore(team);
						double difference = score - baseScore;
						double efficiency = Math.min(1, tokens / (layer + 2.0));
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
			tokens_flag = tokens;
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
		tokens_flag = tokens;
		return board;
	}

	public static Board simulatePlay(Board original, int team, int tokens, int count) {
		Board board = original.copy();
		for (int iterations = 0; iterations < count; iterations++) {
			board = simulatePlay(board, team, tokens);
			tokens = tokens_flag;
		}
		tokens_flag = tokens;
		return board;
	}
	public static final double FAILURE_REDUCTION = 0.85;

	public static PlayerMessage play(MoveRequestMessage input, int moveCount) {
		if (ContestBot.myPlayerId == 1) {
			System.out.println("Scores: " + input.state.score.player1 + "\t" + input.state.score.player2);
		} else {
			System.out.println("Scores: " + input.state.score.player2 + "\t" + input.state.score.player1);
		}
		System.out.println(input.state.tokens + "\t" + input.state.opponent_tokens);
		int team = input.state.player;
		Board board = new Board(input.state.board);
		int opponentTeam = board.opponentOf(team);
		int tokens = input.state.tokens;
		int opponentTokens = input.state.opponent_tokens;

		int opponentScore = 0;
		int myScore = 0;
		if (input.state.player == 1) {
			opponentScore = input.state.score.player2;
			myScore = input.state.score.player1;
		} else {
			opponentScore = input.state.score.player1;
			myScore = input.state.score.player2;
		}
		if (opponentScore <= 2) {
			ContestBot.thisWait++;
		}

		if (tokens == 1 && Math.random() < 0.5) {
			Playability play = new Playability(board, team);
			boolean canPlayHigher = false;
			for (int x = 0; x < 10 - 1; x++) {
				for (int y = 0; y < 10 - x - 1; y++) {
					if (play.get(new Point(x, y, 1))) {
						canPlayHigher = true;
					}
				}
			}
			if (canPlayHigher) {
				return new PlayerWaitMessage(input.id);
			}
		}

		if (moveCount <= 2) {
			return new PlayerWaitMessage(input.id);
		}
		//Board boardWait = board.copy();
		//Playability[] waitPlayabilities = new Playability[10];
		//
		Point bestMove = null;
		double baseScore = board.boardScore(team);
		double bestDifference = 0;
		//

		int layerLimit = 6;


		if (myScore <= 2 && opponentScore <= 2) {
			System.out.println(moveCount + " >? " + ContestBot.lastWait + " / " + tokens);
			if (moveCount > ContestBot.lastWait || moveCount > 5) {
				layerLimit = tokens;
			} else {
				return new PlayerWaitMessage(input.id);
			}
		}

		//
		Playability playability = new Playability(board, team);
		for (int layer = 0; layer < layerLimit; layer++) {
			int passCount = layer - tokens + 1;
			if (passCount >= 1) {
				//boardWait = simulatePlay(boardWait, opponentTeam, opponentTokens);
				//opponentTokens = tokens_flag;
				//Playability newWaitPlayability = new Playability(boardWait, team);
				//waitPlayabilities[passCount - 1] = newWaitPlayability;
			}
			for (int x = 0; x < 10 - layer; x++) {
				for (int y = 0; y < 10 - x - layer; y++) {
					Point at = new Point(x, y, layer);
					if (playability.get(at) && board.get(at) == 0) {
						Board b = board.copy();
						b.playAt(at, team);
						double score = b.boardScore(team);
						double difference = score - baseScore;

						double efficiency = 1.0 / (2.0 + layer);

						difference *= Math.pow(efficiency, 1.5);

						difference *= Math.pow(FAILURE_REDUCTION, Math.max(0, passCount));

						if (bestMove == null || difference > bestDifference) {
							bestDifference = difference;
							bestMove = at;
						}
					}

				}
			}
		}
		if (bestMove == null) {
			System.out.println("BEST MOVE IS NULL");
			return new PlayerWaitMessage(input.id);
		}
		if (board.safe(bestMove, team)) {
			for (int i = 0; i < 10; i++) {
				System.out.println("SAFE POINT PICKED @ " + bestMove);
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
