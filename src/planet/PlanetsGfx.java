package planet;

import galaxy.NebulaStormGalaxyGfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class PlanetsGfx
{

    public static void main(String[] args)
    {
        BufferedImage im = new HabitPlanet(System.currentTimeMillis()).render(400);
//        Graphics2D g2 = im.createGraphics();
//        g2.setColor(Color.green);
//        g2.fillRect(0, 0, 400, 400);
        NebulaStormGalaxyGfx.showImage(im);
    }
    public static void main2(String[] args)
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
        
        switch (rand.nextInt(2))
        {
        case 0:
            p = new HabitPlanet(System.currentTimeMillis());
            break;
        case 1:
            p = new LavaPlanet(System.currentTimeMillis());
            break;
        }
        
        p.setSunDir(sun_dir[0],  sun_dir[1], sun_dir[2]);
        
        BufferedImage pim = p.render(400);
        
        g2.drawImage(pim, W/2-200, H/2-200, null);
        
        
        switch (rand.nextInt(2))
        {
        case 0:
            p = new HabitPlanet(System.currentTimeMillis());
            break;
        case 1:
            p = new LavaPlanet(System.currentTimeMillis());
            break;
        }
        
        p.setSunDir(sun_dir[0],  sun_dir[1], sun_dir[2]);
        pim = p.render(200);
        
        g2.drawImage(pim, W/2-200, H/2-200, null);
        
        NebulaStormGalaxyGfx.showImage(im);
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
