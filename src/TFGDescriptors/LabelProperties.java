package TFGDescriptors;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jmr.descriptor.MediaDescriptorAdapter;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.Comparator;
import jmrModified.DescriptorList;
import jmr.descriptor.MediaDescriptorFactory;

import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.descriptor.label.Classifier;
import jmr.descriptor.label.LabelDescriptor;
import jmr.descriptor.label.LabelDescriptor.WeightBasedComparator;
import jmrModified.DescriptorList.WeightedComparator;
import jmr.descriptor.label.LabelDescriptor.EqualComparator;
import jmr.descriptor.label.LabelDescriptor.SoftEqualComparator;
import jmr.descriptor.label.LabeledClassification;


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
    protected LabelDescriptor label = null; 
    
    /**
     * List of types of the properties that represent the media
     */
    private Class classProperties[] = null;
    
    /**
     * List of properties
     */
    protected DescriptorList properties = null;
    
    private Classifier<BufferedImage,? extends LabeledClassification> classifier = null; 
    
    private static Class DefaultProperties[] = {MPEG7ColorStructure.class, MPEG7ScalableColor.class};
    
    private static Comparator DEFAULT_COMPARATOR = new DefaultComparator();
    
    public LabelProperties (BufferedImage image){
        super(image, new DefaultComparator());
        this.classProperties = DefaultProperties;
        this.init(image);
    }
    
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
    
    public LabelProperties (BufferedImage image, Classifier<BufferedImage,? extends LabeledClassification> classifier){
        super(image, new DefaultComparator());
        this.classProperties = DefaultProperties;
        this.classifier = classifier;
        this.init(image);
    }
   
    public LabelProperties (BufferedImage image, Classifier<BufferedImage,? extends LabeledClassification> classifier, Class<? extends MediaDescriptor>... classProperties ){
        super(image, new DefaultComparator());
        this.classProperties = classProperties;
        this.classifier = classifier;
        this.init(image);
    }
    /**
     *
     * @param image
     */
    @Override
    public void init(BufferedImage image){
        if(classProperties != null){
            if(this.classifier == null){
                this.label = new LabelDescriptor(image);
            }
            else{
                this.label = new LabelDescriptor(image, classifier);
            }
            
            this.properties = new DescriptorList(this.source);
            WeightedComparator c = new WeightedComparator();
            properties.setComparator(c);
            
            this.initProperties();
            
        }
        else{
            this.properties = null;
            this.label=null;
        }
    }

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
    
    public static void setDefaultProperties(Class<? extends MediaDescriptor>... classProperties){
        if(classProperties != null){
            DefaultProperties = classProperties;
        }
    }
    
    public void setWeightComparator(){
        if(this.label.isWeighted()){
            WeightBasedComparator comparator = new WeightBasedComparator();
            this.label.setComparator(comparator);
        }else
            System.out.println("Invalid operation, you can´t set a weightBasedComparator to a non-weight descriptor");
    }
    
    public void setWeightComparator(int type){
        if(this.label.isWeighted()){
            WeightBasedComparator comparator = new WeightBasedComparator(type);
            this.label.setComparator(comparator);
        }else
            System.out.println("Invalid operation, you can´t set a weightBasedComparator to a non-weight descriptor");
    }
    
    public void setWeightComparator(int type, boolean only_inclusion){
        if(this.label.isWeighted()){
            WeightBasedComparator comparator = new WeightBasedComparator(type,only_inclusion);
            this.label.setComparator(comparator);
        }else
            System.out.println("Invalid operation, you can´t set a weightBasedComparator to a non-weight descriptor");
    }
    
    public void setClassifier(Classifier classifier){
        this.label.setClassifier(classifier);
    }
    
    
    public LabelDescriptor getLabel(){
        return label;
    } 
    
    public void addProperty(Class <? extends MediaDescriptor>... classProperties){
        MediaDescriptor descriptor;
        for (Class c : classProperties){
            descriptor = MediaDescriptorFactory.getInstance(c, this.source);
            this.properties.add(descriptor);
        }
    }
    
    public MediaDescriptor getProperty(int index){
        return properties.get(index);
    }
    
    public int SizeProperties(){
        return properties.size();
    }
    
    
    
    
    
    @Override
    public String toString(){
        return (this.label.toString() + "\n" + this.properties.toString());
    }
    
    static class DefaultComparator implements Comparator <LabelProperties, Double> {
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            WeightedComparator c = new WeightedComparator();
            t.properties.setComparator(c);
            double distProperties = (double) t.properties.compare(u.properties);
            double dist = distProperties;
            
            SoftEqualComparator comp = new SoftEqualComparator();
            t.label.setComparator(comp);
            dist += (double) t.label.compare(u.label);
            
            return dist;

        }
    }
    
    static public class WeightedLabelComparator implements Comparator <LabelProperties, Double> {
        double PropertyWeights[] = null;
        int type = 4;
        boolean only_inclusion = false;

        public WeightedLabelComparator (int type, boolean only_inclusion, double... weights){
            if(weights.length != 0)
                this.PropertyWeights = weights;
            this.type = type;
            this.only_inclusion = only_inclusion;
        }
        
        public WeightedLabelComparator (int type, double... weights){
            if(weights.length != 0)
                this.PropertyWeights = weights;
            this.type = type;
        }
        
        public WeightedLabelComparator (double... weights){
            if(weights.length != 0)
                this.PropertyWeights = weights;
        }
        
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            WeightedComparator c = new WeightedComparator(this.PropertyWeights);
            t.properties.setComparator(c);
            
            WeightBasedComparator comp = new WeightBasedComparator(type, only_inclusion);
            t.label.setComparator(comp);
            double dist = (double) t.label.compare(u.label);
            dist += (double) t.properties.compare(u.properties);
            return dist;
        }
    }
    
    /**
     * Compara las propiedades usando los pesos de cada uno de ellas, utiliza el softInclusionComparator para etiquetas
     * (Si alguna etiqueta esta incluida en el otro descriptor devuelve 0, si no 1)
     * 
     */
    static public class WeightedPropertiesComparator implements Comparator <LabelProperties, Double>{
        double weights[] = null;
        public WeightedPropertiesComparator(double... weights){
            if(weights.length != 0)
                this.weights = weights;
        }
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            WeightedComparator c = new WeightedComparator(weights);
            t.properties.setComparator(c);
            
            SoftEqualComparator comp = new SoftEqualComparator();
            t.label.setComparator(comp);
            double dist = (double) t.label.compare(u.label);
            dist += (double) t.properties.compare(u.properties);
            return dist;
        }

    }
    
    /**
     * Si las etiquetas son iguales compara las propiedades, si no no
     */
    static public class EqualLabelsComparator implements Comparator <LabelProperties, Double>{
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            EqualComparator c = new EqualComparator();
            t.label.setComparator(c);
            double dist = (double) t.label.compare(u.label);
            if(dist == 0){
                dist = (double) t.properties.compare(u.properties);
            }
            return dist;
        }
    }
}
