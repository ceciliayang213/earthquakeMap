package secondSun;
import processing.core.*;
public class DrawEllipse extends PApplet {
	
	PImage img;
	
	public void setup()
	{
		size(400,400);
		background(255);//set background color to be white
		stroke(0);//set pen color to be black
		img = loadImage("http://www.globaltravelerreview.com/wp-content/uploads/2014/03/la-jolla-beach.jpg","jpg");
		img.resize(0,height);
		image(img,0,0);//to display img;
		
	}
	
	public void draw()
	{
		int[] color = sunColorSec(second());//second() is a built-in method using system clock, values from 0 ~ 59
		//minute(): 0~59 ; hour(): 0~23;
		fill(color[0],color[1],color[2]);
		ellipse(width/4,height/5,width/4,height/5);//width and height refer to those of the PApplet window
		
	}

	public int[] sunColorSec(float seconds) {
		int[] rgb = new int[3];
		//scale the brightness of yellow based on the seconds. 30 is dark. 0 is bright yellow.
		float diffFrom30 = Math.abs(30-seconds);//abs return absolute value: values from 0 ~ 30
		float ratio = diffFrom30 / 30;//between 0/30 and 30/30: 0 ~ 1
		rgb[0] = (int)(255*ratio);
		rgb[1] = (int)(255*ratio);
		rgb[2] = 0;
		System.out.println(second() + " " + rgb[0] +" " +rgb[1] +" "+ rgb[2]);
		return rgb;
	}
}
