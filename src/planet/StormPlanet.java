package planet;

import galaxy.ImprovedPerlin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;


public class StormPlanet extends Planet
{

    Random rand = new Random();
    
    @Override
    public BufferedImage render(int SIZE)
    {
       BufferedImage im = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        
        long t1 = System.currentTimeMillis(); 
        
        float cx = SIZE/2, cy = SIZE/2;
        
        final int RAD = SIZE/2;
        
        double offx = rand.nextDouble() * 256, offy = rand.nextDouble() * 256;  

        
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
                else
                {
//                    im.setRGB(x, y, 0);
                    continue;
                }
                double x2 = r1 * dx/r+cx;
                double y2 = r1 * dy/r+cy;
                
                // Tilt dont matter
                x2 = x2*planetFeatureSize/RAD+offx;
                y2 = y2*planetFeatureSize/RAD+offy;
                
                if (r > RAD)
                    im.setRGB(x, y, 0xff000000);
                else
                {
                    // Shading.
                    float nx = dx/RAD;
                    float ny = dy/RAD;
                    float nz = (float) Math.sqrt(1-nx*nx-ny*ny);
                    
                    float bright = (nx*sun_dir[0]+ny*sun_dir[1]+nz*sun_dir[2]);
//                    bright = (float) ImprovedPerlin.fade(bright);
                    bright = (float) Math.pow(bright, 10);
                    
                    int surface_rgb = 0xff000000;
                    double n_lava = 0, n_sur = 0;
                    
                    for (int i = 0; i < OCTAVES; i++)
                    {
                        double val = ImprovedPerlin.noise(x2*oct[i], y2*oct[i], .5);
                        n_lava += (1 - Math.abs(val)) * fac[i];
                    }

                    for (int i = 0; i < OCTAVES; i++)
                    {
                        double val = ImprovedPerlin.noise(
                                x2*oct[i]*50, y2*oct[i]*50, .5);
                        n_sur += val * fac[i];
                    }
                    
                    n_lava /= MAXVAL;
                    n_sur /= MAXVAL;
                    n_sur = (n_sur + 1)/2;
                    n_sur = Math.pow(n_sur, 2);
                    
                    
                    surface_rgb = blend(0xff000000, 0xffff0000,
                                (float)(Math.pow(n_lava, 10)));
                    if (bright > .1f)
                    {
                        surface_rgb =
                                blendAdd(surface_rgb,
                                blend(0xff000000, 0xffffafaf,
                                (float)(bright*n_sur))
                                );
                    }
//                    surface_rgb = blend(0xff000000, 0xff00ff00, (float)n);
                    im.setRGB(x, y, surface_rgb);
                }
                
            }
        }
        
        long t2 = System.currentTimeMillis();
        
        System.out.println("Took "+(t2-t1)+"ms");

        
        return im;
    }

}
