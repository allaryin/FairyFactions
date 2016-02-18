package fairies.old.client;

import java.util.List;
import org.lwjgl.opengl.GL11;

// Referenced classes of package fairies.old.client:
//            ModelBase, PositionTextureVertex, TexturedQuad, GLAllocation,
//            Tessellator

public class FRY_ModelRenderer
{
    public FRY_ModelRenderer(ModelBase modelbase, int i, int j)
    {
        field_35971_a = 64F;
        field_35970_b = 32F;
        compiled = false;
        displayList = 0;
        mirror = false;
        showModel = true;
        field_1402_i = false;
        textureOffsetX = i;
        textureOffsetY = j;
        modelbase.boxList.add(this);
    }

    public void addBox(float f, float f1, float f2, int i, int j, int k)
    {
        addBox(f, f1, f2, i, j, k, 0.0F);
    }

    public void addBox(float f, float f1, float f2, int i, int j, int k, float f3)
    {
        addBox(f, f1, f2, i, j, k, 0.0F, 0, 0.0F);
    }

    public void addBox(float f, float f1, float f2, int i, int j, int k, float f3, int carl, float sagan)
    {
        field_35977_i = f;
        field_35975_j = f1;
        field_35976_k = f2;
        field_35973_l = f + (float)i;
        field_35974_m = f1 + (float)j;
        field_35972_n = f2 + (float)k;
        field_35978_r = new PositionTextureVertex[8];
        faces = new TexturedQuad[6];
        float f4 = f + (float)i;
        float f5 = f1 + (float)j;
        float f6 = f2 + (float)k;
        f -= f3;
        f1 -= f3;
        f2 -= f3;
        f4 += f3;
        f5 += f3;
        f6 += f3;

        if (mirror)
        {
            float f7 = f4;
            f4 = f;
            f = f7;
        }

        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, f1, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, f1, f2, 0.0F, 8F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(f4 + sagan, f5, f2 - sagan, 8F, 8F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(f - sagan, f5, f2 - sagan, 8F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, f1, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, f1, f6, 0.0F, 8F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(f4 + sagan, f5, f6 + sagan, 8F, 8F);
        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(f - sagan, f5, f6 + sagan, 8F, 0.0F);

        if (carl == 2)
        {
            sagan = (f + f4) / 2F;
            float robert = (f2 + f6) / 2F;
            positiontexturevertex = new PositionTextureVertex(f, f1, f2, 0.0F, 0.0F);
            positiontexturevertex1 = new PositionTextureVertex(f4, f1, f2, 0.0F, 8F);
            positiontexturevertex2 = new PositionTextureVertex(sagan, f5, robert, 8F, 8F);
            positiontexturevertex3 = new PositionTextureVertex(sagan, f5, robert, 8F, 0.0F);
            positiontexturevertex4 = new PositionTextureVertex(f, f1, f6, 0.0F, 0.0F);
            positiontexturevertex5 = new PositionTextureVertex(f4, f1, f6, 0.0F, 8F);
            positiontexturevertex6 = new PositionTextureVertex(sagan, f5, robert, 8F, 8F);
            positiontexturevertex7 = new PositionTextureVertex(sagan, f5, robert, 8F, 0.0F);
        }

        field_35978_r[0] = positiontexturevertex;
        field_35978_r[1] = positiontexturevertex1;
        field_35978_r[2] = positiontexturevertex2;
        field_35978_r[3] = positiontexturevertex3;
        field_35978_r[4] = positiontexturevertex4;
        field_35978_r[5] = positiontexturevertex5;
        field_35978_r[6] = positiontexturevertex6;
        field_35978_r[7] = positiontexturevertex7;
        faces[0] = new TexturedQuad(new PositionTextureVertex[]
                {
                    positiontexturevertex5, positiontexturevertex1, positiontexturevertex2, positiontexturevertex6
                }, textureOffsetX + k + i, textureOffsetY + k, textureOffsetX + k + i + k, textureOffsetY + k + j, field_35971_a, field_35970_b);
        faces[1] = new TexturedQuad(new PositionTextureVertex[]
                {
                    positiontexturevertex, positiontexturevertex4, positiontexturevertex7, positiontexturevertex3
                }, textureOffsetX + 0, textureOffsetY + k, textureOffsetX + k, textureOffsetY + k + j, field_35971_a, field_35970_b);
        faces[2] = new TexturedQuad(new PositionTextureVertex[]
                {
                    positiontexturevertex5, positiontexturevertex4, positiontexturevertex, positiontexturevertex1
                }, textureOffsetX + k, textureOffsetY + 0, textureOffsetX + k + i, textureOffsetY + k, field_35971_a, field_35970_b);
        faces[3] = new TexturedQuad(new PositionTextureVertex[]
                {
                    positiontexturevertex2, positiontexturevertex3, positiontexturevertex7, positiontexturevertex6
                }, textureOffsetX + k + i, textureOffsetY + 0, textureOffsetX + k + i + i, textureOffsetY + k, field_35971_a, field_35970_b);
        faces[4] = new TexturedQuad(new PositionTextureVertex[]
                {
                    positiontexturevertex1, positiontexturevertex, positiontexturevertex3, positiontexturevertex2
                }, textureOffsetX + k, textureOffsetY + k, textureOffsetX + k + i, textureOffsetY + k + j, field_35971_a, field_35970_b);
        faces[5] = new TexturedQuad(new PositionTextureVertex[]
                {
                    positiontexturevertex4, positiontexturevertex5, positiontexturevertex6, positiontexturevertex7
                }, textureOffsetX + k + i + k, textureOffsetY + k, textureOffsetX + k + i + k + i, textureOffsetY + k + j, field_35971_a, field_35970_b);

        if (mirror)
        {
            for (int l = 0; l < faces.length; l++)
            {
                faces[l].flipFace();
            }
        }
    }

    public void setRotationPoint(float f, float f1, float f2)
    {
        rotationPointX = f;
        rotationPointY = f1;
        rotationPointZ = f2;
    }

    public void render(float f)
    {
        if (field_1402_i)
        {
            return;
        }

        if (!showModel)
        {
            return;
        }

        if (!compiled)
        {
            compileDisplayList(f);
        }

        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(rotationPointX * f, rotationPointY * f, rotationPointZ * f);

            if (rotateAngleZ != 0.0F)
            {
                GL11.glRotatef(rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (rotateAngleY != 0.0F)
            {
                GL11.glRotatef(rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (rotateAngleX != 0.0F)
            {
                GL11.glRotatef(rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            GL11.glCallList(displayList);
            GL11.glPopMatrix();
        }
        else if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F)
        {
            GL11.glTranslatef(rotationPointX * f, rotationPointY * f, rotationPointZ * f);
            GL11.glCallList(displayList);
            GL11.glTranslatef(-rotationPointX * f, -rotationPointY * f, -rotationPointZ * f);
        }
        else
        {
            GL11.glCallList(displayList);
        }
    }

    public void renderWithRotation(float f)
    {
        if (field_1402_i)
        {
            return;
        }

        if (!showModel)
        {
            return;
        }

        if (!compiled)
        {
            compileDisplayList(f);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(rotationPointX * f, rotationPointY * f, rotationPointZ * f);

        if (rotateAngleY != 0.0F)
        {
            GL11.glRotatef(rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
        }

        if (rotateAngleX != 0.0F)
        {
            GL11.glRotatef(rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
        }

        if (rotateAngleZ != 0.0F)
        {
            GL11.glRotatef(rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
        }

        GL11.glCallList(displayList);
        GL11.glPopMatrix();
    }

    public void postRender(float f)
    {
        if (field_1402_i)
        {
            return;
        }

        if (!showModel)
        {
            return;
        }

        if (!compiled)
        {
            compileDisplayList(f);
        }

        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F)
        {
            GL11.glTranslatef(rotationPointX * f, rotationPointY * f, rotationPointZ * f);

            if (rotateAngleZ != 0.0F)
            {
                GL11.glRotatef(rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (rotateAngleY != 0.0F)
            {
                GL11.glRotatef(rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (rotateAngleX != 0.0F)
            {
                GL11.glRotatef(rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }
        }
        else if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F)
        {
            GL11.glTranslatef(rotationPointX * f, rotationPointY * f, rotationPointZ * f);
        }
    }

    private void compileDisplayList(float f)
    {
        displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < faces.length; i++)
        {
            faces[i].draw(tessellator, f);
        }

        GL11.glEndList();
        compiled = true;
    }

    public FRY_ModelRenderer func_35968_a(int i, int j)
    {
        field_35971_a = i;
        field_35970_b = j;
        return this;
    }

    public void func_35969_a(FRY_ModelRenderer modelrenderer)
    {
        rotationPointX = modelrenderer.rotationPointX;
        rotationPointY = modelrenderer.rotationPointY;
        rotationPointZ = modelrenderer.rotationPointZ;
        rotateAngleX = modelrenderer.rotateAngleX;
        rotateAngleY = modelrenderer.rotateAngleY;
        rotateAngleZ = modelrenderer.rotateAngleZ;
    }

    public float field_35971_a;
    public float field_35970_b;
    private PositionTextureVertex field_35978_r[];
    private TexturedQuad faces[];
    private int textureOffsetX;
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public float field_35977_i;
    public float field_35975_j;
    public float field_35976_k;
    public float field_35973_l;
    public float field_35974_m;
    public float field_35972_n;
    private boolean compiled;
    private int displayList;
    public boolean mirror;
    public boolean showModel;
    public boolean field_1402_i;
}
