//Credit for the original CustomSpawner.class goes to DrZhark.

package fairies.world;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fairies.entity.EntityFairy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.biome.BiomeGenEnd;
import net.minecraft.world.biome.BiomeGenHell;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class Spawner {
	private int			maxAnimals					= 40;
	private int			maxMobs						= 60;
	private int			maxAquatic					= 10;
	public BiomeGenBase	standardBiomes[];
	public List<String>			biomeList;
	@SuppressWarnings("rawtypes")
	public List[]		entityClasses;
	@SuppressWarnings("rawtypes")
	protected List[]	customMobSpawnList;
	@SuppressWarnings("rawtypes")
	protected List[]	customCreatureSpawnList;
	@SuppressWarnings("rawtypes")
	protected List[]	customAquaticSpawnList;
	private Set<ChunkCoordIntPair>			eligibleChunksForSpawning	= new HashSet<ChunkCoordIntPair>();
	@SuppressWarnings("rawtypes")
	private List<Class>	vanillaClassList;

	@SuppressWarnings("rawtypes")
	public Spawner() {
		biomeList = new ArrayList<String>();

		try {
			Field afield[] = (BiomeGenBase.class).getDeclaredFields();
			LinkedList<BiomeGenBase> linkedlist = new LinkedList<BiomeGenBase>();

			for (int j = 0; j < afield.length; j++) {
				Class<?> class1 = afield[j].getType();

				if ((afield[j].getModifiers() & 8) != 0 && class1.isAssignableFrom(BiomeGenBase.class)) {
					BiomeGenBase biomegenbase = (BiomeGenBase) afield[j].get(null);
					biomeList.add(biomegenbase.biomeName);

					if (!(biomegenbase instanceof BiomeGenHell) && !(biomegenbase instanceof BiomeGenEnd)) {
						linkedlist.add(biomegenbase);
					}
				}
			}

			standardBiomes = (BiomeGenBase[]) linkedlist.toArray(new BiomeGenBase[0]);
			customCreatureSpawnList = new List[biomeList.size()];
			customMobSpawnList = new List[biomeList.size()];
			customAquaticSpawnList = new List[biomeList.size()];
			entityClasses = new List[3];
			vanillaClassList = new ArrayList<Class>();
			vanillaClassList.add(EntityChicken.class);
			vanillaClassList.add(EntityCow.class);
			vanillaClassList.add(EntityPig.class);
			vanillaClassList.add(EntitySheep.class);
			vanillaClassList.add(EntityWolf.class);
			vanillaClassList.add(EntitySquid.class);
			clearLists();
		} catch (IllegalAccessException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	// Obfuscated name lookups
	private static final String[] MCP_CREATUREMATERIAL = { "creatureMaterial", "field_70703_bu" };

	private Material getCreatureMaterial(EnumCreatureType type) {
		return ReflectionHelper.getPrivateValue(EnumCreatureType.class, type, MCP_CREATUREMATERIAL);
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent event) {
		final World world = event.world;
		if( world != null && !world.isRemote 
				&& world.getDifficulty() != EnumDifficulty.PEACEFUL 
				&& world.getWorldInfo().getWorldTime() % 300L == 0L ) {
			final List<?> list = world.playerEntities;
			if( list != null && list.size() > 0 ) {
				final int maxNum = 12 + (list.size() * 6);
				this.setMaxAnimals(maxNum);
			}
			this.doCustomSpawning(world, true, true);
		}
	}

	protected BlockPos getRandomSpawningPointInChunk(World world, int i, int j) {
		int k = i + world.rand.nextInt(16);
		int l = world.rand.nextInt(128);
		int i1 = j + world.rand.nextInt(16);
		return new BlockPos(k, l, i1);
	}

	public void clearLists() {
		for (int x = 0; x < biomeList.size(); x++) {
			customCreatureSpawnList[x] = new ArrayList<Object>();
			customMobSpawnList[x] = new ArrayList<Object>();
			customAquaticSpawnList[x] = new ArrayList<Object>();
		}

		for (int x = 0; x < 3; x++) {
			entityClasses[x] = new ArrayList<Object>();
		}
	}

	// this one spawns a single mob up to max times
	public final int doSpecificSpawning(World worldObj, Class<?> myClass, int max, EnumCreatureType enumcreaturetype) {
		// boolean flag = false;
		// this initialises chunks for spawning
		eligibleChunksForSpawning.clear();
		int countTotal;
		int var6;

		for (countTotal = 0; countTotal < worldObj.playerEntities.size(); ++countTotal) {
			EntityPlayer entityplayer = (EntityPlayer) worldObj.playerEntities.get(countTotal);
			int var5 = MathHelper.floor_double(entityplayer.posX / 16.0D);
			var6 = MathHelper.floor_double(entityplayer.posZ / 16.0D);
			byte var7 = 8;

			for (int var8 = -var7; var8 <= var7; ++var8) {
				for (int var9 = -var7; var9 <= var7; ++var9) {
					eligibleChunksForSpawning.add(new ChunkCoordIntPair(var8 + var5, var9 + var6));
				}
			}
		}

		countTotal = 0;
		BlockPos chunkcoordspawn = worldObj.getSpawnPoint();
		Iterator<ChunkCoordIntPair> iterator = eligibleChunksForSpawning.iterator();
		label113:

		while (iterator.hasNext()) {
			ChunkCoordIntPair var10 = (ChunkCoordIntPair) iterator.next();
			BlockPos chunkpos = getRandomSpawningPointInChunk(worldObj, var10.chunkXPos * 16,
					var10.chunkZPos * 16);
			final int chunkX = chunkpos.getX();
			final int chunkY = chunkpos.getY();
			final int chunkZ = chunkpos.getZ();

			final Block block = worldObj.getBlockState(chunkpos).getBlock();
			if (!block.isNormalCube() && block.getMaterial() == getCreatureMaterial(enumcreaturetype)) {
				int countSpawn = 0;

				for (int var21 = 0; var21 < 3; ++var21) {
					int tempPosX = chunkX;
					int tempPosY = chunkY;
					int tempPosZ = chunkZ;
					final byte var25 = 6;

					for (int var26 = 0; var26 < 4; ++var26) {
						tempPosX += worldObj.rand.nextInt(var25) - worldObj.rand.nextInt(var25);
						tempPosY += worldObj.rand.nextInt(1) - worldObj.rand.nextInt(1);
						tempPosZ += worldObj.rand.nextInt(var25) - worldObj.rand.nextInt(var25);

						if (canCreatureTypeSpawnAtLocation(enumcreaturetype, worldObj, tempPosX, tempPosY, tempPosZ)) {
							float finalPosX = (float) tempPosX + 0.5F;
							float finalPosY = (float) tempPosY;
							float finalPosZ = (float) tempPosZ + 0.5F;

							if (worldObj.getClosestPlayer((double) finalPosX, (double) finalPosY, (double) finalPosZ,
									24.0D) == null) {
								float distSpawnX = finalPosX - (float) chunkcoordspawn.getX();
								float distSpawnY = finalPosY - (float) chunkcoordspawn.getY();
								float distSpawnZ = finalPosZ - (float) chunkcoordspawn.getZ();
								float sqDist = distSpawnX * distSpawnX + distSpawnY * distSpawnY
										+ distSpawnZ * distSpawnZ;

								if (sqDist >= 576.0F) {
									EntityLiving entityliving;

									try {
										entityliving = (EntityLiving) myClass
												.getConstructor(new Class[] { World.class })
												.newInstance(new Object[] { worldObj });
									} catch (Exception exception) {
										exception.printStackTrace();
										return countTotal;
									}

									entityliving.setLocationAndAngles((double) finalPosX, (double) finalPosY,
											(double) finalPosZ, worldObj.rand.nextFloat() * 360.0F, 0.0F);

									if (entityliving.getCanSpawnHere()) {
										++countSpawn;
										countTotal += countSpawn;

										if (countTotal > max) {
											return countTotal;
										}

										worldObj.spawnEntityInWorld(entityliving);

										if (countSpawn >= entityliving.getMaxSpawnedInChunk()) {
											continue label113;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return countTotal;
	}

	// regular spawning with list
	public final int doCustomSpawning(World worldObj, boolean spawnMobs, boolean spawnAnmls) {
		if (!spawnMobs && !spawnAnmls) {
			return 0;
		} else {
			eligibleChunksForSpawning.clear();
			int countTotal;
			int var6;

			for (countTotal = 0; countTotal < worldObj.playerEntities.size(); ++countTotal) {
				EntityPlayer entityplayer = (EntityPlayer) worldObj.playerEntities.get(countTotal);
				int var5 = MathHelper.floor_double(entityplayer.posX / 16.0D);
				var6 = MathHelper.floor_double(entityplayer.posZ / 16.0D);
				byte var7 = 8;

				for (int var8 = -var7; var8 <= var7; ++var8) {
					for (int var9 = -var7; var9 <= var7; ++var9) {
						eligibleChunksForSpawning.add(new ChunkCoordIntPair(var8 + var5, var9 + var6));
					}
				}
			}

			countTotal = 0;
			BlockPos chunkcoordspawn = worldObj.getSpawnPoint();
			EnumCreatureType[] enumcreaturevalues = EnumCreatureType.values();
			var6 = enumcreaturevalues.length;

			for (int var37 = 0; var37 < var6; ++var37) {
				EnumCreatureType enumcreaturetype = enumcreaturevalues[var37];
				int enumC = countSpawnedEntities(worldObj, enumcreaturetype);

				if ((!enumcreaturetype.getPeacefulCreature() || spawnAnmls)
						&& (enumcreaturetype.getPeacefulCreature() || spawnMobs) && (enumC < getMax(enumcreaturetype))) // *
																														// eligibleChunksForSpawning.size()
																														// /
																														// 256))
				{
					Iterator<ChunkCoordIntPair> iterator = eligibleChunksForSpawning.iterator();
					label113:

					while (iterator.hasNext()) {
						ChunkCoordIntPair var10 = (ChunkCoordIntPair) iterator.next();
						BiomeGenBase biomegenbase = worldObj.getWorldChunkManager().getBiomeGenerator(var10.getCenterBlock(64)); // gets the kind of biome the chunk is at i.e. forest, etc
						List<?> listspawns = getCustomBiomeSpawnList(getCustomSpawnableList(enumcreaturetype),
								biomegenbase);

						if (listspawns != null && !listspawns.isEmpty()) {
							int var13 = 0;
							SpawnListEntry spawnlistentry;

							for (Iterator<?> iteratorB = listspawns.iterator(); iteratorB
									.hasNext(); var13 += spawnlistentry.itemWeight) {
								spawnlistentry = (SpawnListEntry) iteratorB.next();
							}

							int var40 = worldObj.rand.nextInt(var13);
							spawnlistentry = (SpawnListEntry) listspawns.get(0);
							Iterator<?> iteratorC = listspawns.iterator();

							while (iteratorC.hasNext()) {
								SpawnListEntry spawnlistentryA = (SpawnListEntry) iteratorC.next();
								var40 -= spawnlistentryA.itemWeight;

								if (var40 < 0) {
									spawnlistentry = spawnlistentryA;
									break;
								}
							}

							int max = spawnlistentry.maxGroupCount;

							if (max > 0) {
								Class<?> class1 = spawnlistentry.entityClass;

								if (class1 != null && (max > countEntities(class1, worldObj))) {
									continue label113;
								}
							}

							BlockPos chunkpos = getRandomSpawningPointInChunk(worldObj, var10.chunkXPos * 16,
									var10.chunkZPos * 16);
							final int chunkX = chunkpos.getX();
							final int chunkY = chunkpos.getY();
							final int chunkZ = chunkpos.getZ();

							final Block block = worldObj.getBlockState(chunkpos).getBlock();
							if (!block.isNormalCube()
									&& block.getMaterial() == getCreatureMaterial(enumcreaturetype)) {
								int countSpawn = 0;

								for (int var21 = 0; var21 < 3; ++var21) {
									int tempPosX = chunkX;
									int tempPosY = chunkY;
									int tempPosZ = chunkZ;
									final byte var25 = 6;

									for (int var26 = 0; var26 < 4; ++var26) {
										tempPosX += worldObj.rand.nextInt(var25) - worldObj.rand.nextInt(var25);
										tempPosY += worldObj.rand.nextInt(1) - worldObj.rand.nextInt(1);
										tempPosZ += worldObj.rand.nextInt(var25) - worldObj.rand.nextInt(var25);

										if (canCreatureTypeSpawnAtLocation(enumcreaturetype, worldObj, tempPosX, tempPosY, tempPosZ)) {
											float finalPosX = (float) tempPosX + 0.5F;
											float finalPosY = (float) tempPosY;
											float finalPosZ = (float) tempPosZ + 0.5F;

											if (worldObj.getClosestPlayer((double) finalPosX, (double) finalPosY, (double) finalPosZ, 24.0D) == null) {
												float distSpawnX = finalPosX - (float) chunkcoordspawn.getX();
												float distSpawnY = finalPosY - (float) chunkcoordspawn.getY();
												float distSpawnZ = finalPosZ - (float) chunkcoordspawn.getZ();
												float sqDist = distSpawnX * distSpawnX + distSpawnY * distSpawnY
														+ distSpawnZ * distSpawnZ;

												if (sqDist >= 576.0F) {
													EntityLiving entityliving;

													try {
														entityliving = (EntityLiving) spawnlistentry.entityClass
																.getConstructor(new Class[] { World.class })
																.newInstance(new Object[] { worldObj });
													} catch (Exception exception) {
														exception.printStackTrace();
														return countTotal;
													}

													entityliving.setLocationAndAngles((double) finalPosX,
															(double) finalPosY, (double) finalPosZ,
															worldObj.rand.nextFloat() * 360.0F, 0.0F);

													if (entityliving.getCanSpawnHere()) {
														enumC = countSpawnedEntities(worldObj, enumcreaturetype);

														if (enumC >= getMax(enumcreaturetype)) {
															continue label113;
														}

														++countSpawn;
														countTotal += countSpawn;
														worldObj.spawnEntityInWorld(entityliving);

														if (countSpawn >= entityliving.getMaxSpawnedInChunk()) {
															continue label113;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			return countTotal;
		}
	}

	public void AddCustomSpawn(Class<?extends EntityLiving> class1, int i, int max, EnumCreatureType enumcreaturetype) {
		AddCustomSpawn(class1, i, -1, max, enumcreaturetype, null);
	}

	public void AddCustomSpawn(Class<?extends EntityLiving> class1, int i, EnumCreatureType enumcreaturetype) {
		AddCustomSpawn(class1, i, -1, -1, enumcreaturetype, null);
	}

	public void AddCustomSpawn(Class<?extends EntityLiving> class1, int i, int max, EnumCreatureType enumcreaturetype,
			BiomeGenBase abiomegenbase[]) {
		AddCustomSpawn(class1, i, -1, max, enumcreaturetype, abiomegenbase);
	}

	public void AddCustomSpawn(Class<?extends EntityLiving> class1, int i, EnumCreatureType enumcreaturetype, BiomeGenBase abiomegenbase[]) {
		AddCustomSpawn(class1, i, -1, -1, enumcreaturetype, abiomegenbase);
	}

	// this one adds spawn where biome is not specified
	public void AddCustomSpawn(Class<?extends EntityLiving> class1, int i, int j, int k, EnumCreatureType enumcreaturetype) {
		AddCustomSpawn(class1, i, j, k, enumcreaturetype, null);
	}

	@SuppressWarnings("unchecked")
	public void AddCustomSpawn(Class<?extends EntityLiving> class1, int i, int j, int k, EnumCreatureType enumcreaturetype,
			BiomeGenBase abiomegenbase[]) {
		if (class1 == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		}

		if (enumcreaturetype == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		}

		if (abiomegenbase == null) {
			abiomegenbase = standardBiomes;
		}

		int x1 = getEnumIndex(enumcreaturetype);
		{
			boolean flag = false;

			for (Iterator<?> iterator = entityClasses[x1].iterator(); iterator.hasNext();) {
				if (iterator != null) {
					Class<?> class2 = (Class<?>) iterator.next();

					if (class2 == class1) {
						flag = true;
						break;
					}
				}
			}

			if (!flag) {
				entityClasses[x1].add(class1);
			}
		}

		for (int l = 0; l < abiomegenbase.length; l++) {
			@SuppressWarnings("rawtypes")
			List[] fulllist = getCustomSpawnableList(enumcreaturetype);

			if (fulllist != null) {
				int x = biomeList.indexOf(abiomegenbase[l].biomeName);
				boolean flag = false;

				for (Iterator<?> iterator = fulllist[x].iterator(); iterator.hasNext();) {
					if (iterator != null) {
						SpawnListEntry spawnlistentry = (SpawnListEntry) iterator.next();

						if (spawnlistentry.entityClass == class1) {
							spawnlistentry.itemWeight = i;
							spawnlistentry.minGroupCount = j;
							spawnlistentry.maxGroupCount = k;
							flag = true;
							break;
						}
					}
				}

				if (!flag) {
					fulllist[x].add(new SpawnListEntry(class1, i, j, k));
				}
			}
		}
	}

	public void RemoveCustomSpawn(Class<?> class1, EnumCreatureType enumcreaturetype) {
		RemoveCustomSpawn(class1, enumcreaturetype, null);
	}

	public void RemoveCustomSpawn(Class<?> class1, EnumCreatureType enumcreaturetype, BiomeGenBase abiomegenbase[]) {
		if (class1 == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		}

		if (enumcreaturetype == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		}

		if (abiomegenbase == null) {
			abiomegenbase = standardBiomes;
		}

		for (int l = 0; l < abiomegenbase.length; l++) {
			@SuppressWarnings("rawtypes")
			List[] fulllist = getCustomSpawnableList(enumcreaturetype);

			if (fulllist != null) {
				int x = biomeList.indexOf(abiomegenbase[l].biomeName);

				for (Iterator<?> iterator = fulllist[x].iterator(); iterator.hasNext();) {
					if (iterator != null) {
						SpawnListEntry spawnlistentry = (SpawnListEntry) iterator.next();

						if (spawnlistentry.entityClass == class1) {
							iterator.remove();
						}
					}
				}
			}
		}
	}

	private int getEnumIndex(EnumCreatureType enumcreaturetype) {
		if (enumcreaturetype == EnumCreatureType.MONSTER) {
			return 0;
		}

		if (enumcreaturetype == EnumCreatureType.CREATURE) {
			return 1;
		}

		if (enumcreaturetype == EnumCreatureType.WATER_CREATURE) {
			return 2;
		} else {
			return -1;
		}
	}

	public int countSpawnedEntities(World world, EnumCreatureType enumcreaturetype) {
		int i = getEnumIndex(enumcreaturetype);
		int finalcount = 0;
		if( i > -1 ) {
			// boolean flag = false;

			for (Iterator<?> iterator = entityClasses[i].iterator(); iterator.hasNext();) {
				if (iterator != null) {
					Class<?> class1 = (Class<?>) iterator.next();

					if (class1 != null) {
						finalcount += world.countEntities(class1);
					}
				}
			}
		}
		return finalcount;
	}

	@SuppressWarnings("rawtypes")
	private List[] getCustomSpawnableList(EnumCreatureType enumcreaturetype) {
		if (enumcreaturetype == EnumCreatureType.MONSTER) {
			return customMobSpawnList;
		}

		if (enumcreaturetype == EnumCreatureType.CREATURE) {
			return customCreatureSpawnList;
		}

		if (enumcreaturetype == EnumCreatureType.WATER_CREATURE) {
			return customAquaticSpawnList;
		} else {
			return null;
		}
	}

	private List<?> getCustomBiomeSpawnList(@SuppressWarnings("rawtypes") List[] fulllist, BiomeGenBase biomegenbase) {
		int x = biomeList.indexOf(biomegenbase.biomeName);

		if (x >= 0) {
			return fulllist[x];
		}

		return null;
	}

	private int getMax(EnumCreatureType enumcreaturetype) {
		if (enumcreaturetype == EnumCreatureType.MONSTER) {
			return getMaxMobs();
		}

		if (enumcreaturetype == EnumCreatureType.CREATURE) {
			return getMaxAnimals();
		}

		if (enumcreaturetype == EnumCreatureType.WATER_CREATURE) {
			return getMaxAquatic();
		} else {
			return -1;
		}
	}

	public int getMaxAnimals() {
		return maxAnimals;
	}

	public void setMaxAnimals(int max) {
		maxAnimals = max;
	}

	public int getMaxMobs() {
		return maxMobs;
	}

	public void setMaxMobs(int max) {
		maxMobs = max;
	}

	public int getMaxAquatic() {
		return maxAquatic;
	}

	public void setMaxAquatic(int max) {
		maxAquatic = max;
	}
	
	private boolean canCreatureTypeSpawnAtLocation(EnumCreatureType enumcreaturetype, World world, int i, int j,
			int k) {

		final BlockPos pos = new BlockPos(i, j, k);
		
		if (getCreatureMaterial(enumcreaturetype) == Material.water) {
			final Block block = world.getBlockState(pos).getBlock();
			// final Block blockAbove = world.getBlock(i, j + 1, k);

			return block.getMaterial().isLiquid() && !block.isNormalCube();
		} else {
			final Block block = world.getBlockState(pos).getBlock();
			final Block blockAbove = world.getBlockState(pos.up()).getBlock();
			final Block blockBelow = world.getBlockState(pos.down()).getBlock();

			return blockBelow.isNormalCube() && !block.isNormalCube() && !block.getMaterial().isLiquid()
					&& !blockAbove.isNormalCube();
		}
	}

	// New DesPawner stuff
	protected final int entityDespawnCheck(World worldObj, EntityLiving entityliving) {
		if (entityliving instanceof EntityWolf && ((EntityWolf) entityliving).isTamed()) {
			return 0;
		}

		if (entityliving instanceof EntityFairy && !((EntityFairy) entityliving).canDespawn()) {
			return 0; // This is one of the things I changed.
		}

		EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(entityliving, -1D);

		if (entityplayer != null) // entityliving.canDespawn() &&
		{
			double d = ((Entity) (entityplayer)).posX - entityliving.posX;
			double d1 = ((Entity) (entityplayer)).posY - entityliving.posY;
			double d2 = ((Entity) (entityplayer)).posZ - entityliving.posZ;
			double d3 = d * d + d1 * d1 + d2 * d2;

			if (d3 > 16384D) {
				entityliving.setDead();
				return 1;
			}

			if (entityliving.ticksExisted > 600 && worldObj.rand.nextInt(800) == 0) {
				if (d3 < 1024D) {
					entityliving.ticksExisted = 0;
				} else {
					entityliving.setDead();
					return 1;
				}
			}
		}

		return 0;
	}

	public final int countEntities(Class<?> class1, World worldObj) {
		int i = 0;

		for (int j = 0; j < worldObj.loadedEntityList.size(); j++) {
			Entity entity = (Entity) worldObj.loadedEntityList.get(j);

			if (entity instanceof EntityFairy) {
				EntityFairy fairy = (EntityFairy) entity;

				if (!fairy.isDead && fairy.getHealth() > 0 && fairy.tamed()) {
					continue; // I also added this: so tamed fairies don't
								// prevent more from spawning.
				}
			}

			if (class1.isAssignableFrom(entity.getClass())) {
				i++;
			}
		}

		return i;
	}

	public final int despawnVanillaAnimals(World worldObj) {
		int count = 0;

		for (int j = 0; j < worldObj.loadedEntityList.size(); j++) {
			Entity entity = (Entity) worldObj.loadedEntityList.get(j);

			if ((entity instanceof EntityLiving) && (entity instanceof EntityCow || entity instanceof EntitySheep
					|| entity instanceof EntityPig || entity instanceof EntityChicken || entity instanceof EntitySquid
					|| entity instanceof EntityWolf)) {
				count += entityDespawnCheck(worldObj, (EntityLiving) entity);
			}
		}

		return count;
	}

	@SuppressWarnings("rawtypes")
	public final int despawnMob(World worldObj) {
		List<Class> myNullList = null;
		return despawnMob(worldObj, myNullList);
	}

	@SuppressWarnings("rawtypes")
	public final int despawnMob(World worldObj, Class... classList) {
		List<Class> myList = new ArrayList<Class>();

		for (int i = 0; i < classList.length; i++) {
			myList.add(classList[i]);
		}

		return despawnMob(worldObj, myList);
	}

	@SuppressWarnings("rawtypes")
	public final int despawnMob(World worldObj, List<Class> classList) {
		int count = 0;

		if (classList == null) {
			classList = vanillaClassList;
		}

		for (int j = 0; j < worldObj.loadedEntityList.size(); j++) {
			Entity entity = (Entity) worldObj.loadedEntityList.get(j);

			if (!(entity instanceof EntityLiving)) {
				continue;
			}

			for (Iterator<Class> iterator = classList.iterator(); iterator.hasNext();) {
				if (iterator != null) {
					Class<?> class2 = (Class<?>) iterator.next();

					if (class2 == entity.getClass()) {
						count += entityDespawnCheck(worldObj, (EntityLiving) entity);
					}
				}
			}
		}

		return count;
	}

	public final int despawnMobWithMinimum(World worldObj, Class<?> class1, int minimum) {
		int killedcount = 0;
		int mobcount = countEntities(class1, worldObj);

		for (int j = 0; j < worldObj.loadedEntityList.size(); j++) {
			if ((mobcount - killedcount) <= minimum) {
				worldObj.updateEntities();
				return killedcount;
			}

			Entity entity = (Entity) worldObj.loadedEntityList.get(j);

			if (!(entity instanceof EntityLiving)) {
				continue;
			}

			if (class1 == entity.getClass()) {
				killedcount += entityDespawnCheck(worldObj, (EntityLiving) entity);
			}
		}

		worldObj.updateEntities();
		return killedcount;
	}
}
