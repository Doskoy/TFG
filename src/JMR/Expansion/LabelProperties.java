package JMR.Expansion;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jmr.descriptor.MediaDescriptorAdapter;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.Comparator;
import jmr.descriptor.DescriptorList;
import jmr.descriptor.MediaDescriptorFactory;

import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.descriptor.color.SingleColorDescriptor;
import jmr.descriptor.label.Classifier;
import jmr.descriptor.label.LabelDescriptor;
import jmr.descriptor.label.LabelDescriptor.WeightBasedComparator;
import jmr.descriptor.label.LabelDescriptor.EqualComparator;
import jmr.descriptor.label.LabelDescriptor.SoftEqualComparator;
import jmr.descriptor.label.LabelDescriptor.InclusionComparator;
import jmr.descriptor.label.LabeledClassification;


/**
 * This class is a descriptor of an image wich is compone
 * by a list of labels and a list of properties specified at the moment
 * of it creation
 * 
 * @param <T> The Type of the media described by this descriptor
 * @author Fernando Roldán Zafra
 */

public class LabelProperties extends MediaDescriptorAdapter<BufferedImage> implements Serializable{
    /**
     * List of labels associated to the media
     */
    protected LabelDescriptor label = null; 
    
    /**
     * List of types of the properties that represent the media
     */
    protected transient Class classProperties[] = null;
    
    /**
     * List of properties
     */
    protected DescriptorList properties = null;
    
    protected transient Classifier<BufferedImage,? extends LabeledClassification> classifier = null; 
    
    private static Class[] DEFAULT_PROPERTIES = {MPEG7ColorStructure.class, MPEG7ScalableColor.class, SingleColorDescriptor.class};
    
    private static Comparator DEFAULT_COMPARATOR = new EqualLabelsComparator();
    
    private static Classifier DEFAULT_CLASSIFIER = null;
    
    public LabelProperties (BufferedImage image){
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = DEFAULT_PROPERTIES;
        this.classifier = DEFAULT_CLASSIFIER;
        this.init(image);
    }
    
    public LabelProperties(LabelDescriptor label){
        super(null, new InclusionComparator());
        this.classProperties = DEFAULT_PROPERTIES;
        this.label = label;
        this.properties = new DescriptorList(null);
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
        this.classifier = DEFAULT_CLASSIFIER;
        this.init(image);
    }
    
