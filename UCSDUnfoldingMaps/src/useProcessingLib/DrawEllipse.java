package useProcessingLib;
import processing.core.*;
public class DrawEllipse extends PApplet {
	//PShape square;
	public void setup()
	{
		size(400,400);
		//background(255,255,0);
		//square = createShape(RECT,0,0,50,50);
	}
	
	public void draw()
	{
		//shape(square,25,25);
		//畫臉
		fill(255,255,0);//先用fill()選好顏色,再用ellipse()畫圓 
		ellipse(200,200,390,390);//(圓心x,圓心y, (半徑)寬, (半徑)高)
		//畫眼睛
		fill(0,0,0);
		ellipse(120,150,50,50);
		ellipse(280,150,50,50);
		//畫腮紅
		fill(200,50,50);
		ellipse(80,240,40,40);
		ellipse(320,240,40,40);
		//畫嘴巴
		noFill();
		//(弧度中心x, 弧度中心y,扇形的全寬, 扇形的全高, 起始點的角度(注意弧度是從此點[的x是圓心x+扇形的全寬/2=(200+160/2,300)]開始順時鐘畫出), 終點的角度(PI表示180度, 2_PI是360度))
		arc(200,300,160,100,0,PI);
		//測試畫弧度的起點(360,300)與終點(120,300)
		fill(255,0,0);
		ellipse(280,300,5,5);
		ellipse(120,300,5,5);
	}
}
