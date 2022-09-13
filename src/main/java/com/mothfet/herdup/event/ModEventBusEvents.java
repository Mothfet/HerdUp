package com.mothfet.herdup.event;

import com.mothfet.herdup.HerdUp;
import com.mothfet.herdup.entities.ModEntityTypes;
import com.mothfet.herdup.entities.custom.HeiferEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HerdUp.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.HEIFER.get(), HeiferEntity.setAttributes());
    }
}