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
import org.bukkit.World;
import org.bukkit.block.Block;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

import map.Romindous.Enums.TileSet;
import map.Romindous.Enums.TileType;
import net.minecraft.core.BaseBlockPosition;

public class MapBuilder {
	
	public final String nm;
	private boolean ceiling;//is there ceiling on top floor?
	private Material ceilMat;
	private BlockVector3 origin;//the start block for the entire map (most negative X and Z)
	private BlockVector3 mapDims;
	private BlockVector3 cellDims;
//	private static final IBlockData air = 
//		net.minecraft.world.level.block.Block.a(PacketUtils.getNMSIt(new ItemStack(Material.AIR)).c()).n();
	public final LinkedList<PasteSet> sets = new LinkedList<>();

	public static final Material dftCeilMat = Material.SMOOTH_SANDSTONE;
	public static final BlockVector3 dftMapDims = BlockVector3.at(15, 2, 19);
	public static final BlockVector3 dftCellDims = BlockVector3.at(5, 8, 5);
	private static final AffineTransform t3 = new AffineTransform().rotateY(90d), 
		t2 = new AffineTransform().rotateY(180d), 
		t1 = new AffineTransform().rotateY(270d);
	public static final int maxCheckDist = TileType.getNoiseDiff();
	public static final int encodeBits = 6;
	
	public MapBuilder(final String nm) {
		this.nm = nm;
		this.mapDims = dftMapDims;
		this.cellDims = dftCellDims;
		this.ceiling = false;
		this.ceilMat = dftCeilMat;
	}
	
	public MapBuilder(final String nm, final BlockVector3 mapDims, final BlockVector3 cellDims, final Material ceil) {
		this.nm = nm;
		this.mapDims = mapDims;
		this.cellDims = cellDims;
		this.ceiling = ceil != null;
		this.ceilMat = ceiling ? ceil : dftCeilMat;
	}
	
	public void setCeiling(final Material ceil) {
		this.ceilMat = ceil;
	}
	
	public void setMapDims(final int x, final int y, final int z) {
		mapDims = BlockVector3.at(x, y, z);
	}
	
	public void setCellDims(final int x, final int y, final int z) {
		cellDims = BlockVector3.at(x, y, z);
	}
	
	public BlockVector3 getMapDims() {
		return mapDims;
	}
	
	public BlockVector3 getCellDims() {
		return cellDims;
	}
	
	public BlockVector3 getOrigin() {
		return origin;
	}
	
	public void build(final Location cntr, final HashMap<Integer, TileType> tiles) {
		final int dX = mapDims.getX(), dZ = mapDims.getZ();
		final Location loc = cntr.clone().subtract(dX * cellDims.getX() >> 1, 0d, dZ * cellDims.getZ() >> 1);
		origin = BukkitAdapter.asBlockVector(loc);
//		final World w = loc.getWorld();
//		final Chunk ch = loc.getChunk();
//		for (int x = (Math.abs(loc.getBlockX() - cntr.getBlockX()) >> 4) * 2; x >= 0; x --) {
//			for (int z = (Math.abs(loc.getBlockX() - cntr.getBlockX()) >> 4) * 2; x >= 0; x --) {
//				final CompletableFuture<Chunk> cf = w.getChunkAtAsyncUrgently(ch.getX() + x, ch.getZ() + z);
//				cf.thenRun(() -> {
//					try {
//						cf.get().load(false);
//					} catch (InterruptedException | ExecutionException e) {
//						e.printStackTrace();
//					}
//				});
//			}
//		}
		
		Bukkit.getScheduler().runTaskAsynchronously(Main.plug, () -> {
			int[] uts = new int[0];
			Bukkit.getConsoleSender().sendMessage("Generating " + mapDims.getY() + " floors");
			for (int f = 0; f < mapDims.getY(); f++) {
				final HashMap<Integer, TileType> tls = new HashMap<Integer, TileType>();
				if (!tiles.isEmpty()) {
					tls.putAll(tiles);
					tiles.clear();
				}
				for (final int c : uts) {
					tls.put(c, TileType.DWNSTS);
				}
				uts = buildFloor(dX, dZ, origin.add(0, cellDims.getY() * f, 0), tls, f == mapDims.getY() - 1, loc.getWorld());
			}
		});
	}
	
