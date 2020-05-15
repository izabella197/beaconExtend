package io.github.izabella197;
import org.bukkit.plugin.java.JavaPlugin;

public class BeaconExtend extends JavaPlugin {

    private EffectsManager effectsManager;
    private static int DURATION = 20 * 5;

    // remove later as inherited
    @Override
    public void onEnable() {

        effectsManager = new EffectsManager();
        effectsManager.loadBeaconEffects();
        effectsManager.runTaskTimer(this, 0, DURATION);

        getServer().getPluginManager().registerEvents(new BeaconListener(effectsManager), this);
    }
    @Override
    public void onDisable() {
    }



}
