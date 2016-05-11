package util;

import java.awt.Color;
import java.util.Arrays;

import planet.Planet;

public class ColorGrad
{
    int[] colors;
    
    float[] points;

    int l;
    public ColorGrad(int[] colors, float[] pts)
    {
        super();
        
        if (colors.length != pts.length)
            throw new IllegalArgumentException("Arrays not same len");
        
        this.colors = colors;
        this.points = Arrays.copyOf(pts, pts.length);
        
        l = points.length;
        for (int i = 1; i < l; i++)
        {
            if (points[i] < points[i-1])
            {
                points[i] = points[i-1];
            }
            
        }
        
        for (int i = 1; i < l; i++)
        {
            points[i] /= points[l - 1];
        }
        
    }
    
    public int get(float pt)
    {
        int i = 0;
        for (; i < l; i++)
        {
            if (pt < points[i])
                break;
        }
        
        if (i == 0)
            return colors[0];
        
        if (i == l)
            return colors[l-1];
        
        float df = points[i] - points[i-1];// TODO != 0
        float frac = (pt - points[i-1])/df;
        
//        float iFrac = 1-frac;
        return Planet.blend(colors[i-1], colors[i], frac);
    }
    
}
