package org.vb.pokr.client;

import org.vb.pokr.shared.GameState;
import org.vb.pokr.shared.InfoLog;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class InfoPanel extends FlowPanel {

	public static final int NEWPLYR = 1; // general info -> GameState
	public static final int RCVCARD = 2; // RoundState
	public static final int PLYRACT = 3; // RoundState
	public static final int GSTATE = 4;  // RoundState
	public static final int PLYRGAIN = 5; // PotWin
	public static final int PLYRTURN = 6; // RoundState
	public static final int PLYRSHOW = 7; // PotWin
	/*
	public final static int CMD_ADDP 		= 1;
	public final static int CMD_REM 		= 2;
	public final static int CMD_START 		= 3;
	public final static int CMD_CHECK 		= 4;
	public final static int CMD_RAISE 		= 5;
	public final static int CMD_CALL 		= 6;
	public final static int CMD_FOLD 		= 7;
	public final static int CMD_ALLIN 		= 8;
	public final static int CMD_FINISH 		= 9;
	*/
	public void showInfo(GameState g) {
		if (!(g instanceof InfoLog)) return; 
		InfoLog il = (InfoLog) g;
		clear();
		for (int i=il.info.length-1; i>=0; i--)
			add(new Label(convert(il.info[i])));
	}

	
	private String convert(int[] line) {
		switch(line[1]) {
		case NEWPLYR :
			return "player added "+line[0]; 
		case PLYRTURN :
			return "player turn "+line[0];
		case PLYRACT :
		case GSTATE :
		case RCVCARD :
		case PLYRGAIN :
		case PLYRSHOW :
			
		}
		return "some log" + line[0] +" "+ line[1]+" "+line[2];
	}
	
}
