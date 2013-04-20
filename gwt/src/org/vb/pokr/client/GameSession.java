package org.vb.pokr.client;

import org.vb.pokr.client.PokerGameWebApp.MessageReceiver;
import org.vb.pokr.client.PokerGameWebApp.MessageReceiverVoid;
import org.vb.pokr.messages.MsgCmd;
import org.vb.pokr.messages.MsgQueryGameState;
import org.vb.pokr.messages.MsgReportWTF;
import org.vb.pokr.shared.GameState;
import org.vb.pokr.shared.PotWin;
import org.vb.pokr.shared.RoundState;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GameSession extends VerticalPanel {
	public long sid;
	public int pid;
	CommServiceAsync requestService;
	MessageReceiver mReceivr;
	public int pidMarkr;
	Button checkB;
	Button callB;
	Button raiseB;
	Button foldB;
	Button startB;
	Button reportWTF;
	TextBox raiseTB;
	InfoPanel ip;
	BoardPanel bp;
	PlayersPanel pb;
	
	public GameSession() {
	}

	public void initialize(long sid, int pid, String s) {
		this.pid = pid;
		this.sid = sid;
		//this.setSize("600px", "800px");
		ip = new InfoPanel();
		checkB = new Button("check");
		callB = new Button("call");
		raiseB = new Button("raise");
		foldB = new Button("fold");
		startB = new Button("start");
		reportWTF = new Button("Report WTF - don't abuse!");
		raiseTB = new TextBox();
		bp = new BoardPanel();
		pb = new PlayersPanel();
	}
	
	public void setSenderReceiver(CommServiceAsync requestService, MessageReceiver mReceivr) {
		this.requestService = requestService;
		this.mReceivr = mReceivr;
	}
	
	public void update() {
		if (requestService == null) {
			GWT.log("sender receiver not set");
			return;
		}
		requestService.sendMessage(new MsgQueryGameState(pid, sid), mReceivr);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (requestService == null) {
			GWT.log("sender receiver not set");
			return;
		}
		add(bp);
		add(pb);
		add(callB);
		add(checkB);
		add(raiseB);
		add(raiseTB);
		add(foldB);
		add(startB);
		add(reportWTF);
		add(ip);
		callB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestService.sendMessage(new MsgCmd(sid, pid, "ca"), mReceivr);
			}
		});
		checkB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestService.sendMessage(new MsgCmd(sid, pid, "ch"), mReceivr);
			}
		});
		foldB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestService.sendMessage(new MsgCmd(sid, pid, "fo"), mReceivr);
			}
		});
		raiseB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String r = raiseTB.getText();
				if (r.isEmpty()) return;
				requestService.sendMessage(new MsgCmd(sid, pid, "ra " + raiseTB.getText()), mReceivr);
			}
		});
		startB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestService.sendMessage(new MsgCmd(sid, pid, "st"), mReceivr);
				//startB.setEnabled(false);
			}
		});
		reportWTF.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestService.sendMessage(new MsgReportWTF(sid, pid), mReceivr);
				//requestService.postMessage(m, new MessageReceiverVoid());	
				startB.setEnabled(false);
			}
		});		
		switchPlayB(false);
	}

	private void switchPlayB(boolean s) {
		callB.setEnabled(s);
		checkB.setEnabled(s);
		raiseB.setEnabled(s);
		raiseTB.setEnabled(s);
		foldB.setEnabled(s);
		//startB.setEnabled(!s);
	}
	
	public void setGameContent(GameState state) {
		bp.setCards(state);
		pb.setCards(state, pid);
		ip.showInfo(state);
		switch (state.getType()) {
		case RoundState.TYPE : {
			startB.setEnabled(false);
			if (((RoundState)state).currentPlyr == pid)
				switchPlayB(true);
			else
				switchPlayB(false);
		} break;
		case PotWin.TYPE : {
			switchPlayB(false);
			startB.setEnabled(true);
		} break;
		default : break;
		}
	}
}
