package org.vb.pokr.messages;


public class MsgQueryGameState extends Message {

	final public static int TYPE = 0x00020000;
	public int pid;
	public long sid;
	
	@SuppressWarnings("unused")
	private MsgQueryGameState() {}
	
	public MsgQueryGameState(int pid, long sid) {
		super(TYPE, sid);
		this.pid = pid;
	}
}
