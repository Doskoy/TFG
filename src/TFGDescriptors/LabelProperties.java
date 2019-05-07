package TFGDescriptors;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.security.InvalidParameterException;
import jmr.descriptor.MediaDescriptorAdapter;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.Comparator;
import jmr.descriptor.DescriptorList;
import jmr.descriptor.MediaDescriptorFactory;

import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.descriptor.label.Classifier;
import jmr.descriptor.label.LabelDescriptor;
import jmr.descriptor.label.LabelDescriptor.WeightBasedComparator;
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
        super(image, DEFAULT_COMPARATOR);
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
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = classProperties;
        this.init(image);
    }
    
    public LabelProperties (BufferedImage image, Classifier<BufferedImage,? extends LabeledClassification> classifier){
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = DefaultProperties;
        this.classifier = classifier;
        this.init(image);
    }
   
    public LabelProperties (BufferedImage image, Classifier<BufferedImage,? extends LabeledClassification> classifier, Class<? extends MediaDescriptor>... classProperties ){
        super(image, DEFAULT_COMPARATOR);
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
            WeightedPropertiesComparator c = new WeightedPropertiesComparator();
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
    
    public DescriptorList getProperties(){
        return properties;
    }
    
    @Override
    public String toString(){
        return (this.label.toString() + "\n" + this.properties.toString());
    }

    static class DefaultComparator implements Comparator <LabelProperties, Double> {
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            WeightedPropertiesComparator c = new WeightedPropertiesComparator();
            t.properties.setComparator(c);
            double distProperties = (double) t.properties.compare(u.properties);
            double dist = distProperties;
            
            dist += (double) t.label.compare(u.label);
            System.out.println("Hola?");
            return dist;

        }
    }
    
    static public class WeightedLabelComparator implements Comparator <LabelProperties, Double> {
        double PropertyWeights[] = null;
        int type = 4;
        boolean only_inclusion = false;

        public WeightedLabelComparator (int type, boolean only_inclusion, double... weights){
            if(weights != null)
                this.PropertyWeights = weights;
            this.type = type;
            this.only_inclusion = only_inclusion;
        }
        
        public WeightedLabelComparator (int type, double... weights){
            if(weights != null)
                this.PropertyWeights = weights;
            this.type = type;
        }
        
        public WeightedLabelComparator (double... weights){
            if(weights != null)
                this.PropertyWeights = weights;
        }
        
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            if (!t.label.isWeighted() || !u.label.isWeighted()){
                throw new InvalidParameterException("Labels must be weighted!!");
            }else{
            WeightedPropertiesComparator c = new WeightedPropertiesComparator(this.PropertyWeights);
            t.properties.setComparator(c);
            
            WeightBasedComparator comp = new WeightBasedComparator(type, only_inclusion);
            t.label.setComparator(comp);
            double dist = (double) t.label.compare(u.label);
            dist += (double) t.properties.compare(u.properties);
            return dist;
            }
            
        }
    }
    
    /**
     * Compara las propiedades usando los pesos de cada uno de ellas, utiliza el softInclusionComparator para etiquetas
     * (Si alguna etiqueta esta incluida en el otro descriptor devuelve 0, si no 1)
     * 
     */
    /*
    static public class WeightedPropertiesComparator implements Comparator <LabelProperties, Double>{
        double weights[] = null;
        public WeightedPropertiesComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            WeightedPropertiesComparator c = null;
            if(this.weights == null){
                c = new WeightedPropertiesComparator();
            }else{
                c = new WeightedPropertiesComparator(weights);
            }
            t.properties.setComparator(c);
            
            SoftEqualComparator comp = new SoftEqualComparator();
            t.label.setComparator(comp);
            double dist = (double) t.label.compare(u.label);
            dist += (double) t.properties.compare(u.properties);
            return dist;
        }

    }
    */
    /**
     * Si las etiquetas son iguales compara las propiedades, si no no
     */
    static public class EqualLabelsComparator implements Comparator <LabelProperties, Double>{
        double weights[] = null;
        public EqualLabelsComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }
        
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            EqualComparator c = new EqualComparator();
            t.label.setComparator(c);
            double dist = (double) t.label.compare(u.label);
            
            if(dist == 0){
                WeightedPropertiesComparator comp = null;
                if(this.weights == null){
                    comp = new WeightedPropertiesComparator();
                }else{
                    comp = new WeightedPropertiesComparator(weights);
                }
                t.properties.setComparator(comp);
                dist = (double) t.properties.compare(u.properties);
            }else{
                dist = Double.POSITIVE_INFINITY;
            }
                
            return dist;
        }
    }
    
    static public class SoftEqualLabelsComparator implements Comparator <LabelProperties, Double>{
        double weights[] = null;
        public SoftEqualLabelsComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }
        
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            SoftEqualComparator c = new SoftEqualComparator();
            t.label.setComparator(c);
            double dist = (double) t.label.compare(u.label);
            if(dist == 0){
                WeightedPropertiesComparator comp = null;
                if(this.weights == null){
                    comp = new WeightedPropertiesComparator();
                }else{
                    comp = new WeightedPropertiesComparator(weights);
                }
                t.properties.setComparator(comp);
                dist = (double) t.properties.compare(u.properties);
            }else{
                dist = Double.POSITIVE_INFINITY;
            }
            
            return dist;
        }
    }
    
    /**
     * If both comparators have any common label, it compare the properties otherwise 
     * it returns 1
     * @param weights Weights that are going to be used to compare the properties
     * @return 1 if there'nt any common label, 0 if they have a common label
     */
    static public class SoftEqualComparator implements Comparator <LabelProperties, Double>{
        double weights[] = null;
        
        public SoftEqualComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            double dist = 0.0;
            if(t.label.isSoftIncluded(u.label)){
                WeightedPropertiesComparator c = null;
                if(this.weights == null){
                    c = new WeightedPropertiesComparator();
                }else{
                    c = new WeightedPropertiesComparator(weights);
                }
                t.properties.setComparator(c);
                dist = (double) t.properties.compare(u.properties);
            }else
                dist = Double.POSITIVE_INFINITY;
            return dist;
        
        }
    
    }
    
    private static class WeightedPropertiesComparator implements Comparator <DescriptorList, Double> {
        private double weights[] = null;
        public WeightedPropertiesComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }

        @Override
        public Double apply(DescriptorList t, DescriptorList u){
            if(weights != null){
                if(weights.length != t.size()){
                    throw new InvalidParameterException("They must be the same number of weights than descriptors");
                }
            }
            
            
            if(this.weights != null){
                double sum = 0;
                for(int i = 0; i < this.weights.length; i++)
                    sum += weights[i];

                if (sum != 1)
                    throw new InvalidParameterException("The sum of weights must be 1");
            }
            
            double dist = 0.0;
            double distProperty = 0.0;                                                              //distancia de una propiedad concreta
            int numDescriptors = 0;
            
            for (int i=0 ; i < t.size(); i++){                                                      //Itero sobre ella buscando cuales son las propiedades en comun con la otra lista
                for (int j=0; j < u.size(); j++){
                    if(t.get(i).getClass().equals(u.get(j).getClass())){                            //Busco si los nombres de las propiedades coinciden
                        numDescriptors++;
                        if(t.get(i).getClass() == jmr.descriptor.color.SingleColorDescriptor.class){ //Si coinciden, compruebo que propiedad es, 
                            distProperty = (double) t.get(i).compare(u.get(j));                     //en el caso de singlecolor le asigno poco peso poco peso 
                            if(distProperty > 120)                                                  //Le asigno el valor maximo para la normalización
                                distProperty = 120;
                            if(weights != null){
                                distProperty = (1-(120-distProperty)/120)
                                                * this.weights[i];
                            }else{
                                distProperty = (1-(120-distProperty)/120)
                                                * 0.15;
                            }
                            dist +=distProperty;
                        }
                        else if(t.get(i).getClass().equals(jmr.descriptor.color.MPEG7ColorStructure.class) ){
                            distProperty = (double) t.get(i).compare(u.get(j));                     //en el caso de DominantColor le asigno mas peso 
                            if(weights != null){
                                distProperty = (distProperty*5)                                     //El valor maximo ronda los 0.2 
                                            * this.weights[i];                                      // le asigno el peso
                            }else{
                                distProperty = (distProperty*5)                                     //El valor maximo ronda los 0.2 
                                            * 0.35;                                                 // le asigno el peso
                            }
                            dist += distProperty;
                        }
                        else if (t.get(i).getClass().equals(jmr.descriptor.color.MPEG7ScalableColor.class)){
                            distProperty = (double) t.get(i).compare(u.get(j));
                            if(distProperty > 400)
                                distProperty = 400;

                            if(weights != null){
                                distProperty = (1-(400-distProperty)/400)
                                            * this.weights[i];
                            }else{
                                distProperty = (1-(400-distProperty)/400)
                                            * 0.5;
                            }

                            dist +=distProperty;
                        }
                        else {
                            distProperty = (double) t.get(i).compare(u.get(j));
                            if (distProperty >1)
                                throw new InvalidParameterException("Can not compare descriptor at pos " + i + ", distance must be in the interval [0,1]");
                            else
                                distProperty = distProperty*this.weights[i];
                            dist +=distProperty;

                        }
                    }
                }
            }
            if(numDescriptors != t.size())
                throw new InvalidParameterException("Descriptors in both lists must be equals");
            else
                return dist;
        }
    }
}