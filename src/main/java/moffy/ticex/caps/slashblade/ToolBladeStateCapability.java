package moffy.ticex.caps.slashblade;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mojang.datafixers.types.Type;

import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.NBTHelper;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolBladeStateCapability extends SlashBladeState{

    private IToolStackView tool;

    public ToolBladeStateCapability(ItemStack blade, IToolStackView tool) {
        super(blade);
        this.tool = tool;
        /* if(tool.getPersistentData().contains(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, 10)){
            deserializeNBT(tool.getPersistentData().getCompound(ModifiableSlashBladeItem.BLADE_STATE_LOCATION));
        } */
    }

    @Override
    public boolean removeSpecialEffect(ResourceLocation se) {
        boolean result = super.removeSpecialEffect(se);
        if(result){
            tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
        }
        return result;
    }

    @Override
    public boolean addSpecialEffect(ResourceLocation se) {
        boolean result = super.addSpecialEffect(se);
        if(result){
            tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
        }
        return result;
    }

    @Override
    public void setHasChangedActiveState(boolean isChanged) {
        super.setHasChangedActiveState(isChanged);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setOnClick(boolean onClick) {
        super.setOnClick(onClick);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setFallDecreaseRate(float fallDecreaseRate) {
        super.setFallDecreaseRate(fallDecreaseRate);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setActiveState(CompoundTag tag) {
        super.setActiveState(tag);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setAdjust(Vec3 adjust) {
        super.setAdjust(adjust);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setAttackAmplifier(float attackAmplifier) {
        super.setAttackAmplifier(attackAmplifier);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setBaseAttackModifier(float baseAttackModifier) {
        super.setBaseAttackModifier(baseAttackModifier);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setBroken(boolean broken) {
        super.setBroken(broken);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setCarryType(CarryType carryType) {
        super.setCarryType(carryType);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setColorCode(int colorCode) {
        super.setColorCode(colorCode);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setComboRoot(ResourceLocation rootLoc) {
        super.setComboRoot(rootLoc);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setComboSeq(ResourceLocation comboSeq) {
        super.setComboSeq(comboSeq);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setDamage(int damage) {
        super.setDamage(damage);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setDefaultBewitched(boolean defaultBewitched) {
        super.setDefaultBewitched(defaultBewitched);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setEffectColor(Color effectColor) {
        super.setEffectColor(effectColor);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setEffectColorInverse(boolean effectColorInverse) {
        super.setEffectColorInverse(effectColorInverse);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setKillCount(int killCount) {
        super.setKillCount(killCount);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setLastActionTime(long lastActionTime) {
        super.setLastActionTime(lastActionTime);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setMaxDamage(int damage) {
        super.setMaxDamage(damage);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setTargetEntityId(int id) {
        super.setTargetEntityId(id);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setModel(ResourceLocation model) {
        super.setModel(model);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setProudSoulCount(int psCount) {
        super.setProudSoulCount(psCount);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setRefine(int refine) {
        super.setRefine(refine);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setSealed(boolean sealed) {
        super.setSealed(sealed);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setSlashArtsKey(ResourceLocation key) {
        super.setSlashArtsKey(key);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setSpecialEffects(ListTag list) {
        super.setSpecialEffects(list);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setTargetEntityId(Entity target) {
        super.setTargetEntityId(target);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setTexture(ResourceLocation texture) {
        super.setTexture(texture);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setTranslationKey(String translationKey) {
        super.setTranslationKey(translationKey);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public void setUniqueId(UUID uniqueId) {
        super.setUniqueId(uniqueId);
        tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }
    
    @Override
    public ResourceLocation resolvCurrentComboState(LivingEntity user) {
        if(user.getMainHandItem().getItem() instanceof ModifiableSlashBladeItem){
            return (ResourceLocation)this.resolvCurrentComboStateTicks(user).getValue();
        }
        return super.resolvCurrentComboState(user);
    }
}
