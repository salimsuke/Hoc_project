package ai.boubaker.hoc;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ai.api.sample.R;

public class CustomListAdapter extends ArrayAdapter<String> {
    int ind;
    private final Activity context;
    private  String[] itemname;
    private String[] imgid;
    private final String x;

    public CustomListAdapter(Activity context, String[] itemname, String[] imgid, String x) {
        super(context, R.layout.simple_list_img, itemname);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
        this.x = x;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.simple_list_img, parent, false);
            viewHolder.name = (TextView) view.findViewById(R.id.textView111);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.name.setText(itemname[position]);
//            if (imgid[position]!=""){
//                LinearLayout lL = (LinearLayout) view.findViewById(R.id.a);
//                Log.e("Linear", ""+1L);
//                ImageView imgView = new ImageView(this.context);
//                viewHolder.image = imgView;
//                Picasso.with(view.getContext()).load(imgid[position]).into(viewHolder.image);
//                imgView.setVisibility(View.VISIBLE);
//                lL.addView(imgView);
//            }
        return view;
    }
        class ViewHolder {
        TextView name;
        ImageView image;
    }
}