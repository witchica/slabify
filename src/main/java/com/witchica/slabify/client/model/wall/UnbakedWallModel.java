package com.witchica.slabify.client.model.wall;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.witchica.slabify.client.model.SlabifyModelState;
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
import net.minecraft.world.level.block.state.properties.WallSide;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class UnbakedWallModel implements UnbakedModel, BakedModel, FabricBakedModel {
    private final Block parent;
    private final boolean up;
    private final WallSide north;
    private final WallSide east;
    private final WallSide south;
    private final WallSide west;

    private UnbakedModel wallPostUnbakedCache;
    private UnbakedModel wallSideCache;
    private UnbakedModel wallSideTallCache;

    private TextureAtlasSprite particleSprite;

    private List<BakedModel> parts = new ArrayList<>();

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
        parts.forEach(part -> {
            part.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        });
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        parts.forEach(part -> part.emitItemQuads(stack, randomSupplier, context));
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
        this.wallPostUnbakedCache = function.apply(new ResourceLocation("minecraft", "block/template_wall_post"));
        this.wallSideCache = function.apply(new ResourceLocation("minecraft", "block/template_wall_side"));
        this.wallSideTallCache = function.apply(new ResourceLocation("minecraft", "block/template_wall_side_tall"));
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

        if(this.up) {
            parts.add(wallPostUnbakedCache.bake(modelBaker, material -> {
                return this.particleSprite;
            }, modelState, resourceLocation));
        }

        WallSide[] wallSides = new WallSide[] {north, east, south, west};
        float[] rotations = new float[] {0, 270, 180, 90};

        for(int i = 0; i < 4; i++) {
            if(wallSides[i] != WallSide.NONE) {
                int finalI = i;
                parts.add((wallSides[i] == WallSide.LOW ? wallSideCache : wallSideTallCache).bake(modelBaker, material -> {
                    return this.particleSprite;
                }, new SlabifyModelState(modelState.getRotation(), rotations[i]), resourceLocation));
            }
        }


        return this;
    }
}
