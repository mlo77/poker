package org.vb.pokr.messages;

import org.vb.pokr.shared.GameState;

public class MsgPokeBack extends Message {

	public static final int TYPE = 0x00040001;
	private long[] sidFocused = null; // 5 max
	private GameState[] gStates = null;
	private int count = 0;
	
	public MsgPokeBack() {
		super(TYPE, -1);
	}
	
	public void allocate(int s) {
		if (s <= 0) return;
		if (s > MsgPoke.MAX_ALLOWED_POKED_SID) s = MsgPoke.MAX_ALLOWED_POKED_SID;
		sidFocused = new long[s];
		gStates = new GameState[s];
	}
	
	public int addSidState(long s, GameState g) {
		if (null == sidFocused || count >= MsgPoke.MAX_ALLOWED_POKED_SID) return -1;
		sidFocused[count] = s;
		gStates[count++] = g;
		return 0;
	}
	
	public long getSid(int i) {
		if (null == sidFocused || i >= count) return -1;
		return sidFocused[i];
	}
	
	public GameState getGS(int i) {
		if (null == sidFocused || i >= count) return null;
		return gStates[i];
	}
	
	public int getSidCount() {
		return count;
	}
}
