package org.vb.pokr.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.vb.pokr.messages.*;
import org.vb.pokr.shared.SidAndName;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PokerGameWebApp implements EntryPoint {
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	static private final CommServiceAsync requestService = GWT
			.create(CommService.class);
	
	private ArrayList<SidAndName> availSessions;

	DialogBox dialogBox;
	Button closeButton;
	Label textToServerLabel;
	HTML serverResponseLabel;
	TabLayoutPanel tabPanel;
	HashMap<Long, GameSession> playedSessions;

	Integer currentTab;
	GameSession selectedGS;
	Button joinButton;
	Button createButton;
	Button killB;
	Long selectedJoinSess;
	TextBox plyrName;
	Pollr periodicPoll;
	final static private int PERIODIC = 4000;
	ListDataProvider<SidAndName> dataProvider;
	MessageReceiver mReceivr;
	
	
	public PokerGameWebApp() {
		//dialogBox = new DialogBox();
		closeButton = new Button("Close");
		textToServerLabel = new Label();
		serverResponseLabel = new HTML();
		tabPanel = new TabLayoutPanel(2.5, Unit.EM);
		playedSessions = new HashMap<Long, GameSession>();
		joinButton = new Button("Join Game");
		createButton = new Button("Create Game");
		killB = new Button("Kill session");
		plyrName = new TextBox();
		dataProvider = new ListDataProvider<SidAndName>();
		mReceivr = new MessageReceiver(this);
	}
	
	public void onModuleLoad() {
	 
		// retrieve the list of all sessions available from server
		requestService.sendMessage(new MsgQuerySess(), mReceivr); 
		
	    tabPanel.setSize("600px", "828px");
	    tabPanel.setAnimationDuration(1000);
	    tabPanel.getElement().getStyle().setMarginBottom(10.0, Unit.PX);
	    tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				currentTab = event.getSelectedItem();
				((Label)(tabPanel.getTabWidget(currentTab))).setStyleName("gwt-TabBarItem-selected");
				GameSession gs = (GameSession) tabPanel.getWidget(currentTab);
				assert gs != null : "bad game session";
				selectedGS = gs;
				controlBEnabled(true);
				}
	    });
	    periodicPoll = new Pollr(this);
		
	    HorizontalPanel hp = new HorizontalPanel();
	    RootPanel.get().add(hp);
	    
	    
	    // Create a CellTable.
	    CellTable<SidAndName> sessTable = new CellTable<SidAndName>();

	    // Create name column.
	    TextColumn<SidAndName> nameColumn = new TextColumn<SidAndName>() {
	      public String getValue(SidAndName sess) {
	        return sess.name;
	      }
	    };

	    // Add the columns.
	    sessTable.addColumn(nameColumn, "Available Sessions");
	    
	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(sessTable);

	    sessTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    
	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<SidAndName> selectionModel = new SingleSelectionModel<SidAndName>();
	    sessTable.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	    	SidAndName selected = selectionModel.getSelectedObject();
	        if (selected != null) {
	          GWT.log("You selected: " + selected);
	          selectedJoinSess = selected.sid;      
	        }
	      }
	    });
	    
		final Label errorLabel = new Label();

		VerticalPanel vp = new VerticalPanel();
		vp.add(createButton);
	    vp.add(errorLabel);	
	    vp.add(sessTable);
	    vp.add(new Label("your name :"));
	    vp.add(plyrName);
	    vp.add(joinButton);
	    
	    hp.add(vp);
	    hp.add(tabPanel);	
		
	    /*
		// Create the popup dialog box
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
	     */
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});

		joinButton.setEnabled(false);
		
		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MsgCreateGame msg = new MsgCreateGame();
				msg.plyrName = plyrName.getText();
				requestService.sendMessage(msg, mReceivr);
			}
		});
		
		joinButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (playedSessions.get(selectedJoinSess) != null) {
					GWT.log("ALREADY JOINED OR JOINING GAME");
					return;
				}
				playedSessions.put(selectedJoinSess, new GameSession());
				MsgJoinGame msg = new MsgJoinGame(selectedJoinSess);
				msg.plyrName = plyrName.getText();
				requestService.sendMessage(msg, mReceivr);
			}
		});
		
		killB.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				MsgKillGame m = new MsgKillGame(selectedJoinSess);
				GameSession gg = playedSessions.get(selectedJoinSess);
				if (gg != null) {
					tabPanel.remove(gg);
					playedSessions.remove(selectedJoinSess);
				}
				requestService.postMessage(m, new MessageReceiverVoid());	
			}
		});
		vp.add(killB);
		controlBEnabled(false);
		periodicPoll.schedule(PERIODIC);
	}
	
	private void controlBEnabled(boolean v) {
		joinButton.setEnabled(v);
		killB.setEnabled(v);
	}
	
	protected void processMsg(Message result) {	
		switch (result.getType()) {
		case MsgCmdRes.TYPE : 
			pMCRslt((MsgCmdRes) result); break;
		case MsgGameState.TYPE :
			pMGStt((MsgGameState) result);
			break;
		case MsgJoinGameRes.TYPE :
			pMGJnd((MsgJoinGameRes) result);
			break;
		case MsgGameSessions.TYPE :
			pMGSss((MsgGameSessions)result);
			break;
		case MsgPokeBack.TYPE :
			pMPB((MsgPokeBack)result);
		default :
			break;
		}
	}
	
	private void pMPB(MsgPokeBack m) {
		for (int i=m.getSidCount()-1; i>=0; i--) {
			GameSession gg = playedSessions.get(m.getSid(i));
			if (null != gg) {
				if (null == m.getGS(i)) {
					System.out.println("received MsgPokBack with null payload");
					return;
				}
				gg.setGameContent(m.getGS(i));
			}
		}
	}

	private void pMGSss(MsgGameSessions m) {
		GWT.log("pMGSss");
		availSessions = m.sidList;
		int s = availSessions.size();
		List<SidAndName> l = dataProvider.getList();
		l.clear();
		for (int i=0; i<s; i++) 
			l.add(availSessions.get(i));
		if (l.size() != 0) 
			joinButton.setEnabled(true);
		else
			joinButton.setEnabled(false);
	}

	private void pMCRslt(MsgCmdRes m) {
		pMGStt(m);
	}
	/*
	private int findGSIdx(long sid) {
		for (int i=playedSessions.size()-1; i>=0; i--) {
			GameSession gs = playedSessions.get(i);
			if (gs.sid == sid) {
				return i;
			}
		}
		return -1;
	}

	private GameSession findGS(long sid) {
		int i = playedSessions.get(sid);
		if (-1 == i) return null;
		return playedSessions.get(i);
	}
*/
	private void pMGStt(MsgGameState m) {
		GameSession gs = playedSessions.get(m.sid);
		if (null != gs )
			gs.setGameContent(m.state);
	}
	
	private void pMGJnd(MsgJoinGameRes m) {
		
		
		if (m.pid == -1) {
			if (m.sid != -1) {
				// removing pending session
				playedSessions.remove(m.sid);
			}
			//displayErrorBox("JOIN GAME FAILED");
			GWT.log("JOIN GAME FAILED");
			return;
		}
		GameSession gs = playedSessions.get(m.sid);
		if (gs == null) {
			gs = new GameSession();
			playedSessions.put(m.sid, gs);
		}
		gs.initialize(m.sid, m.pid, m.sidName);
		gs.setSenderReceiver(requestService, mReceivr);
		playedSessions.put(m.sid, gs);
		tabPanel.add(gs, new Label(Long.toString(m.sid)));
	}
	
	/*
	private void displayErrorBox(String s) {
		dialogBox.setText(s);
		serverResponseLabel.addStyleName("serverResponseLabelError");
		serverResponseLabel.setHTML(SERVER_ERROR);
		dialogBox.center();
		closeButton.setFocus(true);
	}
	*/
	class Pollr extends Timer {

		PokerGameWebApp app;
		int count;
		Pollr(PokerGameWebApp app) {
			this.app = app;
		}
		
		@Override
		public void run() {
			if (playedSessions.size() != 0) {
				int select = app.tabPanel.getSelectedIndex();
				GameSession gs = (GameSession) app.tabPanel.getWidget(select);
				MsgPoke m = new MsgPoke();
				m.allocate(1);
				if (m.addPidSid(gs.pid, gs.sid) == 0)
					requestService.sendMessage(m, app.mReceivr);
			}
			if (count == 2) {
				requestService.sendMessage(new MsgQuerySess(), mReceivr); 
				count=0;
			}
			this.schedule(PERIODIC);
			count++;
		}
	}
	
	class MessageReceiver implements AsyncCallback<Message> {

		PokerGameWebApp app;
		
		MessageReceiver(PokerGameWebApp app) {
			this.app = app;
		}
		
		@Override
		public void onFailure(Throwable caught) {
			//app.displayErrorBox("Remote Procedure Call - Failure");
			GWT.log("MessageReceiver failure");
		}

		@Override
		public void onSuccess(Message result) {
			app.processMsg(result);
		}
		
	}
	
	class MessageReceiverVoid implements AsyncCallback<Void> {
		
		@Override
		public void onFailure(Throwable caught) {
			GWT.log("MessageReceiverVoid failure");
		}

		@Override
		public void onSuccess(Void result) {
			// TODO Auto-generated method stub
			GWT.log("MessageReceiverVoid success");
		}
		
	}
}


