package map.Romindous;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;

import map.Romindous.Enums.TileSet;
import map.Romindous.Enums.TileType;

public class MapBuilder {
	
	public final String nm;
	public boolean ceil;
	private BlockVector3 mapDims;
	private BlockVector3 cellDims;
	
	public static final int mxFll = 5;
	
	public MapBuilder(final String nm, final BlockVector3 mapDims, final BlockVector3 cellDims, final boolean ceil) {
		this.nm = nm;
		this.mapDims = mapDims;
		this.cellDims = cellDims;
	}
	
	public void setCeiling(final boolean ceil) {
		this.ceil = ceil;
	}
	
	public void setMapDims(final int x, final int y, final int z) {
		mapDims = BlockVector3.at(x, y, z);
	}
	
	public void setCellDims(final int x, final int y, final int z) {
		cellDims = BlockVector3.at(x, y, z);
	}
	
	public boolean build(final Location cntr) {
		final int dX = mapDims.getX(), dZ = mapDims.getZ();
		final Location loc = cntr.subtract(dX * cellDims.getX() / 2, 0d, dZ * cellDims.getZ() / 2);
		int[] uts = new int[0];
		for (int f = 0; f < mapDims.getY(); f++) {
			final HashMap<Integer, TileType> tls = new HashMap<Integer, TileType>();
			for (final int c : uts) {
				tls.put(c, TileType.DWNSTS);
			}
			uts = buildFloor(dX, dZ, loc, tls, f == mapDims.getY() - 1, Material.SMOOTH_SANDSTONE);
			loc.add(0d, cellDims.getY(), 0d);
		}
		return true;
	}
	
	private int[] buildFloor(final int dX, final int dZ, final Location loc, final HashMap<Integer, TileType> tls, final boolean lst, final Material ceil) {
		final LinkedList<Integer> slts = new LinkedList<>();
		for (int x = 0; x < dX; x++) {
			for (int z = 0; z < dZ; z++) {
				slts.add(encd(x, z));
				final Block b = loc.clone().add(x * 5, cellDims.getY() - 1, z * 5).getBlock();
				//ceiling
				if (this.ceil) {
					for (int xx = 0; xx < cellDims.getX(); xx++) {
						for (int zz = 0; zz < cellDims.getZ(); zz++) {
							b.getRelative(xx, 0, zz).setType(ceil);
						}
					}
				}
				//presets
				if (x == 0 || x == dX - 1) {
					tls.put(encd(x, z), TileType.WALL);
				} else if (z == 0 || z == dZ - 1) {
					tls.put(encd(x, z), TileType.WALL);
				} else if (x == dX / 2 && z > 0 && z < dZ) {
					//tls.put(encd(x, z), TileType.OPEN);
				}
			}
		}
		Collections.shuffle(slts);
		
		//upstairs stairs
		final int[] ups;
		if (lst) {
			ups = new int[0];
		} else {
			while (true) {
				final int ux = Main.sr.nextInt(dX >> 2) + 1, uz = Main.sr.nextInt(dZ >> 2) + 1;
				if (tls.get(encd((dX >> 1) + ux, (dZ >> 1) + uz)) != null) continue;
				if (dX * dZ > 160) {
					ups = new int[] {
						encd((dX >> 1) + ux, (dZ >> 1) + uz),
						encd((dX >> 1) - ux, (dZ >> 1) + uz),
						encd((dX >> 1) + ux, (dZ >> 1) - uz),
						encd((dX >> 1) - ux, (dZ >> 1) - uz)
					};
				} else {
					ups = new int[] {
						encd((dX >> 1) - ux, (dZ >> 1) + uz),
						encd((dX >> 1) + ux, (dZ >> 1) - uz)
					};
				}
				for (final int c : ups) {
					tls.put(c, TileType.HGBOX);
				}
				break;
			}
		}
		
		//filling in the rest
		for (final int crd : slts) {
			if (tls.get(crd) != null) continue; 
			final int Z = crd >> 6, X = crd - (Z << 6);
			final EnumSet<TileType> psbl = EnumSet.copyOf(TileType.gns);
			for (int x = 0; x < dX; x++) {
				for (int z = 0; z < dZ; z++) {
					final int d = Math.abs(X - x) + Math.abs(Z - z);
					if (d < mxFll) {
						final TileType tl = tls.get(encd(x, z));
						if (tl != null) {
							final Iterator<TileType> it = psbl.iterator();
							while (it.hasNext()) {
								final TileType ttt = it.next();
								if (!tl.canPlaceNear(x, z, ttt, d)) {
									//Bukkit.getConsoleSender().sendMessage("excluding-" + ttt.toString() + " d-" + d);
									it.remove();
								}
							}
						}
					}
				}
			}
			
			if (psbl.isEmpty()) {
				return new int[0];
			}
			final TileType tt = (TileType) rndElmt(psbl.toArray());
			//Bukkit.getConsoleSender().sendMessage("final-" + tt.toString());
			tls.put(crd, tt);
		}
		
		final EditSession ess = Main.wep.newEditSessionBuilder().world(BukkitAdapter.adapt(loc.getWorld())).maxBlocks(-1).build();
		ess.enableStandardMode();
		for (final Integer in : tls.keySet()) {
			final int z = in >> 6, x = in - (z << 6);
			pasteSet(x, z, tls, loc.clone(), ess);
		}
		ess.close();
		return ups;
	}

