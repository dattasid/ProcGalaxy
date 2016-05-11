package planet;

import galaxy.ImprovedPerlin;
import util.ColorGrad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;


public class StormPlanet extends Planet
{

    int OCTAVES = 5;
    ColorGrad cg;
    
    double stormFeatureFreq;
    
    double tiltx, tilty;
    public StormPlanet(long seed)
    {
        super(seed);
        int NUMBANDS = 3 + rand.nextInt(15);
        
        int[] colors = new int[NUMBANDS];
        float[] pts = new float[NUMBANDS];
        // TODO : Choose a color scheme
        for (int i = 0; i < NUMBANDS; i++)
        {
            colors[i] = Color.HSBtoRGB(
              rand.nextFloat(),
              .2f + rand.nextFloat() * .4f,
              .2f + rand.nextFloat() * .8f
            );
            
            if (i == 0)
                pts[i] = 0;
            else
                pts[i] = pts[i - 1] + 1; //.5f + .5f * rand.nextFloat();
        }
        
        cg = new ColorGrad(colors, pts);
        
        stormFeatureFreq = 5 + rand.nextFloat() * 10;
        
        double tiltAngle = Math.PI*((2* rand.nextFloat() - 1) /6);
        tiltx = Math.cos(tiltAngle);
        tilty = Math.sin(tiltAngle);
    }
    
    @Override
    public BufferedImage render(int SIZE)
    {
       BufferedImage im = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        
        long t1 = System.currentTimeMillis(); 
        
        float cx = SIZE/2, cy = SIZE/2;
        
        final int RAD = SIZE/2;
        
        double offx = rand.nextDouble() * 256, offy = rand.nextDouble() * 256;  
        int oct[] = new int[OCTAVES];
        double fac[] = new double[OCTAVES];
        oct[0] = 1; fac[0] = 1;
        double MAXVAL = 1.01; 
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
                if (r < RAD)
                {
                    r1 = (float) (Math.asin(r_n) * 2 / Math.PI) * RAD;
                }
                else
                {
//                    im.setRGB(x, y, 0);
                    continue;
                }
                double x2 = r1 * dx/r;
                double y2 = r1 * dy/r;
                
                x2 /= RAD;
                y2 /= RAD;
                
                float z2 = (float) Math.sqrt(1-x2*x2-y2*y2);
              
                float bright = (float)(x2*sun_dir[0]+y2*sun_dir[1]+z2*sun_dir[2]);

                if (bright > 0)
                    bright = (float) ImprovedPerlin.fade(bright);
    //              bright = (float) Math.pow(bright, 10);
                
              if (bright > 0f)
              {
                  // Tilt dont matter for the noise part
                  double nx = x2*stormFeatureFreq+offx;
                  double ny = y2*stormFeatureFreq+offy;
                  
                double n_sur = 0;
                
                for (int i = 0; i < OCTAVES; i++)
                {
                    double val = ImprovedPerlin.noise(nx*oct[i], ny*oct[i], .5);
                    n_sur += val * fac[i];
                }

                double tiltedy = x2 * tilty + y2 * tiltx;
                tiltedy += n_sur * .05f;
                  
                im.setRGB(x, y,
                          blend(0xff000000, cg.get((float)((tiltedy) / 2 + .5f)),
                                  bright));
              }
              else
              {
                  im.setRGB(x, y, 0xff000000);
              }
              
            }
        }
        
        long t2 = System.currentTimeMillis();
        
        System.out.println("Took "+(t2-t1)+"ms");

        
        return im;
    }

}
