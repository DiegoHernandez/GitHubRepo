package Asapters;

import java.util.List;

import com.joseluishdz.cupbucks.R;

import Classes.Store;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


//public class PlayerListAdapter extends BaseAdapter{
public class StoresAdapter extends ArrayAdapter<Store>{
	  protected List<Store> items;
	  private StoresAdapter storesAdapter=null;
	  private Store itemStore;
	  private Context context; 
	  private int layoutResourceId;
	  
	    public StoresAdapter(Context context, int layoutResourceId, List<Store> items) {
	        super(context, layoutResourceId, items);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.items = items;
	        this.storesAdapter=this;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        CiudadHolder holder = null;
	        
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new CiudadHolder();
	            holder.image = (ImageView)row.findViewById(R.id.ImageViewStoreImage);
	            holder.address = (TextView)row.findViewById(R.id.TextViewAddress);
	            holder.distance = (TextView)row.findViewById(R.id.TextViewDistance);
	            
	            row.setTag(holder);
	            
	        }
	        else
	        {
	            holder = (CiudadHolder)row.getTag();
	        }

	        itemStore = items.get(position);
	        
	        holder.image.setImageBitmap(itemStore.getStoreImage());
	        holder.address.setText(itemStore.getName() + " " + itemStore.getAddress());
	        holder.distance.setText(itemStore.getDistanceText());
	
	        //String img  = itemTeam.getImgTeam();
	        //holder.nameRanking.setText(nameRankig);
	        //holder.idPoliza.setText(itemPoliza.getId_poliza());
	        return row;
	    }
	    
	    public Store getItemStore(int position){
	    	return itemStore = items.get(position);
	    }
	    
	    static class CiudadHolder
	    {
	    	ImageView image;
	        TextView address;
	        TextView distance;
	    }  
}
