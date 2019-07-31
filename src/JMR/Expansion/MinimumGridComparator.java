/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JMR.Expansion;

import java.util.ArrayList;
import jmr.descriptor.Comparator;
import jmr.descriptor.GriddedDescriptor;

/**
 *
 * @author Fernando Roldán Zafra
 */
public class MinimumGridComparator implements Comparator <GriddedDescriptor, Double>{

    @Override
    public Double apply(GriddedDescriptor t, GriddedDescriptor u) {
        
        /*
        Double dist;
        Double distMin = (Double)t.getTileDescriptor(0).compare(u.getTileDescriptor(0));
        for(int i = 0; i<t.getNumTiles(); i++){
            for(int j = 0; j<u.getNumTiles(); j++){
                dist = (Double)t.getTileDescriptor(i).compare(u.getTileDescriptor(j));
                if(dist < distMin){
                    distMin = dist;
                }
            }
        }
        return distMin;
        */
        ArrayList<Double> min_list = new ArrayList<Double>();
        Double dist;
        Double dist_min; 
        Double dist_min_global;
        
        for(int i = 0; i<t.getNumTiles(); i++){
            dist = (Double)t.getTileDescriptor(i).compare(u.getTileDescriptor(0));
            dist_min = dist;
            for(int j = 0; j<u.getNumTiles(); j++){
                dist = (Double)t.getTileDescriptor(i).compare(u.getTileDescriptor(j));
                if(dist < dist_min)
                    dist_min = dist;
            }
            min_list.add(dist_min);
        }
        
        dist_min_global = min_list.get(0);
        for(Double i : min_list){
            if(i < dist_min_global){
                dist_min_global = i;
            }
        }
        return dist_min_global;
    }
    
}
