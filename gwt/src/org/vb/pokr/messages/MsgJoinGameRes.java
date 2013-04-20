package org.vb.pokr.messages;

public class MsgJoinGameRes extends Message {
	
	public int pid;
	public String sidName;
	//public HTML gstate; 
	final public static int TYPE = 0x10000001;
	
	@SuppressWarnings("unused")
	private MsgJoinGameRes() {}
	
	public MsgJoinGameRes(int pid, long sid, String name) {
		super(TYPE, sid);
		this.pid = pid;
		this.sidName = name;
	}

}
