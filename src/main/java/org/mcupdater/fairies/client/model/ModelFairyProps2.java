package org.mcupdater.fairies.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import org.mcupdater.fairies.entity.FairyEntity;

public class ModelFairyProps2 extends BipedModel<FairyEntity> implements IHasHead, IHasArm
{
    public ModelFairyProps2()
    {
        this(0.0F);
    }

    public ModelFairyProps2(final float modelSize)
    {
        super(modelSize);
        // not sure what this was meant to be - but it came in the original, it's some sort of rotational offset
        final float f1 = 0.0f;

        isSneak = false;
        flymode = false;
        retract = 0F;

        //Rogue Type
        rogueHead = new ModelRenderer(this);
        rogueHead.setTextureOffset(0, 0).addBox(-3F, -5.7F, -3F, 6, 6, 6, modelSize + 0.4F);
        rogueHead.mirror = true;
        rogueHead.setTextureOffset(0, 0).addBox(-3F, -6.0F, -3F, 6, 6, 6, modelSize + 0.125F);
        rogueHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

        rogueBody = new ModelRenderer(this);
        rogueBody.setTextureOffset(0, 19).addBox(-2F, 0.0F, -1F, 4, 6, 2, modelSize + 0.375F);
        rogueBody.setTextureOffset(12, 19).addBox(-2F, 1.0F, -2F, 4, 2, 1, modelSize + 0.375F);
        rogueBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

        rogueRightArm = new ModelRenderer(this, 0, 12);
        rogueRightArm.addBox(-1F, -1F, -1F, 2, 5, 2, modelSize + 0.375F);
        rogueRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);

