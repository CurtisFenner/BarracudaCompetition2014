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
				for (int layer = 0; layer < 10 - x - y; layer++) {
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
		System.out.println(input.state.tokens + "\t" + input.state.opponent_tokens);
		int team = input.state.player;
		Board board = new Board(input.state.board);
		int opponentTeam = board.opponentOf(team);
		int tokens = input.state.tokens;
		int opponentTokens = input.state.opponent_tokens;
		//
		Point bestMove = null;
		double baseScore = board.boardScore(team);
		double bestDifference = 0;
		//

		if (moveCount <= 2) {
			return new PlayerWaitMessage(input.id);
		}

		/* if (firstMove) {
		 if (board.get(new Point(3, 3, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{3, 3, 0});
		 }
		 int num = (int) (Math.random() * 2);
		 if (board.get(new Point(2 + num, 3 - num, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{2 + num, 3 - num, 0});
		 }
		 if (board.get(new Point(3 - num, 2 + num, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{3 - num, 2 + num, 0});
		 }
		 if (board.get(new Point(3 + num, 4 - num, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{3 + num, 4 - num, 0});
		 }
		 if (board.get(new Point(4 - num, 3 + num, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{4 - num, 3 + num, 0});
		 }
		 }

		 if (board.get(new Point(1, 1, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{1, 1, 0});
		 }
		 if (board.get(new Point(7, 1, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{7, 1, 0});
		 }
		 if (board.get(new Point(1, 7, 0)) == 0) {
		 return new PlayerMoveMessage(input.id, new int[]{1, 7, 0});
		 } */

		Board boardWait = board.copy();
		Playability[] waitPlayabilities = new Playability[10];

		Playability playability = new Playability(board, team);
		for (int layer = 0; layer < 10; layer++) {
			int passCount = layer - tokens + 1;
			if (passCount >= 1) {
				boardWait = simulatePlay(boardWait, opponentTeam, opponentTokens);
				opponentTokens = tokens_flag;
				Playability newWaitPlayability = new Playability(boardWait, team);
				waitPlayabilities[passCount - 1] = newWaitPlayability;
			}
			for (int x = 0; x < 10 - layer; x++) {
				for (int y = 0; y < 10 - x - layer; y++) {
					Point at = new Point(x, y, layer);
					if (playability.get(at) && board.get(at) == 0) {
						Board b = board.copy();
						b.playAt(at, team);
						Board nextBoard = b.copy();//simulatePlay(b, opponentTeam, opponentTokens);
						double score = nextBoard.boardScore(team);//b.boardScore(team);
						double difference = score - baseScore;

						double efficiency = 1.0 / (2.0 + layer);

						difference *= efficiency;

						double reduction = 1.0;
						for (int i = 0; i < passCount; i++) {
							if (!waitPlayabilities[i].get(at)) {
								reduction *= FAILURE_REDUCTION;
							}
						}

						difference *= reduction;

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
