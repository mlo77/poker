package org.vb.pokr.client;

import org.vb.pokr.shared.CardUtShared;

import com.google.gwt.user.client.ui.Image;

public class PokrResUt {
	public static Image getImg(int c) {
		if (-1 == c) return new Image(PokrRes.INSTANCE.cback());
		int f = CardUtShared.getFam10(c);
		switch(f) {
		case 1 :
			return getImgF1(c);
		case 2 :
			return getImgF2(c);
		case 3 :
			return getImgF3(c);
		case 4 :
			return getImgF4(c);
		default :
			return new Image(PokrRes.INSTANCE.cback());
		}
	}
	
	private static Image getImgF1(int c) {
		int v = CardUtShared.getValBinary(c);
		switch (v) {
			case 1 :
				return new Image(PokrRes.INSTANCE.c1h());
			case 2 :
				return new Image(PokrRes.INSTANCE.c2h());
			case 3 :
				return new Image(PokrRes.INSTANCE.c3h());
			case 4 :
				return new Image(PokrRes.INSTANCE.c4h());
			case 5 :
				return new Image(PokrRes.INSTANCE.c5h());
			case 6 :
				return new Image(PokrRes.INSTANCE.c6h());
			case 7 :
				return new Image(PokrRes.INSTANCE.c7h());
			case 8 :
				return new Image(PokrRes.INSTANCE.c8h());
			case 9 :
				return new Image(PokrRes.INSTANCE.c9h());
			case 10 :
				return new Image(PokrRes.INSTANCE.c10h());
			case 11 :
				return new Image(PokrRes.INSTANCE.c11h());
			case 12 :
				return new Image(PokrRes.INSTANCE.c12h());
			case 13 :
				return new Image(PokrRes.INSTANCE.c13h());
			default :
				break;
		}
		return null;
	}
	
	private static Image getImgF2(int c) {
		int v = CardUtShared.getValBinary(c);
		switch (v) {
			case 1 :
				return new Image(PokrRes.INSTANCE.c1s());
			case 2 :
				return new Image(PokrRes.INSTANCE.c2s());
			case 3 :
				return new Image(PokrRes.INSTANCE.c3s());
			case 4 :
				return new Image(PokrRes.INSTANCE.c4s());
			case 5 :
				return new Image(PokrRes.INSTANCE.c5s());
			case 6 :
				return new Image(PokrRes.INSTANCE.c6s());
			case 7 :
				return new Image(PokrRes.INSTANCE.c7s());
			case 8 :
				return new Image(PokrRes.INSTANCE.c8s());
			case 9 :
				return new Image(PokrRes.INSTANCE.c9s());
			case 10 :
				return new Image(PokrRes.INSTANCE.c10s());
			case 11 :
				return new Image(PokrRes.INSTANCE.c11s());
			case 12 :
				return new Image(PokrRes.INSTANCE.c12s());
			case 13 :
				return new Image(PokrRes.INSTANCE.c13s());
			default :
				break;
		}
		return null;		
	}

	private static Image getImgF3(int c) {
		int v = CardUtShared.getValBinary(c);
		switch (v) {
			case 1 :
				return new Image(PokrRes.INSTANCE.c1d());
			case 2 :
				return new Image(PokrRes.INSTANCE.c2d());
			case 3 :
				return new Image(PokrRes.INSTANCE.c3d());
			case 4 :
				return new Image(PokrRes.INSTANCE.c4d());
			case 5 :
				return new Image(PokrRes.INSTANCE.c5d());
			case 6 :
				return new Image(PokrRes.INSTANCE.c6d());
			case 7 :
				return new Image(PokrRes.INSTANCE.c7d());
			case 8 :
				return new Image(PokrRes.INSTANCE.c8d());
			case 9 :
				return new Image(PokrRes.INSTANCE.c9d());
			case 10 :
				return new Image(PokrRes.INSTANCE.c10d());
			case 11 :
				return new Image(PokrRes.INSTANCE.c11d());
			case 12 :
				return new Image(PokrRes.INSTANCE.c12d());
			case 13 :
				return new Image(PokrRes.INSTANCE.c13d());
			default :
				break;
		}
		return null;
	}

	private static Image getImgF4(int c) {
		int v = CardUtShared.getValBinary(c);
		switch (v) {
			case 1 :
				return new Image(PokrRes.INSTANCE.c1c());
			case 2 :
				return new Image(PokrRes.INSTANCE.c2c());
			case 3 :
				return new Image(PokrRes.INSTANCE.c3c());
			case 4 :
				return new Image(PokrRes.INSTANCE.c4c());
			case 5 :
				return new Image(PokrRes.INSTANCE.c5c());
			case 6 :
				return new Image(PokrRes.INSTANCE.c6c());
			case 7 :
				return new Image(PokrRes.INSTANCE.c7c());
			case 8 :
				return new Image(PokrRes.INSTANCE.c8c());
			case 9 :
				return new Image(PokrRes.INSTANCE.c9c());
			case 10 :
				return new Image(PokrRes.INSTANCE.c10c());
			case 11 :
				return new Image(PokrRes.INSTANCE.c11c());
			case 12 :
				return new Image(PokrRes.INSTANCE.c12c());
			case 13 :
				return new Image(PokrRes.INSTANCE.c13c());
			default :
				break;
		}
		return null;
	}
}
