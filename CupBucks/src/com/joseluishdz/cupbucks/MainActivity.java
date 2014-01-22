package com.joseluishdz.cupbucks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.joseluishdz.cupbucks.R;

import Classes.GPSTracker;
import Classes.Store;
import JSONParser.ConnConf;
import JSONParser.JSONParser;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {

	JSONParser jParser = new JSONParser();
	private ProgressDialog pDialog;
	private boolean bandDone,bandErrorConn;
	private List<Store> stores = new ArrayList<Store>();
	
	// GPSTracker class
    private GPSTracker gps;
	
	private LocationManager locationManager;
	private LatLng currentLoc;
	
	// Google Map
    private GoogleMap googleMap;
    private Location location;
    
    Calendar ca = Calendar.getInstance();
	private String dia = Integer.toString(ca.get(Calendar.DATE));
	private String mes = Integer.toString((ca.get(Calendar.MONTH)+1));
	private String anio = Integer.toString(ca.get(Calendar.YEAR));
    
	private ListView listView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        listView = (ListView) findViewById(R.id.ListView);
        String[] items = { "Pleace1", "Pleace2", "Pleace3", "Pleace4", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5", "Pleace5" };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, items);
        
        listView.setAdapter(adapter);
        // create class object
        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled     
        if(gps.canGetLocation()){
             
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();            
            currentLoc = new LatLng(latitude,longitude);
        }else{
        	alertError("Error de conexion por favor verifique si esta conectado a internet");
        }
        
        System.out.println("HolaLatitud: " + currentLoc.latitude);
        System.out.println("HolaLong: " + currentLoc.longitude);
        
         
        //double []datos=getlocation();
        //currentLoc = new LatLng(datos[0],datos[1]);
        
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
         
        //googleMap.getUiSettings().setZoomControlsEnabled(false);
        //googleMap.getUiSettings().setScrollGesturesEnabled(false);
        //googleMap.moveCamera(CameraUpdateFactory.scrollBy(+100, 0));
        
        // Move the camera instantly to hamburg with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));

        // Zoom in, animating the camera. 
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        
        new getStarbucksStores().execute();
        
        //googleMap.setPadding(50, 5, 10, 5);

        Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight();
        
        System.out.println("HolaPos1: " + googleMap.getCameraPosition());
        
        System.out.println("HolaTam: " + width + "," + height);
        
        System.out.println("HolaPos2: " + googleMap.getCameraPosition());
    }
    
    public double[] getlocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        double[] gps = new double[2];

        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @Override
    protected void onResume() {
        super.onResume();
    }
	
	public class getStarbucksStores extends AsyncTask<String, String, String> {
	    private String content =  null;
	    private boolean error = false;
	    private String success;
		
	    protected void onPreExecute() {
	    	super.onPreExecute();
	        bandErrorConn=false;
	        pDialog = new ProgressDialog(MainActivity.this);
	        pDialog.setMessage("Loading, please wait...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
	    }
	 
	    protected String doInBackground(String... urls) {
	        try {
	        	//int version=Integer.parseInt(versionDB);
	        	bandDone=true;
	        	
	        	if(Integer.parseInt(dia)<10){
	        		dia="0"+Integer.parseInt(dia);
	        	}
	        	if(Integer.parseInt(mes)<10){
	        		mes="0"+Integer.parseInt(mes);
	        	}
	        	if(Integer.parseInt(anio)<10){
	        		anio="0"+Integer.parseInt(anio);
	        	}
	        	
	        	System.out.println("holaFecha: " + anio+mes+dia);
	        	List<NameValuePair> params = new ArrayList<NameValuePair>();
        		params.add(new BasicNameValuePair("client_id", ConnConf.CLIENT_ID));
        		params.add(new BasicNameValuePair("client_secret", ConnConf.CLIENT_SECRET));
        		params.add(new BasicNameValuePair("ll", currentLoc.latitude+","+currentLoc.longitude));
        		params.add(new BasicNameValuePair("query", "starbucks"));
        		params.add(new BasicNameValuePair("v", anio+mes+dia));
	        	
        		//get distances
    			JSONObject json = jParser.makeHttpRequest(ConnConf.path, "GET", params);
    			System.out.println("JSON: " + json.toString());
    			JSONObject meta = json.getJSONObject(ConnConf.TAG_META);
    			
    			success = meta.getString(ConnConf.TAG_CODE);
        		
    			System.out.println("hola1: " + success);
    			
                if (success.equals("200")) {
                	JSONObject response = json.getJSONObject(ConnConf.TAG_RESPONSE);
                	
                	System.out.println("hola2: " + response.toString());
                	
                	JSONArray venuesArray = response.getJSONArray(ConnConf.TAG_VENUES);
                	
                	// looping through All major
                    for (int i = 0; i < venuesArray.length(); i++) {
                    	JSONObject venue = venuesArray.getJSONObject(i);
                    	JSONObject location = venue.getJSONObject(ConnConf.TAG_LOCATION);
                    	
                    	System.out.println("hola3: " + location.toString());
                    	
                    	String name = venue.getString(ConnConf.TAG_NAME);
                    	String address = location.getString(ConnConf.TAG_ADDRESS);
                    	double lat = location.getDouble(ConnConf.TAG_LAT);
                    	double lng = location.getDouble(ConnConf.TAG_LNG);
                    	
                    	
                    	
                    	stores.add(new Store(lat,lng,name,address));
                    }
                }else{
                	bandDone=false;
                }
	        }catch (Exception e) {
	            content = e.getMessage();
	            bandErrorConn=true;
	            cancel(true);
	        }
	 
	        return null;
	    }
	    
	    protected void onPostExecute(String content) {
	    	// dismiss the dialog
	    	pDialog.dismiss();
	    	if(bandErrorConn==false){
	    		if(bandDone==true){
	            	Toast toast = Toast.makeText(getApplicationContext(), "Tienas starbucks obtenidas con exito", Toast.LENGTH_SHORT);
					toast.show();

					createMarkers();
					
					//googleMap.moveCamera(CameraUpdateFactory.scrollBy(-300, 0));
					googleMap.animateCamera(CameraUpdateFactory.scrollBy(0, 300), 2000, null);
	            }else{
	            	alertError("Ha ocurrido un error al intentar obtener los tiempos");
	            }
	    	}else{
	    		alertError("Error al intentar obtener los tiempos");
	    	}
	    }
	    
	}

	public void createMarkers(){
		
		for(int i=0;i<stores.size();i++){
			
			Marker kiel = googleMap.addMarker(new MarkerOptions()
	        .position(new LatLng(stores.get(i).getLat(),stores.get(i).getLng()))
	        .title(stores.get(i).getName())
	        .snippet(stores.get(i).getAddress())
	        .icon(BitmapDescriptorFactory
	        .fromResource(R.drawable.marker_icon_4)));
		}
	}
	
	public void alertError(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
		        .setIcon(R.drawable.ic_launcher)
				.setTitle("Error")
		        .setCancelable(false)
		        .setNegativeButton("Aceptar",
		                new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int id) {
		                    	dialog.cancel();
		                    }
		                });
		AlertDialog alert = builder.create();
		alert.show();
	}
}
