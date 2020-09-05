package fairies.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fairies.entity.EntityFairy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class FairyGroupGenerator
{
    public FairyGroupGenerator(int min, int max, final int faction)
    {
        if (max < min)
        {
            final int p = max;
            max = min;
            min = p;
        }

        this.minSize = min;
        this.maxSize = max;
        this.faction = faction;
        this.radius = 8;
    }

    public boolean generate(final World world, final Random rand, final int i, final int j, final int k)
    {
        final List list = new ArrayList();
        int halfrad = radius / 2;
        final Block cordial = world.getBlock(i, j, k);

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
            final int x = i + rand.nextInt(radius) - rand.nextInt(radius);
            final int y = j + rand.nextInt(halfrad) - rand.nextInt(halfrad);
            final int z = k + rand.nextInt(radius) - rand.nextInt(radius);

            if (y < 0 || y > 126)
            {
                continue;
            }

            if (world.getBlock(x, y, z) == cordial && isAirySpace(world, x, y + 1, z))
            {
                list.add(new int[] {x, y + 1, z});
            }
        }

        if (list.size() < minSize)
        {
            return false;
        }

        final int disparity = (list.size() - minSize) + 1;
        final int actualSize = minSize + rand.nextInt(disparity);
        int guards = (minSize / 4) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
        int scouts = (minSize / 5) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
        int medics = (minSize / 5) + (rand.nextInt(maxSize - minSize + 1) < disparity ? 1 : 0);
        int specialFairy = 1; //Random

        for (int q = 0; q < actualSize; q++)
        {
            final int coords[] = (int[])list.get(q);
            final int x = coords[0];
            final int y = coords[1];
            final int z = coords[2];
            final double a = x + 0.45D + (rand.nextFloat() * 0.1D);
            final double b = y + 0.5D;
            final double c = z + 0.45D + (rand.nextFloat() * 0.1D);
            final EntityFairy fairy = new EntityFairy(world);
            fairy.setPosition(a, b, c);
            fairy.setFaction(faction);

            if (guards > 0)
            {
                guards--;
                fairy.setJob(1);
                fairy.setCower(false);
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
                fairy.setCower(false);
            }
            else
            {
                fairy.setJob(0);
            }

            world.spawnEntityInWorld(fairy);
        }

        return true;
    }

    public boolean isAirySpace(final World world, final int a, final int b, final int c)
    {
        if (b < 0 || b > 127)
        {
            return false;
        }

        final Block s = world.getBlock(a, b, c);

        if (s == Blocks.air)
        {
            return true;
        }
        else
        {
            final Material matt = s.getMaterial();

            if (matt == null || matt == Material.air || matt == Material.plants || matt == Material.vine ||
                    matt == Material.fire || matt == Material.circuits || matt == Material.snow)
            {
                return true;
            } //Material.field_35574_k is vines
        }

        return false;
    }

    private final int maxSize;
    private final int minSize;
    private final int faction;
    private int radius;
}
