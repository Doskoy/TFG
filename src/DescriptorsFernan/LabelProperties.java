package DescriptorsFernan;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import jmr.descriptor.label.LabelDescriptor;
import jmr.descriptor.MediaDescriptorAdapter;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.Comparator;
import jmr.descriptor.DescriptorList;
import jmr.descriptor.MediaDescriptorFactory;
import jmr.descriptor.label.LabelDescriptor.WeightBasedComparator;


/**
 * This class is a descriptor of an image wich is compone
 * by a list of labels and a list of properties specified at the moment
 * of it creation
 * 
 * @param <BufferedImage> The media described by this descriptor
 * @author Fernando
 */

public class LabelProperties extends MediaDescriptorAdapter<BufferedImage> implements Serializable{
    /**
     * List of labels associated to the media
     */
    private LabelDescriptor label = null; 
    
    /**
     * List of types of the properties that represent the media
     */
    private Class classProperties[] = null;
    
    /**
     * List of properties
     */
    private DescriptorList properties = null;
    
    /**
     * Contruct the descriptor with the default comparator
     * @param image The media to be descripted
     * @param classProperties An undeterminate number of arguments of 
     * undeterminate types
     */
    
    public LabelProperties (BufferedImage image, Class<? extends MediaDescriptor>... classProperties){
        super(image, new DefaultComparator());
        this.classProperties = classProperties;
        this.init(image);
    }
    
    /**
     *
     * @param image
     */
    @Override
    public void init(BufferedImage image){
        
        this.properties = new DescriptorList(image);
        this.label = new LabelDescriptor(image);
        this.initProperties();
    }
    
    /*
    @Override
    public void init(BufferedImage image){
        this.properties = new DescriptorList(image);
        this.etiqueta = new LabelDescriptor(image);
        this.initProperties();
    }
    */
    /**
     * Initialize and calculate all the properties of the descriptor
     */
    private void initProperties(){
        MediaDescriptor descriptor;
        for (Class c : this.classProperties){
            descriptor = MediaDescriptorFactory.getInstance(c, this.source);
            this.properties.add(descriptor);
        }
    }
    
    @Override
    public String toString(){
        return (this.label.toString() + this.properties.toString());
    }
    
    static class DefaultComparator implements Comparator <LabelProperties, Double> {
        /**
         * Compara dos objetos de la clase LabelProperties, define el comparador 
         * de las etiquetas como el "WeightBasedComparator" con el modo "media" 
         * y con solo inclusion de las etiquetas.
         * @param t
         * @param u
         * @return 
         */
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            //WeightBasedComparator comparador = new WeightBasedComparator(3,true);
            //t.label.setComparator(comparador);
            return 0.0;

        }
    }

}
    
    
    
    
     
     
     
     
     
     

