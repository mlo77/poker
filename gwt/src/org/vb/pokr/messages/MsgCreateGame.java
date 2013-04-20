package org.vb.pokr.messages;

public class MsgCreateGame extends Message {

	public static final int TYPE = 0x10000002;
	public String sessName;
	public String plyrName;
	public MsgCreateGame() {
		super(TYPE, -1);
	}
}
