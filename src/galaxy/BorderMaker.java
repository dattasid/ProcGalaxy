package galaxy;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
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
        
        int border = W/10;
        int border1 = W/12;
//        int diff = border - border1;
        LinearGradientPaint p = new LinearGradientPaint(0, 0, W, H, new float[]{0, 1},
                new Color[]{
                    new Color(Color.HSBtoRGB(rand.nextFloat(), 1, .9f)),
                    new Color(Color.HSBtoRGB(rand.nextFloat(), 1, .9f))
        });
        g2.setPaint(p);
        
        g2.setStroke(new BasicStroke(1));
        
        g2.drawRect(border, border, W-2*border, H-2*border);
        
        g2.setStroke(new BasicStroke(3));
        
        g2.drawRect(border1, border1, W-2*border1, H-2*border1);
        
        g2.setClip(border1, border1, W-2*border1, H-2*border1);
        
        int r = W/6;
        makeCircles(g2, border, border, r, rand);
        makeCircles(g2, border+r/2+rand.nextInt(r/2),
                border+r/2+rand.nextInt(r/2), r/4, rand);
        
        
        g2.setClip(null);
        
        g2.setColor(Color.white);
        
//        makeStarryLines(g2, border, border, border, H-border, rand);
//        makeStarryLines(g2, border, border, border, H-border, rand);
        
        return im;
    }
    
    static class P
    {
        public int x, y;

        public P(int x, int y)
        {
            super();
            this.x = x;
            this.y = y;
        }
    }
    
    private static void makeStarryLines(Graphics2D g2, int x1, int y1, int x2,
            int y2, Random rand)
    {
        
        g2 = (Graphics2D) g2.create();
        g2.setComposite(AlphaComposite.SrcOver);
        int dx = x2 - x1;
        int dy = y2 - y1;
        
        
        
        int r = (int) Math.sqrt(dx*dx+dy*dy);
        
        int n = 3 + rand.nextInt(3);
        P rr[]  = new P[n];
        for (int i = 0; i < n; i++)
        {
            rr[i] = new P(0, 0);
            rr[i].x = rand.nextInt(r);
//            int yrange = Math.min(rr[i].x, r - rr[i].x)/6;
            int yrange = 30;
            rr[i].y = (rand.nextBoolean()?-1:1) * rand.nextInt(yrange);
        }
        
        Arrays.sort(rr, new Comparator<P>()
        {

            @Override
            public int compare(P o1, P o2)
            {
                return o1.x - o2.x;
            }
        });
        
        int x = x1, y = y1;
        for (int i = 0; i < n; i++)
        {
            int xx = x1 + rr[i].x * dx/r + rr[i].y * -dy/r;
            int yy = x1 + rr[i].x * dy/r + rr[i].y * dx/r;
            
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.white);
            g2.drawLine(x, y, xx, yy);
            
            NebulaStormGalaxyGfx.drawStar(g2, xx, yy, 20, 0, 0, .65f);
            NebulaStormGalaxyGfx.drawStar(g2, xx, yy, 5, 0, 0, .95f);
            
            x=xx; y=yy;
        }
        g2.setColor(Color.white);
        g2.drawLine(x, y, x2, y2);
        
        g2.dispose();
    }
    
    private static void makeCircles(Graphics2D g2, int x, int y, int r,
            Random rand)
    {
        int n = 1 + rand.nextInt(4);

        for (int i = 0; i < n; i++)
        {
            if (rand.nextInt(10) < 4)
            {
                g2.setColor(new Color(0, true));
                g2.fillOval(x - r, y - r, 2 * r, 2 * r);
            }
            
            g2.setStroke(new BasicStroke(1 + rand.nextFloat() * 2));
            g2.setColor(Color.white);
            g2.drawOval(x - r, y - r, 2 * r, 2 * r);

            r -= (10 + rand.nextInt(10));
        }
    }
    
    public static void main(String[] args)
    {
        BorderMaker b = new BorderMaker(600, 800, new Random());
        BufferedImage im = b.make();
        
        NebulaStormGalaxyGfx.showImage(im);
    }
}
