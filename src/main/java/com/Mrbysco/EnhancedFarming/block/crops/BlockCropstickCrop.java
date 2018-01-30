package com.Mrbysco.EnhancedFarming.block.crops;

import java.util.Random;

import com.Mrbysco.EnhancedFarming.Reference;
import com.Mrbysco.EnhancedFarming.block.EnumCropType;
import com.Mrbysco.EnhancedFarming.config.FarmingConfigGen;
import com.Mrbysco.EnhancedFarming.init.FarmingBlocks;
import com.Mrbysco.EnhancedFarming.util.CropHelper;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockCropstickCrop extends BlockCrops{
	
	private EnumCropType TYPE;
    public static final PropertyInteger CROP_AGE = PropertyInteger.create("age", 0, 5);
    protected static final AxisAlignedBB CROPSTICK_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.6000000238418579D, 0.699999988079071D);
	
    public BlockCropstickCrop(String unlocalizedName, String registryName, EnumCropType cropType) {
    	super();
        this.setHardness(0.5F);
    	this.setDefaultState(this.blockState.getBaseState().withProperty(CROP_AGE, Integer.valueOf(0)));
        this.setCreativeTab((CreativeTabs)null);
        
        this.TYPE = cropType;
		this.setUnlocalizedName(Reference.MOD_PREFIX + unlocalizedName);
		this.setRegistryName(registryName);
	}

    @Override
    protected Item getSeed()
    {
        return CropHelper.getCropSeed(this.TYPE);
    }
    
    @Override
    protected Item getCrop()
    {
        return CropHelper.getCrop(this.TYPE);
    }
    
    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state) {
    	int i;
        int j = this.getMaxAge();;
        
    	if (FarmingConfigGen.general.othersettings.instantGrow)
    	{
    		i = this.getAge(state) + (j - this.getAge(state));
    	}
    	else
    	{
    		i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
    	}

        if (i > j)
        {
            i = j;
        }

        worldIn.setBlockState(pos, this.withAge(i), 2);
    }
    
    protected PropertyInteger getAgeProperty()
    {
        return CROP_AGE;
    }
    
    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return state.getBlock() == Blocks.FARMLAND;
    }
    
	@Override
	public int getMaxAge() {
		return 5;
	}
    
    @Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		if(!isMaxAge(state)) {
			return getSeed();
		}
		else {
			return getCrop();
		}
	}
    
    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
    		ItemStack stack) {
    	if (!worldIn.isRemote)
    	{
        	worldIn.setBlockState(pos, FarmingBlocks.crop_stick.getDefaultState(), 6);
    	}
    	super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (rand.nextInt(5) == 0)
        {
            this.checkAndDropBlock(worldIn, pos, state);
        }
        else
        {
            super.updateTick(worldIn, pos, state, rand);
        }
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(CROP_AGE, Integer.valueOf(meta));
    }
    
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(CROP_AGE)).intValue();
    }
    
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CROP_AGE});
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CROPSTICK_AABB;
    }
    
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    	return EnumPlantType.Crop;
    }
    
    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    	if(FarmingConfigGen.general.othersettings.bonemealGrow)
    	{
    		return getAge(state) < getMaxAge();
    	}
    	else
    	{
    		return false;
    	}
    }
    
    @Override
    protected int getBonemealAgeIncrease(World worldIn)
    {
        return super.getBonemealAgeIncrease(worldIn) / 2;
    }
    
    @Override
    public void getDrops(NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
    	boolean flag = this.TYPE.CanDropMultiple();
        super.getDrops(drops, world, pos, state, fortune);
        if (this.isMaxAge(state) && RANDOM.nextInt(50) == 0 && flag)
            drops.add(new ItemStack(CropHelper.getCrop(this.TYPE)));
    }
}