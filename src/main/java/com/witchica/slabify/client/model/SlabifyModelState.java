package com.witchica.slabify.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SlabifyModelState implements ModelState {
    private final Transformation transformation;
    private final boolean uvLocked;

    public SlabifyModelState(Transformation transformation, float degrees, boolean uvLocked) {
        Vector3f transform = new Vector3f();

        if(degrees == 90 || degrees == 270) {
            transform = transform.add(0, 0.0001f, 0);
        }
        this.transformation = transformation.compose(new Transformation(transform, new Quaternionf().rotateY(Math.toRadians(degrees)), null, null));
        this.uvLocked = uvLocked;
    }
    @Override
    public Transformation getRotation() {
        return transformation;
    }

    @Override
    public boolean isUvLocked() {
        return true;//uvLocked;
    }
}
