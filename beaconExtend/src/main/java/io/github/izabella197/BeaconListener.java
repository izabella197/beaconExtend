package io.github.izabella197;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.Beacon;
import org.bukkit.event.player.PlayerInteractEvent;

public class BeaconListener implements Listener {
    private EffectsManager effectsManager;

    BeaconListener(EffectsManager effectsManager){
        this.effectsManager = effectsManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.BEACON) return;

        Beacon beacon = (Beacon) event.getClickedBlock().getState();
        if (beacon.getTier() >= 4) {
            effectsManager.addEffectToFile(beacon);
        }
        return;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (event.getBlock().getType() != Material.BEACON) return;

        Beacon beacon = (Beacon) event.getBlock().getState();
        effectsManager.removeEffectFromFile(beacon);
    }
}
