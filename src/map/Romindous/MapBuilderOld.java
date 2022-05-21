package map.Romindous;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

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

public class MapBuilderOld {
	
	public final String nm;
	public boolean ceil;
	private BlockVector3 mapDims;
	private BlockVector3 cellDims;
	
	public static final int maxCheckDist = TileType.getNoiseDiff();
	public static final int encodeBits = 6;
	
	public MapBuilderOld(final String nm, final BlockVector3 mapDims, final BlockVector3 cellDims, final boolean ceil) {
		this.nm = nm;
		this.mapDims = mapDims;
		this.cellDims = cellDims;
		this.ceil = true;
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
			uts = buildFloor(dX, dZ, loc, tls, f == mapDims.getY() - 1, Material.STONE_BRICKS);
			loc.add(0d, cellDims.getY(), 0d);
		}
		return true;
	}
	
	private int[] buildFloor(final int lenX, final int widZ, final Location loc, final HashMap<Integer, TileType> tiles, final boolean isTopFloor, final Material ceiling) {
		final LinkedList<Integer> cells = new LinkedList<>();
		for (int x = 0; x < lenX; x++) {
			for (int z = 0; z < widZ; z++) {
				cells.add(encd(x, z));
				final Block b = loc.clone().add(x * 5, cellDims.getY() - 2, z * 5).getBlock();
				//ceiling
				if (this.ceil && !isTopFloor) {
					for (int xx = 0; xx < cellDims.getX(); xx++) {
						for (int zz = 0; zz < cellDims.getZ(); zz++) {
							b.getRelative(xx, 0, zz).setType(ceiling);
						}
					}
				}
				//presets
				if (x == 0 || x == lenX - 1) {
					tiles.put(encd(x, z), TileType.WALL);
				} else if (z == 0 || z == widZ - 1) {
					tiles.put(encd(x, z), TileType.WALL);
				} else if (x == lenX / 2 && z > 0 && z < widZ) {
					//tls.put(encd(x, z), TileType.OPEN);
				}
			}
		}
		Collections.shuffle(cells);
		
		//upstairs stairs
		final int[] upStairCords;
		if (isTopFloor) {
			upStairCords = new int[0];
		} else {
			while (true) {
				final int ux = Main.sRnd.nextInt(lenX >> 2) + 1, uz = Main.sRnd.nextInt(widZ >> 2) + 1;
				if (tiles.get(encd((lenX >> 1) + ux, (widZ >> 1) + uz)) != null) continue;
				if (lenX * widZ > 160) {
					upStairCords = new int[] {
						encd((lenX >> 1) + ux, (widZ >> 1) + uz),
						encd((lenX >> 1) - ux, (widZ >> 1) + uz),
						encd((lenX >> 1) + ux, (widZ >> 1) - uz),
						encd((lenX >> 1) - ux, (widZ >> 1) - uz)
					};
				} else {
					upStairCords = new int[] {
						encd((lenX >> 1) - ux, (widZ >> 1) + uz),
						encd((lenX >> 1) + ux, (widZ >> 1) - uz)
					};
				}
				for (final int c : upStairCords) {
					tiles.put(c, TileType.UPSTS);
				}
				break;
			}
		}
		
		//filling in the rest
		for (final int coords : cells) {
			if (tiles.get(coords) != null) continue; 
			final int Z = coords >> encodeBits, X = coords - (Z << encodeBits);
			final EnumSet<TileType> possible = EnumSet.copyOf(TileType.gns);
			for (final Entry<Integer, TileType> en : tiles.entrySet()) {
				final int z = en.getKey() >> encodeBits, x = en.getKey() - (z << encodeBits);
				final int d = Math.abs(X - x) + Math.abs(Z - z);
				if (d < maxCheckDist) {
					final TileType tileAtXZ = en.getValue();
					final Iterator<TileType> it = possible.iterator();
					while (it.hasNext()) {
						final TileType possibility = it.next();
						if (!tileAtXZ.canPlaceNear(possibility, d)) {
							//Bukkit.getConsoleSender().sendMessage("excluding-" + ttt.toString() + " d-" + d);
							it.remove();
						}
					}
				}
			}
			
			if (possible.isEmpty()) {
				return new int[0];//fallback
			}
			//Bukkit.getConsoleSender().sendMessage("final-" + tt.toString());
			tiles.put(coords, (TileType) rndElmt(possible.toArray()));
		}
		
		final EditSession session = Main.WEPlugin.newEditSessionBuilder().world(BukkitAdapter.adapt(loc.getWorld())).maxBlocks(-1).build();
		session.enableStandardMode();
		for (final Integer in : tiles.keySet()) {
			final int z = in >> encodeBits, x = in - (z << encodeBits);
			pasteSet(x, z, tiles, loc.clone(), session);
		}
		session.close();
		return upStairCords;
	}

	public void pasteSet(final int X, final int Z, final HashMap<Integer, TileType> tiles, final Location origin, final EditSession session) {
		final TileType[] around = new TileType[4];
		TileType tile = tiles.get(encd(X + 1, Z));
		around[0] = tile == null ? TileType.OPEN : tile;
		tile = tiles.get(encd(X, Z + 1));
		around[1] = tile == null ? TileType.OPEN : tile;
		tile = tiles.get(encd(X - 1, Z));
		around[2] = tile == null ? TileType.OPEN : tile;
		tile = tiles.get(encd(X, Z - 1));
		around[3] = tile == null ? TileType.OPEN : tile;
		
		placeRotateSet(tiles.get(encd(X, Z)), around, origin.add(X * cellDims.getX(), 0d, Z * cellDims.getZ()), session);
	}

	private void placeRotateSet(final TileType tile, final TileType[] around, final Location loc, final EditSession session) {
		for (final TileSet set : TileSet.values()) {//for every set
			if (set.original == tile) {
				final int ln = around.length;//usually 4
				sts : for (int rotation = 0; rotation < ln; rotation++) {//for every possible rotation
					for (int j = 0; j < ln; j++) {//check if array matches set
						if (!arrayContains(set.form[j], around[j])) {//rotate by 1 if not
							final TileType first_Last = around[0];
							for (int i = 1; i < ln; i++) {//rotating
								around[i - 1] = around[i];
							}
							around[ln - 1] = first_Last;
							continue sts;
						}
					}
					//getServer().getConsoleSender().sendMessage("\nloc-" + loc.toString() + ",\nType-" + tt.toString() + ", Set-" + ts.toString() + ", Rot-" + i);
					placeWESet(set, set.rotateRnd ? Main.sRnd.nextInt(4) : rotation, loc, session);
					return;
				}
			}
		}
		Bukkit.getConsoleSender().sendMessage("loc-" + loc.toString() + ",\nType-" + tile.toString() + " not placed");
		//placeWESet(TileSet.OPEN, 0, loc, ess);
	}

	private <G> boolean arrayContains(final G[] array, final G elem) {
		for (final G g : array) {
			if (g.equals(elem)) return true; 
		}
		return false;
	}

	private void placeWESet(final TileSet set, final int rotation, final Location loc, final EditSession session) {
		final File fl = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "\\WorldEdit\\schematics\\" + rndElmt(set.schems) + ".schem");
		try {
			if (fl.exists()) {
				final Block b = loc.getBlock();
				for (int y = set.height - 1; y >= 0; y--) {
					for (int x = cellDims.getX() - 1; x >= 0; x--) {
						for (int z = cellDims.getZ() - 1; z >= 0; z--) {
							b.getRelative(x,y,z).setType(set.original.floorMat);
						}
					}
				}
				final Clipboard clip = ClipboardFormats.findByFile(fl).getReader(new FileInputStream(fl)).read();
				//cl.getRegion().contract(null);
				final ClipboardHolder holder = new ClipboardHolder(clip);
				switch (rotation) {
				case 3:
					loc.add(0d, 0d, cellDims.getZ() - 1);
					holder.setTransform(new AffineTransform().rotateY(90d));
					break;
				case 2:
					loc.add(cellDims.getX() - 1, 0d, cellDims.getZ() - 1);
					holder.setTransform(new AffineTransform().rotateY(180d));
					break;
				case 1:
					loc.add(cellDims.getX() - 1, 0d, 0d);
					holder.setTransform(new AffineTransform().rotateY(270d));
					break;
				default:
					break;
				}
				Operations.complete(holder.createPaste(session).to(BukkitAdapter.asBlockVector(loc).add(0, set.height, 0)).ignoreAirBlocks(true).build());
			} else {
				Bukkit.getConsoleSender().sendMessage("loc-" + loc.toString() + ",\nType-" + set.toString() + " not placed");
			}
		} catch (WorldEditException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <G> G rndElmt(final G... arr) {
		return arr[Main.sRnd.nextInt(arr.length)];
	}
	
	public static int encd(final int x, final int z) {
		return x + (z << encodeBits);
	}
}
