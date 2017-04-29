package eyeq.explosivemelon;

import eyeq.explosivemelon.block.BlockMelonExplosive;
import eyeq.util.client.model.UModelLoader;
import eyeq.util.client.renderer.ResourceLocationFactory;
import eyeq.util.client.renderer.block.statemap.StateMapper;
import eyeq.util.client.renderer.block.statemap.StateMapperNormal;
import eyeq.util.client.resource.ULanguageCreator;
import eyeq.util.client.resource.lang.LanguageResourceManager;
import eyeq.util.oredict.UOreDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.io.File;

import static eyeq.explosivemelon.ExplosiveMelon.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
@Mod.EventBusSubscriber
public class ExplosiveMelon {
    public static final String MOD_ID = "eyeq_explosivemelon";

    @Mod.Instance(MOD_ID)
    public static ExplosiveMelon instance;

    private static final ResourceLocationFactory resource = new ResourceLocationFactory(MOD_ID);

    public static Block blockMelonExplosive;
    public static Block stemMelonExplosive;

    public static Item seedsMelonExplosive;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        addRecipes();
        if(event.getSide().isServer()) {
            return;
        }
        renderBlockModels();
        renderItemModels();
        createFiles();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if(event.getSide().isServer()) {
            return;
        }
        registerBlockColors();
    }

    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> event) {
        blockMelonExplosive = new BlockMelonExplosive().setHardness(1.0F).setUnlocalizedName("blockMelonExplosive");
        stemMelonExplosive = new BlockStem(blockMelonExplosive) {{
            setSoundType(SoundType.WOOD);
        }}.setHardness(0.0F).setUnlocalizedName("stemMelonExplosive");

        GameRegistry.register(blockMelonExplosive, resource.createResourceLocation("melon_block"));
        GameRegistry.register(stemMelonExplosive, resource.createResourceLocation("melon_stem"));
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event) {
        seedsMelonExplosive = new ItemSeeds(stemMelonExplosive, Blocks.FARMLAND).setUnlocalizedName("seedsMelonExplosive");

        GameRegistry.register(new ItemBlock(blockMelonExplosive), blockMelonExplosive.getRegistryName());
        GameRegistry.register(seedsMelonExplosive, resource.createResourceLocation("melon_seeds"));
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(seedsMelonExplosive), Items.MELON_SEEDS, UOreDictionary.OREDICT_GUNPOWDER));
    }

    @SideOnly(Side.CLIENT)
    public static void renderBlockModels() {
        ResourceLocationFactory mc = ResourceLocationFactory.mc;
        ModelLoader.setCustomStateMapper(blockMelonExplosive, new StateMapperNormal(Blocks.MELON_BLOCK.getRegistryName()));
        ModelLoader.setCustomStateMapper(stemMelonExplosive, new StateMapper(mc, null, "melon_stem") {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                ignores.clear();
                if(state.getValue(BlockStem.FACING) != EnumFacing.UP) {
                    ignores.add(BlockStem.AGE);
                }
                return super.getModelResourceLocation(state);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public static void renderItemModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMelonExplosive), 0, ResourceLocationFactory.createModelResourceLocation(Blocks.MELON_BLOCK));
        ModelLoader.setCustomModelResourceLocation(seedsMelonExplosive, 0, ResourceLocationFactory.createModelResourceLocation(Items.MELON_SEEDS));
    }

    @SideOnly(Side.CLIENT)
    public static void registerBlockColors() {
        BlockColors blockColors = FMLClientHandler.instance().getClient().getBlockColors();
        blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            int age = state.getValue(BlockStem.AGE);
            int red = age * 32;
            int green = 255 - age * 8;
            int blue = age * 4;
            return red << 16 | green << 8 | blue;
        }, stemMelonExplosive);
    }

    public static void createFiles() {
        File project = new File("../1.11.2-ExplosiveMelon");

        LanguageResourceManager language = new LanguageResourceManager();

        language.register(LanguageResourceManager.EN_US, blockMelonExplosive, "Explosive Melon Block");
        language.register(LanguageResourceManager.JA_JP, blockMelonExplosive, "爆発するスイカブロック");
        language.register(LanguageResourceManager.EN_US, stemMelonExplosive, "Explosive Melon Stem");
        language.register(LanguageResourceManager.JA_JP, stemMelonExplosive, "爆発するスイカの茎");

        language.register(LanguageResourceManager.EN_US, seedsMelonExplosive, "Explosive Melon Seeds");
        language.register(LanguageResourceManager.JA_JP, seedsMelonExplosive, "爆発するスイカの種");

        ULanguageCreator.createLanguage(project, MOD_ID, language);
    }
}
