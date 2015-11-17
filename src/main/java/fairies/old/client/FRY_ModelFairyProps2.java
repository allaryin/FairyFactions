package fairies.old.client;

import org.lwjgl.opengl.GL11;

public class FRY_ModelFairyProps2 extends ModelBiped
{
    public FRY_ModelFairyProps2()
    {
        this(0.0F);
    }

    public FRY_ModelFairyProps2(float f)
    {
        this(f, 0.0F);
    }

    public FRY_ModelFairyProps2(float f, float f1)
    {
        heldItemLeft = 0;
        heldItemRight = 0;
        isSneak = false;
        flymode = false;
        retract = 0F;
        //Rogue Type
        rogueHead = new ModelRenderer(this);
        rogueHead.setTextureOffset(0, 0).addBox(-3F, -5.7F, -3F, 6, 6, 6, f + 0.4F);
        rogueHead.mirror = true;
        rogueHead.setTextureOffset(0, 0).addBox(-3F, -6.0F, -3F, 6, 6, 6, f + 0.125F);
        rogueHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        rogueBody = new ModelRenderer(this);
        rogueBody.setTextureOffset(0, 19).addBox(-2F, 0.0F, -1F, 4, 6, 2, f + 0.375F);
        rogueBody.setTextureOffset(12, 19).addBox(-2F, 1.0F, -2F, 4, 2, 1, f + 0.375F);
        rogueBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        rogueRightArm = new ModelRenderer(this, 0, 12);
        rogueRightArm.addBox(-1F, -1F, -1F, 2, 5, 2, f + 0.375F);
        rogueRightArm.setRotationPoint(-5F, 1.0F + f1, 0.0F);
        rogueLeftArm = new ModelRenderer(this, 0, 12);
        rogueLeftArm.mirror = true;
        rogueLeftArm.addBox(-1F, -1F, -1F, 2, 5, 2, f + 0.375F);
        rogueLeftArm.setRotationPoint(5F, 1.0F + f1, 0.0F);
        rogueRightLeg = new ModelRenderer(this, 12, 12);
        rogueRightLeg.addBox(-1F, 0.0F, -1F, 2, 5, 2, f + 0.375F);
        rogueRightLeg.setRotationPoint(-1F, 18F + f1, 0.0F);
        rogueLeftLeg = new ModelRenderer(this, 12, 12);
        rogueLeftLeg.mirror = true;
        rogueLeftLeg.addBox(-1F, 0.0F, -1F, 2, 5, 2, f + 0.375F);
        rogueLeftLeg.setRotationPoint(1.0F, 18F + f1, 0.0F);
        wingRight = new ModelRenderer(this, 12, 25);
        wingRight.addBox(0F, -1.75F, -1.0F, 6, 6, 1, f + 0.25F);
        wingRight.setRotationPoint(0.5F, 0.0F + f1, 1.0F);
        wingLeft = new ModelRenderer(this, 12, 25);
        wingLeft.mirror = true;
        wingLeft.addBox(-6F, -1.75F, -1.0F, 6, 6, 1, f + 0.25F);
        wingLeft.setRotationPoint(-0.5F, 0.0F + f1, 1.0F);
        //Blades, Yo.
        rogueBlade1 = new FRY_ModelRenderer(this, 24, 0);
        rogueBlade1.addBox(-1.5F, 4F, -1F, 1, 4, 2, f - 0.2F, 2, 0F);
        rogueBlade1.setRotationPoint(-5F, 1.0F + f1, 0.0F);
        rogueBlade2 = new FRY_ModelRenderer(this, 30, 0);
        rogueBlade2.addBox(-1.5F, 4F, -1F, 1, 4, 2, f - 0.2F, 2, 0F);
        rogueBlade2.setRotationPoint(-5F, 1.0F + f1, 0.0F);
    }

    public void render(Entity e, float f, float f1, float f2, float f3, float f4, float f5)
    {
        setRotationAngles(f, f1, f2, f3, f4, f5);
        rogueHead.render(f5);
        rogueBody.render(f5);
        rogueRightArm.render(f5);
        rogueLeftArm.render(f5);
        rogueRightLeg.render(f5);
        rogueLeftLeg.render(f5);
        wingLeft.render(f5);
        wingRight.render(f5);

        if (venom)
        {
            rogueBlade2.render(f5);
        }
        else
        {
            rogueBlade1.render(f5);
        }
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {
        rogueHead.rotateAngleY = f3 / (180F / (float)Math.PI);
        rogueHead.rotateAngleX = f4 / (180F / (float)Math.PI);

        if (!flymode)
        {
            rogueRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * f1 * 0.5F;
            rogueLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F;
            rogueRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
            rogueLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
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

        if ((isRiding || isSneak) && !flymode)
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

        if (heldItemLeft != 0)
        {
            rogueLeftArm.rotateAngleX = rogueLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        if (heldItemRight != 0)
        {
            rogueRightArm.rotateAngleX = rogueRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
        }

        rogueRightArm.rotateAngleY = 0.0F;
        rogueLeftArm.rotateAngleY = 0.0F;

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
            float f8 = MathHelper.sin(f6 * (float)Math.PI);
            float f9 = MathHelper.sin(onGround * (float)Math.PI) * -(rogueHead.rotateAngleX - 0.7F) * 0.75F;
            rogueRightArm.rotateAngleX -= (double)f8 * 1.2D + (double)f9;
            rogueRightArm.rotateAngleY += rogueBody.rotateAngleY * 2.0F;
            rogueRightArm.rotateAngleZ = MathHelper.sin(onGround * (float)Math.PI) * -0.4F;
        }

        if (flymode)
        {
            float f7 = (float)Math.PI;
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

            if (isRiding)
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
            rogueRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            rogueLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            rogueRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.1F;
            rogueLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.1F;
            rogueRightLeg.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            rogueLeftLeg.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.1F + 0.05F;
            rogueRightLeg.rotateAngleX = 0.1F;
            rogueLeftLeg.rotateAngleX = 0.1F;
        }
        else
        {
            rogueRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
            rogueLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
            rogueRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
            rogueLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
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
        rogueBlade1.rotateAngleX = rogueBlade2.rotateAngleX = rogueRightArm.rotateAngleX;
        rogueBlade1.rotateAngleY = rogueBlade2.rotateAngleY = rogueRightArm.rotateAngleY;
        rogueBlade1.rotateAngleZ = rogueBlade2.rotateAngleZ = rogueRightArm.rotateAngleZ;
        rogueBlade1.rotationPointX = rogueBlade2.rotationPointX = rogueRightArm.rotationPointX;
        rogueBlade1.rotationPointY = rogueBlade2.rotationPointY = rogueRightArm.rotationPointY;
        rogueBlade1.rotationPointZ = rogueBlade2.rotationPointZ = rogueRightArm.rotationPointZ;
    }

    public void renderEars(float f)
    {
    }

    public void renderCloak(float f)
    {
    }

    public ModelRenderer rogueHead;
    public ModelRenderer rogueBody;
    public ModelRenderer rogueLeftArm;
    public ModelRenderer rogueRightArm;
    public ModelRenderer rogueLeftLeg;
    public ModelRenderer rogueRightLeg;

    public ModelRenderer wingLeft;
    public ModelRenderer wingRight;

    public FRY_ModelRenderer rogueBlade1, rogueBlade2;

    public boolean flymode;
    public float sinage;
    public float retract;
    public boolean venom;
}
