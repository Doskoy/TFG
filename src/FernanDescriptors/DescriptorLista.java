package FernanDescriptors;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import jmr.descriptor.Comparator;
import jmr.descriptor.MediaDescriptorAdapter;
import jmr.descriptor.DescriptorList;
import jmr.descriptor.label.LabelDescriptor;
import jmr.descriptor.color.MPEG7ScalableColor;

/**
 *
 * @author Fernando Roldán Zafra
 */
public class DescriptorLista extends MediaDescriptorAdapter <BufferedImage> implements Serializable{
    DescriptorList lista;

    public DescriptorLista (BufferedImage image){
        super(image, new DefaultComparator());
        
    }
    
    public DescriptorLista (LabelDescriptor etiq, BufferedImage image){
        super(image, new DefaultComparator());
        this.lista.set(0, etiq);
    }
    
    @Override
    public void init(BufferedImage image){
        this.lista = new DescriptorList(image);
        
        LabelDescriptor etiq = new LabelDescriptor(image);
        lista.add(etiq);
        
        MPEG7ScalableColor hist = new MPEG7ScalableColor(image);
        lista.add(hist);
    }
    
    public LabelDescriptor getEtiqueta(){
        LabelDescriptor etiq = (LabelDescriptor) this.lista.get(0);
        return etiq;
    }
    
    public MPEG7ScalableColor getHistograma(){
        return (MPEG7ScalableColor) this.lista.get(1);
    }
    
    @Override
    public String toString(){
        return "DescriptorLista: [" + lista.get(0).toString() + " " + lista.get(1).toString() + "]";
    }
    
    static class DefaultComparator implements Comparator <DescriptorLista, Double> {
    
        @Override
        public Double apply(DescriptorLista t, DescriptorLista u){
            LabelDescriptor etiq1 = t.getEtiqueta();
            LabelDescriptor etiq2 = u.getEtiqueta();

            MPEG7ScalableColor hist1 = t.getHistograma();
            MPEG7ScalableColor hist2 = u.getHistograma();
            
            double Dif;

            if(etiq1 != etiq2)
                Dif = Double.POSITIVE_INFINITY;
            else
                Dif = hist1.compare(hist2);
            
            return Dif;
        }
    }
}