        rogueLeftArm = new ModelRenderer(this, 0, 12);
        rogueLeftArm.mirror = true;
        rogueLeftArm.addBox(-1F, -1F, -1F, 2, 5, 2, modelSize + 0.375F);
        rogueLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);

        rogueRightLeg = new ModelRenderer(this, 12, 12);
        rogueRightLeg.addBox(-1F, 0.0F, -1F, 2, 5, 2, modelSize + 0.375F);
        rogueRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);

        rogueLeftLeg = new ModelRenderer(this, 12, 12);
        rogueLeftLeg.mirror = true;
        rogueLeftLeg.addBox(-1F, 0.0F, -1F, 2, 5, 2, modelSize + 0.375F);
        rogueLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);

        wingRight = new ModelRenderer(this, 12, 25);
        wingRight.addBox(0F, -1.75F, -1.0F, 6, 6, 1, modelSize + 0.25F);
        wingRight.setRotationPoint(0.5F, 0.0F + f1, 1.0F);

        wingLeft = new ModelRenderer(this, 12, 25);
        wingLeft.mirror = true;
        wingLeft.addBox(-6F, -1.75F, -1.0F, 6, 6, 1, modelSize + 0.25F);
        wingLeft.setRotationPoint(-0.5F, 0.0F + f1, 1.0F);

        /*
        //Blades, Yo.
        rogueBlade1 = new FairyModelRenderer(this, 24, 0);
        rogueBlade1.addBox(-1.5F, 4F, -1F, 1, 4, 2, f - 0.2F, 2, 0F);
        rogueBlade1.setRotationPoint(-5F, 1.0F + f1, 0.0F);
        rogueBlade2 = new FairyModelRenderer(this, 30, 0);
        rogueBlade2.addBox(-1.5F, 4F, -1F, 1, 4, 2, f - 0.2F, 2, 0F);
        rogueBlade2.setRotationPoint(-5F, 1.0F + f1, 0.0F);
         */
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        rogueHead.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        rogueBody.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        rogueRightArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        rogueLeftArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        rogueRightLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        rogueLeftLeg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        wingLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        wingRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        /*
        if (venom)
        {
            rogueBlade2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
        else
        {
            rogueBlade1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
         */
    }

    public void setRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rogueHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        rogueHead.rotateAngleX = headPitch / (180F / (float)Math.PI);

        if (!flymode)
        {
            rogueRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            rogueLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            rogueRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            rogueLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        }
        else
        {
            rogueRightArm.rotateAngleX = 0.0F;
            rogueLeftArm.rotateAngleX = 0.0F;
            rogueRightLeg.rotateAngleX = 0.0F;
            rogueLeftLeg.rotateAngleX = 0.0F;
        }

        rogueRightArm.rotateAngleZ = 0.05F;
        rogueLeftArm.rotateAngleZ = -0.05F;
        rogueRightLeg.rotateAngleY = 0.0F;
        rogueLeftLeg.rotateAngleY = 0.0F;
        rogueRightLeg.rotateAngleZ = 0.0F;
        rogueLeftLeg.rotateAngleZ = 0.0F;

        if ((isSitting || isSneak) && !flymode)
        {
            rogueRightArm.rotateAngleX += -((float)Math.PI / 5F);
            rogueLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            rogueRightLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            rogueLeftLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            rogueRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            rogueLeftLeg.rotateAngleY = -((float)Math.PI / 10F);

            if (isSneak)
            {
                rogueRightLeg.rotateAngleX = -((float)Math.PI / 2F);
                rogueLeftLeg.rotateAngleX = -((float)Math.PI / 2F);
            }
        }

        if (!entityIn.getHeldItemOffhand().isEmpty())
        {
            rogueLeftArm.rotateAngleX = rogueLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        if (!entityIn.getHeldItemMainhand().isEmpty())
        {
            rogueRightArm.rotateAngleX = rogueRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        rogueRightArm.rotateAngleY = 0.0F;
        rogueLeftArm.rotateAngleY = 0.0F;

        /*
        if (onGround > -9990F)
        {
            float f6 = onGround;
            rogueBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
            wingLeft.rotateAngleY = wingRight.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
            rogueRightArm.rotationPointZ = MathHelper.sin(rogueBody.rotateAngleY) * 5F;
            rogueRightArm.rotationPointX = -MathHelper.cos(rogueBody.rotateAngleY) * 5F + 2.0F;
            rogueLeftArm.rotationPointZ = -MathHelper.sin(rogueBody.rotateAngleY) * 5F;
            rogueLeftArm.rotationPointX = MathHelper.cos(rogueBody.rotateAngleY) * 5F - 2.0F;
            rogueRightArm.rotateAngleY += rogueBody.rotateAngleY;
            rogueLeftArm.rotateAngleY += rogueBody.rotateAngleY;
            rogueLeftArm.rotateAngleX += rogueBody.rotateAngleY;
            f6 = 1.0F - onGround;
            f6 *= f6;
            f6 *= f6;
            f6 = 1.0F - f6;
            final float f8 = MathHelper.sin(f6 * (float)Math.PI);
            final float f9 = MathHelper.sin(onGround * (float)Math.PI) * -(rogueHead.rotateAngleX - 0.7F) * 0.75F;
            rogueRightArm.rotateAngleX -= f8 * 1.2D + f9;
            rogueRightArm.rotateAngleY += rogueBody.rotateAngleY * 2.0F;
            rogueRightArm.rotateAngleZ = MathHelper.sin(onGround * (float)Math.PI) * -0.4F;
        }
         */

        if (flymode)
        {
            final float f7 = (float)Math.PI;
            rogueBody.rotateAngleX = f7 / 2.0F;
            rogueBody.rotationPointY = 19F;
            wingLeft.rotateAngleX = f7 / 2.0F;
            wingRight.rotateAngleX = f7 / 2.0F;
            wingLeft.rotationPointY = 17.5F;
            wingRight.rotationPointY = 17.5F;
            wingLeft.rotationPointZ = 1.0F;
            wingRight.rotationPointZ = 1.0F;
            rogueRightLeg.rotationPointZ = 0.0F;
            rogueLeftLeg.rotationPointZ = 0.0F;
            rogueRightArm.rotationPointY = 19F;
            rogueLeftArm.rotationPointY = 19F;
            rogueRightLeg.rotationPointY = 18F;
            rogueLeftLeg.rotationPointY = 18F;
            rogueRightLeg.rotationPointZ = 6F;
            rogueLeftLeg.rotationPointZ = 6F;
            rogueHead.rotationPointZ = -3F;
            rogueHead.rotationPointY = 19.75F;
        }
        else
        {
            rogueBody.rotateAngleX = 0.0F;
            rogueBody.rotationPointY = 12F;
            wingLeft.rotateAngleX = 0.0F;
            wingRight.rotateAngleX = 0.0F;
            wingLeft.rotationPointY = 12.5F;
            wingRight.rotationPointY = 12.5F;
            wingLeft.rotationPointZ = 1.0F;
            wingRight.rotationPointZ = 1.0F;
            rogueRightLeg.rotationPointZ = 0.0F;
            rogueLeftLeg.rotationPointZ = 0.0F;

            if (isSitting)
            {
                rogueRightArm.rotationPointY = 13F;
                rogueLeftArm.rotationPointY = 13F;
            }
            else
            {
                rogueRightArm.rotationPointY = 13F;
                rogueLeftArm.rotationPointY = 13F;
            }

            rogueRightLeg.rotationPointY = 18F;
            rogueLeftLeg.rotationPointY = 18F;
            rogueRightLeg.rotationPointZ = 0.0F;
            rogueLeftLeg.rotationPointZ = 0.0F;
            rogueHead.rotationPointZ = 0.0F;
            rogueHead.rotationPointY = 12F;
        }

        if (flymode)
        {
            rogueRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            rogueLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            rogueRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.1F;
            rogueLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.1F;
            rogueRightLeg.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            rogueLeftLeg.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.1F + 0.05F;
            rogueRightLeg.rotateAngleX = 0.1F;
            rogueLeftLeg.rotateAngleX = 0.1F;
        }
        else
        {
            rogueRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            rogueLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            rogueRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            rogueLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
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
        /*
        rogueBlade1.rotateAngleX = rogueBlade2.rotateAngleX = rogueRightArm.rotateAngleX;
        rogueBlade1.rotateAngleY = rogueBlade2.rotateAngleY = rogueRightArm.rotateAngleY;
        rogueBlade1.rotateAngleZ = rogueBlade2.rotateAngleZ = rogueRightArm.rotateAngleZ;
        rogueBlade1.rotationPointX = rogueBlade2.rotationPointX = rogueRightArm.rotationPointX;
        rogueBlade1.rotationPointY = rogueBlade2.rotationPointY = rogueRightArm.rotationPointY;
        rogueBlade1.rotationPointZ = rogueBlade2.rotationPointZ = rogueRightArm.rotationPointZ;
         */
    }

    public ModelRenderer rogueHead;
    public ModelRenderer rogueBody;
    public ModelRenderer rogueLeftArm;
    public ModelRenderer rogueRightArm;
    public ModelRenderer rogueLeftLeg;
    public ModelRenderer rogueRightLeg;

    public ModelRenderer wingLeft;
    public ModelRenderer wingRight;

    //public FairyModelRenderer rogueBlade1, rogueBlade2;

    public boolean flymode;
    public float sinage;
    public float retract;
    public boolean venom;
}
