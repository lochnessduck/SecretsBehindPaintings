package secret.behind.paintings;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerInteractEntityEvent;


public class SecretSwitchesBehindPaintingsListener implements Listener
{
	SecretSwitchesBehindPaintingsMain plugin;
	
	//public interface to allow us to get the SecretSwitchesBehindPaintingsMain plugin stored
	public SecretSwitchesBehindPaintingsListener(SecretSwitchesBehindPaintingsMain ssbpm)
	{
		plugin = ssbpm;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	@EventHandler
	public void onPaintingRightClick(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity painting = event.getRightClicked(); //note NOT left clicked. right clicks do nothing to a painting
		if (painting instanceof Painting)
		{
			//then we clicked a painting! now we should see if there's a sign directly behind it in our view.
			if (plugin.verbose) plugin.logger.info("painting found.. gettting line of sight");
			HashSet<Byte> transparent = new HashSet<Byte>(); //got this three lines from     http://pastebin.com/iEbFCPVE
	        transparent.add((byte) Material.AIR.getId());
	        transparent.add((byte) Material.SIGN_POST.getId());
	        //hint normal reach distance of a player is 4 blocks long
			List<Block> blocks = player.getLineOfSight(transparent, 4);//org.bukkit.entity.LivingEntity.getLineOfSite(); //tell it to ignore those blocks you deem transparent
			if (plugin.verbose) plugin.logger.info("used blockIterator");
			Block newblock = getLeverBlockFromLineOfSight(blocks); //just returned the button or lever or null
			//once we have the block (lever or button), we activate it!
			if (plugin.verbose) plugin.logger.info("just had the lever returned from line of sight");
			if (newblock != null)
			{
				if (plugin.verbose) 
				{
					plugin.logger.info(newblock.toString());
					plugin.logger.info("and about to activate the lever!");
				}
				//got that gem thanks to ZachBora --
				//http://forums.bukkit.org/threads/how-to-set-a-block-powered.53286/
				//http://forums.bukkit.org/threads/casting-craftworld-to-net-minecraft-server-world.78703/
				// ( (CraftWorld) block.getWorld()).getHandle()   //somehow calling getHandle() on a CraftWorld returns the minecraft WorldServer, which is what I need
				//net.minecraft.server.v1_5_R3.Block.byId[newblock.getTypeId()].interact((World) ((CraftHumanEntity)player).getWorld(), newblock.getX(), newblock.getY(), newblock.getZ(), ((CraftHumanEntity)player).getHandle(), 0, 0, 0, 0);
				net.minecraft.server.v1_4_R1.Block.byId[newblock.getTypeId()].interact( ((CraftWorld) newblock.getWorld()).getHandle(), newblock.getX(), newblock.getY(), newblock.getZ(), ((CraftHumanEntity)player).getHandle(), 0, 0, 0, 0);
				//older version   net.minecraft.server.Block.byId[newblock.getTypeId()].interact(((CraftWorld) newblock.getWorld()).getHandle(),newblock.getX(), newblock.getY(), newblock.getZ(),((CraftPlayer)player).getHandle(), 0, 0, 0, 0); //WARNING: this line was recently changed, adding on those 4 0s at the end
				if (plugin.verbose) plugin.logger.info("activated!");
			}
			
		}
		
	}
	
	public Block getLeverBlockFromLineOfSight(List<Block> blocks)
	{ //look in list of line of site blocks. The first instance of a lever (so long as we only prior had paintings and signs?) should get returned.
		if (blocks == null)
		{
			return null;
		}
		int tempi = -1;
		for (int i = 0; i < blocks.size(); i++) 
		{
			Block block = blocks.get(i);
			if (plugin.verbose) plugin.logger.info(block.toString());
			if (block.getTypeId() == Material.SIGN_POST.getId()) //if the item is a sign
			{
				tempi = i;
				if (plugin.verbose) plugin.logger.info("found sign post! ^");
				break; //break out of this loop. We're gonna spend the next amount of time checking for a switch in the line of sight
			}
		}
		//remember reuse i
		if (tempi != -1)
		{
			if (plugin.verbose) 
			{
				plugin.logger.info("now we're going to iterate over next blocks until we run into lever");
				plugin.logger.info(Integer.toString(blocks.size()));
				plugin.logger.info("block size vs current index");
				plugin.logger.info(Integer.toString(tempi));
			}
			for (int j = tempi + 1; j < blocks.size(); j++) //start checking for blocks past the painting and the 1 sign
			{
				Block block = blocks.get(j);
				if (plugin.verbose) plugin.logger.info(block.toString());
				//just iterate until we run into a block that is not air. We will assume that is the proper item we were looking for
//				if ( block.getTypeId() == Material.SIGN_POST.getId())
//				{
//					if (plugin.verbose) plugin.logger.info("we found another sign post");
//					return null; //we don't want to return all these sign posts!
//				}
				if (block.getTypeId() != Material.AIR.getId())//( block.getTypeId() == Material.LEVER.getId() || block.getTypeId() == Material.STONE_BUTTON.getId() || block.getTypeId() == Material.CHEST.getId())
				{
					if (plugin.verbose) 
					{
						plugin.logger.info("got a block! V");
						plugin.logger.info(block.toString());
					}
					return block; //this is the block to activate / interact with
				}
			}
		}
		return null; //or else return null
	}
	
	
}
