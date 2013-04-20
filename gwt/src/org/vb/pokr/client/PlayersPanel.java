package org.vb.pokr.client;

import org.vb.pokr.shared.GameState;
import org.vb.pokr.shared.InfoLog;
import org.vb.pokr.shared.PotWin;
import org.vb.pokr.shared.RoundState;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PlayersPanel extends FlowPanel {
	
	Label lbl;
	
	public PlayersPanel() {
		lbl = new Label("PLAYERS : ");
	}
	
	public void setCards(GameState g, int pid) {
		switch(g.getType()) {
		case RoundState.TYPE :
			processRS((RoundState)g, pid); break;
		case PotWin.TYPE :
			processPW((PotWin)g, pid); break;
		case InfoLog.TYPE :
			processIL((InfoLog)g, pid); break;
		default :
			add(new Label(g.toString()));
			break;
		}
	}
	
	private void processIL(InfoLog g, int pid) {
		//add(new Label("Received InfoLog"));
	}

	private void processPW(PotWin pw, int pid) {
		reset();
		int winr = 0;
		int maxScore = 0;
		for (int i=0; i<pw.players.length; i++) {
			VerticalPanel vp = new VerticalPanel();
			HorizontalPanel hp = new HorizontalPanel();
			//hp.addStyleName("GS-plyrPanel");
			vp.add(new Label(pw.plyrsName[i]));
			vp.add(new Label(ScoreUt.description(pw.score[i])));
			if (pw.score[i] > maxScore) {
				maxScore = pw.score[i];
				winr = i; 
			}
			if (pw.gains[i] != 0)
				vp.add(new Label("gains : " + pw.gains[i]));
			hp.add(PokrResUt.getImg(pw.players[i][0]));
			hp.add(PokrResUt.getImg(pw.players[i][1]));
			vp.add(hp);
			add(vp);
		}
		add(new Label(pw.plyrsName[winr] + " wins the hand"));
	}

	private void processRS(RoundState rs, int pid) {
		reset();
		for (int i=0; i<rs.players.length; i++) {
			VerticalPanel vp = new VerticalPanel();
			HorizontalPanel hp = new HorizontalPanel();
			vp.add(new Label(rs.plyrsName[i]));
			vp.add(new Label("cash : " +  rs.players[i][2]));
			vp.add(new Label("betting : " +  rs.players[i][3]));
			hp.add(PokrResUt.getImg(rs.players[i][0]));
			hp.add(PokrResUt.getImg(rs.players[i][1]));
			vp.add(hp);
			add(vp);
			if (rs.currentPlyr == rs.players[i][4])
				if (rs.currentPlyr == pid)
					vp.setStyleName("GS-yourTurn");
				else
					vp.setStyleName("GS-plyrTurn");
		}
	}
	
	public void reset() {
		clear();
		add(lbl);
	}
}
