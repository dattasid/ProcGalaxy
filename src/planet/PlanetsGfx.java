package planet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import galaxy.NebulaStormGalaxyGfx;

public class PlanetsGfx
{

    static class Args
    {
        @Option(name="-imageSize", usage="Output image size."
                + " Images are always square. Default is 800x800.")
        int imageSize = 800;
        
        @Option(name="-out", metaVar="dir/prefix",
                usage="Output file. Finds the first unused file dir/prefixN and"
                    + " writes to it. Can write -numRuns number of files"
                    + " incrementally numbered. Directory must be provided and"
                    + " must already exist.\n"
                    + " If absent, shows one image and exits.")
        String out = null;
        
        @Option(name="-numRuns", usage="Produce how many images."
                + " Without an -out option, shows 1 image and quits.")
        int numRuns = 1;
        
        @Option(name="--help", help=true, aliases={"-h", "-?", "/h", "/?"})
        boolean help;
    }
    public static void main(String[] argv)
    {
        Args args = new Args();
        CmdLineParser parser = new CmdLineParser(args,
                ParserProperties.defaults().withOptionSorter(null)
                    .withUsageWidth(80).withShowDefaults(false));
        
        boolean wasExcept = false;
        try {
            parser.parseArgument(argv);
        }
        catch (CmdLineException e)
        {
            System.out.println(e.getMessage());
            wasExcept = true;
        }

        if (wasExcept || args.help)
        {
            
            System.out.println("java -jar PlanetGfx.v1.jar [options...]");
            parser.printUsage(System.out);
            
            System.exit(args.help?0:1);
        }
        
        for (int i = 0; i < args.numRuns; i++)
        {
            BufferedImage im = generate(args);

            if (args.out == null)
            {
                NebulaStormGalaxyGfx.showImage(im);
            } else
            {
                NebulaStormGalaxyGfx.saveImage(im, args.out);
            }
        }
//        NebulaStormGalaxyGfx.showImage(im);
    }
    
//    public static void main2(String[] args)
//    {
//        for (int i = 0; i < 2; i++)
//        {
//            BufferedImage im = generate();
//            NebulaStormGalaxyGfx.saveImage(im, "out/planets");
//        }
//    }
    public static BufferedImage generate(Args args)
    {
        final int H = args.imageSize, W = H*3/4;

        BufferedImage im = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = im.createGraphics();
        
        Random rand = new Random();
        
        g2.setColor(Color.black);
        g2.fillRect(0, 0, W, H);
        
        drawStars(g2, rand, W, H);
        
        float[] sun_dir = { rand.nextFloat(), rand.nextFloat(), rand.nextFloat()};
        Planet.norm_vec(sun_dir);
        
        // line
//        sun_dir = new float[]{1, 0, .2f};
//        int r = 100;
//        int x1 = W - r;
//        int dx1 = 150;
//        for (int i = 0; i < 5; i++)
//        {
//            makePlanet(g2, W, H, r, x1, H/2-r/2, rand, sun_dir);
//            r = (int) (r*1.55);
//            x1 -= dx1;
//            dx1 += 60;
//        }
        
        // grid
        sun_dir = new float[]{0, 0, 1f};
        for (int x = 0; x < W; x+=W/5)
            for (int y = 0; y < H; y+=H/5)
                makePlanet(g2, W, H, Math.min(W/5-10, H/5-10), x, y, rand, sun_dir);
        
//        makePlanet(g2, W, H, 400, W/2-200, H/2-200, rand, sun_dir);
//        makePlanetRecur(g2, W, H, 400, W/2-200, H/2-200, rand, sun_dir, 0);
        
//        int N = rand.nextInt(5);
//        for (int i = 0; i < N; i++)
//        {
//            int dia = rand.nextInt(100) + 50;
//            
//            int x = rand.nextInt(W *3/4) + W/2/4;
//            int y = rand.nextInt(H *3/4) + H/2/4;
//            
//            makePlanet(g2, W, H, dia, x, y, rand, sun_dir);
//        }

        return im;
        
//        NebulaStormGalaxyGfx.showImage(im);
    }
    private static void makePlanetRecur(Graphics2D g2, final int W, final int H,
            int diameter, int x, int y, Random rand, float[] sun_dir, int level)
    {
        makePlanet(g2, W, H, diameter, x, y, rand, sun_dir);
        
        if (level > 1)
            return;
        int numSat = rand.nextInt(3);
        ArrayList<Integer> prevAngle = new ArrayList<Integer>();
        for (int i = 0; i < numSat; i++)
        {
            int an = rand.nextInt(8);
            while(prevAngle.contains(an))
                an = rand.nextInt(8);
            
            prevAngle.add(an);
            
            double th = Math.PI * 2 *an / 8;
            int satx = (int) (x+(diameter/2) * (1+Math.cos(th))); 
            int saty = (int) (y+(diameter/2) * (1+Math.sin(th)));
            
            int satdia = (int) (diameter * (.2 + rand.nextDouble() * .4));
            
            makePlanetRecur(g2, W, H, satdia, satx-satdia/2, saty-satdia/2, rand, sun_dir, level+1);
        }
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
