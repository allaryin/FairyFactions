package fairies.old.server;

import java.util.List;
import java.util.ArrayList;

public class FRY_FairyJob
{
    public FRY_FairyJob(FRY_EntityFairy entityfairy)
    {
        fairy = entityfairy;
    }

    public void discover(World world)
    {
        if (fairy.entityToAttack != null || fairy.entityFear != null || fairy.cryTime > 0 || fairy.attackTime > 0 || fairy.health <= 0)
        {
            return;
        }

        int x = MathHelper.floor_double(fairy.posX);
        int y = MathHelper.floor_double(fairy.boundingBox.minY);

        if (fairy.flymode())
        {
            y--;
        }

        int z = MathHelper.floor_double(fairy.posZ);

        if (y < 0 || y >= world.getHeight())
        {
            return;
        }

        goodies = getGoodies(world);
        getNearbyChest2(x, y, z, world);
    }

    public void sittingFishing(World world)
    {
        if (fairy.entityToAttack != null || fairy.entityFear != null || fairy.cryTime > 0 || fairy.attackTime > 0 || fairy.health <= 0)
        {
            return;
        }

        int x = MathHelper.floor_double(fairy.posX);
        int y = MathHelper.floor_double(fairy.boundingBox.minY);

        if (fairy.flymode())
        {
            y--;
        }

        int z = MathHelper.floor_double(fairy.posZ);

        if (y < 0 || y >= world.getHeight())
        {
            return;
        }

        getNearbyChest3(x, y, z, world);
    }

    public ArrayList getGoodies(World world)
    {
        List list = world.getEntitiesWithinAABB(EntityItem.class, fairy.boundingBox.expand(2.5D, 2.5D, 2.5D));
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < list.size(); i++)
        {
            EntityItem entity1 = (EntityItem)list.get(i);

            if (entity1.item != null && entity1.delayBeforeCanPickup <= 0)
            {
                ItemStack stack = entity1.item;

                if (stack.stackSize > 0 && goodItem(stack.itemID, stack.getItemDamage()))
                {
                    list2.add(entity1);
                }
            }
        }

