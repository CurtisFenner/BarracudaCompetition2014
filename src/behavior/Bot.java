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
		if (Math.random() < 0.5) {
			return new PlayerWaitMessage(input.id);
		} else {
			int i = (int) (Math.random() * input.state.legal_moves.length);
			return new PlayerMoveMessage(input.id, input.state.legal_moves[i]);
		}
	}
}
