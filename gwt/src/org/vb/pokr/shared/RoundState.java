package org.vb.pokr.shared;

public class RoundState extends GameState {
	public int[] board; // 5 int max
	public int[] pot; // pot bet, bet round
	public int[][] players; // for each 2 cards, cash, betting, pid
	public String[] plyrsName;
	public int[][] info; // pid, cmd code, amount involved
	public static final int TYPE = 0x01000001; 
	public RoundState() {
		super(TYPE);
	}
}
