package com.kesari.trackingfresh.Settings.MyCards;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.network.MyApplication;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kesari on 06/06/17.
 */

class Cards_RecyclerAdpater<T> extends BaseAdapter {
    List<CardPOJO> CardPOJOs;
    private Activity activity;
    private LayoutInflater layoutInflater = null;
    private String TAG = this.getClass().getSimpleName();
    MyApplication myApplication;

    public Cards_RecyclerAdpater(List<CardPOJO> CardPOJOs, Activity activity) {
        this.CardPOJOs = CardPOJOs;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return CardPOJOs.size();
    }

    @Override
    public Object getItem(int position) {
        return CardPOJOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.cards_row_layout, null);

            viewHolder.cardImage = (ImageView) view.findViewById(R.id.cardImage);
            viewHolder.default_text = (TextView) view.findViewById(R.id.default_text);
            viewHolder.subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            viewHolder.delete = (FancyButton) view.findViewById(R.id.delete);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        try
        {
            final CardPOJO cardPOJO = CardPOJOs.get(position);

            /*if(cardPOJO.isDefault())
            {
                viewHolder.default_text.setBackgroundColor(ContextCompat.getColor(activity,R.color.button_green));
                viewHolder.default_text.setText("Default Card");
            }
            else
            {
                viewHolder.default_text.setBackgroundColor(ContextCompat.getColor(activity,R.color.red_focus));
                viewHolder.default_text.setText("Set as Default Card");
            }*/

            viewHolder.cardImage.setImageBitmap(BitmapFactory.decodeFile(cardPOJO.getCard_image()));

            /*viewHolder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!CardPOJOs.get(position).isDefault())
                    {
                        for (Iterator<CardPOJO> it = myApplication.getCardList().iterator(); it.hasNext(); ) {
                            CardPOJO cardPOJO1 = it.next();

                            if (!cardPOJO1.isDefault())
                            {
                                cardPOJO.setDefault(true);
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(activity, "Card already set default", Toast.LENGTH_SHORT).show();
                    }

                }
            });*/

            myApplication = (MyApplication) getApplicationContext();

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myApplication.removeCards(position);
                    CardPOJOs.remove(position);
                    notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return view;
    }

    private class ViewHolder {
        TextView default_text;
        CardView subItemCard_view;
        ImageView cardImage;
        FancyButton delete;
    }
}