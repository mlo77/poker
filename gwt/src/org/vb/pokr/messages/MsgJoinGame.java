package org.vb.pokr.messages;


public class MsgJoinGame extends Message {
	
	final public static int TYPE = 0x10000000; 
	public String plyrName;
	@SuppressWarnings("unused")
	private MsgJoinGame() {}
	
	public MsgJoinGame(long sid) {
		super(TYPE, sid);
	}
}
