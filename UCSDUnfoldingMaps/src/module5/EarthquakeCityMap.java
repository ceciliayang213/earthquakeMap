package module5;

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
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
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
	// that the setup and draw methods will need to access (as well as other methods)
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

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;

	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			//map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Microsoft.RoadProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
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
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		    System.out.println(Float.parseFloat(feature.getProperty("magnitude").toString()));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
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
		background(0);
		//map.draw();Updates and draws the map. The main method to display this UnfoldingMap. 
		map.draw();//若不客製化marker的形狀大小顏色, 就是統一大小的灰色圓點
		addKey();
		
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()//只要滑鼠一移動, 就會自動呼叫這個方法:先清除先前所儲存的marker,才呼叫selectMarkerIfHover()
	{
		// clear the last selection
		if (lastSelected != null) {//如果之前有選到marker, 就先清除掉, 接著才呼叫selectMarkerIfHover()
			lastSelected.setSelected(false);
			lastSelected = null;	
		}
		//這裡會導致city跟quake同時顯示出來
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);		
	}
	
	// If there is a marker under the cursor, and lastSelected is null 
	// set the lastSelected to be the first marker found under the cursor
	// Make sure you do not select two markers.
	// 
	
	private void selectMarkerIfHover(List<Marker> markers)
	{ 		// TODO: Implement this method
		//System.out.println("Return 1: 進入方法selectMarkerIfHover()");	
		if(lastSelected !=null){
			return;
		}
		System.out.println("Return 2: 在lastSelected != null 之後");
			for(Marker marker:markers){		
				//將每個marker的selected設回default狀態: false
				marker.setSelected(false);
				CommonMarker m = (CommonMarker)marker;
				//除非滑鼠游標落在marker的area之內, 才將selected設為true
				//這樣當CommonMarker的draw()被呼叫時, 狀態為selected的marker的title才會被顯示出來
				if(m.isInside(map, mouseX, mouseY)){				
					PointFeature p = new PointFeature(m.getLocation());
					p.setProperties(m.getProperties());
					 lastSelected=m;
					 m.setSelected(true);
					 return;//一旦選到第一個marker就跳離這個迴圈
					
					/*if(marker.getClass() == LandQuakeMarker.class){
						lastSelected = new LandQuakeMarker(p);	
						//lastSelected.setSelected(true);
						//marker.setSelected(true);
						selectedMarkers.add(marker);
					}
					else if(marker.getClass() == OceanQuakeMarker.class){		
						lastSelected = new OceanQuakeMarker(p);	 
						//lastSelected.setSelected(true);
						//marker.setSelected(true);
						selectedMarkers.add(marker);
					}
					else if(marker.getClass() == CityMarker.class){			
						lastSelected = new CityMarker(marker.getLocation());
						//lastSelected.setSelected(true);
						//marker.setSelected(true);
						selectedMarkers.add(marker);
					}*/		
					//lastSelected.setSelected(true);
				}
				System.out.println("Return 3: 一旦選到第一個marker就跳離這個迴圈");
			}
			System.out.println("Return 4: 離開Markers的loop");
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{	// TODO: Implement this method
		//loop所有地圖上的markers, 先將lastClick設為null
		//先判斷marker是cityMarker還是earthquakeMarker: marker.setClicked(true)
		//是cityMarker: show出會影響此city的所有地震
		//是earthquakeMarker: show出所有會受到這個地震所影響的城市		
		if(lastClicked != null){
			lastClicked = null;
			unhideMarkers();
		}
		//第一圈: lastClicked預設是null,所以走else statement,
		//接著判斷如果沒點到city: do nothing, 有點到的話show 1 marker, hide the rest
		else {//lastClicked == null, 區分有點到marker跟沒點到marker
			showAndHideMarkers(cityMarkers);
			showAndHideMarkers(quakeMarkers);
		}
	}
	
	private void showAndHideMarkers(List<Marker> markers)//放入cityMarkers或quakeMarkers
	{
		int hit=0;		
		for(Marker marker:markers){
			if(marker.isInside(map, mouseX, mouseY)){
				hit++;
			}
		}		
		if(hit==0){
			//沒點到marker:do nothing
		}
		else{//有點到marker: 有點到的show, 沒點到的hide
			for(Marker marker: markers){			
				if(marker.isInside(map, mouseX, mouseY))
				{
					if(marker.getClass()==LandQuakeMarker.class||marker.getClass()==OceanQuakeMarker.class){
						PointFeature p = new PointFeature(marker.getLocation());
						p.setProperties(marker.getProperties());
						lastClicked = new LandQuakeMarker(p);//當然也有可能是OceanMarker, 但在這裡是哪一種並不重要;但精確起見其實應該多一個else if
						lastClicked.setClicked(true);
						((CommonMarker)marker).setClicked(true);
						calDistanceForQuake(marker,cityMarkers);
					}
					else{
						lastClicked = new CityMarker(marker.getLocation());
						lastClicked.setClicked(true);
						((CommonMarker)marker).setClicked(true);
						calDistanceForCity(marker,quakeMarkers);
					}					
				}
				else
				{	
					marker.setHidden(true);
				}		
			}
		}
	}
	
	private void calDistanceForQuake(Marker quake,List<Marker> cityMarkers) {
		for(Marker city:cityMarkers){
			if(quake.getDistanceTo(city.getLocation()) > ((EarthquakeMarker)quake).threatCircle()){
				city.setHidden(true);
			}
		}
	}

	private void calDistanceForCity(Marker city,List<Marker> quakeMarkers) {
		for(Marker quake:quakeMarkers){
			if(city.getDistanceTo(quake.getLocation()) > ((EarthquakeMarker)quake).threatCircle()){
				quake.setHidden(true);	
			}	
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}		
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);		
	}
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.	
	private boolean isLand(PointFeature earthquake) {	
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}	
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}

	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();
		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {			
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}	
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}
}