	private int[] buildFloor(final int lenX, final int widZ, final BlockVector3 org, final HashMap<Integer, TileType> tiles, final boolean isTopFloor, final World w) {
		final LinkedList<Integer> cells = new LinkedList<>();
		for (int x = 0; x < lenX; x++) {
			for (int z = 0; z < widZ; z++) {
				cells.add(encd(x, z));
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
				final int ux = Main.sRnd.nextInt(lenX >> 3) + 1, uz = Main.sRnd.nextInt(widZ >> 3) + 1;
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
						encd((lenX >> 1) + ux, (widZ >> 1) + uz),
						encd((lenX >> 1) - ux, (widZ >> 1) - uz)
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
		for (final Integer in : tiles.keySet()) {
			final int z = in >> encodeBits, x = in - (z << encodeBits);
			//Bukkit.getConsoleSender().sendMessage("x-" + x + "z-" + z);
			pasteSet(x, z, tiles, org);
		}
		Bukkit.getConsoleSender().sendMessage("Done async generating a floor");
		
		return upStairCords;
	}

	public void pasteSet(final int X, final int Z, final HashMap<Integer, TileType> tiles, final BlockVector3 org) {
		final TileType[] around = new TileType[4];
		TileType tile = tiles.get(encd(X + 1, Z));
		around[0] = tile == null ? TileType.OPEN : tile;
		tile = tiles.get(encd(X, Z + 1));
		around[1] = tile == null ? TileType.OPEN : tile;
		tile = tiles.get(encd(X - 1, Z));
		around[2] = tile == null ? TileType.OPEN : tile;
		tile = tiles.get(encd(X, Z - 1));
		around[3] = tile == null ? TileType.OPEN : tile;
		
		placeRotateSet(tiles.get(encd(X, Z)), around, org.add(X * cellDims.getX(), 0, Z * cellDims.getZ()));
	}

	private void placeRotateSet(final TileType tile, final TileType[] around, final BlockVector3 loc) {
		for (final TileSet set : TileSet.values()) {//for every set
			if (set.original == tile) {
				final int ln = around.length;//usually 4
				sts : for (int rot = 0; rot < ln; rot++) {//for every possible rotation
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
					//Bukkit.getConsoleSender().sendMessage("added set " + set.toString());
					sets.add(new PasteSet(set, (byte) (set.rotateRnd ? Main.sRnd.nextInt(4) : rot), BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())));
					return;
				}
			}
		}
		//Bukkit.getConsoleSender().sendMessage("loc-" + loc.toString() + ",\nType-" + tile.toString() + " not placed");
		//placeWESet(TileSet.OPEN, 0, loc, ess);
	}

	public void remove(final World w, final int i) {
		final BlockVector3 lm = mapDims.multiply(cellDims);
		for (int x = lm.getX(); x >= 0; x--) {
			for (int y = lm.getY(); y >= 0; y--) {
				for (int z = lm.getZ(); z >= 0; z--) {
					w.getBlockAt(origin.getX() + x, origin.getY() + y - 1, origin.getZ() + z).setType(Material.AIR, false);
				}
			}
		}
		//failsafe
		Bukkit.getScheduler().runTaskLater(Main.plug, () -> {
			if (w.getHighestBlockYAt(0, 0) > 0 && i != 0) {
				Bukkit.getConsoleSender().sendMessage("couldnt remove arena form " + origin.toString() + " trying again");
				remove(w, i - 1);
			}
		}, 10);
	}
	
	public void placeSets(final World w, final BaseBlockPosition A, final int trs) {
		final HashMap<String, Clipboard> clips = new HashMap<>();
		final EditSession session = Main.WEPlugin.newEditSessionBuilder().world(BukkitAdapter.adapt(w)).maxBlocks(-1).build();
		final int cX = cellDims.getX(), cZ = cellDims.getZ();
		session.enableStandardMode();
		
		//load chunkssssss
		for (final BlockVector2 ch : new CuboidRegion(origin, origin.add(mapDims.multiply(cellDims))).getChunks()) {
			w.getChunkAt(ch.getX(), ch.getZ()).load();
		}
		
		//ceiling
		for (int y = ceiling ? mapDims.getY() : mapDims.getY() - 1; y > 0; y--) {
			final int cY = cellDims.getY() * y - 2;
			for (int x = (cX * mapDims.getX()) - 1; x >= 0; x--) {
				for (int z = (cZ * mapDims.getZ()) - 1; z >= 0; z--) {
					w.getBlockAt(origin.getX() + x, origin.getY() + cY, origin.getZ() + z).setType(ceilMat, false);
				}
			}
		}
		
		for (final PasteSet set : sets) {
			try {
				final Block b = w.getBlockAt(set.loc.getX(), set.loc.getY(), set.loc.getZ());
				for (int y = set.ts.height - 1; y >= 0; y--) {
					for (int x = 0; x < cX; x++) {
						for (int z = 0; z < cZ; z++) {
							b.getRelative(x,y,z).setType(set.ts.original.floorMat);
						}
					}
				}
				final String sch = rndElmt(set.ts.schems);
				final Clipboard clip;
				if (clips.containsKey(sch)) {
					clip = clips.get(sch);
				} else {
					final File fl = new File(Main.plug.getDataFolder() + "/schems/" + sch + ".schem");
					if (fl.exists()) {
						try {
							clip = ClipboardFormats.findByFile(fl).getReader(new FileInputStream(fl)).read();
							clips.put(sch, clip);
						} catch (IOException e) {
							e.printStackTrace();
							continue;
						}
					} else {
						Bukkit.getConsoleSender().sendMessage("Schem-" + fl.getName() + " doesn't exist!");
						continue;
					}
				}
				//cl.getRegion().contract(null);
				final ClipboardHolder holder = new ClipboardHolder(clip);
				final BlockVector3 bvc;
				switch (set.rtt) {
				case 3:
					bvc = set.loc.add(0, 0, cZ - 1);
					holder.setTransform(t3);
					break;
				case 2:
					bvc = set.loc.add(cX - 1, 0, cZ - 1);
					holder.setTransform(t2);
					break;
				case 1:
					bvc = set.loc.add(cX - 1, 0, 0);
					holder.setTransform(t1);
					break;
				default:
					bvc = set.loc;
					break;
				}
				Operations.complete(holder.createPaste(session).to(bvc.add(0, set.ts.height, 0)).ignoreAirBlocks(set.ts.ignrAir).build());
			} catch (WorldEditException e) {
				e.printStackTrace();
			}
		}

		//failsafe
		Bukkit.getScheduler().runTaskLater(Main.plug, () -> {
			if (w.getHighestBlockYAt(0, 0) > 0) {
				sets.clear();
				clips.clear();
				session.close();
			} else if (trs != 0) {
				Bukkit.getConsoleSender().sendMessage("not placed, sets n-" + sets.toString());
				/*for (final Entry<String, Clipboard> s : clips.entrySet()) {
					Bukkit.getConsoleSender().sendMessage("clip-" + s.getKey() + " for " + s.getValue().getRegion().getBoundingBox().toString());
				}*/
				placeSets(w, A, trs - 1);
			}
		}, 10);
	}

	private <G> boolean arrayContains(final G[] array, final G elem) {
		for (final G g : array) {
			if (g.equals(elem)) return true; 
		}
		return false;
	}
	
	public static int encd(final int x, final int z) {
		return x + (z << encodeBits);
	}

	public static <G> G rndElmt(G[] arr) {
		return arr[Main.sRnd.nextInt(arr.length)];
	}
}
