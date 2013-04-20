package org.vb.pokr.client;

import org.vb.pokr.messages.*;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("comms")
public interface CommService extends RemoteService {
	
	Message sendMessage(Message msg);
	void postMessage(Message msg);
}
