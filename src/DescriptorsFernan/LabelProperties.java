package DescriptorsFernan;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jmr.descriptor.label.LabelDescriptor;
import jmr.descriptor.MediaDescriptorAdapter;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.Comparator;
import jmr.descriptor.DescriptorList;
import jmr.descriptor.MediaDescriptorFactory;

import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.descriptor.label.Classifier;
import jmr.descriptor.label.LabelDescriptor.WeightBasedComparator;
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
    private LabelDescriptor label = null; 
    
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
        this.properties = new DescriptorList(this.source);
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
    
    
    
    
    
    @Override
    public String toString(){
        return (this.label.toString() + "\n" + this.properties.toString());
    }
    
    static class DefaultComparator implements Comparator <LabelProperties, Double> {
        
        private double normalizeProperties(LabelProperties t, LabelProperties u){
            double dist = 0.0;
            double distProperty = 0.0;                                                //distancia de una propiedad concreta
            for (int i=0 ; i < t.properties.size(); i++){                   //Itero sobre ella buscando cuales son las propiedades en comun con la otra lista
                for (int j=0; j < u.properties.size(); j++){
                    if(t.getProperty(i).getClass().equals(u.getProperty(j).getClass())){       //Busco si los nombres de las propiedades coinciden
                        if(t.getProperty(i).getClass() == jmr.descriptor.color.SingleColorDescriptor.class){ //Si coinciden, compruebo que propiedad es, 
                            distProperty = (double) t.getProperty(i).compare(u.getProperty(j));                    //en el caso de singlecolor le asigno poco peso poco peso 
                            if(distProperty > 400) //Le asigno el valor maximo para la normalización
                                distProperty = 400;
                            distProperty = (distProperty/400)
                                            * 0.15;
                            dist +=distProperty;
                        }
                        else if(t.getProperty(i).getClass().equals(jmr.descriptor.color.MPEG7ColorStructure.class) ){
                            distProperty = (double) t.getProperty(i).compare(u.getProperty(j));                    //en el caso de DominantColor le asigno mas peso 
                            distProperty = (distProperty*5)//El valor maximo ronda los 0.2 
                                            * 0.35;// le asigno el peso
                            dist += distProperty;
                        }
                        else if (t.getProperty(i).getClass().equals(jmr.descriptor.color.MPEG7ScalableColor.class)){
                            distProperty = (double) t.getProperty(i).compare(u.getProperty(j));
                            if(distProperty > 700)
                                distProperty = 700;
                            distProperty = (distProperty/700)
                                            * 0.5;
                            dist +=distProperty;
                        }
//                          else {                                              Si no es un descriptor de esos 3 no hace nada, hay que darle mas vueltas
//                          
//                        
//                            }
                    }
                }
            }
            return dist;
        }
        
        /**
         * @param t
         * @param u
         * @return 
         */
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            double distProperties = normalizeProperties(t,u);
            double dist = distProperties;
            dist += (double) t.label.compare(u.label);
            
            //WeightBasedComparator comparador = new WeightBasedComparator(3,true);
            //t.label.setComparator(comparador);
            return dist;

        }
    }
    
    static class PropertiesComparator implements Comparator <LabelProperties, Double> {
        
        private double normalizeProperties(LabelProperties t, LabelProperties u){
            double dist = 0.0;
            double distProperty = 0.0;                                                //distancia de una propiedad concreta
            for (int i=0 ; i < t.properties.size(); i++){                   //Itero sobre ella buscando cuales son las propiedades en comun con la otra lista
                for (int j=0; j < u.properties.size(); j++){
                    if(t.getProperty(i).getClass().equals(u.getProperty(j).getClass())){       //Busco si los nombres de las propiedades coinciden
                        if(t.getProperty(i).getClass() == jmr.descriptor.color.SingleColorDescriptor.class){ //Si coinciden, compruebo que propiedad es, 
                            distProperty = (double) t.getProperty(i).compare(u.getProperty(j));                    //en el caso de singlecolor le asigno poco peso poco peso 
                            if(distProperty > 400) //Le asigno el valor maximo para la normalización
                                distProperty = 400;
                            distProperty = (distProperty/400)
                                            * 0.15;
                            dist +=distProperty;
                        }
                        else if(t.getProperty(i).getClass().equals(jmr.descriptor.color.MPEG7ColorStructure.class) ){
                            distProperty = (double) t.getProperty(i).compare(u.getProperty(j));                    //en el caso de DominantColor le asigno mas peso 
                            distProperty = (distProperty*5)//El valor maximo ronda los 0.2 
                                            * 0.35;// le asigno el peso
                            dist += distProperty;
                        }
                        else if (t.getProperty(i).getClass().equals(jmr.descriptor.color.MPEG7ScalableColor.class)){
                            distProperty = (double) t.getProperty(i).compare(u.getProperty(j));
                            if(distProperty > 700)
                                distProperty = 700;
                            distProperty = (distProperty/700)
                                            * 0.5;
                            dist +=distProperty;
                        }
//                          else {                                              Si no es un descriptor de esos 3 no hace nada, hay que darle mas vueltas
//                          
//                        
//                            }
                    }
                }
            }
            return dist;
        }

        /**
         * @param t
         * @param u
         * @return 
         */
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            double distProperties = normalizeProperties(t,u);
            
            return distProperties;

        }
    }
    
    
}
    
    
    
    
     
     
     
     
     
     

