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
import jmr.db.ListDB;
import java.util.ArrayList;
import java.lang.Math;
import jmr.descriptor.label.SingleLabelDescriptor;
import jmr.descriptor.DescriptorList;
import jmr.media.JMRExtendedBufferedImage;
import FernanDescriptors.GreyScaleDescriptor;
import FernanDescriptors.GreyScaleMediaDescriptor;
import FernanDescriptors.DescriptorLista;
import jmr.descriptor.label.LabelDescriptor;
import FernanDescriptors.MiListaDescriptores;
import DescriptorsFernan.LabelProperties;
import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.descriptor.color.MPEG7ScalableColor;
/**
 *
 * @author Fernando Roldán Zafra
 */

public class PruebasJMR {
    
    public static void changeNameFiles(){
    
        File folder = new File("D:/Dropbox/Apuntes/TFG/Imagenes");
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {

                if (listOfFiles[i].isFile()) {
                    
                    File f = new File("D:/Dropbox/Apuntes/TFG/Imagenes/"+listOfFiles[i].getName()); 
                    System.out.println(listOfFiles[i].getName());
                    f.renameTo(new File("D:/Dropbox/Apuntes/TFG/Imagenes/"+i+".jpg"));
                }
            }
    }
    public static void main(String[] args) {
        //changeNameFiles();
       
        BufferedImage img1 = null;
        BufferedImage img2 = null;
        BufferedImage img3 = null;
        BufferedImage img4 = null;
        BufferedImage img5 = null;
        
        try{
        File sourceImage = new File ("C:/Users/Fernando/Dropbox/Apuntes/TFG/Imagenes/"
            + Integer.toString(68) + ".jpg");
        img1 = ImageIO.read(sourceImage);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        try{
        File sourceImage = new File ("C:/Users/Fernando/Dropbox/Apuntes/TFG/Imagenes/"
            + Integer.toString(6) + ".jpg");
        img2 = ImageIO.read(sourceImage);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        try{
        File sourceImage = new File ("C:/Users/Fernando/Dropbox/Apuntes/TFG/Imagenes/"
            + Integer.toString(15) + ".jpg");
        img3 = ImageIO.read(sourceImage);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        
//        try{
//        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/blanco.png");
//        img4 = ImageIO.read(sourceImage);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        try{
//        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/negro.jpg");
//        img5 = ImageIO.read(sourceImage);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        try{
//        File sourceImage = new File ("D:/ImagenesTFG/Data/CLS-LOC/train/n03134739/"
//            + "n03134739_438.JPEG");
//        img1 = ImageIO.read(sourceImage);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        
//        try{
//        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/"
//           + "Alemania.jpg");
//        img2 = ImageIO.read(sourceImage);
//        
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        
//        try{
//        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/" +
//            "Belgica.jpg");
//        img3 = ImageIO.read(sourceImage);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
        
        //LabelDescriptor etiq = new LabelDescriptor(img1);
        
        //MiListaDescriptores imagen1 = new MiListaDescriptores(img1);
        //MiListaDescriptores imagen2 = new MiListaDescriptores(img2);        
        
        
//        
//        SingleLabelDescriptor singledescriptor1 = new SingleLabelDescriptor(img1);
//        SingleLabelDescriptor singledescriptor2 = new SingleLabelDescriptor(img3);
//        MPEG7ScalableColor dominant1 = new MPEG7ScalableColor(img1);
//        MPEG7ScalableColor dominant2 = new MPEG7ScalableColor(img3);
        //System.out.println(dominant1.compare(dominant2));
        //imagen1.add(Desc2);
        //double Dif = 0.0;
        //Dif = imagen1.compare(imagen2);
        
//        DescriptorList lista1 = new DescriptorList(img1);
//        lista1.add(singledescriptor1);
//        lista1.add(dominant1);
//        
//        DescriptorList lista2 = new DescriptorList(img3);
//        lista2.add(singledescriptor2);
//        lista2.add(dominant2);
        
        //System.out.println(imagen1.getEtiqueta());
        //System.out.println(imagen2.getEtiqueta());
        //System.out.println(Dif);
        
//        LabelProperties Desc1 = new LabelProperties(img1,SingleColorDescriptor.class,jmr.descriptor.color.MPEG7ScalableColor.class);
//        System.out.println(Desc1.toString());
        
        
        
        
        
        LabelProperties Desc1 = new LabelProperties(img2); Desc1.addProperty(SingleColorDescriptor.class);
        LabelProperties Desc2 = new LabelProperties(img3); Desc2.addProperty(SingleColorDescriptor.class);
        
        System.out.println(Desc1);
        System.out.println(Desc2);
        double dist = Desc1.compare(Desc2);
        
        System.out.println(dist);
//        Desc1.compare(Desc2);
//        Desc2.compare()
        
        
//        
//        MPEG7ScalableColor Desc1 = new MPEG7ScalableColor(img2);
//        System.out.println(Desc1.toString());
//        MPEG7ScalableColor Desc2 = new MPEG7ScalableColor(img3);
//        System.out.println(Desc2.toString());
//        System.out.println(Desc1.compare(Desc2).toString());
        /*
        System.out.println("--------------------------------------------------");
        //MPEG7DominantColors Desc3 = new MPEG7DominantColors(img1);
        //System.out.println(Desc3.getNumberOfDominantColors());
        //System.out.println(Desc3.getDominantColor(0).getColor());
        //MPEG7DominantColors Desc4 = new MPEG7DominantColors(img2);
        //System.out.println(Desc4.toString());
        System.out.println("--------------------------------------------------");
        JMRExtendedBufferedImage imgJMR1 = new JMRExtendedBufferedImage(img1);
        JMRExtendedBufferedImage imgJMR2 = new JMRExtendedBufferedImage(img2);
        MPEG7EdgeHistogram Desc5 = new MPEG7EdgeHistogram();
        Desc5.calculate(imgJMR1);
        System.out.println(Desc5.toString());
        MPEG7EdgeHistogram Desc6 = new MPEG7EdgeHistogram(imgJMR2);
        System.out.println(Desc6.toString());
        System.out.println("--------------------------------------------------");
        SingleLabelDescriptor Desc7 = new SingleLabelDescriptor(img1);
        System.out.println(Desc7.getLabel());
        SingleLabelDescriptor Desc8 = new SingleLabelDescriptor(img2);
        System.out.println(Desc8.getLabel());
        System.out.println("--------------------------------------------------");
        GreyScaleDescriptor Desc9 = new GreyScaleDescriptor(img3);
        System.out.println(Desc9.toString());
        GreyScaleMediaDescriptor Desc10 = new GreyScaleMediaDescriptor(img3);
        System.out.println(Desc10.toString());
        */
        
        
        
        
    }
    
}
