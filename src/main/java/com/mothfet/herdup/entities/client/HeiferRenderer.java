package com.mothfet.herdup.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mothfet.herdup.config.ConfigHolder;
import com.mothfet.herdup.entities.custom.HeiferEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class HeiferRenderer extends GeoEntityRenderer<HeiferEntity> {

    public HeiferRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HeiferModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public RenderType getRenderType(HeiferEntity entity, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        if (entity.isBaby()) {
            stack.pushPose();
            float scale = 0.9f + (((ConfigHolder.SERVER.calfAgeInSeconds.get() * 20.0f - entity.getAge()) / (ConfigHolder.SERVER.calfAgeInSeconds.get() * 20.0f)) * 0.25f);
            stack.scale(scale, scale, scale);
        }
        super.getRenderType(entity, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
        if (entity.isBaby()) {
            stack.popPose();
        }

        return super.getRenderType(entity, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
