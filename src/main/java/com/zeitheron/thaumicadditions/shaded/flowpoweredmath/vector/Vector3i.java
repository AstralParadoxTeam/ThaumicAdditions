/*
 * Decompiled with CFR 0_123.
 */
package com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector;

import java.io.Serializable;

import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.GenericMath;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.HashFunctions;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vector2i;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vector3d;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vector3f;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vector3l;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vector4i;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.VectorNi;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vectord;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vectorf;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vectori;
import com.zeitheron.thaumicadditions.shaded.flowpoweredmath.vector.Vectorl;

public class Vector3i
implements Vectori,
Comparable<Vector3i>,
Serializable,
Cloneable {
    private static final long serialVersionUID = 1;
    public static final Vector3i ZERO = new Vector3i(0, 0, 0);
    public static final Vector3i UNIT_X = new Vector3i(1, 0, 0);
    public static final Vector3i UNIT_Y = new Vector3i(0, 1, 0);
    public static final Vector3i UNIT_Z = new Vector3i(0, 0, 1);
    public static final Vector3i ONE = new Vector3i(1, 1, 1);
    public static final Vector3i RIGHT = UNIT_X;
    public static final Vector3i UP = UNIT_Y;
    public static final Vector3i FORWARD = UNIT_Z;
    private final int x;
    private final int y;
    private final int z;
    private volatile transient boolean hashed = false;
    private volatile transient int hashCode = 0;

    public Vector3i() {
        this(0, 0, 0);
    }

    public Vector3i(Vector2i v) {
        this(v, 0);
    }

    public Vector3i(Vector2i v, double z) {
        this(v, GenericMath.floor(z));
    }

    public Vector3i(Vector2i v, int z) {
        this(v.getX(), v.getY(), z);
    }

    public Vector3i(Vector3i v) {
        this(v.x, v.y, v.z);
    }

    public Vector3i(Vector4i v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public Vector3i(VectorNi v) {
        this(v.get(0), v.get(1), v.size() > 2 ? v.get(2) : 0);
    }

    public Vector3i(double x, double y, double z) {
        this(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public Vector3i add(Vector3i v) {
        return this.add(v.x, v.y, v.z);
    }

    public Vector3i add(double x, double y, double z) {
        return this.add(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i add(int x, int y, int z) {
        return new Vector3i(this.x + x, this.y + y, this.z + z);
    }

    public Vector3i sub(Vector3i v) {
        return this.sub(v.x, v.y, v.z);
    }

    public Vector3i sub(double x, double y, double z) {
        return this.sub(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i sub(int x, int y, int z) {
        return new Vector3i(this.x - x, this.y - y, this.z - z);
    }

    public Vector3i mul(double a) {
        return this.mul(GenericMath.floor(a));
    }

    @Override
    public Vector3i mul(int a) {
        return this.mul(a, a, a);
    }

    public Vector3i mul(Vector3i v) {
        return this.mul(v.x, v.y, v.z);
    }

    public Vector3i mul(double x, double y, double z) {
        return this.mul(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i mul(int x, int y, int z) {
        return new Vector3i(this.x * x, this.y * y, this.z * z);
    }

    public Vector3i div(double a) {
        return this.div(GenericMath.floor(a));
    }

    @Override
    public Vector3i div(int a) {
        return this.div(a, a, a);
    }

    public Vector3i div(Vector3i v) {
        return this.div(v.x, v.y, v.z);
    }

    public Vector3i div(double x, double y, double z) {
        return this.div(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i div(int x, int y, int z) {
        return new Vector3i(this.x / x, this.y / y, this.z / z);
    }

    public int dot(Vector3i v) {
        return this.dot(v.x, v.y, v.z);
    }

    public int dot(double x, double y, double z) {
        return this.dot(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public int dot(int x, int y, int z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public Vector3i project(Vector3i v) {
        return this.project(v.x, v.y, v.z);
    }

    public Vector3i project(double x, double y, double z) {
        return this.project(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i project(int x, int y, int z) {
        int lengthSquared = x * x + y * y + z * z;
        if (lengthSquared == 0) {
            throw new ArithmeticException("Cannot project onto the zero vector");
        }
        float a = (float)this.dot(x, y, z) / (float)lengthSquared;
        return new Vector3i(a * (float)x, a * (float)y, a * (float)z);
    }

    public Vector3i cross(Vector3i v) {
        return this.cross(v.x, v.y, v.z);
    }

    public Vector3i cross(double x, double y, double z) {
        return this.cross(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i cross(int x, int y, int z) {
        return new Vector3i(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }

    public Vector3i pow(double pow) {
        return this.pow(GenericMath.floor(pow));
    }

    @Override
    public Vector3i pow(int power) {
        return new Vector3i(Math.pow(this.x, power), Math.pow(this.y, power), Math.pow(this.z, power));
    }

    @Override
    public Vector3i abs() {
        return new Vector3i(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    @Override
    public Vector3i negate() {
        return new Vector3i(- this.x, - this.y, - this.z);
    }

    public Vector3i min(Vector3i v) {
        return this.min(v.x, v.y, v.z);
    }

    public Vector3i min(double x, double y, double z) {
        return this.min(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i min(int x, int y, int z) {
        return new Vector3i(Math.min(this.x, x), Math.min(this.y, y), Math.min(this.z, z));
    }

    public Vector3i max(Vector3i v) {
        return this.max(v.x, v.y, v.z);
    }

    public Vector3i max(double x, double y, double z) {
        return this.max(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public Vector3i max(int x, int y, int z) {
        return new Vector3i(Math.max(this.x, x), Math.max(this.y, y), Math.max(this.z, z));
    }

    public int distanceSquared(Vector3i v) {
        return this.distanceSquared(v.x, v.y, v.z);
    }

    public int distanceSquared(double x, double y, double z) {
        return this.distanceSquared(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public int distanceSquared(int x, int y, int z) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public float distance(Vector3i v) {
        return this.distance(v.x, v.y, v.z);
    }

    public float distance(double x, double y, double z) {
        return this.distance(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    public float distance(int x, int y, int z) {
        return (float)Math.sqrt(this.distanceSquared(x, y, z));
    }

    @Override
    public int lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Override
    public float length() {
        return (float)Math.sqrt(this.lengthSquared());
    }

    @Override
    public int getMinAxis() {
        return this.x < this.y ? (this.x < this.z ? 0 : 2) : (this.y < this.z ? 1 : 2);
    }

    @Override
    public int getMaxAxis() {
        return this.x < this.y ? (this.y < this.z ? 2 : 1) : (this.x < this.z ? 2 : 0);
    }

    public Vector2i toVector2() {
        return new Vector2i(this);
    }

    public Vector2i toVector2(boolean useZ) {
        return new Vector2i(this.x, useZ ? this.z : this.y);
    }

    public Vector4i toVector4() {
        return this.toVector4(0);
    }

    public Vector4i toVector4(double w) {
        return this.toVector4(GenericMath.floor(w));
    }

    public Vector4i toVector4(int w) {
        return new Vector4i(this, w);
    }

    public VectorNi toVectorN() {
        return new VectorNi(this);
    }

    @Override
    public int[] toArray() {
        return new int[]{this.x, this.y, this.z};
    }

    @Override
    public Vector3i toInt() {
        return new Vector3i(this.x, this.y, this.z);
    }

    @Override
    public Vector3l toLong() {
        return new Vector3l(this.x, this.y, this.z);
    }

    @Override
    public Vector3f toFloat() {
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override
    public Vector3d toDouble() {
        return new Vector3d(this.x, this.y, this.z);
    }

    @Override
    public int compareTo(Vector3i v) {
        return this.lengthSquared() - v.lengthSquared();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vector3i)) {
            return false;
        }
        Vector3i vector3 = (Vector3i)o;
        if (vector3.x != this.x) {
            return false;
        }
        if (vector3.y != this.y) {
            return false;
        }
        if (vector3.z != this.z) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (!this.hashed) {
            this.hashCode = (HashFunctions.hash(this.x) * 211 + HashFunctions.hash(this.y)) * 97 + HashFunctions.hash(this.z);
            this.hashed = true;
        }
        return this.hashCode;
    }

    public Vector3i clone() {
        return new Vector3i(this);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public static Vector3i from(int n) {
        return n == 0 ? ZERO : new Vector3i(n, n, n);
    }

    public static Vector3i from(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? ZERO : new Vector3i(x, y, z);
    }
}
