package PruebaDescriptores;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import jmr.descriptor.Comparator;
import jmr.descriptor.MediaDescriptor;
/**
 *
 * @author Fernando Roldán Zafra
 */
public class GreyScaleMediaDescriptor implements MediaDescriptor<BufferedImage>, Serializable {
    public Color color;
    private transient BufferedImage source = null;
    
    public GreyScaleMediaDescriptor(Color color){
        this.color = color;
    }
    
    public GreyScaleMediaDescriptor(BufferedImage image){
        setSource(image);
    }
    
    @Override
    final public void setSource(BufferedImage image){
        this.source = image;
        init(image);
    }
    
    @Override
    final public BufferedImage getSource(){
        return this.source;
    }
    
    @Override
    public void init(BufferedImage image){
        if (image != null)
            color = grayScale(image);
        else 
            color = null;
                    
    }
    
    public Color getColor(){
        return color;
    }
    
    public Color grayScale(BufferedImage image){
        Color pixel, grayScale; 
        float meanRGB[] = {0f, 0f, 0f};
        float mean;
        double imageSize = image.getHeight()*image.getWidth();
        for(int i = 0; i < image.getWidth(); i++ ){
            for(int j = 0; j < image.getHeight(); j++){
                pixel = new Color(image.getRGB(i, j));
                meanRGB[0] += pixel.getRed();
                meanRGB[1] += pixel.getGreen();
                meanRGB[2] += pixel.getBlue();
            }
        }
        
        meanRGB[0] /= imageSize;
        meanRGB[1] /= imageSize;
        meanRGB[2] /= imageSize;
        
        mean = (meanRGB[0]+meanRGB[1]+meanRGB[2])/3;
        grayScale = new Color((int)mean,(int)mean,(int)mean);
        return grayScale;
    }
    
    @Override
    public String toString(){
        return "GrayScaleMediaDescriptor: [" + (color.getRed()/255.0)+"]";
    }
    
    @Override
    public Double compare(MediaDescriptor mediaDescriptor){
        if(!(mediaDescriptor instanceof GreyScaleMediaDescriptor))
            return(null);
        return (compare((GreyScaleMediaDescriptor) mediaDescriptor));
    }
    
    public Double compare(GreyScaleMediaDescriptor t, GreyScaleMediaDescriptor u){
        Color c1 = t.color, c2 = u.color;
        double Dif = c1.getRed()-c2.getRed();
        return Dif;

    }
}
