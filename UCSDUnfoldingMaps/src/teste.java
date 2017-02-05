import java.util.HashMap;
import java.util.List;

public class teste {
	String[] titles = {"Chile", "Alaska", "Sumatra", "Japan","Kamchatka"};
	String[] magnitudes = {"9.5", "9.2", "9.1", "9.0","9.0"};
	String[] dates = {"May 20", "March 28", "December 26", "March 11","November 4"};
	String[] years = {"1960", "1964", "2004", "2011","1952"};
	HashMap<String,String> chile,alaska,sumatra,japan,kamchatka;
	List<Object> properties;
	public void main(String[] args) {
		alaska.put("title", titles[1]);
		alaska.put("magnitude", magnitudes[1]);
		alaska.put("date", dates[1]);
		alaska.put("year", years[1]);
		properties.add(alaska);
		
		sumatra.put("title", titles[2]);
		sumatra.put("magnitude", magnitudes[2]);
		sumatra.put("date", dates[2]);
		sumatra.put("year", years[2]);
		properties.add(sumatra);
		
		japan.put("title", titles[3]);
		japan.put("magnitude", magnitudes[3]);
		japan.put("date", dates[3]);
		japan.put("year", years[3]);
		properties.add(japan);
		
		kamchatka.put("title", titles[4]);
		kamchatka.put("magnitude", magnitudes[4]);
		kamchatka.put("date", dates[4]);
		kamchatka.put("year", years[4]);
		properties.add(kamchatka);
		
		System.out.println(properties.get(0));
		

	}

}
