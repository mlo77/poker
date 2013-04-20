/**
 * 
 */
package app.main;

/**
 * @author yyc
 *
 */
public class Board extends CardHolder {

	private static int IDCount = 0;
	private int internalID;
	
	/**
	 * 
	 */
	public Board() {
		// TODO Auto-generated constructor stub
		super(5);
		internalID = IDCount++;
	}
	
	public boolean equals(CardHolder p) {
		if (!(p instanceof Board))
			return false;
		return internalID == ((Board)p).internalID;
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
	
	public String toString() {
		return "BOARD:\n" + super.toString();
	}
}
