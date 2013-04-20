package app.main;

import java.lang.Integer;

public class CardUt {
	// 2  -> 0000 0000 0000 0001
	// 3  -> 0000 0000 0000 0010
	// ...
	// 10 -> 0000 0001 0000 0000
	// J  -> 0000 0010 0000 0000
	// Q  -> 0000 0100 0000 0000
	// K  -> 0000 1000 0000 0000
	// A  -> 0001 0000 0000 0000
	public static final int KACEVal = 13; 			// 0001 0000 0000 0000
	public static final int KMaskStraight = 0x1f; 	// 0000 0000 0001 1111
	public static final int KQJ10A = 0x1f00; 		// 0001 1111 0000 0000
	public static final int K5432A = 0x100f; 		// 0001 0000 0000 1111
	public static final int KMaskVal   = 0x00001fff; // 0000 0000 0000 0000 0001 1111 1111 1111
	public static final int KMaskFam   = 0x0001e000; // 0000 0000 0000 0001 1110 0000 0000 0000
	public static final int KMaskFam10 = 0x00060000; // 0000 0000 0000 0110 0000 0000 0000 0000
	public static final int KMaskIdx   = 0x01F80000; // 0000 0001 1111 1000 0000 0000 0000 0000
	public static final int KFamShift = 13;
	public static final int KFam10Shift = 17;
	public static final int KIdxShift = 19;
	public static final int UNVDECKBITFLAG = 0x40000000; // 0100 0000 0000 0000 0000 0000 0000 0000
	
	public static String getValChar(int c) {
		switch (c) {
		case KACEVal :
			return "A";
		case 12 :
			return "K";
		case 11 :
			return "Q";
		case 10 :
			return "J";
		default :
			return (new Integer(c+1)).toString();
		}
	}
	
	public static String getValBinChar(int c) {
		int v = getValBinary(c);
		switch (v) {
		case KACEVal :
			return "A";
		case 12 :
			return "K";
		case 11 :
			return "Q";
		case 10 :
			return "J";
		default :
			return (new Integer(v+1)).toString();
		}
	}
	
	public static int getValBinary(int c) {
		int valCard = 1;
		int temp = c & CardUt.KMaskVal;
		int dbg = temp & (temp-1);
		if (dbg != 0) System.err.println("bar arg Unv.getVal");
		while (temp != 1) {
			temp >>= 1;
			valCard++;
		}
		return valCard;
	}
	
	public static int getFam(int c) {
		int fam = 0;
		int res = (c & CardUt.KMaskFam) >> CardUt.KFamShift;
		int d = res;
		while (d != 0) {
			d >>= 1;
			fam++;
		}
		int fam10 = getFam10(c);
		assert (fam10 == fam);
		if (fam10 != fam)
			System.err.println("not good (fam10 != fam)");
		return res;
	}
	
	public static int getDeckIdx(int c) {
		int d = (c & CardUt.KMaskIdx) >> CardUt.KIdxShift;
		if (d < 0 || d > 51)
			System.err.println("getIdx out of range");
		return (c & CardUt.KMaskIdx) >> CardUt.KIdxShift;	
	}
	
	public static int getFam10(int c) {
		int dbg = ((c & CardUt.KMaskFam10) >> CardUt.KFam10Shift) + 1;
		if (dbg < 1 || dbg > 4)
			System.err.println("getFam10 out of range");
		return ((c & CardUt.KMaskFam10) >> CardUt.KFam10Shift) + 1;
	}
	
	public static int countOnes(long val) {
		int r = 0;
		while(val != 0) {
			val &= val-1;
			r++;
		}
		return r;
	}
	
	public static String getFamName(int c) {
		switch (getFam10(c)) {
		case 1 :
			return "Heart";
		case 2 :
			return "Spade";
		case 3 :
			return "Diamond";
		case 4 :
			return "Clubs";
		default :
			System.err.println("getFamName out of range");
			return null;
		}
	}
}
