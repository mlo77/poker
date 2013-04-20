package org.vb.pokr.shared;

public class InfoLog extends GameState {
	final static public int TYPE = 0x01000003;
	public int[][] info; // pid, cmd code, arg
	public InfoLog() {
		super(TYPE);
	}
}
