package planet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import galaxy.NebulaStormGalaxyGfx;

public class PlanetsGfx
{

    public static void main2(String[] args)
    {
        BufferedImage im = new StormPlanet(System.currentTimeMillis()).render(400);
//        Graphics2D g2 = im.createGraphics();
//        g2.setColor(Color.green);
//        g2.fillRect(0, 0, 400, 400);
        NebulaStormGalaxyGfx.showImage(im);
    }
    
    public static void main(String[] args)
    {
        for (int i = 0; i < 10; i++)
        {
            BufferedImage im = generate();
            NebulaStormGalaxyGfx.saveImage(im, "out/planets");
        }
    }
    public static BufferedImage generate()
    {
        final int H = 800, W = H*3/4;

        BufferedImage im = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = im.createGraphics();
        
        Planet p = null;
        Random rand = new Random();
        
        g2.setColor(Color.black);
        g2.fillRect(0, 0, W, H);
        
        drawStars(g2, rand, W, H);
        
        float[] sun_dir = { rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};
        Planet.norm_vec(sun_dir);
        
        long seed;
        
        makePlanet(g2, W, H, 400, W/2-200, H/2-200, rand, sun_dir);
        
        int N = rand.nextInt(5);
        for (int i = 0; i < N; i++)
        {
            int dia = rand.nextInt(100) + 50;
            
            int x = rand.nextInt(W *3/4) + W/2/4;
            int y = rand.nextInt(H *3/4) + H/2/4;
            
            makePlanet(g2, W, H, dia, x, y, rand, sun_dir);
        }

        return im;
        
//        NebulaStormGalaxyGfx.showImage(im);
    }
    private static Planet makePlanet(Graphics2D g2, final int W, final int H,
            int diameter, int x, int y, Random rand, float[] sun_dir)
    {
        Planet p = null;
        long seed = rand.nextLong();
        switch (rand.nextInt(5))
        {
        case 0:
        case 1:
            p = new HabitPlanet(seed);
            break;
        case 2:
            p = new LavaPlanet(seed);
            break;
        case 3:
        case 4:
            p = new StormPlanet(seed);
            break;
        }
        
        p.setSunDir(sun_dir[0],  sun_dir[1], sun_dir[2]);
        
        BufferedImage pim = p.render(diameter);
        
        g2.drawImage(pim, x, y, null);
        return p;
    }

    private static void drawStars(Graphics2D g2, Random rand, int w, int h)
    {
        g2.setColor(Color.white);
        for (int i = 0; i < 1000; i++)
        {
            double x = rand.nextInt(w);
            double y = rand.nextInt(h);
        
            int rad = 1 + (int) Math.abs(rand.nextGaussian() * 2);
            
            float bright = .3f + .2f * rad;
            if (bright > 1) bright = 1;
                
//           g2.fillOval((int)x, (int)y, 10, 10);
            NebulaStormGalaxyGfx.drawStar(g2, x, y, rad,
                    0, 0,
//                    rand.nextFloat(), rand.nextFloat()*.5f,
                    bright);
        }
        
        
    }
    
}
