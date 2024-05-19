package com.witchica.slabify.client.model.wall;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class UnbakedWallModel implements UnbakedModel, BakedModel, FabricBakedModel {
    private final Block parent;
    private final boolean up;
    private final WallSide north;
    private final WallSide east;
    private final WallSide south;
    private final WallSide west;
    private Mesh mesh;

    private TextureAtlasSprite particleSprite;

    public UnbakedWallModel(boolean up, WallSide north, WallSide east, WallSide south, WallSide west, Block parent) {
        this.parent = parent;
        this.up = up;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return List.of();
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        mesh.outputTo(context.getEmitter());
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        mesh.outputTo(context.getEmitter());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleSprite;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {

    }


    public Map<Direction, TextureAtlasSprite> getTextureMapFromBlock(Block parent, BakedModel blockModel) {
        Map<Direction, TextureAtlasSprite> textures = new HashMap<>();
        TextureAtlasSprite defaultTexture = blockModel.getParticleIcon();

        for(Direction direction : Direction.values()) {

            List<BakedQuad> quads = blockModel.getQuads(parent.defaultBlockState(), direction, RandomSource.create());
            for(BakedQuad quad : quads) {
                if(quad.getDirection() == direction) {
                    textures.put(direction, quad.getSprite());
                    break;
                }
            }

            if(!textures.containsKey(direction)) {
                textures.put(direction, defaultTexture);
            }
        }

        return textures;
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation) {
        UnbakedModel model = modelBaker.getModel(BlockModelShaper.stateToModelLocation(parent.defaultBlockState()));
        BakedModel bakedModel = model.bake(modelBaker, function, modelState, resourceLocation);

        Map<Direction, TextureAtlasSprite> textureAtlasSpriteMap = getTextureMapFromBlock(parent, bakedModel);
        this.particleSprite = bakedModel.getParticleIcon();

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder meshBuilder = renderer.meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();

        generateMesh(emitter, textureAtlasSpriteMap);

        mesh = meshBuilder.build();

        return this;
    }

    private void box(Vector3f position, Vector3f size, QuadEmitter emitter, Map<Direction, TextureAtlasSprite> textureAtlasSpriteMap) {
        for(Direction direction : Direction.values()) {
            emitter.square(direction, 0.25f, 0f, 0.75f, 1f, 0f);

            emitter.spriteBake(textureAtlasSpriteMap.get(direction), MutableQuadView.BAKE_LOCK_UV);
            emitter.color(-1,-1,-1,-1);
            emitter.emit();
        }
    }

    private void generateMesh(QuadEmitter emitter, Map<Direction, TextureAtlasSprite> textureAtlasSpriteMap) {
        float minHeight = 0.5f;
        float maxHeight = 1f;

        box(new Vector3f(0.25f,0f, 0.25f), new Vector3f(0.5f, 1f, 0.5f), emitter, textureAtlasSpriteMap);
    }
}
