package com.gmail.subnokoii78.tplcore.random;

import com.gmail.subnokoii78.tplcore.execute.NumberRange;
import com.gmail.subnokoii78.tplcore.vector.BlockPositionBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import org.jetbrains.annotations.NotNull;

public class PerlinNoise {
    private record CubeRange(double $000, double $100, double $010, double $110, double $001, double $101, double $011, double $111) {}

    public record NoiseGenerationOptions(double frequency, double amplitude) {}

    private final RangeRandomizer randomizer;

    private final int[] permutation = new int[512];

    private final Vector3Builder offset;

    public PerlinNoise(@NotNull RangeRandomizer randomizer) {
        this.randomizer = randomizer;
        offset = new Vector3Builder().calculate(__unused__ -> (double) randomizer.randInt(NumberRange.of(0, 2147483647)) / 2147483647 * 256);

        final int[] p = new int[]{
            151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225,
            140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148,
            247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32,
            57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175,
            74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122,
            60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54,
            65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169,
            200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64,
            52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212,
            207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
            119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104,
            218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241,
            81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157,
            184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93,
            222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
        };

        for (int i = 0; i < 256; i++) {
            int index = randomizer.randInt(NumberRange.of(i, 255));
            int old = p[i];
            p[i] = p[index];
            p[index] = old;

            permutation[i] = p[i];
            permutation[i + 256] = p[i];
        }
    }

    private double linear(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double trilinear(Vector3Builder t, CubeRange range) {
        // X
        final double x00 = linear(t.x(), range.$000, range.$100);
        final double x10 = linear(t.x(), range.$010, range.$110);
        final double x01 = linear(t.x(), range.$001, range.$101);
        final double x11 = linear(t.x(), range.$011, range.$111);

        // Y
        final double y0 = linear(t.y(), x00, x10);
        final double y1 = linear(t.y(), x01, x11);

        // Z
        return linear(t.z(), y0, y1);
    }

    private double fade(double x) {
        return (6 * x * x * x * x * x) - (15 * x * x * x * x) + (10 * x * x * x);
    }

    private double gradient(int hash, @NotNull Vector3Builder distance) {
        hash &= 15;

        final double u = hash < 8 ? distance.x() : distance.y();
        final double v = hash < 4 ? distance.y() : (hash != 12 && hash != 14 ? distance.z() : distance.x());

        return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
    }

    private CubeRange gridGradients(@NotNull Vector3Builder v, int AA, int AB, int BA, int BB) {
        return new CubeRange(
            gradient(permutation[AA], v),
            gradient(permutation[BA], v.x(v.x() - 1)),
            gradient(permutation[AB], v.y(v.y() - 1)),
            gradient(permutation[BB], v.x(v.x() - 1).y(v.y() - 1)),
            gradient(permutation[AA + 1], v.z(v.z() - 1)),
            gradient(permutation[BA + 1], v.x(v.x() - 1).z(v.z() - 1)),
            gradient(permutation[AB + 1], v.y(v.y() - 1).z(v.z() - 1)),
            gradient(permutation[BB + 1], v.x(v.x() - 1).y(v.y() - 1).z(v.z() - 1))
        );
    }

    public double noise3(@NotNull Vector3Builder v, @NotNull NoiseGenerationOptions options) {
        final Vector3Builder vb = v.copy()
            .scale(options.frequency)
            .add(offset);

        final Vector3Builder floored = vb.copy().calculate(Math::floor);

        final BlockPositionBuilder indices = floored.toBlockPositionBuilder().calculate(component -> component & 255);

        vb.subtract(floored).calculate(this::fade);

        final int xy00 = permutation[indices.x] + indices.y;
        final int xy10 = permutation[indices.x + 1] + indices.y;

        final int AA = permutation[xy00] + indices.z;
        final int AB = permutation[xy00 + 1] + indices.z;
        final int BA = permutation[xy10] + indices.z;
        final int BB = permutation[xy10 + 1] + indices.z;

        return options.amplitude * trilinear(
            vb,
            gridGradients(vb, AA, AB, BA, BB)
        );
    }

    /*public noise2(v: Vector2, options: NoiseGenerationOptions): number {
        return this.noise3({ x: v.x, y: v.y, z: 0 }, options);
    }

    public noise1(v: number, options: NoiseGenerationOptions): number {
        return this.noise2({ x: v, y: 0 }, options);
    }*/
}
