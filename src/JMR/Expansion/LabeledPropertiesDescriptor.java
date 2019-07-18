/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JMR.Expansion;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import jmr.descriptor.DescriptorList;
import jmr.descriptor.color.SingleColorDescriptor;
import jmr.descriptor.label.LabelDescriptor;

/**
 *
 * @author Fernando Roldán Zafra
 */
public class LabeledPropertiesDescriptor extends LabelProperties<BufferedImage>{
    public LabeledPropertiesDescriptor(BufferedImage img){
        super(img);
        this.classProperties = new Class[]{LabelDescriptor.class};
        init(img);
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
            SingleColorDescriptor very_light_red = new SingleColorDescriptor(new Color(255,102,102));
            SingleColorDescriptor light_red = new SingleColorDescriptor(new Color(255,51,51));
            SingleColorDescriptor red = new SingleColorDescriptor(Color.RED);
            SingleColorDescriptor dark_red = new SingleColorDescriptor(new Color(204,0,0));
            SingleColorDescriptor very_dark_red = new SingleColorDescriptor(new Color(153,0,0));

            SingleColorDescriptor very_light_blue = new SingleColorDescriptor(new Color(51,204,255));
            SingleColorDescriptor light_blue_1 = new SingleColorDescriptor(new Color(173,216,230));
            SingleColorDescriptor light_blue = new SingleColorDescriptor(new Color(51,153,255));
            SingleColorDescriptor blue = new SingleColorDescriptor(Color.BLUE);
            SingleColorDescriptor dark_blue = new SingleColorDescriptor(new Color(0,0,204));
            SingleColorDescriptor very_dark_blue = new SingleColorDescriptor(new Color(0,0,153));

            SingleColorDescriptor very_light_green = new SingleColorDescriptor(new Color(102,255,102));
            SingleColorDescriptor light_green = new SingleColorDescriptor(new Color(0,255,51));
            SingleColorDescriptor green = new SingleColorDescriptor(Color.GREEN);
            SingleColorDescriptor dark_green = new SingleColorDescriptor(new Color(0,153,0));
            SingleColorDescriptor very_dark_green = new SingleColorDescriptor(new Color(0,102,0));

            SingleColorDescriptor very_light_yellow = new SingleColorDescriptor(new Color(255,255,204));
            SingleColorDescriptor light_yellow = new SingleColorDescriptor(new Color(255,255,153));
            SingleColorDescriptor yellow = new SingleColorDescriptor(Color.YELLOW);
            SingleColorDescriptor dark_yellow = new SingleColorDescriptor(new Color(204,204,0));
            SingleColorDescriptor very_dark_yellow = new SingleColorDescriptor(new Color(153,153,0));

            SingleColorDescriptor black = new SingleColorDescriptor(Color.BLACK);

            ArrayList<SingleColorDescriptor> colors = new ArrayList<SingleColorDescriptor>(
              Arrays.asList(very_light_red, light_red, red, dark_red, very_dark_red,
                      very_light_blue, light_blue_1, light_blue, blue, dark_blue, very_dark_blue,
                      very_light_green, light_green, green, dark_green, very_dark_green, 
                      very_light_yellow, light_yellow, yellow, dark_yellow, very_dark_yellow, 
                      black)
            );
            java.awt.image.BufferedImage image = (java.awt.image.BufferedImage) img;
            SingleColorDescriptor singlecolor = new SingleColorDescriptor(image);
            double single_color_red = singlecolor.getColor().getRed();
            double single_color_green = singlecolor.getColor().getGreen();
            double single_color_blue = singlecolor.getColor().getBlue();
            double greyDist = Math.sqrt(Math.pow(single_color_red - single_color_green, 2.0) + Math.pow(single_color_blue - single_color_green, 2.0));
            
            LabelDescriptor label = null;
            if(greyDist <= 5 && single_color_red <= 230){
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
                    System.out.println(colors.get(i).getColor().toString() + " - " + singlecolor.getColor().toString() + dist);
                }
                System.out.println("Dist min " + min_index);
                

                if(min_index >= 0 && min_index <= 4){
                    label = new LabelDescriptor("Red");
                }else if(min_index >= 5 && min_index <= 10){
                    label = new LabelDescriptor("Blue");
                }else if(min_index >= 11 && min_index <= 15){
                    label = new LabelDescriptor("Green");
                }else if(min_index >= 16 && min_index <= 20){
                    label = new LabelDescriptor("Yellow");
                }else if(min_index == 21){
                    label = new LabelDescriptor("Black");
                }
            }
            

            System.out.println(this.properties);
            this.properties.add(label);
        }
    }
}
