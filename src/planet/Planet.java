package planet;

import java.awt.image.BufferedImage;

public abstract class Planet
{
    float sun_dir[];

    public abstract BufferedImage render(int SIZE);

    

    public void setSunDir(float x, float y, float z)
    {
        sun_dir = new float[]{x, y, z};
    }
    
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
    
    /*
     * This is a weird function
     */
    public static int blendAdd( int c1, int c2) {
        int i1 = c1;
        int i2 = c2;

//        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

//        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

//        int a = Math.max(a1, a2);
        int r = (int)(r1+r2);
        int g = (int)(g1+g2);
        int b = (int)(b1+b2);

//        a = 0xff;//!
        r &= 0xff;
        g &= 0xff;
        b &= 0xff;
        
        return (/*a*/0xff << 24 | r << 16 | g << 8 | b );
    }
    public static void norm_vec(float[] v)
    {
        float r = (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
        v[0] /= r;
        v[1] /= r;
        v[2] /= r;
    }
}
