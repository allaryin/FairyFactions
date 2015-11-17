package fairies.old.client;
import java.io.PrintStream;
import java.util.List;
import java.util.Collections;

public class FRY_EntityFairy extends EntityAnimal
{
    public FRY_EntityFairy(World world)
    {
        super(world);
        health = 15;
        setSkin(rand.nextInt(4));
        setJob(rand.nextInt(4));
        setSpecialJob(false);
        setFaction(0);
        setFairyName(rand.nextInt(16), rand.nextInt(16));
        texture = "/fairy/fairy1.png";
        moveSpeed = 0.9F;
        sinage = rand.nextFloat();
        setFlymode(false);
        flyTime = 400 + rand.nextInt(200);
        setSize(0.6F, 0.85F);
        cower = rand.nextInt(2) == 0;
        postX = postY = postZ = -1;
    }

    @Override protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(17, new Byte((byte)0)); //array of flags
        dataWatcher.addObject(18, new Byte((byte)0)); //skin, job and faction
        dataWatcher.addObject(19, new Byte((byte)0)); //name
        dataWatcher.addObject(20, "");                //name of owner
        dataWatcher.addObject(21, new Byte((byte)0)); //other array of flags
        dataWatcher.addObject(22, new Byte((byte)0)); //a byte storing the fairy's health
        dataWatcher.addObject(23, "");                //custom name
        dataWatcher.addObject(24, new Integer(0));    //temporary tool
    }

    @Override public void onUpdate()
    {
        super.onUpdate();
        texture = getTexture(getSkin()); //Main model skin

        if (createGroup)
        {
            createGroup = false;
            int i = MathHelper.floor_double(posX);
            int j = MathHelper.floor_double(boundingBox.minY) - 1;
            int k = MathHelper.floor_double(posZ);

            if ((new FRY_FairyGroup(8, 10, getFaction())).generate(worldObj, rand, i, j, k))
            {
                //This is good.
            }
            else
            {
                setDead(); //For singleplayer mode
                //mod_FairyMod.fairyMod.sendFairyDespawn(this);
            }
        }

        if (scout())
        {
            moveSpeed = 1.05F;
        }
        else
        {
            moveSpeed = 0.9F;
        }

        if (withered())
        {
            moveSpeed *= 0.75F;
        }

        if (!worldObj.isRemote)
        {
            updateWithering();
            setFairyHealth(health);
            setFairyClimbing(flymode() && canFlap() && hasPath() && isCollidedHorizontally);

            if (isSitting() && (ridingEntity != null || !onGround))
            {
                setSitting(false);
            }

            setPosted(postY > -1);
        }

        if (health > 0)
        {
            if (!onGround)  //Wing animations
            {
                sinage += 0.75F;
            }
            else
            {
                sinage += 0.15F;
            }

            if (sinage > (float)Math.PI * 2F)
            {
                sinage -= ((float)Math.PI * 2F);
            }

            if (flymode())
            {
                if (!liftOff() && ridingEntity != null && !ridingEntity.onGround && ridingEntity instanceof EntityLiving)
                {
                    ridingEntity.fallDistance = 0F;

                    if (ridingEntity.motionY < -0.2D)
                    {
                        ridingEntity.motionY = -0.2D;
                    }

                    if (((EntityLiving)ridingEntity).isJumping && ridingEntity.motionY < 0.15D && canFlap())
                    {
                        ridingEntity.motionY = 0.15D;
                    }

                    if (!worldObj.isRemote)
                    {
                        renderYawOffset = prevRotationYaw = rotationYaw = ridingEntity.rotationYaw;
                    }
                }
                else
                {
                    if (motionY < -0.2D)
                    {
                        motionY = -0.2D;
                    }

                    if (canFlap() && checkGroundBelow() && motionY < 0D)
                    {
                        motionY = 0.1875D;
                    }

                    if (liftOff() && ridingEntity != null)
                    {
                        ridingEntity.fallDistance = 0F;
                        motionY = ridingEntity.motionY = 0.3D;
                    }
                }
            }

            if (hearts() != didHearts)
            {
                didHearts = !didHearts;
                showHeartsOrSmokeFX(tamed());
            }
        }

        particleCount ++;

        if (particleCount >= 5)
        {
            particleCount = rand.nextInt(2);

            if (angry() || (crying() && queen()))
            {
                worldObj.spawnParticle("smoke", posX, boundingBox.maxY, posZ, 0D, 0D, 0D); //anger smoke
            }
            else if (crying())
            {
                worldObj.spawnParticle("splash", posX, boundingBox.maxY, posZ, 0D, 0D, 0D); //crying effects
            }

            if (liftOff())
            {
                worldObj.spawnParticle("explode", posX, boundingBox.minY, posZ, 0D, 0D, 0D); //liftoff effects
            }

            if (withered() || (rogue() && canHeal()))
            {
                double a = posX - 0.2D + (0.4D * (double)rand.nextFloat());
                double b = posY + 0.45D + (0.15D * (double)rand.nextFloat());
                double c = posZ - 0.2D + (0.4D * (double)rand.nextFloat());
                EntitySmokeFX smoke = new EntitySmokeFX(worldObj, a, b, c, 0D, 0D, 0D);
                a = 0.3D + (0.15D * (double)rand.nextFloat());
                b = 0.5D + (0.2D * (double)rand.nextFloat());
                c = 0.3D + (0.15D * (double)rand.nextFloat());
                smoke.setRBGColorF((float)a, (float)b, (float)c);
                ModLoader.getMinecraftInstance().effectRenderer.addEffect(smoke);
            }

            if (nameEnabled() && tamed() && !rulerName().equals(""))
            {
                EntityPlayer player = ModLoader.getMinecraftInstance().thePlayer;

                if (player != null && player.username != null && player.username.equals(rulerName()))
                {
                    setNameEnabled(false);
                    ModLoader.getMinecraftInstance().displayGuiScreen(new FRY_GuiName(this));
                }
            }
        }

        processSwinging();
    }

    @Override public float getEyeHeight()   //Changes rapidly between normal and above the head - lets them see over tall grass.
    {
        if (!worldObj.isRemote && onGround && rand.nextInt(2) == 0)
        {
            int a = MathHelper.floor_double(posX);
            int b = MathHelper.floor_double(boundingBox.minY);
            int c = MathHelper.floor_double(posZ);

            if (isAirySpace(a, b, c) && isAirySpace(a, b + 1, c))
            {
                return height * 1.375F;
            }
        }

        return super.getEyeHeight();
    }

    @Override public boolean isEntityInsideOpaqueBlock()
    {
        for (int i = 0; i < 8; i++)
        {
            float f = ((float)((i >> 0) % 2) - 0.5F) * width * 0.8F;
            float f1 = ((float)((i >> 1) % 2) - 0.5F) * 0.1F;
            float f2 = ((float)((i >> 2) % 2) - 0.5F) * width * 0.8F;
            int j = MathHelper.floor_double(posX + (double)f);
            int k = MathHelper.floor_double(posY + (double)super.getEyeHeight() + (double)f1);
            int l = MathHelper.floor_double(posZ + (double)f2);

            if (worldObj.isBlockNormalCube(j, k, l))
            {
                return true;
            }
        }

        return false;
    }

    @Override public void faceEntity(Entity entity, float f, float f1)   //Fixes the head shaking glitch.
    {
        double d = entity.posX - posX;
        double d2 = entity.posZ - posZ;
        double d1;

        if (entity instanceof EntityLiving)
        {
            EntityLiving entityliving = (EntityLiving)entity;
            d1 = (posY + (double)(height * 0.85F)) - (entityliving.posY + (double)entityliving.getEyeHeight());
        }
        else
        {
            d1 = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2D - (posY + (double)(height * 0.85F));
        }

        double d3 = MathHelper.sqrt_double(d * d + d2 * d2);
        float f2 = (float)((Math.atan2(d2, d) * 180D) / Math.PI) - 90F;
        float f3 = (float)(-((Math.atan2(d1, d3) * 180D) / Math.PI));
        rotationPitch = -updateRotation(rotationPitch, f3, f1);
        rotationYaw = updateRotation(rotationYaw, f2, f);
    }

    private float updateRotation(float f, float f1, float f2)   //Had to redo this because its private.
    {
        float f3;

        for (f3 = f1 - f; f3 < -180F; f3 += 360F) { }

        for (; f3 >= 180F; f3 -= 360F) { }

        if (f3 > f2)
        {
            f3 = f2;
        }

        if (f3 < -f2)
        {
            f3 = -f2;
        }

        return f + f3;
    }

    @Override protected void updateFallState(double par1, boolean par3)
    {
        super.updateFallState(par1 / 6D, par3);
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY) - 1;
        int k = MathHelper.floor_double(posZ);

        if (j > 0 && j < worldObj.getHeight())
        {
            worldObj.markBlockNeedsUpdate(i, j, k);
        }
    }

    @Override protected void fall(float f)
    {
    }

    @Override public void updateEntityActionState()
    {
        super.updateEntityActionState();

        if (wasFishing)
        {
            wasFishing = false;

            if (isSitting() && fishEntity == null)
            {
                setSitting(false);
            }
        }

        if (isSitting())
        {
            handlePosted(false);
            return;
        }

        flyBlocked = checkFlyBlocked();

        if (flyTime > 0)
        {
            flyTime --;
        }

        boolean liftFlag = false;

        if (flymode())
        {
            fallDistance = 0F;

            if (ridingEntity != null)
            {
                if (entityToAttack != null && ridingEntity == entityToAttack)
                {
                    flyTime = 200;
                    liftFlag = true;

                    if (attackTime <= 0 || flyBlocked)
                    {
                        attackTime = 0;
                        attackEntity(ridingEntity, 0F);
                        liftFlag = false;
                    }
                }
                else if (tamed())
                {
                    if (ridingEntity.onGround || ridingEntity.inWater)
                    {
                        flyTime = (queen() || scout() ? 60 : 40);

                        if (withered())
                        {
                            flyTime -= 10;
                        }
                    }
                }
            }

            if (flyTime <= 0 || (flyBlocked && (ridingEntity == null || (entityToAttack != null && ridingEntity == entityToAttack))))
            {
                setCanFlap(false);
            }
            else
            {
                setCanFlap(true);
            }

            if (ridingEntity == null && (onGround || inWater))
            {
                setFlymode(false);
                flyTime = 400 + rand.nextInt(200);

                if (scout())  //Scouts are less likely to want to walk.
                {
                    flyTime /= 3;
                }
            }
        }
        else
        {
            if (flyTime <= 0 && !flyBlocked)
            {
                jump();
                setFlymode(true);
                setCanFlap(true);
                flyTime = 400 + rand.nextInt(200);

                if (scout())  //Scouts are more likely to want to fly.
                {
                    flyTime *= 3;
                }
            }

            if (ridingEntity != null && !flymode())
            {
                setFlymode(true);
                setCanFlap(true);
            }

            if (!flymode() && !onGround && fallDistance >= 0.5F && ridingEntity == null)
            {
                setFlymode(true);
                setCanFlap(true);
                flyTime = 400 + rand.nextInt(200);
            }
        }

        setLiftOff(liftFlag);

        if (healTime > 0)
        {
            healTime --;
        }

        if (cryTime > 0)
        {
            cryTime --;

            if (cryTime <= 0)
            {
                entityFear = null;
            }

            if (cryTime > 600)
            {
                cryTime = 600;
            }
        }

        listActions ++;

        if (listActions >= 8)
        {
            listActions = rand.nextInt(3);

            if (angry())
            {
                handleAnger();
            }
            else if (crying())
            {
                handleFear();
            }
            else
            {
                handleRuler();

                if (medic())
                {
                    handleHealing();
                }
                else if (rogue())
                {
                    handleRogue();
                }
                else
                {
                    handleSocial();
                }

                handlePosted(true);
            }
        }

        if (worldObj.difficultySetting <= 0 && entityToAttack != null && entityToAttack instanceof EntityPlayer)
        {
            entityFear = entityToAttack;
            cryTime = Math.max(cryTime, 100);
            setTarget((Entity)null);
        }

        setCrying(cryTime > 0);
        setAngry(entityToAttack != null);
        setCanHeal(healTime <= 0);
    }

    public void handleAnger()
    {
        entityFear = null;

        if (entityToAttack != null)  //Lose interest in an entity that is far away or out of sight over time.
        {
            float dist = getDistanceToEntity(entityToAttack);

            if (dist >= 16F || (rand.nextInt(2) == 0 && !canEntityBeSeen(entityToAttack)))
            {
                loseInterest++;

                if (loseInterest >= (tamed() ? 15 : 45))
                {
                    setTarget((Entity)null);
                    loseInterest = 0;
                }
            }
            else
            {
                loseInterest = 0;
            }

            //Guards can fight for a queen - will make her run away instead
            if (guard() && getFaction() > 0 && ruler != null && ruler instanceof FRY_EntityFairy)
            {
                FRY_EntityFairy fairy = (FRY_EntityFairy)ruler;

                if (fairy.entityToAttack != null)
                {
                    float fist = getDistanceToEntity(fairy);

                    if (fist < 8F && dist < 8F && canEntityBeSeen(fairy))
                    {
                        this.setTarget(fairy.entityToAttack);
                        fairy.setTarget((Entity)null);
                        fairy.cryTime = 100;
                        fairy.entityFear = entityToAttack;
                    }
                }
            }
        }
    }

    public void handleFear()
    {
        if (entityFear != null && entityFear.isDead)
        {
            entityFear = null; //Don't fear the dead.
        }

        if (entityFear != null && !hasPath() && canEntityBeSeen(entityFear) && cower)
        {
            float dist = getDistanceToEntity(entityFear);

            if (dist < 12F)  //Run from entityFear if you can see it and it is close.
            {
                PathEntity doug = roam(entityFear, this, (float)Math.PI);

                if (doug != null)
                {
                    setPathToEntity(doug);
                    cryTime += 120;
                }
            }
        }
    }

    public void handleRuler()
    {
        if (ruler != null)  //get rid of that ruler.
        {
            if (ruler.health <= 0 || ruler.isDead)
            {
                ruler = null;
            }
        }

        if (ruler == null)
        {
            if (!tamed() && !queen())  //Looking for a queen to follow.
            {
                double d = 40D;

                if (getFaction() == 0)
                {
                    d = 16D;
                }

                List list = worldObj.getEntitiesWithinAABB(FRY_EntityFairy.class, boundingBox.expand(d, d, d));

                for (int j = 0; j < list.size(); j++)
                {
                    FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                    if (fairy != this && fairy.health > 0 && fairy.queen())  //Fairy finds the queen of its faction, fairly standard.
                    {
                        if (getFaction() > 0 && fairy.getFaction() == this.getFaction())
                        {
                            ruler = fairy;
                            break;
                        }
                        else if (getFaction() == 0 && fairy.getFaction() > 0 && canEntityBeSeen(fairy))    //A factionless fairy may find a new ruler on its own.
                        {
                            ruler = fairy;
                            setFaction(fairy.getFaction());
                            break;
                        }
                    }
                }
            }
            else if (getFaction() == 0 && tamed())    //Looking for a player to follow.
            {
                List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(16D, 16D, 16D));

                for (int j = 0; j < list.size(); j++)
                {
                    EntityPlayer player = (EntityPlayer)list.get(j);

                    if (player.health > 0 && player.username.equals(rulerName()) && canEntityBeSeen(player))
                    {
                        ruler = player;
                        break;
                    }
                }
            }
        }

        if (ruler != null && !hasPath() && !posted())  //This makes fairies walk towards their ruler.
        {
            float dist = getDistanceToEntity(ruler);

            if ((guard() || queen()) && canEntityBeSeen(ruler) && dist > 5F && dist < 16F)  //Guards and Queens walk closer to the player (Medic healing?)
            {
                PathEntity path = worldObj.getPathEntityToEntity(this, ruler, 16F, false, false, true, true);

                if (path != null)
                {
                    setPathToEntity(path);
                }
            }
            else
            {
                if (scout() && ruler instanceof FRY_EntityFairy)  //Scouts stay way out there on the perimeter.
                {
                    if (dist < 12F)
                    {
                        PathEntity doug = roam(ruler, this, (float)Math.PI);

                        if (doug != null)
                        {
                            setPathToEntity(doug);
                        }
                    }
                    else if (dist > 24F)
                    {
                        PathEntity doug = roam(ruler, this, 0F);

                        if (doug != null)
                        {
                            setPathToEntity(doug);
                        }
                    }
                }
                else     //Regular fairies stay moderately close.
                {
                    if (dist > 16F && ruler instanceof EntityPlayer && canTeleportToRuler((EntityPlayer)ruler))
                    {
                        teleportToRuler(ruler); //Can teleport to the owning player if he has an ender eye or an ender pearl.
                    }
                    else if (dist > (ruler instanceof FRY_EntityFairy ? 12F : 6F))
                    {
                        PathEntity doug = roam(ruler, this, 0F);

                        if (doug != null)
                        {
                            setPathToEntity(doug);
                        }
                    }
                }
            }
        }

        if (snowballin > 0 && attackTime <= 0 && ruler != null && entityToAttack == null && entityFear == null && cryTime == 0)
        {
            float dist = getDistanceToEntity(ruler);

            if (dist < 10F && canEntityBeSeen(ruler))
            {
                tossSnowball(ruler);
            }
            else if (!hasPath() && dist < 16F)
            {
                PathEntity doug = roam(ruler, this, 0F);

                if (doug != null)
                {
                    setPathToEntity(doug);
                }
            }
        }

        if (getFaction() > 0)  //This is a method for making sure that fairies eventually relaize they're alone
        {
            boolean flag = false;

            if (!queen() && (ruler == null || getDistanceToEntity(ruler) > 40F))  //If a follower has lost its leader
            {
                flag = true;
            }
            else if (queen())    //If a leader has lost her followers
            {
                flag = true;
                List list = worldObj.getEntitiesWithinAABB(FRY_EntityFairy.class, boundingBox.expand(40D, 40D, 40D));

                for (int j = 0; j < list.size(); j++)
                {
                    FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                    if (fairy != this && fairy.sameTeam(this) && fairy.health > 0)
                    {
                        flag = false;
                        break;
                    }
                }
            }
            else if (ruler != null && ruler instanceof FRY_EntityFairy)    //If a fairy queenwas tamed in peaceful mode
            {
                FRY_EntityFairy fairy = (FRY_EntityFairy)ruler;

                if (!sameTeam(fairy))
                {
                    flag = true;

                    if (loseTeam < 65)
                    {
                        loseTeam = 65 + rand.nextInt(8);
                    }
                }
            }

            if (flag)
            {
                loseTeam ++; //Takes a while for it to take effect.

                if (loseTeam >= 75)
                {
                    ruler = null;
                    disband();
                    loseTeam = 0;
                    cryTime = 0;
                    setPathToEntity((PathEntity)null);
                }
            }
            else
            {
                loseTeam = 0;
            }
        }
    }

    public void handleSocial()   //This handles actions concerning teammates and entities atacking their ruler.
    {
        if (rand.nextInt(2) == 0)
        {
            return;
        }

        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 16D, 16D));
        Collections.shuffle(list, rand);

        for (int j = 0; j < list.size(); j++)
        {
            Entity entity = (Entity)list.get(j);

            if (canEntityBeSeen(entity) && !entity.isDead)
            {
                if ((ruler != null || queen()) && entity instanceof FRY_EntityFairy && sameTeam((FRY_EntityFairy)entity))
                {
                    FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                    if (fairy.health > 0)
                    {
                        Entity scary = (Entity)null;

                        if (fairy.entityFear != null)
                        {
                            scary = fairy.entityFear;
                        }
                        else if (fairy.entityToAttack != null)
                        {
                            scary = fairy.entityToAttack;
                        }

                        if (scary != null)
                        {
                            float dist = getDistanceToEntity(scary);

                            if (dist > 16F || !canEntityBeSeen(scary))
                            {
                                scary = null;
                            }
                        }

                        if (scary != null)
                        {
                            if (cower)
                            {
                                if (fairy.entityToAttack == scary && canEntityBeSeen(scary))
                                {
                                    cryTime = 120;
                                    this.entityFear = scary;
                                    PathEntity doug = roam(entity, this, (float)Math.PI);

                                    if (doug != null)
                                    {
                                        setPathToEntity(doug);
                                    }

                                    break;
                                }
                                else if (fairy.cryTime > 60)
                                {
                                    cryTime = Math.max(fairy.cryTime - 60, 0);
                                    this.entityFear = scary;
                                    PathEntity doug = roam(entity, this, (float)Math.PI);

                                    if (doug != null)
                                    {
                                        setPathToEntity(doug);
                                    }

                                    break;
                                }
                            }
                            else
                            {
                                this.setTarget((Entity)scary);
                                break;
                            }
                        }
                    } //Guards proactivley seeking holstile enemies. Will add slimes? Maybe dunno.
                }
                else if (ruler != null && (guard() || queen()) && entity instanceof EntityCreature && !(entity instanceof EntityCreeper) && (!(entity instanceof EntityAnimal) || (!peacefulAnimal((EntityAnimal)entity))))
                {
                    EntityCreature creature = (EntityCreature)entity;

                    if (creature.health > 0 && creature.entityToAttack != null && creature.entityToAttack == ruler)
                    {
                        this.setTarget((Entity)creature);
                        break;
                    }
                }
                else if (entity instanceof EntityTNTPrimed && !hasPath())    //Running away from lit TNT.
                {
                    float dist = getDistanceToEntity(entity);

                    if (dist < 8F)
                    {
                        PathEntity doug = roam(entity, this, (float)Math.PI);

                        if (doug != null)
                        {
                            setPathToEntity(doug);

                            if (!flymode())
                            {
                                setFlymode(true);
                                jump();
                                flyTime = 100;
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    public void handleHealing()   //This handles actions of the medics.
    {
        if (healTime > 0)
        {
            return;
        }

        if (entityHeal != null)
        {
            if (entityHeal.health <= 0 || entityHeal.isDead)
            {
                entityHeal = null;
            }
            else if (!hasPath())
            {
                PathEntity doug = worldObj.getPathEntityToEntity(this, entityHeal, 16F, false, false, true, true);

                if (doug != null)
                {
                    setPathToEntity(doug);
                }
                else
                {
                    entityHeal = null;
                }
            }
            else
            {
                float g = getDistanceToEntity(entityHeal);

                if (g < 2.5F && canEntityBeSeen(entityHeal))
                {
                    healThatAss(entityHeal);
                    entityHeal = null;
                }
            }
        }

        if (entityHeal == null && healTime <= 0)
        {
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 16D, 16D));

            for (int j = 0; j < list.size(); j++)
            {
                Entity entity = (Entity)list.get(j);

                if (canEntityBeSeen(entity) && !entity.isDead)
                {
                    if (entity instanceof FRY_EntityFairy)
                    {
                        FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                        if (fairy.health > 0 && sameTeam(fairy) && fairy.health < fairy.getMaxHealth())
                        {
                            this.entityHeal = fairy;
                            PathEntity doug = worldObj.getPathEntityToEntity(this, entityHeal, 16F, false, false, true, true);

                            if (doug != null)
                            {
                                setPathToEntity(doug);
                            }

                            break;
                        }
                    }
                    else if (entity instanceof EntityLiving && ruler != null && ((EntityLiving)entity) == ruler)
                    {
                        if (ruler.health > 0 && ruler.health < ruler.getMaxHealth())
                        {
                            this.entityHeal = ruler;
                            PathEntity doug = worldObj.getPathEntityToEntity(this, entityHeal, 16F, false, false, true, true);

                            if (doug != null)
                            {
                                setPathToEntity(doug);
                            }

                            break;
                        }
                    }
                }
            }

            if (entityHeal == null && health < getMaxHealth())
            {
                healThatAss(this);
            }
        }
    }

    public void handleRogue()   //A handler specifically for the rogue class.
    {
        if (rand.nextInt(2) == 0)
        {
            return;
        }

        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 16D, 16D));
        Collections.shuffle(list, rand);

        for (int j = 0; j < list.size(); j++)
        {
            Entity entity = (Entity)list.get(j);

            if (canEntityBeSeen(entity) && !entity.isDead)
            {
                if ((ruler != null || queen()) && entity instanceof FRY_EntityFairy && sameTeam((FRY_EntityFairy)entity))
                {
                    FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                    if (fairy.health > 0)
                    {
                        Entity scary = (Entity)null;

                        if (fairy.entityFear != null)
                        {
                            scary = fairy.entityFear;
                        }
                        else if (fairy.entityToAttack != null)
                        {
                            scary = fairy.entityToAttack;
                        }

                        if (scary != null)
                        {
                            float dist = getDistanceToEntity(scary);

                            if (dist > 16F || !canEntityBeSeen(scary))
                            {
                                scary = null;
                            }
                        }

                        if (scary != null)
                        {
                            if (canHeal())
                            {
                                if (fairy.entityToAttack == scary && canEntityBeSeen(scary))
                                {
                                    cryTime = 120;
                                    this.entityFear = scary;
                                    PathEntity doug = roam(entity, this, (float)Math.PI);

                                    if (doug != null)
                                    {
                                        setPathToEntity(doug);
                                    }

                                    break;
                                }
                                else if (fairy.cryTime > 60)
                                {
                                    cryTime = Math.max(fairy.cryTime - 60, 0);
                                    this.entityFear = scary;
                                    PathEntity doug = roam(entity, this, (float)Math.PI);

                                    if (doug != null)
                                    {
                                        setPathToEntity(doug);
                                    }

                                    break;
                                }
                            }
                            else
                            {
                                this.setTarget((Entity)scary);
                                break;
                            }
                        }
                    }
                }
                else if (ruler != null && canHeal() && entity instanceof EntityCreature && !(entity instanceof EntityCreeper) && (!(entity instanceof EntityAnimal) || (!peacefulAnimal((EntityAnimal)entity))))
                {
                    EntityCreature creature = (EntityCreature)entity;

                    if (creature.health > 0 && creature.entityToAttack != null && creature.entityToAttack == ruler)
                    {
                        this.setTarget((Entity)creature);
                        break;
                    }
                }
                else if (entity instanceof EntityTNTPrimed && !hasPath())    //Running away from lit TNT.
                {
                    float dist = getDistanceToEntity(entity);

                    if (dist < 8F)
                    {
                        PathEntity doug = roam(entity, this, (float)Math.PI);

                        if (doug != null)
                        {
                            setPathToEntity(doug);

                            if (!flymode())
                            {
                                setFlymode(true);
                                jump();
                                flyTime = 100;
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    public int postedCount;
    public void handlePosted(boolean flag)   //The AI method which handles post-related activities.
    {
        if (!tamed() || getFaction() > 0 || postedCount <= (posted() ? 2 : 5))
        {
            postedCount ++;
            return; //Avoid processing too often or when not necessary.
        }

        postedCount = 0;
        boolean farFlag = false;

        if (postY > -1)
        {
            if (ridingEntity != null && ruler != null && ridingEntity == ruler)
            {
                abandonPost();
                return; //When a the player takes a tamed fairy away, it automatically cancels the post.
            }

            Chunk chunk = worldObj.getChunkFromBlockCoords(postX, postZ); //Checks to see if the chunk is loaded.

            if (chunk != null && !(chunk instanceof EmptyChunk))
            {
                int blok = worldObj.getBlockId(postX, postY, postZ);

                if (blok <= 0 || blok >= Block.blocksList.length || Block.blocksList[blok] == null || !(Block.blocksList[blok] instanceof BlockSign))  //If the saved position is not a sign block
                {
                    abandonPost();
                }
                else
                {
                    TileEntity tileentity = worldObj.getBlockTileEntity(postX, postY, postZ); //Makes sure the tile entity is right

                    if (tileentity == null || !(tileentity instanceof TileEntitySign))
                    {
                        abandonPost();
                    }
                    else
                    {
                        TileEntitySign sign = (TileEntitySign)tileentity; //Makes sure the name still matches.

                        if (!mySign(sign))
                        {
                            abandonPost();
                        }
                        else if (canRoamFar(sign))
                        {
                            farFlag = true;
                        }
                    }
                }
            }
        }
        else     //Try to find a post. The ruler has to be nearby for it to work.
        {
            if (ruler != null && (ridingEntity == null || ridingEntity != ruler) && getDistanceSqToEntity(ruler) <= 64F && canEntityBeSeen(ruler))
            {
                int a = MathHelper.floor_double(posX);
                int b = MathHelper.floor_double(boundingBox.minY);
                int c = MathHelper.floor_double(posZ); //Gets the fairy's relative position

                for (int i = 0; i < 245; i++)
                {
                    int x = -3 + (i % 7); //Look around randomly.
                    int y = -2 + (i / 49);
                    int z = -3 + ((i / 7) % 7);

                    if (Math.abs(x) == 3 && Math.abs(z) == 3)
                    {
                        continue;
                    }

                    x += a;
                    y += b;
                    z += c;

                    if (y >= 0 && y < worldObj.getHeight())
                    {
                        int blok = worldObj.getBlockId(x, y, z); //Makes sure it's a sign

                        if (blok == Block.signPost.blockID || blok == Block.signWall.blockID/*> 0 && blok < Block.blocksList.length && Block.blocksList[blok] != null && Block.blocksList[blok] instanceof BlockSign*/)
                        {
                            TileEntity tileentity = worldObj.getBlockTileEntity(x, y, z);

                            if (tileentity != null && tileentity instanceof TileEntitySign)  //Makes sure the tile entity is right
                            {
                                TileEntitySign sign = (TileEntitySign)tileentity;

                                if (mySign(sign))  //Makes sure the name is present.
                                {
                                    postX = x;
                                    postY = y;
                                    postZ = z;
                                    setPosted(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!flag)  //Processes fishing, then returns, if sitting.
        {
            if (fishEntity != null)
            {
                if (fishEntity.gotBite())
                {
                    castRod();
                    attackTime = 10;
                }
                else if (rand.nextFloat() < 0.1F)
                {
                    mod_FairyMod.setPrivateValueBoth(EntityLiving.class, this, "currentTarget", "ay", fishEntity);
                    numTicksToChaseTarget = 10 + this.rand.nextInt(20);
                }
            }
            else if (rand.nextInt(2) == 0)
            {
                new FRY_FairyJob(this).sittingFishing(worldObj);
            }

            return;
        }

        if (posted() && !hasPath() && !angry() && !crying())
        {
            double a = (double)postX + 0.5D;
            double b = (double)postY + 0.5D;
            double c = (double)postZ + 0.5D;
            double d = posX - a;
            double e = boundingBox.minY - b;
            double f = posZ - c;
            double g = Math.sqrt((d * d) + (e * e) + (f * f));

            if (g >= (farFlag ? 12D : 6D))
            {
                PathEntity doug = roamBlocks(a, b, c, this, 0F);

                if (doug != null)
                {
                    setPathToEntity(doug);
                }
            }
        }

        if (posted())
        {
            new FRY_FairyJob(this).discover(worldObj);
        }
    }

    public void abandonPost()   //Leave a post.
    {
        postX = postY = postZ = -1;
        setPosted(false);
    }

    private boolean mySign(TileEntitySign sign)
    {
        if (sign.signText == null)  //If the sign's text is messed up or something
        {
            return false;
        }

        CharSequence mySeq = getActualName(getFairyName1(), getFairyName2()).subSequence(0, getActualName(getFairyName1(), getFairyName2()).length() - 1); //Converts actual name

        for (int i = 0; i < sign.signText.length; i++)  //loops through for all sign lines
        {
            if (sign.signText[i].contains(mySeq))  //name just has to be included in full on one of the lines.
            {
                return true;
            }
        }

        return false;
    }

    private boolean canRoamFar(TileEntitySign sign)
    {
        if (sign.signText == null)  //If the sign's text is messed up or something
        {
            return false;
        }

        CharSequence mySeq = "~f".subSequence(0, 1); //Makes the subsequence

        for (int i = 0; i < sign.signText.length; i++)  //loops through for all sign lines
        {
            if (sign.signText[i].contains(mySeq))  //name just has to be included in full on one of the lines.
            {
                return true;
            }
        }

        return false;
    }

    public void updateWithering()
    {
        if (rogue())
        {
            return;
        }

        witherTime++;

        if (withered())
        {
            if (witherTime >= 8)  //Deplete Health Very Quickly.
            {
                witherTime = 0;

                if (health > 1)
                {
                    health--;
                }

                if (worldObj.isDaytime())
                {
                    int a = MathHelper.floor_double(posX);
                    int b = MathHelper.floor_double(boundingBox.minY);
                    int c = MathHelper.floor_double(posZ);
                    float f = getBrightness(1.0F);

                    if (f > 0.5F && worldObj.canBlockSeeTheSky(a, b, c) && rand.nextFloat() * 5F < (f - 0.4F) * 2.0F)
                    {
                        setWithered(false);

                        if (tamed())
                        {
                            setHearts(!didHearts);
                        }

                        witherTime = 0;
                        return;
                    }
                }
            }

            setWithered(true);
        }
        else
        {
            if (witherTime % 10 == 0)
            {
                int a = MathHelper.floor_double(posX);
                int b = MathHelper.floor_double(boundingBox.minY);
                int c = MathHelper.floor_double(posZ);
                float f = getBrightness(1.0F);

                if (f > 0.05F || worldObj.canBlockSeeTheSky(a, b, c))
                {
                    witherTime = rand.nextInt(3);
                }
                else if (witherTime >= 900)
                {
                    setWithered(true);
                    witherTime = 0;
                    return;
                }
            }

            setWithered(false);
        }
    }

    @Override public void setTarget(Entity entity)   //non-consistent method names
    {
        if (entity == null || entityToAttack == null || entity != entityToAttack)
        {
            loseInterest = 0;
        }

        entityToAttack = entity;
    }

    private boolean sameTeam(FRY_EntityFairy fairy)
    {
        if (tamed())  //Checks to see if a fairy is their comrade.
        {
            return fairy.tamed() && fairy.getFaction() == 0 && fairy.rulerName().equals(this.rulerName());
        }
        else if (getFaction() > 0)
        {
            return fairy.getFaction() == this.getFaction();
        }

        return false;
    }

    public boolean peacefulAnimal(EntityAnimal animal)
    {
        Class thing = animal.getClass();
        return thing == EntityPig.class ||
                thing == EntityCow.class ||
                thing == EntityChicken.class ||
                thing == EntitySheep.class ||
                thing == EntityMooshroom.class;
    }

    void showHeartsOrSmokeFX(boolean flag)
    {
        String s = "heart";

        if (!flag)
        {
            s = "smoke";
        }

        for (int i = 0; i < 7; i++)
        {
            double d = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            worldObj.spawnParticle(s, (posX + (double)(rand.nextFloat() * width * 2.0F)) - (double)width, posY + 0.5D + (double)(rand.nextFloat() * height), (posZ + (double)(rand.nextFloat() * width * 2.0F)) - (double)width, d, d1, d2);
        }
    }

    public void processSwinging()
    {
        if (getArmSwing() != didSwing)
        {
            didSwing = !didSwing;
            //if(!isSwinging || swingProgressInt >= 3 || swingProgressInt < 0) {
            swingProgressInt = -1;
            isSwinging = true;
            tempItem = null;
            //}
        }

        if (isSwinging)
        {
            swingProgressInt++;

            if (swingProgressInt >= 6)
            {
                swingProgressInt = 0;
                isSwinging = false;

                if (tempItem != null && tempItem != fishingStick)
                {
                    tempItem = null;
                }
            }
            else if (tempItem == null)
            {
                if (getTempItem() > 0)
                {
                    tempItem = new ItemStack(getTempItem(), 1, 0);
                }
                else if (getTempItem() == -2)
                {
                    tempItem = fishingStick;
                }
            }
        }

        swingProgress = (float)swingProgressInt / 6F;

        if (!isSitting() && tempItem != null && tempItem == fishingStick)
        {
            tempItem = null;
        }
    }

    public boolean isAirySpace(int a, int b, int c)
    {
        if (b < 0 || b >= worldObj.getHeight())
        {
            return false;
        }

        int s = worldObj.getBlockId(a, b, c);

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
            }
        }

        return false;
    }

    public boolean checkGroundBelow()
    {
        int a = MathHelper.floor_double(posX);
        int b = MathHelper.floor_double(boundingBox.minY);
        int b1 = MathHelper.floor_double(boundingBox.minY - 0.5D);
        int c = MathHelper.floor_double(posZ);

        if (!isAirySpace(a, b - 1, c) || !isAirySpace(a, b1 - 1, c))
        {
            return true;
        }

        return false;
    }

    public boolean checkFlyBlocked()
    {
        int a = MathHelper.floor_double(posX);
        int b = MathHelper.floor_double(boundingBox.minY);
        int c = MathHelper.floor_double(posZ);

        if (!isAirySpace(a, b + 1, c) || !isAirySpace(a, b + 2, c))
        {
            return true;
        }

        return false;
    }

    public boolean canTeleportToRuler(EntityPlayer player)
    {
        return player.inventory != null && player.inventory.hasItem(Item.enderPearl.shiftedIndex) || player.inventory.hasItem(Item.eyeOfEnder.shiftedIndex);
    }

    @Override public boolean interact(EntityPlayer player)
    {
        if (!worldObj.isRemote && (ridingEntity == null || ridingEntity == player || ridingEntity instanceof EntityMinecart))
        {
            ItemStack stack = player.inventory.getCurrentItem();

            if (tamed() && player.username.equals(rulerName()))
            {
                if (stack != null && health < getMaxHealth() && acceptableFoods(stack.itemID) && stack.stackSize > 0)
                {
                    stack.stackSize--;

                    if (stack.stackSize <= 0)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }

                    setHearts(!hearts());

                    if (stack.itemID == Item.sugar.shiftedIndex)
                    {
                        heal(5);
                    }
                    else
                    {
                        heal(99);

                        if (stack.itemID == Item.speckledMelon.shiftedIndex)
                        {
                            setWithered(false);
                            witherTime = 0;
                        }
                    }

                    return true;
                }
                else if (stack != null && haircutItem(stack.itemID) && stack.stackSize > 0 && !rogue())
                {
                    setHairType(!hairType());
                    return true;
                }
                else if (stack != null && ridingEntity == null && !isSitting() && vileSubstance(stack.itemID) && stack.stackSize > 0)
                {
                    dropItem(stack.itemID, 1);
                    stack.stackSize--;

                    if (stack.stackSize <= 0)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }

                    disband();
                    return true;
                }
                else if (onGround && stack != null && namingItem(stack.itemID) && stack.stackSize > 0)
                {
                    stack.stackSize--;

                    if (stack.stackSize <= 0)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }

                    setSitting(true);
                    setNameEnabled(true);
                    isJumping = false;
                    setPathToEntity((PathEntity)null);
                    setTarget((Entity)null);
                    entityFear = null;
                }
                else
                {
                    if (isSitting())
                    {
                        if (stack != null && realFreshOysterBars(stack.itemID) && stack.stackSize > 0)
                        {
                            hydraFairy();
                        }
                        else
                        {
                            setSitting(false);
                        }

                        return true;
                    }
                    else if (player.isSneaking())
                    {
                        if (flymode() || !onGround)
                        {
                            flyTime = 0;
                        }
                        else
                        {
                            setSitting(true);
                            isJumping = false;
                            setPathToEntity((PathEntity)null);
                            setTarget((Entity)null);
                            entityFear = null;
                        }
                    }
                    else if (stack == null || !snowballItem(stack.itemID))
                    {
                        mountEntity(player);
                        setFlymode(true);
                        flyTime = 200;
                        setCanFlap(true);
                        return true;
                    }
                }
            }
            else
            {
                if ((getFaction() == 0 || worldObj.difficultySetting <= 0) && !((queen() || posted()) && tamed()) && !crying() && !angry() && stack != null && acceptableFoods(stack.itemID) && stack.stackSize > 0)
                {
                    stack.stackSize--;

                    if (stack.stackSize <= 0)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }

                    if (stack.itemID != Item.sugar.shiftedIndex || rand.nextInt(4) == 0)
                    {
                        if (stack.itemID == Item.sugar.shiftedIndex)
                        {
                            heal(5);
                        }
                        else
                        {
                            heal(99);
                        }

                        tameMe(player);
                        return true;
                    }
                    else
                    {
                        setHearts(!hearts());
                        return true;
                    }
                }
                else if (!tamed())
                {
                    setHearts(!hearts());
                }

                tameFailMessage(player);
                return true;
            }
        }

        return super.interact(player);
    }

    public boolean acceptableFoods(int i)   //Foods that can be used for taming
    {
        if (i == Item.speckledMelon.shiftedIndex)
        {
            return true;
        }
        else if (tamed() || !queen())
        {
            return i == Item.appleRed.shiftedIndex || i == Item.melon.shiftedIndex || i == Item.sugar.shiftedIndex || i == Item.cake.shiftedIndex || i == Item.cookie.shiftedIndex;
        }

        return false;
    }

    public boolean vileSubstance(int i)   //Things used for disbanding
    {
        return i == Item.slimeBall.shiftedIndex || i == Item.rottenFlesh.shiftedIndex || i == Item.spiderEye.shiftedIndex || i == Item.fermentedSpiderEye.shiftedIndex;
    }

    public boolean realFreshOysterBars(int i)   //The quickest way to Daphne
    {
        return i == Item.magmaCream.shiftedIndex;
    }

    public boolean namingItem(int i)   //Item used to rename a fairy, paper
    {
        return i == Item.paper.shiftedIndex;
    }

    public boolean snowballItem(int i)   //Is the item a snowball or not.
    {
        return i == Item.snowball.shiftedIndex;
    }

    public boolean haircutItem(int i)   //Can the item give a haircut.
    {
        return i == Item.shears.shiftedIndex;
    }

    public void disband()
    {
        setRulerName("");
        setFaction(0);
        setHearts(!didHearts);
        cryTime = 200;
        setTamed(false);
        setCustomName("");
        abandonPost();
        snowballin = 0;

        if (ruler != null)
        {
            PathEntity doug = roam(ruler, this, (float)Math.PI);

            if (doug != null)
            {
                setPathToEntity(doug);
            }

            if (ruler instanceof EntityPlayer)
            {
                //
                String s = getActualName(getFairyName1(), getFairyName2()) + " ";

                if (queen())
                {
                    s = "Queen " + s;
                }

                int i = rand.nextInt(6);

                if (queen() && i < 3)
                {
                    s += "was greatly offended by your offering.";
                }
                else if (queen())
                {
                    s += "called you a 'dirty peasant' and stormed out.";
                }
                else if (i == 0)
                {
                    s += "threw it on the ground and had a fit.";
                }
                else if (i == 1)
                {
                    s += "called you a 'poopy-head' and ran away.";
                }
                else if (i == 2)
                {
                    s += "would rather die than eat that gross thing.";
                }
                else if (i == 3)
                {
                    s += "decided not to be your friend anymore.";
                }
                else if (i == 4)
                {
                    s += "gave you a dirty look and headed off.";
                }
                else
                {
                    s += "says that's the grossest thing she's ever seen.";
                }

                //mod_FairyMod.fairyMod.sendDisband(player, "* §9" + s);
                ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("* §9" + s);
            }
        }

        ruler = null;
    }

    public void tameMe(EntityPlayer player)
    {
        if (player == null)
        {
            return;
        }

        setFaction(0);
        setTamed(true);
        setRulerName(player.username);
        setHearts(!hearts());
        abandonPost();
        snowballin = 0;
        ruler = player;

        if (scout())
        {
            cower = false;
        }

        String f = getActualName(getFairyName1(), getFairyName2()) + " ";

        if (queen())
        {
            f = "Queen " + f;
        }

        String s = f;
        int i = rand.nextInt(6);

        if (queen() && i < 2)
        {
            s += "reluctantly joined your party.";
        }
        else if (queen() && i > 3)
        {
            s += "sighed and became your follower.";
        }
        else if (queen())
        {
            s += "really enjoys eating glistering melons.";
        }
        else if (i == 0)
        {
            s += "was kind of lonely without a leader.";
        }
        else if (i == 1)
        {
            s += "shrugged and decided to follow you.";
        }
        else if (i == 2)
        {
            s += "put the past behind her and joined you.";
        }
        else if (i == 3)
        {
            s += "was easily persuaded by that yummy snack.";
        }
        else if (i == 4)
        {
            s += "introduced herself properly to you.";
        }
        else
        {
            s += "ate that snack like there was no tomorrow.";
        }

        //mod_FairyMod.fairyMod.sendDisband(newRuler, "* §a" + s);
        ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("* §a" + s);
    }

    public void tameFailMessage(EntityPlayer player)
    {
        String s = "You can't ";

        if (angry())
        {
            s += "tame this fairy because she's angry right now.";
        }
        else if (crying())
        {
            s += "tame this fairy because she's upset right now.";
        }
        else if (getFaction() > 0)
        {
            if (queen())
            {
                s += "tame a fairy queen until you defeat her minions.";
            }
            else
            {
                s += "tame this fairy until you defeat her queen.";
            }
        }
        else if (tamed() && queen())
        {
            s += "steal a fairy queen owned by someone else.";
        }
        else if (posted())
        {
            s += "steal a fairy who's assigned to a post";
        }
        else
        {
            ItemStack stack = (ItemStack)null;

            if (player.inventory != null)
            {
                stack = player.inventory.getCurrentItem();
            }

            if (stack != null && stack.stackSize > 0 && stack.itemID == Item.lightStoneDust.shiftedIndex)
            {
                s += "seriously be trying to feed a fairy something that resembles its own guts.";
            }
            else if (queen())
            {
                s += "tame a fairy queen without a slice of speckled melon.";
            }
            else
            {
                s += "tame a fairy without a sweet-tasting snack.";
            }
        }

        // if(player instanceof EntityPlayerMP) {
        // EntityPlayerMP playerMP = (EntityPlayerMP)player;
        // mod_FairyMod.fairyMod.sendDisband(playerMP, "* §9" + s);
        // }
        ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("* §9" + s);
    }

    public String getTexture(int i)
    {
        if (getCustomName().equals("Steve"))
        {
            return "/fairy/notFairy.png";
        }

        if (i < 0)
        {
            return "/fairy/fairy" + (queen() ? "q" : "") + "1.png";
        }
        else if (i > 3)
        {
            return "/fairy/fairy" + (queen() ? "q" : "") + "4.png";
        }
        else
        {
            return "/fairy/fairy" + (queen() ? "q" : "") + (i + 1) + ".png";
        }
    }

    public boolean snowballFight(DamageSource damagesource)   //Checks if a damage source is a snowball.
    {
        if (damagesource instanceof EntityDamageSourceIndirect)
        {
            EntityDamageSourceIndirect snowdamage = (EntityDamageSourceIndirect)damagesource;

            if (snowdamage.getSourceOfDamage() != null && snowdamage.getSourceOfDamage() instanceof EntitySnowball)
            {
                snowballin += 1;

                if (attackTime < 10)
                {
                    attackTime = 10;
                }
            }
        }

        return snowballin <= 0;
    }

    private void tossSnowball(Entity attackTarget)   //Throws a snowball towarda a target.
    {
        EntitySnowball entitysnowball = new EntitySnowball(worldObj, this);
        double d = attackTarget.posX - this.posX;
        double d1 = (attackTarget.posY + (double)attackTarget.getEyeHeight()) - 1.1000000238418579D - entitysnowball.posY;
        double d2 = attackTarget.posZ - this.posZ;
        float f = MathHelper.sqrt_double(d * d + d2 * d2) * 0.2F;
        entitysnowball.setThrowableHeading(d, d1 + (double)f, d2, 1.6F, 12F);
        worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        worldObj.spawnEntityInWorld(entitysnowball);
        attackTime = 30;
        armSwing(!didSwing);
        faceEntity(attackTarget, 180F, 180F);
        snowballin -= 1;

        if (snowballin < 0)
        {
            snowballin = 0;
        }
    }

    public void castRod()
    {
        if (fishEntity != null)
        {
            int var4 = fishEntity.catchFish();
            armSwing(!didSwing);
            setSitting(false);
        }
        else
        {
            worldObj.playSoundAtEntity(this, "random.bow", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
            FRY_EntityFishHook hook = new FRY_EntityFishHook(worldObj, this);
            worldObj.spawnEntityInWorld(hook);
            armSwing(!didSwing);
            setTempItem(-2);
            setSitting(true);
            isJumping = false;
            setPathToEntity((PathEntity)null);
            setTarget((Entity)null);
            entityFear = null;
        }
    }

    @Override public boolean attackEntityFrom(DamageSource damagesource, int i)
    {
        boolean ignoreTarget = false;
        Entity entity = damagesource.getEntity();

        if (ruler != null && ridingEntity != null && ridingEntity == ruler)
        {
            return false; //Prevents the fairy from recieving any damage if on the tamer's head.
        }

        if (ruler != null && entity != null && ruler == entity && ruler instanceof EntityPlayer)
        {
            if (snowballFight(damagesource))
            {
                return false; //Prevents a fairy from being damaged by its ruler at all if it's a player.
            }
            else
            {
                ignoreTarget = true;
            }
        }

        if (tamed() && !rulerName().equals("") && entity != null)
        {
            if (entity instanceof EntityPlayer && ((EntityPlayer)entity).username.equals(rulerName()))
            {
                if (!ignoreTarget && snowballFight(damagesource))
                {
                    return false;  //Another handler made for sitting fairies just in case.
                }
                else
                {
                    ignoreTarget = true;
                }
            }
            else if (entity instanceof EntityWolf && ((EntityWolf)entity).isTamed() && ((EntityWolf)entity).getOwner().equals(rulerName()))
            {
                EntityWolf wolf = (EntityWolf)entity;
                wolf.setTarget((Entity)null);
                return false; //Protects against ruler-owned wolves.
            }
        }

        if ((guard() || queen()) && i > 1)
        {
            i *= 2; //Guards and queens recieve two thirds damage, won't reduce to 0 if it was at least 1 to begin with.
            i /= 3;
            i = Math.max(i, 1);
        }

        boolean flag = super.attackEntityFrom(damagesource, i);
        fleeingTick = 0; //Stop them from running really fast

        if (flag && health > 0)
        {
            if (entity != null)
            {
                if (entity instanceof EntityLiving && !ignoreTarget)
                {
                    if (entityToAttack == null && cower && rand.nextInt(2) == 0)
                    {
                        cryTime += 120; //Cowering fairies will have a chance of becoming offensive.
                        entityFear = entity;
                        PathEntity doug = roam(entity, this, (float)Math.PI);

                        if (doug != null)
                        {
                            setPathToEntity(doug);
                        }
                    }
                    else     //Become aggressive - no more screwing around.
                    {
                        setTarget((Entity)entity);
                        entityFear = null;
                        cryTime = 0;
                    }
                }
                else     //This just makes fairies run from inanimate objects that hurt them.
                {
                    PathEntity doug = roam(entity, this, (float)Math.PI);

                    if (doug != null)
                    {
                        setPathToEntity(doug);
                    }
                }
            }

            if (isSitting())  //A fairy will get up if hurt while sitting.
            {
                setSitting(false);
            }

            if (ridingEntity != null && rand.nextInt(2) == 0)
            {
                mountEntity(ridingEntity);
            }
        }
        else if (flag)
        {
            if (ridingEntity != null)
            {
                mountEntity(ridingEntity);
            }

            if (queen() && !tamed())
            {
                alertFollowers(entity);
            }
            else
            {
                alertRuler(entity);
            }
        }

        return flag;
    }

    public void alertFollowers(Entity entity)
    {
        if (queen() && getFaction() > 0)
        {
            List list = worldObj.getEntitiesWithinAABB(FRY_EntityFairy.class, boundingBox.expand(40D, 40D, 40D));

            for (int j = 0; j < list.size(); j++)
            {
                FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                if (fairy != this && fairy.health > 0 && sameTeam(fairy) && (fairy.ruler == null || fairy.ruler == this))
                {
                    if (fairy.ridingEntity != null)
                    {
                        fairy.mountEntity(fairy.ridingEntity);
                        //mod_FairyMod.fairyMod.sendFairyMount(fairy, fairy.ridingEntity);
                    }

                    fairy.setTarget((Entity)null);
                    fairy.cryTime = 300;
                    fairy.setFaction(0);
                    //if(entity != null && entity instanceof EntityLiving) {
                    fairy.entityFear = null;
                    //}
                    fairy.ruler = null;
                }
            }
        }
    }

    public void alertRuler(Entity entity)
    {
        if (getFaction() > 0 && ruler != null && ruler instanceof FRY_EntityFairy && sameTeam((FRY_EntityFairy)ruler))
        {
            FRY_EntityFairy queen = ((FRY_EntityFairy)ruler);
            boolean flag = false;
            List list = worldObj.getEntitiesWithinAABB(FRY_EntityFairy.class, queen.boundingBox.expand(40D, 40D, 40D));

            for (int j = 0; j < list.size(); j++)
            {
                FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                if (fairy != queen && fairy.health > 0 && queen.sameTeam(fairy) && (fairy.ruler == null || fairy.ruler == queen))
                {
                    flag = true;
                    break;
                }
            }

            if (!flag)
            {
                queen.setTarget((Entity)null);
                queen.cryTime = 600;
                queen.setFaction(0);
                //if(entity != null && entity instanceof EntityLiving) {
                queen.entityFear = null;
                //}
            }
        }
        else if (tamed() && ruler != null && ruler instanceof EntityPlayer)
        {
            //EntityPlayerMP player = (EntityPlayerMP)ruler;
            String f = getActualName(getFairyName1(), getFairyName2());

            if (queen())
            {
                f = "Queen " + f;
            }

            String s = f;
            int i = rand.nextInt(6);

            if (i == 0)
            {
                s += " bit the dust.";
            }
            else if (i == 1)
            {
                s += " ran into some problems.";
            }
            else if (i == 2)
            {
                s += " went to the big forest in the sky.";
            }
            else if (i == 3)
            {
                s += " had to go away for a while.";
            }
            else if (i == 3)
            {
                s += " lived a good life.";
            }
            else if (i == 4)
            {
                s += " is in a better place now.";
            }
            else
            {
                s += " kicked the bucket.";
            }

            //mod_FairyMod.fairyMod.sendDisband(player, "* §c" + s);
            ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("* §c" + s);
        }
    }

    public void hydraFairy()   //Don't let that spider bite you, spider bite hurt.
    {
        double a = (boundingBox.minX + boundingBox.maxX) / 2D;
        double b = (boundingBox.minY + (double)yOffset) - (double)ySize;
        double c = (boundingBox.minZ + boundingBox.maxZ) / 2D;
        motionX = 0D;
        motionY = -0.1D;
        motionZ = 0D;
        isJumping = false; //Anthony stopped to tie his shoe, and they all went marching on.
        moveForward = 0F;
        moveStrafing = 0F;
        setPathToEntity((PathEntity)null);
        setSitting(true);
        onGround = true;
        List list = worldObj.getEntitiesWithinAABB(FRY_EntityFairy.class, boundingBox.expand(80D, 80D, 80D));

        for (int j = 0; j < list.size(); j++)
        {
            FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

            if (fairy != this && fairy.health > 0 && sameTeam(fairy) && fairy.ridingEntity == null && fairy.riddenByEntity == null)
            {
                fairy.setTarget((Entity)null);
                fairy.cryTime = 0;
                fairy.entityFear = null;
                fairy.setPosition(a, b, c); //I'll pay top dollar for that Gidrovlicheskiy in the window.
                fairy.motionX = 0D;
                fairy.motionY = -0.1D;
                fairy.motionZ = 0D;
                fairy.isJumping = false;
                fairy.moveForward = 0F;
                fairy.moveStrafing = 0F;
                fairy.setPathToEntity((PathEntity)null);
                fairy.setSitting(true);
                fairy.onGround = true;
                fairy.setFlymode(false); //It feels like I'm floating but I'm not
            }
        }
    }

    @Override public int getTalkInterval()
    {
        return 180;
    }

    @Override protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override protected boolean isMovementCeased()
    {
        return isSitting();
        // renderYawOffset = prevRenderYawOffset = rotationYaw;
        // rotationPitch = 10F;
        // return true;
    }

    @Override public boolean isOnLadder()
    {
        return climbing();
    }

    @Override protected void attackEntity(Entity entity, float f)
    {
        if (attackTime <= 0 && f < (tamed() ? 2.5F : 2.0F) && ((entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY) || f == 0F))
        {
            attackTime = 20;

            if (flymode() && canFlap() && scout() && entity instanceof EntityLiving && ridingEntity == null && riddenByEntity == null && entity.ridingEntity == null && entity.riddenByEntity == null && !(entity instanceof FRY_EntityFairy || entity instanceof EntityFlying))
            {
                mountEntity(entity); //Scout's Totally Leet Air Attack.
                setFlymode(true);
                flyTime = 200;
                setCanFlap(true);
                attackTime = 35;
            }
            else
            {
                if (scout() && ridingEntity != null && entity != null && entity == ridingEntity)
                {
                    mountEntity(entity); //The finish of its air attack.
                    attackTime = 35;
                }

                smackThatAss(entity); //normal boring strike.
            }
        }
    }

    protected boolean smackThatAss(Entity entity)
    {
        armSwing(!didSwing); //Swings arm and attacks.

        if (rogue() && healTime <= 0 && entity != null && entity instanceof EntityLiving)
        {
            boolean fred = entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackStrength());

            if (fred)
            {
                applyPoison((EntityLiving)entity);
            }

            return fred;
        }

        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackStrength());
    }

    public void applyPoison(EntityLiving entityliving)
    {
        int greg = rand.nextInt(3);

        if (greg == 0)
        {
            greg = Potion.poison.id;
        }
        else if (greg == 1)
        {
            greg = Potion.weakness.id;
        }
        else
        {
            greg = Potion.blindness.id;
        }

        byte byte0 = 0;

        if (worldObj.difficultySetting > 1)
        {
            if (worldObj.difficultySetting == 2)
            {
                byte0 = 7;
            }
            else if (worldObj.difficultySetting == 3)
            {
                byte0 = 15;
            }
        }

        if (byte0 > 0)
        {
            (entityliving).addPotionEffect(new PotionEffect(greg, byte0 * 20, 0));
        }

        healTime = 100 + rand.nextInt(100);
        setTarget((Entity)null);
        entityFear = entityliving;
        cryTime = healTime;
    }

    public int attackStrength()
    {
        if (queen())  //Self explanatory.
        {
            return 5;
        }
        else if (guard())
        {
            return 4;
        }
        else if (rogue())
        {
            return 3;
        }
        else
        {
            return 2;
        }
    }

    protected void healThatAss(EntityLiving guy)
    {
        armSwing(!didSwing); //Swings arm and heals the specified person.
        EntityPotion potion = new EntityPotion(worldObj, this, handPotion().getItemDamage());
        worldObj.spawnEntityInWorld(potion);
        potion.onImpact(new MovingObjectPosition(guy));
        setPathToEntity((PathEntity)null);
        healTime = 200;
        setRarePotion(rand.nextInt(4) == 0);
    }

    @Override protected String getLivingSound()
    {
        return "fairy." + (queen() ? "queen." : "fairy") + (angry() ? "angry" : "idle");
    }

    @Override protected String getHurtSound()
    {
        return "fairy." + (queen() ? "queen." : "fairy") + "hurt";
    }

    @Override protected String getDeathSound()
    {
        return "fairy." + (queen() ? "queen." : "fairy") + "death";
    }

    @Override public boolean canDespawn()
    {
        return ruler == null && !tamed();
    }

    @Override protected void despawnEntity()
    {
        EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this, -1D);

        if (entityplayer != null)
        {
            double d = ((Entity)(entityplayer)).posX - posX;
            double d1 = ((Entity)(entityplayer)).posY - posY;
            double d2 = ((Entity)(entityplayer)).posZ - posZ;
            double d3 = d * d + d1 * d1 + d2 * d2;

            if (canDespawn() && d3 > 16384D)
            {
                setDead();

                //mod_FairyMod.fairyMod.sendFairyDespawn(this);
                if (queen())
                {
                    despawnFollowers();
                }
            }

            if (entityAge > 600 && rand.nextInt(800) == 0 && d3 > 1024D && canDespawn())
            {
                setDead();

                //mod_FairyMod.fairyMod.sendFairyDespawn(this);
                if (queen())
                {
                    despawnFollowers();
                }
            }
            else if (d3 < 1024D)
            {
                entityAge = 0;
            }
        }
    }

    public void despawnFollowers()
    {
        if (queen() && getFaction() > 0)
        {
            List list = worldObj.getEntitiesWithinAABB(FRY_EntityFairy.class, boundingBox.expand(40D, 40D, 40D));

            for (int j = 0; j < list.size(); j++)
            {
                FRY_EntityFairy fairy = (FRY_EntityFairy)list.get(j);

                if (fairy != this && fairy.health > 0 && sameTeam(fairy) && (fairy.ruler == null || fairy.ruler == this))
                {
                    fairy.setDead();
                    //mod_FairyMod.fairyMod.sendFairyDespawn(fairy);
                }
            }
        }
    }

    @Override public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setShort("Data1", (short)dataWatcher.getWatchableObjectByte(17));
        nbttagcompound.setShort("Data2", (short)dataWatcher.getWatchableObjectByte(18));
        nbttagcompound.setShort("Data3", (short)dataWatcher.getWatchableObjectByte(19));
        nbttagcompound.setShort("Data4", (short)dataWatcher.getWatchableObjectByte(21));
        nbttagcompound.setShort("FlyTime", (short)flyTime);
        nbttagcompound.setShort("CryTime", (short)cryTime);
        nbttagcompound.setShort("HealTime", (short)healTime);
        nbttagcompound.setShort("LoseInterest", (short)loseInterest);
        nbttagcompound.setShort("LoseTeam", (short)loseTeam);
        nbttagcompound.setShort("Snowballin", (short)snowballin);
        nbttagcompound.setInteger("PostX", postX);
        nbttagcompound.setInteger("PostY", postY);
        nbttagcompound.setInteger("PostZ", postZ);
        nbttagcompound.setBoolean("DidHearts", didHearts);
        nbttagcompound.setBoolean("DidSwing", didSwing);
        nbttagcompound.setBoolean("Cower", cower);

        if (fishEntity != null)
        {
            wasFishing = true;
            nbttagcompound.setBoolean("WasFishing", wasFishing);
        }

        nbttagcompound.setBoolean("StayPut", isSitting());
        nbttagcompound.setString("RulerName", rulerName());
        nbttagcompound.setString("CustomName", getCustomName());
    }

    @Override public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        dataWatcher.updateObject(17, Byte.valueOf((byte)nbttagcompound.getShort("Data1")));
        dataWatcher.updateObject(18, Byte.valueOf((byte)nbttagcompound.getShort("Data2")));
        dataWatcher.updateObject(19, Byte.valueOf((byte)nbttagcompound.getShort("Data3")));
        dataWatcher.updateObject(21, Byte.valueOf((byte)nbttagcompound.getShort("Data4")));
        flyTime = nbttagcompound.getShort("FlyTime");
        cryTime = nbttagcompound.getShort("CryTime");
        healTime = nbttagcompound.getShort("HealTime");
        loseInterest = nbttagcompound.getShort("LoseInterest");
        loseTeam = nbttagcompound.getShort("LoseTeam");
        snowballin = nbttagcompound.getShort("Snowballin");
        postX = nbttagcompound.getInteger("PostX");
        postY = nbttagcompound.getInteger("PostY");
        postZ = nbttagcompound.getInteger("PostZ");
        didHearts = nbttagcompound.getBoolean("DidHearts");
        didSwing = nbttagcompound.getBoolean("DidSwing");
        cower = nbttagcompound.getBoolean("Cower");
        wasFishing = nbttagcompound.getBoolean("WasFishing");
        setSitting(nbttagcompound.getBoolean("StayPut"));
        setRulerName(nbttagcompound.getString("RulerName"));
        setCustomName(nbttagcompound.getString("CustomName"));

        if (!worldObj.isRemote)
        {
            setCanHeal(healTime <= 0);
            setPosted(postY > -1);
        }
    }

    @Override public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    @Override public boolean getCanSpawnHere()
    {
        if (super.getCanSpawnHere())
        {
            int x = MathHelper.floor_double(posX);
            int z = MathHelper.floor_double(posZ);
            BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);

            if (biome != null &&
                    biome.minHeight > -0.25F &&
                    biome.maxHeight <= 0.5F &&
                    biome.temperature >= 0.1F &&
                    biome.temperature <= 1.0F &&
                    biome.rainfall > 0.0F &&
                    biome.rainfall <= 0.8F)
            {
                List list = worldObj.getEntitiesWithinAABB(FRY_EntityFairy.class, this.boundingBox.expand(32D, 32D, 32D));

                if ((list == null || list.size() < 1) && !worldObj.isRemote)
                {
                    setJob(0);
                    setSpecialJob(true);
                    heal(30);
                    health = 30;
                    int i = rand.nextInt(15) + 1;
                    setFaction(i);
                    setSkin(rand.nextInt(4));
                    cower = false;
                    createGroup = true;
                }

                return true;
            }
        }

        return false;
    }

    @Override public EntityAnimal spawnBabyAnimal(EntityAnimal entityanimal)   //UpdateMe
    {
        return new FRY_EntityFairy(worldObj);
    }

    @Override public boolean isWheat(ItemStack itemstack)
    {
        return false;
    }

    @Override public int getMaxHealth()   //Max Health
    {
        return queen() ? 30 : rogue() ? 10 : 15;
    }

    @Override public double getYOffset()
    {
        if (ridingEntity != null)
        {
            if (ridingEntity instanceof EntityPlayerSP)
            {
                return yOffset - (flymode() ? 1.15F : 1.35f);
            }

            return yOffset + (flymode() ? 0.65F : 0.55F) - (ridingEntity instanceof EntityChicken ? 0.0F : 0.15F);
        }
        else
        {
            return yOffset;
        }
    }

    public static final ItemStack woodSword = new ItemStack(Item.swordWood, 1);
    public static final ItemStack goldSword = new ItemStack(Item.swordGold, 1);
    public static final ItemStack ironSword = new ItemStack(Item.swordSteel, 1);
    public static final ItemStack healPotion = new ItemStack(Item.potion.shiftedIndex, 1, 16389);
    public static final ItemStack restPotion = new ItemStack(Item.potion.shiftedIndex, 1, 16385);
    public static final ItemStack scoutMap = new ItemStack(Item.map, 1);
    public static final ItemStack fishingStick = new ItemStack(Item.stick, 1);
    public ItemStack tempItem;

    public ItemStack handPotion()
    {
        return (rarePotion() ? restPotion : healPotion);
    }

    @Override protected int getDropItemId()
    {
        return Item.lightStoneDust.shiftedIndex;
    }

    @Override public ItemStack getHeldItem()
    {
        if (tempItem != null)
        {
            return tempItem;
        }
        else if (queen())     //Queens always carry the gold/iron sword, guards always have the wooden sword.
        {
            if (getSkin() % 2 == 1)
            {
                return ironSword;
            }
            else
            {
                return goldSword;
            }
        }
        else if (guard())
        {
            return woodSword;
        }
        else if (medic() && canHeal() && !angry())    //Medics carry potions
        {
            return handPotion();
        }
        else if (scout())    //Scouts have maps now.
        {
            return scoutMap;
        }
        else
        {
            return super.getHeldItem();
        }
    }

    public float sinage;
    public int flyTime, cryTime, swingProgressInt, listActions, particleCount, healTime, loseInterest, loseTeam, witherTime, snowballin;
    private int postX, postY, postZ;
    public boolean flyBlocked, didHearts, didSwing, cower, isSwinging, createGroup, wasFishing;
    public EntityLiving ruler, entityHeal;
    public Entity entityFear;
    public FRY_EntityFishHook fishEntity;

    public PathEntity roam(Entity entity1, Entity entity2, float griniscule)
    {
        //if griniscule is 0F, entity2 will roam towards entity1.
        //if griniscule is pi, entity2 will roam away from entity1.
        //Also, a griniscule is a portmanteau of grin and miniscule.
        double a = entity1.posX - entity2.posX;
        double b = entity1.posZ - entity2.posZ;
        double crazy = Math.atan2(a, b);
        crazy += (rand.nextFloat() - rand.nextFloat()) * 0.25D;
        crazy += griniscule;
        double c = entity2.posX + (Math.sin(crazy) * 8F);
        double d = entity2.posZ + (Math.cos(crazy) * 8F);
        int x = MathHelper.floor_double(c);
        int y = MathHelper.floor_double(entity2.boundingBox.minY);
        int z = MathHelper.floor_double(d);

        for (int q = 0; q < 32; q++)
        {
            int i = x + rand.nextInt(5) - rand.nextInt(5);
            int j = y + rand.nextInt(5) - rand.nextInt(5);
            int k = z + rand.nextInt(5) - rand.nextInt(5);

            if (j > 4 && j < worldObj.getHeight() - 1 && isAirySpace(i, j, k) && !isAirySpace(i, j - 1, k))
            {
                PathEntity dogs = worldObj.getEntityPathToXYZ(entity2, i, j, k, 16F, false, false, true, true);

                if (dogs != null)
                {
                    return dogs;
                }
            }
        }

        return (PathEntity)null;
    }

    public PathEntity roamBlocks(double t, double u, double v, Entity entity2, float griniscule)
    {
        // t is an X coordinate, u is a Y coordinate, v is a Z coordinate.
        // Griniscule of 0.0 means towards, 3.14 means away.
        double a = t - entity2.posX;
        double b = v - entity2.posZ;
        double crazy = Math.atan2(a, b);
        crazy += (rand.nextFloat() - rand.nextFloat()) * 0.25D;
        crazy += griniscule;
        double c = entity2.posX + (Math.sin(crazy) * 8F);
        double d = entity2.posZ + (Math.cos(crazy) * 8F);
        int x = MathHelper.floor_double(c);
        int y = MathHelper.floor_double(entity2.boundingBox.minY + (rand.nextFloat() * (u - entity2.boundingBox.minY)));
        int z = MathHelper.floor_double(d);

        for (int q = 0; q < 32; q++)
        {
            int i = x + rand.nextInt(5) - rand.nextInt(5);
            int j = y + rand.nextInt(5) - rand.nextInt(5);
            int k = z + rand.nextInt(5) - rand.nextInt(5);

            if (j > 4 && j < worldObj.getHeight() - 1 && isAirySpace(i, j, k) && !isAirySpace(i, j - 1, k))
            {
                PathEntity dogs = worldObj.getEntityPathToXYZ(entity2, i, j, k, 16F, false, false, true, true);

                if (dogs != null)
                {
                    return dogs;
                }
            }
        }

        return (PathEntity)null;
    }

    private void teleportToRuler(Entity entity)   //Can teleport to the ruler if he has an enderman drop in his inventory.
    {
        int i = MathHelper.floor_double(entity.posX) - 2;
        int j = MathHelper.floor_double(entity.posZ) - 2;
        int k = MathHelper.floor_double(entity.boundingBox.minY);

        for (int l = 0; l <= 4; l++)
        {
            for (int i1 = 0; i1 <= 4; i1++)
            {
                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && worldObj.isBlockNormalCube(i + l, k - 1, j + i1) && !worldObj.isBlockNormalCube(i + l, k, j + i1) && !worldObj.isBlockNormalCube(i + l, k + 1, j + i1) && isAirySpace(i + l, k, j + i1))
                {
                    setLocationAndAngles((float)(i + l) + 0.5F, k, (float)(j + i1) + 0.5F, rotationYaw, rotationPitch);
                    return;
                }
            }
        }
    }

    @Override public int getItemIcon(ItemStack itemstack, int i)
    {
        int j = super.getItemIcon(itemstack, i);

        if (itemstack.itemID == Item.potion.shiftedIndex)
        {
            if (i == 1)
            {
                return itemstack.getIconIndex();
            }
            else
            {
                return 141;
            }
        }

        return j;
    }

    //################################
    // DATA WATCHER STUFF AND STRINGS
    //################################

    protected void armSwing(boolean flag)
    {
        setFairyFlag(0, flag);
        setTempItem(0);
    }

    public boolean getArmSwing()
    {
        return getFairyFlag(0);
    }

    protected void setFlymode(boolean flag)
    {
        setFairyFlag(1, flag);
    }

    public boolean flymode()
    {
        return getFairyFlag(1);
    }

    protected void setCanFlap(boolean flag)
    {
        setFairyFlag(2, flag);
    }

    public boolean canFlap()
    {
        return getFairyFlag(2);
    }

    protected void setTamed(boolean flag)
    {
        setFairyFlag(3, flag);
    }

    public boolean tamed()
    {
        return getFairyFlag(3);
    }

    protected void setAngry(boolean flag)
    {
        setFairyFlag(4, flag);
    }

    public boolean angry()
    {
        return getFairyFlag(4);
    }

    protected void setCrying(boolean flag)
    {
        setFairyFlag(5, flag);
    }

    public boolean crying()
    {
        return getFairyFlag(5);
    }

    protected void setLiftOff(boolean flag)
    {
        setFairyFlag(6, flag);
    }

    public boolean liftOff()
    {
        return getFairyFlag(6);
    }

    protected void setHearts(boolean flag)
    {
        setFairyFlag(7, flag);
    }

    public boolean hearts()
    {
        return getFairyFlag(7);
    }

    protected boolean getFairyFlag(int i)
    {
        return (dataWatcher.getWatchableObjectByte(17) & 1 << i) != 0;
    }

    protected void setFairyFlag(int i, boolean flag)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(17);

        if (flag)
        {
            dataWatcher.updateObject(17, Byte.valueOf((byte)(byte0 | 1 << i)));
        }
        else
        {
            dataWatcher.updateObject(17, Byte.valueOf((byte)(byte0 & ~(1 << i))));
        }
    }

    protected void setSkin(int i)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(18);

        if (i < 0)
        {
            i = 0;
        }
        else if (i > 3)
        {
            i = 3;
        }

        byte0 = (byte)(byte0 & 0xfc);
        byte0 = (byte)(byte0 | (byte)i);
        dataWatcher.updateObject(18, Byte.valueOf(byte0));
    }

    public int getSkin()
    {
        return dataWatcher.getWatchableObjectByte(18) & 0x03;
    }

    protected void setJob(int i)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(18);

        if (i < 0)
        {
            i = 0;
        }
        else if (i > 3)
        {
            i = 3;
        }

        byte0 = (byte)(byte0 & 0xf3);
        byte0 = (byte)(byte0 | ((byte)i << 2));
        dataWatcher.updateObject(18, Byte.valueOf(byte0));
    }

    public int getJob()
    {
        return (byte)(dataWatcher.getWatchableObjectByte(18) >> 2) & 0x03;
    }

    public boolean normal()
    {
        return getJob() == 0 && !specialJob();
    }

    public boolean guard()
    {
        return getJob() == 1 && !specialJob();
    }

    public boolean scout()
    {
        return getJob() == 2 && !specialJob();
    }

    public boolean medic()
    {
        return getJob() == 3 && !specialJob();
    }

    public boolean queen()
    {
        return getJob() == 0 && specialJob();
    }

    public boolean rogue()
    {
        return getJob() == 1 && specialJob();
    }

    protected void setFaction(int i)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(18);

        if (i < 0)
        {
            i = 0;
        }
        else if (i > 15)
        {
            i = 15;
        }

        byte0 = (byte)(byte0 & 0x0f);
        byte0 = (byte)(byte0 | ((byte)i << 4));
        dataWatcher.updateObject(18, Byte.valueOf(byte0));
    }

    public int getFaction()
    {
        return (byte)(dataWatcher.getWatchableObjectByte(18) >> 4) & 0x0f;
    }

    protected void setFairyName(int i, int j)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(19);

        if (i < 0)
        {
            i = 0;
        }
        else if (i > 15)
        {
            i = 15;
        }

        if (j < 0)
        {
            j = 0;
        }
        else if (j > 15)
        {
            j = 15;
        }

        byte0 = (byte)(((byte)i & 0x0f) | (((byte)j & 0x0f) << 4));
        dataWatcher.updateObject(19, Byte.valueOf(byte0));
    }

    public int getFairyName1()
    {
        return (byte)dataWatcher.getWatchableObjectByte(19) & 0x0f;
    }

    public int getFairyName2()
    {
        return (byte)(dataWatcher.getWatchableObjectByte(19) >> 4) & 0x0f;
    }

    private static final String name1[] =
    {
        "Silly",
        "Fire",
        "Twinkle",
        "Bouncy",
        "Speedy",
        "Wiggle",
        "Fluffy",
        "Cloudy",
        "Floppy",
        "Ginger",
        "Sugar",
        "Winky",
        "Giggle",
        "Cutie",
        "Sunny",
        "Honey"
    };

    private static final String name2[] =
    {
        "puff",
        "poof",
        "butt",
        "munch",
        "star",
        "bird",
        "wing",
        "shine",
        "snap",
        "kins",
        "bee",
        "chime",
        "button",
        "bun",
        "heart",
        "boo"
    };

    private static final String faction[] =
    {
        "no queen",
        "<Aviary Army>",
        "<Bantam Brawlers>",
        "<Charging Cherubs>",
        "<Dainty Demons>",
        "<Enigmatic Escorts>",
        "<Floating Fury>",
        "<Graceful Gliders>",
        "<Hardy Handmaids>",
        "<Iron Imps>",
        "<Opulent Order>",
        "<Kute Killers>",
        "<Lethal Ladies>",
        "<Maiden Militia>",
        "<Nimble Nymphs>",
        "<Petite Pugilists>"
    };

    private static final String factionColor[] =
    {
        "§0",
        "§1",
        "§2",
        "§3",
        "§4",
        "§5",
        "§6",
        "§7",
        "§8",
        "§9",
        "§a",
        "§b",
        "§c",
        "§d",
        "§e",
        "§f"
    };

    public String getActualName(int i, int j)
    {
        if (i < 0 || i > 15 || j < 0 || j > 15)
        {
            return "Error-name";
        }

        if (getCustomName() != null && !getCustomName().equals(""))
        {
            return getCustomName();
        }
        else
        {
            return name1[i] + "-" + name2[j];
        }
    }

    public String getQueenName(int i, int j, int k)
    {
        if (i < 0 || i > 15 || j < 0 || j > 15 || k < 0 || k > 15)
        {
            return "Error-name";
        }

        return factionColor[k] + "Queen " + getActualName(i, j);
    }

    public String getFactionName(int k)
    {
        if (k < 0 || k > 15)
        {
            return "Error-name";
        }

        return factionColor[k] + faction[k];
    }

    public String getDisplayName()
    {
        if (getFaction() != 0)
        {
            if (queen())
            {
                return getQueenName(getFairyName1(), getFairyName2(), getFaction());
            }
            else
            {
                return getFactionName(getFaction());
            }
        }
        else
        {
            if (tamed())
            {
                String woosh = getActualName(getFairyName1(), getFairyName2());

                if (queen())
                {
                    woosh = "Queen " + woosh;
                }

                if (ModLoader.getMinecraftInstance().thePlayer.username.equals(rulerName()))
                {
                    woosh = (posted() ? "§a" : "§c") + "@§f" + woosh + (posted() ? "§a" : "§c") + "@";
                }

                return woosh;
            }
            else
            {
                return (String)null;
            }
        }
    }

    public String rulerName()
    {
        return dataWatcher.getWatchableObjectString(20);
    }

    public void setRulerName(String s)
    {
        dataWatcher.updateObject(20, s);
    }

    public boolean isSitting()   //Variables concerning sitting for tamed fairies.
    {
        return getFlag(1);
    }

    public void setSitting(boolean flag)
    {
        setFlag(1, flag);
    }

    protected boolean getFairyFlagTwo(int i)   //Second set of fairy flags.
    {
        return (dataWatcher.getWatchableObjectByte(21) & 1 << i) != 0;
    }

    protected void setFairyFlagTwo(int i, boolean flag)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(21);

        if (flag)
        {
            dataWatcher.updateObject(21, Byte.valueOf((byte)(byte0 | 1 << i)));
        }
        else
        {
            dataWatcher.updateObject(21, Byte.valueOf((byte)(byte0 & ~(1 << i))));
        }
    }

    public boolean canHeal()   //Whether or not the potion should be shown.
    {
        return getFairyFlagTwo(0);
    }

    public void setCanHeal(boolean flag)
    {
        setFairyFlagTwo(0, flag);
    }

    public boolean rarePotion()   //The type of potion that the fairy will use.
    {
        return getFairyFlagTwo(1);
    }

    public void setRarePotion(boolean flag)
    {
        setFairyFlagTwo(1, flag);
    }

    public boolean specialJob()   //Whether or not the fairy is a special class: queen etc
    {
        return getFairyFlagTwo(2);
    }

    public void setSpecialJob(boolean flag)
    {
        setFairyFlagTwo(2, flag);
    }

    public boolean nameEnabled()   //If the fairy has been given a naming item
    {
        return getFairyFlagTwo(3);
    }

    public void setNameEnabled(boolean flag)
    {
        setFairyFlagTwo(3, flag);
    }

    public boolean climbing()   //Going to allow fairies to fly up walls.
    {
        return getFairyFlagTwo(4);
    }

    public void setClimbing(boolean flag)
    {
        setFairyFlagTwo(4, flag);
    }

    public boolean posted()   //Says if a fairy has a post or not.
    {
        return getFairyFlagTwo(5);
    }

    public void setPosted(boolean flag)
    {
        setFairyFlagTwo(5, flag);
    }

    public boolean withered()   //Says if a fairy has the withering disease.
    {
        return getFairyFlagTwo(6);
    }

    public void setWithered(boolean flag)
    {
        setFairyFlagTwo(6, flag);
    }

    public boolean hairType()   //Defines a fairy's current hair type.
    {
        return getFairyFlagTwo(7);
    }

    public void setHairType(boolean flag)
    {
        setFairyFlagTwo(7, flag);
    }

    protected void setFairyHealth(int i)
    {
        byte byte0 = dataWatcher.getWatchableObjectByte(19);

        if (i < 0)
        {
            i = 0;
        }
        else if (i > 255)
        {
            i = 255;
        }

        byte0 = (byte)((byte)i & 0xff);
        dataWatcher.updateObject(22, Byte.valueOf(byte0));
    }

    protected void setFairyClimbing(boolean flag)
    {
        setClimbing(flag);
    }

    public int fairyHealth()
    {
        return (byte)dataWatcher.getWatchableObjectByte(22) & 0xff;
    }

    public String getCustomName()   //Custom name of the fairy, enabled by paper.
    {
        return dataWatcher.getWatchableObjectString(23);
    }

    public void setCustomName(String s)
    {
        dataWatcher.updateObject(23, s);
    }

    public int getTempItem()   //A temporary item shown while arm is swinging, related to jobs.
    {
        return dataWatcher.getWatchableObjectInt(24);
    }

    public void setTempItem(int i)
    {
        dataWatcher.updateObject(24, i);
    }
}
