package org.mcupdater.fairies.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.mcupdater.fairies.entity.FairyEntity;

public class ModelFairyEyes extends BipedModel<FairyEntity> implements IHasHead
{
    public ModelFairyEyes()
    {
        this(0.0F);
    }

    public ModelFairyEyes(final float modelSize)
    {
        super(modelSize);
        // not sure what this was meant to be - but it came in the original, it's some sort of rotational offset
        final float f1 = 0.0f;

        isSneak = false;
        flymode = false;
        bipedHead = new ModelRenderer(this, 40, 0);
        bipedHead.addBox(-3F, -6F, -3F, 6, 6, 6, modelSize + 0.01F);
        bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    }

    @Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        bipedHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        bipedHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        bipedHead.rotateAngleX = headPitch / (180F / (float)Math.PI);

        if (flymode)
        {
            bipedHead.rotationPointZ = -3F;
            bipedHead.rotationPointY = 19.75F;
        }
        else
        {
            bipedHead.rotationPointZ = 0.0F;
            bipedHead.rotationPointY = 12F;
        }
    }

    public boolean flymode;
}
