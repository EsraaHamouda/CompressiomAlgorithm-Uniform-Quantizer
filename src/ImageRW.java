/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectInputStream.GetField;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

 
public class ImageRW 
{	
    public static int width=0;
    public static int height=0;
    //to use in read function
    public static int widthFromFile=0;
    public static int heightFromFile=0;
    public static int numOfLevelFromFile = 0;
    public static int imgFromFile[][];
    
    public void compress(String path , int numOfLevels)
    {
    	
    }
    
    public static  Vector<Rang> formatRange(Vector<Rang>  ranges){
    	for (int i = 0; i < ranges.size(); i++) {
    		
    		int ava = (ranges.get(i).start+ranges.get(i).end)/2;
    		ranges.get(i).midPoint = ava+1;
    		ranges.get(i).code = i;
    	}
    	return ranges;
    }
    public static int getValueCompress(Vector<Rang> ranges , int currentPixel){ // this original values
    	for (int i = 0; i < ranges.size(); i++) {
			if(currentPixel>=ranges.get(i).start &&currentPixel<=ranges.get(i).end )
			{
				return ranges.get(i).code;
			}
		}
    	return 0;
    }
    public static int getValueDeCompress(Vector<Rang> ranges , int currentPixel){ // this values from 0-15 ex:
    	for (int i = 0; i < ranges.size(); i++) {
    		if(currentPixel ==ranges.get(i).code ){
			
				return ranges.get(i).midPoint;
			}
		}
    	return 0;
    }
    public static int[][] readImage(String filePath)
    {
        File file=new File(filePath);
        BufferedImage image=null;
        try
        {
            image=ImageIO.read(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

          width=image.getWidth();
          height=image.getHeight();
        int[][] pixels=new int[height][width];

        for(int x=0;x<width;x++)
        {
            for(int y=0;y<height;y++)
            {
                int rgb=image.getRGB(x, y);
                int alpha=(rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb >> 0) & 0xff;

                pixels[y][x]=r;
            }
        }

        return pixels;
    }

    public static void writeImage(int[][] pixels,String outputFilePath)
    {
        File fileout=new File(outputFilePath);
        BufferedImage image2=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB );

        for(int x=0;x<width ;x++)
        {
            for(int y=0;y<height;y++)
            {
            	
                image2.setRGB(x,y,(pixels[y][x]<<16)|(pixels[y][x]<<8)|(pixels[y][x])); //16 60 // 8 80
            }
        }
        try
        {
            ImageIO.write(image2, "jpg", fileout);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
   
    public static void main(String[] args) 
    {

        //int img[][]=readImage("E:\\java examples\\Uniform Quantizer\\src\\lena.jpg");
    	Scanner in = new Scanner(System.in);
    	Vector<Rang> ranges = new Vector<Rang>(); 
    	System.out.println("Enetr num ");
    	int numOfLevels = in.nextInt();
    	
    	int fullScaler = 256;
    	int step = fullScaler/numOfLevels;
    	int start = 0;
    	while(start <fullScaler)
    	{
    		Rang r = new Rang();
    		r.start = start;
    		start = start+ step-1;
    		r.end = start;
    		start++;
    		ranges.add(r);
    		
    	}
    	
    	ranges = formatRange(ranges);
    	for (int i = 0; i < ranges.size(); i++) {
			System.out.println(ranges.get(i).toString());
		}
    	
    	//wirteOnFile("QBinary.txt","AvaDQ.txt", ranges); // values which i will use in Compression
    	
    	
    	
    	int currentCode = 0;
    	Vector<Integer> imgVluesAfterQuantizing = new Vector<Integer>();
    	int img[][]=readImage("lena.jpg");
    	int imgAfterCompression[][]=new int[height][width];
    	for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				currentCode = getValueCompress(ranges, img[i][j]);
				imgAfterCompression[i][j] = currentCode;
			}
		}
    	wirteCompressionResOnFile("imgValAfterCompression.txt", numOfLevels, width, height, imgAfterCompression);
    	
    	
    	////////////////All above Compression is done and is wittin on file///////////////
    	////////////////Decompression below////////////////////
    	
    	
    	
    	//readFromFile("imgValAfterCompression.txt");
    	
    	/*System.out.println("numOfLevelFromFile  "+numOfLevelFromFile);
    	System.out.println("widthFromFile  "+widthFromFile);
    	System.out.println("heightFromFile  "+heightFromFile);*/
    	
    	for (int i = 0; i <widthFromFile ; i++) {
			for (int j = 0; j < heightFromFile; j++) {
				System.out.println(i+"  "+ imgAfterCompression[i][j]+"  ");
			}
			System.out.println();
		}
    	
//    	for (int i = 0; i < widthFromFile; i++) {
//			for (int j = 0; j < heightFromFile; j++) {
//				System.out.println(i+"  "+ imgFromFile[i][j]+"  ");
//			}
//			System.out.println();
//		}
    	
    	/**
    	 * 
    	 * 
    	 * 
    	 * */
    	
    	//System.out.println("gggggggggggggggg"+ranges.toString());
    	int currentMidValue = 0;
    	for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				currentMidValue = getValueDeCompress(ranges, imgAfterCompression[i][j]);
				imgAfterCompression[i][j] = currentMidValue;
			}
		}
    	
    	
    	writeImage(imgAfterCompression, "copy1imgAfterCompression.jpg");
    	int sub = 0;
    	double sum = 0;
    	int sqr = 0;
    	for (int i = 0; i < width; i++) {
			for (int j = 0; i < height; i++) {
			//System.out.println(img[i][j]);
				sub = img[i][j] - imgAfterCompression[i][j];
				System.out.println("Sub = "+sub);
				sqr= sub*sub;
				sum+=sqr;
			}
				
		}
    	double MSE = sum / (width*height);
    	System.out.println("MSE = "+ MSE);
    	
