package app.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;


public class PokerGame extends Interpreter {

	//private Board board;
	//private ArrayList<Player> players;
	//private ArrayList<Player> playing;
	
	PokerGame() {
        super();
        //board = pitb.board;
        //deck = pitb.deck;
        //players = pitb.players;
        //playing = pitb.playing;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("howdi ho");
		
		PokerGame g = new PokerGame();
        g.runGame();

	}

	@Override
	public void printUsage() {
		System.out.println("ADDP - adds a player");
		System.out.println("REMP <pid> - removes player <pid>");
		System.out.println("CHECK - current player by default checks");
		System.out.println("RAISE <n> - current player by default raises for <n>");
		System.out.println("CALL - current player is calling");
		System.out.println("FOLD - current player folds");
		System.out.println("ALLIN / ALL - current by default player has balls in");
		System.out.println("START / s - starts play");
		System.out.println("FIN / f - ends game");
	}

	private void runGame() throws IOException {
		
        //  open up standard input
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));		
		FileReader fis = new FileReader(new File("poka.txt"));
		BufferedReader br = new BufferedReader(fis);
        String cmd = null;
        String[] inputs;
        boolean run = true;
		while(run) {
			cmd = br.readLine();
			if (cmd.startsWith("--")) continue;
			inputs = cmd.split(" ");
			int code = cmdCode(inputs[0]);
			if (CMD_FINISH == code) run = false;
			doAction(code, inputs, cmd);
		}	
		br.close();
		fis.close();
	}
