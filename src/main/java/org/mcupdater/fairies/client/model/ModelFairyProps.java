package org.mcupdater.fairies.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.model.ModelRenderer;
import org.mcupdater.fairies.entity.FairyEntity;

public class ModelFairyProps extends BipedModel<FairyEntity> implements IHasHead, IHasArm
{
    public ModelFairyProps()
    {
        this(0.0F);
    }

    public ModelFairyProps(final float modelSize) {
        super(modelSize);
        // not sure what this was meant to be - but it came in the original, it's some sort of rotational offset
        final float f1 = 0.0f;

        isSneak = false;
        flymode = false;
        jobType = 0;

        //Guard Type
        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.addBox(-3F, -6.25F, -3F, 6, 2, 6, modelSize + 0.2F);
        bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        bipedBody = new ModelRenderer(this);
        bipedBody.setTextureOffset(8, 8).addBox(-2F, 0.0F, -1F, 4, 5, 2, modelSize + 0.25F);
        bipedBody.setTextureOffset(0, 16).addBox(-2F, 1.0F, -2F, 4, 2, 1, modelSize + 0.25F);
        bipedBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        bipedRightArm = new ModelRenderer(this, 0, 8);
        bipedRightArm.addBox(-1F, -1F, -1F, 2, 2, 2, modelSize + 0.375F);
        bipedRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);
        bipedLeftArm = new ModelRenderer(this, 0, 8);
        bipedLeftArm.mirror = true;
        bipedLeftArm.addBox(-1F, -1F, -1F, 2, 2, 2, modelSize + 0.375F);
        bipedLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);
        bipedRightLeg = new ModelRenderer(this, 0, 12);
        bipedRightLeg.addBox(-1F, 4.0F, -1F, 2, 2, 2, modelSize + 0.25F);
        bipedRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);
        bipedLeftLeg = new ModelRenderer(this, 0, 12);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.addBox(-1F, 4.0F, -1F, 2, 2, 2, modelSize + 0.25F);
        bipedLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);

        //Scout Type
        scoutHead = new ModelRenderer(this);
        scoutHead.setTextureOffset(0, 0).addBox(0, 0, 0, 1, 1, 1, modelSize + 0.2F);
        scoutHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        scoutBody = new ModelRenderer(this);
        scoutBody.setTextureOffset(28, 7).addBox(-2F, 0.0F, -1F, 4, 6, 2, modelSize + 0.125F);
        scoutBody.setTextureOffset(28, 15).addBox(-2F, 1.0F, -2F, 4, 2, 1, modelSize + 0.125F);
        scoutBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        scoutRightArm = new ModelRenderer(this, 20, 8);
        scoutRightArm.addBox(-1F, -1F, -1F, 2, 3, 2, modelSize + 0.125F);
        scoutRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);
        scoutLeftArm = new ModelRenderer(this, 20, 8);
        scoutLeftArm.mirror = true;
        scoutLeftArm.addBox(-1F, -1F, -1F, 2, 3, 2, modelSize + 0.125F);
        scoutLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);
        scoutRightLeg = new ModelRenderer(this, 20, 13);
        scoutRightLeg.addBox(-1F, 0.0F, -1F, 2, 3, 2, modelSize + 0.125F);
        scoutRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);
        scoutLeftLeg = new ModelRenderer(this, 20, 13);
        scoutLeftLeg.mirror = true;
        scoutLeftLeg.addBox(-1F, 0.0F, -1F, 2, 3, 2, modelSize + 0.125F);
        scoutLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);
        wingRight = new ModelRenderer(this, 43, 15);
        wingRight.addBox(0F, -1.75F, -1.0F, 6, 6, 1, modelSize + 0.25F);
        wingRight.setRotationPoint(0.5F, 0.0F + f1, 1.0F);
        wingLeft = new ModelRenderer(this, 43, 15);
        wingLeft.mirror = true;
        wingLeft.addBox(-6F, -1.75F, -1.0F, 6, 6, 1, modelSize + 0.25F);
        wingLeft.setRotationPoint(-0.5F, 0.0F + f1, 1.0F);

        //Medic Type
        medicHead = new ModelRenderer(this);
        medicHead.setTextureOffset(0, 19).addBox(-3F, -5.5F, -3F, 6, 1, 6, modelSize + 0.2F);
        medicHead.setTextureOffset(10, 15).addBox(-1.5F, -7.0F, -3.125F, 3, 3, 1, modelSize + 0.2F);
        medicHead.setTextureOffset(24, 0).addBox(-3F, -3.5F, -3F, 6, 1, 6, modelSize + 0.2F);
        medicHead.setTextureOffset(42, 3).addBox(-2.5F, -4F, -3.5F, 2, 2, 1, modelSize + 0.2F);
        medicHead.setTextureOffset(42, 3).addBox(0.5F, -4F, -3.5F, 2, 2, 1, modelSize + 0.2F);
        medicHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        medicBody = new ModelRenderer(this);
        medicBody.setTextureOffset(28, 18).addBox(-2F, 0.0F, -1F, 4, 6, 2, modelSize + 0.125F);
        medicBody.setTextureOffset(18, 18).addBox(-2F, 1.0F, -2F, 4, 2, 1, modelSize + 0.125F);
        medicBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    }

    @Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();

        switch (jobType) {
            case 0:
                bipedHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                bipedBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                bipedRightArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                bipedLeftArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                bipedRightLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                bipedLeftLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                break;
            case 1:
                scoutHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                scoutBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                scoutRightArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                scoutLeftArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                scoutRightLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                scoutLeftLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                wingLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                wingRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                break;
            case 2:
                medicHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                medicBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

        GL11.glPopMatrix();
    }

    @Override
    public void setRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        switch (jobType) {
            case 0:
                setGuardRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            case 1:
                setScoutRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            case 2:
                setMedicRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    protected void setGuardRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        bipedHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        bipedHead.rotateAngleX = headPitch / (180F / (float)Math.PI);

        if (!flymode)
        {
            bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        }
        else
        {
            bipedRightArm.rotateAngleX = 0.0F;
            bipedLeftArm.rotateAngleX = 0.0F;
            bipedRightLeg.rotateAngleX = 0.0F;
            bipedLeftLeg.rotateAngleX = 0.0F;
        }

        bipedRightArm.rotateAngleZ = 0.05F;
        bipedLeftArm.rotateAngleZ = -0.05F;
        bipedRightLeg.rotateAngleY = 0.0F;
        bipedLeftLeg.rotateAngleY = 0.0F;
        bipedRightLeg.rotateAngleZ = 0.0F;
        bipedLeftLeg.rotateAngleZ = 0.0F;

        if ((isSitting || isSneak) && !flymode)
        {
            bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            bipedRightLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);

            if (isSneak)
            {
                bipedRightLeg.rotateAngleX = -((float)Math.PI / 2F);
                bipedLeftLeg.rotateAngleX = -((float)Math.PI / 2F);
            }
        }

        if (!entityIn.getHeldItemOffhand().isEmpty())
        {
            bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        if (!entityIn.getHeldItemMainhand().isEmpty())
        {
            bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        bipedRightArm.rotateAngleY = 0.0F;
        bipedLeftArm.rotateAngleY = 0.0F;

        /*
        if (onGround > -9990F)
        {
            float f6 = onGround;
            bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
            bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5F;
            bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5F + 2.0F;
            bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5F;
            bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5F - 2.0F;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
            f6 = 1.0F - onGround;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;
            final float f8 = MathHelper.sin(f6 * (float)Math.PI);
            final float f9 = MathHelper.sin(onGround * (float)Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            bipedRightArm.rotateAngleX -= f8 * 1.2D + f9;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
            bipedRightArm.rotateAngleZ = MathHelper.sin(onGround * (float)Math.PI) * -0.4F;
        }
        */

        if (flymode)
        {
            final float f7 = (float)Math.PI;
            bipedBody.rotateAngleX = f7 / 2.0F;
            bipedBody.rotationPointY = 19F;
            bipedRightLeg.rotationPointZ = 0.0F;
            bipedLeftLeg.rotationPointZ = 0.0F;
            bipedRightArm.rotationPointY = 19F;
            bipedLeftArm.rotationPointY = 19F;
            bipedRightLeg.rotationPointY = 18F;
            bipedLeftLeg.rotationPointY = 18F;
            bipedRightLeg.rotationPointZ = 6F;
            bipedLeftLeg.rotationPointZ = 6F;
            bipedHead.rotationPointZ = -3F;
            bipedHead.rotationPointY = 19.75F;
        }
        else
        {
            bipedBody.rotateAngleX = 0.0F;
            bipedBody.rotationPointY = 12F;
            bipedRightLeg.rotationPointZ = 0.0F;
            bipedLeftLeg.rotationPointZ = 0.0F;

            if (isSitting)
            {
                bipedRightArm.rotationPointY = 13F;
                bipedLeftArm.rotationPointY = 13F;
            }
            else
            {
                bipedRightArm.rotationPointY = 13F;
                bipedLeftArm.rotationPointY = 13F;
            }

            bipedRightLeg.rotationPointY = 18F;
            bipedLeftLeg.rotationPointY = 18F;
            bipedRightLeg.rotationPointZ = 0.0F;
            bipedLeftLeg.rotationPointZ = 0.0F;
            bipedHead.rotationPointZ = 0.0F;
            bipedHead.rotationPointY = 12F;
        }

        if (flymode)
        {
            bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.1F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.1F;
            bipedRightLeg.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            bipedLeftLeg.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            bipedRightLeg.rotateAngleX = 0.1F;
            bipedLeftLeg.rotateAngleX = 0.1F;
        }
        else
        {
            bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        }
    }

    protected void setScoutRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        scoutHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        scoutHead.rotateAngleX = headPitch / (180F / (float)Math.PI);

        if (!flymode)
        {
            scoutRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            scoutLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            scoutRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            scoutLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        }
        else
        {
            scoutRightArm.rotateAngleX = 0.0F;
            scoutLeftArm.rotateAngleX = 0.0F;
            scoutRightLeg.rotateAngleX = 0.0F;
            scoutLeftLeg.rotateAngleX = 0.0F;
        }

        scoutRightArm.rotateAngleZ = 0.05F;
        scoutLeftArm.rotateAngleZ = -0.05F;
        scoutRightLeg.rotateAngleY = 0.0F;
        scoutLeftLeg.rotateAngleY = 0.0F;
        scoutRightLeg.rotateAngleZ = 0.0F;
        scoutLeftLeg.rotateAngleZ = 0.0F;

        if ((isSitting || isSneak) && !flymode)
        {
            scoutRightArm.rotateAngleX += -((float)Math.PI / 5F);
            scoutLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            scoutRightLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            scoutLeftLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            scoutRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            scoutLeftLeg.rotateAngleY = -((float)Math.PI / 10F);

            if (isSneak)
            {
                scoutRightLeg.rotateAngleX = -((float)Math.PI / 2F);
                scoutLeftLeg.rotateAngleX = -((float)Math.PI / 2F);
            }
        }

        if (!entityIn.getHeldItemOffhand().isEmpty())
        {
            scoutLeftArm.rotateAngleX = scoutLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        if (!entityIn.getHeldItemMainhand().isEmpty())
        {
            scoutRightArm.rotateAngleX = scoutRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        scoutRightArm.rotateAngleY = 0.0F;
        scoutLeftArm.rotateAngleY = 0.0F;

        /*
        if (onGround > -9990F)
        {
            float f6 = onGround;
            scoutBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
            wingLeft.rotateAngleY = wingRight.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
            scoutRightArm.rotationPointZ = MathHelper.sin(scoutBody.rotateAngleY) * 5F;
            scoutRightArm.rotationPointX = -MathHelper.cos(scoutBody.rotateAngleY) * 5F + 2.0F;
            scoutLeftArm.rotationPointZ = -MathHelper.sin(scoutBody.rotateAngleY) * 5F;
            scoutLeftArm.rotationPointX = MathHelper.cos(scoutBody.rotateAngleY) * 5F - 2.0F;
            scoutRightArm.rotateAngleY += scoutBody.rotateAngleY;
            scoutLeftArm.rotateAngleY += scoutBody.rotateAngleY;
            scoutLeftArm.rotateAngleX += scoutBody.rotateAngleY;
            f6 = 1.0F - onGround;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;
            final float f8 = MathHelper.sin(f6 * (float)Math.PI);
            final float f9 = MathHelper.sin(onGround * (float)Math.PI) * -(scoutHead.rotateAngleX - 0.7F) * 0.75F;
            scoutRightArm.rotateAngleX -= f8 * 1.2D + f9;
            scoutRightArm.rotateAngleY += scoutBody.rotateAngleY * 2.0F;
            scoutRightArm.rotateAngleZ = MathHelper.sin(onGround * (float)Math.PI) * -0.4F;
        }
         */

        if (flymode)
        {
            final float f7 = (float)Math.PI;
            scoutBody.rotateAngleX = f7 / 2.0F;
            scoutBody.rotationPointY = 19F;
            wingLeft.rotateAngleX = f7 / 2.0F;
            wingRight.rotateAngleX = f7 / 2.0F;
            wingLeft.rotationPointY = 17.5F;
            wingRight.rotationPointY = 17.5F;
            wingLeft.rotationPointZ = 1.0F;
            wingRight.rotationPointZ = 1.0F;
            scoutRightLeg.rotationPointZ = 0.0F;
            scoutLeftLeg.rotationPointZ = 0.0F;
            scoutRightArm.rotationPointY = 19F;
            scoutLeftArm.rotationPointY = 19F;
            scoutRightLeg.rotationPointY = 18F;
            scoutLeftLeg.rotationPointY = 18F;
            scoutRightLeg.rotationPointZ = 6F;
            scoutLeftLeg.rotationPointZ = 6F;
            scoutHead.rotationPointZ = -3F;
            scoutHead.rotationPointY = 19.75F;
        }
        else
        {
            scoutBody.rotateAngleX = 0.0F;
            scoutBody.rotationPointY = 12F;
            wingLeft.rotateAngleX = 0.0F;
            wingRight.rotateAngleX = 0.0F;
            wingLeft.rotationPointY = 12.5F;
            wingRight.rotationPointY = 12.5F;
            wingLeft.rotationPointZ = 1.0F;
            wingRight.rotationPointZ = 1.0F;
            scoutRightLeg.rotationPointZ = 0.0F;
            scoutLeftLeg.rotationPointZ = 0.0F;

            if (isSitting)
            {
                scoutRightArm.rotationPointY = 13F;
                scoutLeftArm.rotationPointY = 13F;
            }
            else
            {
                scoutRightArm.rotationPointY = 13F;
                scoutLeftArm.rotationPointY = 13F;
            }

            scoutRightLeg.rotationPointY = 18F;
            scoutLeftLeg.rotationPointY = 18F;
            scoutRightLeg.rotationPointZ = 0.0F;
            scoutLeftLeg.rotationPointZ = 0.0F;
            scoutHead.rotationPointZ = 0.0F;
            scoutHead.rotationPointY = 12F;
        }

        if (flymode)
        {
            scoutRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            scoutLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            scoutRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.1F;
            scoutLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.1F;
            scoutRightLeg.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            scoutLeftLeg.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            scoutRightLeg.rotateAngleX = 0.1F;
            scoutLeftLeg.rotateAngleX = 0.1F;
        }
        else
        {
            scoutRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            scoutLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            scoutRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            scoutLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        }

        if (flymode)
        {
            wingLeft.rotateAngleY = 0.1F;
            wingRight.rotateAngleY = -0.1F;
            wingLeft.rotateAngleY += Math.sin(sinage) / 6F;
            wingRight.rotateAngleY -= Math.sin(sinage) / 6F;
            wingLeft.rotateAngleZ = 0.5F;
            wingRight.rotateAngleZ = -0.5F;
        }
        else
        {
            wingLeft.rotateAngleY = 0.6F;
            wingRight.rotateAngleY = -0.6F;
            wingLeft.rotateAngleY += Math.sin(sinage) / 3F;
            wingRight.rotateAngleY -= Math.sin(sinage) / 3F;
            wingLeft.rotateAngleZ = 0.125F;
            wingRight.rotateAngleZ = -0.125F;
        }

        wingLeft.rotateAngleZ += Math.cos(sinage) / (flymode ? 3F : 8F);
        wingRight.rotateAngleZ -= Math.cos(sinage) / (flymode ? 3F : 8F);
    }

    protected void setMedicRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        medicHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        medicHead.rotateAngleX = headPitch / (180F / (float)Math.PI);

        /*
        if (onGround > -9990F)
        {
            final float f6 = onGround;
            medicBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
        }
         */

        if (flymode)
        {
            final float f7 = (float)Math.PI;
            medicBody.rotateAngleX = f7 / 2.0F;
            medicBody.rotationPointY = 19F;
            medicHead.rotationPointZ = -3F;
            medicHead.rotationPointY = 19.75F;
        }
        else
        {
            medicBody.rotateAngleX = 0.0F;
            medicBody.rotationPointY = 12F;
            medicHead.rotationPointZ = 0.0F;
            medicHead.rotationPointY = 12F;
        }
    }

    public ModelRenderer scoutHead;
    public ModelRenderer scoutBody;
    public ModelRenderer scoutLeftArm;
    public ModelRenderer scoutRightArm;
    public ModelRenderer scoutLeftLeg;
    public ModelRenderer scoutRightLeg;

    public ModelRenderer wingLeft;
    public ModelRenderer wingRight;

    public ModelRenderer medicHead;
    public ModelRenderer medicBody;

    public boolean flymode;
    public int jobType;
    public float sinage;
}
