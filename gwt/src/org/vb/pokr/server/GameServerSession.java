package org.vb.pokr.server;

import org.vb.pokr.messages.MsgCmd;
import org.vb.pokr.messages.MsgCmdRes;
import org.vb.pokr.shared.EndGame;
import org.vb.pokr.shared.GameState;
import org.vb.pokr.shared.InfoLog;
import org.vb.pokr.shared.PotWin;
import org.vb.pokr.shared.RoundState;

import app.main.PBL;
import app.main.PitBoss;
import app.main.Player;
import app.main.PokrInterpreter;
import app.main.Status;

public class GameServerSession extends PokrInterpreter {

	public long sid;
	private int actionCount; 
	public int[] lastAct = new int[3];
	public String sessName;
	
	public GameServerSession(long sid, String sessName) {
		this.sid = sid;
		if (null != sessName) this.sessName = sessName;
		else this.sessName = "sess" + Long.toString(sid);
	}
	
	synchronized public void playerJoins(String name) {
		if (name == null)
			addPlayers(1);
		else addPlayer(name);
	}
	
	public int getPlayerCount() {
		return players.size();
	}
	
	synchronized public MsgCmdRes process(MsgCmd m) {
		String[] inputs = m.cmd.split(" ");
		int code = cmdCode(inputs[0]);
		
		if (inGame				&& 
			null != currentP 	&& 
			m.pid != currentP.getId()) 
			return new MsgCmdRes(sid,code,Status.POKR_NOT_TURN_TO_PLAY,getGameStateFor(m.pid));
		
		if (code == CMD_START && inGame) 
			return new MsgCmdRes(sid,code,Status.POKR_ALREADY_STARTED,getGameStateFor(m.pid));
		
		int res = doAction(code, inputs, m.cmd);
		if (Status.POKR_OK == res) {
			postAction(code, inputs, m);
			actionCount++;
		}
		lgr.log(PBL.PLYRACT, findPlyrById(m.pid), code, 
				(inputs.length>1)?Integer.parseInt(inputs[1]):0, res);
		return new MsgCmdRes(sid,code,res,getGameStateFor(m.pid));
	}
	
	private void postAction(int code, String[] inputs, MsgCmd m) {
		lastAct[0] = m.pid;
		lastAct[1] = code;
		if (code == CMD_RAISE) {
			lastAct[2] = Integer.parseInt(inputs[1]);
			return;
		}
		lastAct[2] = -1;
	}

	public String printGameState(int pid) {
		String r = new String();
		if (PitBoss.POTWIN < getState()) {
			for (int i=playing.size()-1; i>=0; i--) {
				Player p = playing.get(i);
				if (pid == p.getId())
					r += "-> ";
				r += p + "\n";
			}
			r += board + "\n";
			r += pot + "\n";
			r += "turn left : " + turnLeft + "\n";
		}
		return r;
	}
	
	public boolean isUpdatedFor(int pid) {
		return lgr.plyrCheckPoint[pid] != lgr.entries.size()-1;
	}
	
	public GameState getGameStateIfUpdatedFor(int pid) {
		if (!isUpdatedFor(pid)) return null;
		return getGameStateFor(pid);
	}
	
	public GameState getGameStateFor(int pid) {
		if (PitBoss.NOT == getState()) {
			InfoLog il = new InfoLog();
			il.info = lgr.getLogFor(pid);
			lgr.checkPoint(pid);
			return il;
		}
		//if (PitBoss.ENDED == getState()) return new EndGame();
		
		int[] hist = lgr.getHistoryLevelFor(pid);
		// 0 RoundState, 1 PotWin
		if (0 == hist[PBL.PLYRGAIN]) {
			RoundState r = new RoundState();
			r.board = getBoard();
			r.pot = new int[2];
			r.pot[0] = pot.getBetPot();
			r.pot[1] = pot.getMaxBet();
			fillPlyrsInfo(pid, r, getState()!=PitBoss.SHOWDOWN);
			r.info = new int[1][3];
			for (int i=0; i<3; i++)
				r.info[0][i] = this.lastAct[i];	
			lgr.checkPoint(pid);
			return r;
		}
		return newPotWinMsg(hist, pid);
	}
	
	private PotWin newPotWinMsg(int[] pbl, int pid) {
		PotWin pw = new PotWin();
		int lastChkPt = lgr.plyrCheckPoint[pid];
		int size = lgr.entries.size();
		for (int i= lastChkPt; i<size; i++) {
			switch(lgr.getCatAt(i)) {
			case PBL.PLYRGAIN :
				int[] data = lgr.getDataAt(i);
				pw.gains[data[0]] += data[1];
				if (null == pw.plyrsName[data[0]]) {
					pw.plyrsName[data[0]] = findPlyrById(data[0]).name;
				}
				break;
			case PBL.PLYRSHOW : // score
				int[] data1 = lgr.getDataAt(i);
				pw.score[data1[0]] = data1[1];
				if (null == pw.plyrsName[data1[0]]) {
					pw.plyrsName[data1[0]] = findPlyrById(data1[0]).name;
				}
				break;
			default : break;
			}
		}
		pw.board = getBoard();
		pw.players = getPlyrsInfo(pid);
		lgr.checkPoint(pid);
		return pw;
	}
	
	private int[] getBoard() {
		int[] r = new int[board.cardsCount()];
		for (int i=board.cardsCount()-1; i>=0; i--)
			r[i] = board.card(i);
		return r;
	}
	
	private void fillPlyrsInfo(int pid, RoundState rs, boolean hideOthers) {
		rs.players = new int[playing.size()][5];
		rs.plyrsName = new String[playing.size()];
		for (int i=playing.size()-1; i>=0; i--) {
			Player p = playing.get(i);
			if (hideOthers && p.getId() != pid) { // other players cards hidden
				rs.players[i][0] = -1;
				rs.players[i][1] = -1;
			}
			else {
				rs.players[i][0] = p.card(0);
				rs.players[i][1] = p.card(1);
			}
			rs.players[i][2] = p.cash;
			rs.players[i][3] = p.betRound;
			rs.plyrsName[i] = p.name;
			int ppid = p.getId();
			rs.players[i][4] = ppid;
			if (ppid == currentP.getId())
				rs.currentPlyr = ppid;
		}
	}
	
	private int[][] getPlyrsInfo(int pid) {
		int siz = playing.size();
		int[][] r= new int[siz][2];
		if (siz == 1 && pid != playing.get(0).getId()) {
			r[0][0] = -1;
			r[0][1] = -1;
			return r;
		}
		for (int i=playing.size()-1; i>=0; i--) {
			Player p = playing.get(i);
			r[i][0] = p.card(0);
			r[i][1] = p.card(1);
		}
		return r;
	}
	
	public String toString() {
		return sessName;
	}
}
