package fairies.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelFairyProps extends ModelBiped
{
    public ModelFairyProps()
    {
        this(0.0F);
    }

    public ModelFairyProps(final float f)
    {
        this(f, 0.0F);
    }

    public ModelFairyProps(final float f, final float f1)
    {
        heldItemLeft = 0;
        heldItemRight = 0;
        isSneak = false;
        flymode = false;
        jobType = 0;
        //Guard Type
        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.addBox(-3F, -6.25F, -3F, 6, 2, 6, f + 0.2F);
        bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        bipedBody = new ModelRenderer(this);
        bipedBody.setTextureOffset(8, 8).addBox(-2F, 0.0F, -1F, 4, 5, 2, f + 0.25F);
        bipedBody.setTextureOffset(0, 16).addBox(-2F, 1.0F, -2F, 4, 2, 1, f + 0.25F);
        bipedBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        bipedRightArm = new ModelRenderer(this, 0, 8);
        bipedRightArm.addBox(-1F, -1F, -1F, 2, 2, 2, f + 0.375F);
        bipedRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);
        bipedLeftArm = new ModelRenderer(this, 0, 8);
        bipedLeftArm.mirror = true;
        bipedLeftArm.addBox(-1F, -1F, -1F, 2, 2, 2, f + 0.375F);
        bipedLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);
        bipedRightLeg = new ModelRenderer(this, 0, 12);
        bipedRightLeg.addBox(-1F, 4.0F, -1F, 2, 2, 2, f + 0.25F);
        bipedRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);
        bipedLeftLeg = new ModelRenderer(this, 0, 12);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.addBox(-1F, 4.0F, -1F, 2, 2, 2, f + 0.25F);
        bipedLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);
        //Scout Type
        scoutHead = new ModelRenderer(this);
        scoutHead.setTextureOffset(0, 0).addBox(0, 0, 0, 1, 1, 1, f + 0.2F);
        scoutHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        scoutBody = new ModelRenderer(this);
        scoutBody.setTextureOffset(28, 7).addBox(-2F, 0.0F, -1F, 4, 6, 2, f + 0.125F);
        scoutBody.setTextureOffset(28, 15).addBox(-2F, 1.0F, -2F, 4, 2, 1, f + 0.125F);
        scoutBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        scoutRightArm = new ModelRenderer(this, 20, 8);
        scoutRightArm.addBox(-1F, -1F, -1F, 2, 3, 2, f + 0.125F);
        scoutRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);
        scoutLeftArm = new ModelRenderer(this, 20, 8);
        scoutLeftArm.mirror = true;
        scoutLeftArm.addBox(-1F, -1F, -1F, 2, 3, 2, f + 0.125F);
        scoutLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);
        scoutRightLeg = new ModelRenderer(this, 20, 13);
        scoutRightLeg.addBox(-1F, 0.0F, -1F, 2, 3, 2, f + 0.125F);
        scoutRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);
        scoutLeftLeg = new ModelRenderer(this, 20, 13);
        scoutLeftLeg.mirror = true;
        scoutLeftLeg.addBox(-1F, 0.0F, -1F, 2, 3, 2, f + 0.125F);
        scoutLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);
        wingRight = new ModelRenderer(this, 43, 15);
        wingRight.addBox(0F, -1.75F, -1.0F, 6, 6, 1, f + 0.25F);
        wingRight.setRotationPoint(0.5F, 0.0F + f1, 1.0F);
        wingLeft = new ModelRenderer(this, 43, 15);
        wingLeft.mirror = true;
        wingLeft.addBox(-6F, -1.75F, -1.0F, 6, 6, 1, f + 0.25F);
        wingLeft.setRotationPoint(-0.5F, 0.0F + f1, 1.0F);
        //Medic Type
        medicHead = new ModelRenderer(this);
        medicHead.setTextureOffset(0, 19).addBox(-3F, -5.5F, -3F, 6, 1, 6, f + 0.2F);
        medicHead.setTextureOffset(10, 15).addBox(-1.5F, -7.0F, -3.125F, 3, 3, 1, f + 0.2F);
        medicHead.setTextureOffset(24, 0).addBox(-3F, -3.5F, -3F, 6, 1, 6, f + 0.2F);
        medicHead.setTextureOffset(42, 3).addBox(-2.5F, -4F, -3.5F, 2, 2, 1, f + 0.2F);
        medicHead.setTextureOffset(42, 3).addBox(0.5F, -4F, -3.5F, 2, 2, 1, f + 0.2F);
        medicHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        medicBody = new ModelRenderer(this);
        medicBody.setTextureOffset(28, 18).addBox(-2F, 0.0F, -1F, 4, 6, 2, f + 0.125F);
        medicBody.setTextureOffset(18, 18).addBox(-2F, 1.0F, -2F, 4, 2, 1, f + 0.125F);
        medicBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    }

    @Override
	public void render(final Entity e, final float f, final float f1, final float f2, final float f3, final float f4, final float f5)
    {
        GL11.glPushMatrix();

        if (jobType == 0)
        {
            setRotationAngles(f, f1, f2, f3, f4, f5);
            bipedHead.render(f5);
            bipedBody.render(f5);
            bipedRightArm.render(f5);
            bipedLeftArm.render(f5);
            bipedRightLeg.render(f5);
            bipedLeftLeg.render(f5);
        }
        else if (jobType == 1)
        {
            setScoutRotationAngles(f, f1, f2, f3, f4, f5);
            scoutHead.render(f5);
            scoutBody.render(f5);
            scoutRightArm.render(f5);
            scoutLeftArm.render(f5);
            scoutRightLeg.render(f5);
            scoutLeftLeg.render(f5);
            wingLeft.render(f5);
            wingRight.render(f5);
        }
        else
        {
            setMedicRotationAngles(f, f1, f2, f3, f4, f5);
            medicHead.render(f5);
            medicBody.render(f5);
        }

        GL11.glPopMatrix();
    }

    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5)
    {
        bipedHead.rotateAngleY = f3 / (180F / (float)Math.PI);
        bipedHead.rotateAngleX = f4 / (180F / (float)Math.PI);

        if (!flymode)
        {
            bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * f1 * 0.5F;
            bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
            bipedRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
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

        if ((isRiding || isSneak) && !flymode)
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

        if (heldItemLeft != 0)
        {
            bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        if (heldItemRight != 0)
        {
            bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        bipedRightArm.rotateAngleY = 0.0F;
        bipedLeftArm.rotateAngleY = 0.0F;

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

            if (isRiding)
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
            bipedRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.1F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.1F;
            bipedRightLeg.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            bipedLeftLeg.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            bipedRightLeg.rotateAngleX = 0.1F;
            bipedLeftLeg.rotateAngleX = 0.1F;
        }
        else
        {
            bipedRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
        }
    }

    public void setScoutRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5)
    {
        scoutHead.rotateAngleY = f3 / (180F / (float)Math.PI);
        scoutHead.rotateAngleX = f4 / (180F / (float)Math.PI);

        if (!flymode)
        {
            scoutRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * f1 * 0.5F;
            scoutLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
            scoutRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
            scoutLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
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

        if ((isRiding || isSneak) && !flymode)
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

        if (heldItemLeft != 0)
        {
            scoutLeftArm.rotateAngleX = scoutLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        if (heldItemRight != 0)
        {
            scoutRightArm.rotateAngleX = scoutRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        scoutRightArm.rotateAngleY = 0.0F;
        scoutLeftArm.rotateAngleY = 0.0F;

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

            if (isRiding)
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
            scoutRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            scoutLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            scoutRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.1F;
            scoutLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.1F;
            scoutRightLeg.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            scoutLeftLeg.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            scoutRightLeg.rotateAngleX = 0.1F;
            scoutLeftLeg.rotateAngleX = 0.1F;
        }
        else
        {
            scoutRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
            scoutLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
            scoutRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
            scoutLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
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

    public void setMedicRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5)
    {
        medicHead.rotateAngleY = f3 / (180F / (float)Math.PI);
        medicHead.rotateAngleX = f4 / (180F / (float)Math.PI);

        if (onGround > -9990F)
        {
            final float f6 = onGround;
            medicBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float)Math.PI * 2.0F) * 0.2F;
        }

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

    @Override
	public void renderEars(final float f)
    {
    }

    @Override
	public void renderCloak(final float f)
    {
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
