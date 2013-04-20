/**
 *
 */
package app.main;
import java.util.List;
/**
 * @author yyc
 *
 */
public class Player extends CardHolder {
	private int id = -1;
	//private boolean mShowHand;
	private Deck deck;
	private Board board;
	public double[] mComboProba;
	public double[] mCP0; // proba pair- double paire - triple - full house - quad
	public double[] mCP1;
	public double[] mP0AnyOtherCard;  
	public double[] mP1AnyOtherCard;
	public double[] mP0SameCard;
	public double[] mP1SameCard;
	public double mChanceWin;

	public int cash;
	public int betHand;
	public int betRound;
	public String name;
	public int potReach;
	public int finalScore;
	
	private static long[][] permut = new long[53][53];
	
	public Player(Deck deck, Board b, String n, int cash, int pid) {
		// TODO Auto-generated constructor stub
		super(2);
		this.deck = deck;
		this.board = b;
		this.mComboProba = new double[9];
		this.mCP0 = new double[5]; // proba pair- double paire - triple - full house - quad
		this.mCP1 = new double[5];
		this.mP0AnyOtherCard = new double[3];
		this.mP1AnyOtherCard = new double[3];
		this.mP0SameCard = new double[3];
		this.mP1SameCard = new double[3];
		this.id = pid;	
		this.name = n;
		this.cash = cash;
		this.potReach = 0;
		this.finalScore = 0;
		clearAllCards();
	}

	public Player(Deck deck, Board b, int pid) {
		// TODO Auto-generated constructor stub
		this(deck, b, "guest", 10000, pid);
	}
	
	@SuppressWarnings("unused")
	private void test() {
		/*
		int nWanted1 = 1;
		int nWanted2 = 3;
		int nAvail1 = 3;
		int nAvail2 = 4;
		*/
		/*
		long t1 = nbP(50, nAvail1);
		long p1 = nbP(5, nWanted1);
		p1 *= nbP(45, nAvail1 - nWanted1);
		
		long t2 = nbP(50-nAvail1, nAvail2);
		long p2 = nbP(5-nWanted1, nWanted2);
		p2 *= nbP(45-(nAvail1-nWanted1), nAvail2-nWanted2);
		
		double x = (double)(p1*p2)/(t1*t2);
		*/
		
		/*
		long t2 = nbP(50, nAvail2);
		long p2 = nbP(5, nWanted2);
		p2 *= nbP(45, nAvail2 - nWanted2);
		
		long t1 = nbP(50-nAvail2, nAvail1);
		long p1 = nbP(5-nWanted2, nWanted1);
		p1 *= nbP(45-(nAvail2-nWanted2), nAvail1-nWanted1);
		
		double x = (double)(p1*p2)/(t1*t2);
		
		
		
		long t12 = nbP(50, nAvail1+nAvail2);
		t12 *= nbP(nAvail1+nAvail2, nAvail2);
		
		long p12 = nbP(5, nWanted1+nWanted2);
		p12 *= nbP(nWanted1+nWanted2, nWanted2);
		p12 *= nbP(45, (nAvail1+nAvail2)-(nWanted1+nWanted2));
		p12 *= nbP((nAvail1+nAvail2)-(nWanted1+nWanted2), nAvail2-nWanted2);
		
		double x12 = (double)p12/t12;
		*/
		
		
		
		// long t = nbP(50,4);
		// long p = nbP(5,2);
		// long v = nbP(45,2);		
		// double x = (double) p*v / t;
		
		
	}
	
	private double bbb(int Nt, int nLeft, int nRight, int nRemDraw) {
		int[] p = listPermutations(nRemDraw, nLeft);
		double res = 0.0;
		for (int i=0; i<p.length; i++) {
			int c = p[i];
			int n=nLeft+nRight;
			double res2 = 1.0;
			int N = Nt;
			for(int j=0; j<nRemDraw; j++) {
				//System.out.println(" n:" + n + " / N-offset:" + (N-offset) + " = " + ((double)n/(N-offset)));
				if ((c&0x01) == 1) {
					System.out.println(" n:" + n + " / N:" + N + " = " + ((double)n/N));
					res2 *= (double) n-- / N--;
				}
				else {
					System.out.println(" N-n:" + (N-n) + " / N:" + N + " = " + ((double)(N-n)/N));
					res2 *= (double) (N-n) / N--;
				}
				c >>= 1;
			}
			res += res2;
			System.out.println("res = " + res);
		}
		return res;
	}

