/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JMR.Expansion.Grid;

import java.util.ArrayList;
import jmr.descriptor.Comparator;
import jmr.descriptor.GriddedDescriptor;

/**
 *
 * Class that implements the comparation between two griddedDescriptor objects 
 * In this case the comparation will take one tile of one grid each time and will compare 
 * this tile with all the tiles in the other grid, looking for the minimum. 
 * Once each tile have search its minimum the comparator search for the minimum of these minimums
 * 
 * @author Fernando Roldán Zafra
 */
public class MinimumGridComparator implements Comparator <GriddedDescriptor, Double>{

    /**
     * Apply this comparator
     * @param t The first grid
     * @param u The second grid
     * @return It returns the minimum diference between all the minimums associated to each tile.
     */
    @Override
    public Double apply(GriddedDescriptor t, GriddedDescriptor u) {
        
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
