package org.vb.pokr.messages;

import org.vb.pokr.shared.GameState;

public class MsgCmdRes extends MsgGameState {

	final public static int TYPE = 0x00030001;
	public int cmd;
	public int cmdStatus;
	@SuppressWarnings("unused")
	private MsgCmdRes() {super(TYPE, 0, null);}
	
	public MsgCmdRes(long sid, int cmd, int cmdStatus, GameState state) {
		super(TYPE, sid, state);
		this.cmd = cmd;
		this.cmdStatus = cmdStatus;
	}
}
