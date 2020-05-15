package io.github.izabella197;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.block.Beacon;

import java.io.File;
import java.io.IOException;

import static org.bukkit.Bukkit.getOnlinePlayers;

public class EffectsManager extends BukkitRunnable {

    public static float TIER_THREE_RANGE = 500;
    private File file;

    public EffectsManager() {
        file = new File("plugins/beaconExtend/effects.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe("Error: beaconExtend unable to  create effects.yml data file");
                e.printStackTrace();
            }
        }
    }

    public void addEffectToFile(Beacon beacon) {
        FileConfiguration effectData = YamlConfiguration.loadConfiguration(file);
        String beaconId = this.getBeaconId(beacon);

        if (effectData.contains(beaconId)){
            String oldPrimary = effectData.getString(beaconId + ".effectPrim");
            String oldSecondary = effectData.getString(beaconId + ".effectSec");
            if (beacon.getPrimaryEffect() != null && !oldPrimary.equals(beacon.getPrimaryEffect().getType().getName())){
                effectData.set(beaconId + ".effectPrim", beacon.getPrimaryEffect().getType().getName());
            }
            if (beacon.getSecondaryEffect() != null && !oldSecondary.equals(beacon.getSecondaryEffect().getType().getName())){
                effectData.set(beaconId + ".effectSec", beacon.getSecondaryEffect().getType().getName());
            }
        } else{
            effectData.set(beaconId +  ".x", beacon.getLocation().getBlockX());
            effectData.set(beaconId +  ".y", beacon.getLocation().getBlockY());
            effectData.set(beaconId +  ".z", beacon.getLocation().getBlockZ());
            effectData.set(beaconId +  ".world", beacon.getLocation().getWorld().getName());
            if (beacon.getSecondaryEffect() != null) {
                effectData.set(beaconId + ".effectSec", beacon.getSecondaryEffect().getType().getName());
            }
            if (beacon.getPrimaryEffect() != null) {
                effectData.set(beaconId + ".effectPrim", beacon.getPrimaryEffect().getType().getName());
            }
        }

        saveFile(effectData);
    }

    public void removeEffectFromFile(Beacon beacon){
        FileConfiguration effectData = YamlConfiguration.loadConfiguration(file);
        String beaconId = this.getBeaconId(beacon);

        if (effectData.contains(beaconId)) {
            effectData.set(beaconId, null);
        }
        saveFile(effectData);
    }


    private void saveFile(FileConfiguration fileConfig){
        try{
            fileConfig.save(file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getBeaconId(Beacon beacon){
        String worldName = beacon.getLocation().getWorld().getName();

        return ""+ worldName + beacon.getLocation().getBlockX() +
                beacon.getLocation().getBlockY() +
                beacon.getLocation().getBlockZ();
    }

    @Override
    public void run(){
        loadBeaconEffects();
    }

    public Beacon getBeacon(FileConfiguration effectData, String beaconId){
        int bX = effectData.getInt(beaconId + ".x");
        int bY = effectData.getInt(beaconId + ".y");
        int bZ = effectData.getInt(beaconId + ".z");
        String world  = effectData.getString(beaconId + ".world");
        Location bLocation = new Location(Bukkit.getWorld(world), bX, bY, bZ);
        BlockState blockState = bLocation.getBlock().getState();
        if (blockState instanceof Beacon){
            return (Beacon) blockState;
        } else {
            return null;
        }
    }

    protected Boolean checkPyramidLayers(Beacon beacon){
        Boolean isValid = true;
        Location location = beacon.getLocation();
        Location checking = beacon.getLocation();
        //tier 5 11x11
        if (beacon.getTier() >= 4) {
            for (double i = -5; i <= 5; i += 1) {
                for (double j = -5; j <= 5; j += 1) {
                    checking.setX(location.getX() + (i));
                    checking.setY(location.getY() -  5.0);
                    checking.setZ(location.getZ() + (j));

                    if (checking.getBlock().getType() != Material.DIAMOND_BLOCK) {
                        isValid = false;
                        break;
                    }
                }
            }
        }
        return isValid;
    }


    protected Boolean checkPlayerInRange(Beacon beacon, Player player, float range){
        Location bLocation = beacon.getLocation();
        Location pLocation = player.getLocation();

        if ((Math.abs(pLocation.getBlockX() - bLocation.getBlockX()) <= range) &&
                (Math.abs(pLocation.getBlockY() - bLocation.getBlockY()) <= range) &&
                (Math.abs(pLocation.getBlockZ() - bLocation.getBlockZ()) <= range)){
            return true;
        }
        return false;
    }

    public void loadBeaconEffects(){
        FileConfiguration effectData =  YamlConfiguration.loadConfiguration(file);

        for (Player p : getOnlinePlayers()){

            for (String beaconId : effectData.getKeys(false)) {
                Beacon beacon = getBeacon(effectData,  beaconId);

                if (checkPyramidLayers(beacon) && checkPlayerInRange(beacon, p, TIER_THREE_RANGE)) {

                    if (beacon.getPrimaryEffect() != null){
                        p.addPotionEffect(beacon.getPrimaryEffect());
                    }
                    if (beacon.getSecondaryEffect() != null) {
                        p.addPotionEffect(beacon.getSecondaryEffect());
                    }
                }
            }
        }
        return;
    }



}
