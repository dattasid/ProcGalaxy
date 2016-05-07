package planet;

import java.awt.image.BufferedImage;

public abstract class Planet
{
    
    public abstract BufferedImage render(int SIZE);

    /*
     * Radius normalized, 0 to 1
     */
    public static double spherizeR(double r_n)
    {
        return (Math.asin(r_n) * 2 / Math.PI);
    }
    
    public static int blend( int c1, int c2, float ratio ) {
        if ( ratio > 1f ) ratio = 1f;
        else if ( ratio < 0f ) ratio = 0f;
        float iRatio = 1.0f - ratio;

        int i1 = c1;
        int i2 = c2;

        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));

        return (a << 24 | r << 16 | g << 8 | b );
    }
    
    private static void norm_vec(float[] v)
    {
        float r = (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
        v[0] /= r;
        v[1] /= r;
        v[2] /= r;
    }
}
