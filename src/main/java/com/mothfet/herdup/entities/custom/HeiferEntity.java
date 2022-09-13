package com.mothfet.herdup.entities.custom;

import com.mothfet.herdup.HerdUp;
import com.mothfet.herdup.config.ConfigHolder;
import com.mothfet.herdup.entities.ModEntityTypes;
import com.mothfet.herdup.entities.ai.FleeRainGoal;
import com.mothfet.herdup.entities.ai.RestrictRainGoal;
import com.mothfet.herdup.entities.variants.HeiferVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HeiferEntity extends Animal implements IAnimatable {

    public static final Ingredient BREEDING_ITEMS = Ingredient.of(Items.WHEAT);
    public static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.WHEAT);

    private AnimationFactory factory = new AnimationFactory(this);

    private int animTimer = 0;
    private int idleAnimCooldown = 0;

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT =
            SynchedEntityData.defineId(HeiferEntity.class, EntityDataSerializers.INT);

    public HeiferEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            animTimer = Math.max(animTimer - 1, 0);
            idleAnimCooldown = Math.max(idleAnimCooldown - 1, 0);
        }
        super.tick();
    }


    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .build();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Horse.class, (float) 16, 0.8D, 1.5D));
        //this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Wolf.class, (float) 16, 0.8D, 1.5D));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowMobGoal(this, (float) 1.5, 25, 40));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new EatBlockGoal(this));
        this.goalSelector.addGoal(6, new FleeRainGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RestrictRainGoal(this));
        //this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new FloatGoal(this));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));

    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        if (isFood(itemstack)) {
            return super.mobInteract(player, hand);
        }

        if (this.isBreedingFood(itemstack)) {
            this.fedBreedingFood(player, itemstack);
        }

        if (itemstack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, itemstack1);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    //*Entity Data*

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ID_TYPE_VARIANT, tag.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getTypeVariant());

    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    //*animation stuff*
    public <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        HeiferEntity cow = (HeiferEntity) event.getAnimatable();
        if (cow.isBaby()) return babyPredicate(event);

        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.heifer.walk"));
        } else {
            if (event.getController().getAnimationState() == AnimationState.Stopped ||
                    event.getController().getCurrentAnimation().animationName.equalsIgnoreCase("animation.heifer.walk")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.heifer.idle", false));
            }
        }
        return PlayState.CONTINUE;
    }

     public <E extends IAnimatable> PlayState babyPredicate(AnimationEvent<E> event) {

         if (event.isMoving()) {
             event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.calf.walk"));
                } else {
             if (event.getController().getAnimationState() == AnimationState.Stopped ||
                     event.getController().getCurrentAnimation().animationName.equalsIgnoreCase("animation.calf.walk")) {
                 event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.calf.idle", false));
             }
         }
         return PlayState.CONTINUE;
     }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }


    //**sounds!**

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.COW_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.COW_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.COW_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    //**VARIANTS**

    public HeiferVariants getCowType() {
        return HeiferVariants.getById(this.getTypeVariant());
    }

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(int id) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, id);
    }

    public void setCowType(HeiferVariants type) {
        this.setVariant(type.getId());
    }

    //**BABIES??**//

    //Can make baby?
    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (pOtherAnimal.getClass() != this.getClass()) {
            return false;
        } else {
            return this.isInLove() && pOtherAnimal.isInLove();
        }
    }

    //Smooch time
    @Override
    public void setInLove(@Nullable Player pPlayer) {
        this.setInLoveTime(ConfigHolder.SERVER.heiferInLoveInSeconds.get() * 20); // Ticks
    }

    //How old is baby?
    @Override
    public void setBaby(boolean pChildZombie) {
        this.setAge(pChildZombie ? -(ConfigHolder.SERVER.calfAgeInSeconds.get() * 20) : 0);
    }

    @Override
    protected void ageBoundaryReached() {
        if (!this.isBaby()) {
            this.setCowType(HeiferVariants.calfToParentType(this.getCowType()));
        }
        super.ageBoundaryReached();
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        HeiferEntity calf = ModEntityTypes.HEIFER.get().create(level);
        if (calf == null) {
            HerdUp.LOGGER.error("Uh oh - A calf could not be spawned, something went wrong.");
            return null;
        }
        HeiferEntity herdPartner = (HeiferEntity) partner;
        int i = this.getRandom().nextInt(9);
        HeiferVariants cowType;
        if (i < 4) {
            cowType = HeiferVariants.parentToCalfType(this.getCowType());
        } else if ( i < 8 ) {
            cowType = HeiferVariants.parentToCalfType(herdPartner.getCowType());
        } else {
            cowType = HeiferVariants.getRandomCalfCoat();
        }
        calf.setCowType(cowType);
        HerdUp.LOGGER.debug("A BABY IS BORN!");

        return calf;
    }

    //Breeding munchies

    public boolean isBreedingFood(ItemStack pStack) {
        return BREEDING_ITEMS.test(pStack);
    }

    public InteractionResult fedBreedingFood(Player pPlayer, ItemStack pStack) {
        boolean flag = this.handleEatingBreedingFood(pPlayer, pStack);
        if (!pPlayer.getAbilities().instabuild) {
            if (pStack.getItem() == Items.WHEAT)
                pStack.shrink(1);
        }

        if (this.level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
    }

    public boolean handleEatingBreedingFood(Player pPlayer, ItemStack pStack) {
        boolean flag = false;
        float f = 0.0F;
        int i = 0;
        int j = 0;
        Item item = pStack.getItem();
        if (item == Items.WHEAT) {
            i = (int) (ConfigHolder.SERVER.calfAgeInSeconds.get() * 0.5);
            if (!this.level.isClientSide  && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.setInLove(pPlayer);
            }
        }

        if (this.isBaby() && i > 0) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            if (!this.level.isClientSide) {
                this.ageUp(i);
            }
            flag = true;
        }
        return flag;
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_,
                                        MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749,
                                        @Nullable CompoundTag p_146750_) {
        HeiferVariants cowType;
        if (p_146749 instanceof HeiferData) {
            cowType = ((HeiferData) p_146749).variant;
        } else {
            cowType = HeiferVariants.getRandomNaturalCowType();
            p_146749 = new HeiferData(cowType);
        }
        this.setCowType(getCowType());
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749, p_146750_);
    }

    public static class HeiferData extends AgeableMobGroupData {
        public final HeiferVariants variant;

        public HeiferData(HeiferVariants p_i231557_1_) {
            super(false);
            this.variant = p_i231557_1_;
        }
    }
}
