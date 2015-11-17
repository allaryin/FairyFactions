package net.minecraft.src;

import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class FRY_GuiName extends GuiScreen
{
    protected String screenTitle;
    private FRY_EntityFairy fairy;
    private int updateCounter;
    private static final String allowedCharacters;
    private String nameText;

    public FRY_GuiName(FRY_EntityFairy entityfairy)
    {
        screenTitle = "Enter custom name or leave blank for default:";
        fairy = entityfairy;
        nameText = "";

        if (fairy != null && fairy.getCustomName() != null)
        {
            nameText = fairy.getCustomName();
        }
    }

    public void initGui()
    {
        controlList.clear();
        Keyboard.enableRepeatEvents(true);
        controlList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120, "Done"));
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (fairy != null)
        {
            if (fairy.worldObj.isRemote)
            {
                String s1 = "setfryname " + fairy.entityId + " " + nameText;
                mod_FairyMod.fairyMod.sendFairyRename(s1);
            }
            else
            {
                fairy.setCustomName(nameText);
            }
        }
    }

    public void updateScreen()
    {
        updateCounter++;

        if (fairy == null || fairy.isDead)
        {
            mc.displayGuiScreen(null);
        }
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if (!guibutton.enabled)
        {
            return;
        }

        if (guibutton.id == 0)
        {
            mc.displayGuiScreen(null);
        }
    }

    protected void keyTyped(char c, int i)
    {
        if (i == 28)
        {
            mc.displayGuiScreen(null);
        }

        if (i == 14 && nameText.length() > 0)
        {
            nameText = nameText.substring(0, nameText.length() - 1);
        }

        if (allowedCharacters.indexOf(c) >= 0 && nameText.length() < 12)
        {
            nameText += c;
        }
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, screenTitle, width / 2, 40, 0xffffff);
        drawCenteredString(fontRenderer, nameText, width / 2, 56, 0xffffff);
        super.drawScreen(i, j, f);
    }

    static
    {
        allowedCharacters = ChatAllowedCharacters.allowedCharacters;
    }
}
