package org.vb.pokr.server;

import org.vb.pokr.client.CommService;
import org.vb.pokr.messages.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommServiceImpl extends RemoteServiceServlet implements CommService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public Message sendMessage(Message msg) throws IllegalArgumentException {
		switch (msg.getType()) {
		case MsgCreateGame.TYPE :
			{
			MsgCreateGame m = (MsgCreateGame) msg;
			GameServerSession gs = SessionMgr.getInstance().findOrCreateAndJoinSession(m.sid, m.sessName, m.plyrName);
			if (gs == null) return new MsgJoinGameRes(-1, -1, "MAX REACHED");
			System.out.println("Session "+gs.sid+" created by pid:"+(gs.getPlayerCount()-1));
			return new MsgJoinGameRes(gs.getPlayerCount()-1, gs.sid, gs.toString());
			}
		case MsgJoinGame.TYPE : 
			{
			// for the moment any client can join several times
			// I may use login id to uniquely identify client
			MsgJoinGame m = (MsgJoinGame) msg;
			GameServerSession gs = SessionMgr.getInstance().findAndJoinSession(m.sid, m.plyrName);
			if (gs==null) new MsgJoinGameRes(-1, m.sid, "failed");
			System.out.println("Session "+gs.sid+" joined by pid:"+(gs.getPlayerCount()-1));
			return new MsgJoinGameRes(gs.getPlayerCount()-1, gs.sid, gs.toString());
			}
		case MsgCmd.TYPE :
			{
			MsgCmd m = (MsgCmd) msg;
			GameServerSession gs = SessionMgr.getInstance().findSession(m.sid);
			if (gs == null) return new MsgCmdRes(m.sid, -1, -1, null);
			return (Message) gs.process(m);
			}
		case MsgQueryGameState.TYPE :
			{
			MsgQueryGameState m = (MsgQueryGameState) msg;
			GameServerSession gs = SessionMgr.getInstance().findSession(msg.sid);
			if (gs == null) return new MsgGameState(m.sid, null);
			return new MsgGameState(m.sid, gs.getGameStateIfUpdatedFor(m.pid));
			}
		case MsgQuerySess.TYPE :
			{
			MsgGameSessions	m = new MsgGameSessions();
			m.sidList = SessionMgr.getInstance().sidList;
			return m;
			}
		case MsgPoke.TYPE :
			{
			MsgPoke m = (MsgPoke) msg;
			//System.out.println("MsgPoke for");
			int found = 0;
			for (int i=m.sidCount()-1; i>=0; i--) {
				//System.out.print("Session:"+m.getSid(0)+" pid:"+m.getPid(0));
				GameServerSession gg = SessionMgr.getInstance().findSession(m.getSid(i));
				//System.out.println(" - updated:"+gg.isUpdatedFor(m.getPid(i)));
				if (null == gg || !gg.isUpdatedFor(m.getPid(i))) continue;
				found++;
			}
			MsgPokeBack r = new MsgPokeBack();
			r.allocate(found);
			for (int i=m.sidCount()-1; i>=0; i--) {
				GameServerSession gg = SessionMgr.getInstance().findSession(m.getSid(i));
				if (null == gg || !gg.isUpdatedFor(m.getPid(i))) continue;
				r.addSidState(gg.sid, gg.getGameStateIfUpdatedFor(m.getPid(i)));
			}
			return r;
			}
		default :
			break;
		}
		return null;
	}

	@Override
	public void postMessage(Message msg) {
		switch(msg.getType()) {
		case MsgReportWTF.TYPE :
			{
			MsgReportWTF m = (MsgReportWTF) msg;
			if (!m.passwd.equals("pantera")) return;	
			} break;
		case MsgKillGame.TYPE : {
			MsgKillGame m = (MsgKillGame) msg;
			SessionMgr.getInstance().removeSession(m.sid);
			} break;
		default : break;
		}
	}

	private void sendReport(long sid) {
		GameServerSession gg = SessionMgr.getInstance().findSession(sid);
		if (null == gg) return;
		
	}
	
}
