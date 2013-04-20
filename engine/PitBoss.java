package app.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PitBoss {
	public Board board;
	public Deck deck;
	public ArrayList<Player> players;
	public ArrayList<Player> playing;
	
	private int sblind = 100;
	private int hblind = 200; 
	
	public int turnLeft;
	protected int currentPidx;
	public Player currentP;
	
	private Random r = new Random();
	private int dealer; 				// position of first player in new deal
	private int state;					// current state game
	private int raiseCount; 			// count 3 max raises in bet round
	
	final static int NOT = -1;
	final static int PREFLOP = 0;
	final static int FLOP = 1;
	final static int TURN_RIVER = 2;
	final static int SHOWDOWN = 3;
	
	public Pot pot;
	
	private DbgConfig dbg;
	private boolean dbgMode = false;
	private Timer timr = null;
	private ArrayList<PitBossObserver> observers;
	final private static long BLIND_TIMER_MS = 5*60000;
	
	PitBoss() {
		this.board = new Board();
        this.players = new ArrayList<Player>();
        this.playing = new ArrayList<Player>();
        this.deck = new Deck(); 
        this.observers = new ArrayList<PitBossObserver>();
        state = NOT;
	}
	
	public int newDeal() {
		int s = players.size();
		if (s<=1) {
			System.err.println("not enough player");
			return Status.POKR_NOT_ENOUGH_PLAYER; 
		}
		playing.clear();
		for (int i=0; i<s; i++) {
			Player p = players.get(i);
			p.betHand = 0;
			p.betRound = 0;
			p.potReach = 0;
			p.finalScore = 0;
			if (p.cash == 0) continue;
			boolean v = playing.add(p);
			assert v : "PITBOSS new deal fail";
			if (!v) return Status.POKR_ERR_GENERAL;
		}
		this.pot = new Pot();
		s = playing.size();
		deck.takeAllCards();
		distribute(2, 0);
		if (dbgMode)
			currentPidx = 0;
		else 
			currentPidx = (dealer++)%s;
		currentP = playing.get(currentPidx);
		dealer = currentPidx;
		pRaiseNoCheck(sblind); System.out.println("ACTION: player"+currentP.getId()+" bets small blind");
		pRaiseNoCheck(hblind); System.out.println("ACTION: player"+currentP.getId()+" bets big blind");
		turnLeft = s; // all players must play once
		state = PREFLOP;
		raiseCount = 0;
		/*
		if (timr == null) {
			timr = new Timer();
			timr.schedule(new TimerTask() {
				@Override
				public void run() {
					sblind <<= 1;
					hblind <<= 1;
					if (observers.size() != 0) {
						for (int i=observers.size()-1; i>=0; i--)
							observers.get(i).onBlindRaise(sblind, hblind);
					}
				}
			}, BLIND_TIMER_MS, BLIND_TIMER_MS);
		}
		*/
		return Status.POKR_OK;
	}
	
	public void stop() {
		if (timr!=null) 
			timr.cancel();
	}
	
	public boolean addObserver(PitBossObserver obs) {
		assert obs != null : "BAD ARGUMENT";
		return observers.add(obs);
	}
	
	public void removeObserver(PitBossObserver obs) {
		int i = observers.indexOf(obs);
		if (-1 != i)
			observers.remove(i);
	}
	
    public ArrayList<Player> makeSortedLosers() {
    	ArrayList<Player> res = new ArrayList<Player>();
    	if (playing.isEmpty()) return res;
    	if (playing.get(0).cardsCount() != 2) return res;
    	if (board.cardsCount() != 5) return res;
    	for (int i=playing.size()-1; i>=0; i--) {
    		Player p = playing.get(i);
    		p.finalScore = p.getFinalScore();
    		boolean b = res.add(p);
    		assert b : "PITBOSS could not update collection";
    	}
    	if (res.size()!=1)
    		Collections.sort(res, new ScoreUt());
   
    	return res;
    }
    
    private void nextPlayer(boolean nround) {
		currentPidx = (currentPidx+1) % playing.size();
		currentP = playing.get(currentPidx);
		if (nround)
			turnLeft = playing.size()-1;
		else
			turnLeft--;
		if (turnLeft < 0) turnLeft=0;
		if (currentP.cash == 0 && turnLeft!=0) 
			nextPlayer(nround);
    }
    
    public int pCheck() {
		if (pot.maxBet != currentP.betHand && !currentP.isAllIn()) {
			System.out.println("playerID"+currentP.getId()+" cannot check");
			return Status.POKR_CHECK_FAIL;
		}
		System.out.println("ACTION : player"+currentP.getId()+" checks");
		nextPlayer(false);
		return Status.POKR_OK;
    }
    
    private int pRaiseNoCheck(int amount) {
		boolean newround = false;
		if (pot.maxBet < currentP.betHand+amount)
			newround = true;
		pot.bet(currentP, amount);
		nextPlayer(newround);
		raiseCount++;
		return Status.POKR_OK;
    }
    
    public int pRaise(int amount) {
		if ((pot.maxBet > currentP.betHand + amount) ||
			(amount > currentP.cash) ||
			(amount < hblind)) {
			System.out.println("cannot raise with such amount");
			return Status.POKR_RAISE_FAIL;
		}
    	if (raiseCount >= 3) {
    		System.out.println("WARNING : only 3 raises max per round, bet is capped");
    		return pCall();
    	}
		int r = pRaiseNoCheck(amount);
		if (r==Status.POKR_OK) System.out.println("ACTION : player"+currentP.getId()+" raises of "+amount);
		return r;
    }    	
	
    public int pAllIn() {
    	if (currentP.cash == 0) {
    		// already all in
    		System.out.println("ACTION : player"+currentP.getId()+" already all in");
    		nextPlayer(false);
    		return Status.POKR_OK;
    	}
    	if (currentP.betRound > pot.maxBet && raiseCount >= 3) {
    		System.out.println("WARNING : only 3 raises max per round, bet is capped");
    		return pCall();
    	}
    	int r = pRaiseNoCheck(currentP.cash);
    	if (Status.POKR_OK == r) System.out.println("ACTION : player"+currentP.getId()+" is all in");
    	return r;
    }
    
    public int pCall() {
		int v = pot.maxBet - currentP.betHand;
		if (Status.POKR_OK != pot.bet(currentP, v)) { // bet will check if v>player.cash, all in if so
			return Status.POKR_RAISE_FAIL;
		}
		System.out.println("ACTION : player"+currentP.getId()+" calls");
		nextPlayer(false);
		return Status.POKR_OK;
    }
    
    public void pFold() {
		System.out.println("ACTION : player"+currentP.getId()+" folds");
		playing.remove(currentPidx);
		currentPidx = currentPidx % playing.size();
		currentP = playing.get(currentPidx);
		turnLeft--;
		if (turnLeft < 0) turnLeft=0;
    }
	
	public void checkTurn() {
		if (state == NOT) return;
		if (checkBetRoundEnded()) {
			// check if all have bet same amount	
			if (playing.size() == 1 || board.cardsCount()==5) {
				ArrayList<Player> losers = makeSortedLosers();
				System.out.println("WINNER is p:" + losers.get(losers.size()-1));
				pot.distributeGain(losers);
				safeShutDbgMode();
				
				printGameState();
				
				state = NOT;
				if (!keepPlaying()) {
					//assert timr!=null : "PITBOSS timr null";
					//timr.cancel();
				}
				return;
			}
				
			switch (state) {
			case PREFLOP :
			case FLOP :
				System.out.println("**FLOP**");
				distribute(0,3);
				turnLeft = playing.size();
				state = TURN_RIVER;
				break;
			case TURN_RIVER :
				int count = board.cardsCount();
				if (count == 3) System.out.println("**TURN**");
				if (count == 4) {
					System.out.println("**RIVER**");
					state = SHOWDOWN;
				}
				distribute(0,1);
    			turnLeft = playing.size();
    			break;
			case SHOWDOWN :
				distribute(0,1); // one by one, has to pause in between
				//distribute(0,5-board.cardsCount());
				turnLeft = 0;
				break;
			default:
				break;
			}
		}
		printGameState();
	}
	
	public void printGameState() {
		//print game state
		if (state != NOT) {
			for (int i=playing.size()-1; i>=0; i--) {
				Player p = playing.get(i);
				if (currentP.getId() == p.getId())
					System.out.print("-> ");
				System.out.println(p);
			}
			System.out.println(board);
			System.out.println(pot);
			System.out.println("turn left : " + turnLeft);
			System.out.println("******************************");
		}
	}
	
	public String gameStateToString() {
		String r = new String();
		if (state != NOT) {
			for (int i=playing.size()-1; i>=0; i--) {
				Player p = playing.get(i);
				if (currentP.getId() == p.getId())
					r += "-> ";
				r += p + "\n";
			}
			r += board + "\n";
			r += pot + "\n";
			r += "turn left : " + turnLeft + "\n";
		}
		return r;
	}
	
	private boolean keepPlaying() {
		int s = players.size();
		int count = 0;
		for (int i=0; i<s; i++) 
			if (players.get(i).cash != 0)
				count++;
		return (count>1);
	}
	
    public void distribute(int nCardsPerPlayer, int nCardsBoard) {
    	assert(players.size() > 1 && nCardsPerPlayer <= 2 && nCardsBoard <= 5);
    	for(int i=players.size()-1; i>=0; i--) {
    		for (int k=0; k<nCardsPerPlayer; k++) {
    			int v;
    			if (dbgMode) {
    				if (dbg.hasNextP()) v = dbg.getNextP();
    				else {
    					v = r.nextInt(52);
        				while(!deck.hasCardByIndex(v) || dbg.hasReserved(v)) // not to choose a reserved
        					v = r.nextInt(52);
    				}
    			}
    			else {
    				v = r.nextInt(52);
    				while(!deck.hasCardByIndex(v)) v = r.nextInt(52);
    			}
    			deck.giveCardTo(v, players.get(i));
    		}
    	}
    	for (int j=0; j<nCardsBoard; j++) {
    		int v;
			if (dbgMode) {
				if (dbg.hasNextB()) v = dbg.getNextB();
				else {
					v = r.nextInt(52);
    				while(!deck.hasCardByIndex(v) || dbg.hasReserved(v)) // not to choose a reserved
    					v = r.nextInt(52);
				}
			}
			else {
				v = r.nextInt(52);
				while(!deck.hasCardByIndex(v)) v = r.nextInt(52);
			}
    		deck.giveCardTo(v, board);
    	}
    }    
    
    private Player addPlayer() {
   		if (players.size() >= 10) return null;
   		Player p = new Player(deck, board, players.size());
   		return (players.add(p)) ? p : null;
    }    
    
    public void removePlayer(int pid) {
		for (int i=players.size()-1; i>=0; i--)
			if (players.get(i).getId() == pid)
				players.remove(i);
		for (int i=playing.size()-1; i>=0; i--)
			if (playing.get(i).getId() == pid)
				playing.remove(i);
    }	
	    
	public int addPlayers(int num) {
		while (num-- != 0) {
			Player p = addPlayer();
			if (p != null) {
				System.out.println("player:" + p.getId() + " added");
				if (playing.size() != 0)
					System.out.println("Game in play, new player will play in next game");
			}
			else {
				System.out.println("8 players max");
				return Status.POKR_ADDP_MAXOUT;
			}
		}
		return Status.POKR_OK;
	}
	
	private boolean checkBetRoundEnded() {
		if (turnLeft == 0) {
			boolean betEquality = true;
			int count=0;
			for (int i=playing.size()-1; i>=0; i--) {
				Player p = playing.get(i);
				if (!p.isAllIn()) { // TODO 
					if (pot.maxBet != p.betHand)
						betEquality = false;
					count++;
				}
			}
			if (count<=1) state = SHOWDOWN; // showdown if less than 2 not all in
			if (!betEquality) return false;
			raiseCount = 0;
			System.out.println("** BET ROUND ENDED **");
			pot.roundEnded(playing);
			return true;
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	public class Pot {
		private int maxBet;
		private int[] pot;
		private int[] cap;
		private int potCount;
		private ArrayList<Player> gamblers;
		private int betPot;
		
		Pot() {
			//this.pboss = pboss;
			this.betPot = 0;
			this.maxBet = 0;
			this.gamblers = new ArrayList<Player>();
			this.pot = new int[10];
			this.cap = new int[10];
		}
		
		public int getBetPot() {
			return betPot;
		}
		
		public int getMaxBet() {
			return maxBet;
		}
		
		/**
		 * set max bet in round, put bet in the bet pot
		 * @param p
		 * @param a
		 * @return
		 */
		private int bet(Player p, int a) {
			if (-1 == this.gamblers.indexOf(p)) {
				if (!this.gamblers.add(p))
					return Status.POKR_BET_FAIL;
			}
			if (p.cash < a) a = p.cash; // allin
			boolean allin = a==p.cash;
			if (a+p.betHand<this.maxBet && !allin) return Status.POKR_BET_FAIL;
			this.maxBet = Math.max(this.maxBet, a+p.betHand);
			this.betPot += a;
			p.betHand += a;
			p.betRound += a;
			p.cash -= a;
			return Status.POKR_OK;
		}

		/**
		 * Must be called at end of each round, put each player bet in corresponding (side) pot
		 * cap the side pot when Player is all in. 
		 */
		private void roundEnded(ArrayList<Player> playing) {
			if (this.gamblers.size() <= 1) {
				System.err.println("Pot::roundEnded with 1 or 0 gambler");
				return;
			}
				
			Collections.sort(this.gamblers, new BetSort()); // gamblers may contain player that have fold
			int s = this.gamblers.size();
			if (this.gamblers.get(s-1).betRound == 0) return;
			
			// highest gambler reclaim
			if (playing.size() < 2) return;
			Player pLast = null;
			Player p2ndLast = null;
			int i;
			for (i=s-1; i>=0; i--) {
				pLast = this.gamblers.get(i);
				if (playing.indexOf(pLast) != -1)
					break;
			}
			for (i--; i>=0; i--) {
				p2ndLast = this.gamblers.get(i);
				if (playing.indexOf(p2ndLast) != -1)
					break;
			}
			if (pLast.betRound != p2ndLast.betRound && p2ndLast.isAllIn()) {
				// can reclaim is 2nd highest is betting less and is all in
				// therefore no need to bet that much
				int reclaim = pLast.betRound - p2ndLast.betRound;
				pLast.cash += reclaim;
				pLast.betRound -= reclaim;
				pLast.betHand -= reclaim;
				maxBet -= reclaim;
				betPot -= reclaim;
			}
			
			// split into side pots
			for (i=0; i<s; i++) {
				Player p = this.gamblers.get(i);
				int b = p.betRound;
				int potIdx = 0;
				while(b>0) {
					if (this.cap[potIdx] != 0 && b > this.cap[potIdx]) {
						b -= this.cap[potIdx];
						this.pot[potIdx] += this.cap[potIdx];
						potIdx++;
					}
					else {
						this.pot[potIdx] += b;
						if (p.isAllIn() && this.cap[potIdx] == 0) {
							this.cap[potIdx] = b;
						}
						b = 0;
						p.potReach = potIdx;
						potCount = Math.max(potCount, potIdx);
					}
				}
			}
			
			// reset round bets
			for (i=0; i<s; i++) {
				Player p = this.gamblers.get(i);
				p.betRound = 0;
			}
		}
		
		private void distributeGain(ArrayList<Player> losers) {
			int s = losers.size();
			if (s == 1) {
				losers.get(0).cash += this.betPot;
				return;
			}
			int i=s-1; int u=s-2;
			while(this.betPot > 0) {
				Player p1 = losers.get(i);
				Player p2 = losers.get(u);
				while (ScoreUt.cmp(p1, p2) == 0) p2 = losers.get(--u);
				ArrayList<Integer> pr = new ArrayList<Integer>();
				pr.add(p1.potReach);
				for (int j=i-1; j>u; j--) {
					if (pr.indexOf(losers.get(j).potReach) == -1)
						pr.add(losers.get(j).potReach);
				}
				int prs = pr.size();
				if (prs != 1) {
					// case where there is equivalent score and winners have bet different amount
					// so may not be elligible on same pots. First get all pot reach indexes from those
					// guys. Sort the pot reach indexes. From lowest reach make a split pot for all 
					// winners, split gain...
					Collections.sort(pr);
					for (int j=0; j<prs; j++) {
						int potSplit = 0;
						for (int m=pr.get(j); m>=0; m--) {
							potSplit += pot[m]; // assumption that all have same reach
							pot[m] = 0;
						}
						int countP = 0;
						for (int q=i; q>u; q--) {
							if (losers.get(q).potReach >= pr.get(j))
								countP++;
						}
						this.betPot -= potSplit;
						potSplit += potSplit % (countP);
						potSplit /= countP;
						for (int q=i; q>u; q--) {
							if (losers.get(q).potReach >= pr.get(j))
								losers.get(q).cash += potSplit;
						}
					}
				}
				else {
					int potSplit = 0;
					for (int j=p1.potReach; j>=0; j--) {
						potSplit += pot[j]; // assumption that all have same reach
						pot[j] = 0;
					}
					this.betPot -= potSplit;
					potSplit += potSplit % (i-u);
					potSplit /= i-u;
					for (int j=i; j>u; j--) losers.get(j).cash += potSplit;
				}
				i=u;
				u=i-1;
			}
		}
		
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
		
		public String toString() {
			String s = "";
			for (int i=0; i<this.potCount; i++) {
				s += "\np["+i+"]:"+pot[i]+"]";
			}
			return "Pot:"+ this.betPot + " - max bet:"+this.maxBet+s+"\n";
		}
		
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
		
		class BetSort implements Comparator<Player> {
			@Override
			public int compare(Player arg0, Player arg1) {
				return arg0.betRound - arg1.betRound;
			}
		}
	} // end Pot
	
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
	
	/**
	 * "CMD_NAME c1,c2|c1,c2|c1,c2|... b1,b2,..."
	 * @param s
	 */
	public int setDbgMode(String s) {
		String[] e = s.split(" ");
		//if (e.length  4) return;
		dbg = new DbgConfig();
		String[] p = e[1].split(":");
		for (int i=0; i<p.length; i++) {
			String[] c = p[i].split(",");
			for (int j=0; j<c.length; j++) {
				int cv = Integer.parseInt(c[j]);
				if (dbg.playersCards[dbg.idxP] != -1)
					System.err.println("DEBUG DISTRIB duplicate!!");
				dbg.playersCards[dbg.idxP++] = cv;
				dbg.map |= (long)1<<cv;
			}
		}
		
		String[] b = e[2].split(",");
		for (int i=0; i<b.length; i++) {
			int cv = Integer.parseInt(b[i]);
			if (dbg.boardCards[dbg.idxB] != -1)
				System.err.println("DEBUG DISTRIB duplicate!!");
			dbg.boardCards[dbg.idxB++] = cv;
			dbg.map |= (long)1<<cv;
		}
		this.dbgMode = true;
		dbg.pcLen = dbg.idxP;
		dbg.bcLen = dbg.idxB;
		dbg.idxB = 0;
		dbg.idxP = 0;
		return Status.POKR_OK;
	}
	
	public void safeShutDbgMode() {
		this.dbgMode = false;
		this.dbg = null;
	}
	
	class DbgConfig {
		int idxP;
		int idxB;
		int[] playersCards; // pair [c1,c2,c1,c2,...] for each player
		int pcLen;
		int[] boardCards;
		int bcLen;
		long map;
		
		DbgConfig() {
			this.idxP = 0;
			this.idxB = 0;
			this.pcLen = 0;
			this.bcLen = 0;
			this.map = 0;
			this.playersCards = new int[20];
			for (int i=0; i<20; i++)
				this.playersCards[i] = -1;
			this.boardCards = new int[5];
			for (int i=0; i<5; i++)
				this.boardCards[i] = -1;
		}
		
		boolean hasNextP() {
			return (idxP < pcLen);
		}
		
		boolean hasNextB() {
			return (idxB < bcLen);
		}
		
		boolean hasReserved(int v) {
			return (((long)1<<v)&map) != 0;
		}
		
		int getNextP() {
			if (idxP >= pcLen) return -1;
			return playersCards[idxP++];
		}
		
		int getNextB() {
			if (idxB >= bcLen) return -1;
			return boardCards[idxB++];
		}
	}
}
