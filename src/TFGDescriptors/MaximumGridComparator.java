/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TFGDescriptors;

import jmr.descriptor.Comparator;
import jmr.descriptor.GriddedDescriptor;

/**
 *
 * @author Fernando Roldán Zafra
 */
public class MaximumGridComparator implements Comparator<GriddedDescriptor, Double> {

    @Override
    public Double apply(GriddedDescriptor t, GriddedDescriptor u) {
        Double dist;
        Double distMin = (Double)t.getTileDescriptor(0).compare(u.getTileDescriptor(0));
        for(int i = 0; i<t.getNumTiles(); i++){
            for(int j = 0; j<u.getNumTiles(); j++){
                dist = (Double)t.getTileDescriptor(i).compare(u.getTileDescriptor(j));
                if(dist > distMin){
                    distMin = dist;
                }
            }
        }
        return distMin;
    }
}

