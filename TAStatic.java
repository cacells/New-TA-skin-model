/*
 * This is a class that contains main and
 * will call the TAGrid simulation, also displaying the results 
 * Graphically in a window
 * TA is for Transit Amplifying
 */


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.*;
 
public class TAStatic extends JFrame implements Runnable {

    TAGridStatic experiment;
    Random rand = new Random();    
	Thread runner;
    Container mainWindow;
	CAImagePanel CApicture;
	Image backImg1;
	Graphics backGr1;
	Image backImg2;
	Graphics backGr2;
	JProgressBar progressBar;
	int scale = 20;//beth: could set to 1. Makes the colour transitions better?
	int border = 20;
	int iterations;
	int gSize;
    Colour palette = new Colour();
	int[] colorindices = {45,1,5,4,2,54};//{0,1,2,54,4,5};
	int nnw = colorindices.length-1;
//    Color[] colours = {Color.white,Color.black,Color.green,Color.blue,Color.yellow,Color.red,Color.pink};
    Color[] javaColours;
    double[][] epsColours;
    Color[] colours = {Color.black,Color.white,Color.green,Color.blue,Color.yellow,Color.red,Color.pink};
    boolean writeImages = false;
    int maxIters = 100;

	public TAStatic(int size, int maxC, double frac) {
	    gSize=size;
		experiment = new TAGridStatic(size, maxC, frac);
		//setSize(400, 500);//window (Frame) size
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		backImg1 = createImage(scale * size, scale * size);
		backGr1 = backImg1.getGraphics();
		backImg2 = createImage(scale * size, scale * size);
		backGr2 = backImg2.getGraphics();
		setpalette();
		
	    int wscale = 6;//scale for main panel
	    int btnHeight = 480-384;//found by trial and error - must be a better way!
	    //although no buttons yet
	    int wh = (gSize*1)*wscale + 2*border;// +btnHeight;//mainWindow height
	    int ww = (gSize*2)*wscale + 3*border;//mainWindow width   
	    
		mainWindow = getContentPane();
		mainWindow.setLayout(new BorderLayout());
		setSize(ww,wh);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
        CApicture = new CAImagePanel(ww,wh);
        CApicture.setBorder(border);
        CApicture.rowstoShow = gSize;
        mainWindow.add(CApicture,BorderLayout.CENTER);
		setVisible(true);
	    progressBar = new JProgressBar(0,maxIters);
	    progressBar.setValue(25);
	    progressBar.setStringPainted(true);
	    mainWindow.add(progressBar, BorderLayout.SOUTH);
		setVisible(true);
		
	}
	//new ones
	public void drawCA() {
		int a;
		for (TACell c : experiment.tissue){
			a = c.type;
			CApicture.drawCircleAt(c.home.x, c.home.y, javaColours[a], 1);
		}
	    CApicture.updateGraphic();
	}
	public void drawCAstain() {
		double cstain;
		for (TACell c : experiment.tissue){
			//if ((c.stain < minstain) && (c.type>0)) minstain = c.stain;
			cstain = c.stain;
			CApicture.drawCircleAt(c.home.x, c.home.y, palette.Javashades(cstain), 2);
		}
	    //outputImage();
	    CApicture.updateGraphic();
	}
	//old ones
	public void olddrawCA() {
		backGr1.setColor(Color.white);
		int a;
		backGr1.fillRect(0, 0, scale*gSize, scale*gSize);
		for (TACell c : experiment.tissue){
			a = c.type;
			if(a<7){
				backGr1.setColor(javaColours[a]);
			}else{
				backGr1.setColor(Color.orange);
			}
			backGr1.fillOval(c.home.x * scale, c.home.y * scale, scale, scale);
		}
        backGr2.drawImage(backImg1, 0, 0, gSize * scale, gSize * scale, 0, 0, scale * gSize, scale * gSize, this);
	    repaint();//beth: calls for a screen repaint asap
	}
	public void olddrawCAstain() {
		backGr1.setColor(Color.white);
		double cstain;
		backGr1.fillRect(0, 0, scale*gSize, scale*gSize);
		//double minstain = 1.0;
		for (TACell c : experiment.tissue){
			//if ((c.stain < minstain) && (c.type>0)) minstain = c.stain;
			cstain = c.stain;
			backGr1.setColor(palette.Javashades(cstain));
			backGr1.fillOval(c.home.x * scale, c.home.y * scale, scale, scale);
		}
		//System.out.println("min stain: "+minstain);
        backGr2.drawImage(backImg1, 0, 0, gSize * scale, gSize * scale, 0, 0, scale * gSize, scale * gSize, this);
	    //outputImage();
        repaint();//beth: calls for a screen repaint asap
	}



/*	public void paint(Graphics g) {
		if ((backImg2 != null) && (g != null)) {
			g.drawImage(backImg2, 0, 100, 400,500,0,0,scale*gSize,scale*gSize,Color.white,this);
			//g.drawImage(backImg2, 0, 0, this.getSize().width, this.getSize().height, -10, -92, scale * gSize + 10, scale * gSize + 10, this);
			//g.drawImage(backImg2, 0, scale+10, this.getSize().width, scale*2, -10, 0, scale * gSize + 10, scale,this);
		}
	}*/

