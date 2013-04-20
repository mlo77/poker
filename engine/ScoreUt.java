package app.main;

import java.util.Comparator;

public class ScoreUt implements Comparator<Player> {

	// [ID 8 | cat 8 | subscore 8 | cardH 4 | cardL 4]
	public static final int SCORE_SHIFT_ID = 24;
	public static final int SCORE_SHIFT_CAT = 16;
	public static final int SCORE_SHIFT_SUBSC = 8;
	public static final int SCORE_SHIFT_CARDH = 4;
	public static final int SCORE_MASK_CARDS = 0xff;
	public static final int SCORE_MASK_CAT = 0xff << SCORE_SHIFT_CAT;
	public static final int SCORE_MASK_SUBSC = 0xff << SCORE_SHIFT_SUBSC;
	public static final int SCORE_MASK_NO_ID = 0xffffff;
	public static final int SCORE_MASK_NO_ID_NO_CAT = 0xffff;

	static public int cat(int v) {
		return (v & ScoreUt.SCORE_MASK_CAT) >> ScoreUt.SCORE_SHIFT_CAT;
	}
	
	static public int subsc(int v) {
		return (v & ScoreUt.SCORE_MASK_SUBSC) >> ScoreUt.SCORE_SHIFT_SUBSC;
	}
	
	static public int subsc1(int v) {
		return ((v>>(ScoreUt.SCORE_SHIFT_SUBSC+ScoreUt.SCORE_SHIFT_CARDH))&0xf);
	}
	
	static public int subsc2(int v) {
		return ((v>>(ScoreUt.SCORE_SHIFT_SUBSC))&0xf);
	}
	
	static public int no_id(int v) {
		return (v & ScoreUt.SCORE_MASK_NO_ID);
	}
	
	public static int _compare(int v0, int v1) {
		int cat0 = cat(v0);
		int cat1 = cat(v1);
		if (cat0!=cat1) return (cat0-cat1)<<8;
		if (cat0 > 3 && cat0 != 7) {
			// for straight, flush, fullhouse, sf only
			// winning hand is determined by 5 cards, those hands characterize fully
			return subsc(v0)-subsc(v1);
		}
		return no_id(v0) - no_id(v1);
	}
	
	public static int cmp(Player arg0, Player arg1) {
		return _compare(arg0.finalScore, arg1.finalScore);
	}
	
	@Override
	public int compare(Player arg0, Player arg1) {
		return _compare(arg0.finalScore, arg1.finalScore);
	}
	
	static public String description(int s) {
		switch (cat(s)) {
		case 0 :
			return "High cards";
		case 1 :
			return "a pair of " + CardUt.getValChar((s>>ScoreUt.SCORE_SHIFT_SUBSC) & 0xf);
		case 2 :
			return "two pairs of " + CardUt.getValChar((s>>ScoreUt.SCORE_SHIFT_SUBSC) & 0xf) +
				" and " + CardUt.getValChar((s>>(ScoreUt.SCORE_SHIFT_SUBSC+ScoreUt.SCORE_SHIFT_CARDH)) & 0xf);
		case 3 :
			return "three of a kind of " + CardUt.getValChar((s>>ScoreUt.SCORE_SHIFT_SUBSC) & 0xf);
		case 4 :
			return "a straight, high of " + CardUt.getValChar((s>>ScoreUt.SCORE_SHIFT_SUBSC) & 0xf);
		case 5 :
			return "a flush";
		case 6 :
			return "the full house";
		case 7 :
			return "four of a kind";
		case 8 :
			return "A STRAIGHT FLUSH !!";
		case 9 :
			return "THE ROYAL STRAIGHT FLUSH !!";
		default :
			System.err.println("getScoreName out of range");
			return null;
		}
	}
}

