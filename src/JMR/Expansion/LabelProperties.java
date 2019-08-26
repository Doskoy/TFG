package JMR.Expansion;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jfi.color.ISCCColorMap;
import jfi.color.fuzzy.FuzzyColorSpace;
import jfi.fuzzy.FuzzySetCollection.PossibilityDistributionItem;
import jfi.geometry.Point3D;
import jmr.descriptor.MediaDescriptorAdapter;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.Comparator;
import jmr.descriptor.DescriptorList;
import jmr.descriptor.MediaDescriptorFactory;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.descriptor.label.Classifier;
import jmr.descriptor.label.LabelDescriptor;
import jmr.descriptor.label.LabelDescriptor.WeightBasedComparator;
import jmr.descriptor.label.LabelDescriptor.EqualComparator;
import jmr.descriptor.label.LabeledClassification;
import jmr.initial.descriptor.mpeg7.MPEG7DominantColors;
import jmr.initial.descriptor.mpeg7.MPEG7DominantColors.MPEG7SingleDominatColor;


/**
 * This class is a descriptor of an image wich is composed
 * by a list of labels and a list of properties specified at the moment
 * of its creation
 * 
 * @param <BufferedImage> The Type of the media described by this descriptor
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
    
    /**
     * Classifier used to label the given image.
     */
    protected transient Classifier<BufferedImage,? extends LabeledClassification> classifier = null; 
    
    /**
     * Visual Properties used by default
     */
    private static Class[] DEFAULT_PROPERTIES = {MPEG7ScalableColor.class};
    
    /**
     * Comparator used by default
     */
    private static Comparator DEFAULT_COMPARATOR = new EqualLabelsComparator();
    
    /**
     * Classifier used by default 
     */
    
    private static Classifier DEFAULT_CLASSIFIER = null;
    
    /**
     * Construct an object using the given image, it will use the default classifier,
     * properties and comparator.
     * @param image 
     */
    public LabelProperties (BufferedImage image){
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = DEFAULT_PROPERTIES;
        this.classifier = DEFAULT_CLASSIFIER;
        this.init(image);
    }
    
    /**
     * Construct an object with no image, or properties just with a list of labels. It is used to make querys to the database.
     * It will use the {@link InclusionComparator()}
     * @param label the label that will have the object
     */
    
    public LabelProperties(LabelDescriptor label){
        super(null, new InclusionComparator());
        this.classProperties = DEFAULT_PROPERTIES;
        this.label = label;
        this.properties = new DescriptorList(null);
    }
    
    /**
     * Contruct an object using the given image and calculating the properties specified. It will use the default comparator
     * @param image The media to be described
     * @param classProperties The visualProperties that will be calculated for the given image
     */
    public LabelProperties (BufferedImage image, Class<? extends MediaDescriptor>... classProperties){
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = classProperties;
        this.classifier = DEFAULT_CLASSIFIER;
        this.init(image);
    }

    /**
     * Construct an object using the given image, the given classifier and the defaults properties an comparator
     * @param image The media to be described 
     * @param classifier The classifier that will be used to label the given image
     */
    public LabelProperties (BufferedImage image, Classifier<BufferedImage,? extends LabeledClassification> classifier){
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = DEFAULT_PROPERTIES;
        this.classifier = classifier;
        this.init(image);
    }
   
    /**
     * Construct an object using the given image, the given classifier and the given properties, it will use the default comparator
     * @param image The media to be described
     * @param classifier The classifier that will be used to label the given image
     * @param classProperties The properties that will be calculated for the given image
     */
    public LabelProperties (BufferedImage image, Classifier<BufferedImage,? extends LabeledClassification> classifier, Class<? extends MediaDescriptor>... classProperties ){
        super(image, DEFAULT_COMPARATOR);
        this.classProperties = classProperties;
        this.classifier = classifier;
        this.init(image);
    }
    /**
     * Initialize the descriptor with the specified properties and the label that will be calculated
     * @param image the image that will be used to initialize the descriptor
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
    
    /**
     *  Set the default properties for this class.
     * @param classProperties The properties that will be used by default.
     */
    public static void setDefaultProperties(Class<? extends MediaDescriptor>... classProperties){
        if(classProperties != null){
            DEFAULT_PROPERTIES = classProperties;
        }
    }
    
    /**
     *  Set the default classifier for this class.
     * @param classifier The classifier that will be used by default for this class
     */
    public static void setDefaultClassifier(Classifier classifier){
        DEFAULT_CLASSIFIER = classifier;
    }
    
    /**
     * Set the default comparator for this class.
     * @param comparator The comparator that will be used by default
     */
    public static void setDefaultComparator(Comparator comparator){
        DEFAULT_COMPARATOR = comparator;
    }
    
    /**
     * Set the classifier for this object.
     * @param classifier The classifier that will be used for this object. 
     */
    public void setClassifier(Classifier classifier){
        this.label.setClassifier(classifier);
    }
    
    /**
     * Returns the {@link jmr.descriptor.label.LabelDescriptor()} of this object
     * @return the label of this object
     */
    public LabelDescriptor getLabel(){
        return label;
    } 
    
    /**
     * Allows to add properties to this object. It will be calculated automatically
     * @param classProperties The class of the properties that will be added to this object.
     */
    public void addProperty(Class <? extends MediaDescriptor>... classProperties){
        MediaDescriptor descriptor;
        for (Class c : classProperties){
            descriptor = MediaDescriptorFactory.getInstance(c, this.source);
            this.properties.add(descriptor);
        }
    }
    
    /**
     * Add a calculated property to this object.
     * @param descriptor The property to be added.
     */
    
    public void addProperty(MediaDescriptor descriptor){
        this.properties.add(descriptor);
    }
    
    /**
     * Return the property at the specified position.
     * @param index The position of the descriptor to retrieve
     * @return the property at the specified position
     */
    public MediaDescriptor getProperty(int index){
        return properties.get(index);
    }
    
    /**
     * Return the number of properties for this object
     * @return the number of properties for this object
     */
    public int SizeProperties(){
        return properties.size();
    }
    
    /**
     * Return the list of properties
     * @return the list of properties
     */
    
    public DescriptorList getProperties(){
        return properties;
    }

    /**
     * Return a string with all the data of this descriptor
     * @return a string with all the data of this descriptor
     */
    @Override
    public String toString(){
        return (this.label.toString() + "\n" + this.properties.toString());
    }
    
    /**
     * Functional class that implements the comparation between this descriptor.
     * It only compares the labels and if it is indicated it also compares the 
     * properties as a label. 
     */
    static public class OnlyLabelsComparator implements Comparator <LabelProperties, Double>{
        /**
         * Boolean that indicates if in the comparation will be also compared the properties as a label.
         * If false, it will only compare the label of the descriptor, ignoring
         * the properties. If true, it will also compare the properties.
         */
        
        private boolean labeledProperties = false;
        
        /**
         * Construct a comparator based only on labels.
         * @param labeledProp boolean for check if the properties are also labels
         */
        public OnlyLabelsComparator(boolean labeledProp){
            labeledProperties = labeledProp;
        }
        
        /**
         * Aplies this comparator to the given arguments.
         * @param t The first argument
         * @param u The second argument
         * @return Distance between arguments
         */
        @Override
        public Double apply(LabelProperties t, LabelProperties u){
            LabelDescriptor.InclusionComparator c = new LabelDescriptor.InclusionComparator();
            t.label.setComparator(c);
            double dist = (double)t.label.compare(u.label);
            if(labeledProperties == true && dist == 0){
                LabelDescriptor label_t = (LabelDescriptor)t.properties.get(0);
                LabelDescriptor label_u = (LabelDescriptor)u.properties.get(0);
                Comparator comp = null;
                
                if(label_t.isWeighted() && label_u.isWeighted()){
                    comp = new LabelDescriptor.WeightBasedComparator(WeightBasedComparator.TYPE_MIN, true);
                }else{
                    comp = new LabelDescriptor.InclusionComparator();
                }
                label_t.setComparator(comp);
                dist = (double) label_t.compare(label_u);
            }
            return dist;
        }
    }
    
    /**
     * Functional class that represents the comparation based on weighted labels.
     * It returns the distance between weighted labels combined with the 
     * distance between properties.
     * 
     * It also allows to set a weight for the properties. This weight will be 
     * used to gives a property more importance than others.
     */
    static public class WeightedLabelComparator implements Comparator <LabelProperties, Double> {
        /**
         * Weights asociated to the properties. 
         */
        private double PropertyWeights[] = null;
        
        /**
         * Type of distance agregation based on the euclidean distance
         */
        private int type = 4;
        
        /**
         * If true, only inclusion will be used. If not it will use equality
         */
        private boolean only_inclusion = false;

        /**
         * Construct a comparator with the given type of agregation and weights 
         * of properties and the specified comparation (inclusion or equality).
         * @param type Type of distance agregation
         * @param only_inclusion If true only inclusion will be used, if not, 
         * it will use equality
         * @param weights weights of properties.
         */
        public WeightedLabelComparator (int type, boolean only_inclusion, double... weights){
            if(weights != null)
                this.PropertyWeights = weights;
            this.type = type;
            this.only_inclusion = only_inclusion;
        }
        
        /**
         * Construct a comparator with the given type of agregation and weights 
         * of properties. It will use equality comparation
         * @param type Type of distance agregation
         * @param weights weights of properties.
         */
        public WeightedLabelComparator (int type, double... weights){
            if(weights != null)
                this.PropertyWeights = weights;
            this.type = type;
        }
        
        /**
         * Construct a comparator with the given weights of properties. It will 
         * use equality comparation and the euclidean type agregation
         * @param weights 
         */
        public WeightedLabelComparator (double... weights){
            if(weights != null)
                this.PropertyWeights = weights;
        }
        
        /**
         * Aplies this comparator to the given arguments
         * @param t The first argument
         * @param u The second argument
         * @return The distance between both arguments
         */
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
     * Functional class that implements the comparation based on equality. 
     * if Labels are equal it takes the distance of properties as distance. If labels 
     * are not equals, it return infinity as distance.
     */
    static public class EqualLabelsComparator implements Comparator <LabelProperties, Double>{
        /**
         * Weights asociated to the properties. 
         */
        double weights[] = null;

        /**
         * Constructs a comparator based on equality
         * @param weights The weights of the properties
         */
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
            
            //This condition solves the problem that happen when the weights of 
            //properties are 0 when combine distLabel and distprop if weights of
            //properties are 0 distance becomes 0 and it doesnt take in count the labels
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
    
    /**
     * Functional class that implements the comparation based on Inclusion. 
     * if in the comparation the labels are included it takes the distance of properties as distance. If labels 
     * are not included, it return infinity as distance.
     */
    static public class InclusionComparator implements Comparator <LabelProperties, Double>{
        
        /**
        * Weights asociated to the properties. 
        */
        double weights[] = null;
        
        /**
         * Constructs a comparator based on inclusion
         * @param weights The weights of the properties
         */
        public InclusionComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }
        /**
         * Apply this comparation
         * @param t The first argument
         * @param u The second argument
         * @return The distance between arguments
         */
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
     * If both arguments have any common label, it compare the properties otherwise 
     * it returns infinity
     * @param weights Weights that are going to be used to compare the properties
     * @return It returns infinity if there are not any common label, 0 if they have a common label
     */
    static public class SoftEqualComparator implements Comparator <LabelProperties, Double>{
        /**
         * Weights asociated to the properties. 
         */
        double weights[] = null;
        
        /**
         * Construct a comparator based on the soft equal comparation.
         * @param weights Weights asociated to the properties
         */
        public SoftEqualComparator(double... weights){
            if(weights != null)
                this.weights = weights;
        }
        
        /**
         * Apply this comparation
         * @param t The first argument
         * @param u The second argument
         * @return The distance between arguments
         */
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
    
    /**
     * Functional class implemented for compare list of descriptors. It takes as
     * parameter a list of weights that represents the weights associated to each descriptor 
     * in the list. If the list is empty it calculates the euclidean distance between
     * descriptors
     */
    public static class WeightedPropertiesComparator implements Comparator <DescriptorList, Double> {
        /**
         * The weights of the descriptor
         */
        private double weights[] = null;
        private final double SINGLE_COLOR_MAX_DISTANCE = 120;
        private final double STRUCTURED_COLOR_MAX_DISTANCE = 0.2;
        private final double SCALABLE_COLOR_MAX_DISTANCE = 1000;
        /**
         * Creates a comparator with the given weights
         * @param weights The weights that will be used
         */
        
        public WeightedPropertiesComparator(double... weights){
            if(weights.length != 0)
                this.weights = weights;
        }
        
        /**
         * Apply this comparation to the given arguments
         * @param t The first argument
         * @param u The second argument
         * @return The distance between arguments
         */
        @Override
        public Double apply(DescriptorList t, DescriptorList u){
            if(weights != null){
                if(weights.length != t.size()){
                    throw new InvalidParameterException("They must be the same number of weights than descriptors");
                }
            }
            
            double dist = 0.0;
            double distProperty = 0.0;
            int numDescriptors = 0;
            
            for(int i=0; i < t.size(); i++){
                for(int j=0; j < u.size(); j++){
                    if(t.get(i).getClass().equals(u.get(j).getClass())){
                        numDescriptors++;
                        distProperty = (double) t.get(i).compare(u.get(j));
                        if(weights != null){
                            distProperty = distProperty * this.weights[i];
                        }
                        distProperty = Math.pow(distProperty, 2);
                        
                        dist += distProperty;
                    }
                }
            }
            if(numDescriptors != t.size())
                throw new InvalidParameterException("Descriptors in both lists does not have the same length");
            else {
                dist = Math.sqrt(dist);
            }
                return dist;
        }
    }
    
    /**
     * Special case when a LabelProperties is composed by two labels, one for describe the 
     * cathegory of the object and another one for describe its visual properties, specifically
     * its dominants colors if specified it will use fuzzy colors space for label the colors
     */
    static public class LabeledColorDescriptor extends LabelProperties{
        /**
         * This variable specify if the label for the dominants colors will be weighted
         */
        private static Boolean WEIGHTED = false;
        
        /**
         * Basic colors that will be used for calculate the label
         */
        private static Map<String, List<Color>> basic_colors = new HashMap <String, List<Color>>();
        
        /**
         * Map of colors that will be used for create a fuzzy color space
         */
        private static Map<String, Point3D> prototype = new ISCCColorMap(ISCCColorMap.TYPE_CUSTOM);
        
        /**
         * Construct a descriptor of this type using the given media
         * @param media The media that will be used to create this descriptor
         */
        public LabeledColorDescriptor(BufferedImage media){
            super(media);
            init(media, WEIGHTED);
            this.classProperties = new Class[]{LabelDescriptor.class};
            this.comparator = new InclusionComparator();
        }
                
        /**
         * Construct a descriptor of this tyhpe using the given label for the category object
         * and the given color for the color label. It is used for create a query for the 
         * Data base
         * 
         * @param Label The label of the object category
         * @param Color The label of the color
         */
        public LabeledColorDescriptor(LabelDescriptor Label, LabelDescriptor Color){
            super(Label);
            this.label = Label;
            this.classProperties = new Class[]{LabelDescriptor.class};
            LabelDescriptor color = Color;
            this.properties = new DescriptorList(null);
            this.properties.add(color);
            this.comparator = new InclusionComparator();
        }
        
        /**
         * Initialize this descriptor using the given image, if fuzzy variable is true
         * it will use fuzzy color spaces for label the colors. 
         * @param img The image used to calculate this descriptor
         * @param fuzzy Condition that specify if fuzzy color spaces will be used
         */
        
        public void init(BufferedImage img, Boolean fuzzy){
            if(this.classProperties != null){
                initColors(fuzzy);
                if(this.classifier == null){
                    this.label = new LabelDescriptor(img);
                }
                else{
                    this.label = new LabelDescriptor(img, classifier);
                }
                this.properties = new DescriptorList(null);
                
                LabelDescriptor label = getLabeledColors(img, fuzzy);
                this.properties.add(label);
                this.comparator = new LabelDescriptor.InclusionComparator();
            }
        }
        
        /**
         * Initialize the colors.
         * @param fuzzy if true it will initialize the map for create a fuzzy color
         * space.
         */
        public void initColors(Boolean fuzzy){
            if(fuzzy){
                prototype.put("Pink", new Point3D(220, 160, 161));
                prototype.put("Red", new Point3D(230, 0, 38));
                prototype.put("Orange", new Point3D(243, 132, 1));
                prototype.put("Brown", new Point3D(180,116,94));
                prototype.put("Yellow", new Point3D(201, 174, 93));
                prototype.put("Olive", new Point3D(102, 93, 30));
                prototype.put("Yellow-Green", new Point3D(141, 182, 1));
                prototype.put("Green", new Point3D(70, 180, 20));
                prototype.put("Blue", new Point3D(67, 90, 230));
                prototype.put("Purple", new Point3D(154, 78, 174));
                prototype.put("White", new Point3D(252, 252, 249));
                prototype.put("Gray", new Point3D(135, 134, 134));
                prototype.put("Black", new Point3D(7, 7, 7));
            }else{           
                //Pink
                List<Color> colors = new ArrayList<Color>(
                        Arrays.asList(new Color(254, 181, 186), new Color(196, 131, 121))
                );
                basic_colors.put("Pink", colors);

                //Red
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(190, 1, 50) , new Color(150,10,10), new Color(180, 60, 70), new Color(130, 20, 45), new Color(150,50,50))
                );
                basic_colors.put("Red", colors);

                //Orange
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(243, 132, 1))
                );
                basic_colors.put("Orange", colors);

                //Brown
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(138,73,37), new Color(180,116,94))
                );
                basic_colors.put("Brown", colors);

                //Yellow
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(243,195,1), new Color(255, 233, 0), new Color(201, 174, 93), new Color(250, 218, 94))
                );
                basic_colors.put("Yellow", colors);

                //Olive
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(102,93,30))
                );
                basic_colors.put("Olive", colors);

    //            //Yellow-Green
    //            colors = new ArrayList<Color>(
    //                    Arrays.asList(new Color(141,182,1))
    //            );
    //            basic_colors.put("Yellow-Green", colors);
    //            
                //Green
                colors = new ArrayList<Color>(
                        Arrays.asList(  new Color(0,152,70), new Color(141,182,1), new Color(39,166,76), 
                                        new Color(126,159,46), new Color(138,154,91), new Color(23,54,32), new Color(103, 146, 103), 
                                        new Color(147,197,146))
                );
                basic_colors.put("Green", colors);

                //Blue
                colors = new ArrayList<Color>(
                        Arrays.asList(  new Color(1,103,194), new Color(67, 107, 149), new Color(73, 151, 208),
                                        new Color(112, 163, 204), new Color(1, 48, 78))
                );
                basic_colors.put("Blue", colors);

                //Purple
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(154,78,174))
                );
                basic_colors.put("Purple", colors);

                //White
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(240,240,240))
                );
                basic_colors.put("White", colors);

                //Gray
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(135,134,134),new Color (214,214,214), new Color(170,170,170), new Color(100,100,100))
                );
                basic_colors.put("Gray", colors);

                //Black
                colors = new ArrayList<Color>(
                        Arrays.asList(new Color(20,20,20), new Color(40,40,40))
                );
                basic_colors.put("Black", colors);
            }
        }
        
        /**
         * Return the color's label
         * @param image The image to calculate its color's labels
         * @param weighted If true it will use fuzzy color spaces 
         * @return Label of colors
         */
        public LabelDescriptor getLabeledColors(BufferedImage image, Boolean weighted){
            if(!weighted){
                MPEG7DominantColors dominant_colors = new MPEG7DominantColors(0.6f, 0.03f);
                dominant_colors.setSource(image);

                ArrayList<MPEG7SingleDominatColor> list_of_colors = dominant_colors.getDominantColors();

                ArrayList<String> color_string = new ArrayList<>();
                MPEG7SingleDominatColor analized_color;


                if(!list_of_colors.isEmpty()){
                    for(int i = 0; i < list_of_colors.size(); i++){
                        analized_color = (MPEG7SingleDominatColor)list_of_colors.get(i);
                        String label = getSingleColorTag(analized_color);
                        System.out.println(label + " RGB: " + analized_color.getColor().toString());
                        System.out.println("------------------");
                        color_string.add(getSingleColorTag(analized_color));
                    }
                    List<String> distinctList = color_string.stream().distinct().collect(Collectors.toList());
                    String label = distinctList.get(0);
                    String[] labels = {};

                    if(distinctList.size() > 1){
                        distinctList.remove(0);
                        labels = distinctList.toArray(new String[distinctList.size()]);

                    }
                    return new LabelDescriptor(label, labels);
                }else{
                    return new LabelDescriptor(null);
                }
            }else{
                LabelDescriptor labelD; 
                //Map prototipos = new ISCCColorMap(ISCCColorMap.TYPE_BASIC);
//                FuzzyColorSpace fcs = FuzzyColorSpace.Factory.createSphereBasedFCS(prototype);
                FuzzyColorSpace fcs = FuzzyColorSpace.Factory.createFuzzyCMeansFCS(prototype);
                MPEG7DominantColors dominant_colors = new MPEG7DominantColors(0.6f, 0.03f);
                dominant_colors.setSource(image);
                
                List<Color> color_list = new ArrayList<Color>();
                for(MPEG7SingleDominatColor sdc: dominant_colors.getDominantColors()){
                    color_list.add(sdc.getColor());
                }
                Point3D color;
                List<ArrayList> pd_list = new ArrayList<>();
                for(Color c: color_list){
                    color = new Point3D(c.getRed(),c.getGreen(), c.getBlue());
                    ArrayList<PossibilityDistributionItem> pd = fcs.getPossibilityDistribution(color);
                    pd_list.add(pd);
                }
                
                Map<String, Double> weightedColors = new HashMap <String, Double>();
                
                for(ArrayList<PossibilityDistributionItem> pd_item: pd_list){
                    for(int i = 0; i < pd_item.size(); i++){
                        
                        String label = pd_item.get(i).fuzzySet.getLabel();
                        double degree = pd_item.get(i).degree;
                        if(weightedColors.containsKey(label) && degree >= 0.01){
                            if(weightedColors.get(label) < degree)
                                weightedColors.replace(label, degree);
                        }else if(degree >= 0.1){
                            weightedColors.put(label, degree);
                        }
                    }
                }
                List<String> label_list = new ArrayList<String>();
                List<Double> weights_list = new ArrayList<Double>();
                for(Map.Entry<String, Double> entry: weightedColors.entrySet()){
                    label_list.add(entry.getKey());
                    weights_list.add(entry.getValue());
                }
                if(label_list.size() > 0){
                    String label = label_list.get(0);
                    String[] labels = {};
                    if(label_list.size() > 1){
                        label_list.remove(0);
                        labels = label_list.toArray(new String[label_list.size()]);
                    }
                    labelD = new LabelDescriptor(label, labels);
                    Double[] weightsArray = weights_list.toArray(new Double[weights_list.size()]);
                    labelD.setWeights(weightsArray);
                    return labelD;
                }else{
                    return new LabelDescriptor(null);
                }
            }
        }
        
        /**
         * This function, given a DominantColor returns a label with the name of that color
         * @param color The color to be labeled
         * @return String representing a color label
         */
        private String getSingleColorTag(MPEG7SingleDominatColor color){
            String label = null;
            double min_dist = Double.POSITIVE_INFINITY;
            String min_key = "";
            double dist = 0;
            for(Map.Entry<String, List<Color>> entry: basic_colors.entrySet()){
                for(Color c : entry.getValue()){
                    dist = compareColors(color.getColor(), c);
                    if(dist < min_dist){
                        min_dist = dist;
                        min_key = entry.getKey();
                    }
                }
            }
            return min_key;
        }
        /**
         * Set if the labels of the colors are weighted or not
         * @param weight Boolean representing if the color labels will be weighted 
         * or not.
         */
        public static void setWeighted(boolean weight){
            WEIGHTED = weight;
        }
        
        /**
         * This function given two colors return a distance between them
         * @param t The first color
         * @param u The second color
         * @return Distance between colors
         */
        private double compareColors(Color t, Color u){
            double dist;// = Math.abs(t.getRed() - u.getRed()) + Math.abs(t.getGreen() - u.getGreen()) 
                    //+ Math.abs(t.getBlue() - u.getBlue()) ;
                double red = Math.pow(t.getRed()-u.getRed(),2);
                double green = Math.pow(t.getGreen()-u.getGreen(),2);
                double blue = Math.pow(t.getBlue()-u.getBlue(),2);
                dist = Math.sqrt(red+green+blue);

            return dist;
        }
    }
    //End of class LabeledProperties
}
//End of class Labelproperties