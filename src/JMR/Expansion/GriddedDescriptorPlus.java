/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JMR.Expansion;

import JMR.Expansion.Grid.MinimumGridComparator;
import java.awt.image.BufferedImage;
import jmr.descriptor.Comparator;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;

/**
 *
 * @author Fernando Roldán Zafra
 */
public class GriddedDescriptorPlus<T> extends GriddedDescriptor<BufferedImage> {

    private static Comparator DEFAULT_COMPARATOR = new MinimumGridComparator();
    
    public GriddedDescriptorPlus(BufferedImage image){
        super(image);
        this.setComparator(DEFAULT_COMPARATOR);
    }
    
    public static void setDefaultComparator (Comparator c){
        DEFAULT_COMPARATOR = c;
    }
    
//    public void addDescriptor(int index, MediaDescriptor descriptor){
//        descriptors.add(index, descriptor); 
//    }
}
