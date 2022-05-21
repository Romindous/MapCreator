package map.Romindous;

import java.security.SecureRandom;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plug;
	
	public static final SecureRandom sRnd = new SecureRandom();
	public static final WorldEdit WEPlugin = WorldEdit.getInstance();
	
	public void onEnable() {
		plug = this;
		getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onClick(final BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.IRON_BLOCK) {
			final Block bl = e.getBlockPlaced();
			final MapBuilder mb = new MapBuilder("map");
			mb.build(bl.getLocation(), new HashMap<>());
			//usefull stuff
			mb.getCellDims();
			mb.getMapDims();
			mb.getOrigin();
			
			//its here for now, just to demonstrate
		}
	}

	public static double getRndNum(final int i) {
		return sRnd.nextBoolean() ? sRnd.nextInt(i) : -sRnd.nextInt(i);
	}
}
