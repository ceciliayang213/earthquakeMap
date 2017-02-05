import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class EarthquakeCityMap extends PApplet {
	private UnfoldingMap map;
	int totalLoc = 5;//不應該寫死, 應該讓value依據parse回來的list長度而改變?
	List<Location> listLoc = new ArrayList<Location>();//我竟然會忘記要將三個list初始化...
	List<PointFeature> listPointFeature= new ArrayList<PointFeature>();
	List<Marker> markers= new ArrayList<Marker>();
	String[] titles = {"Chile", "Alaska", "Sumatra", "Japan","Kamchatka"};
	String[] magnitudes = {"9.5", "9.2", "9.1", "9.0","9.0"};
	String[] dates = {"May 20", "March 28", "December 26", "March 11","November 4"};
	int[] years = {1960, 1964, 2004, 2011,1952};
	 
	public void setup()
	{
		size(950,600,OPENGL);
		map = new UnfoldingMap(this,200,50,700,500,new Google.GoogleMapProvider());
		map.zoomToLevel(1);
		MapUtils.createDefaultEventDispatcher(this, map);//this means the PApplet window
		listLoc.add(new Location(-38.14f,-73.03f));
		listLoc.add(new Location(61.02f, -147.65f));
		listLoc.add(new Location( 3.30f,95.78f));
		listLoc.add(new Location( 38.322f,142.369f));
		listLoc.add(new Location( 52.76f,160.06f));
		for(int i=0;i<totalLoc;i++){//為每一個地點產生一個PointFeature, 用來裝各自的4個properties, 
			PointFeature pointFeature = new PointFeature(listLoc.get(i));
			pointFeature.addProperty("title", titles[i]);
			pointFeature.addProperty("magnitude", magnitudes[i]);
			pointFeature.addProperty("date", dates[i]);
			pointFeature.addProperty("year", years[i]);
			listPointFeature.add(pointFeature);//properties裝入pointFeature後, 將pointFeature加入listPointFeature
		}
		for(int i=0;i<totalLoc;i++){//利用listPointFeature裡面的每個地點的location與properties, 來生成SimplePointMarker, 並加入markers
			SimplePointMarker spm = new SimplePointMarker(listPointFeature.get(i).getLocation(),listPointFeature.get(i).getProperties());
			markers.add(spm);
		}
		//int yellow = color(255,255,0);//在processing裡color()的return type就是int
		//int green = color(0,255,0);
		for(Marker m : markers){
			if((int)m.getProperty("year") > 2000){
				m.setColor(color(255,255,0));//color(255,255,0)可以替換成yellow
			}
			else{
				m.setColor(color(0,255,0));//可替換成green
			}
		}
		map.addMarkers(markers);
	}
	
	public void draw()
	{
		background(200);//or simply put gray, or red, etc.
		map.draw();
		addKey();
	}

	private void addKey() {
		// TODO Auto-generated method stub
		
	}

}
