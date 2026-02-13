package net.aurea.testmod.block.entity;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;


public class SoulCrucibleBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

    private static final int[] Input_Slots={0, 1, 2, 3};
    //private static final int[] Input_Slots_number=4;

    private static final int[] Output_Slots ={4,5};
    //private static final int[] Output_Slot_number =2;


    /*protected final PropertyDelegate propertyDelegate;

    private int progress = 0;
    private int maxProgress = 4; */

    public SoulCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOUL_CRUCIBLE_BLOCK_ENTITY, pos, state);
    /*    this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SoulCrucibleBlockEntity.this.progress;
                    case 1 -> SoulCrucibleBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SoulCrucibleBlockEntity.this.progress = value;
                    case 1 -> SoulCrucibleBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        }; */
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        //nbt.putInt("soul_crucible.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        //progress=nbt.getInt("soul_crucible.progress");
    }

    /*
    private void resetProgress() {
        this.progress=0;
    }

    private boolean hasRecipe(){
        ItemStack result = new ItemStack(ModItems.BloodIronIngot);
        boolean hasInput = getStack()
    }
     */

    private boolean areOutputSlotsEmptyOrReceivable(){
        boolean allAvailable = true;
        for(int i=0; i<2; i++){
            allAvailable = allAvailable && (this.getStack(Output_Slots[i]).isEmpty() || (this.getStack(Output_Slots[i]).getCount() < this.getStack(Output_Slots[i]).getMaxCount()));
            if(!allAvailable){
                break;
            }
        }
        return allAvailable;
    }


    private static boolean areInputSpotsAvailable(Inventory insertInventory){
        boolean allAvailable = true;
        for(int i=0; i<4; i++){
            allAvailable = allAvailable && (insertInventory.getStack(Input_Slots[i]).isEmpty() || (insertInventory.getStack(Input_Slots[i]).getCount() < insertInventory.getStack(Input_Slots[i]).getMaxCount()));
            if(!allAvailable){
                break;
            }
        }
        return allAvailable;
    }

    //This function returns the first available slot for the item in question
        //If it returns -1, it found no slot
    private static int findAvailableInputSlot(ItemStack item, Inventory insertInventory){

        ItemStack Slot = null;

        for(int i=0; i<4; i++){
            Slot=insertInventory.getStack(Input_Slots[i]);

            if(Slot.isEmpty() ||
              ((Slot.getItem() == item.getItem()) &&
              (Slot.getCount() != Slot.getMaxCount()))){
                return i;
            }
        }
        //Failed all tests
        return -1;
    }


    private static void insertInput(World world, ItemStack item, Inventory insertInventory){
        if (!world.isClient()){
            if(areInputSpotsAvailable(insertInventory)){

                int slotNumber=findAvailableInputSlot(item, insertInventory);
                if(slotNumber>=0){
                // Found a slot to be able to input

                    // Extra variables to nmake things clearer
                    ItemStack Slot=insertInventory.getStack(Input_Slots[slotNumber]);
                    ItemStack inputItem = item.copy(); // Extra variable to not mess around with the actual item entity just in case.


                    if(Slot.isEmpty()){
                    // The chosen slot is empty
                        insertInventory.setStack(slotNumber, inputItem.copy());

                    } else if(Slot.getMaxCount() - item.getCount() > 0){
                    //The slot has a bunch of items, although not all that many

                        //Add item count to slot
                        inputItem.setCount(item.getCount() + Slot.getCount());
                        insertInventory.setStack(slotNumber, inputItem);

                        //Delete item
                        item.decrement(item.getCount());

                    } else {
                    //The item doesn't fit entirely, so only a little enters

                        //Get the excess and the amount that does fit
                        int amountToFill = Slot.getMaxCount() - item.getCount();
                        int excess = item.getCount() - amountToFill;

                        //Distribute between amount inputted and excess
                        inputItem.setCount(64);
                        item.setCount(excess);

                        //Insert amount
                        insertInventory.setStack(slotNumber, inputItem);

                    }

                    insertInventory.markDirty();

                } // If it escapes the if condition is because no slots are available, or can fit this particular item
            }
        }

    }


    // Notes: State is there just in case we want to add redstone blocking inserting items

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, SoulCrucibleBlockEntity blockEntity) {
        if(entity instanceof ItemEntity){
            //If we are working with an item

            // We get the area where the item entity would be inserted from
            VoxelShape INSIDE_SHAPE = Block.createCuboidShape((double)2.0F, (double)5.0F, (double)2.0F, (double)14.0F, (double)16.0F, (double)14.0F);
            VoxelShape ABOVE_SHAPE = Block.createCuboidShape((double)0.0F, (double)16.0F, (double)0.0F, (double)16.0F, (double)24.0F, (double)16.0F);
            VoxelShape INPUT_AREA_SHAPE = VoxelShapes.union(INSIDE_SHAPE, ABOVE_SHAPE);

            //We get the item entities hitbox
            VoxelShape itemHitbox = VoxelShapes.cuboid(entity.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ())));

            //Then we compare that input area with the hitbox of the item
            if(VoxelShapes.matchesAnywhere(itemHitbox, INPUT_AREA_SHAPE, BooleanBiFunction.AND)){
                //If the hitbox is somewhat inside the two areas

                //Insert the item into the hopper
                ItemStack item = ((ItemEntity) entity).getStack();
                insertInput(world, item, (Inventory)blockEntity);

                //Just in case, if the item count=0, we discard (kill) the item entity
                if(item.isEmpty() || item.getCount()==0 ){ entity.discard(); }
            }

        }

    }




}
