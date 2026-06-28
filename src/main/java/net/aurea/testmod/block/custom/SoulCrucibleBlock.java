package net.aurea.testmod.block.custom;

import net.aurea.testmod.block.entity.SoulCrucibleBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
//import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;



public class SoulCrucibleBlock extends BlockWithEntity implements BlockEntityProvider {
    //Voxel Shape
    private static final VoxelShape RAYCAST_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
            VoxelShapes.fullCube(),
            VoxelShapes.union(
                    createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                    createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                    createCuboidShape(4.0, 0.0, 4.0, 12.0, 3.0, 12.0),
                    RAYCAST_SHAPE
            ),
            BooleanBiFunction.ONLY_FIRST
    );

    public SoulCrucibleBlock(Settings settings){
        super(settings);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){
        return OUTLINE_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }



    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new SoulCrucibleBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SoulCrucibleBlockEntity) {
                ItemScatterer.spawn(world, pos, (SoulCrucibleBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    // Using items on this.... PAIN probs
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SoulCrucibleBlockEntity soulCrucibleBlockEntity) {
            ItemStack itemStack = player.getStackInHand(hand);

            // Actions when not sneaking
            if (!player.isSneaking()) {

                // Input held stack (as much as fits)
                if (!world.isClient
                        && SoulCrucibleBlockEntity.insertInput(world, player.getAbilities().creativeMode ? itemStack.copy() : itemStack, (Inventory)soulCrucibleBlockEntity)) {
                    // player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE); // For when we do statistics
                    return ActionResult.SUCCESS;
                }


            // Actions when not sneaking
            } else {
                if(!world.isClient) {
                    // Sneaking empty-handed removes the top stack
                    if (itemStack.isEmpty()) {
                        if(SoulCrucibleBlockEntity.outputItem(world, soulCrucibleBlockEntity, player)){
                            // .output item is what outputs the item. If it succeeds, it returns "true", which is why its possible to put this if statement like it is
                            return ActionResult.SUCCESS;
                        }
                    }
                    // Sneaking with item already present in hand, or while there are empty slots, inputs just 1 from the stack
                    // THIS ACTUALLY CAN'T BE IMPLEMENTED, Shift click with items ignores the block interaction, hence this code can never be reached
                }

            }

            return ActionResult.CONSUME;

        }
        return ActionResult.PASS;
    }


    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SoulCrucibleBlockEntity) {
            SoulCrucibleBlockEntity.onEntityCollided(world, pos, state, entity, (SoulCrucibleBlockEntity)blockEntity);
        }

    }




}
