package secret.behind.paintings;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SecretSwitchesBehindPaintingsMain extends JavaPlugin 
{
	Logger logger = Logger.getLogger("heeeeey");
	Map<String, String> paintingOwner = new HashMap<String, String>(); //first string is painting.toString() + painting.Location().toString()
																		//second string is owner.toString()
	public boolean verbose = false;
	
	public void onEnable()
	{
		logger.info("secret switches behind paintings enabled");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new SecretSwitchesBehindPaintingsListener(this),this);
	}
	
	public void onDisable()
	{
		logger.info("secret switches behind paintings disabled!");
	}
}
