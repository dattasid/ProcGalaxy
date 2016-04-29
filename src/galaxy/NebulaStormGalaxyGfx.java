package galaxy;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.sun.prism.paint.LinearGradient;

public class NebulaStormGalaxyGfx
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
        
        @Option(name="-numStarsInThou", usage="Number of stars in thousands."
                + " Default 20k.")
        int numStarsInThou = 20;
        
        @Option(name="-numArms", usage="Number of arms of the galaxy."
                + " Default random between 2-6.")
        int arms = -1;
        @Option(name="-maxArms", usage="Maximum number of randomly chosen arms."
                + " Minimum is always 2. If -numArms is specified, that value"
                + " is always used. Default 6.")
        int maxArms = 6;
        
        
        @Option(name="-swirl", usage="Angular spread, in multiples of Pi."
                + " Default random  -3 to -1 or +1 to +3. ")
        float swirlyAngularSpread = Float.MIN_VALUE;
        
        @Option(name="-armsWeight", usage="Low values keep stars close to disk,"
                + " high values push them out to the arms. Default .1 to .4.")
        float armWeight = Float.MIN_VALUE;
        
        @Option(name="-armsThickness", usage="Low values make thinner arms,"
                + " high values make thicker arms and bigger central disk."
                + " Default values .05 to .15.")
        float armThickness = Float.MIN_VALUE;
        
        @Option(name="-avgStarRad", usage="Average star radius in pixels. Default 2.5."
                + " Star radius is chosen on a gaussian distribution.")
        float avgStarRad = 2.5f;
        
        @Option(name="-maxStarRad", usage="Maximum star radius in pixels allowed. Default 10.")
        float maxStarRad = 10;
        
        @Option(name="-hueStart", usage="Color of the center of the galaxy. Range 0.0 to 1.0.")
        float hueStart = Float.MIN_VALUE;
        
        @Option(name="-hueChangeLong", usage="Rate of color change center towards arms. Default random -2.0 to 2.0")
        float hueChangeAlongArm = Float.MIN_VALUE;
        
        @Option(name="-hueChangeTrans", usage="Rate of color change from center of arms towards sides. Default -0.2 + +0.2"
                + " Default random -1.0 to 1.0")
        float hueChangeTransverseArm = Float.MIN_VALUE;
        
        @Option(name="--help", help=true, aliases={"-h", "-?", "/h", "/?"})
        boolean help;
        
        void sanitize()
        {
            if (imageSize <= 0)
                imageSize = 100;
            
            if (arms < 2)
                arms = 2;
            
            if (maxArms < 2)
            {
                maxArms = 2;
                if (maxArms <= 2)
                    arms = 2;
            }
            
            if (numStarsInThou < 0)
                numStarsInThou = 0;
            
            if (avgStarRad < 1)
                avgStarRad = 1;
            avgStarRad--;
            
            if (maxStarRad < avgStarRad+1)
                maxStarRad = avgStarRad+1;
            
        }
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
            
            System.out.println("java -jar prettygalaxy.v1.jar [options...]");
            parser.printUsage(System.out);
            
            System.exit(args.help?0:1);
        }
        
        args.sanitize();
        
        for (int i = 0; i < args.numRuns; i++)
        {
            generate(args);
            
            if (args.out == null)
                break;
        }
    }

    private static void generate(Args args)
    {
        Random rand = new Random();
        
        final int W = args.imageSize*3/4, H = args.imageSize;
       
        BufferedImage im = new BufferedImage(W,  H,
                                             BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = im.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.black);
        g2.fillRect(0, 0, W, H);
        
        int cx = W/2, cy = H/2;
        
        //The arm extends to the edge of the image.
        // Note: Since we are using a gaussian distribution, it is not
        // guaranteed that the edge will always reach, or stop at, edge of the
        // image. Armweight will also play a role.
        int armlen = Math.min(W, H)/2 ; 
        
        int arms = args.arms;
        if (arms == -1)
            arms = rand.nextInt(args.maxArms - 2) + 2;
        
        double armdTheta = Math.PI * 2/ arms;
        
        float swirlyAngularSpread = args.swirlyAngularSpread;
        if (swirlyAngularSpread == Float.MIN_VALUE)
            swirlyAngularSpread = (rand.nextFloat()*2 + 1) * 
                        (rand.nextBoolean()?-1:1);
            
        float armThickness = args.armThickness;
        if (armThickness == Float.MIN_VALUE)
            armThickness = rand.nextFloat()*.1f + .05f;
        
        float armsWeight = args.armWeight;
        if (armsWeight == Float.MIN_VALUE)
            armsWeight = rand.nextFloat() * .3f + .1f;
        
        float hueStart = args.hueStart;
        if (hueStart == Float.MIN_VALUE)
            hueStart = rand.nextFloat();
        
        float hueAlongArm = args.hueChangeAlongArm;
        if (hueAlongArm == Float.MIN_VALUE)
            hueAlongArm = (rand.nextFloat() * .5f + .5f) * (rand.nextBoolean()?-1:1);
        
        float hueThickArm = args.hueChangeTransverseArm;
        if (hueThickArm == Float.MIN_VALUE)
            hueThickArm = (rand.nextFloat() * .1f + .1f) * (rand.nextBoolean()?-1:1);
        
        arms = 2;
//        swirlyAngularSpread = 0;
        swirlyAngularSpread = 3;
        armsWeight = .6f;
        armThickness = .02f;
        args.numStarsInThou = 20;
        
        final float SPIRAL_END = -1f; //???? HOW DOES THIS WORK ??
        
        for (int i = 0; i < args.numStarsInThou * 1000; i++)
        {
            // Pick a point in a oval distribution. Use -swirly 0 to see how this works.
//            int x = rand.nextInt(armlen);
            double x = Math.abs(rand.nextGaussian()) * armlen * armsWeight;
//            int y = rand.nextInt(armlen/10);
            double y = rand.nextGaussian() * armlen * armThickness;
        
            double org_y = y;
            
            y *= (Math.pow(1.1, x*15/armlen) - 1 + .5)*6;
            
            // Chose an arm
            int arm = rand.nextInt(arms);
            
            double r = Math.sqrt(x*x+y*y);
            
            double alph = r/(armlen*(1-SPIRAL_END));
            alph = 1- Math.pow((1-alph), 10);
            
            if (alph > 1)
                alph = 1;
            
            // Rotate the star based on how far it is from the center, and also 
            // which arm it is in.
            double dTh = swirlyAngularSpread * alph;
            
            // rotate by 90
            dTh += .5;
            
            dTh *= Math.PI;
            
            dTh += arm * armdTheta;
            
            double sin = Math.sin(dTh);
            double cos = Math.cos(dTh);
            double x1 = x * cos - y * sin;
            double y1 = y * cos + x * sin;
            
            x1 += cx;
            y1 += cy;
            
            float hue = (float) (hueStart + x * hueAlongArm/armlen + Math.abs(org_y) * hueThickArm/armlen/armThickness/*+ rand.nextFloat() * .3f*/);
            int rad = (int) (Math.abs(rand.nextGaussian()) * args.avgStarRad)+1;
            if (rad > args.maxStarRad)
                rad = (int) args.maxStarRad;
            float bright = .6f;
            if (rad > 2*args.avgStarRad)
                bright = .9f;
            
//            bright = rand.nextFloat();

            if (x1 < 0 || y1 < 0 || x1 > W || y1 > H)
                continue;
            
            drawStar(g2, x1, y1, rad, hue, bright);
        }
        
        makeBorder(g2, W, H, rand);
        
        if (args.out == null)
        {
            showImage(im);
        }
        else
        {
            saveImage(im, args.out);
        }
    }
    
        
    public static void saveImage(BufferedImage im, String name)
    {
        int i = 0;
        for (;i < 1000; i++)
        {
            File f = new File(name+i+".png");
            if (f.exists())
                continue;
            
            try
            {
                ImageIO.write(im, "png", f);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            
            break;
        }
        
        if (i == 1000)
           System.err.println("Could not write image, already 1000 images with"
                   + " the same prefix exist.");
    }

    // WARNING: This needs to be thread local for the code to be multithreaded
    private static Color[] gCol = new Color[]{null, new Color(0, true)};
    private static final float[] zeroOne = new float[]{0, 1};
    private static void drawStar(Graphics2D g2, double x, double y, int rad, float hue, float bright)
    {
        int rgb = Color.HSBtoRGB(hue, .9f, bright);
        gCol[0] = new Color(rgb);
        RadialGradientPaint p = new RadialGradientPaint((float)x, (float)y, rad, zeroOne, gCol);
        
//        g2.setColor(new Color(rgb));
        g2.setPaint(p);
        g2.fillOval(((int)x)-rad, ((int)y)-rad, 2*rad+1, 2*rad+1);
    }
    
    public static void showImage(BufferedImage im)
    {
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().add(new JLabel(new ImageIcon(im)));
        jf.getContentPane().setBackground(Color.black);
        jf.pack();
        jf.setVisible(true);
        
        try
        {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e)
        {
        }
        
        System.exit(0);
    }
    
    private static void makeBorder(Graphics2D g2, int w, int h, Random rand)
    {
        int border = w/10;
        int border1 = w/12;
//        int diff = border - border1;
        LinearGradientPaint p = new LinearGradientPaint(0, 0, w, h, new float[]{0, 1},
                new Color[]{
                    new Color(Color.HSBtoRGB(rand.nextFloat(), 1, .9f)),
                    new Color(Color.HSBtoRGB(rand.nextFloat(), 1, .9f))
        });
        g2.setPaint(p);
        
        g2.setStroke(new BasicStroke(1));
        
        g2.drawRect(border, border, w-2*border, h-2*border);
        
        g2.setStroke(new BasicStroke(3));
        
        g2.drawRect(border1, border1, w-2*border1, h-2*border1);
        
        g2.setClip(border1, border1, w-2*border1, h-2*border1);
        
        int r = w/6;
        makeCircles(g2, border+40, border+40, r, rand);
        makeCircles(g2, border+r/2+rand.nextInt(r/2),
                border+r/2+rand.nextInt(r/2), r/4, rand);
    }

    private static void makeCircles(Graphics2D g2, int x, int y, int r,
                                    Random rand)
    {
        int n = 1+ rand.nextInt(4);
        
        for (int i = 0; i < n; i++)
        {
            g2.setStroke(new BasicStroke(1+rand.nextFloat() * 2));
            g2.drawOval(x-r, y-r, 2*r, 2*r);
            
            r -= (10 + rand.nextInt(10));
        }
    }


}
