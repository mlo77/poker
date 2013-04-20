package org.vb.pokr.messages;

public class MsgKillGame extends Message {

	public static final int TYPE = 0x10000003;
	
	@SuppressWarnings("unused")
	private MsgKillGame() {
		super(TYPE, -1);
	}
	
	public MsgKillGame(long sid) {
		super(TYPE, sid);
	}
}
