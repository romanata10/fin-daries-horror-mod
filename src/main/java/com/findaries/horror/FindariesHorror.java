package com.findaries.horror;

import com.findaries.horror.entity.HorrorEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(FindariesHorror.MOD_ID)
public class FindariesHorror {
    public static final String MOD_ID = "findaries_horror";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Block> WONDER_PORTAL = BLOCKS.register("wonder_portal",
            () -> new net.minecraft.world.level.block.Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(-1.0F, 3600000.0F).noOcclusion().lightLevel(s -> 8).sound(SoundType.AMETHYST)) {
                @Override public net.minecraft.world.InteractionResult use(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level,
                        net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
                        net.minecraft.world.phys.BlockHitResult hit) {
                    if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer sp) {
                        com.findaries.horror.world.HorrorWorldEvents.enterPortal(sp, pos);
                    }
                    return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
                }
            });
    public static final RegistryObject<Item> WONDER_PORTAL_ITEM = ITEMS.register("wonder_portal", () -> new BlockItem(WONDER_PORTAL.get(), new Item.Properties()));

    public FindariesHorror() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus); ITEMS.register(bus); HorrorEntities.TYPES.register(bus);
    }
    public static ResourceLocation id(String path) { return new ResourceLocation(MOD_ID, path); }
}
