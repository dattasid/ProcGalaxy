package planet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import galaxy.ImprovedPerlin;

public class HabitPlanet extends Planet
{

    int OCTAVES = 5;
    int planetAtmosColor;
    
    int planetSurfaceColor1;
    int planetSurfaceColor2;
    
    float planetFeatureSize;
    float planetWaterLevel;
    
    boolean clouds;
    Random rand;
    double tiltx, tilty;
    double cloud_stretch;
    double cloud_amount_min;
    double cloud_swirly_scale;
    double cloudFeatureRelativeSize;
    double cloud_swirlyness;
    public HabitPlanet(long seed)
    {
        rand = new Random(seed);
        planetAtmosColor = 
                Color.HSBtoRGB(rand.nextFloat(), rand.nextFloat() * .2f + .7f, .9f)
                ;

        float hue1 = rand.nextFloat();
        float bright1 = rand.nextFloat() * .9f;
        planetSurfaceColor1 = 
                Color.HSBtoRGB(hue1, rand.nextFloat() * .2f + .2f,
                        bright1);
        planetSurfaceColor2 = 
                Color.HSBtoRGB(hue1+.5f, rand.nextFloat() * .2f + .3f,
                        1 - bright1);
        
        planetFeatureSize = 1 + rand.nextFloat() * 6;
        planetWaterLevel = .1f + rand.nextFloat() * .4f;
        
        sun_dir = new float[]{
                rand.nextFloat(),rand.nextFloat(), rand.nextFloat(),  
        };
        norm_vec(sun_dir);
        
        clouds = true;//rand.nextBoolean();
    
        tiltx = .6 + rand.nextDouble()*.4;
        tiltx *= rand.nextBoolean()?-1:1;
        tilty = Math.sqrt(1-tiltx*tiltx);
        
        cloud_stretch = 1+rand.nextDouble() *10;
        
        cloud_amount_min = rand.nextDouble() * .6;
        
        cloud_swirly_scale = rand.nextDouble() * 4;
        
        cloud_swirlyness = 1 + rand.nextDouble() * 3;
        cloudFeatureRelativeSize = Math.pow(4, rand.nextDouble()*2-1);
    }
    
    
    @Override
    public BufferedImage render(int SIZE)
    {
        BufferedImage im = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        
        long t1 = System.currentTimeMillis(); 
        
        float cx = SIZE/2, cy = SIZE/2;
        
        final int RAD = SIZE/2;
        
        int oct[] = new int[OCTAVES];
        double fac[] = new double[OCTAVES];
        oct[0] = 1; fac[0] = 1;
        double MAXVAL = 1.01; 
        double offx = rand.nextDouble() * 256, offy = rand.nextDouble() * 256;  
        for (int i = 1; i < OCTAVES; i++)
        {
            oct[i] = oct[i-1]*2;
            fac[i] = fac[i-1]*.5;
        
            MAXVAL += fac[i];
        }
        
        
        for (int x = 0; x < SIZE; x++)
        {
            for (int y = 0; y < SIZE; y++)
            {
                // spherize
                float dx = x - cx;
                float dy = y - cy;
                float r = (float) Math.sqrt(dx*dx + dy*dy);
                
                float r1 = r;
                
                float r_n = r/RAD;
                
                if (r_n > 1)
                    continue;
                
                r1 = (float) (Math.asin(r_n) * 2 / Math.PI) * RAD;
                double x2 = r1 * dx/r+cx;
                double y2 = r1 * dy/r+cy;

                // TODO: tilt, ice cap
                x2 = (x2-RAD)*planetFeatureSize/RAD+offx;
                y2 = (y2-RAD)*planetFeatureSize/RAD+offy;
                
                double tx = x2*tiltx - y2 * tilty;
                double ty = x2*tilty + y2 * tiltx;

                if (r < RAD)
                {
                    // Shading.
                    float nx = dx/RAD;
                    float ny = dy/RAD;
                    float nz = (float) Math.sqrt(1-nx*nx-ny*ny);
                    
                    float bright = (nx*sun_dir[0]+ny*sun_dir[1]+nz*sun_dir[2]);
                    bright = (float) ImprovedPerlin.fade(bright);

                    //SID_DEBUG
                    bright = 1;
                    if (bright > .05f)
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
                            double land_color = 0;
                            for (int i = 0; i < OCTAVES; i++)
                            {
                                land_color += ImprovedPerlin.noise(x2*oct[i], y2*oct[i], 9.5)*fac[i];
                            }

                            if (land_color < -.1) 
                                surface_rgb = planetSurfaceColor1;
                            else if (land_color > .1)
                                surface_rgb = planetSurfaceColor2;
                            else surface_rgb = blend(planetSurfaceColor1, planetSurfaceColor2,
                                    (float)(land_color+.1)/.2f);
//                            land_color = (land_color+MAXVAL)/2/MAXVAL;
//                            surface_rgb = blend(planetSurfaceColor1, planetSurfaceColor2,
//                                    land_color);
//                            int c = blend(planetSurfaceColor1, planetSurfaceColor2,
//                                    (float)(ele));
//                            
//                            surface_rgb = c;//planetSurfaceColor1;
                        }
    
                        // clouds
                        if (clouds)
                        {
                            // TODO: Extract cloud params
                            n = 0;
                            for (int i = 0; i < OCTAVES; i++)
                            {
                                n +=
                                    ImprovedPerlin.noise(
                                        tx*oct[i] * cloudFeatureRelativeSize
                                                + ImprovedPerlin.noise(tx*oct[i]*cloud_swirly_scale, ty*oct[i]*cloud_swirly_scale, 3.5)*cloud_swirlyness
                                        , ty*oct[i]*(i==0?cloud_stretch:1) * cloudFeatureRelativeSize 
                                                + ImprovedPerlin.noise(tx*oct[i]*cloud_swirly_scale, ty*oct[i]*cloud_swirly_scale, 4.5)*cloud_swirlyness
                                        , 1.5)*fac[i]
                                    ;
                            }
                            float cloud = 0;
                            if (n > cloud_amount_min)
                                cloud = (float)Math.pow((n-cloud_amount_min)/(MAXVAL-cloud_amount_min), .1);
                            
                            if (cloud > 0)
                                surface_rgb = blend(surface_rgb, planetAtmosColor, cloud);
                            
                            
                            
                            // atmosphere
                            if (r_n > .9)
                                surface_rgb =  
                                        blend(planetAtmosColor, surface_rgb,
        //                                        (1-r_n)/.1f
                                                (float)Math.pow((1-r_n)/.1f, .5)
                                                );
                        }
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

        
        return im;
    }
    
    
}