	public void initialise(){
			CApicture.setScale(gSize,gSize,scale,gSize,gSize,scale);
      	    CApicture.clearCAPanel(1);
      	    CApicture.clearCAPanel(2);
      	    CApicture.clearParent();
		    iterations=0;
	}
	
	
	public void start() {
		initialise();
		if (runner == null) {
			runner = new Thread(this);
		}
		runner.start();
	}


	public void run() {
		while (iterations < maxIters) {
		    progressBar.setValue(iterations);
			if (runner == Thread.currentThread()) {
			experiment.iterate();
			//if (iterations < 89) drawCA();
			//else 
			if (iterations == 0) experiment.stain();
			if (iterations%2==0){
			drawCA();
			drawCAstain();

			if (writeImages) CApicture.writeImage(iterations);
			}
			iterations++;
			//if((iterations%5)==0)postscriptPrint("TA"+iterations+".eps");
			// This will produce a postscript output of the tissue
			}
		}
	}


	public void postscriptPrint(String fileName) {
		int xx;
		int yy;
		int state;
		boolean flag;
		try {
			java.io.FileWriter file = new java.io.FileWriter(fileName);
			java.io.BufferedWriter buffer = new java.io.BufferedWriter(file);
			System.out.println(fileName);
			buffer.write("%!PS-Adobe-2.0 EPSF-2.0");
			buffer.newLine();
			buffer.write("%%Title: test.eps");
			buffer.newLine();
			buffer.write("%%Creator: gnuplot 4.2 patchlevel 4");
			buffer.newLine();
			buffer.write("%%CreationDate: Thu Jun  4 14:16:00 2009");
			buffer.newLine();
			buffer.write("%%DocumentFonts: (atend)");
			buffer.newLine();
			buffer.write("%%BoundingBox: 0 0 300 300");
			buffer.newLine();
			buffer.write("%%EndComments");
			buffer.newLine();
			for (TACell c : experiment.tissue){
				if(c.type>0){
					xx = (c.home.x * 4) + 20;
					yy = (c.home.y * 4) + 20;
					if (c.proliferated) {
						buffer.write("newpath " + xx + " " + yy + " 1.5 0 360 arc fill\n");
						buffer.write("0 setgray\n");
						buffer.write("newpath " + xx + " " + yy + " 1.5 0 360 arc  stroke\n");
					} else {
						buffer.write("0.75 setgray\n");
						buffer.write("newpath " + xx + " " + yy + " 1.5 0 360 arc fill\n");
					}
				}
			}
			buffer.write("showpage");
			buffer.newLine();
			buffer.write("%%Trailer");
			buffer.newLine();
			buffer.write("%%DocumentFonts: Helvetica");
			buffer.newLine();
			buffer.close();
		} catch (java.io.IOException e) {
			System.out.println(e.toString());
		}
	}
    public void setpalette(){
    	int ind = colorindices.length;
    	javaColours = new Color[ind];
    	epsColours = new double[ind][3];
    	for (int i=0;i<ind;i++){
    		//System.out.println("color index "+colorindices[i]);
    		javaColours[i] = palette.chooseJavaColour(colorindices[i]);
    		epsColours[i] = palette.chooseEPSColour(colorindices[i]);
    	}
    }

	public static void main(String args[]) {
		double initalSeed = 0.1;
		if(args.length>0){
			initalSeed = Double.parseDouble(args[0]);
			TAStatic s = new TAStatic(64, 4, initalSeed);
			s.start();
		}else{
			TAStatic s = new TAStatic(64, 4, 0.10);
			s.start();
		}
	}
}

