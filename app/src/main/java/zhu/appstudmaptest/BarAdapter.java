package zhu.appstudmaptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lenovo on 2017/5/30.
 */
public class BarAdapter extends ArrayAdapter<Bar> {
    private int resourceId;
    private final Context myContext;
    public BarAdapter(Context context, int textViewResourceId,
                        List<Bar> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
        this.myContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Bar bar = getItem(position);
        View view;
        final ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.placeImage = (ImageView) view.findViewById(R.id.place_image);
            viewHolder.placeLabel = (TextView)view.findViewById(R.id.text_place);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.placeLabel.setText(bar.getLabel().toString());
        Picasso.with(myContext).load(bar.getImageUrl().toString()).into(viewHolder.placeImage);
        return view;

    }

    class ViewHolder {
        TextView placeLabel;
        ImageView placeImage;
    }
}
