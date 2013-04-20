package org.vb.pokr.messages;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Message implements IsSerializable {

	private int type;
	public long sid;// the session id
	
	protected Message() {}
	
	protected Message(int type, long sid) {
		this.type = type;
		this.sid = sid;
	}

	public int getType() {
		return type;
	}
	
}
