package planet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import galaxy.ImprovedPerlin;

public class HabitPlanet extends Planet
{

    int OCTAVES = 5;
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

    @Override
    public BufferedImage render(int SIZE)
    {
        BufferedImage im = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        
        long t1 = System.currentTimeMillis(); 
        
        Random rand = new Random();
        
        float cx = SIZE/2, cy = SIZE/2;
        
        final int RAD = SIZE/2;
        
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
        
        norm_vec(sun_dir);
        
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
                if (r < RAD)
                {
                    r1 = (float) (Math.asin(r_n) * 2 / Math.PI) * RAD;
                }
                double x2 = r1 * dx/r+cx;
                double y2 = r1 * dy/r+cy;
                
                x2 = x2*planetFeatureSize/RAD+offx;
                y2 = y2*planetFeatureSize/RAD+offy;
//                y2+=-x2;
                

                
                if (r > RAD)
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
                                ImprovedPerlin.noise(
                                    x2*oct[i] + ImprovedPerlin.noise(x2*oct[i], y2*oct[i], 3.5)*3
                                    , y2*oct[i]*(i==0?5:1) + ImprovedPerlin.noise(x2*oct[i], y2*oct[i], 4.5)*3
                                    , 1.5)*fac[i]
                                ;
                        }
                        float cloud = 0;
                        if (n >.1f)
                            cloud = (float)Math.pow((n-.1)/(MAXVAL-.1), .1);
                        
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

    }
    
    
}
