package moffy.ticex.caps.slashblade;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.CarryType;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolBladeStateCapability implements ISlashBladeState{

   public static final UUID SLASHBLADE_TOOL_UUID = UUID.fromString("2fee599e-b407-4cc5-a7be-e5a1a5be6ed9");

      protected long lastActionTime;
      protected int targetEntityId;
      protected boolean _onClick;
      protected float fallDecreaseRate;
      protected boolean isCharged;
      protected float attackAmplifier;
      protected ResourceLocation comboSeq;
      protected String lastPosHash;
      protected boolean isBroken;
      protected boolean isNoScabbard;
      protected boolean isSealed;
      protected float baseAttackModifier = 4.0F;
      protected int killCount;
      protected int refine;
      protected UUID owner;
      protected UUID uniqueId = SLASHBLADE_TOOL_UUID;
      protected String translationKey = "";
      protected ResourceLocation slashArtsKey;
      protected boolean isDefaultBewitched = false;
      protected ResourceLocation comboRootName;
      protected Optional<CarryType> carryType = Optional.empty();
      protected Optional<Color> effectColor = Optional.empty();
      protected boolean effectColorInverse;
      protected Optional<Vec3> adjust = Optional.empty();
      protected Optional<ResourceLocation> texture = Optional.empty();
      protected Optional<ResourceLocation> model = Optional.empty();
      protected LazyOptional<ResourceLocation> rootCombo = this.instantiateRootComboHolder();
      protected int maxDamage = 40;
      protected int damage = 0;
      protected int proudSoul = 0;
      protected boolean isChangedActiveState = false;
      protected List<ResourceLocation> specialEffects = new ArrayList<>();
      protected boolean isEmpty = true;

      protected IToolStackView tool;

      public ToolBladeStateCapability(ItemStack blade, IToolStackView tool) {
         this.tool = tool;
         CompoundTag persistentTag = tool.getPersistentData().getCompound(ModifiableSlashBladeItem.BLADE_STATE_LOCATION);
         if(persistentTag != null && !persistentTag.isEmpty()){
            deserializeNBT(persistentTag);
         }
      }

    public long getLastActionTime() {
      return this.lastActionTime;
   }

   public void setLastActionTime(long lastActionTime) {
      this.lastActionTime = lastActionTime;
      this.setHasChangedActiveState(true);
   }

   public boolean onClick() {
      return this._onClick;
   }

   public void setOnClick(boolean onClick) {
      this._onClick = onClick;
      this.setHasChangedActiveState(true);
   }

   public float getFallDecreaseRate() {
      return this.fallDecreaseRate;
   }

   public void setFallDecreaseRate(float fallDecreaseRate) {
      this.fallDecreaseRate = fallDecreaseRate;
      this.setHasChangedActiveState(true);
   }

   public float getAttackAmplifier() {
      return this.attackAmplifier;
   }

   public void setAttackAmplifier(float attackAmplifier) {
      this.attackAmplifier = attackAmplifier;
      this.setHasChangedActiveState(true);
   }

   @Nonnull
   public ResourceLocation getComboSeq() {
      return this.comboSeq != null ? this.comboSeq : ComboStateRegistry.NONE.getId();
   }

   public void setComboSeq(ResourceLocation comboSeq) {
      this.comboSeq = comboSeq;
      this.setHasChangedActiveState(true);
   }

   public boolean isBroken() {
      return this.isBroken;
   }

   public void setBroken(boolean broken) {
      this.isBroken = broken;
      this.setHasChangedActiveState(true);
   }

   public boolean isSealed() {
      return this.isSealed;
   }

   public void setSealed(boolean sealed) {
      this.isSealed = sealed;
      writeToPersistentData();
   }

   public float getBaseAttackModifier() {
      return this.baseAttackModifier;
   }

   public void setBaseAttackModifier(float baseAttackModifier) {
      this.baseAttackModifier = baseAttackModifier;
      writeToPersistentData();
   }

   public int getKillCount() {
      return this.killCount;
   }

   public void setKillCount(int killCount) {
      this.killCount = killCount;
      this.setHasChangedActiveState(true);
   }

   public int getRefine() {
      return this.refine;
   }

   public void setRefine(int refine) {
      this.refine = refine;
      this.setHasChangedActiveState(true);
   }

   public ResourceLocation getSlashArtsKey() {
      return this.slashArtsKey;
   }

   public void setSlashArtsKey(ResourceLocation key) {
      this.slashArtsKey = key;
      writeToPersistentData();
   }

   public boolean isDefaultBewitched() {
      return this.isDefaultBewitched;
   }

   public void setDefaultBewitched(boolean defaultBewitched) {
      this.isDefaultBewitched = defaultBewitched;
      writeToPersistentData();
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public void setTranslationKey(String translationKey) {
      this.translationKey = (String)Optional.ofNullable(translationKey).orElse("");
      writeToPersistentData();
   }

   @Nonnull
   public CarryType getCarryType() {
      return (CarryType)this.carryType.orElse(CarryType.NONE);
   }

   public void setCarryType(CarryType carryType) {
      this.carryType = Optional.ofNullable(carryType);
      writeToPersistentData();
   }

   public Color getEffectColor() {
      return (Color)this.effectColor.orElseGet(() -> {
         return new Color(0x3333FF);
      });
   }

   public void setEffectColor(Color effectColor) {
      this.effectColor = Optional.ofNullable(effectColor);
      writeToPersistentData();
   }

   public boolean isEffectColorInverse() {
      return this.effectColorInverse;
   }

   public void setEffectColorInverse(boolean effectColorInverse) {
      this.effectColorInverse = effectColorInverse;
      writeToPersistentData();
   }

   public Vec3 getAdjust() {
      return (Vec3)this.adjust.orElseGet(() -> {
         return Vec3.ZERO;
      });
   }

   public void setAdjust(Vec3 adjust) {
      this.adjust = Optional.ofNullable(adjust);
      writeToPersistentData();
   }

   public Optional<ResourceLocation> getTexture() {
      return this.texture;
   }

   public void setTexture(ResourceLocation texture) {
      this.texture = Optional.ofNullable(texture);
      writeToPersistentData();
   }

   public Optional<ResourceLocation> getModel() {
      return this.model;
   }

   public void setModel(ResourceLocation model) {
      this.model = Optional.ofNullable(model);
      writeToPersistentData();
   }

   public int getTargetEntityId() {
      
      return this.targetEntityId;
   }

   public void setTargetEntityId(int id) {
      this.targetEntityId = id;
      
      this.setHasChangedActiveState(true);
   }

   public ResourceLocation getComboRoot() {
      return this.comboRootName != null && ((IForgeRegistry<ComboState>)ComboStateRegistry.REGISTRY.get()).containsKey(this.comboRootName) ? this.comboRootName : ComboStateRegistry.STANDBY.getId();
   }

   public void setComboRoot(ResourceLocation rootLoc) {
      this.comboRootName = ((IForgeRegistry<ComboState>)ComboStateRegistry.REGISTRY.get()).containsKey(rootLoc) ? rootLoc : ComboStateRegistry.STANDBY.getId();
      this.rootCombo = this.instantiateRootComboHolder();
      writeToPersistentData();
   }

   private LazyOptional<ResourceLocation> instantiateRootComboHolder() {
      return LazyOptional.of(() -> {
         return !((IForgeRegistry<ComboState>)ComboStateRegistry.REGISTRY.get()).containsKey(this.getComboRoot()) ? ComboStateRegistry.STANDBY.getId() : this.getComboRoot();
      });
   }

   public boolean hasChangedActiveState() {
      return this.isChangedActiveState;
   }

   public void setHasChangedActiveState(boolean isChanged) {
      this.isChangedActiveState = isChanged;
      writeToPersistentData();
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public void setUniqueId(UUID uniqueId) {
      this.uniqueId = uniqueId;
      writeToPersistentData();
   }

   public int getMaxDamage() {
      return this.maxDamage;
   }

   public void setMaxDamage(int damage) {
      this.maxDamage = damage;
      writeToPersistentData();
   }

   public int getDamage() {
      return this.damage;
   }

   public void setDamage(int damage) {
      this.damage = Math.max(0, damage);
      this.setHasChangedActiveState(true);
   }

   public int getProudSoulCount() {
      return this.proudSoul;
   }

   public void setProudSoulCount(int psCount) {
      this.proudSoul = Math.max(0, psCount);
      this.setHasChangedActiveState(true);
   }

   public List<ResourceLocation> getSpecialEffects() {
      return this.specialEffects;
   }

   public void setSpecialEffects(ListTag list) {
      List<ResourceLocation> result = new ArrayList<>();
      list.forEach((tag) -> {
         ResourceLocation se = ResourceLocation.tryParse(tag.getAsString());
         if (((IForgeRegistry<SpecialEffect>)SpecialEffectsRegistry.REGISTRY.get()).containsKey(se)) {
            result.add(se);
         }

      });
      this.specialEffects = result;
      writeToPersistentData();
   }

   public boolean addSpecialEffect(ResourceLocation se) {
        boolean result = ((IForgeRegistry<SpecialEffect>)SpecialEffectsRegistry.REGISTRY.get()).containsKey(se) ? this.specialEffects.add(se) : false;
        if(result){
            writeToPersistentData();
        }
        return result;
   }

   public boolean removeSpecialEffect(ResourceLocation se) {
        boolean result = this.specialEffects.remove(se);
        if(result){
            writeToPersistentData();
        }
        return result;
   }

    public boolean hasSpecialEffect(ResourceLocation se) {
        if (((IForgeRegistry<SpecialEffect>)SpecialEffectsRegistry.REGISTRY.get()).containsKey(se)) {
            return this.specialEffects.contains(se);
        } else {
            this.specialEffects.remove(se);
            return true;
        }
    }

    @Override
    public ResourceLocation resolvCurrentComboState(LivingEntity user) {
        return !(user.getMainHandItem().getItem() instanceof ModifiableSlashBladeItem) ? ComboStateRegistry.NONE.getId() : (ResourceLocation)this.resolvCurrentComboStateTicks(user).getValue();
    }

    public void writeToPersistentData(){
      tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }

    @Override
    public boolean isEmpty() {
      return isEmpty;
    }

    @Override
    public void setNonEmpty() {
         this.isEmpty = false;
         tool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, serializeNBT());
    }
}
