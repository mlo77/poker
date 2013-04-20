package org.vb.pokr.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GameState implements IsSerializable {
	protected int type;
	public int currentPlyr;
	
	@SuppressWarnings("unused")
	private GameState() {}
	protected GameState(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type; 
	}
	/*
	@Override
	public String toString() {
		String r = new String();
		r += "BOARD : ";
		for (int i=0; i<board.length; i++) {
			r +="\t[v:" + CardUtShared.getValBinChar(board[i]) +
			   ", f:" + CardUtShared.getFamName(board[i]) + "]\n";
		}
		r += "POT : " + pot[0] + " - bet round : " + pot[1];
		r += "PLAYERS : \n";
		for (int i=0; i<players.length; i++) {
			if (players[i][0] == -1) continue;
			r += "player" + i 										+ 
				"\t[v:" + CardUtShared.getValBinChar(players[i][0]) 		+
				", f:" + CardUtShared.getFamName(players[i][0]) + "]\n" 	+
				"\t[v:" + CardUtShared.getValBinChar(players[i][1])		+
				", f:" + CardUtShared.getFamName(players[i][1]) + "]\n" 	+
				" cash : " + players[i][2] 							+
				" betting : " + players[i][3];
		}
		r += "\n LAST ACTION from player" + lastAct[0] + 
			 " doing " + lastAct[1] + " " + lastAct[2];
		return r;
	}
	*/
}
