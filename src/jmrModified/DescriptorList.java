package jmrModified;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import jmr.descriptor.Comparator;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.MediaDescriptorAdapter;

/**
 * Class representing a list of descriptors calculated from the same media. 
 * 
 * There are not restrictions about the type of descriptors (the list even
 * may contain descriptors of different classes, but from the same source).
 * 
 * @param <T> the type of media described by this object
 * 
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class DescriptorList<T> extends MediaDescriptorAdapter<T> implements Serializable{
    /**
     * List of descriptors
     */
    protected ArrayList<MediaDescriptor<T>> descriptors;

    /**
     * Constructs the descriptor as an empty list and set as comparator the
     * default one.
     * 
     * @param media the source media
     */
    public DescriptorList(T media) {
        super(media, new DefaultComparator());
    }
    
    /**
     * Initialize the descriptor as an empty list.
     *
     * @param media the media associated to this descriptor
     */
    @Override
    public void init(T media) {
        descriptors = new ArrayList<>();
    }
    
    /**
     * Appends the specified descriptor to the end of this list.
     *
     * @param descriptor descriptor to be appended to this list
     * @return <tt>true</tt> (as specified by 
     * {@link java.util.Collection#add(java.lang.Object) })
     * @throws InvalidParameterException if the new descriptor does not share 
     * the list media source
     */
    public boolean add(MediaDescriptor<T> descriptor) {
        if (descriptor.getSource() != this.getSource()) {
            throw new InvalidParameterException("The new descriptor does not share this list media source.");
        }
        return descriptors.add(descriptor);
    }
    
    /**
     * Inserts the specified descriptor at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified descriptor is to be inserted
     * @param descriptor descriptor to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws InvalidParameterException if the new descriptor does not share 
     * the list media source
     */
    public void add(int index, MediaDescriptor<T> descriptor) {
        if (descriptor.getSource() != this.getSource()) {
            throw new InvalidParameterException("The new descriptor does not share this list media source.");
        }
        descriptors.add(index, descriptor);
    }
    
    /**
     * Replaces the descriptor at the specified position in this list with
     * the specified descriptor.
     *
     * @param index index of the descriptor to replace
     * @param descriptor descriptor to be stored at the specified position
     * @return the descriptor previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws InvalidParameterException if the new descriptor does not share 
     * the list media source
     */
    public MediaDescriptor<T> set(int index, MediaDescriptor<T> descriptor) {
        if (descriptor.getSource() != this.getSource()) {
            throw new InvalidParameterException("The new descriptor does not share this list media source.");
        }
        return descriptors.set(index, descriptor);
    }
    
    /**
     * Returns the descriptor at the specified position in this list.
     *
     * @param  index index of the descriptor to return
     * @return the descriptor at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public MediaDescriptor<T> get(int index) {
        return descriptors.get(index);
    }
    
    /**
     * Removes the descriptor at the specified position in this list.
     * Shifts any subsequent descriptor to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the descriptor to be removed
     * @return the descriptor that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public MediaDescriptor<T> remove(int index) {
        return descriptors.remove(index);
    }
    
    /**
     * Removes all of the descriptors from this list.  The list will
     * be empty after this call returns.
     */
    public void clear() {
        descriptors.clear();
    }
   
    /**
     * Returns the number of descriptors in this list.
     *
     * @return the number of descriptors in this list
     */
    public int size() {
        return descriptors.size();
    }
    
    /**
     * Returns <tt>true</tt> if this list contains no descriptors.
     *
     * @return <tt>true</tt> if this list contains no descriptors
     */
    public boolean isEmpty() {
        return descriptors.isEmpty();
    }
    
    /**
     * Returns a string representation of this descriptor
     * .
     * @return a string representation of this descriptor 
     */
    @Override
    public String toString(){
        String output ="";
        for(MediaDescriptor descriptor : descriptors){
            output += descriptor.toString()+"\n";
        }
        return output;
    }
    
    /**
     * Functional (inner) class implementing a comparator between list
     * descriptors.
     * 
     * The difference between list descriptors is calculated as the Euclidean
     * distance. Both lists must have the same size and all the descriptors in 
     * the list must be comparables (at a given position) with a double value 
     * as result.
     */
    static class DefaultComparator implements Comparator<DescriptorList, Double> {
        @Override
        /**
         * Calculates the difference between list descriptors by means a
         * Euclidean distance. Both lists must have the same size and all the
         * descriptors in the list must be comparables (at a given position)
         * with a double value as result.
         *
         * @param t the first descriptor list.
         * @param u the second descriptor list.
         * @return the difference between descriptors.
         * @throws InvalidParameterException if the descriptor lists have 
         * different size, or if the descriptors at a given position are not 
         * comparables with a double value as result.
         */
        public Double apply(DescriptorList t, DescriptorList u) {
            if(t.size() != u.size()){
                throw new InvalidParameterException("The descriptor lists must have the same size.");
            }
            Double item_distance, sum = 0.0;
            for(int i=0; i<t.size(); i++){
                try{
                    item_distance = (Double)t.get(i).compare(u.get(i));
                    sum += item_distance*item_distance;
                }
                catch(ClassCastException e){
                    throw new InvalidParameterException("The comparision between descriptors at position '"+i+"' is not interpetrable as a double value.");
                }
                catch(Exception e){
                    throw new InvalidParameterException("The descriptors at position '"+i+"' are not comparables.");
                }                
            }
            return Math.sqrt(sum);
        }    
    }
    
    
    public static class WeightedComparator implements Comparator <DescriptorList, Double> {
        private double weights[] = null;
        public WeightedComparator(double... weights){
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