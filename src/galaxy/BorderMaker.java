package galaxy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BorderMaker
{
    final int W, H;
    Random rand;
    public BorderMaker(int w, int h, Random rand)
    {
        super();
        W = w;
        H = h;
        this.rand = rand;
    }
    
    public BufferedImage make()
    {
        BufferedImage im = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        
        g2.setColor(new Color(0, true));
        g2.fillRect(0, 0, W, H);
        
        g2.setColor(Color.white);
        g2.drawLine(0, 0, 100, 100);
        
        return im;
    }
    
    public static void main(String[] args)
    {
        BorderMaker b = new BorderMaker(600, 800, new Random());
        BufferedImage im = b.make();
        
        NebulaStormGalaxyGfx.showImage(im);
    }
}
