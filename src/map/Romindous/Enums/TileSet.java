package map.Romindous.Enums;

public enum TileSet {
	
	FULL_WALL(	TileType.WALL, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.WALL}}), 															"fullwall", true),
	MOST_WALL(	TileType.WALL, 		mkArry(new int[] {0,0,0,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 								"mostwall", false),
	HALF_WALL(	TileType.WALL, 		mkArry(new int[] {0,0,1,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 								"halfwall", false),
	CRSS_WALL(	TileType.WALL, 		mkArry(new int[] {0,1,0,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 								"crsswall", false),
	SOME_WALL(	TileType.WALL, 		mkArry(new int[] {0,1,1,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 								"somewall", false),
	PILLAR(		TileType.WALL, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.UPSTS}}), 											"pillar", true),
	OPEN(		TileType.OPEN, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.WALL, TileType.UPSTS, TileType.BOX, TileType.DWNSTS}}), "open", true),
	OPEN_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS, TileType.UPSTS}}), 							"box", true),
	SOME_BOX(	TileType.BOX, 		mkArry(new int[] {0,1,1,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 				"somebox", false),
	HALF_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,1,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 				"halfbox", false),
	CRSS_BOX(	TileType.BOX, 		mkArry(new int[] {0,1,0,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 				"crssbox", false),
	MOST_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,0,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 				"mostbox", false),
	FULL_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.BOX, TileType.UPSTS}}), 												"fullbox", true),
	NONE_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS}}), 											"noneup", true),
	SOME_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,0,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 				"someup", false),
	HALF_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,1,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 				"halfup", false),
	CRSS_UP(	TileType.UPSTS, 	mkArry(new int[] {0,1,0,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 				"crssup", false),
	MOST_UP(	TileType.UPSTS, 	mkArry(new int[] {0,1,1,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 				"mostup", false),
	FULL_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS}}), 											"fullup", true),
	DOWN(		TileType.DWNSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS, TileType.UPSTS, TileType.BOX}}), 				"down", true);
	
	public final TileType[][] frm;
	public final boolean rndmRtt;
	public final TileType org;
	public final String schm;
	
	private TileSet(final TileType org, final TileType[][] frm, final String schm, final boolean rndmRtt) {
		this.rndmRtt = rndmRtt;
		this.schm = schm;
		this.frm = frm;
		this.org = org;
	}
	
	private static TileType[][] mkArry(final int[] which, final TileType[]... tps) {
		final TileType[][] rt = new TileType[which.length][];
		for (int j = 0; j < rt.length; j++) {
			rt[j] = tps[which[j]];
		}
		
		return rt;
	}
}
