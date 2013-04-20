package org.vb.pokr.messages;

public class MsgQuerySess extends Message {

	final static public int TYPE = 0x00050000;
	public MsgQuerySess() {
		super(TYPE, -1);
	}
}
