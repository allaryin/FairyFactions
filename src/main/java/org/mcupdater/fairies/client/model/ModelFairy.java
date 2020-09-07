package org.mcupdater.fairies.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import org.mcupdater.fairies.entity.FairyEntity;

public class ModelFairy extends BipedModel<FairyEntity> implements IHasArm, IHasHead {
	public ModelRenderer strand, strand2, strand3, strand4;
	public ModelRenderer crown;
	public ModelRenderer wingLeft;
	public ModelRenderer wingRight;
	public boolean flymode;
	public boolean showCrown;
	public boolean scoutWings;
	public boolean rogueParts;
	public boolean hairType;
	public float sinage;

	public ModelFairy() {
		this(0.0F);
	}

	public ModelFairy(float modelSize) {
		super(modelSize);
		// not sure what this was meant to be - but it came in the original, it's some sort of rotational offset
		final float f1 = 0.0f;

		isSneak = false;
		flymode = showCrown = false;

		bipedHead = new ModelRenderer(this, 0, 0);
		bipedHead.addBox(-3F, -6F, -3F, 6, 6, 6, modelSize);
		bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

		strand = (new ModelRenderer(this)).setTextureSize((byte)64, (byte)32);
		strand.setTextureOffset(0, 20).addBox(-3F, -5F, 3F, 6, 3, 1, modelSize);
		strand.setTextureOffset(24, 0).addBox(-4F, -5F, -3F, 1, 3, 6, modelSize);
		strand.setTextureOffset(24, 0).addBox(3F, -5F, -3F, 1, 3, 6, modelSize);
		strand.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

		strand2 = (new ModelRenderer(this)).setTextureSize((byte)64, (byte)32);
		strand2.setTextureOffset(13, 23).addBox(-5F, -2.5F, 1.5F, 10, 3, 3, modelSize);
		strand2.setRotationPoint(0F, 0F + f1, 0F);
		strand.addChild(strand2);

		strand3 = (new ModelRenderer(this)).setTextureSize((byte)64, (byte)32);
		strand3.setTextureOffset(13, 23).addBox(-3F, -1.5F, -1.5F, 3, 3, 3, modelSize - 0.5F);
		strand3.setTextureOffset(13, 23).addBox(-5.25F, -1.5F, -1.5F, 3, 3, 3, modelSize - 0.25F);
		strand3.setRotationPoint(-2F, -1.75F + f1, 3F);
		strand3.rotateAngleZ = -1.0F;
		strand3.rotateAngleY = 0.5F;
		strand3.showModel = false;
		strand.addChild(strand3);

		strand4 = (new ModelRenderer(this)).setTextureSize((byte)64, (byte)32);
		strand4.mirror = true;
		strand4.setTextureOffset(13, 23).addBox(0F, -1.5F, -1.5F, 3, 3, 3, modelSize - 0.5F);
		strand4.setTextureOffset(13, 23).addBox(2.25F, -1.5F, -1.5F, 3, 3, 3, modelSize - 0.25F);
		strand4.setRotationPoint(2F, -1.75F + f1, 3F);
		strand4.rotateAngleZ = 1.0F;
		strand4.rotateAngleY = -0.5F;
		strand4.showModel = false;
		strand.addChild(strand4);

		crown = new ModelRenderer(this, 37, 14);
		crown.addBox(-3F, -6.75F, -3F, 6, 3, 6, modelSize + 0.25F);
		crown.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

		bipedBody = (new ModelRenderer(this)).setTextureSize((byte)64, (byte)32);
		bipedBody.setTextureOffset(8, 12).addBox(-2F, 0.0F, -1F, 4, 6, 2, modelSize);
		bipedBody.setTextureOffset(15, 20).addBox(-2F, 1.0F, -2F, 4, 2, 1, modelSize);
		bipedBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);

		wingRight = new ModelRenderer(this, 27, 9);
		wingRight.addBox(0F, -0.75F, -1.0F, 5, 4, 1, modelSize + 0.25F);
		wingRight.setRotationPoint(0.5F, 0.0F + f1, 1.0F);

		wingLeft = new ModelRenderer(this, 27, 9);
		wingLeft.mirror = true;
		wingLeft.addBox(-5F, -0.75F, -1.0F, 5, 4, 1, modelSize + 0.25F);
		wingLeft.setRotationPoint(-0.5F, 0.0F + f1, 1.0F);

		bipedRightArm = new ModelRenderer(this, 0, 12);
		bipedRightArm.addBox(-1F, -1F, -1F, 2, 6, 2, modelSize);
		bipedRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);

		bipedLeftArm = new ModelRenderer(this, 0, 12);
		bipedLeftArm.mirror = true;
		bipedLeftArm.addBox(-1F, -1F, -1F, 2, 6, 2, modelSize);
		bipedLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);

		bipedRightLeg = new ModelRenderer(this, 20, 12);
		bipedRightLeg.addBox(-1F, 0.0F, -1F, 2, 6, 2, modelSize);
		bipedRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);

		bipedLeftLeg = new ModelRenderer(this, 20, 12);
		bipedLeftLeg.mirror = true;
		bipedLeftLeg.addBox(-1F, 0.0F, -1F, 2, 6, 2, modelSize);
		bipedLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (!rogueParts)
		{
			strand2.showModel = !hairType;
			strand3.showModel = hairType;
			strand4.showModel = hairType;
			strand.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
		}

		if (showCrown && !rogueParts)
		{
			crown.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
		}

		if (!scoutWings && !rogueParts)
		{
			wingLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
			wingRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
		}
	}

	@Override
	public void setRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		strand.rotateAngleY = bipedHead.rotateAngleY;
		strand.rotateAngleX = bipedHead.rotateAngleX;
		crown.rotateAngleY = bipedHead.rotateAngleY;
		crown.rotateAngleX = bipedHead.rotateAngleX;

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

		// TODO: Figure out what `onGround` actually meant
		/*
		if (onGround > -9990F)
		{
			float f6 = onGround;
			bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
			wingLeft.rotateAngleY = wingRight.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
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
			wingLeft.rotateAngleX = f7 / 2.0F;
			wingRight.rotateAngleX = f7 / 2.0F;
			wingLeft.rotationPointY = 17.5F;
			wingRight.rotationPointY = 17.5F;
			wingLeft.rotationPointZ = 1.0F;
			wingRight.rotationPointZ = 1.0F;
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
			strand.rotationPointZ = -3F;
			strand.rotationPointY = 19.75F;
			crown.rotationPointZ = -3F;
			crown.rotationPointY = 19.75F;
		}
		else
		{
			bipedBody.rotateAngleX = 0.0F;
			bipedBody.rotationPointY = 12F;
			wingLeft.rotateAngleX = 0.0F;
			wingRight.rotateAngleX = 0.0F;
			wingLeft.rotationPointY = 12.5F;
			wingRight.rotationPointY = 12.5F;
			wingLeft.rotationPointZ = 1.0F;
			wingRight.rotationPointZ = 1.0F;
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
			strand.rotationPointZ = 0.0F;
			strand.rotationPointY = 12F;
			crown.rotationPointZ = 0.0F;
			crown.rotationPointY = 12F;
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
}
