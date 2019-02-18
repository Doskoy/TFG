/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasjmr;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import jmr.descriptor.color.SingleColorDescriptor;
/**
 *
 * @author Fernando Roldán Zafra
 */
public class PruebasJMR {

    public static void main(String[] args) {
       BufferedImage img = null;
       Image image = null;
       try{
           File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/images.jpg");
           img = ImageIO.read(sourceImage);
       } catch (IOException e){
           e.printStackTrace();
       }
       
       SingleColorDescriptor color = new SingleColorDescriptor(img);
       System.out.println(color.toString());
       
       
       
    }
    
}
