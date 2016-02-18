package fairies.old.server;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class FRY_FairyGroup
{
    public FRY_FairyGroup(int i, int j, int k)
    {
        if (j < i)
        {
            int p = j;
            j = i;
            i = p;
        }

        minSize = i;
        maxSize = j;
        faction = k;
        radius = 8;
    }

    public boolean generate(World world, Random rand, int i, int j, int k)
    {
        List list = new ArrayList();
        int halfrad = radius / 2;
        int cordial = world.getBlockId(i, j, k);

        if (radius < 1)
        {
            radius = 1;
        }

        if (halfrad < 1)
        {
            halfrad = 1;
        }

        for (int q = 0; q < 128 && list.size() < maxSize; q++)
        {
            int x = i + rand.nextInt(radius) - rand.nextInt(radius);
            int y = j + rand.nextInt(halfrad) - rand.nextInt(halfrad);
            int z = k + rand.nextInt(radius) - rand.nextInt(radius);

            if (y < 0 || y > 126)
            {
                continue;
            }

            if (world.getBlockId(x, y, z) == cordial && isAirySpace(world, x, y + 1, z))
            {
                list.add(new int[] {x, y + 1, z});
            }
        }

        if (list.size() < minSize)
        {
            return false;
        }

        int disparity = (list.size() - minSize) + 1;
        int actualSize = minSize + rand.nextInt(disparity);
        int guards = (minSize / 4) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
        int scouts = (minSize / 5) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
        int medics = (minSize / 5) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
        int specialFairy = 1; //Random

        for (int q = 0; q < actualSize; q++)
        {
            int coords[] = (int[])list.get(q);
            int x = coords[0];
            int y = coords[1];
            int z = coords[2];
            double a = x + 0.45D + (rand.nextFloat() * 0.1D);
            double b = y + 0.5D;
            double c = z + 0.45D + (rand.nextFloat() * 0.1D);
            FRY_EntityFairy fairy = new FRY_EntityFairy(world);
            fairy.setPosition(a, b, c);
            fairy.setFaction(faction);

            if (guards > 0)
            {
                guards--;
                fairy.setJob(1);
                fairy.cower = false;
            }
            else if (scouts > 0)
            {
                scouts --;
                fairy.setJob(2);
            }
            else if (medics > 0)
            {
                medics --;
                fairy.setJob(3);
                fairy.setCanHeal(true);
                fairy.setRarePotion(rand.nextInt(4) == 0);
            }
            else if (specialFairy == 1)
            {
                specialFairy = 0;
                fairy.setJob(1);
                fairy.setCanHeal(true);
                fairy.setSpecialJob(true);
                fairy.cower = false;
            }
            else
            {
                fairy.setJob(0);
            }

            world.spawnEntityInWorld(fairy);
        }

        return true;
    }

    public boolean isAirySpace(World world, int a, int b, int c)
    {
        if (b < 0 || b > 127)
        {
            return false;
        }

        int s = world.getBlockId(a, b, c);

        if (s == 0)
        {
            return true;
        }
        else
        {
            Block jock = Block.blocksList[s];
            Material matt = jock.blockMaterial;

            if (matt == null || matt == Material.air || matt == Material.plants || matt == Material.vine ||
                    matt == Material.fire || matt == Material.circuits || matt == Material.snow)
            {
                return true;
            } //Material.field_35574_k is vines
        }

        return false;
    }

    private int maxSize;
    private int minSize;
    private int faction;
    private int radius;
}
