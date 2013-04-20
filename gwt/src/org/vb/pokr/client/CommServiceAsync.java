package org.vb.pokr.client;

import org.vb.pokr.messages.*;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommServiceAsync {
	void sendMessage(Message action, AsyncCallback<Message> callback)
		throws IllegalArgumentException;
	void postMessage(Message action, AsyncCallback<Void> callback)
		throws IllegalArgumentException;
}
