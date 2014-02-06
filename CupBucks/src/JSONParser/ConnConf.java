package JSONParser;

public class ConnConf {
	public static String path="https://api.foursquare.com/v2/venues";

	// JSON Node names
	public static final String CLIENT_ID = "B3TGNACVGPVOBSGW3NUZFX5N433W2F20SMS33OEUPRSIENOM";
	public static final String CLIENT_SECRET = "HKPRRQ4ANAJR4XMSSEURSPBOYIW3WC231WDGAZV0YQSMEIIT";
	
	public static final String TAG_META = "meta";
	public static final String TAG_CODE = "code";
	public static final String TAG_RESPONSE = "response";
	public static final String TAG_VENUES = "venues";
	public static final String TAG_PHOTOS = "photos";
	public static final String TAG_ITEMS = "items";
	public static final String TAG_PREFIX = "prefix";
	public static final String TAG_SUFFIX = "suffix";
	public static final String TAG_NAME = "name";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_ADDRESS = "address";
	public static final String TAG_LAT = "lat";
	public static final String TAG_LNG = "lng";
	public static final String TAG_STORE_ID = "id";
	
	public static String pathDistance="http://maps.googleapis.com/maps/api/distancematrix/json";

	// JSON Node names 
	public static final String TAG_STATUS = "status";
    public static final String TAG_ROWS = "rows";
    public static final String TAG_ELEMENTS = "elements";
    public static final String TAG_DISTANCE = "distance";
    public static final String TAG_DURATION = "duration";
    public static final String TAG_TEXT = "text";
    public static final String TAG_VALUE = "value";
}
