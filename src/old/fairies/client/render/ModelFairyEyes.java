package fairies.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFairyEyes extends ModelBiped
{
    public ModelFairyEyes()
    {
        this(0.0F);
    }

    public ModelFairyEyes(final float f)
    {
        this(f, 0.0F);
    }

    public ModelFairyEyes(final float f, final float f1)
    {
        heldItemLeft = 0;
        heldItemRight = 0;
        isSneak = false;
        flymode = false;
        bipedHead = new ModelRenderer(this, 40, 0);
        bipedHead.addBox(-3F, -6F, -3F, 6, 6, 6, f + 0.01F);
        bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    }

    @Override
	public void render(final Entity e, final float f, final float f1, final float f2, final float f3, final float f4, final float f5)
    {
        setRotationAngles(f, f1, f2, f3, f4, f5);
        bipedHead.render(f5);
    }

    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4, final float f5)
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

    @Override
	public void renderEars(final float f)
    {
    }

    @Override
	public void renderCloak(final float f)
    {
    }

    public boolean flymode;
}
