package com.joseluishdz.cupbucks;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.joseluishdz.cupbucks.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import Asapters.StoresAdapter;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowStores extends Activity {

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
    
	private ListView listViewStores;
	private StoresAdapter storesAdapter;
	
	private SlidingUpPanelLayout layout;
	private static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	
	private FrameLayout dragView;
	private LinearLayout overLayout;
	private int originalHeight;
	private ViewGroup.LayoutParams params;
	private int min;
	
	private ImageView DownImage;
	private LinearLayout LinearLayoutItem;
	
	private ImageView ImageViewStore;
	private TextView TextViewAddressStore;
	private TextView TextViewDistanceStore;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.show_stores);
        
        listViewStores = (ListView) findViewById(R.id.ListViewStores);
       
        dragView = (FrameLayout) findViewById(R.id.DragView);
        overLayout = (LinearLayout) findViewById(R.id.OverLayout);
        DownImage = (ImageView) findViewById(R.id.DownImage);
        LinearLayoutItem = (LinearLayout) findViewById(R.id.LinearLayoutItem);
        
        ImageViewStore = (ImageView) findViewById(R.id.ImageViewStore);
        TextViewAddressStore = (TextView) findViewById(R.id.TextViewAddressStore);
        TextViewDistanceStore = (TextView) findViewById(R.id.TextViewDistanceStore);
        
        ImageViewStore.setVisibility(View.INVISIBLE);
        TextViewAddressStore.setVisibility(View.INVISIBLE);
        TextViewDistanceStore.setVisibility(View.INVISIBLE);
        
        AlphaAnimation alpha = new AlphaAnimation(0,0);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        // And then on your layout
        DownImage.startAnimation(alpha);
        
        /*dragView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // gets called after layout has been done but before display.
            	dragView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            	
            	ViewGroup.LayoutParams params = dragView.getLayoutParams();
                params.height = 140;
                dragView.requestLayout();
            	
            	// get width and height
            	originalHeight = dragView.getHeight();
            }
        });*/
         
        originalHeight = 180;
        min=40;
        
        params = dragView.getLayoutParams();
		params.height = originalHeight;
		dragView.requestLayout();
		
    	/*FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
    			dragView .getLayoutParams();
    	params.height = 140;
    	params.width=LayoutParams.MATCH_PARENT;
    	dragView .setLayoutParams(params);*/
        
        // create class object
        gps = new GPSTracker(ShowStores.this);

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
         
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        //googleMap.getUiSettings().setScrollGesturesEnabled(false);
        //googleMap.moveCamera(CameraUpdateFactory.scrollBy(+100, 0));
        
        // Move the camera instantly to hamburg with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));

        // Zoom in, animating the camera. 
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        
        new getStarbucksStores().execute();
        
        Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight();
        
        layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        layout.setAnchorPoint(0.0f);
        layout.setLimitSlide(400);
        
        layout.setDragView(dragView);
        //layout.setPanelHeight(originalHeight);
        
        /*int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        
        layout.setPadding(0, actionBarHeight, 0, 0);*/
        //layout.setPadding(0, 200, 0, 0);
        
        layout.setEnableDragViewTouchEvents(true);
        
        alpha = new AlphaAnimation(0,0);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        // And then on your layout
        overLayout.startAnimation(alpha);
        
        layout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //System.out.println("onPanelSlide, offset " + slideOffset);
                /*if (slideOffset < 0.2) {
                    if (getActionBar().isShowing()) {
                        getActionBar().hide();
                    }
                } else {
                    if (!getActionBar().isShowing()) {
                        getActionBar().show();
                    }
                }*/
            	
                /*if(slideOffset==0){
                	layout.setSlidingEnabled(false);
                	bandFirstTimeSlideUp=true;
                	bandListWasMoved=false;
                	//layout.setDragView(dragView); 
                }*/ 
                
                slideOffset=1-slideOffset;
                AlphaAnimation alpha = new AlphaAnimation(slideOffset,slideOffset);
                alpha.setDuration(0); // Make animation instant
                alpha.setFillAfter(true); // Tell it to persist after the animation ends
                // And then on your layout
                overLayout.startAnimation(alpha);

                int newHeight = (int) (((1-slideOffset)*100)*originalHeight)/100;
                
                params = dragView.getLayoutParams();
                if(newHeight>=min){
                	params.height = newHeight;
            		dragView.requestLayout();
                }else{
                	params.height = min;
            		dragView.requestLayout();
                }
                
                
                
            }

            @Override
            public void onPanelExpanded(View panel) {
            	layout.setEnableDragViewTouchEvents(false);
            	googleMap.getUiSettings().setScrollGesturesEnabled(false);
            	
            	AlphaAnimation alpha = new AlphaAnimation(0,1);
                alpha.setDuration(500); // Make animation instant
                alpha.setFillAfter(true); // Tell it to persist after the animation ends
                // And then on your layout
                DownImage.startAnimation(alpha);
            }

            @Override
            public void onPanelCollapsed(View panel) {
            	layout.setEnableDragViewTouchEvents(true);
            	googleMap.getUiSettings().setScrollGesturesEnabled(true);
            	
            	AlphaAnimation alpha = new AlphaAnimation(0,0);
                alpha.setDuration(0); // Make animation instant
                alpha.setFillAfter(true); // Tell it to persist after the animation ends
                // And then on your layout
                overLayout.startAnimation(alpha);
                
                alpha = new AlphaAnimation(0,0);
                alpha.setDuration(0); // Make animation instant
                alpha.setFillAfter(true); // Tell it to persist after the animation ends
                // And then on your layout
                DownImage.startAnimation(alpha);
            } 

            @Override
            public void onPanelAnchored(View panel) {

            }
        });
        
        listViewStores.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,int position, long arg3) {
				// TODO Auto-generated method stub
				Store itemStore = storesAdapter.getItemStore(position);
				
			}
		});
        
		listViewStores.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
				int index = view.getFirstVisiblePosition();
				View v = view.getChildAt(0);
				int top = (v == null) ? 0 : v.getTop();
				
				/*if(itemPostion==0){
					if(top==0){
						layout.setDragView(null);
					}
				}*/
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
				/*View v = view.getChildAt(0);
				int top = (v == null) ? 0 : v.getTop();
				
				if(top==0){
					if(!bandFirstTimeSlideUp){
						layout.setSlidingEnabled(true);
						//layout.setDragView(null);
					}
					
					if(!bandListWasMoved){
						layout.setSlidingEnabled(true);
					}
				}else{
					bandFirstTimeSlideUp=false;
					bandListWasMoved=true;
				}*/
				
			}
			
		});
        
        boolean actionBarHidden = savedInstanceState != null ?
                savedInstanceState.getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false): false;
        if (actionBarHidden) {
            getActionBar().hide();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, !getActionBar().isShowing());
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
    /*public double[] getlocation() {
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
    }*/
	
    @Override
    protected void onResume() {
        super.onResume();
    }
	
	public class getStarbucksStores extends AsyncTask<String, String, String> {
	    private String content =  null;
	    private boolean error = false;
	    private String success;
	    private Bitmap storeImage;
	    private double distanceValue;
	    private double timeValue;
	    private String distanceText;
	    private String timeText;
		
	    protected void onPreExecute() {
	    	super.onPreExecute();
	        bandErrorConn=false;
	        pDialog = new ProgressDialog(ShowStores.this);
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
	        	
	        	List<NameValuePair> params = new ArrayList<NameValuePair>();
        		params.add(new BasicNameValuePair("client_id", ConnConf.CLIENT_ID));
        		params.add(new BasicNameValuePair("client_secret", ConnConf.CLIENT_SECRET));
        		params.add(new BasicNameValuePair("ll", currentLoc.latitude+","+currentLoc.longitude));
        		params.add(new BasicNameValuePair("query", "starbucks"));
        		params.add(new BasicNameValuePair("v", anio+mes+dia));
	        	
        		String path = ConnConf.path+"/search";
        		
        		//get distances
    			JSONObject json = jParser.makeHttpRequest(path, "GET", params);
    			JSONObject meta = json.getJSONObject(ConnConf.TAG_META);
    			
    			success = meta.getString(ConnConf.TAG_CODE);
    			
                if (success.equals("200")) {
                	JSONObject response = json.getJSONObject(ConnConf.TAG_RESPONSE);
                	
                	JSONArray venuesArray = response.getJSONArray(ConnConf.TAG_VENUES);
                	
                	// looping through All major
                    for (int i = 0; i < venuesArray.length(); i++) {
                    	JSONObject venue = venuesArray.getJSONObject(i);
                    	JSONObject location = venue.getJSONObject(ConnConf.TAG_LOCATION);
                    	
                    	String id = venue.getString(ConnConf.TAG_STORE_ID);
                    	String name = venue.getString(ConnConf.TAG_NAME);
                    	String address = location.getString(ConnConf.TAG_ADDRESS);
                    	double lat = location.getDouble(ConnConf.TAG_LAT);
                    	double lng = location.getDouble(ConnConf.TAG_LNG);
                    	
                    	//Distance.
                    	List<NameValuePair> paramsDistance = new ArrayList<NameValuePair>();
                    	paramsDistance.add(new BasicNameValuePair("origins", currentLoc.latitude+","+currentLoc.longitude));
                    	paramsDistance.add(new BasicNameValuePair("destinations", lat+","+lng));
                    	paramsDistance.add(new BasicNameValuePair("mode", "driving"));
                    	paramsDistance.add(new BasicNameValuePair("sensor", "false"));
		        		
		                //get distances
		    			JSONObject jsonDistances = jParser.makeHttpRequest(ConnConf.pathDistance, "GET", paramsDistance);                  		
		                success = jsonDistances.getString(ConnConf.TAG_STATUS);
		                
		                if (success.equals("OK")) {
		                	
		                	JSONArray rows = jsonDistances.getJSONArray(ConnConf.TAG_ROWS);
		                	JSONObject elements = rows.getJSONObject(0);
		                	JSONArray elementsArray = elements.getJSONArray(ConnConf.TAG_ELEMENTS);
		                	
		                	JSONObject elemento = elementsArray.getJSONObject(0);
	                        // Storing each json item in variable
	                    	JSONObject distance = elemento.getJSONObject(ConnConf.TAG_DISTANCE);
	                    	JSONObject time = elemento.getJSONObject(ConnConf.TAG_DURATION);
	                    	
	                    	distanceValue = distance.getDouble(ConnConf.TAG_VALUE);
	                    	timeValue = time.getDouble(ConnConf.TAG_VALUE);
	                    	distanceText = distance.getString(ConnConf.TAG_TEXT);
	                    	timeText = time.getString(ConnConf.TAG_TEXT);
		                    
		                    List<NameValuePair> paramsPhoto = new ArrayList<NameValuePair>();
	                    	paramsPhoto.add(new BasicNameValuePair("client_id", ConnConf.CLIENT_ID));
	                    	paramsPhoto.add(new BasicNameValuePair("client_secret", ConnConf.CLIENT_SECRET));
	                    	paramsPhoto.add(new BasicNameValuePair("v", anio+mes+dia));
	                		
	                		path = ConnConf.path+"/"+id+"/photos";
	                		
	                		//get distances
	                		JSONObject json2 = jParser.makeHttpRequest(path, "GET", paramsPhoto);
	            			JSONObject meta2 = json2.getJSONObject(ConnConf.TAG_META);
	            			
	            			success = meta2.getString(ConnConf.TAG_CODE);	                		
	            			
	                        if (success.equals("200")) {
	                        	JSONObject response2 = json2.getJSONObject(ConnConf.TAG_RESPONSE);
	                        	JSONObject photos = response2.getJSONObject(ConnConf.TAG_PHOTOS);
	                        	
	                        	JSONArray photosArray = photos.getJSONArray(ConnConf.TAG_ITEMS);
	                        	JSONObject photo = photosArray.getJSONObject(0);
	                        	
	                        	String prefix = photo.getString(ConnConf.TAG_PREFIX);
	                        	String suffix = photo.getString(ConnConf.TAG_SUFFIX);
	                        	
	                        	String image=prefix+"original"+suffix;	                	
	                        	
	                			try{
	                				URL url = new URL(image);
	                		        //try this url = "http://0.tqn.com/d/webclipart/1/0/5/l/4/floral-icon-5.jpg"
	                		        HttpGet httpRequest = null;

	                		        httpRequest = new HttpGet(url.toURI());

	                		        HttpClient httpclient = new DefaultHttpClient();
	                		        HttpResponse responseURL = (HttpResponse) httpclient
	                		                .execute(httpRequest);

	                		        HttpEntity entity = responseURL.getEntity();
	                		        BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
	                		        InputStream input = b_entity.getContent();

	                		        storeImage=null;
	                		        storeImage = BitmapFactory.decodeStream(input);
	                		        
	                		        if(storeImage==null){
	                            		Drawable myDrawable = getResources().getDrawable(R.drawable.starbucks_logo);
	                            		storeImage = ((BitmapDrawable) myDrawable).getBitmap();
	                            	}
	                            	
	                            	stores.add(new Store(lat,lng,name,address,distanceValue,timeValue,distanceText,timeText,storeImage));
	                		        
	                			}catch(Exception e){
	                				bandDone=false;
	                				break;
	                			}
	                        }else{
	                        	bandDone=false;
	                        	break;
	                        }
		                }else{
		                	bandDone=false;
		                	break;
		                }
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
					sortStores();
					fillListStores(stores);
					fillDragView(stores);
					moveOnSlidePaneDownCamera(300,1000);
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
	
	public void moveOnSlidePaneDownCamera(int y, int animationTime){
		googleMap.animateCamera(CameraUpdateFactory.scrollBy(0, (y*-1)), animationTime, null);
	}
	
	public void moveOnSlidePaneUpCamera(int y, int animationTime){
		googleMap.animateCamera(CameraUpdateFactory.scrollBy(0, y), animationTime, null);
	}
	
	public void fillListStores(List<Store> list){
		
		ArrayList <Store>listStores = (ArrayList<Store>)list;
		ArrayList <Store> list2 = new ArrayList<Store>();
		
		for(int i=0;i<listStores.size();i++){
			list2.add(listStores.get(i));
		}
		for(int i=0;i<listStores.size();i++){
			list2.add(listStores.get(i));
		}
		for(int i=0;i<listStores.size();i++){
			list2.add(listStores.get(i));
		}
		for(int i=0;i<listStores.size();i++){
			list2.add(listStores.get(i));
		}
		storesAdapter = new StoresAdapter(this, R.layout.item_store, list2);
		listViewStores.setAdapter(storesAdapter);
		
	}
	
	public void sortStores(){
		Store aux;
        for(int i=0;i<stores.size()-1;i++){
             for(int j=0;j<stores.size()-i-1;j++){
            	 if(stores.get(j+1).getDistanceValue()<stores.get(j).getDistanceValue()){
                     aux=stores.get(j+1);
                     stores.set((j+1), stores.get(j));
                     stores.set((j), aux);
                  }
             }
        }
	}
	
	public void fillDragView(List<Store> stores){
		ArrayList <Store>listStores = (ArrayList<Store>)stores;
		
		Store itemStore = listStores.get(0); 
		
		ImageViewStore.setImageBitmap(itemStore.getStoreImage());
		TextViewAddressStore.setText(itemStore.getName() + " " + itemStore.getAddress());
		TextViewDistanceStore.setText(itemStore.getDistanceText());
		
		ImageViewStore.setVisibility(View.VISIBLE);
        TextViewAddressStore.setVisibility(View.VISIBLE);
        TextViewDistanceStore.setVisibility(View.VISIBLE);
	}
}
