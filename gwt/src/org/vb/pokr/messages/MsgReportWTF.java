package org.vb.pokr.messages;

public class MsgReportWTF extends Message {
	
	final static public int TYPE = 0xFEEDBEEF;
	public String comnt;
	public String passwd;
	public int pid;
	@SuppressWarnings("unused")
	private MsgReportWTF() {}
	
	public MsgReportWTF(long sid, int pid) {
		super(TYPE, sid);
		this.pid = pid;
	}

}