	public int getId() {
		return id;
	}

	@Override
	public int receivesCard(int card) {
		int error = super.receivesCard(card);
		if (error != 0) {
			return error;
		}
		//if (cardsCount() == 2)
		//	evaluatePossibilities();
		return 0;
	}

	@Override
	public void clearAllCards() {
		super.clearAllCards();
		for (int i = 0; i<9; i++) {
			mComboProba[i] = -1;
		}
	}

	public int[] maskFamiliesCards() {
		return maskFamiliesCards(-1, cNum);
	}

	public int[] sumCards() {
		return sumCards(-1, cNum);
	}

	public int[][] sumCardsByFam() {
		return sumCardsByFam(-1, cNum);
	}    
	
	@SuppressWarnings("unused")
	private double ptpWantedList(int[] wList, int nRemDraw, int[]poolSum) {
		int nWanted = 0;
		for(int i=0; i<wList.length; i++) {
			nWanted+=wList[i];
		}
		if (nWanted > nRemDraw) return 0.0;
		double res = 1.0;
		int deckCC = deck.cardsCount();
		int n=0;
		while(nWanted-- != 0) {
			while(wList[n]==0) n++;
			long t = nbP(deckCC, poolSum[n]);
			long p = nbP(nRemDraw, wList[n]) * nbP(deckCC-nRemDraw, poolSum[n]-wList[n]);
			res *= (double)p/t;
			deckCC-=poolSum[n];
			nRemDraw-=wList[n];
		}
		return res;
	}
	
	private double ptpWantedMap(int wMap, int nRemDraw, int[]poolSum) {
		//System.out.println("wMap:" Integer.toBinaryString(wMap));
		int nWanted = CardUt.countOnes(wMap);
		if (nWanted > nRemDraw) return 0.0;
		double res = 1.0;
		int idx = 0;
		int deckCC = deck.cardsCount();
		while(nWanted-- != 0) {
			while((wMap&0x01) != 1) {
				idx++;
				wMap>>=1;
			}
			long t = nbP(deckCC, poolSum[idx]);
			long p = nRemDraw * nbP(deckCC-nRemDraw, poolSum[idx]-1);
			res *= (double)p/t;
			deckCC-=poolSum[idx];
			nRemDraw--;
		}
		return res;
	}
	
