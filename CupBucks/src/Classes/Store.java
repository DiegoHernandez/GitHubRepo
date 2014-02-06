package Classes;

import android.graphics.Bitmap;

public class Store {
	private double lat;
	private double lng;
	private String name;
	private String address;
	private String distanceText;
	private String timeText;
	private double distanceValue;
	private double timeValue;
	private Bitmap  storeImage;
	
	public Store(double lat,double lng,String name, String address, double distanceValue, double timeValue, String distanceText, String timeText, Bitmap storeImage){
		this.lat=lat;
		this.lng=lng;
		this.name=name;
		this.address=address;
		this.distanceValue=distanceValue;
		this.timeValue=timeValue;
		this.distanceText=distanceText;
		this.timeText=timeText;
		this.storeImage=storeImage;
	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Bitmap getStoreImage() {
		return storeImage;
	}

	public void setStoreImage(Bitmap storeImage) {
		this.storeImage = storeImage;
	}

	public String getDistanceText() {
		return distanceText;
	}

	public void setDistanceText(String distanceText) {
		this.distanceText = distanceText;
	}

	public String getTimeText() {
		return timeText;
	}

	public void setTimeText(String timeText) {
		this.timeText = timeText;
	}

	public double getDistanceValue() {
		return distanceValue;
	}

	public void setDistanceValue(double distanceValue) {
		this.distanceValue = distanceValue;
	}

	public double getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(double timeValue) {
		this.timeValue = timeValue;
	}
	
	
}
