package planet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import galaxy.NebulaStormGalaxyGfx;

public class Craters
{
    public static void main(String[] args)
    {
        final int W = 800;
        Random rand = new Random();
        BufferedImage im = new BufferedImage(W, W, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, W, W);
        
        g2.setColor(Color.white);
        
        double delta = Math.PI * 2 / 40;
        
        for (double a = 0; a < Math.PI * 2; a += delta)
        {
            double c = Math.cos(a);
            double s = Math.sin(a);
            double l1 = 20 + rand.nextDouble() * 5;
            double l2 = 100 + rand.nextDouble() * 200;
            g2.drawLine(
                    (int)(W/2 + l1 * c),
                    (int)(W/2 + l1 * s),
                    (int)(W/2 + l2 * c),
                    (int)(W/2 + l2 * s)
                    );
        }
        
        NebulaStormGalaxyGfx.showImage(im);
    }
}