	private void evaluateAnyStraightProba(int remDraws) { // evaluate any straight
		int s = cardsCount();
		if (s == 0) {
			return;
		}    
		int[] floptrMask = board.maskFamiliesCards();       
		int[] playerCards = maskFamiliesCards();
		int[] deckMask = deck.maskFamiliesCards();
		int[] deckSum = deck.sumCards();
		int[][] deckSumByFamily = deck.sumCardsByFam();
		
		for (int j=0; j<5; j++) {
			// possibilities sont toutes les cartes par famille d'abord puis tout confondu,
			// dans le deck + celles du joueur + celles du floptr
			int possibilities = (deckMask[j] | playerCards[j] | floptrMask[j]) & CardUt.KMaskVal;
			// TODO rajouter les cartes des autres joueurs si elles sont "cachŽes"
			// si cartes cachŽes, prendre en compte la proba que les autres joueurs n'ont pas eu la carte

			if ((possibilities & CardUt.KQJ10A) == CardUt.KQJ10A) {
				int nWantedMask = (possibilities & CardUt.KQJ10A) & ~(playerCards[j] | floptrMask[j]);
				if (j != 4)
					// royal flush : best straight + same color
					mComboProba[8] += ptpWantedMap(nWantedMask, remDraws, deckSumByFamily[j]);
						
						//ptpAllWExact(nWantedMask, remDraws);
				else
					//mComboProba[4] += ptp1SpecFromAnySuitExact(nWantedMask,remDraws,deckSum);
					mComboProba[4] += ptpWantedMap(nWantedMask,remDraws,deckSum);
			}
			if ((possibilities & CardUt.K5432A) == CardUt.K5432A) {
				int wantedInDeckMask = (possibilities & CardUt.K5432A) & ~(playerCards[j] | floptrMask[j]);
				if (j != 4)
					// straight flush
					//mComboProba[7] += ptpAllWExact(wantedInDeckMask, remDraws);
					mComboProba[7] += ptpWantedMap(wantedInDeckMask, remDraws, deckSumByFamily[j]);
				else
					// straight
					//mComboProba[4] += ptp1SpecFromAnySuitExact(wantedInDeckMask,remDraws,deckSum);
					mComboProba[4] += ptpWantedMap(wantedInDeckMask,remDraws,deckSum);
			}

			int straightMask = CardUt.KMaskStraight;
			for (int k=0; k<8; k++) {
				straightMask <<= 1;
				// applique le masque de la suite ..11111..
				int eval = possibilities & straightMask;
				if (straightMask != eval) {
					// il manque des cartes, elles ont ŽtŽ distribuŽes ˆ d'autres joueur // TODO ˆ revoir avec "cachŽ"
					continue;
				}
				eval = (deckMask[j] & ~(playerCards[j] | floptrMask[j])) & straightMask;
				// eval represents the wanted cards map
				if (j == 4) {
					// toutes familles confondues
					// straight
					mComboProba[4] += ptpWantedMap(eval, remDraws, deckSum);
				}
				else {
					// straight flush
					// calcule le nombre de cartes maquantes dans             
					mComboProba[7] += ptpWantedMap(eval, remDraws, deckSumByFamily[j]);
				}
			}
		}
		// enleve les probas redondantes
		mComboProba[4] -= mComboProba[7];
	}

	/**
	 * [11220|0001100002200000...]
	 * step 1 - calculate nb permutations for [11110|0001100001100000...]
	 * step 2 - calculate nb permutations for [22112211...]
	 * step 3 - return intersection
	 * @param N
	 * @param n1
	 * @param n2
	 * @return
	 */
	@SuppressWarnings("unused")
	private long nPerm2Char(int N, int n1, int n2) {
		long r1 = nbP(N, n1+n2);
		long r2 = nbP(n1+n2, Math.min(n1,n2));
		return r1*r2;
	}
	
	private int[] listPermutations(int N, int n) {
		if (N > 5 || n > 5) { System.err.println("listPermutations - args too big"); return null;}
		long s = nbP(N, n);
		int[] r = new int[(int) s];
		printPermutationsNoCache(N, n, r, 0);
		return r;
	}

	private void printPermutationsNoCache(int N, int n, int[] res, int start) {
		if (0>=N || 0>=n) return;
		int idx = start;
		for (int i=(int) nbP(N-1, n-1); i>0; i--)
			res[idx++] |= 1<<(N-1);
		printPermutationsNoCache(N-1, n-1, res, start);
		printPermutationsNoCache(N-1, n, res, idx); // then move the leading 1 to the right 	
	}
		
	private long nbP(int N, int n) {
		/**
		 * n is number of 1
		 * N is total number of cards
		 **/
		//System.out.println("permut[" + N + "][" + n +"]");
		if (N<n || n<0 || ((n>20)&&(N-n > 20))) {System.err.println("nbP ARG NOT GOOD");return 0;}
		//if (N-1 == n) n = 1;
		if (permut[N][n]!=0) return permut[N][n];
		if (N==n || 0==n) return 1;
		if (n==1) {
			permut[N][n] = N;
			return N;
		}
		long res = nbP(N-1, n-1) + // leave the leading 1 on the left side
			nbP(N-1, n); // then move the leading 1 to the right 	
		permut[N][n] = res;
		//System.out.println("permut[" + N + "][" + n +"] = " + res);
		return res;
	}

