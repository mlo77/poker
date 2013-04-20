/**
 * 
 * 
 * String t = "";
for (int i=0; i<this.iCount; i++) {
	t+="[v:" + Unv.getVal(this.iCards[i]) +
	   ", f:" + Unv.getFam(this.iCards[i]) + "]\n";
}	
t.toString();




String t = "";
for (int i=0; i<52; i++) {
	int v = this.iCards[i];
	if ((v & DeckBitFlag) == 0)
		t+= "DISTRIBUTED";
	t+="[v:" + Unv.getVal(v) +
	   ", f:" + Unv.getFam(v) + "]\n";
}	
t.toString();

 * 
 */
package app.main;

import java.util.List;

/**
 * @author yyc
 *
 */
public class CardHolder {
	protected int[] cards;
	protected int cNum;
	protected int cNumMax;
	
	//public TouchArea iArea;
	
	public CardHolder(int count) {
		cards = new int[count];
		for (int i=0; i<count; i++) {
			cards[i] = 0;
		}
		cNumMax = count;
		cNum = 0;
	}
	
	public int receivesCard(int card) {
		if (cNum == cNumMax) {
			return -4;
		}	
		cards[cNum++] = card;
		return 0;
	}
	
	public int cardsCount() {
		return cNum;
	}
	
	public int card(int pos) {
		if (pos >= cNum) {
			return 0;
		}
		return cards[pos];
	}
	
	// can only be called by the deck takeAllCardsFrom(player)
	public void clearAllCards() {
		for (int i=0; i<cNum; i++) {
			cards[i] = 0;
		}
		cNum = 0;
	}
		
	public int getCardValAtIdx(int pos) {
		assert pos < cNum;
		return CardUt.getValBinary(card(pos));
	}	
	
	public int initialPositionInDeck(int pos) {
		assert pos < cNum;
		return CardUt.getDeckIdx(cards[pos]);
	}
	
/*	
	public boolean has(int val) {
		for (int i=0; i<cNum; i++) {
			if ((cards[i] & val) != 0) {
				return true;
			}
		}
		return false;
	}

	public boolean has(int val, int f, int c) {
		for (int i=0; i<cNum; i++) {
			if (((cards[i] & val) != 0) &&
				((cards[i] & f) != 0) &&
				((cards[i] & c) != 0))	{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean hasKQJA10() {
		return has(Unv.KQJ10A);
	}
	  
	public int decimal(int pos) {
		int dec = 0;
		int val = cards[pos];
		while(val!=0) {
			val >>= 1;
			dec++;
		}
		return dec;
	}
*/	
	protected void maskFamiliesCardsUpdate(int checkBitmap, int len, int[] res) {
		int s = (cNumMax < len)?cNumMax:len;
		for (int i=0; i<s; i++) {
			if ((cards[i] & checkBitmap) == 0) continue;
			//res[Unv.getFam10(cards[i])-1] |= cards[i] & (Unv.KMaskFam|Unv.KMaskVal);
			//res[4] |= cards[i] & (Unv.KMaskFam|Unv.KMaskVal);
			res[CardUt.getFam10(cards[i])-1] |= cards[i] & CardUt.KMaskVal;
			res[4] |= cards[i] & CardUt.KMaskVal;
		}
	}
	
	protected int[] maskFamiliesCards(int checkBitmap, int len) {
		int s = (cNumMax < len)?cNumMax:len;
		int [] res = new int [5];
		for (int i=0; i<s; i++) {
			if ((cards[i] & checkBitmap) == 0) continue;
			//res[Unv.getFam10(cards[i])-1] |= cards[i] & (Unv.KMaskFam|Unv.KMaskVal);
			//res[4] |= cards[i] & (Unv.KMaskFam|Unv.KMaskVal);
			res[CardUt.getFam10(cards[i])-1] |= cards[i] & CardUt.KMaskVal;
			res[4] |= cards[i] & CardUt.KMaskVal;
		}
		return res;
	}
	
	protected int[] sumCards(int checkBitmap, int len) {
		int s = (cNumMax < len)?cNumMax:len;
		int[] res = new int[14];
		for (int i=0; i<s; i++) {
			if ((cards[i] & checkBitmap) == 0) continue;
			int val = cards[i] & CardUt.KMaskVal;
			res[13] |= val; 
			int valdec = 0;
			while(val != 1) {
				val >>= 1;
				valdec++;
			}
			res[valdec]++;
		}
		return res;
	}
	
	protected int[][] sumCardsByFam(int checkBitmap, int len) {
		int s = (cNumMax < len)?cNumMax:len;
		int [][] res = new int[4][13];
		for (int i=0; i<s; i++) {
			if ((cards[i] & checkBitmap) == 0) continue;
			res[CardUt.getFam10(cards[i])-1][CardUt.getValBinary(cards[i])-1]++;
		}
		return res;
	}
	
	public String toString() {
		String t = "";
		for (int i=0; i<this.cardsCount(); i++) {
			t+="\t[v:" + CardUt.getValBinChar(cards[i]) +
			   ", f:" + CardUt.getFamName(cards[i]) + "]\n";
		}	
		return t;
	}
}