    public LabelProperties (BufferedImage image, Classifier<BufferedImage,? extends LabeledClassification> classifier){
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = DEFAULT_PROPERTIES;
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
            DEFAULT_PROPERTIES = classProperties;
        }
    }
    
    public static void setDefaultClassifier(Classifier classifier){
        DEFAULT_CLASSIFIER = classifier;
    }
    public static void setDefaultComparator(Comparator c){
        DEFAULT_COMPARATOR = c;
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
    
    public void addProperty(MediaDescriptor descriptor){
        this.properties.add(descriptor);
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
    
    static public class OnlyLabelsComparator implements Comparator <LabelProperties, Double>{
        boolean labeledProperties = false;
        
        public OnlyLabelsComparator(boolean labeledProp){
            labeledProperties = labeledProp;
        }
        
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            LabelDescriptor.InclusionComparator c = new LabelDescriptor.InclusionComparator();
            t.label.setComparator(c);
            double dist = (double)t.label.compare(u.label);
            if(labeledProperties == true && dist == 0){
                LabelDescriptor label = (LabelDescriptor)t.properties.get(0);
                LabelDescriptor.EqualComparator comp = new LabelDescriptor.EqualComparator();
                label.setComparator(comp);
                dist = (double) label.compare(u.properties.get(0));
            }
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
                if(PropertyWeights != null){
                    WeightedPropertiesComparator c = new WeightedPropertiesComparator(this.PropertyWeights);
                    t.properties.setComparator(c);
                }
                WeightBasedComparator comp = new WeightBasedComparator(type, only_inclusion);
                t.label.setComparator(comp);
                double distLabel = (double) t.label.compare(u.label);
                double distProp = (double) t.properties.compare(u.properties);
                double dist = (distLabel+1)*distProp;
                if(this.PropertyWeights != null && distProp == 0){
                    double sum = 0.0;
                    for(int i = 0; i < PropertyWeights.length; i++){
                        sum += PropertyWeights[i];
                    }
                    if(sum == 0){
                        dist = distLabel;
                    }
                }
                return dist;
            }
            
        }
    }
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
            if(this.weights != null){
                WeightedPropertiesComparator comp = new WeightedPropertiesComparator(weights);
                t.properties.setComparator(comp);
            }
            double dist = 0.0;
            double distLabel = (double) t.label.compare(u.label);

            double distProp = (double) t.properties.compare(u.properties);
            
            if(this.weights != null && distProp == 0){
                    double sum = 0.0;
                    for(int i = 0; i < weights.length; i++){
                        sum += weights[i];
                    }
                    if(sum == 0){
                        dist = distLabel;
                    }
            }else{
                dist = (distLabel+1)*distProp;
            } 
            return dist;
        }
    }
    
    static public class InclusionComparator implements Comparator <LabelProperties, Double>{
        double weights[] = null;
        public InclusionComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }
        
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            LabelDescriptor.InclusionComparator c = new LabelDescriptor.InclusionComparator();
            t.label.setComparator(c);
            if(this.weights != null){
                WeightedPropertiesComparator comp = new WeightedPropertiesComparator(weights);
                t.properties.setComparator(comp);
            }
            double dist = 0.0;
            double distLabel = (double) t.label.compare(u.label);

            double distProp = (double) t.properties.compare(u.properties);
            if(this.weights != null && distProp == 0){
                    double sum = 0.0;
                    for(int i = 0; i < weights.length; i++){
                        sum += weights[i];
                    }
                    if(sum == 0){
                        dist = distLabel;
                    }
            }else
                dist = (distLabel+1)*distProp;
                
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
        
        
        /*public Double apply(LabelProperties t, LabelProperties u){
            LabelDescriptor.SoftEqualComparator c = new LabelDescriptor.SoftEqualComparator();
            t.label.setComparator(c);
            if(this.weights != null){
                WeightedPropertiesComparator comp = new WeightedPropertiesComparator(weights);
                t.properties.setComparator(comp);
            }
            double dist = 0.0;
            double distLabel = 0.0;
            distLabel = (double) t.label.compare(u.label);

            if(distLabel == Double.POSITIVE_INFINITY){
                distLabel = 1.0;
            }
            
            double distProp = (double) t.properties.compare(u.properties);
            if(this.weights != null && distProp == 0){
                    double sum = 0.0;
                    for(int i = 0; i < weights.length; i++){
                        sum += weights[i];
                    }
                    if(sum == 0){
                        dist = distLabel;
                    }
            }else
                dist = (distLabel+1)*distProp;
            return dist;
        
        }
        */
    }
    
    private static class WeightedPropertiesComparator implements Comparator <DescriptorList, Double> {
        private double weights[] = null;
        private final double SINGLE_COLOR_MAX_DISTANCE = 120;
        private final double STRUCTURED_COLOR_MAX_DISTANCE = 0.2;
        private final double SCALABLE_COLOR_MAX_DISTANCE = 1000;
        public WeightedPropertiesComparator(double... weights){
            if(weights.length != 0)
                this.weights = weights;
        }
        
        
        
        private double euclidea(DescriptorList t, DescriptorList u){
            double dist = 0.0;
            for(int i = 0; i < t.size(); i++){
                for(int j = 0; j < t.size(); j++){
                    if(t.get(i).getClass().equals(u.get(j).getClass())){
                        dist += Math.pow((double)t.get(i).compare(u.get(j)),2);
                    }
                }
            }
            return Math.sqrt(dist);
        }
        
        @Override
        public Double apply(DescriptorList t, DescriptorList u){
                    if(weights != null){
                        if(weights.length != t.size()){
                            throw new InvalidParameterException("They must be the same number of weights than descriptors");
                        }
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
                                    if(distProperty > SINGLE_COLOR_MAX_DISTANCE)                                                  //Le asigno el valor maximo para la normalización
                                        distProperty = SINGLE_COLOR_MAX_DISTANCE;
                                    if(weights != null){
                                        distProperty = (distProperty/SINGLE_COLOR_MAX_DISTANCE)
                                                        * this.weights[i];
                                    } else{
                                        distProperty = Math.pow(distProperty, 2);
                                    }
                                    dist += distProperty;
                                }
                                else if(t.get(i).getClass().equals(jmr.descriptor.color.MPEG7ColorStructure.class) ){
                                    distProperty = (double) t.get(i).compare(u.get(j));                     //en el caso de DominantColor le asigno mas peso 
                                    if(distProperty > STRUCTURED_COLOR_MAX_DISTANCE)
                                        distProperty = STRUCTURED_COLOR_MAX_DISTANCE;
                                    if(weights != null){
                                        distProperty = (distProperty/STRUCTURED_COLOR_MAX_DISTANCE)                                     //El valor maximo ronda los 0.2 
                                                    * this.weights[i];                                      // le asigno el peso
                                    } else{
                                        distProperty = Math.pow(distProperty, 2);
                                    }
                                    dist += distProperty;
                                }
                                else if (t.get(i).getClass().equals(jmr.descriptor.color.MPEG7ScalableColor.class)){
                                    distProperty = (double) t.get(i).compare(u.get(j));
                                    if(distProperty > SCALABLE_COLOR_MAX_DISTANCE)
                                        distProperty = SCALABLE_COLOR_MAX_DISTANCE;
                                    if(weights != null){
                                        distProperty = (distProperty/SCALABLE_COLOR_MAX_DISTANCE)
                                                    * this.weights[i];
                                    } else{
                                        distProperty = Math.pow(distProperty, 2);
                                    }
                                    dist +=distProperty;
                                }
                                else{
                                    return euclidea(t,u);
                                }
                            }
                        }
                    }
            if(numDescriptors != t.size())
                throw new InvalidParameterException("Descriptors in both lists does not have the same length");
            else if(weights == null){
                dist = Math.sqrt(dist);
            }
                return dist;
            }
        }
    
    static public class ImageLabelProperties extends LabelProperties{

        public ImageLabelProperties(BufferedImage media) {
            super(media);
        }
        
        public ImageLabelProperties(LabelDescriptor label) {
            super(label);
        }
    }
    
    static public class LabeledColorDescriptor extends LabelProperties{
        public LabeledColorDescriptor(BufferedImage media){
            super(media);
            this.classProperties = new Class[]{LabelDescriptor.class};
            init(media);
        }
        
        public LabeledColorDescriptor(LabelDescriptor Label, LabelDescriptor Color){
            super(Label);
            this.label = Label;
            this.classProperties = new Class[]{LabelDescriptor.class};
            LabelDescriptor color = Color;
            this.properties = new DescriptorList(null);
            this.properties.add(color);
        }
        
        public void init(BufferedImage img){
            if(this.classProperties != null){
                if(this.classifier == null){
                    this.label = new LabelDescriptor(img);
                }
                else{
                    this.label = new LabelDescriptor(img, classifier);
                }
                this.properties = new DescriptorList(null);
            
                
            
            //Version Con Amarillo y Negro
                /*
                List<Integer> colors_red = new ArrayList<Integer>(
                        Arrays.asList(255,192,203, 255,102,102, 255,51,51, 255,0,0, 204,0,0, 153,0,0, 132,101,102,
                                192,127,126, 233,169,160, 221,198,197, 186,157,161, 96,59,59));
                
                List<Integer> colors_blue = new ArrayList<Integer>(
                        Arrays.asList(51,204,255, 173,216,230, 51,153,255, 0,0,255, 0,0,204, 0,0,153, 157, 162, 187,
                                191,193,205, 49,72,92));
                
                List<Integer> colors_green = new ArrayList<Integer>(
                        Arrays.asList(102,255,102, 0,255,51, 0,255,255, 0,153,0, 0,102,0, 154,205,50, 144,238,144, 143,188,143,
                                208,215,176, 102,115,101, 75,225,68, 198,237,197, 227,237,203, 56,114,73
                        ));

                List<Integer> colors_yellow = new ArrayList<Integer>(
                        Arrays.asList(255,255,204, 255,255,153, 255,255,0, 204,204,0, 153,153,0, 232,219,163,
                                177,154,86, 244,239,195, 242,228,185, 207,193,118
                        ));
                
                ArrayList<Integer> colors_int = new ArrayList<Integer>();
                
                colors_int.addAll(colors_red);
                colors_int.addAll(colors_blue);
                colors_int.addAll(colors_green);
                colors_int.addAll(colors_yellow);

                ArrayList<SingleColorDescriptor> colors = new ArrayList<SingleColorDescriptor>();
                
                for(int i = 0; i < colors.size(); i = i+3){
                    colors.add(new SingleColorDescriptor(new Color(i,i+1,i+2)));
                }
                colors.add(new SingleColorDescriptor(Color.BLACK));
                
                
                
                SingleColorDescriptor singlecolor = new SingleColorDescriptor(image);
                SingleColorDescriptor black = new SingleColorDescriptor(Color.black);
                double single_color_red = singlecolor.getColor().getRed();
                double single_color_green = singlecolor.getColor().getGreen();
                double single_color_blue = singlecolor.getColor().getBlue();
                double greyDist = Math.sqrt(Math.pow(single_color_red - single_color_green, 2.0) + Math.pow(single_color_blue - single_color_green, 2.0));

                LabelDescriptor label = null;
                if(greyDist <= 10 && single_color_red <= 230){
                    label = new LabelDescriptor("Black");
                }else{
                    Double min_dist = 0.0;
                    Double dist = 0.0;
                    min_dist = singlecolor.compare(black);
                    int min_index = 0;
                    for(int i = 0; i< colors.size(); i++){
                        dist = singlecolor.compare(colors.get(i));
                        if(dist < min_dist){
                            min_index = i;
                            min_dist = dist;
                        }
                        //System.out.println(colors.get(i).getColor().toString() + " - " + singlecolor.getColor().toString() + dist);
                    }
                    System.out.println(singlecolor + "Dist min " + min_index);
                    

                    if(min_index >= 0 && min_index <= 11){
                        label = new LabelDescriptor("Red");
                    }else if(min_index >= 12 && min_index <= 20){
                        label = new LabelDescriptor("Blue");
                    }else if(min_index >= 21 && min_index <= 34){
                        label = new LabelDescriptor("Green");
                    }else if(min_index >= 35 && min_index <= 44){
                        label = new LabelDescriptor("Yellow");
                    }else if(min_index == 45){
                        label = new LabelDescriptor("Black");
                    }
                }
            */
                
                LabelDescriptor label = null;
                SingleColorDescriptor singlecolor = new SingleColorDescriptor(img);
                System.out.println(singlecolor.toString());
                
                int red = singlecolor.getColor().getRed();
                int green = singlecolor.getColor().getGreen();
                int blue = singlecolor.getColor().getBlue();
                
                if(Math.abs(red - green) <= 40 && Math.abs(red-blue) >= 25 && Math.abs(green-blue) >= 25 && red-blue > 0 && green-blue > 0){
                    label = new LabelDescriptor("Yellow");
                }else if(red > green && red > blue){
                    label = new LabelDescriptor("Red");
                }else if(green > red && green > blue){
                    label = new LabelDescriptor("Green");
                }else if(blue > red && blue > green){
                    label = new LabelDescriptor("Blue");
                }else 
                    label = new LabelDescriptor("Unknown");
                
                //System.out.println(this.properties);
                this.properties.add(label);
            }
        }
    }
    //End of class LabelProperties
    }