	private double ptpTheseFavCardsOnFlop(int nWanted,
			int remDraws,
			int nFavourableCardsInDeck,
			int nFavourableCardsInFlop,
			int nForbidnCardInDeck) {
		/**
		 * [1110|1110000000000]
		 **/
		nWanted -= nFavourableCardsInFlop;
		if (nWanted <= 0) {
			return 1.0; // got it
		}
		if (nWanted > remDraws) {
			return 0.0; // no way
		}
		long totalPerm = nbP(deck.cardsCount(), nFavourableCardsInDeck+nForbidnCardInDeck);
		int nCWindow2 = deck.cardsCount() - remDraws;
		long rightP = nbP(nCWindow2, nFavourableCardsInDeck-nWanted+nForbidnCardInDeck);
		long leftP = nbP(remDraws, nWanted);
		return (double)(leftP*rightP) / totalPerm;
	}    

	private double ptpSameValueCardsOnBoard(int nWanted,
			int valueNotToDraw,
			int remDraw,
			int[] SumCInDeck,
			int[] SumCInFlop) {
		if (nWanted > remDraw) {
			return 0.0; // no way
		}
		int forbidn = CardUt.getValBinary(valueNotToDraw);
		double res = 0;
		for (int i=0; i<13; i++) {
			if (i==forbidn)	continue;
			if( SumCInDeck[i] < (nWanted - SumCInFlop[i])) continue; // cards not in deck or board,
			res += ptpTheseFavCardsOnFlop(nWanted, remDraw, SumCInDeck[i], SumCInFlop[i], SumCInDeck[forbidn]);
		}
		return res;
	}