	public void pasteSet(final int X, final int Z, final HashMap<Integer, TileType> tls, final Location org, final EditSession ess) {
		final TileType[] ard = new TileType[4];
		TileType tt = tls.get(encd(X + 1, Z));
		ard[0] = tt == null ? TileType.OPEN : tt;
		tt = tls.get(encd(X, Z + 1));
		ard[1] = tt == null ? TileType.OPEN : tt;
		tt = tls.get(encd(X - 1, Z));
		ard[2] = tt == null ? TileType.OPEN : tt;
		tt = tls.get(encd(X, Z - 1));
		ard[3] = tt == null ? TileType.OPEN : tt;
		
		tt = tls.get(encd(X, Z));
		placeRotateSet(tt, ard, org.add(X * cellDims.getX(), 0d, Z * cellDims.getZ()), ess);
	}

	private void placeRotateSet(final TileType tt, final TileType[] ard, final Location loc, final EditSession ess) {
		for (final TileSet ts : TileSet.values()) {//for every set
			if (ts.org == tt) {
				final int ln = ard.length;
				sts : for (int i = 0; i < ln; i++) {//for every possible rotation
					for (int j = 0; j < ln; j++) {//check if array matches set
						if (!arrCntns(ts.frm[j], ard[j])) {//rotate by 1 if not
							final TileType t = ard[0];
							for (int l = 1; l < ln; l++) {//rotating
								ard[l - 1] = ard[l];
							}
							ard[ln - 1] = t;
							continue sts;
						}
					}
					//getServer().getConsoleSender().sendMessage("\nloc-" + loc.toString() + ",\nType-" + tt.toString() + ", Set-" + ts.toString() + ", Rot-" + i);
					placeWESet(ts, ts.rndmRtt ? Main.sr.nextInt(4) : i, loc, ess);
					return;
				}
			}
		}
		Bukkit.getConsoleSender().sendMessage("loc-" + loc.toString() + ",\nType-" + tt.toString() + " not placed");
		//placeWESet(TileSet.OPEN, 0, loc, ess);
	}

	private <G> boolean arrCntns(final G[] frm, final G ns) {
		for (final G g : frm) {
			if (g.equals(ns)) return true; 
		}
		return false;
	}

	private void placeWESet(final TileSet ts, final int rt, final Location loc, final EditSession ess) {
		final File fl = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "\\WorldEdit\\schematics\\" + rndElmt(ts.schms) + ".schem");
		try {
			if (fl.exists()) {
				final Block b = loc.getBlock();
				for (int y = ts.dY - 1; y >= 0; y--) {
					for (int x = cellDims.getX() - 1; x >= 0; x--) {
						for (int z = cellDims.getZ() - 1; z >= 0; z--) {
							b.getRelative(x,y,z).setType(ts.org.flr);
						}
					}
				}
				final Clipboard cl = ClipboardFormats.findByFile(fl).getReader(new FileInputStream(fl)).read();
				//cl.getRegion().contract(null);
				final ClipboardHolder hld = new ClipboardHolder(cl);
				switch (rt) {
				case 3:
					loc.add(0d, 0d, cellDims.getZ() - 1);
					hld.setTransform(new AffineTransform().rotateY(90d));
					break;
				case 2:
					loc.add(cellDims.getX() - 1, 0d, cellDims.getZ() - 1);
					hld.setTransform(new AffineTransform().rotateY(180d));
					break;
				case 1:
					loc.add(cellDims.getX() - 1, 0d, 0d);
					hld.setTransform(new AffineTransform().rotateY(270d));
					break;
				default:
					break;
				}
				Operations.complete(hld.createPaste(ess).to(BukkitAdapter.asBlockVector(loc).add(0, ts.dY, 0)).ignoreAirBlocks(true).build());
			} else {
				Bukkit.getConsoleSender().sendMessage("loc-" + loc.toString() + ",\nType-" + ts.toString() + " not placed");
			}
		} catch (WorldEditException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <G> G rndElmt(final G... arr) {
		return arr[Main.sr.nextInt(arr.length)];
	}
	
	public static int encd(final int x, final int z) {
		return x + (z << 6);
	}
}
