package map.Romindous.Enums;

public enum TileSet {
	
	FULL_WALL(	TileType.WALL, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.WALL}}), 												true, 0, "fullwall"),
	MOST_WALL(	TileType.WALL, 		mkArry(new int[] {0,0,0,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 					false, 0, "mostwall"),
	HALF_WALL(	TileType.WALL, 		mkArry(new int[] {0,0,1,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 					false, 0, "halfwall"),
	CRSS_WALL(	TileType.WALL, 		mkArry(new int[] {0,1,0,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 					false, 0, "crsswall"),
	SOME_WALL(	TileType.WALL, 		mkArry(new int[] {0,1,1,1}, new TileType[] {TileType.WALL}, new TileType[] {TileType.OPEN, TileType.UPSTS}), 					false, 0, "somewall"),
	PILLAR(		TileType.WALL, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.UPSTS}}), 								true, 0, "nonewall"),
	
	OPEN(		TileType.OPEN, 		mkArry(new int[] {0,0,0,0}, 
	new TileType[][] {new TileType[] {TileType.BOX, TileType.DWNSTS, TileType.WALL, TileType.UPSTS, TileType.HGSTS, TileType.OPEN}}), 								true, 0, "open"),
	
	OPEN_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS, TileType.UPSTS}}), 				true, 0, "nonebox"),
	SOME_BOX(	TileType.BOX, 		mkArry(new int[] {0,1,1,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 	false, 0, "somebox"),
	HALF_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,1,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 	false, 0, "halfbox"),
	CRSS_BOX(	TileType.BOX, 		mkArry(new int[] {0,1,0,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 	false, 0, "crssbox"),
	MOST_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,0,1}, new TileType[] {TileType.BOX}, new TileType[] {TileType.OPEN, TileType.UPSTS, TileType.DWNSTS}), 	false, 0, "mostbox"),
	FULL_BOX(	TileType.BOX, 		mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.BOX}}), 													true, 0, "fullbox"),
	
	NONE_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS}}), 								true, 0, "noneup"),
	SOME_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,0,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 	false, 0, "someup"),
	HALF_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,1,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 	false, 0, "halfup"),
	CRSS_UP(	TileType.UPSTS, 	mkArry(new int[] {0,1,0,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 	false, 0, "crssup"),
	MOST_UP(	TileType.UPSTS, 	mkArry(new int[] {0,1,1,1}, new TileType[] {TileType.BOX, TileType.UPSTS}, new TileType[] {TileType.OPEN, TileType.DWNSTS}), 	false, 0, "mostup"),
	FULL_UP(	TileType.UPSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS}}), 								true, 0, "fullup"),
	
	DOWN(		TileType.DWNSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.OPEN, TileType.DWNSTS, TileType.UPSTS, TileType.BOX}}), 	true, 0, "down"),
	
	NONE_HGS(	TileType.HGSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.BOX, TileType.OPEN}}), 									true, 0, "nonehgs"),
	SOME_HGS(	TileType.HGSTS, 	mkArry(new int[] {0,0,1,0}, new TileType[] {TileType.OPEN}, new TileType[] {TileType.HGBOX, TileType.HGSTS}), 					false, 0, "strthgs"),
	HALF_HGS(	TileType.HGSTS, 	mkArry(new int[] {0,0,1,1}, new TileType[] {TileType.OPEN}, new TileType[] {TileType.HGBOX, TileType.HGSTS}), 					false, 0, "halfhgs"),
	CRSS_HGS(	TileType.HGSTS, 	mkArry(new int[] {0,1,0,1}, new TileType[] {TileType.OPEN}, new TileType[] {TileType.HGBOX, TileType.HGSTS}), 					false, 0, "crsshgs"),
	MOST_HGS(	TileType.HGSTS, 	mkArry(new int[] {0,1,1,1}, new TileType[] {TileType.OPEN}, new TileType[] {TileType.HGBOX, TileType.HGSTS}), 					false, 0, "strthgs"),
	FULL_HGS(	TileType.HGSTS, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.HGBOX, TileType.HGSTS}}), 								true, 0, "fullhgs"),
	
	NONE_HGBOX( TileType.HGBOX, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.HGSTS, TileType.HGWALL}}), 								true, 3, "nonebox"),
	SOME_HGBOX(	TileType.HGBOX, 	mkArry(new int[] {1,0,0,0}, new TileType[] {TileType.HGSTS, TileType.HGWALL}, new TileType[] {TileType.HGBOX}), 				false, 3, "somebox"),
	HALF_HGBOX(	TileType.HGBOX, 	mkArry(new int[] {1,1,0,0}, new TileType[] {TileType.HGSTS, TileType.HGWALL}, new TileType[] {TileType.HGBOX}), 				false, 3, "halfbox"),
	CRSS_HGBOX(	TileType.HGBOX, 	mkArry(new int[] {1,0,1,0}, new TileType[] {TileType.HGSTS, TileType.HGWALL}, new TileType[] {TileType.HGBOX}), 				false, 3, "crssbox"),
	MOST_HGBOX(	TileType.HGBOX, 	mkArry(new int[] {1,1,1,0}, new TileType[] {TileType.HGSTS, TileType.HGWALL}, new TileType[] {TileType.HGBOX}), 				false, 3, "mostbox"),
	FULL_HGBOX(	TileType.HGBOX, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.HGBOX}}), 												true, 3, "fullbox"),
	
	NONE_HGWALL(TileType.HGWALL, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.HGSTS, TileType.HGBOX}}), 								true, 3, "nonewall"),
	SOME_HGWALL(TileType.HGWALL, 	mkArry(new int[] {1,0,0,0}, new TileType[] {TileType.HGSTS, TileType.HGBOX}, new TileType[] {TileType.HGWALL}), 				false, 3, "somewall"),
	HALF_HGWALL(TileType.HGWALL, 	mkArry(new int[] {1,1,0,0}, new TileType[] {TileType.HGSTS, TileType.HGBOX}, new TileType[] {TileType.HGWALL}), 				false, 3, "halfwall"),
	CRSS_HGWALL(TileType.HGWALL, 	mkArry(new int[] {1,0,1,0}, new TileType[] {TileType.HGSTS, TileType.HGBOX}, new TileType[] {TileType.HGWALL}), 				false, 3, "crsswall"),
	MOST_HGWALL(TileType.HGWALL, 	mkArry(new int[] {1,1,1,0}, new TileType[] {TileType.HGSTS, TileType.HGBOX}, new TileType[] {TileType.HGWALL}), 				false, 3, "mostwall"),
	FULL_HGWALL(TileType.HGWALL, 	mkArry(new int[] {0,0,0,0}, new TileType[][] {new TileType[] {TileType.HGWALL}}), 												true, 3, "fullwall");
	
	public final TileType[][] frm;
	public final boolean rndmRtt;
	public final TileType org;
	public final String[] schms;
	public final int dY;
	
	private TileSet(final TileType org, final TileType[][] frm, final boolean rndmRtt, final int dY, final String... schms) {
		this.rndmRtt = rndmRtt;
		this.schms = schms;
		this.frm = frm;
		this.org = org;
		this.dY = dY;
	}
	
	private static TileType[][] mkArry(final int[] which, final TileType[]... tps) {
		final TileType[][] rt = new TileType[which.length][];
		for (int j = 0; j < rt.length; j++) {
			rt[j] = tps[which[j]];
		}
		
		return rt;
	}
}
