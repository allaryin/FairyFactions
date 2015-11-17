package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class FRY_ModelFairyEyes extends ModelBiped
{
    public FRY_ModelFairyEyes()
    {
        this(0.0F);
    }

    public FRY_ModelFairyEyes(float f)
    {
        this(f, 0.0F);
    }

    public FRY_ModelFairyEyes(float f, float f1)
    {
        heldItemLeft = 0;
        heldItemRight = 0;
        isSneak = false;
        flymode = false;
        bipedHead = new ModelRenderer(this, 40, 0);
        bipedHead.addBox(-3F, -6F, -3F, 6, 6, 6, f + 0.01F);
        bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    }

    public void render(Entity e, float f, float f1, float f2, float f3, float f4, float f5)
    {
        setRotationAngles(f, f1, f2, f3, f4, f5);
        bipedHead.render(f5);
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {
        bipedHead.rotateAngleY = f3 / (180F / (float)Math.PI);
        bipedHead.rotateAngleX = f4 / (180F / (float)Math.PI);

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

    public void renderEars(float f)
    {
    }

    public void renderCloak(float f)
    {
    }

    public boolean flymode;
}
