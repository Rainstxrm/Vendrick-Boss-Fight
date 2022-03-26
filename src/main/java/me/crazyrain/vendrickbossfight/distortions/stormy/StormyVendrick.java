package me.crazyrain.vendrickbossfight.distortions.stormy;

import me.crazyrain.vendrickbossfight.CustomEvents.VendrickFightStartEvent;
import me.crazyrain.vendrickbossfight.CustomEvents.VendrickStartAttackEvent;
import me.crazyrain.vendrickbossfight.VendrickBossFight;
import me.crazyrain.vendrickbossfight.functionality.ItemManager;
import me.crazyrain.vendrickbossfight.npcs.Vendrick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vindicator;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StormyVendrick extends Vendrick {
    List<UUID> players = new ArrayList<>();
    Vindicator vendrick;
    VendrickBossFight plugin;
    Location spawnLoc;
    int phase;

    public StormyVendrick(List<UUID> players, Location spawnLoc, VendrickBossFight plugin) {
        super(players, spawnLoc, plugin);
        this.players = players;
        this.plugin = plugin;
        this.spawnLoc = spawnLoc;
    }

    @Override
    public void spawnBoss(){
        vendrick = (Vindicator) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.VINDICATOR);
        vendrick.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + "STORMY " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "Vendrick");
        vendrick.getEquipment().setItemInMainHand(ItemManager.vendrickHatchet);
        vendrick.setPatrolLeader(false);

        vendrick.setMetadata("Vendrick", new FixedMetadataValue(plugin, "vendrick"));
        vendrick.addScoreboardTag("venStorm");
        vendrick.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(100000, 2));
        vendrick.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(100000, 1));
        vendrick.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(100000, 1));

        if (plugin.getConfig().getInt("vendrick-health") > 2048){
            plugin.getConfig().set("vendrick-health", 2048);
            AttributeModifier modifier = new AttributeModifier(Objects.requireNonNull(vendrick.getCustomName()), 2048, AttributeModifier.Operation.ADD_NUMBER);
            vendrick.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(modifier);
            vendrick.setHealth(2048);

        } else if (plugin.getConfig().getInt("vendrick-health") < 500){
            AttributeModifier modifier = new AttributeModifier(Objects.requireNonNull(vendrick.getCustomName()), 500, AttributeModifier.Operation.ADD_NUMBER);
            plugin.getConfig().set("vendrick-health", 500);
            vendrick.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(modifier);
            vendrick.setHealth(500);

        } else {
            AttributeModifier modifier = new AttributeModifier(Objects.requireNonNull(vendrick.getCustomName()), plugin.getConfig().getInt("vendrick-health"), AttributeModifier.Operation.ADD_NUMBER);
            vendrick.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(modifier);
            vendrick.setHealth(plugin.getConfig().getInt("vendrick-health"));
        }
        vendrick.setHealth(plugin.getConfig().getInt("vendrick-health"));
        Bukkit.getServer().getPluginManager().callEvent(new VendrickFightStartEvent(players, getDistortion(), getDifficulty()));
    }

    @Override
    public LivingEntity getVendrick(){
        return vendrick;
    }

    @Override
    public void startAttack(int phase){
        vendrick.setAI(false);
        vendrick.setInvulnerable(true);
        vendrick.setGlowing(true);
        vendrick.teleport(vendrick.getLocation().add(0,4,0));
        this.phase = phase;

        vendrick.getLocation().getWorld().playSound(vendrick.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 10, 0.6f);
    }

    @Override
    public void stopAttack(){
        vendrick.setAI(true);
        vendrick.setInvulnerable(false);
        vendrick.setGlowing(false);
    }

    @Override
    public boolean wasDistorted(){
        return true;
    }
    @Override
    public int getDifficulty() {return 4;}

    @Override
    public String getDistortion(){ return "Stormy";}
}
