package org.vb.pokr.messages;

import java.util.ArrayList;

import org.vb.pokr.shared.SidAndName;

public class MsgGameSessions extends Message {
	
	final static public int TYPE = 0x00050001;
	public int token;
	public ArrayList<SidAndName> sidList;
	public MsgGameSessions() {
		super(TYPE, -1);
	}
}