        if (list2.size() <= 0)
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    public ArrayList getAnimals(World world)
    {
        List list = world.getEntitiesWithinAABB(EntityAnimal.class, fairy.boundingBox.expand(5D, 5D, 5D));

        if (list.size() < 2)
        {
            return null;
        }

        ArrayList list2 = new ArrayList();

        for (int i = 0; i < list.size(); i++)
        {
            EntityAnimal entity1 = (EntityAnimal)list.get(i);

            if (fairy.peacefulAnimal(entity1) && fairy.canEntityBeSeen(entity1) && entity1.health > 0 && entity1.getEntityToAttack() == null &&
                    entity1.fleeingTick <= 0 && !entity1.isInLove() && entity1.getGrowingAge() == 0)
            {
                for (int j = 0; j < list.size(); j++)
                {
                    EntityAnimal entity2 = (EntityAnimal)list.get(j);

                    if (entity1 != entity2 && entity1.getClass() == entity2.getClass() && entity2.getGrowingAge() == 0)
                    {
                        list2.add(entity1);
                    }
                }
            }
        }

        if (list2.size() <= 0)
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    public ArrayList getSheep(World world)
    {
        List list = world.getEntitiesWithinAABB(EntitySheep.class, fairy.boundingBox.expand(5D, 5D, 5D));

        if (list.size() < 1)
        {
            return null;
        }

        ArrayList list2 = new ArrayList();

        for (int i = 0; i < list.size(); i++)
        {
            EntitySheep entity1 = (EntitySheep)list.get(i);

            if (fairy.canEntityBeSeen(entity1) && entity1.health > 0 && entity1.getEntityToAttack() == null &&
                    entity1.fleeingTick <= 0 && entity1.getGrowingAge() >= 0 && !entity1.getSheared())
            {
                list2.add(entity1);
            }
        }

        if (list2.size() <= 0)
        {
            return null;
        }
        else
        {
            return list2;
        }
    }

    private static final int radius = 5;
    private void getNearbyChest2(int x, int y, int z, World world)
    {
        int i, j, k;

        for (int a = -radius; a <= radius; a++)
        {
            for (int b = -2; b <= 2; b++)
            {
                for (int c = -radius; c <= radius; c++)
                {
                    i = x + a;
                    j = y + b;
                    k = z + c;

                    if (world.getBlockId(i, j, k) == Block.chest.blockID)
                    {
                        TileEntity tent = world.getBlockTileEntity(i, j, k);

                        if (tent != null && tent instanceof TileEntityChest)
                        {
                            TileEntityChest chest = (TileEntityChest)tent;

                            if (goodies != null && collectGoodies(chest, world))
                            {
                                fairy.postedCount = 2;
                                return;
                            }

                            for (int p = 0; p < chest.getSizeInventory(); p++)
                            {
                                if (checkChestItem(chest.getStackInSlot(p), x, y, z, world))
                                {
                                    cleanSlot(chest, p);
                                    fairy.postedCount = 2;
                                    return;
                                }
                            }

                            if (miscActions(chest, x, y, z, world))
                            {
                                fairy.postedCount = 2;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void getNearbyChest3(int x, int y, int z, World world)
    {
        int i, j, k;

        for (int a = -radius; a <= radius; a++)
        {
            for (int b = -2; b <= 2; b++)
            {
                for (int c = -radius; c <= radius; c++)
                {
                    i = x + a;
                    j = y + b;
                    k = z + c;

                    if (world.getBlockId(i, j, k) == Block.chest.blockID)
                    {
                        TileEntity tent = world.getBlockTileEntity(i, j, k);

                        if (tent != null && tent instanceof TileEntityChest)
                        {
                            triedBreeding = false;
                            triedShearing = false;
                            TileEntityChest chest = (TileEntityChest)tent;

                            for (int p = 0; p < chest.getSizeInventory(); p++)
                            {
                                ItemStack stack = chest.getStackInSlot(p);

                                if (stack == null)
                                {
                                }
                                else if (stack.itemID > 0 && stack.stackSize > 0)
                                {
                                    if (isFishingItem(stack.itemID) && onFishingUse(stack, x, y, z, world))
                                    {
                                        cleanSlot(chest, p);
                                        fairy.postedCount = 2;
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkChestItem(ItemStack stack, int x, int y, int z, World world)   //Actions related to specific items.
    {
        if (stack == null)
        {
        }
        else if (stack.itemID > 0 && stack.stackSize > 0)
        {
            //Farming
            if (isHoeItem(stack.itemID) && onHoeUse(stack, x, y - 1, z, world))
            {
                return true;
            }

            if (isSeedItem(stack.itemID) && onSeedUse(stack, x, y - 1, z, world))
            {
                return true;
            }

            if (isBonemealItem(stack.itemID, stack.getItemDamage()) && onBonemealUse(stack, x, y - 1, z, world))
            {
                return true;
            }

            //Foresting
            if (isAxeItem(stack.itemID) && onAxeUse(stack, x, y, z, world))
            {
                return true;
            }

            if (isSaplingBlock(stack.itemID) && onSaplingUse(stack, x, y - 1, z, world))
            {
                return true;
            }

            //Breeding
            if (!triedBreeding && isBreedingItem(stack.itemID) && onBreedingUse(stack, world))
            {
                return true;
            }

            //Breeding
            if (!triedShearing && isShearingItem(stack.itemID) && onShearingUse(stack, world))
            {
                return true;
            }

            //Fishing
            if (isFishingItem(stack.itemID) && onFishingUse(stack, x, y, z, world))
            {
                return true;
            }

            //Snack
            if (fairy.acceptableFoods(stack.itemID) && snackTime(stack))
            {
                return true;
            }
        }

        return false;
    }

    private boolean miscActions(TileEntityChest chest, int x, int y, int z, World world)   //Actions that only require a chest.
    {
        if (cutTallGrass(x, y, z, world))
        {
            return true;
        }

        if (doHaveAxe && trimExcessLeaves(x, y, z, world))
        {
            return true;
        }

        return false;
    }

    private void cleanSlot(TileEntityChest chest, int p)   //Remove an itemstack that's been used up.
    {
        if (chest.getStackInSlot(p) != null && chest.getStackInSlot(p).itemID == 0)
        {
            chest.setInventorySlotContents(p, (ItemStack)null);
        }
    }

    private boolean onHoeUse(ItemStack stack, int x, int y, int z, World world)   //What do do with a hoe
    {
        for (int a = 0; a < 3; a++)
        {
            int i = world.getBlockId(x, y, z);

            if (world.isAirBlock(x, y + 1, z) && (i == Block.grass.blockID || i == Block.dirt.blockID))
            {
                Block block = Block.tilledField;
                world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                world.setBlockWithNotify(x, y, z, block.blockID);
                fairy.armSwing(!fairy.didSwing);
                fairy.setTempItem(stack.itemID);
                stack.damageItem(1, fairy);

                if (stack.stackSize <= 0)
                {
                    stack.itemID = 0;
                }

                fairy.attackTime = 1;

                if (fairy.flymode() && fairy.flyTime > 0)
                {
                    fairy.flyTime = 0;
                }

                return true;
            }

            x += fairy.rand.nextInt(3) - 1;
            z += fairy.rand.nextInt(3) - 1;
        }

        return false;
    }

    private boolean onSeedUse(ItemStack stack, int x, int y, int z, World world)   //What to do with seeds
    {
        for (int a = 0; a < 3; a++)
        {
            int i = world.getBlockId(x, y, z);

            if (world.isAirBlock(x, y + 1, z) && i == Block.tilledField.blockID)
            {
                Block block = Block.crops;
                world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                world.setBlockWithNotify(x, y + 1, z, block.blockID);
                fairy.armSwing(!fairy.didSwing);
                fairy.setTempItem(stack.itemID);
                stack.stackSize--;

                if (stack.stackSize <= 0)
                {
                    stack.itemID = 0;
                }

                fairy.attackTime = 1;

                if (fairy.flymode() && fairy.flyTime > 0)
                {
                    fairy.flyTime = 0;
                }

                return true;
            }

            x += fairy.rand.nextInt(3) - 1;
            z += fairy.rand.nextInt(3) - 1;
        }

        return false;
    }

    private boolean onBonemealUse(ItemStack stack, int x, int y, int z, World world)   //Use bonemeal to speed up wheat growth
    {
        for (int a = 0; a < 3; a++)
        {
            int i = world.getBlockId(x, y + 1, z);
            int j = world.getBlockMetadata(x, y + 1, z);

            if (i == Block.crops.blockID && j < 7)
            {
                Block block = Block.crops;
                world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                world.setBlockMetadataWithNotify(x, y + 1, z, 7);
                fairy.armSwing(!fairy.didSwing);
                fairy.setTempItem(stack.itemID);
                stack.stackSize--;

                if (stack.stackSize <= 0)
                {
                    stack.itemID = 0;
                }

                fairy.attackTime = 1;

                if (fairy.flymode() && fairy.flyTime > 0)
                {
                    fairy.flyTime = 0;
                }

                return true;
            }

            x += fairy.rand.nextInt(3) - 1;
            z += fairy.rand.nextInt(3) - 1;
        }

        return false;
    }

    private boolean onAxeUse(ItemStack stack, int x, int y, int z, World world)   //What do do with an axe
    {
        int m = x;
        int n = z;

        for (int a = 0; a < 9; a++)
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;
            int i = world.getBlockId(x, y, z);
            int j = world.getBlockMetadata(x, y, z);

            if (i == Block.wood.blockID)
            {
                Block block = Block.wood;
                world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                block.dropBlockAsItemWithChance(world, x, y, z, j, 1.0F, 0);
                world.setBlockWithNotify(x, y, z, 0);
                fairy.armSwing(!fairy.didSwing);
                fairy.setTempItem(stack.itemID);
                stack.damageItem(1, fairy);

                if (stack.stackSize <= 0)
                {
                    stack.itemID = 0;
                }

                if (stack.itemID > 0)
                {
                    additionalAxeUse(stack, x, y + 1, z, world, maxTreeHeight);
                }

                fairy.attackTime = 1;

                if (!fairy.flymode() && fairy.flyTime > 0)
                {
                    fairy.flyTime = 0;
                }

                return true;
            }
        }

        return false;
    }

    private void additionalAxeUse(ItemStack stack, int x, int y, int z, World world, int recurse)   //What do do with an axe
    {
        if (recurse > maxTreeHeight)
        {
            recurse = maxTreeHeight;
        }

        int m = x;
        int n = z;

        for (int a = 0; a < 9; a++)
        {
            x = m + ((a / 3) % 9) - 1;
            z = n + (a % 3) - 1;
            int i = world.getBlockId(x, y, z);
            int j = world.getBlockMetadata(x, y, z);

            if (i == Block.wood.blockID)
            {
                Block block = Block.wood;
                world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                block.dropBlockAsItemWithChance(world, x, y, z, j, 1.0F, 0);
                world.setBlockWithNotify(x, y, z, 0);
                stack.damageItem(1, fairy);

                if (stack.stackSize <= 0)
                {
                    stack.itemID = 0;
                }

                if (stack.itemID > 0 && recurse > 0)
                {
                    if (a != 4)
                    {
                        additionalAxeUse(stack, x, y, z, world, recurse - 1);
                    }

                    if (stack.itemID > 0 && recurse > 0)
                    {
                        additionalAxeUse(stack, x, y + 1, z, world, recurse - 1);
                    }
                }
            }
        }
    }

    private boolean onSaplingUse(ItemStack stack, int x, int y, int z, World world)   //What to do with saplings
    {
        for (int a = -2; a < 3; a++)
        {
            for (int c = -2; c < 3; c++)
            {
                if (a == 0 && c == 0)
                {
                    if (goodPlaceForTrees(x + a, y, z + c, world) > 0)
                    {
                        return false;
                    }

                    int i = world.getBlockId(x + a, y, z + c);

                    if (i != Block.grass.blockID && a != Block.dirt.blockID)
                    {
                        return false;
                    }
                }
                else if (Math.abs(a) != 2 || Math.abs(c) != 2)
                {
                    boolean flag = false;

                    for (int b = -1; b < 2; b++)
                    {
                        int status = goodPlaceForTrees(x + a, y + b, z + c, world);

                        if (status == 2)
                        {
                            return false;
                        }
                        else if (status == 0)
                        {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag)
                    {
                        return false;
                    }
                }
            }
        }

        Block block = Block.sapling;
        world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
        world.setBlockWithNotify(x, y + 1, z, block.blockID);
        world.setBlockMetadataWithNotify(x, y + 1, z, stack.getItemDamage());
        stack.stackSize--;

        if (stack.stackSize <= 0)
        {
            stack.itemID = 0;
        }

        fairy.armSwing(!fairy.didSwing);
        fairy.attackTime = 1;

        if (fairy.flymode() && fairy.flyTime > 0)
        {
            fairy.flyTime = 0;
        }

        return true;
    }

    private boolean onBreedingUse(ItemStack stack, World world)   //Attempt to breed animals
    {
        ArrayList animals = getAnimals(world);
        triedBreeding = true;

        if (animals == null)
        {
            return false;
        }

        int count = 0;

        for (int i = 0; i < animals.size() && count < 3 && stack.stackSize > 0; i++)
        {
            EntityAnimal entity = (EntityAnimal)animals.get(i);

            if (fairy.getDistanceToEntity(entity) < 3F)
            {
                mod_FairyMod.setPrivateValueBoth(EntityAnimal.class, entity, "inLove", "a", 600);
                count++;
                stack.stackSize--;
            }
        }

        if (count > 0)
        {
            fairy.armSwing(!fairy.didSwing);
            fairy.setTempItem(stack.itemID);

            if (stack.stackSize <= 0)
            {
                stack.itemID = 0;
            }

            fairy.attackTime = 1;
            fairy.setHearts(!fairy.didHearts);

            if (fairy.flymode() && fairy.flyTime > 0)
            {
                fairy.flyTime = 0;
            }

            return true;
        }

        return false;
    }

    private boolean onShearingUse(ItemStack stack, World world)
    {
        ArrayList sheep = getSheep(world);
        triedShearing = true;

        if (sheep == null)
        {
            return false;
        }

        for (int i = 0; i < sheep.size(); i++)
        {
            EntitySheep entity = (EntitySheep)sheep.get(i);

            if (fairy.getDistanceToEntity(entity) < 3F)
            {
                entity.setSheared(true);
                int k = 1 + fairy.rand.nextInt(3);

                for (int j = 0; j < k; j++)
                {
                    EntityItem entityitem = entity.entityDropItem(new ItemStack(Block.cloth.blockID, 1, entity.getFleeceColor()), 1.0F);
                    entityitem.motionY += entity.rand.nextFloat() * 0.05F;
                    entityitem.motionX += (entity.rand.nextFloat() - entity.rand.nextFloat()) * 0.1F;
                    entityitem.motionZ += (entity.rand.nextFloat() - entity.rand.nextFloat()) * 0.1F;
                }

                fairy.armSwing(!fairy.didSwing);
                fairy.setTempItem(stack.itemID);
                stack.damageItem(1, fairy);

                if (stack.stackSize <= 0)
                {
                    stack.itemID = 0;
                }

                fairy.attackTime = 1;

                if (fairy.flymode() && fairy.flyTime > 0)
                {
                    fairy.flyTime = 0;
                }

                return true;
            }
        }

        return false;
    }

    private static final float pia = -(float)Math.PI / 180F;
    private boolean onFishingUse(ItemStack stack, int x, int y, int z, World world)   //Use bonemeal to speed up wheat growth
    {
        if (fairy.inWater && !fairy.hasPath())
        {
            getToLand(x, y, z, world);
            return false;
        }
        else if (fairy.flymode() && fairy.flyTime > 0 && !fairy.hasPath())
        {
            fairy.flyTime = 0;
            return false;
        }
        else if (!fairy.onGround || fairy.inWater)
        {
            return false;
        }

        float angle = fairy.rotationYaw - 30F + (fairy.rand.nextFloat() * 60F);
        double posX = fairy.posX + (double)Math.sin(angle * pia) * 6D;
        double posY = fairy.posY;
        double posZ = fairy.posZ + (double)Math.cos(angle * pia) * 6D;
        int a = MathHelper.floor_double(posX);
        int b = y;
        int c = MathHelper.floor_double(posZ);

        for (int j = -4; j < 0; j++)
        {
            if (b + j > 0 && b + j < world.getHeight() - 10)
            {
                boolean flag = false;

                for (int i = -1; i <= 1 && !flag; i++)
                {
                    for (int k = -1; k <= 1 && !flag; k++)
                    {
                        if (world.getBlockId(a + i, b + j, c + k) != Block.waterStill.blockID ||
                                world.getBlockId(a + i, b + j + 1, c + k) != 0 ||
                                world.getBlockId(a + i, b + j + 2, c + k) != 0 ||
                                world.getBlockId(a + i, b + j + 3, c + k) != 0)
                        {
                            flag = true;
                        }
                    }
                }

                if (!flag)
                {
                    PathEntity doug = world.getEntityPathToXYZ(fairy, a, b + j, c, 16F, false, false, true, true);

                    if (doug != null && canSeeToSpot(posX, posY, posZ, world))
                    {
                        fairy.rotationYaw = angle;
                        fairy.castRod();
                        stack.damageItem(1, fairy);

                        if (stack.stackSize <= 0)
                        {
                            stack.itemID = 0;
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void getToLand(int x, int y, int z, World world)
    {
        for (int q = 0; q < 16; q++)
        {
            int i = x - 5 + fairy.rand.nextInt(11);
            int j = y + 1 + fairy.rand.nextInt(5);
            int k = z - 5 + fairy.rand.nextInt(11);

            if (y > 1 && y < world.getHeight() - 1)
            {
                if (fairy.isAirySpace(i, j, k) && !fairy.isAirySpace(i, j - 1, k) && world.isBlockNormalCube(i, j - 1, k))
                {
                    PathEntity doug = world.getEntityPathToXYZ(fairy, i, j, k, 16F, false, false, true, true);

                    if (doug != null)
                    {
                        fairy.setPathToEntity(doug);

                        if (!fairy.flymode())
                        {
                            fairy.flyTime = 0;
                        }

                        return;
                    }
                }
            }
        }
    }

    private boolean canSeeToSpot(double posX, double posY, double posZ, World world)
    {
        return world.rayTraceBlocks(Vec3D.createVector(fairy.posX, fairy.posY + (double)fairy.getEyeHeight(), fairy.posZ), Vec3D.createVector(posX, posY, posZ)) == null;
    }

    private int goodPlaceForTrees(int x, int y, int z, World world)   //Check if it's a good place to put a sapling down
    {
        int i = world.getBlockId(x, y, z);

        if (i == Block.sapling.blockID)
        {
            return 2;
        }

        int j = world.getBlockId(x, y + 1, z);

        if (j == Block.sapling.blockID)
        {
            return 2;
        }

        if (j == 0 && world.canBlockSeeTheSky(x, y + 1, z))
        {
            return 0;
        }

        return 1;
    }

    private boolean cutTallGrass(int x, int y, int z, World world)   //Trim tall grass to look for seeds.
    {
        int m = x;
        int n = z;

        for (int a = 0; a < 9; a++)
        {
            x = m + (a / 3) - 1;
            z = n + (a % 3) - 1;
            int i = world.getBlockId(x, y, z);
            int j = world.getBlockMetadata(x, y, z);

            if (breakablePlant(i, j))
            {
                Block block = Block.blocksList[i];
                world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                block.dropBlockAsItemWithChance(world, x, y, z, j, 1.0F, 0);
                world.setBlockWithNotify(x, y, z, 0);
                fairy.armSwing(!fairy.didSwing);
                fairy.attackTime = 1;

                if (fairy.flymode() && fairy.flyTime > 0)
                {
                    fairy.flyTime = 0;
                }

                return true;
            }
        }

        return false;
    }

    private boolean trimExcessLeaves(int x, int y, int z, World world)   //Pick apart trees
    {
        for (int d = 0; d < 3; d++)
        {
            int a = fairy.rand.nextInt(3);
            int b = (fairy.rand.nextInt(2) * 2) - 1;

            if (a == 0)
            {
                x += b;
            }
            else if (a == 1)
            {
                y += b;
            }
            else
            {
                z += b;
            }

            int i = world.getBlockId(x, y, z);
            int j = world.getBlockMetadata(x, y, z);

            if (i == Block.leaves.blockID)
            {
                Block block = Block.blocksList[i];
                world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                block.dropBlockAsItemWithChance(world, x, y, z, j, 1.0F, 0);
                world.setBlockWithNotify(x, y, z, 0);
                fairy.armSwing(!fairy.didSwing);
                fairy.attackTime = 1;
                return true;
            }
        }

        return false;
    }

    private boolean collectGoodies(TileEntityChest chest, World world)   //Pick up useful objects off of the ground
    {
        int count = 0;

        for (int i = 0; i < goodies.size() && count < 3; i++)
        {
            EntityItem entity = (EntityItem)goodies.get(i);
            ItemStack stack = entity.item;
            int emptySpace = getEmptySpace(chest, stack);

            if (emptySpace >= 0)
            {
                chest.setInventorySlotContents(emptySpace, stack);
                entity.setDead();
                count ++;
            }
        }

        if (count > 0)
        {
            world.playSoundAtEntity(fairy, "random.pop", 0.4F, ((fairy.rand.nextFloat() - fairy.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            fairy.armSwing(!fairy.didSwing);
            fairy.attackTime = 1;
            // if(fairy.flymode() && fairy.flyTime > 0) {
            // fairy.flyTime = 0;
            // }
            return true;
        }

        return false;
    }

    private int getEmptySpace(TileEntityChest chest, ItemStack stack)   //Look for a spot to put collected items
    {
        int temp = -1;

        for (int i = 0; i < chest.getSizeInventory(); i++)
        {
            ItemStack stack2 = chest.getStackInSlot(i);

            if (temp < 0 && (stack2 == null || stack2.itemID == 0 || stack2.stackSize == 0))
            {
                temp = i;
            }
            else if (stack2 != null && stack.itemID == stack2.itemID && stack2.stackSize > 0 &&
                    stack2.stackSize + stack.stackSize <= stack.getMaxStackSize() && !stack2.isItemDamaged() && !stack.isItemDamaged() &&
                    stack.getItemDamage() == stack2.getItemDamage())
            {
                stack.stackSize += stack2.stackSize;
                return i;
            }
        }

        return temp;
    }

    private boolean snackTime(ItemStack stack)   //Fairy can heal itself if damaged.
    {
        if (fairy.health < fairy.getMaxHealth())
        {
            stack.stackSize--;

            if (stack.stackSize <= 0)
            {
                stack.itemID = 0;
            }

            fairy.setHearts(!fairy.hearts());

            if (stack.itemID == Item.sugar.shiftedIndex)
            {
                fairy.heal(5);
            }
            else
            {
                fairy.heal(99);

                if (stack.itemID == Item.speckledMelon.shiftedIndex)
                {
                    fairy.setWithered(false);
                    fairy.witherTime = 0;
                }
            }

            fairy.armSwing(!fairy.didSwing);
            fairy.attackTime = 1;
            return true;
        }

        return false;
    }

    private boolean isHoeItem(int i)   //Is the item a hoe?
    {
        return i == Item.hoeWood.shiftedIndex ||
                i == Item.hoeStone.shiftedIndex ||
                i == Item.hoeSteel.shiftedIndex ||
                i == Item.hoeDiamond.shiftedIndex ||
                i == Item.hoeGold.shiftedIndex;
    }

    private boolean isAxeItem(int i)   //Is the item an axe?
    {
        if (i == Item.axeWood.shiftedIndex ||
                i == Item.axeStone.shiftedIndex ||
                i == Item.axeSteel.shiftedIndex ||
                i == Item.axeDiamond.shiftedIndex ||
                i == Item.axeGold.shiftedIndex)
        {
            doHaveAxe = true;
            return true;
        }

        return false;
    }

    private boolean breakablePlant(int i, int j)   //Is it a plant that should be broken
    {
        return (i == Block.crops.blockID && j == 7) ||
                i == Block.tallGrass.blockID ||
                i == Block.plantYellow.blockID ||
                i == Block.plantRed.blockID ||
                i == Block.snow.blockID;
    }

    private boolean isSeedItem(int i)   //Is the item a wheat seed?
    {
        return i == Item.seeds.shiftedIndex;
    }

    private boolean isBonemealItem(int i, int j)   //Is the item a wheat seed?
    {
        return i == Item.dyePowder.shiftedIndex && j == 15;
    }

    private boolean isSaplingBlock(int i)   //Is the item a sapling?
    {
        return i == Block.sapling.blockID;
    }

    private boolean isLogBlock(int i)   //Is the item a log block?
    {
        return i == Block.wood.blockID;
    }

    private boolean isBreedingItem(int i)   //Item used to breed animals
    {
        return i == Item.wheat.shiftedIndex;
    }

    private boolean isShearingItem(int i)   //Item used to breed animals
    {
        return i == Item.shears.shiftedIndex;
    }

    private boolean isClothBlock(int i)   //Is the item a log block?
    {
        return i == Block.cloth.blockID;
    }

    private boolean isFishingItem(int i)   //A fishing rod, used to fish
    {
        return i == Item.fishingRod.shiftedIndex;
    }

    private boolean isRawFish(int i)   //Item gotten from fishing, also used to tame Ocelots
    {
        return i == Item.fishRaw.shiftedIndex;
    }

    private boolean goodItem(int i, int j)   //Items worth picking up
    {
        return isHoeItem(i) || isSeedItem(i) || isBonemealItem(i, j) ||
                isAxeItem(i) || isSaplingBlock(i) || isLogBlock(i) ||
                fairy.acceptableFoods(i) || isBreedingItem(i) || isShearingItem(i) ||
                isClothBlock(i) || isFishingItem(i) || isRawFish(i) ||
                i == Block.plantRed.blockID ||
                i == Block.plantYellow.blockID;
    }

    private static final int maxTreeHeight = 99; //Recursion limit for trees.

    private FRY_EntityFairy fairy; //Will be referenced a lot. It's the fairy who's doing the job
    private boolean doHaveAxe; //Used to make sure fairies don't just tear apart random trees.
    private boolean triedBreeding; //Make sure that breeding is only attempted once per chest.
    private boolean triedShearing; //Make sure that shearing is only attempted once per chest.
    private ArrayList goodies; //A list of items on the ground.
}
