package fairies.ai;

import java.util.ArrayList;
import java.util.List;

import fairies.entity.EntityFairy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class FairyJob {
	// Obfuscated name lookups
	private static final String[] MCP_FLEEINGTICK = { "fleeingTick", "field_70788_c" };
	private static final String[] MCP_BREEDING = { "breeding", "field_70882_e" };
	private static final String[] MCP_INLOVE = { "inLove", "field_70881_d" };
	
	public FairyJob( final EntityFairy entityfairy ) {
		fairy = entityfairy;
	}

	public void discover( final World world ) {
		if ( fairy.getEntityToAttack() != null || fairy.getEntityFear() != null || fairy.getCryTime() > 0
				|| fairy.attackTime > 0 || fairy.getHealth() <= 0 ) {
			return;
		}

		final int x = MathHelper.floor_double( fairy.posX );
		int y = MathHelper.floor_double( fairy.getEntityBoundingBox().minY );

		if ( fairy.flymode() ) {
			y--;
		}

		final int z = MathHelper.floor_double( fairy.posZ );

		if ( y < 0 || y >= world.getHeight() ) {
			return;
		}

		goodies = getGoodies( world );
		getNearbyChest2( x, y, z, world );
	}

	public void sittingFishing( final World world ) {
		if ( fairy.getEntityToAttack() != null || fairy.getEntityFear() != null || fairy.getCryTime() > 0
				|| fairy.attackTime > 0 || fairy.getHealth() <= 0 ) {
			return;
		}

		final int x = MathHelper.floor_double( fairy.posX );
		int y = MathHelper.floor_double( fairy.getEntityBoundingBox().minY );

		if ( fairy.flymode() ) {
			y--;
		}

		final int z = MathHelper.floor_double( fairy.posZ );

		if ( y < 0 || y >= world.getHeight() ) {
			return;
		}

		getNearbyChest3( x, y, z, world );
	}

	public ArrayList<EntityItem> getGoodies( final World world ) {
		final List<?> list = world.getEntitiesWithinAABB( EntityItem.class, fairy.getEntityBoundingBox().expand( 2.5D, 2.5D, 2.5D ) );
		final ArrayList<EntityItem> list2 = new ArrayList<EntityItem>();

		for ( int i = 0; i < list.size(); i++ ) {
			final EntityItem entity1 = (EntityItem) list.get( i );

			final ItemStack stack = entity1.getEntityItem();
			if ( stack != null && !entity1.cannotPickup() ) {
				if ( stack.stackSize > 0 && goodItem( stack.getItem(), stack.getItemDamage() ) ) {
					list2.add( entity1 );
				}
			}
		}

		if ( list2.size() <= 0 ) {
			return null;
		} else {
			return list2;
		}
	}

	public ArrayList<EntityAnimal> getAnimals( final World world ) {
		final List<?> list = world.getEntitiesWithinAABB( EntityAnimal.class, fairy.getEntityBoundingBox().expand( 5D, 5D, 5D ) );

		if ( list.size() < 2 ) {
			return null;
		}

		final ArrayList<EntityAnimal> list2 = new ArrayList<EntityAnimal>();

		for ( int i = 0; i < list.size(); i++ ) {
			final EntityAnimal entity1 = (EntityAnimal) list.get( i );

			// TODO: track combat correctly
			final int fleeingTick = ReflectionHelper.getPrivateValue(EntityCreature.class, entity1, MCP_FLEEINGTICK);
			if ( fairy.peacefulAnimal( entity1 ) && fairy.canEntityBeSeen( entity1 ) && entity1.getHealth() > 0
					/*&& entity1.getEntityToAttack() == null*/ && fleeingTick <= 0 && !entity1.isInLove()
					&& entity1.getGrowingAge() == 0 ) {
				for ( int j = 0; j < list.size(); j++ ) {
					final EntityAnimal entity2 = (EntityAnimal) list.get( j );

					if ( entity1 != entity2 && entity1.getClass() == entity2.getClass()
							&& entity2.getGrowingAge() == 0 ) {
						list2.add( entity1 );
					}
				}
			}
		}

		if ( list2.size() <= 0 ) {
			return null;
		} else {
			return list2;
		}
	}

	public ArrayList<EntitySheep> getSheep( final World world ) {
		final List<?> list = world.getEntitiesWithinAABB( EntitySheep.class, fairy.getEntityBoundingBox().expand( 5D, 5D, 5D ) );

		if ( list.size() < 1 ) {
			return null;
		}

		final ArrayList<EntitySheep> list2 = new ArrayList<EntitySheep>();

		for ( int i = 0; i < list.size(); i++ ) {
			final EntitySheep entity1 = (EntitySheep) list.get( i );

			// TODO: track combat correctly
			final int fleeingTick = ReflectionHelper.getPrivateValue(EntityCreature.class, entity1, MCP_FLEEINGTICK);
			if ( fairy.canEntityBeSeen( entity1 ) && entity1.getHealth() > 0 /*&& entity1.getEntityToAttack() == null*/
					&& fleeingTick <= 0 && entity1.getGrowingAge() >= 0 && !entity1.getSheared() ) {
				list2.add( entity1 );
			}
		}

		if ( list2.size() <= 0 ) {
			return null;
		} else {
			return list2;
		}
	}

	private static final int radius = 5;

	private void getNearbyChest2( final int x, final int y, final int z, final World world ) {
		int i, j, k;

		for ( int a = -radius; a <= radius; a++ ) {
			for ( int b = -2; b <= 2; b++ ) {
				for ( int c = -radius; c <= radius; c++ ) {
					i = x + a;
					j = y + b;
					k = z + c;
					
					final BlockPos pos = new BlockPos(i, j, k);

					if ( world.getBlockState(pos).getBlock() == Blocks.chest ) {
						final TileEntity tent = world.getTileEntity(pos);

						if ( tent != null && tent instanceof TileEntityChest ) {
							final TileEntityChest chest = (TileEntityChest) tent;

							if ( goodies != null && collectGoodies( chest, world ) ) {
								// fairy.postedCount = 2;
								return;
							}

							for ( int p = 0; p < chest.getSizeInventory(); p++ ) {
								if ( checkChestItem( chest, p, x, y, z, world ) ) {
									cleanSlot( chest, p );
									// fairy.postedCount = 2;
									return;
								}
							}

							if ( miscActions( chest, x, y, z, world ) ) {
								// fairy.postedCount = 2;
								return;
							}
						}
					}
				}
			}
		}
	}

	private void getNearbyChest3( final int x, final int y, final int z, final World world ) {
		int i, j, k;

		for ( int a = -radius; a <= radius; a++ ) {
			for ( int b = -2; b <= 2; b++ ) {
				for ( int c = -radius; c <= radius; c++ ) {
					i = x + a;
					j = y + b;
					k = z + c;
					
					final BlockPos pos = new BlockPos( i, j, k );

					if ( world.getBlockState(pos).getBlock() == Blocks.chest ) {
						final TileEntity tent = world.getTileEntity(pos);

						if ( tent != null && tent instanceof TileEntityChest ) {
							triedBreeding = false;
							triedShearing = false;
							final TileEntityChest chest = (TileEntityChest) tent;

							for ( int p = 0; p < chest.getSizeInventory(); p++ ) {
								final ItemStack stack = chest.getStackInSlot( p );

								if ( stack != null && isFishingItem( stack.getItem() )
										&& onFishingUse( stack, x, y, z, world ) ) {
									cleanSlot( chest, p );
									// fairy.postedCount = 2;
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	// Actions related to specific items.
	private boolean checkChestItem(IInventory chest, int slot, final int x, final int y, final int z, final World world ) {
		ItemStack stack = chest.decrStackSize(slot, 1);
		try {
			if (stack == null || stack.stackSize == 0) {
				return false;
			}

			// Farming
			if (isHoeItem(stack.getItem()) && onHoeUse(stack, x, y - 1, z, world)) {
				return true;
			}

			if (isSeedItem(stack.getItem()) && onSeedUse(stack, x, y, z, world)) {
				return true;
			}

			if (isBonemealItem(stack.getItem(), stack.getItemDamage()) && onBonemealUse(stack, x, y - 1, z, world)) {
				return true;
			}

			// Foresting
			if (isAxeItem(stack.getItem()) && onAxeUse(stack, x, y, z, world)) {
				return true;
			}
			// TODO, I think the seed planting code should take care of this now?
			if ( isSaplingBlock( stack.getItem() ) && onSaplingUse( stack, x, y - 1, z, world ) ) {
				return true;
			}

			// Breeding
			if (!triedBreeding && onBreedingUse(stack, world)) {
				return true;
			}

			// Breeding
			if (!triedShearing && isShearingItem(stack.getItem()) && onShearingUse(stack, world)) {
				return true;
			}

			// Fishing
			if (isFishingItem(stack.getItem()) && onFishingUse(stack, x, y, z, world)) {
				return true;
			}

			// Snack
			if (fairy.acceptableFoods(stack.getItem()) && snackTime(stack)) {
				return true;
			}

			return false;
		} finally {
			// return the remainder to the chest.
			if (stack != null && stack.stackSize > 0) {
				ItemStack chestStack = chest.getStackInSlot(slot);
				if (chestStack == null) {
					chest.setInventorySlotContents(slot, stack);
				}
				else {
					assert stack.getItem() == chestStack.getItem(); // avoid duplication glitch?
					assert stack.stackSize + chestStack.stackSize < chestStack.getMaxStackSize();
					chestStack.stackSize += stack.stackSize;
				}
			}
		}
	}

	// Actions that only require a chest.
	private boolean miscActions( final TileEntityChest chest, final int x, final int y, final int z,
			final World world ) {
		if ( cutTallGrass( x, y, z, world ) ) {
			return true;
		}

		if ( doHaveAxe && trimExcessLeaves( x, y, z, world ) ) {
			return true;
		}

		return false;
	}

	// Remove an itemstack that's been used up.
	private void cleanSlot( final TileEntityChest chest, final int p ) {
		if ( chest.getStackInSlot( p ) != null && chest.getStackInSlot( p ).getItem() == null ) {
			chest.setInventorySlotContents( p, (ItemStack) null );
		}
	}

	// What to do with a hoe
	private boolean onHoeUse( final ItemStack stack, int x, final int y, int z, final World world ) {
		for ( int a = 0; a < 3; a++ ) {
			final BlockPos pos = new BlockPos( x, y, z );
			final Block i = world.getBlockState(pos).getBlock();

			if ( world.isAirBlock(pos.up()) && (i == Blocks.grass || i == Blocks.dirt) ) {
				final Block block = Blocks.farmland;
				world.playSoundEffect( x + 0.5D, y + 0.5D, z + 0.5D, block.stepSound.getBreakSound(),
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F );
				world.setBlockState( pos, block.getDefaultState() );
				fairy.armSwing( !fairy.didSwing );
				fairy.setTempItem( stack.getItem() );
				stack.damageItem( 1, fairy );

				fairy.attackTime = 30;

				if ( fairy.flymode() && fairy.getFlyTime() > 0 ) {
					fairy.setFlyTime( 0 );
				}

				return true;
			}

			x += fairy.getRNG().nextInt( 3 ) - 1;
			z += fairy.getRNG().nextInt( 3 ) - 1;
		}

		return false;
	}

	// What to do with seeds
	private boolean onSeedUse( final ItemStack stack, int x, final int y, int z, final World world ) {

		/**
		 * This can be a bit messy, so will actually defer cleanup until after release.
		 */
		IPlantable plantable;
		if (stack.getItem() instanceof IPlantable) {
			plantable = (IPlantable) stack.getItem();
		} else if (stack.getItem() == Items.reeds) {
			plantable = (BlockReed) Blocks.reeds;
		} else {
			throw new NullPointerException("stack doesn't look plantable to me.");
		}
		BlockPos pos = new BlockPos(x,y,z);
		final Block block = plantable.getPlant(world, pos).getBlock();

		for ( int a = 0; a < 3; a++ ) {
			if ( block.canPlaceBlockAt(world, pos) ) {

				world.playSoundEffect( x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.getBreakSound(),
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F );
				world.setBlockState( pos, block.getDefaultState() );
				stack.stackSize--;
				
				fairy.armSwing( !fairy.didSwing );
				fairy.setTempItem( stack.getItem() );
				fairy.attackTime = 30;

				if ( fairy.flymode() && fairy.getFlyTime() > 0 ) {
					fairy.setFlyTime( 0 );
				}

				return true;
			}

			x += fairy.getRNG().nextInt( 3 ) - 1;
			z += fairy.getRNG().nextInt( 3 ) - 1;
			pos = new BlockPos(x,y,z);
		}

		return false;
	}

	// Use bonemeal to speed up wheat growth
	private boolean onBonemealUse( final ItemStack stack, int x, final int y, int z, final World world ) {
		for ( int a = 0; a < 3; a++ ) {
			final BlockPos pos = new BlockPos(x,y+1,z);
			final IBlockState state = world.getBlockState(pos);
			final Block block = state.getBlock();

			if ( block instanceof IGrowable && state.getValue(BlockCrops.AGE) < 7 ) {
				world.playSoundEffect( x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.getBreakSound(),
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F );

				ItemDye.applyBonemeal(stack, world, pos, null);

				fairy.armSwing( !fairy.didSwing );
				fairy.setTempItem( stack.getItem() );
				fairy.attackTime = 30;

				if ( fairy.flymode() && fairy.getFlyTime() > 0 ) {
					fairy.setFlyTime( 0 );
				}

				return true;
			}

			x += fairy.getRNG().nextInt( 3 ) - 1;
			z += fairy.getRNG().nextInt( 3 ) - 1;
		}

		return false;
	}

	// What to do with an axe
	private boolean onAxeUse( final ItemStack stack, int x, final int y, int z, final World world ) {
		final int m = x;
		final int n = z;

		// TODO: handle additional tree types
		// TODO: clean up treecapitation logic, integrate additionalAxeUse

		for ( int a = 0; a < 9; a++ ) {
			x = m + ((a / 3) % 9) - 1;
			z = n + (a % 3) - 1;
			if( chopLog( world, x, y, z) ) {
				fairy.armSwing( !fairy.didSwing );
				fairy.setTempItem( stack.getItem() );
				stack.damageItem( 1, fairy );

				if ( stack.stackSize > 0 ) {
					additionalAxeUse( stack, x, y + 1, z, world, maxTreeHeight );
				}

				fairy.attackTime = 30;

				if ( !fairy.flymode() && fairy.getFlyTime() > 0 ) {
					fairy.setFlyTime( 0 );
				}

				return true;
			}
		}

		return false;
	}
	
	private boolean chopLog( final World world, final int x, final int y, final int z ) {
		final BlockPos pos = new BlockPos(x,y,z);		
		final IBlockState state = world.getBlockState(pos);
		final Block block = state.getBlock();

		if ( block == Blocks.log || block == Blocks.log2 ) {
			world.playSoundEffect( x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.getBreakSound(),
					(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F );
			block.dropBlockAsItemWithChance( world, pos, state, 1.0F, 0 );
			world.setBlockToAir( pos );
			return true;
		} else {
			return false;
		}
	}

	@Deprecated
	private void additionalAxeUse( final ItemStack stack, int x, final int y, int z, final World world, int recurse ) {
		if ( recurse > maxTreeHeight ) {
			recurse = maxTreeHeight;
		}

		final int m = x;
		final int n = z;

		for ( int a = 0; a < 9; a++ ) {
			x = m + ((a / 3) % 9) - 1;
			z = n + (a % 3) - 1;
			
			if( chopLog( world, x, y, z ) ) {
				stack.damageItem( 1, fairy );

				if ( stack.stackSize > 0 && recurse > 0 ) {
					if ( a != 4 ) {
						additionalAxeUse( stack, x, y, z, world, recurse - 1 );
					}

					if ( stack.stackSize > 0 && recurse > 0 ) {
						additionalAxeUse( stack, x, y + 1, z, world, recurse - 1 );
					}
				}
			}
		}
	}

	// What to do with saplings
	private boolean onSaplingUse( final ItemStack stack, final int x, final int y, final int z, final World world ) {
		// TODO: use a sapling correctly :)
		return true;
	}

	// Attempt to breed animals
	private boolean onBreedingUse( final ItemStack stack, final World world ) {
		final ArrayList<EntityAnimal> animals = getAnimals( world );

		if ( animals == null ) {
			return false;
		}

		int count = 0;


		for ( int i = 0; i < animals.size() && count < 3 && stack.stackSize > 0; i++ ) {
			final EntityAnimal entity = (EntityAnimal) animals.get( i );

			int isBreedingCounter = ReflectionHelper.getPrivateValue(EntityAnimal.class, entity, MCP_BREEDING);
			// skip unbreedable animals
			if (!entity.isBreedingItem(stack) // can't breed with this item
					|| entity.getGrowingAge() != 0 // is juvenile (negative) or recently proceated (positive)
					|| isBreedingCounter != 0 // literally breeding now.
					) {
				continue;}
			triedBreeding = true;

			if ( fairy.getDistanceToEntity( entity ) < 3F ) {
				ReflectionHelper.setPrivateValue(EntityAnimal.class, entity, 600, MCP_INLOVE);			
				count++;
				stack.stackSize--;
			}
		}

		if ( count > 0 ) {
			fairy.armSwing( !fairy.didSwing );
			fairy.setTempItem( stack.getItem() );

			fairy.attackTime = 1;
			fairy.setHearts( !fairy.didHearts );

			if ( fairy.flymode() && fairy.getFlyTime() > 0 ) {
				fairy.setFlyTime( 0 );
			}

			return true;
		}

		return false;
	}

	private boolean onShearingUse( final ItemStack stack, final World world ) {
		final ArrayList<EntitySheep> sheep = getSheep( world );
		triedShearing = true;

		if ( sheep == null ) {
			return false;
		}
		for (Object one_sheep_raw : sheep) {
			EntitySheep one_sheep = (EntitySheep) one_sheep_raw;
			if (!one_sheep.getSheared()) {
				fairy.armSwing( !fairy.didSwing );
				fairy.setTempItem( stack.getItem() );
				stack.damageItem( 1, fairy );
				fairy.attackTime = 30;

				one_sheep.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, one_sheep.getFleeceColor().getMetadata()), 0.0F);
				one_sheep.setSheared(true);
				break; // shear one at a time... looks better.
			}
		}

		return false;
	}

	private static final float pia = -(float) Math.PI / 180F;

	private boolean onFishingUse( final ItemStack stack, final int x, final int y, final int z, final World world ) {
		if ( fairy.isInWater() && !fairy.hasPath() ) {
			getToLand( x, y, z, world );
			return false;
		} else if ( fairy.flymode() && fairy.getFlyTime() > 0 && !fairy.hasPath() ) {
			fairy.setFlyTime( 0 );
			return false;
		} else if ( !fairy.onGround || fairy.isInWater() ) {
			return false;
		}

		final float angle = fairy.rotationYaw - 30F + (fairy.getRNG().nextFloat() * 60F);
		final double posX = fairy.posX + Math.sin( angle * pia ) * 6D;
		final double posY = fairy.posY;
		final double posZ = fairy.posZ + Math.cos( angle * pia ) * 6D;
		final int a = MathHelper.floor_double( posX );
		final int b = y;
		final int c = MathHelper.floor_double( posZ );
		final BlockPos pos = new BlockPos(a,b,c); 

		for ( int j = -4; j < 0; j++ ) {
			if ( b + j > 0 && b + j < world.getHeight() - 10 ) {
				boolean flag = false;

				for ( int i = -1; i <= 1 && !flag; i++ ) {
					for ( int k = -1; k <= 1 && !flag; k++ ) {
						final BlockPos pos2 = pos.add(i, j, k);
						if ( world.getBlockState( pos2 ).getBlock() != Blocks.water
								|| world.getBlockState( pos2.up() ) != Blocks.air
								|| world.getBlockState( pos2.up(2) ) != Blocks.air
								|| world.getBlockState( pos2.up(3) ) != Blocks.air ) {
							flag = true;
						}
					}
				}

				if ( !flag ) {
					// final PathEntity doug = world.getEntityPathToXYZ( fairy, a, b + j, c, 16F, false, false, true, true );
					final PathEntity path = fairy.getNavigator().getPathToXYZ(a, b+j, c);

					if ( path != null && canSeeToSpot( posX, posY, posZ, world ) ) {
						fairy.rotationYaw = angle;
						fairy.castRod();
						// TODO: player fishing normally damages the rod when the reels something in; should we do that?
						stack.damageItem( 1, fairy );

						return true;
					}
				}
			}
		}

		return false;
	}

	private void getToLand( final int x, final int y, final int z, final World world ) {
		for ( int q = 0; q < 16; q++ ) {
			final int i = x - 5 + fairy.getRNG().nextInt( 11 );
			final int j = y + 1 + fairy.getRNG().nextInt( 5 );
			final int k = z - 5 + fairy.getRNG().nextInt( 11 );

			if ( y > 1 && y < world.getHeight() - 1 ) {
				if ( fairy.isAirySpace( i, j, k ) && !fairy.isAirySpace( i, j - 1, k )
						&& world.getBlockState( new BlockPos(i, j - 1, k) ).getBlock().isBlockNormalCube() ) {
					// final PathEntity doug = world.getEntityPathToXYZ( fairy, i, j, k, 16F, false, false, true, true );
					final PathEntity path = fairy.getNavigator().getPathToXYZ(i, j, k);

					if ( path != null ) {
						fairy.getNavigator().setPath(path, 1.0);

						if ( !fairy.flymode() ) {
							fairy.setFlyTime( 0 );
						}

						return;
					}
				}
			}
		}
	}

	private boolean canSeeToSpot( final double posX, final double posY, final double posZ, final World world ) {
		return world.rayTraceBlocks(
				new Vec3( fairy.posX, fairy.posY + fairy.getEyeHeight(), fairy.posZ ),
				new Vec3( posX, posY, posZ ) ) == null;
	}

	// Check if it's a good place to put a sapling down
	@SuppressWarnings("unused")
	@Deprecated
	private int goodPlaceForTrees( final int x, final int y, final int z, final World world ) {
		final BlockPos pos = new BlockPos(x,y,z);
		final Block i = world.getBlockState(pos).getBlock();

		if ( i == Blocks.sapling ) {
			return 2;
		}

		final BlockPos above = pos.up();
		final Block j = world.getBlockState(above).getBlock();

		if ( j == Blocks.sapling ) {
			return 2;
		}
		if ( j == Blocks.air && world.canBlockSeeSky(above) ) {
			return 0;
		}

		return 1;
	}

	// Trim tall grass to look for seeds.
	/* TODO: I think we can do somewhat better than this, something like Block::harvestBlock for mobs?
	 * check inventory for best tool?  can earn harvest?  Can mine ores?
	 */
	private boolean cutTallGrass( int x, final int y, int z, final World world ) {
		final int m = x;
		final int n = z;

		for ( int a = 0; a < 9; a++ ) {
			x = m + (a / 3) - 1;
			z = n + (a % 3) - 1;
			final BlockPos pos = new BlockPos(x,y,z);
			final IBlockState state = world.getBlockState(pos);
			final Block above = world.getBlockState(pos.up()).getBlock();
			final Block below = world.getBlockState(pos.down()).getBlock();

			if ( breakablePlant( state, above, below ) ) {
				final Block block = state.getBlock();
				world.playSoundEffect( x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.getBreakSound(),
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F );
				block.dropBlockAsItemWithChance( world, pos, state, 1.0F, 0 );
				world.setBlockToAir(pos);
				fairy.armSwing( !fairy.didSwing );
				fairy.attackTime = 30;

				if ( fairy.flymode() && fairy.getFlyTime() > 0 ) {
					fairy.setFlyTime( 0 );
				}

				return true;
			}
		}

		return false;
	}

	// Pick apart trees
	private boolean trimExcessLeaves( int x, int y, int z, final World world ) {
		for ( int d = 0; d < 3; d++ ) {
			final int a = fairy.getRNG().nextInt( 3 );
			final int b = (fairy.getRNG().nextInt( 2 ) * 2) - 1;

			if ( a == 0 ) {
				x += b;
			} else if ( a == 1 ) {
				y += b;
			} else {
				z += b;
			}

			final BlockPos pos = new BlockPos(x,y,z);
			final IBlockState state = world.getBlockState(pos);

			final Block block = state.getBlock();
			if ( block == Blocks.leaves ) {
				world.playSoundEffect( x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.getBreakSound(),
						(block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getFrequency() * 0.8F );
				block.dropBlockAsItemWithChance( world, pos, state, 1.0F, 0 );
				world.setBlockToAir(pos);
				fairy.armSwing( !fairy.didSwing );
				fairy.attackTime = 20;
				return true;
			}
		}

		return false;
	}

	// Pick up useful objects off of the ground
	private boolean collectGoodies( final TileEntityChest chest, final World world ) {
		int count = 0;

		for ( int i = 0; i < goodies.size() && count < 3; i++ ) {
			final EntityItem entity = (EntityItem) goodies.get( i );
			final ItemStack stack = entity.getEntityItem();
			final int emptySpace = getEmptySpace( chest, stack );

			if ( emptySpace >= 0 ) {
				chest.setInventorySlotContents( emptySpace, stack );
				entity.setDead();
				count++;
			}
		}

		if ( count > 0 ) {
			world.playSoundAtEntity( fairy, "random.pop", 0.4F,
					((fairy.getRNG().nextFloat() - fairy.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F );
			fairy.armSwing( !fairy.didSwing );
			fairy.attackTime = 1;
			// if(fairy.flymode() && fairy.flyTime > 0) {
			// fairy.flyTime = 0;
			// }
			return true;
		}

		return false;
	}

	// Look for a spot to put collected items
	private int getEmptySpace( final TileEntityChest chest, final ItemStack stack ) {
		int temp = -1;

		for ( int i = 0; i < chest.getSizeInventory(); i++ ) {
			final ItemStack stack2 = chest.getStackInSlot( i );

			if ( temp < 0 && (stack2 == null || stack2.stackSize == 0) ) {
				temp = i;
			} else if ( stack2 != null && stack.getItem() == stack2.getItem() && stack2.stackSize > 0
					&& stack2.stackSize + stack.stackSize <= stack.getMaxStackSize() && !stack2.isItemDamaged()
					&& !stack.isItemDamaged() && stack.getItemDamage() == stack2.getItemDamage() ) {
				stack.stackSize += stack2.stackSize;
				return i;
			}
		}

		return temp;
	}

	// Fairy can heal itself if damaged.
	private boolean snackTime( final ItemStack stack ) {
		if ( fairy.getHealth() < fairy.getMaxHealth() ) {
			stack.stackSize--;

			fairy.setHearts( !fairy.hearts() );

			if ( stack.getItem() == Items.sugar ) {
				fairy.heal( 5 );
			} else {
				fairy.heal( 99 );

				if ( stack.getItem() == Items.speckled_melon ) {
					fairy.setWithered( false );
					fairy.witherTime = 0;
				}
			}

			fairy.armSwing( !fairy.didSwing );
			fairy.attackTime = 1;
			return true;
		}

		return false;
	}

	// Is the item a hoe?
	private boolean isHoeItem( final Item item ) {
		return item == Items.wooden_hoe || item == Items.stone_hoe || item == Items.iron_hoe
				|| item == Items.diamond_hoe || item == Items.golden_hoe;
	}

	// Is the item an axe?
	private boolean isAxeItem( final Item item ) {
		if ( item == Items.wooden_axe || item == Items.stone_axe || item == Items.iron_axe || item == Items.diamond_axe
				|| item == Items.golden_axe ) {
			doHaveAxe = true;
			return true;
		}

		return false;
	}

	// Is it a plant that should be broken
	private boolean breakablePlant( final IBlockState state, final Block above, final Block below ) {
		// we're gonna treat this as everything block that should be punched.
		// cocoa?... hrmmm
		// mushrooms: tricky, when there are at least 4 other mushrooms of same type in 9x3x9 area.
		// snow?  maybe?  if there's plants?  if there's no shovel?

		// crops: that should be wheat, carrots and potatoes, when MD level is 7.
		final Block block = state.getBlock();
		return (block instanceof BlockCrops && state.getValue(BlockCrops.AGE) == 7)
				// not a crop, a bush apparently...
				|| block == Blocks.nether_wart && state.getValue(BlockCrops.AGE) == 3
				// reeds: when above reeds.
				|| block == Blocks.reeds && below == Blocks.reeds
				// cactus: break only when above sand and below cactus, to prevent losing drops.
				|| block == Blocks.cactus && above == Blocks.cactus && below != Blocks.cactus
				// melons/pumkins... always?
				|| block == Blocks.melon_block || state == Blocks.pumpkin
				// tallgrass, which drops seeds!
				|| block == Blocks.tallgrass
				// all other doo-dads? ie bushes and tall plants?
				|| block == Blocks.yellow_flower
				|| block == Blocks.red_flower
				|| block == Blocks.snow;
	}

	// TODO: read allowed seeds from config file.
	private boolean isSeedItem( final Item item ) {
		return item instanceof IPlantable
				|| item == Items.reeds;
	}

	private boolean isBonemealItem( final Item item, final int j ) {
		return item == Items.dye && j == 15;
	}

	// Is the item a sapling?
	private boolean isSaplingBlock( final Item item ) {
		return item == Item.getItemFromBlock( Blocks.sapling );
	}

	// Is the item a log block?
	private boolean isLogBlock( final Item item ) {
		return item == Item.getItemFromBlock( Blocks.log ) || item == Item.getItemFromBlock( Blocks.log2 );
	}

	private boolean isShearingItem( final Item item ) {
		return item == Items.shears;
	}

	private boolean isClothBlock( final Item item ) {
		return item == Item.getItemFromBlock( Blocks.wool );
	}

	// A fishing rod, used to fish
	private boolean isFishingItem( final Item item ) {
		return item == Items.fishing_rod;
	}

	// Item gotten from fishing, also used to tame Ocelots
	private boolean isRawFish( final Item item ) {
		return item == Items.fish;
	}

	private boolean isFlower( final Item item ) {
		// NB: Let's just hope that iplantables are sufficient for this for now
		return item instanceof IPlantable;
	}

	// Items worth picking up
	private boolean goodItem( final Item item, final int j ) {
		return isHoeItem( item ) || isSeedItem( item ) || isBonemealItem( item, j ) || isAxeItem( item )
				|| isSaplingBlock( item ) || isLogBlock( item ) || fairy.acceptableFoods( item )
				|| /* isBreedingItem( item ) || */ isShearingItem( item ) || isClothBlock( item ) || isFishingItem( item )
				|| isRawFish( item ) || isFlower( item );
	}

	// Recursion limit for trees.
	private static final int	maxTreeHeight	= 99;

	// Will be referenced a lot. It's the fairy who's doing the job
	private final EntityFairy	fairy;
	// Used to make sure fairies don't just tear apart random trees.
	private boolean				doHaveAxe;
	// Make sure that breeding is only attempted once per chest.
	private boolean				triedBreeding;
	// Make sure that shearing is only attempted once per chest.
	private boolean				triedShearing;
	// A list of items on the ground.
	private ArrayList<EntityItem>			goodies;
}
