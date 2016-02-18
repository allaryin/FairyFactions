package fairies.old.client;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class FRY_RenderFairy extends RenderLiving
{
    public FRY_RenderFairy(FRY_ModelFairy modelfairy, float f)
    {
        super(modelfairy, f);
        fairyModel = modelfairy;
        fairyModel2 = new FRY_ModelFairyProps();
        fairyModel3 = new FRY_ModelFairyEyes();
        fairyModel4 = new FRY_ModelFairy(0.015625F);
        fairyModel5 = new FRY_ModelFairyProps2();
    }

    protected void preRenderCallback(EntityLiving entityliving, float f)
    {
        FRY_EntityFairy fairy = (FRY_EntityFairy)entityliving;
        float f1 = 0.875F;
        fairyModel.sinage = fairy.sinage;
        fairyModel.flymode = fairy.flymode();
        fairyModel.showCrown = fairy.tamed() || fairy.queen();
        fairyModel.isSneak = fairy.getFlag(1);
        fairyModel.scoutWings = fairy.scout();
        fairyModel.rogueParts = fairy.rogue();
        fairyModel.hairType = fairy.hairType();
        GL11.glScalef(f1, f1, f1);

        if (entityliving.getFlag(1))
        {
            GL11.glTranslatef(0F, (5F / 16F), 0F);
        }
    }

    protected void renderEquippedItems(EntityLiving entityliving, float f)
    {
        ItemStack itemstack = entityliving.getHeldItem();

        if (itemstack != null)
        {
            GL11.glPushMatrix();
            fairyModel.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(0.0F, 0.1F, 0.0F);

            if (itemstack.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
            {
                float f1 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f1 *= 0.75F;
                GL11.glRotatef(20F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f1, -f1, f1);
            }
            else if (Item.itemsList[itemstack.itemID].isFull3D())
            {
                float f2 = 0.625F;
                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                float f3 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f3, f3, f3);
                GL11.glRotatef(60F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20F, 0.0F, 0.0F, 1.0F);
            }

            if (itemstack.itemID == Item.potion.shiftedIndex)
            {
                int j = itemstack.getItem().getColorFromDamage(itemstack.getItemDamage(), 0);
                float f9 = (float)(j >> 16 & 0xff) / 255F;
                float f10 = (float)(j >> 8 & 0xff) / 255F;
                float f11 = (float)(j & 0xff) / 255F;
                GL11.glColor4f(f9, f10, f11, 1.0F);
                renderManager.itemRenderer.renderItem(entityliving, itemstack, 0);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                renderManager.itemRenderer.renderItem(entityliving, itemstack, 1);
            }
            else
            {
                renderManager.itemRenderer.renderItem(entityliving, itemstack, 0);
            }

            GL11.glPopMatrix();
        }
    }

    protected int setFairyBrightness(FRY_EntityFairy fairy, int i, float f)
    {
        if (i == 0 && (fairy.withered() || fairy.rogue()))  //Render Withered Skin.
        {
            float transp = 0.7F;

            if (fairy.queen())
            {
                if (fairy.getSkin() > 1)
                {
                    loadTexture("/fairy/fairyWithered3.png");
                }
                else
                {
                    loadTexture("/fairy/fairyWithered2.png");
                }
            }
            else
            {
                loadTexture("/fairy/fairyWithered1.png");
            }

            setRenderPassModel(fairyModel4);
            fairyModel4.sinage = fairy.sinage;
            fairyModel4.flymode = fairy.flymode();
            fairyModel4.showCrown = fairy.tamed() || fairy.queen();
            fairyModel4.isSneak = fairy.getFlag(1);
            fairyModel4.scoutWings = fairy.scout();
            fairyModel4.onGround = fairyModel.onGround;
            fairyModel4.rogueParts = fairy.rogue();
            fairyModel4.hairType = fairy.hairType();
            GL11.glColor4f(0.7F, 0.7F, 0.7F, transp);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        }
        else if (i == 1)    //Render Fairy Eyes.
        {
            loadTexture(fairy.getTexture(fairy.getSkin()));
            float transp = 1.0F - ((float)fairy.fairyHealth() / (float)(fairy.getMaxHealth()));

            if (transp < 0.1F)
            {
                return -1;
            }

            setRenderPassModel(fairyModel3);
            fairyModel3.flymode = fairy.flymode();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, transp);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            return 1;
        }
        else if (i == 2 && !fairy.queen() && !fairy.normal())    //Render Armor Overlay.
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);

            if (fairy.rogue())
            {
                setRenderPassModel(fairyModel5);
                loadTexture("/fairy/fairyProps2.png");
                fairyModel5.flymode = fairy.flymode();
                fairyModel5.retract = 0F;
                fairyModel5.isSneak = fairy.getFlag(1);
                fairyModel5.sinage = fairy.sinage;
                fairyModel5.onGround = fairyModel.onGround;
                fairyModel5.venom = fairy.canHeal();
            }
            else
            {
                setRenderPassModel(fairyModel2);
                loadTexture("/fairy/fairyProps.png");
                fairyModel2.flymode = fairy.flymode();
                fairyModel2.jobType = fairy.getJob() - 1;
                fairyModel2.isSneak = fairy.getFlag(1);
                fairyModel2.sinage = fairy.sinage;
                fairyModel2.onGround = fairyModel.onGround;
            }

            return 1;
        }
        else
        {
            GL11.glDisable(GL11.GL_BLEND);
            return -1;
        }
    }

    protected void passSpecialRender(EntityLiving entityliving, double d, double d1, double d2)
    {
        renderFairyName((FRY_EntityFairy)entityliving, d, d1, d2);
    }

    protected void renderFairyName(FRY_EntityFairy fairy, double d, double d1, double d2)
    {
        if (Minecraft.isGuiEnabled() && fairy != renderManager.livingPlayer)
        {
            float f = 1.6F;
            float f1 = 0.01666667F * f;
            float f2 = fairy.getDistanceToEntity(renderManager.livingPlayer);
            float f3 = 12F;

            if (f2 < f3)
            {
                String s = fairy.getDisplayName();

                if (s != null)
                {
                    renderLivingLabel(fairy, s, d, d1 - (fairy.flymode() ? 1.125D : 0.825D), d2, 64);
                }
            }
        }
    }

    protected int shouldRenderPass(EntityLiving entityliving, int i, float f)
    {
        return setFairyBrightness((FRY_EntityFairy)entityliving, i, f);
    }

    protected void renderLivingLabel(EntityLiving par1EntityLiving, String par2Str, double par3, double par5, double par7, int par9)
    {
        float f = par1EntityLiving.getDistanceToEntity(renderManager.livingPlayer);

        if (f > (float)par9)
        {
            return;
        }

        FontRenderer fontrenderer = getFontRendererFromRenderManager();
        float f1 = 1.6F;
        float f2 = 0.01666667F * f1;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par3 + 0.0F, (float)par5 + 2.3F, (float)par7);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f2, -f2, f2);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.instance;
        byte byte0 = 0;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        int i = fontrenderer.getStringWidth(fontrenderer.stripColorCodes(par2Str)) / 2;
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex(-i - 1, -1 + byte0, 0.0D);
        tessellator.addVertex(-i - 1, 8 + byte0, 0.0D);
        tessellator.addVertex(i + 1, 8 + byte0, 0.0D);
        tessellator.addVertex(i + 1, -1 + byte0, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(fontrenderer.stripColorCodes(par2Str)) / 2, byte0, 0x20ffffff);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fontrenderer.drawString(par2Str, -fontrenderer.getStringWidth(fontrenderer.stripColorCodes(par2Str)) / 2, byte0, -1);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    protected FRY_ModelFairy fairyModel, fairyModel4; //Body and withered overlay
    protected FRY_ModelFairyProps fairyModel2; //Clothes and stuff
    protected FRY_ModelFairyEyes fairyModel3; //Eyes
    protected FRY_ModelFairyProps2 fairyModel5; //Rogue Clothes
}
