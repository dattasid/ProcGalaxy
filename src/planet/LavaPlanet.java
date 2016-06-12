package planet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import galaxy.ImprovedPerlin;

public class LavaPlanet extends Planet
{

    int OCTAVES = 5;
    
    float planetFeatureSize;
    float planetWaterLevel;
    
    int oct[] = new int[OCTAVES];
    double fac[] = new double[OCTAVES];
    double MAXVAL = 1.01; 

    public LavaPlanet(long seed)
    {
        super(seed);
        
        planetFeatureSize = 2 + rand.nextFloat() * 30;
        
        sun_dir = new float[]{
                rand.nextFloat(),rand.nextFloat(), rand.nextFloat(),  
        };
        norm_vec(sun_dir);
        
        oct[0] = 1; fac[0] = 1;
        for (int i = 1; i < OCTAVES; i++)
        {
            oct[i] = oct[i-1]*2;
            fac[i] = fac[i-1]*.5;
        
            MAXVAL += fac[i];
        }
        
        planetWaterLevel = (float) (.85f * MAXVAL);//.1f + rand.nextFloat() * .4f;
        
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

//        NaiveVoronoi v= new NaiveVoronoi(40, rand);
        
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
                    
                    
                    surface_rgb = blend(0xff000000, 0xffef0000,
                                (float)(Math.pow(n_lava, 20)));
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
    

    /*
     * Not working out right now.
     * Cell/Worley noise keeps the point origins at zero value
     * and the edges can be arbitrary heights.
     * It will be nice to have a noise where all the edges
     * voronoi boundaries are all the same value.
     */
    static class NaiveVoronoi
    {
        int n;
        double x[], y[];
        
        public NaiveVoronoi(int n, Random rand)
        {
            this.n = n;
            
            x = new double[n];
            y = new double[n];
            
            for (int i = 0; i < n; i++)
            {
                x[i] = rand.nextDouble();
                y[i] = rand.nextDouble();
            }
        }
        
        public double get_d2_minus_d2(double x, double y)
        {
//            x -= (long) x;
//            y -= (long) y;
            
            double dist2_1 = Double.POSITIVE_INFINITY;
            double dist2_2 = Double.POSITIVE_INFINITY;
            
            for (int i = 0; i < n; i++)
            {
                double dx = this.x[i] - x;
                double dy = this.y[i] - y;
                double d = dx*dx+dy*dy;
//                System.out.println(i+"---------"+x+" "+y+" "+dx+" "+dy+" // "+d);
                    
                if (d < dist2_1)
                {
                    dist2_2 = dist2_1;
                    dist2_1 = d;
                }
            }

            double ret = Math.abs(
//                    dist2_1 - dist2_2
                        Math.sqrt(dist2_1)
                        - Math.sqrt(dist2_2)
                    );
            return ret;
        }
    }
}
