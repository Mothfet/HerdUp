package com.mothfet.herdup.entities;

import com.mothfet.herdup.HerdUp;
import com.mothfet.herdup.entities.custom.HeiferEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, HerdUp.MOD_ID);

    public static final RegistryObject<EntityType<HeiferEntity>> HEIFER =
            ENTITY_TYPES.register("heifer",
            () -> EntityType.Builder.of(HeiferEntity::new, MobCategory.CREATURE)
                    // width, height
                    .sized(1.0f, 2.0F)
                    .build(new ResourceLocation(HerdUp.MOD_ID, "heifer").toString()));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
