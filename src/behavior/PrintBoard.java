/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behavior;

/**
 *
 * @author Blu
 */
public class PrintBoard {

	public static void print(int[][][] data,int team) {
		for (int layer = 0; layer < 10; layer++) {
			String s = "";
			for (int x = 0; x < 10 - layer; x++) {
				for (int y = 0; y < 10 - x - layer; y++) {
					String c = ".";
					if (data[x][y][layer] == team) {
						c = "#";
					}
					if (data[x][y][layer] == 3 - team) {
						c = "O";
					}
					s += c;
				}
				s += "\n";
			}
			System.out.println(s);
		}
	}
}
