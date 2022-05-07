package map.Romindous;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;

public class Main extends JavaPlugin implements Listener {
	
	public static final SecureRandom sr = new SecureRandom();
	public static final ItemStack air = new ItemStack(Material.AIR);
	public static final WorldEdit wep = WorldEdit.getInstance();
	public static final MapBuilder mbd = new MapBuilder("map", BlockVector3.at(21, 1, 28), BlockVector3.at(5, 8, 5), false);
	public static EditSession ess;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onClick(final BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.IRON_BLOCK) {
			final Block bl = e.getBlockPlaced();
			mbd.build(bl.getLocation());
			
		} else if (e.getBlock().getType() == Material.RAW_GOLD_BLOCK) {
			try {
				final File fl = new File(getServer().getPluginsFolder().getAbsolutePath() + "\\WorldEdit\\schematics\\cut.schem");
				final ClipboardHolder hld = new ClipboardHolder(ClipboardFormats.findByFile(fl).getReader(new FileInputStream(fl)).read());
				hld.setTransform(new AffineTransform().rotateY(90.0d));
				final EditSession ess = wep.newEditSessionBuilder().world(BukkitAdapter.adapt(e.getBlock().getWorld())).maxBlocks(-1).build();
				Operations.complete(hld.createPaste(ess).to(BukkitAdapter.asBlockVector(e.getBlockPlaced().getLocation())).ignoreAirBlocks(true).build());
				ess.close();
			} catch (IOException | WorldEditException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static double getRndNum(final int i) {
		return sr.nextBoolean() ? sr.nextInt(i) : -sr.nextInt(i);
	}
}
