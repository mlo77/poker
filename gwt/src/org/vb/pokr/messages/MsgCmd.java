package org.vb.pokr.messages;

public class MsgCmd extends Message {

	final static public int TYPE = 0x00030000;
	public String cmd;
	public int pid;
	@SuppressWarnings("unused")
	private MsgCmd() {}
	
	public MsgCmd(long sid, int pid, String cmd) {
		super(TYPE, sid);
		this.pid = pid;
		this.cmd = cmd;
	}

}
