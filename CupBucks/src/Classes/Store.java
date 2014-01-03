package Classes;

public class Store {
	private double lat;
	private double lng;
	private String name;
	private String address;
	
	public Store(double lat,double lng,String name, String address){
		this.lat=lat;
		this.lng=lng;
		this.name=name;
		this.address=address;
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
	
	
}