/*
	@SuppressWarnings("unused")
	private int calculateChanceWin() {
    	int np = players.size();
    	double c=0f;
    	for(int i=0; i<np; i++) {
    		Player pi = players.get(i);
    		pi.mChanceWin = 0; // reset
    		for (int j=0; j<np; j++) {
    			if (i == j) {
    				continue;
    			}
    			calculateVS(pi, players.get(j));
    		}
    		c += pi.mChanceWin;
    	}
    	c /= 100f;
    	double max = 0;
    	int idxMax = -1;
    	for(int i=0; i<np; i++) {
    		Player pi = players.get(i);
    		pi.mChanceWin = pi.mChanceWin / c;
    		if (pi.mChanceWin > max) {
    			max = pi.mChanceWin;
    			idxMax = i;
    		}
    	}
    	return idxMax;
    }    
    
    
    private void calculateVS(Player ref, Player cgr) {
    	double res = 0f;
    	int likelyWinner = 0; // 0 undef, 1 ref, 2 cgr
    	int idxOffset = 0;
    	for (int j=0; j<9; j++) {
    		double c = cgr.mComboProba[j];
    		if (c == 1f) { // cgr has good chance already
    			likelyWinner = 2;
    			idxOffset = j;
    		}
    		double r = ref.mComboProba[j];
    		if (r == 1f) { // ref has good chance already
    			likelyWinner = 1;
    			idxOffset = j;
    		}
    	}
    	
    	if (likelyWinner == 1) {
    		res += calculateVSScoreSameFig(ref, cgr, idxOffset);
        	for (int i=idxOffset+1; i<9; i++) { // TODO optimiser les boucles
        		res += 1-cgr.mComboProba[i]; 
        	}
    	}
    	else if (likelyWinner == 2) {
    		res += calculateVSScoreSameFig(ref, cgr, idxOffset);
        	for (int i=idxOffset+1; i<9; i++) {
        		res += ref.mComboProba[i]; 
        	}
    	}
    	else {
	    	for (int j=0; j<9; j++) {
	    		if (ref.mComboProba[j] <= 0) {
	    			continue; // no chance to win with this combo, just skip, no addition to chance win
	    		}
	    		// score ref[j] * challenger chances de ne pas gagner
	    		double cScoreLow = 0f;
	    		double cAntiScoreHigh = 0f;
	    		double cScoreSameFig = 0f;
	    		int i = 0;
	        	for (i=0; i<j; i++) {
	        		cScoreLow += cgr.mComboProba[j]; 
	        	}
	        	for (i=j+1; i<9; i++) {
	        		cAntiScoreHigh += 1-cgr.mComboProba[j]; 
	        	}
	        	if (cgr.mComboProba[j] != 0) {
	        		// calculer en detail pour cette combi
	        		cScoreSameFig = calculateVSScoreSameFig(ref, cgr, j);
	        	}    		
	    		res += ref.mComboProba[j] * (cScoreLow + cAntiScoreHigh + cScoreSameFig); 
	    	}
	    	ref.mChanceWin += res; // need to normalize
	    	
	    	int sumr = ref.getCardValAtIdx(0) + ref.getCardValAtIdx(1);
	    	int sumc = cgr.getCardValAtIdx(0) + cgr.getCardValAtIdx(1);
	    	if (sumr > sumc && likelyWinner == 0) {
	    		ref.mChanceWin += 1f;
	    	}
    	}
    }
    
    private double calculateVSScoreSameFig(Player ref, Player cgr, int combi) {
    	    	
    	int s = ref.cardsCount();
    	int r0 = ref.getCardValAtIdx(0);
    	int r1 = ref.getCardValAtIdx(1); // can be 0
    	int c0 = cgr.getCardValAtIdx(0);
    	int c1 = cgr.getCardValAtIdx(1); // can be 0
		
		boolean c0r0 = c0>=r0; // c0 gagne sur r0
		boolean c1r0 = c1>=r0; // c1 gagne sur r0
		boolean c0r1 = c0>=r1;
		boolean c1r1 = c1>=r1;
    	
    	if (r0 == c0 && r1 == c1) {
			return 0; // draw
    	}
    	else if ((r0<c0 && s==1) ||
    			 (r0<c0 && s==2 && r0<c1 && r1<c0 && r1<c1)) {
    		 // challenger has better cards all the way, must not get any of that combo at all
    		return ref.mComboProba[combi] * (1f-cgr.mComboProba[combi]);
    	}
    	else if ((r0>c0 && s==1) ||
   			 (r0>c0 && s==2 && r0>c1 && r1>c0 && r1>c1)) {
    		// ref has better cards all the way, just need to get that combo r0 or r1
    		return ref.mComboProba[combi];
    	}
    	
    	//// this far, we should have 2 cards
    	//// previous if-else if must have dealt with it 
    	
    	// paire, dpaire, triple, fullhouse, quad
    	if (combi == 3) { // full house    		
    		boolean c0r0FH = false; // c0 gagne sur r0
    		boolean c1r0FH = false; // c1 gagne sur r0
    		// test r0 threekind
    		if (c0 > r0) {
    			// cgr must not have full house, card 1 3kind and card 0 pocket
    			c0r0FH = true;
    			// if cgr gets this full house (c0 3kind) ref cannot win with that FH (r0 three kind) 
    		}
    		else if (c0 == r0) {
    			if (c1 >= r1) {
    				// cgr must not have full house, card 0 3kind and card 1 pocket
    				c0r0FH = true;
    			}
    			// else ref has better FH 
    		}
    		
    		if (c1 > r0) {
    			c1r0FH = true;
    			// if cgr gets this full house (c0 3kind) ref cannot win with that FH (r0 three kind) 
    		}
    		else if (c1 == r0) {
    			if (c0 >= r1) {
    				// cgr must not have full house, card 1 3kind and card 0 pocket
    				c1r0FH = true;
    			}
    			// else ref has better FH 
    		}
    		
    		boolean c0r1FH = false;
    		boolean c1r1FH = false;
    		// r1 threekind
    		if (c0 > r1) {
    			c0r1FH = true; 
    		}
    		else if (c0 == r1) {
    			if (c1 >= r0) {
    				c0r1FH = true;
    			}
    			// else ref has better FH 
    		}
    		if (c1 > r1) {
    			c1r1FH = true; 
    		}
    		else if (c1 == r1) {
    			if (c0 >= r0) {
    				c1r1FH = true;
    			}
    			// else ref has better FH 
    		}
    		if (!c0r0FH && !c0r1FH && !c1r0FH && !c1r1FH) {
    			// case where 3kind is equal, pocket makes winner
    			return ref.mComboProba[combi]; 
    		}
    		else if (c0r0FH && c0r1FH && c1r0FH && c1r1FH) {
    			return ref.mComboProba[combi] * (1f-cgr.mComboProba[combi]);
    		}
    		// then at least one combi from cgr is better
    		
    		double fhr0 = ref.mCP0[2]*ref.mCP1[0];
    		double fhr1 = ref.mCP1[2]*ref.mCP0[0];
    		if (c0r0FH && c1r0FH) {
    			fhr0 *= 1f-cgr.mComboProba[3];
    		}
    		else {
    			if (c0r0FH) {
	    			fhr0 *= 1f-(cgr.mCP0[2] * cgr.mCP1[0]); // chance to win with r0 3kind -> no 3 kind c0
	    		}
	    		else if (c1r0FH) {
	    			fhr0 *= 1f-(cgr.mCP1[2] * cgr.mCP1[0]); // chance to win with r0 3kind -> no 3 kind c0
	    		}
    		}

    		if (c0r1FH && c1r1FH) {
    			fhr1 *= 1f-cgr.mComboProba[3];
    		}
    		else {
    			if (c0r1FH) {
	    			fhr1 *= 1f-(cgr.mCP0[2] * cgr.mCP1[0]); // chance to win with r0 3kind -> no 3 kind c0
	    		}
	    		else if (c1r1FH) {
	    			fhr1 *= 1f-(cgr.mCP1[2] * cgr.mCP1[0]); // chance to win with r0 3kind -> no 3 kind c0
	    		}
    		}    		
    		return fhr0 + fhr1;
    	}
    	
    	else if (combi <= 4 || combi == 6) { // pair, double pair, 3 kind, quad, straight
    		
    		if (!c0r0 && !c0r1 && !c1r0 && !c1r1) {
    			// case where 3kind is equal, pocket makes winner
    			return ref.mComboProba[combi]; 
    		}
    		else if (c0r0 && c0r1 && c1r0 && c1r1) {
    			return ref.mComboProba[combi] * (1f-cgr.mComboProba[combi]);
    		}
    		// then at least one combi from cgr is better
    		
    		double r0win = ref.mCP0[combi];
    		double r1win = ref.mCP1[combi];
    		if (c0r0 && c1r0) {
    			r0win *= 1f-cgr.mComboProba[combi];
    		}
    		else {
    			if (c0r0) {
    				r0win *= 1f-cgr.mCP0[combi]; // chance to win with r0 -> no win with c0
	    		}
	    		else if (c1r0) {
	    			r0win *= 1f-cgr.mCP1[combi]; // chance to win with r0 -> no win with c1
	    		}
    		}

    		if (c0r1 && c1r1) {
    			r1win *= 1f-cgr.mComboProba[combi];
    		}
    		else {
    			if (c0r1) {
    				r1win *= 1f-cgr.mCP0[combi]; // chance to win with r1 -> no win with c0
	    		}
	    		else if (c1r1) {
	    			r1win *= 1f-cgr.mCP1[combi]; // chance to win with r1 -> no win with c1
	    		}
    		}    		
    		return r0win + r1win;
    	}

    	//else if (combi == 5) { // couleur, straight flush, royal
		int fr0 = CardUt.getFam10(ref.card(0));
		int fr1 = CardUt.getFam10(ref.card(1));
		int fc0 = CardUt.getFam10(cgr.card(0));
		int fc1 = CardUt.getFam10(cgr.card(1));
		boolean cc = fc0 == fc1;
		boolean rr = fr0 == fr1;
		if (rr) { // one possibility, counting with ref.mComboProba
			double w = ref.mComboProba[combi];
			if (cc && fc0 == fr1) { // there is a possibility cgr might get same color
				if (c0r0 && c0r1 || c1r0 && c1r1) {
					return w*(1f-cgr.mComboProba[combi]);
				}
				return w; // ref is certain to win if gets this color 
			}
			
			// !cc
			if (fc0 == fr1) { // may use same color
				if (c0r0 && c0r1) {
					w*= 1f-cgr.mCP0[combi];
				}
			}
			if (fc1 == fr1) {
				if (c1r0 && c1r1) {
					w*= 1f-cgr.mCP1[combi];
				}
			}
			return w;
		}
		
		// win with r0
		double w0 = ref.mCP0[combi];
		if (cc && fc0 == fr0) {
			if (c0r0 && c0r1 || c1r0 && c1r1) {
				w0 *= (1f-cgr.mComboProba[combi]);
			}
		}
		// !cc
		if (fc0 == fr0) {
			if (c0r0) {
				w0 *= 1f-cgr.mCP0[combi];
			}
		}
		if (fc1 == fr0) {
			if (c1r0) {
				w0 *= 1f-cgr.mCP1[combi];
			}
		}
			
		// win with r1
		double w1 = ref.mCP1[combi];
		if (cc && fc0 == fr1) {
			if (c0r0 && c0r1 || c1r0 && c1r1) {
				w1 *= (1f-cgr.mComboProba[combi]);
			}
		}
		// !cc
		if (fc0 == fr1) {
			if (c0r1) {
				w1 *= 1f-cgr.mCP0[combi];
			}
		}
		if (fc1 == fr1) {
			if (c1r1) {
				w1 *= 1f-cgr.mCP1[combi];
			}
		}
		
		return w0+w1;    	
    }
    */        
}

	