    	//writeImage(img, "copy1.jpg");
    	/*
			try (PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter("imgVluesAfterQuantizing.txt", false)))) {
				
				
				for (int i = 0; i < imgVluesAfterQuantizing.size(); i++) {
					System.out.println(i+"  "+ imgVluesAfterQuantizing.get(i));
					String strPixel = String.valueOf(imgVluesAfterQuantizing.get(i));
					out.print(strPixel);
					out.println();
				}
				

			} catch (IOException e) {

			}

		*/	
		
    	/////////////image is compressed///////////////
    	/*int currentQ = 0;
    	int img2[][]=readImage("copy.jpg");
    	for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				currentQ = getValue(ranges, img[i][j]);
				img2[i][j] = currentQ;
			}
		}
    	
    	writeImage(img2, "copyOriginal.jpg");
    	*/
    	
    }
    /**to write 0 & 1 0==>7*/
    public static void wirteCompressionResOnFile(String path1,int numOfLevels,int width, int height , int[][] img) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(path1, false)))) {
			
			out.println(String.valueOf(numOfLevels));
			out.println(String.valueOf(width));
			out.println(String.valueOf(height));
			
			for (int i = 0; i < width; i++) {
				for (int j = 0; i < height; i++) {
				out.println(String.valueOf(img[i][j]));
				
				}
					
			}
			
			for (int i = 0; i < width; i++) {
				for (int j = 0; i < height; i++) {
				System.out.println(img[i][j]);
				
				}
					
			}
			
			out.close();
			

		} catch (IOException e) {

		}
		
	}
    
	/*public static void readFromFile(String path) {
		String line = "";
		// String path =
		File file1 = new File(path);
		int imgFromFile[][] = new int [width][height];
		BufferedReader read;
		try {
			read = new BufferedReader(new FileReader(file1));
			numOfLevelFromFile = Integer.parseInt(read.readLine());
			widthFromFile = Integer.parseInt(read.readLine());
			heightFromFile= Integer.parseInt(read.readLine());
			imgFromFile = new int[widthFromFile][heightFromFile];
			for (int i = 0; i < widthFromFile; i++) {
				for (int j = 0; j < heightFromFile; j++) {
					
					if(line!= null)
					line = read.readLine();
						//System.out.println(line);
						imgFromFile[i][j] = Integer.parseInt(line);
					
				}
			}
	    	
			read.close();
		} catch (IOException e) {

		}
		
		
		

    	
	//	return imgFromFile;
	}*/

}

