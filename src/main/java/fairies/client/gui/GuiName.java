package fairies.client.gui;

import org.lwjgl.input.Keyboard;

import fairies.FairyFactions;
import fairies.entity.EntityFairy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiName extends GuiScreen
{
    protected String screenTitle;
    private EntityFairy fairy;
    private int updateCounter;
    private String nameText;

    public GuiName(EntityFairy entityfairy)
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
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120, "Done"));
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (fairy != null)
        {
            if (fairy.worldObj.isRemote)
            {
                //String s1 = "setfryname " + fairy.getEntityId() + " " + nameText;
                FairyFactions.proxy.sendFairyRename(fairy, nameText);
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

        if (ChatAllowedCharacters.isAllowedCharacter(c) && nameText.length() < 12)
        {
            nameText += c;
        }
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, screenTitle, width / 2, 40, 0xffffff);
        drawCenteredString(fontRendererObj, nameText, width / 2, 56, 0xffffff);
        super.drawScreen(i, j, f);
    }

}
