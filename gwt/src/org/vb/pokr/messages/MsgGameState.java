package org.vb.pokr.messages;

import org.vb.pokr.shared.GameState;

public class MsgGameState extends Message {

	final public static int TYPE = 0x00020001;
	public GameState state;
	
	@SuppressWarnings("unused")
	private MsgGameState() {}
	
	public MsgGameState(long sid, GameState state) {
		super(TYPE, sid);
		this.state = state;
	}

	protected MsgGameState(int type, long sid, GameState state) {
		super(type, sid);
		this.state = state;
	}
	
}
