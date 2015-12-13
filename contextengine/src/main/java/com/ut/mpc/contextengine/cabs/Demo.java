package com.ut.mpc.contextengine.cabs;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nathanielwendt.contextengine.R;
import com.example.nathanielwendt.pacolib.Cabs;

import java.util.Set;

/**
 * Created by nathanielwendt on 12/7/15.
 */
public class Demo extends Cabs {
    final GridCellViewHolder holder;
    final Set<Long> mSelectedItems;

    @Override
    public void init() {
        final String description = "";


        long imageID = 0L;

        PacoMediator mediator = new PacoMediator();



        mediator.cab(AbstractLocation.ID)
                .filter(x -> x.equals("Home"))
                .subscribe(x -> holder.description.setText(description));


        mediator.cab(Sociality.ID)
                .buffer(10)
                .filter(x -> ((Sociality.Data) x).value > .6)
                .subscribe(x -> setIcon(imageID, View.VISIBLE));



    }

    public void setIcon(long imageID, int val){
        holder.icon.setVisibility(((mSelectedItems != null) && (mSelectedItems.contains(imageID))) ? View.VISIBLE : View.GONE);
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    /** data belonging to gridview element */
    static class GridCellViewHolder {
        private static int lastInstanceNo = 0;
        private final String debugPrefix;

        final public ImageView image;
        final public ImageView icon;
        final public TextView description;
        //private DownloadImageTask downloader = null;

        /** onClick add this as sql-where-filter */
        public String filter;

        /** for delay loading */
        public long imageID = 0;

        GridCellViewHolder(View parent) {
            lastInstanceNo++;
            debugPrefix = "Holder@" + lastInstanceNo + "#";

            this.description = (TextView) parent.findViewById(R.id.text);
            this.image = (ImageView) parent.findViewById(R.id.image);
            this.icon = (ImageView) parent.findViewById(R.id.icon);
        };

        @Override
        public String toString() {
            return debugPrefix + this.imageID;
        }

        public void loadImageInBackground(long imageID, Drawable imageNotLoadedYet) {
            if (imageID != this.imageID) {
                // to avoid reload the same again

            }
        }
    }


}
