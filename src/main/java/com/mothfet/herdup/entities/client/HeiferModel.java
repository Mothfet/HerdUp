package com.mothfet.herdup.entities.client;

import com.google.common.collect.Maps;
import com.mothfet.herdup.HerdUp;
import com.mothfet.herdup.entities.custom.HeiferEntity;
import com.mothfet.herdup.entities.variants.HeiferVariants;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Map;

public class HeiferModel extends AnimatedGeoModel<HeiferEntity> {

    public static final Map<HeiferVariants, ResourceLocation> VARIANTS =
            Util.make(
                    Maps.newEnumMap(HeiferVariants.class),
                    (iter) -> {
                        iter.put(
                                HeiferVariants.HEIFER,
                                new ResourceLocation(HerdUp.MOD_ID, "textures/entity/heifer/heifers/heifer.png"));
                        iter.put(
                                HeiferVariants.BULL,
                                new ResourceLocation(HerdUp.MOD_ID, "textures/entity/heifer/bulls/bull.png"));
                        iter.put(
                                HeiferVariants.CALF,
                                new ResourceLocation(HerdUp.MOD_ID, "textures/entity/heifer/calves/calf.png"));
                    });

    @Override
    public ResourceLocation getModelLocation(HeiferEntity heiferEntity) {
        if (heiferEntity.isBaby()) {
            return new ResourceLocation(HerdUp.MOD_ID, "geo/calf.geo.json");
        }
        return new ResourceLocation(HerdUp.MOD_ID, "geo/heifer.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(HeiferEntity heiferEntity) {
        return VARIANTS.get(heiferEntity.getCowType());
    }

    @Override
    public ResourceLocation getAnimationFileLocation(HeiferEntity heiferEntity) {
        if (heiferEntity.isBaby()) {
            return new ResourceLocation(HerdUp.MOD_ID, "animations/calf.animations.json");
        }
        return new ResourceLocation(HerdUp.MOD_ID, "animations/heifer.animations.json");
    }


}
