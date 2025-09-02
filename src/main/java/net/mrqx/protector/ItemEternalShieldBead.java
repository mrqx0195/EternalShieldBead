package net.mrqx.protector;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class ItemEternalShieldBead extends Item {
    public static final String PROTECT_KEY = EternalShieldBead.MODID + ".protect";

    public ItemEternalShieldBead() {
        super(new Properties().rarity(Rarity.EPIC).durability(10));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.is(EternalShieldBead.ETERNAL_SHIELD_BEAD.get()) && !player.getCooldowns().isOnCooldown(EternalShieldBead.ETERNAL_SHIELD_BEAD.get())) {
            Entity target = event.getTarget();
            ResourceLocation resourceLocation = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
            if (resourceLocation != null && !Config.BLACKLIST.get().contains(resourceLocation.toString())) {
                CompoundTag persistentData = target.getPersistentData();
                persistentData.putBoolean(PROTECT_KEY, !persistentData.getBoolean(PROTECT_KEY));
                if (persistentData.getBoolean(PROTECT_KEY)) {
                    itemStack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
                player.getCooldowns().addCooldown(EternalShieldBead.ETERNAL_SHIELD_BEAD.get(), 20);
                player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        boolean targetFlag = event.getEntity().getPersistentData().getBoolean(PROTECT_KEY);
        if (targetFlag) {
            event.getEntity().setHealth(event.getEntity().getMaxHealth() * 0.01F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living) {
            boolean attackerFlag = living.getPersistentData().getBoolean(PROTECT_KEY);
            if (attackerFlag) {
                event.setAmount(event.getAmount() * living.getHealth() / living.getMaxHealth());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamageEvent(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity living) {
            boolean attackerFlag = living.getPersistentData().getBoolean(PROTECT_KEY);
            if (attackerFlag) {
                event.setAmount(event.getAmount() * living.getHealth() / living.getMaxHealth());
            }
        }
    }
}
