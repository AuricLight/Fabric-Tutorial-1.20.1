package net.aurea.testmod.block.entity.renderer;

import net.aurea.testmod.block.entity.SoulCrucibleBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;


public class SoulCrucibleBlockEntityRenderer implements BlockEntityRenderer<SoulCrucibleBlockEntity> {
    public SoulCrucibleBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    @Override
    public void render(SoulCrucibleBlockEntity entity, float tickDelta, MatrixStack matrixes,
                       VertexConsumerProvider vertexConsumers, int light, int overlay){
        //TestMod.LOGGER.info("Attempted to render an item");

        for(int i=0; i<4; i++){

            // 1. Get item to render
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            ItemStack stack = entity.getRenderStack(i);
            matrixes.push();

            /* DEBUGGING
            SoulCrucibleBlockEntity.readStack(0,entity);
            SoulCrucibleBlockEntity.readStack(1,entity);
            SoulCrucibleBlockEntity.readStack(2,entity);
            SoulCrucibleBlockEntity.readStack(3,entity);
             */

            // 2 Position the item in the right place
            //Note: Distance from center to side: ~0.4 (7 pixels, 40.625% of a full block)

                //Items placed at -15º, 90º-15, 180º-15 and 270º-15 round the vertical axis (Y)
            int RotY=90*i-15;

                // Move to the origin of coordinates; (X,Z,Y) = (0.5,0.5,0.4)
            matrixes.translate(0.5f, 0.4f, 0.5);

                // Rotate in the angle stablished beforehand
            matrixes.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(RotY));

                // Move the distance from the center stablished beforehand, minus a little bit to compensate for the size of the item
            matrixes.translate(0.22f, 0.0f, 0.0f);

                // Make it look cute
            matrixes.scale(0.35f, 0.35f, 0.35f);
            matrixes.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));

            // 3. Render the item
            itemRenderer.renderItem(stack, ModelTransformationMode.GUI, getLightLevel(entity.getWorld(),
                    entity.getPos()), OverlayTexture.DEFAULT_UV, matrixes, vertexConsumers, entity.getWorld(), 1);
            matrixes.pop();
        }

    }

    //Calculate the right light level for the rendered items
    private int getLightLevel(World world, BlockPos pos){
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }

}