	private void evaluateAnySimilarities(int nRemDraw, int[] deckSum, int[] boardSum) {     
		int s = cardsCount();
		if (s != 2) {
			return;
		}
		// proba pair- double paire - triple - full house - quad
		//Pour chaque carte du joueur, evaluer possib du pair, triple, quad
		int c0 = CardUt.getValBinary(card(0));
		int c1 = CardUt.getValBinary(card(1));
		boolean hasPocket = false;        
		if (c0 == c1) {
			hasPocket = true;
		}

		// merge probaALL
		if (hasPocket) {
			// prob to have 2 cards of same value other than c0 on board
			mP0AnyOtherCard[1] = ptpSameValueCardsOnBoard(2,c0,nRemDraw,deckSum,boardSum);

			mP0AnyOtherCard[2] = ptpSameValueCardsOnBoard(3,c0,nRemDraw,deckSum,boardSum);
			mP0SameCard[0] = ptpTheseFavCardsOnFlop(1,nRemDraw,deckSum[c0],boardSum[c0],0);
			mP0SameCard[1] = ptpTheseFavCardsOnFlop(2,nRemDraw,deckSum[c0], boardSum[c0],0);

			mP1AnyOtherCard[1] = mP0AnyOtherCard[1];
			mP1AnyOtherCard[2] = mP0AnyOtherCard[2];
			mP1SameCard[0] = mP0SameCard[0];
			mP1SameCard[1] = mP0SameCard[1];

			// pocket : win brother
			mComboProba[0] = 1.0; //KMaxProbOrder;

			// double pocket : current pocket & some other pocket
			mComboProba[1] = mP0AnyOtherCard[1];

			// 3 kind : current pocket & 1 same card
			mComboProba[2] = mP0SameCard[0];

			// full house : current pocket & 3 same other cards | current pocket & 1 same card & 2 other same cards
			mComboProba[5] = mP0AnyOtherCard[2] +
			(mP0AnyOtherCard[1] * mP0SameCard[0]);

			// quad : current pocket & 2 samecards
			mComboProba[6] = mP0SameCard[1];

			mCP0[0] = mCP1[0] = mComboProba[0];
			mCP0[1] = mCP1[1] = mComboProba[1];
			mCP0[2] = mCP1[2] = mComboProba[2];
			mCP0[5] = mCP1[5] = mComboProba[5];
			mCP0[6] = mCP1[6] = mComboProba[6];
		}
		else {
			// prob to pick 2 cards of same value other than c0
			mP0AnyOtherCard[1] = ptpSameValueCardsOnBoard(2,c0,nRemDraw,deckSum,boardSum);

			mP0AnyOtherCard[2] = ptpSameValueCardsOnBoard(3,c0,nRemDraw,deckSum,boardSum);
			mP0SameCard[0] = ptpTheseFavCardsOnFlop(1,nRemDraw,deckSum[c0],boardSum[c0],0);
			mP0SameCard[1] = ptpTheseFavCardsOnFlop(2,nRemDraw,deckSum[c0], boardSum[c0],0);

			// pocket : 1 same card
			/// check case C0 == 0
			mCP0[0] = mP0SameCard[0];

			// double pocket : pocket & some other pocket
			mCP0[1] = mCP0[0] * mP0AnyOtherCard[1];

			// 3 kind : 2 same cards
			mCP0[2] = mP0SameCard[1];

			// full house : pocket & 3 same other cards | 3 kind & some other pocket
			mCP0[5] = mCP0[0] * mP0AnyOtherCard[2] +
			mCP0[2] * mP0AnyOtherCard[1];

			// quad : 3 same cards
			mCP0[6] = mP0AnyOtherCard[2] ;

			// same stuff with other card
			mP1AnyOtherCard[1] = ptpSameValueCardsOnBoard(2,c1,nRemDraw,deckSum,boardSum);
			mP1AnyOtherCard[2] = ptpSameValueCardsOnBoard(3,c1,nRemDraw,deckSum,boardSum);
			mP1SameCard[0] = ptpTheseFavCardsOnFlop(1,nRemDraw,deckSum[c1],boardSum[c1],0);
			mP1SameCard[1] = ptpTheseFavCardsOnFlop(2,nRemDraw,deckSum[c1], boardSum[c1],0);

			mCP1[0] = mP1SameCard[0] ;
			mCP1[1] = mCP1[0] * mP1AnyOtherCard[1];
			mCP1[2] = mP1SameCard[1];
			mCP1[5] = mCP1[0] * mP1AnyOtherCard[2] +
			mCP1[2] * mP1AnyOtherCard[1];
			mCP1[6] = mP1AnyOtherCard[2];

			// pocket with 1 | pocket with 2
			mComboProba[0] = mCP0[0] + mCP1[0]; // pair

			// pocket 1 & pocket 2 | double pocket 1 & not double pocket 2 | double pocket 2 & not double pocket 1
			mComboProba[1] = mCP0[0]*mCP1[0] + mCP0[1]*(1f-mCP1[1]) + mCP1[1]*(1f-mCP0[1]); // double pair

			// 3 kind 1 & not pocket 2 | 3 kind 2 & not pocket 1
			mComboProba[2] = mCP0[2]*(1f-mCP1[0]) + mCP1[2]*(1f-mCP0[0]); // triple

			// full house 1 | full house 2 | pocket 1.2 & 3 kind 1.2
			mComboProba[5] = mCP0[5] + mCP1[5] + mComboProba[0] * mComboProba[2]; // full house

			// quad 1 | quad 2
			mComboProba[6] = mCP0[6] + mCP1[6]; // quad
		}
	}
/*
	private void evaluateColor(int nRemDraw, int[] deckSumByFam, int[] boardSumByFam) {
		int s = cardsCount();
		if (s != 2) {
			return;
		}

		int f1 = Unv.getFam10(card(0));
		int f2 = Unv.getFam10(card(1));
		boolean has2CardsSameFamily = false;
		if (f2 == f1) {
			has2CardsSameFamily = true;
		}

		if (has2CardsSameFamily) {
			mComboProba[5] +=
				ptpTheseFavCardsOnFlop(3, nRemDraw, deckSumByFam[f1], boardSumByFam[f1],0);
		}
		else {
			mComboProba[5] +=
				ptpTheseFavCardsOnFlop(4, nRemDraw, deckSumByFam[f1], boardSumByFam[f1],0);
			mComboProba[5] +=
				ptpTheseFavCardsOnFlop(4, nRemDraw, deckSumByFam[f2], boardSumByFam[f2],0);
		}
	}
*/
	public void evaluatePossibilities() {
		int s = cardsCount();
		if (s != 2)	return;
		//int[][] deckSumByFam = deck.sumCardsByFam();
		//int[][] boardSumbyFam = board.sumCardsByFam();
		int[] deckSum = deck.sumCards();
		int[] boardSum = board.sumCards();
		int remaining = 2-s + 5-board.cardsCount();

		if (mComboProba[8]<=0 && mComboProba[7]<=0 && mComboProba[4]<=0) {
			// reset
			mComboProba[8]=0;
			mComboProba[7]=0;
			mComboProba[4]=0;
			evaluateAnyStraightProba(remaining);
		}
		evaluateAnySimilarities(remaining, deckSum, boardSum);
		//evaluateColor(remaining, deckSumByFam, boardSumbyFam);
	}
	
////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
	
