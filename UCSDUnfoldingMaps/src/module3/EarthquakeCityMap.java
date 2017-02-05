package module3;

//Java utilities libraries
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;
import java.util.Map;

//Processing library
import processing.core.PApplet;
import processing.core.PShape;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	private PShape rect;
	private PShape circleRed,circleYellow,circleGreen,circlePink;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	//****宣告List/Map或其他容器之後一定要new!!!****
	//用來裝csv檔案裡的國家縮寫跟平均年齡
	Map<String, Float> countryAndLifeExp;// = new HashMap<>();
	//用來裝countries.geo.json裡的國家features
	List<Feature> geoCountries;// = new ArrayList<>();
	//用來裝利用geoCountries所製造出來的markers
	List<Marker> geoMarkers;// = new ArrayList<>();
	
	public void setup() {
		size(950, 600, OPENGL);
		
		//畫個框框放地圖的資訊
		rect = createShape(RECT,20,50,160,260);//,20,50,
		rect.setFill(color(255,255,255));
		
		//畫三個不同顏色與大小的圓形分別代表不同的地震強度
		
		//指定要畫的形狀, 圓心, 和半徑
		circleYellow = createShape(ELLIPSE,30,110,12,12);
		//指定圖形的顏色
	    circleYellow.setFill(color(255,255,0));
	    
		circleRed = createShape(ELLIPSE,30,150,10,10);
		circleRed.setFill(color(255,0,0));
		
		circlePink = createShape(ELLIPSE,30,190,8,8);
		circlePink.setFill(color(255, 153, 153));
		
		circleGreen = createShape(ELLIPSE,30,230,5,5);
	    circleGreen.setFill(color(0,255,0));
	    
		
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			//map = new UnfoldingMap(this, 200,50,700,500, new Google.GoogleMapProvider());
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Microsoft.RoadProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "my_2.5_week.atom";//要放在data這個資料夾內
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		//*************將平均年齡視覺化, 以年齡來設定國家的顏色****************
		countryAndLifeExp = getCountryAndLifeExp("LifeExpectancyWorldBankModule3.csv");
		println("Loaded " + countryAndLifeExp.size() + " data entries");
		
		geoCountries = GeoJSONReader.loadData(this, "countries.geo.json");//存放jsonFile裡面的國家資料
		println("Length of geoCountries " + geoCountries.size());
		
		//用geoCountries生成markers, 也就是將地圖上的國家變成一個個的marker, 
		geoMarkers = MapUtils.createSimpleMarkers(geoCountries);
		println("Length of geoMarkers " + geoCountries.size());
		
		//記得要將marker加入地圖
		map.addMarkers(geoMarkers);//以國家為marker
	    //map.zoomToLevel(2);	    
	    
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    
	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    //這個list裡的每一個feature都有location及properties
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    System.out.println("Length of listEarthquakes:" + earthquakes.size());
	    
	    for(PointFeature pointFeature : earthquakes){//把earthquakes裡的每一個PointFeature變成SimplePointMarker,並加入markers
	    	SimplePointMarker simplePointMarker = new SimplePointMarker(pointFeature.getLocation(),pointFeature.getProperties());
	    	if(simplePointMarker.getProperty("age").toString().equals("Past Hour"))
	    	{
	    		System.out.println(simplePointMarker.getProperty("title").toString()+" "+ simplePointMarker.getProperty("age").toString());
	    		resizeAndColorMarker(simplePointMarker,12, 255,255,0);
	    	}
	    	else if((float)simplePointMarker.getProperty("magnitude") >= THRESHOLD_MODERATE)
	    	{
	    		resizeAndColorMarker(simplePointMarker,10, 255,0,0);
	    	}
	    		 
	    	else if((float)simplePointMarker.getProperty("magnitude") >= THRESHOLD_LIGHT && (float)simplePointMarker.getProperty("magnitude") < THRESHOLD_MODERATE)
	    	{
	    		resizeAndColorMarker(simplePointMarker,8, 255, 153, 153);
	    	}
	    		
	    	else if((float)simplePointMarker.getProperty("magnitude") < THRESHOLD_LIGHT)
	    	{
	    		resizeAndColorMarker(simplePointMarker,5, 0, 255, 0);
	    	}
	    	
	    	markers.add((Marker)simplePointMarker);//該方法的參數必須是type marker或marker list
	    }
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    }
	    
	    // Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	    //int yellow = color(255, 255, 0);
	    
	    map.addMarkers(markers);//以地震的經緯度為marker
	    shadeCountry();
	   
	    
	    //TODO: Add code here as appropriate
	}


	private void resizeAndColorMarker(SimplePointMarker spMarker,int radius,int r, int g, int b) {
		spMarker.setRadius(radius);
		spMarker.setColor(color(r,g,b));
	}
	
	
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// TODO: Implement this method and call it from setUp, if it helps
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// finish implementing and use this method, if it helps.
		return new SimplePointMarker(feature.getLocation());
	}
	
	public void draw() {

		background(200,200,200);
		fill(color(0,0,0));//必須指定寫字的顏色
	    textSize(10);
	    
	    //外框
	    addKey(rect, "Earthquake Key", 63,75);
		//框內的指示圖形
		addKey(circleYellow, "happened past hour",63,118);
		addKey(circleRed, "5.0+ Magnitude", 63,158);
		addKey(circlePink,"4.0+ Magnitude", 63,198);
		addKey(circleGreen,"Below 4.0", 63,238);
		
	    map.draw();
	    
	}

	private void shadeCountry() {
		//有了平均年齡分布狀況以及國家地理資料後, 開始以國家縮寫來比對兩份資料來源
		for(Marker m : geoMarkers){
			String countryId = m.getId();//每個marker的國家縮寫
			//如果這個map中有個key是countryId
			if(countryAndLifeExp.containsKey(countryId)){
				//數字越高越藍, 越小越紅
				Float lifeExp = countryAndLifeExp.get(countryId);
				int color = (int)map(lifeExp,40,90,10,255);
				m.setColor(color(255-color,100,color));
			}
			else{
				m.setColor(color(200,200,200));//無對應值的國家就設成深灰色
			}			
		}	
	}

	//***********************	
	private Map<String, Float> getCountryAndLifeExp(String fileName) {
		Map<String, Float> map = new HashMap<>();
		String[] rows = loadStrings(fileName);//將csv檔案內每一行讀進rows;
		
		for(String row : rows){//分析每一行資料
			String[] columns = row.split(",");
			
			//字串比較務必用.equals(), == 用在比對是否是同一個物件
			if(columns.length == 6 && !columns[5].equals("..")){
				Float val = Float.parseFloat(columns[5]);
				map.put(columns[4], val);
			}
		}
		return map;
	}

	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey(PShape shape, String text , int x,int y) 
	{	//將setUp()所create的圖形實際畫入PApplet, 並加上文字說明
		shape(shape);
		text(text,x,y);
	}
}
