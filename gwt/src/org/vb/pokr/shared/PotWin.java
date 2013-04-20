package org.vb.pokr.shared;

public class PotWin extends GameState {
	final static public int TYPE = 0x01000002;
	public String[] plyrsName = new String[10];
	public int[] gains = new int[10];
	public int[] score = new int[10];
	public int[] board; // 5 int max
	public int[][] players; // pid, 2 cards
	
	public PotWin() {
		super(TYPE);
	}
}
