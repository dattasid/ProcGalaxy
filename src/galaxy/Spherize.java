package galaxy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Spherize
{

    public static void main(String[] args)
    {
        int W = 400, H = 400;
        BufferedImage im = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        
        long t1 = System.currentTimeMillis(); 
        
        Random rand = new Random();
        
        float cx = W/2, cy = H/2;
        final float maxrad = W/2;
        
        int OCTAVES = 5;
        final int RAD = W/2;
        
        int oct[] = new int[OCTAVES];
        double fac[] = new double[OCTAVES];
        oct[0] = 1; fac[0] = 1;
        double MAXVAL = 1.01; 
        double offx = rand.nextDouble() * 3, offy = rand.nextDouble() * 3;  
        for (int i = 1; i < OCTAVES; i++)
        {
            oct[i] = oct[i-1]*2;
            fac[i] = fac[i-1]*.5;
        
            MAXVAL += fac[i];
        }
        
        int planetAtmosColor = 
                Color.HSBtoRGB(rand.nextFloat(), rand.nextFloat() * .2f + .7f, .9f)
                ;

        int planetSurfaceColor1 = 
                Color.HSBtoRGB(rand.nextFloat(), rand.nextFloat() * .2f + .2f,
                        /*rand.nextFloat() * .4f + .0f*/.9f);
        int planetSurfaceColor2 = 
                Color.HSBtoRGB(rand.nextFloat(), rand.nextFloat() * .2f + .7f,
                        /*rand.nextFloat() * .3f + .6f*/.9f);
        
        float planetFeatureSize = 2 + rand.nextFloat() * 5;
        float planetWaterLevel = .1f + rand.nextFloat() * .4f;
        
        float sun_dir[] = {
                rand.nextFloat(),rand.nextFloat(), rand.nextFloat(),  
        };
        norm_vec(sun_dir);
        
        for (int x = 0; x < W; x++)
        {
            for (int y = 0; y < H; y++)
            {
                // spherize
                float dx = x - cx;
                float dy = y - cy;
                float r = (float) Math.sqrt(dx*dx + dy*dy);
                
                float r1 = r;
                
                float r_n = r/maxrad;
                if (r < maxrad)
                {
                    r1 = (float) (Math.asin(r_n) * 2 / Math.PI) * maxrad;
                }
                double x2 = r1 * dx/r+cx;
                double y2 = r1 * dy/r+cy;
                
                x2 = x2*planetFeatureSize/RAD+offx;
                y2 = y2*planetFeatureSize/RAD+offy;
//                y2+=-x2;
                

                
                if (r > maxrad)
                    im.setRGB(x, y, 0xff000000);
                else
                {
                    // Shading.
                    float nx = dx/RAD;
                    float ny = dy/RAD;
                    float nz = (float) Math.sqrt(1-nx*nx-ny*ny);
                    
                    float bright = (nx*sun_dir[0]+ny*sun_dir[1]+nz*sun_dir[2]);
                    bright = (float) ImprovedPerlin.fade(bright);
                    
                    if (bright > .1f)
                    {
                        int surface_rgb = 0;
                        double n = 0;
                        
                        for (int i = 0; i < OCTAVES; i++)
                        {
                            n += ImprovedPerlin.noise(x2*oct[i], y2*oct[i], .5)*fac[i];
                        }
                        
                        if (n < planetWaterLevel)
                            surface_rgb = 0xff0000af;
                        else
                        {
    //                        int c = 0x7f+(int) (0xff * n/MAXVAL/2);
    //                        c &= 0xff;
    //                        c *= 0x000100;
    //                        c += 0xff000000;
    
    //                        int c = blend(planetSurfaceColor1, planetSurfaceColor2,
    //                                (float)((n-planetWaterLevel)/(MAXVAL - planetWaterLevel)));
                            
                            surface_rgb = planetSurfaceColor1;
                        }
    
                        // clouds
                        n = 0;
                        for (int i = 0; i < OCTAVES; i++)
                        {
                            n +=
                            Math.abs(
                                ImprovedPerlin.noise(
                                    x2*oct[i] + ImprovedPerlin.noise(x2*oct[i], y2*oct[i], 3.5)*1
                                    , y2*oct[i]*(i==0?3:1) + ImprovedPerlin.noise(x2*oct[i], y2*oct[i], 4.5)*1//3
                                    , 1.5)*fac[i]
                                );
                        }
                        float cloud = 0;
                        if (n >.5f)
                            cloud = (float)Math.pow((n-.5)/(MAXVAL-.5), .1);
                        
                        if (cloud > 0)
                            surface_rgb = blend(surface_rgb, planetAtmosColor, cloud);
                        
                        // atmosphere
                        if (r_n > .9)
                            surface_rgb =  
                                    blend(planetAtmosColor, surface_rgb,
    //                                        (1-r_n)/.1f
                                            (float)Math.pow((1-r_n)/.1f, .5)
                                            );
                        
                        surface_rgb =  
                                blend(0xff000000, surface_rgb,
                                        bright
                                        );
                        im.setRGB(x, y, surface_rgb);
                    }
                    else
                        im.setRGB(x, y, 0xff000000);
                }
                
            }
        }
        
        long t2 = System.currentTimeMillis();
        
        System.out.println("Took "+(t2-t1)+"ms");
        
        NebulaStormGalaxyGfx.showImage(im);
        
    }
    
    private static void norm_vec(float[] v)
    {
        float r = (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
        v[0] /= r;
        v[1] /= r;
        v[2] /= r;
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
    
}
