package map.Romindous.Enums;

import java.util.EnumSet;
import java.util.Iterator;

import org.bukkit.Material;

public enum TileType {
	
	WALL(true, Material.SANDSTONE, -1, 0),
	OPEN(true, Material.SANDSTONE, 0, 0),
	BOX(true, Material.SANDSTONE, 1, 0),
	UPSTS(false, Material.SANDSTONE, 1, 0),
	DWNSTS(false, Material.SANDSTONE, 1, 0),
	HGSTS(true, Material.SANDSTONE, 0, 1),
	HGBOX(true, Material.SANDSTONE, 0, 2),
	HGWALL(true, Material.SANDSTONE, 0, 3);
	
	public final boolean gnrt;
	public final Material flr;
	public final int[] ns;
	
	public static final EnumSet<TileType> gns = getGens(EnumSet.allOf(TileType.class));
	
	private TileType(final boolean gnrt, final Material flr, final int... ns) {
		this.ns = ns;
		this.flr = flr;
		this.gnrt = gnrt;
	}
	
	private static EnumSet<TileType> getGens(final EnumSet<TileType> all) {
		final Iterator<TileType> it = all.iterator();
		while (it.hasNext()) {
			if (!it.next().gnrt) it.remove();
		}
		return all;
	}
	
	public boolean canPlaceNear(final int x, final int z, final TileType tt, final int dst) {
		final int[] fst = ns;
		final int[] scd = tt.ns;
		final int ln = fst.length;
		if (ln != scd.length) {
			return false;
		}
		//Bukkit.getConsoleSender().sendMessage("comparing t-" + Arrays.toString(ns) + " to t-" + Arrays.toString(tt.ns) + " ");
		int df = 0;
		for (int i = 0; i < ln; i++) {
			df += Math.abs(fst[i] - scd[i]);
		}
		//Bukkit.getConsoleSender().sendMessage("df-" + df + " dst-" + dst);
		return df <= dst;
	}
}
