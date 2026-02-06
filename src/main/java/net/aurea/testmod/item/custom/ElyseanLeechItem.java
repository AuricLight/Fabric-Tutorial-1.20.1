package net.aurea.testmod.item.custom;

import net.aurea.testmod.entity.effect.ModStatusEffect;
import net.aurea.testmod.item.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class ElyseanLeechItem extends Item {
    public ElyseanLeechItem(Settings settings) {
        super(settings);
    }

    //Max charge that can be held by the item
    private final int maxCharge=20;

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (!world.isClient) { //Is the server executing this?
            NbtCompound data = stack.getOrCreateNbt();
            boolean isAwake = data.getBoolean("Awake");
            long timeWhenUsed = data.getLong("TimeWhenUsed");

            // For every 4 seconds like this (80 ticks), execute
            if(isAwake && entity.isPlayer() && ((world.getTime()-timeWhenUsed)%80==0)){
                PlayerEntity user = (PlayerEntity) entity;
                 //user.sendMessage(Text.literal("4s have passed, executing code"));

                // INFLICT BLEEDING BASED ON THE AMOUNT OF LEECHES

                    //Checking the number of leeches on the inventory
                int leechNumber = 0;
                for (int i=0; i<=40; i++){
                    Item currentItem = user.getInventory().getStack(i).getItem();
                    NbtCompound currentData = user.getInventory().getStack(i).getOrCreateNbt();
                    // If the item is a Leech and is Awake, count it!
                        //user.sendMessage(Text.literal(String.valueOf(currentItem)));
                        //user.sendMessage(Text.literal(String.valueOf(currentData.getBoolean("Awake"))));
                    if( (currentItem == ModItems.ElyseanLeech) && currentData.getBoolean("Awake")){
                        leechNumber++;
                            //user.sendMessage(Text.literal("Leech"));
                    }
                }
                    //user.sendMessage(Text.literal("Found " + leechNumber + " awake leeches"));

                // Turn leechNumber into an actual amplifier (essentialy, bring it down by 1)
                if(leechNumber!=0){
                    leechNumber--;
                }

                StatusEffectInstance statusEffect = new StatusEffectInstance(ModStatusEffect.BLEEDING, 90, leechNumber);; //Lasts for 4.5 seconds (80 ticks)

                user.addStatusEffect(statusEffect);

               // CHARGING THE ITEM
                int Charge= data.getInt("Charge");
                    //If the item is fully charged (6 charges), don't add to charges
                if(Charge<maxCharge){
                    // If 80 ticks, or 4 seconds have passed, add to charge and reset counter
                    Charge++;
                    data.putInt("Charge",Charge);
                }

            }
        }
    }

    // First time the shrimp is used, it awakens

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack item = user.getStackInHand(hand);
        NbtCompound data = item.getOrCreateNbt();

            if (!data.contains("Awake")){
                data.putBoolean("Awake",true);
                data.putInt("Charge",0);
                data.putLong("TimeWhenUsed",world.getTime());
                return TypedActionResult.success(item);
            } else if (data.getBoolean("Awake")){
                return ItemUsage.consumeHeldItem(world, user, hand);
            } else {
                return TypedActionResult.fail(item);
            }

    }

    //CONSUMING ITEM THINGIE

    @Override
    public ItemStack finishUsing(ItemStack item, World world, LivingEntity user) {
        NbtCompound data = item.getOrCreateNbt();
        boolean isAwake = data.getBoolean("Awake");

        if(user.isPlayer()){
            ((PlayerEntity) user).getItemCooldownManager().set(this, 10);
        }

        if(isAwake){
            if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, item);
            }

            if (!world.isClient) {
                // Heal whatever is held in the charge amount.
                    //Excess health will be halved and turned into absorption
                int currentCharge = data.getInt("Charge");
                float excess= currentCharge - (user.getMaxHealth() - user.getHealth());

                //Can't seem to make a signed float, so this will have to do (no negative absorption)
                if(excess<0){
                    excess=0;
                }

                    //Every time you overheal, you loose 2hp of overheal
                float originalOverheal;
                if(user.getAbsorptionAmount()!=0f){
                    // This formula is done so the amount of absorption lost is proportional to the absorption already had
                        //If a player (20hp) has already half an hp bar of absorption, then it must lose 4 absorption hp, hence this linear formula.
                    originalOverheal = user.getAbsorptionAmount()* ( 1 - 12/user.getMaxHealth());
                } else {
                    originalOverheal = 0;
                }

                user.setAbsorptionAmount(excess + originalOverheal);
                user.heal(currentCharge);

                //user.sendMessage(Text.literal("HEALHEALHEAL"));
            } else {
                // Show particles to the user
                showEmoteParticle(world, user);
                    //user.sendMessage(Text.literal("PARTICLEE"));
            }

            if (user instanceof PlayerEntity && ((PlayerEntity)user).getAbilities().creativeMode) {
                return new ItemStack(ModItems.ElyseanLeech);
            }

        }
        return new ItemStack(Items.AIR); // After popping the shrimp, it dies (Once you activate the shrimp, you can only eat it, making it vanish)
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 16; // Shrimp takes 0.8s to pop
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    // DURABILITY BAR THINGIE

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        NbtCompound data = stack.getOrCreateNbt();

        return data.getBoolean("Awake");
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(getCharge(stack) * 13.0F / maxCharge);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        float f = Math.max(0.0F, ((float)maxCharge - getCharge(stack)) / maxCharge);
        return MathHelper.hsvToRgb(0.0F, (1.0F - f/2.0F), (1.0F - f/2.0F));
    }

    protected int getCharge(ItemStack stack){
        return stack.getOrCreateNbt().getInt("Charge");
    }


    protected void showEmoteParticle(World world, Entity user) {
        ParticleEffect particleEffect = ParticleTypes.HEART;

        for (int i = 0; i < 7; i++) {
            world.addParticle (particleEffect,
                    user.getParticleX(1.5),
                    user.getRandomBodyY() + 0.5,
                    user.getParticleZ(1.5),
                    0.1, 0.1, 0.1);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context){
        int Charges = stack.getOrCreateNbt().getInt("Charge");
        tooltip.add(Text.translatable("tooltip.tutorialmod.elysean_leech.tooltip").formatted(Formatting.GRAY));

        if(stack.getOrCreateNbt().getBoolean("Awake")){
            tooltip.add(Text.translatable("tooltip.tutorialmod.elysean_leech.charge",Charges,maxCharge).formatted((Formatting.DARK_RED)));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

}