	private int pairScore(int[] tab) {
		for(int i=tab.length-1; i>0; i--)
			if (tab[i] > 1)
				return 1<<ScoreUt.SCORE_SHIFT_CAT | i<<ScoreUt.SCORE_SHIFT_SUBSC; // cat|high card
		return 0;
	}
	
	private int twoPairScore(int[] tab) {
		int count=0;
		int shift=4;
		int res=0;
		for(int i=tab.length-1; i>0; i--)
			if (tab[i] > 1) {
				res |= 2<<ScoreUt.SCORE_SHIFT_CAT;
				res |= (i<<ScoreUt.SCORE_SHIFT_SUBSC) << shift; // cat|high card
				shift = 0;
				count++;
			}
		if (count > 1) return res;
		return 0;
	}
	
	private int threeKindScore(int[] tab) {
		for(int i=tab.length-1; i>0; i--)
			if (tab[i] > 2)
				return 3<<ScoreUt.SCORE_SHIFT_CAT | i<<ScoreUt.SCORE_SHIFT_SUBSC; // cat|high card
		return 0;
	}
	
	private int straightScore(int map) {
		if ((map&CardUt.K5432A) == CardUt.K5432A) {
			return 4<<ScoreUt.SCORE_SHIFT_CAT | CardUt.KACEVal <<ScoreUt.SCORE_SHIFT_SUBSC; // cat|ace card
		}
		int straight = CardUt.KQJ10A;
		for (int i=0; i<8; i++) {
			if ((map&straight) == straight) {
				int cardIdx = 1;
				while(straight != 1) {
					cardIdx++;
					straight>>=1;
				}
				return 4<<ScoreUt.SCORE_SHIFT_CAT | cardIdx<<ScoreUt.SCORE_SHIFT_SUBSC; // cat|high card
			}
			straight>>=1;
		}
		return 0;
	}
	
	private int flushScore(int[] maps) {
		for (int i=0; i<4; i++) {
			if (CardUt.countOnes(maps[i]) >= 5) {
				int cardIdx = 1;
				int map = maps[i];
				while(map != 1) {
					cardIdx++;
					map>>=1;
				}
				return 5<<ScoreUt.SCORE_SHIFT_CAT | cardIdx<<ScoreUt.SCORE_SHIFT_SUBSC; // cat|high card
			}
		}
		return 0;
	}
	
	private int fullHouseScore(int[] tab) {
		int count=0;
		int res=0;
		for(int i=tab.length-1; i>0; i--) {
			if (tab[i] == 2) {
				res |= i<<ScoreUt.SCORE_SHIFT_SUBSC; // cat|low card
				count+=2;
			}
			else if (tab[i] > 2) {
				res |= i<<(ScoreUt.SCORE_SHIFT_SUBSC + ScoreUt.SCORE_SHIFT_CARDH); // cat|high card
				count+=3;
			}
		}
		if (count == 5) {
			res = (6<<ScoreUt.SCORE_SHIFT_CAT) | res & ScoreUt.SCORE_MASK_NO_ID_NO_CAT; 
			return res;
		}
		// count 6 : maybe 3 pairs, 2 3kind, 2pairs + 1 3kind 
		return 0;
	}
	
