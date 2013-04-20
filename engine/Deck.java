/**
 * 
 */
package app.main;

import java.util.ArrayList;

/**
 * @author yyc
 *
 */
public class Deck extends CardHolder {
	
	public static final int DECKBITFLAG = CardUt.UNVDECKBITFLAG; // 0100 0000 0000 0000 0000 0000 0000 0000 
	private ArrayList<Player> players = new ArrayList<Player>();
	private Board board;
	private int[][] sumCardsByFamTable;
	private int[] sumCardsTable;
	private int[] maskFamiliesCardsTable;
	
	private void generateCards() {
		cNum = 0;
		for (int f=0; f<4; f++) {
			for (int v=0; v<13; v++) {
				int c = 0;
				c = 1<<v;
				c |= ((1<<f) << CardUt.KFamShift) & CardUt.KMaskFam;
				c |= (f << CardUt.KFam10Shift) & CardUt.KMaskFam10;
				c |= cNum++ << CardUt.KIdxShift;
				c |= DECKBITFLAG;
				cards[cNum-1] = c; 
			}
		}
	}
	
	/**
	 * 
	 */
	public Deck() {
		// TODO Auto-generated constructor stub
		super(52);
		generateCards();
	}
	
	private int findPlayer(Player p) {
		for (int i=players.size()-1; i>=0; i--)
			if (players.get(i).getId() == ((Player)p).getId()) 
				return i;
		return -1;
	}
	
	public int giveCardTo(int pos, CardHolder p) {
		if (pos > cNumMax || (cards[pos] & DECKBITFLAG) == 0 || cNum == 0) {
			System.err.println("DECK giveCardTo range pb");
			return -1;
		}
		if (p instanceof Player) {
			if (-1 == findPlayer((Player)p))
				players.add((Player) p);
		}
		else { //if (p instanceof psBoard) {
			if (board == null)
				board = (Board) p;
			else if (board!=null && !board.equals(p)) {
				System.err.println("DECK give card trying to give card to second board");
				return -2;
			}
		}
		return safeGive(pos, p);
	}
	
	private int safeGive(int pos, CardHolder p) {
		cards[pos] &= ~(DECKBITFLAG);
		cNum--;
		maskFamiliesCardsTable = maskFamiliesCards(DECKBITFLAG, cNumMax);
		sumCardsTable = sumCards(DECKBITFLAG, cNumMax);
		sumCardsByFamTable = sumCardsByFam(DECKBITFLAG, cNumMax);
		if (0 != p.receivesCard(cards[pos])) {
			cards[pos] |= DECKBITFLAG;
			cNum++;
			maskFamiliesCardsTable = maskFamiliesCards(DECKBITFLAG, cNumMax);
			sumCardsTable = sumCards(DECKBITFLAG, cNumMax);
			sumCardsByFamTable = sumCardsByFam(DECKBITFLAG, cNumMax);
			assert false;
			return -4;
		}
		return cNum;
	}
	
	public void takeAllCards() {
		takeAllCardsFrom(board);
		for (int i=players.size()-1; i>=0; i--)
			takeAllCardsFrom(players.get(i));
	}
	
	private void takeAllCardsFrom(CardHolder p) {
		if (p == null) {
			//System.err.println("DECK err takeAllCardsFrom");
			return;
		}
			
		// check repeat
		for (int i=0; i<p.cardsCount(); i++) {
			int idx = p.initialPositionInDeck(i);
			assert (cards[idx] & DECKBITFLAG) == 0;
			cards[idx] = p.card(i);
			cards[idx] |= DECKBITFLAG;
			cNum++;
		}
		p.clearAllCards();
	}

	public int[] maskFamiliesCards() {
		if (maskFamiliesCardsTable.length == 0)
			maskFamiliesCardsTable = maskFamiliesCards(DECKBITFLAG, cNumMax);
		return maskFamiliesCardsTable;
	}

	public int[] sumCards() {
		if (sumCardsTable.length == 0)
			sumCardsTable = sumCards(DECKBITFLAG, cNumMax);
		return sumCardsTable;
	}
	
	public int[][] sumCardsByFam() {
		if (sumCardsByFamTable.length == 0)
			sumCardsByFamTable = sumCardsByFam(DECKBITFLAG, cNumMax);
		return sumCardsByFamTable;
	}	
	
	public int[] otherPlayersMaskValueCards(int[] playerCards) {
		int res[] = new int[5];
		for (int i=0; i<cNumMax; i++) {
			int c = cards[i];
			// deck ne l'a pas, le joueur non plus 
			int f = CardUt.getFam10(cards[i])-1;
			if ((c & DECKBITFLAG) == 0 && ((playerCards[f] & CardUt.getValBinary(c)) == 0) ) {
				res[f] |= c & (CardUt.KMaskFam|CardUt.KMaskVal);
				res[4] |= res[f];
			}
		}
		return res;
	}
	
	// not effective on deck
	@Override
	public int receivesCard(int card) {
		System.err.println("psDeck receivesCard SHOULD NOT BE CALLED");
		return -1;
	}
	
	// not effective on deck
	@Override
	public void clearAllCards() {
		System.err.println("psDeck clearAllCards SHOULD NOT BE CALLED");
		return;
	}

	public boolean hasCardByIndex(int pos) {
		// TODO Auto-generated method stub
		if(pos < 0 || pos > cNumMax) return false;
		return (cards[pos] & DECKBITFLAG) != 0;
	}
	
	
	public String toString() {
		String t = "";
		for (int i=0; i<52; i++) {
			int v = this.cards[i];
			if ((v & DECKBITFLAG) == 0)
				t+= "DISTRIBUTED";
			t+="[v:" + CardUt.getValBinChar(v) +
			   ", f:" + CardUt.getFamName(v) + "]\n";
		}	
		t.toString();
		return t;
	}
}

