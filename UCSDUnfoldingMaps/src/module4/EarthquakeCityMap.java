package module4;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;
	private int land,ocean,china;
	private int polygon,multipolygon;
	// A List of country markers
	private List<Marker> countryMarkers;

	private List<PointFeature> earthquakes;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		//size(900, 700, OPENGL);
		size(1350,800,OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 150, 50, 1100, 750, new Microsoft.RoadProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		map.zoomToLevel(2);
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		earthquakesURL = "test2.atom";
		
		// WHEN TAKING THIS QUIZ: Uncomment the next line
		//earthquakesURL = "quiz1.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);		
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {//isLand又會呼叫isInCountry, 如果是true, 就讓feature增加一個property是country:國家名稱
		    quakeMarkers.add(new LandQuakeMarker(feature));
		    //land++;
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		    //ocean++;
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	}  // End setup
	
	
	public void draw() {
		background(153, 206, 255);
		map.draw();
		addKey();
		
	}
	
	// helper method to draw key in GUI
	// TODO: Update this method as appropriate
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		rect(25, 50, 150, 280);
		
		fill(0);//寫字之前先指定顏色
		textAlign(LEFT, CENTER);
		textSize(15);
		text("Earthquake Key", 40, 75);
		text("Size - Magnitude", 40, 188);
		
		textSize(12);
		text("City Marker", 75,105);
		text("Land Quake",75,132);
		text("Ocean Quake",75,160);
		text("Deep", 75, 213);
		text("Intermediate", 75, 243);
		text("Shallow", 75, 273);
		text("Past Hour", 75, 307);
		
		triangle(50,100,42,113,58,113);
		fill(0);
		
		fill(255);
		ellipse(50,135,15,15);
		rect(43,155,14,14);		
		
		fill(color(255, 0, 0));
		ellipse(50, 215, 15, 15);
		
		fill(color(0, 0, 255));
		ellipse(50, 245, 15, 15);
		
		fill(color(255, 255, 0));
		ellipse(50, 275, 15, 15);
		
		fill(color(0));
		line(43,300,57,314);
		line(57,300,43,314);
		fill(255);
		ellipse(50,307,16,16);
		rect(42,300,16,16);
	}
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		//邏輯是:如果地震發生在一國之內, 就將地震歸納在地面上
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them	
		// TODO: Implement this method using the helper method isInCountry	
		//isInCountry(Feature,countryMarker)
		
		for(Marker marker : countryMarkers){
			if(isInCountry(earthquake, marker))
			{
				//if(marker.getProperty("name").equals("China"))
					//china++;
				land++;
				return true;
			}
		}
		// not inside any country
		ocean++;
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() 
	{   //countryMarkers有國名
		//earthquakes是feature, 如果再isInCountry是true, feature會增加country這個property
		//比對國家和地震資料, 列出國家: 發生次數	
		int aboveSea=0;
		int underSea=0;
		for(Marker country:countryMarkers)
		{
			String countryName=country.getProperty("name").toString();
			int sum = 0;
			for(PointFeature feature:earthquakes){
				if(feature.getStringProperty("country").equals(countryName)){
					sum++;
				}
			}
			aboveSea+=sum;
			System.out.println(countryName+" : "+sum);
		}
		System.out.println("Land: "+aboveSea);
		System.out.println("Ocean: "+(quakeMarkers.size()-aboveSea));		
	}
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake 
	// feature if it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// 判斷地震是否發生在一國之內
		// getting location of feature
		//利用earthquake參數取的經緯度
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		//利用country參數判斷這個Marker在runtime時刻的實際類別是單一Marker還是綜合性Marker
		if(country.getClass() == MultiMarker.class) {
			//如果此Marker在runtime的實際類別==MultiMarker.class, 	
			// looping over markers making up MultiMarker
			//方法getMarkers()回傳一個List
			for(Marker marker : ((MultiMarker)country).getMarkers()) {					
				// 如果地震的經緯度是在這個國家的marker之內, 就將該國名加入地震的properties
				//可能是因為在地震資料中, 國名是title的其中一部份並不容易擷取,而從國家資料來取得國名相對容易多了
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));	
					//System.out.println(country.getProperty("name")+" "+multipolygon++);
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			//System.out.println(country.getProperty("name")+" "+polygon++);
			return true;
		}
		return false;
	}

}
