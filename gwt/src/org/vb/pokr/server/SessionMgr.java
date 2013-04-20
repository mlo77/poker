package org.vb.pokr.server;

import java.util.ArrayList;
import java.util.Hashtable;

import org.vb.pokr.shared.SidAndName;

public class SessionMgr {
	private static SessionMgr myself;
	private Hashtable<Long, GameServerSession> allSess = new Hashtable<Long, GameServerSession>(50);
	private static long count;
	public ArrayList<SidAndName> sidList = new ArrayList<SidAndName>();
	private Long privLong = new Long(-1);
	private SessionMgr() {	
	}
	
	static public SessionMgr getInstance() {
		if (myself == null) {
			myself = new SessionMgr();
		}
		return myself;
	}
	
	synchronized GameServerSession findOrCreateAndJoinSession(long sid, String sessName, String plyrName) {
		if (allSess.size() >= 100) return null;
		
		privLong = sid;
		if (-1 == sid || !allSess.containsKey(privLong)) {
			privLong = count++;
			GameServerSession gg = new GameServerSession(privLong, sessName);
			gg.playerJoins(plyrName);
			allSess.put(gg.sid, gg);
			sidList.add(new SidAndName(gg.sid, gg.sessName));
			return gg;
		}
		GameServerSession gg = (GameServerSession) allSess.get(privLong);
		gg.playerJoins(plyrName);
		return gg;
	}
	
	GameServerSession findSession(long sid) {
		privLong = sid;
		return (GameServerSession) allSess.get(privLong);
	}
	
	synchronized public void removeSession(long sid) {
		privLong = sid;
		if (-1 == sid || !allSess.containsKey(privLong)) return;
		allSess.remove(privLong);
		for (int i=sidList.size()-1; i>=0; i--)
			if (sidList.get(i).sid==privLong) {
				sidList.remove(i);
				return;
			}
		
	}

	public GameServerSession findAndJoinSession(long sid, String plyrName) {
		GameServerSession gg = findSession(sid);
		if (null != gg)
			gg.playerJoins(plyrName);
		return gg;
	}

}
