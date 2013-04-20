package org.vb.pokr.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SidAndName implements IsSerializable {
	public long sid;
	public String name;
	@SuppressWarnings("unused")
	private SidAndName() {}
	public SidAndName(long sid, String name) {
		this.sid = sid;
		this.name = name;
	}
}