	private int fourKindScore(int[] tab) {
		for(int i=tab.length-1; i>0; i--)
			if (tab[i] > 3)
				return 7<<ScoreUt.SCORE_SHIFT_CAT | i<<ScoreUt.SCORE_SHIFT_SUBSC; // cat|high card
		return 0;
	}
	
	private int sfScore(int[] maps) {
		for (int i=0; i<4; i++) {
			if (CardUt.countOnes(maps[i]) >= 5) {
				int map = maps[i];
				if ((map&CardUt.K5432A) == CardUt.K5432A) {
					return 8<<ScoreUt.SCORE_SHIFT_CAT | CardUt.KACEVal <<ScoreUt.SCORE_SHIFT_SUBSC; // cat|ace card
				}
				if ((map&CardUt.KQJ10A) == CardUt.KQJ10A) {
					return 9<<ScoreUt.SCORE_SHIFT_CAT | CardUt.KACEVal <<ScoreUt.SCORE_SHIFT_SUBSC; // royal straight flush
				}
				int straight = CardUt.KQJ10A >> 1;
				for (int j=0; j<7; j++) {
					if ((map&straight) == straight) {
						int cardIdx = 1;
						while(straight != 1) {
							cardIdx++;
							straight>>=1;
						}
						return 8<<ScoreUt.SCORE_SHIFT_CAT | cardIdx<<ScoreUt.SCORE_SHIFT_SUBSC; // cat|high card
					}
					straight>>=1;
				}
			}
		}
		return 0;
	}
	
	/**
	 * @return [ID 8 | cat 8 | subscore 8 | cardH 4 | cardL 4]
	 */
	public int getFinalScore() {
		if (this.cardsCount()!=2 && this.board.cardsCount()!=5) return 0;
		int v0 = getCardValAtIdx(0); int v1 = getCardValAtIdx(1);
		int score = (v0>=v1)? 
			(v0<<ScoreUt.SCORE_SHIFT_CARDH|v1)<<ScoreUt.SCORE_SHIFT_SUBSC | (v0<<ScoreUt.SCORE_SHIFT_CARDH)|v1 : 
			(v1<<ScoreUt.SCORE_SHIFT_CARDH|v0)<<ScoreUt.SCORE_SHIFT_SUBSC | (v1<<ScoreUt.SCORE_SHIFT_CARDH)|v0; // high card
			
		int[] tab = new int[14];
		tab[getCardValAtIdx(0)]++;
		tab[getCardValAtIdx(1)]++;
		for (int i=0; i<5; i++)	tab[board.getCardValAtIdx(i)]++;
		
		int[] maps = board.maskFamiliesCards();
		maskFamiliesCardsUpdate(-1, cNum, maps);
		
		int t = pairScore(tab);	 	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		t = twoPairScore(tab);	 	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		t = threeKindScore(tab); 	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		t = straightScore(maps[4]);	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		t = flushScore(maps);	 	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		t = fullHouseScore(tab); 	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		t = fourKindScore(tab);	 	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		t = sfScore(maps);		 	if (score < t) score= t | score & ScoreUt.SCORE_MASK_CARDS;
		if (score > 0xffffff) System.err.println("score out of range");
		
		System.out.print("player id" + this.id + this);
		System.out.println("player id" + this.id + 
				" has cat:" + ScoreUt.cat(score) +
				" - "+ScoreUt.description(score) +
				" - subscore:["+ScoreUt.subsc1(score)+"]["+ScoreUt.subsc2(score)+"]\n");
				
		return (this.id << ScoreUt.SCORE_SHIFT_ID) | score & ScoreUt.SCORE_MASK_NO_ID; 
	}
	
	public String toString() {
		return "player" + this.getId() + "\n" + 
			super.toString() + 
			"\tbetting:" 	+ Integer.toString(this.betRound) +  
			" - cash left:" + Integer.toString(this.cash) + "\n";
	}
	
	public boolean isAllIn() {
		return (cash==0 /*&& bet>0*/);
	}
}
