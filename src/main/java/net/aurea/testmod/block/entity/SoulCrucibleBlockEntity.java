package net.aurea.testmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;



public class SoulCrucibleBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(7, ItemStack.EMPTY);

    // Slots where input items will be
    private static final int[] Input_Slots={0, 1, 2, 3};
    //private static final int[] Input_Slots_number=4;

    // Slots where the Shovel will be, for when crafting is about to happen
    private static final int Spoon_Slot = 4;

    // Slots where output will be
    private static final int[] Output_Slots ={5, 6};
    //private static final int[] Output_Slot_number =2;

    // All slots, for ease of code later on (like in getRenderItem)
    private static final int[] All_Slots={0 ,1 ,2 ,3 ,4 ,5 ,6};
    //private static final int[] Slot_number =7;

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
        for (int i = 0; i<=6; i++){
            inventory.set(i, ItemStack.EMPTY);
        }

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
            allAvailable = (this.getStack(Output_Slots[i]).isEmpty() || (this.getStack(Output_Slots[i]).getCount() < this.getStack(Output_Slots[i]).getMaxCount()));
            if(!allAvailable){
                break;
            }
        }
        return allAvailable;
    }


    private static boolean areInputSpotsAvailable(Inventory insertInventory){
        boolean allAvailable = false;
        for(int i=0; i<4; i++){
            allAvailable = allAvailable || (insertInventory.getStack(Input_Slots[i]).isEmpty() || (insertInventory.getStack(Input_Slots[i]).getCount() < insertInventory.getStack(Input_Slots[i]).getMaxCount()));
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
               (Slot.getCount() < Slot.getMaxCount()))){
                return i;
            }
        }
        //Failed all tests
        return -1;
    }


    public static boolean insertInput(World world, ItemStack item, Inventory insertInventory){
        boolean success=false;
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
                        //Delete item
                        item.decrement(item.getCount());

                    } else if(Slot.getMaxCount() > (item.getCount() + Slot.getCount()) ){
                    //The slot has a bunch of items, although not all that many
                            //TestMod.LOGGER.info("Filling slot"); //DEBUG
                        //Add item count to slot
                        inputItem.setCount(item.getCount() + Slot.getCount());
                        insertInventory.setStack(slotNumber, inputItem);

                        //Delete item
                        item.decrement(item.getCount());

                    } else {
                    //The item doesn't fit entirely, so only a little enters

                        //Get the excess and the amount that does fit
                        int amountToFill = item.getMaxCount() - Slot.getCount();
                        int excess = item.getCount() - amountToFill;

                        //Distribute between amount inputted and excess
                        inputItem.setCount(item.getMaxCount());
                        item.setCount(excess);

                        //Insert amount
                        insertInventory.setStack(slotNumber, inputItem);

                    }

                    insertInventory.markDirty();
                    success=true;

                } // If it escapes the if condition is because no slots are available, or can fit this particular item
            }
        }
        return success;
    }

    private static int findLastSlotWithItems(Inventory inventory){
        ItemStack Slot = null;

        for(int i=3; i>=0; i--){
            Slot=inventory.getStack(All_Slots[i]);

            // DEBUG
            //TestMod.LOGGER.info("Reading slot number " + i + " | Contents: " + Slot.getItem().toString() + " x" + Slot.getCount());
            // readStack(i, (SoulCrucibleBlockEntity)inventory);

            if(!Slot.isEmpty()){
                //TestMod.LOGGER.info("Not empty stack");
                return i;
            }
        }
        //Failed all tests
        return -1;
    }

    // Function for outputting item entities (for when shift right clicked empty handed)
    // WHEN OUTPUT SLOTS ARE FUNCTIONAL, ADAPT THIS TO INCLUDE THEM FIRST AND FOREMOST
    public static boolean outputItem(World world, Inventory inventory, PlayerEntity player){
        boolean success = false;

        if(!world.isClient){

            int slotToOutput_num=findLastSlotWithItems(inventory);

            if(slotToOutput_num>=0){
                ItemStack Slot = inventory.getStack(slotToOutput_num);

                ItemEntity itemEntity = new ItemEntity(world, player.getX() + 0.5 , player.getY() + 1, player.getZ() + 0.5, Slot.copyAndEmpty());
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);

                /* DEBUG
                if(Slot.isEmpty()){
                    TestMod.LOGGER.info("Emptied slot");
                }
                // readStack(slotToOutput_num, (SoulCrucibleBlockEntity)inventory);
                */

                inventory.markDirty();
                success = true;
            }

        }

        return success;
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

    // Stuff for rendering the block entity fancy-ly
    public ItemStack getRenderStack(int slotNum){
        ItemStack stack;

        stack = this.getStack(All_Slots[slotNum]);

         /* DEBUG
        TestMod.LOGGER.info("Reading slot number " + slotNum + " | Contents: " + stack.getItem().toString() + " x" + stack.getCount());
        if(stack.isEmpty()){
            TestMod.LOGGER.info("Emtpy stack");
        }
         */

        return stack;
    }

    /* DEBUGGING
    static public void readStack(int slotNum, SoulCrucibleBlockEntity entity){
        ItemStack Slot=entity.getStack(Input_Slots[slotNum]);
        TestMod.LOGGER.info("Reading slot number " + slotNum + " | Contents: " + Slot.getItem().toString() + " x" + Slot.getCount());
        if(Slot.isEmpty()){
            TestMod.LOGGER.info("Emtpy stack");
        }
    }
    */

    @Override
    public void markDirty(){
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        super.markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket(){
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public NbtCompound toInitialChunkDataNbt(){
        return createNbt();
    }


}
