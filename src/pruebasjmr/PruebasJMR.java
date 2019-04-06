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
import PruebaDescriptores.GreyScaleDescriptor;
import PruebaDescriptores.GreyScaleMediaDescriptor;
import PruebaDescriptores.DescriptorLista;
import jmr.descriptor.label.LabelDescriptor;
import PruebaDescriptores.MiListaDescriptores;
import TFGDescriptors.LabelProperties;
import TFGDescriptors.LabelProperties.EqualLabelsComparator;
import TFGDescriptors.LabelProperties.WeightedLabelComparator;
import TFGDescriptors.LabelProperties.WeightedPropertiesComparator;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.learning.KerasClassifier;

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

    public static void main(String[] args) throws Exception {
        //changeNameFiles();
       
        BufferedImage img1 = null;
        BufferedImage img2 = null;
        BufferedImage img3 = null;
        BufferedImage img4 = null;
        BufferedImage img5 = null;
        
//        try{
//        File sourceImage = new File ("C:/Users/Fernando/Dropbox/Apuntes/TFG/Imagenes/"
 //           + Integer.toString(68) + ".jpg");
 //       img1 = ImageIO.read(sourceImage);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
        
//        try{
//        File sourceImage = new File ("C:/Users/Fernando/Dropbox/Apuntes/TFG/Imagenes/"
//            + Integer.toString(6) + ".jpg");
//        img2 = ImageIO.read(sourceImage);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
        
//        try{
//        File sourceImage = new File ("C:/Users/Fernando/Dropbox/Apuntes/TFG/Imagenes/"
//            + Integer.toString(15) + ".jpg");
//        img3 = ImageIO.read(sourceImage);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
        
        
        try{
        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/121.jpg");
        img4 = ImageIO.read(sourceImage);
        } catch (IOException e){
            e.printStackTrace();
        }
        try{
        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/106.jpg");
        img5 = ImageIO.read(sourceImage);
        } catch (IOException e){
            e.printStackTrace();
        }
        try{
        File sourceImage = new File ("D:/ImagenesTFG/Data/CLS-LOC/train/n03134739/"
            + "n03134739_438.JPEG");
        img1 = ImageIO.read(sourceImage);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        try{
        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/"
           + "Alemania.jpg");
        img2 = ImageIO.read(sourceImage);
        
        } catch (IOException e){
            e.printStackTrace();
        }
       
        try{
        File sourceImage = new File ("D:/Dropbox/Apuntes/TFG/Imagenes/" +
            "Belgica.jpg");
        img3 = ImageIO.read(sourceImage);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        File sourceCnn = new File("D:/Dropbox/Apuntes/TFG/cnn.vgg16/cnn.vgg16.xml");
        
        KerasClassifier clasificador = KerasClassifier.loadModel(sourceCnn);
        
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
        
//      LabelProperties Desc1 = new LabelProperties(img1,SingleColorDescriptor.class,jmr.descriptor.color.MPEG7ScalableColor.class);
//      System.out.println(Desc1.toString());
//      SingleColorDescriptor Desc1 = new SingleColorDescriptor(img5);
//      SingleColorDescriptor Desc2 = new SingleColorDescriptor(img4);
//      System.out.println(" -> "+ Desc1.compare(Desc2));
        
        
//        MPEG7ColorStructure Desc1 = new MPEG7ColorStructure(img5);
//        MPEG7ColorStructure Desc2 = new MPEG7ColorStructure(img4);
//        System.out.println(" -> "+ Desc1.compare(Desc2));

//        MPEG7ScalableColor Desc1 = new MPEG7ScalableColor(img5);
//        MPEG7ScalableColor Desc2 = new MPEG7ScalableColor(img4);
//        System.out.println(" -> "+ Desc1.compare(Desc2));
        
        
                                clasificador.setWeighted(true);
                                clasificador.setThreshold(0.15);
                                LabelProperties Desc1 = new LabelProperties(img1, clasificador); Desc1.addProperty(SingleColorDescriptor.class);
                                System.out.println(Desc1);
//LabelProperties Desc2 = new LabelProperties(img3); Desc2.addProperty(SingleColorDescriptor.class);


//                                SoftInclusionComparator c = new SoftInclusionComparator();
//                                Desc1.setComparator(c);

//                                double dist = (double) Desc1.compare(Desc2);
//                                System.out.println(dist);
//        Desc1.compare(Desc2);
//        Desc2.compare()
    }
    
}
