package org.vb.pokr.client;

import org.vb.pokr.shared.GameState;
import org.vb.pokr.shared.RoundState;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class BoardPanel extends HorizontalPanel {

	public BoardPanel() {
		add(new Label("BOARD : "));
	}
	
	public void setCards(GameState g) {
		if (g.getType() == RoundState.TYPE) {
			reset();
			RoundState rs = (RoundState) g;
			for (int i=0; i<rs.board.length; i++) {
				add(PokrResUt.getImg(rs.board[i]));
			}
		}
	}
	
	public void reset() {
		clear();
		add(new Label("BOARD : "));
	}

}
