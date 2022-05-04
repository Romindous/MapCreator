package map.Romindous.Enums;

import java.util.EnumSet;
import java.util.Iterator;

public enum TileType {
	
	WALL(10, 0, true),
	OPEN(8, 1, true),
	BOX(10, 2, true),
	UPSTS(1, 2, false),
	DWNSTS(1, 2, false);
	
	public final int wt;
	public final int ns;
	public final boolean gnrt;
	
	public static final EnumSet<TileType> gns = getGens(EnumSet.allOf(TileType.class));
	
	private TileType(final int wt, final int ns, final boolean gnrt) {
		this.wt = wt;
		this.ns = ns;
		this.gnrt = gnrt;
	}
	
	private static EnumSet<TileType> getGens(final EnumSet<TileType> all) {
		final Iterator<TileType> it = all.iterator();
		while (it.hasNext()) {
			if (!it.next().gnrt) it.remove();
		}
		return all;
	}

	public boolean cantPlace(final TileType tt, final int dst) {
		return Math.abs(ns - tt.ns) > dst;
	}
}
