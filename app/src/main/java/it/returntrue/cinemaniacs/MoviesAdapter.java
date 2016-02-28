/*
 * Copyright (C) 2016 Alessandro Riperi
*/

package it.returntrue.cinemaniacs;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import it.returntrue.cinemaniacs.data.MoviesContract.MovieEntry;
import it.returntrue.cinemaniacs.data.MovieData;
import it.returntrue.cinemaniacs.provider.MoviesProvider;
import it.returntrue.cinemaniacs.utilities.PaletteCache;
import it.returntrue.cinemaniacs.utilities.Utilities;

/**
 * Adapts data returned from cursor to show in a RecyclerView
 * */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    public static final String TAG = MoviesAdapter.class.getSimpleName();

    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private final String IMAGE_SIZE = "w185";
    private final int MAX_ITEM_COUNT = 18;
    private final Context mContext;

    private Cursor mCursor;
    private OnItemClickListener mListener;

    /** Provides listeners for click events */
    public interface OnItemClickListener {
        void onFavoriteClick(View view, long id);
        void onItemClick(View view, Uri uri);
    }

    public MoviesAdapter(Context context) {
        mContext = context;
    }

    /** Sets a new cursor and notifies the change */
    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /** Sets a new click events listener */
    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.movies_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        final int id = MovieData.getId(mCursor);
        final String title = MovieData.getTitle(mCursor);
        final String coverUrl = IMAGE_BASE_URL + IMAGE_SIZE + MovieData.getCoverPath(mCursor);

        Glide.with(mContext)
                .load(coverUrl)
                .asBitmap()
                .into(new BitmapImageViewTarget(holder.imageCover) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);

                        Palette.Swatch swatch = PaletteCache
                                .getPaletteSwatch(id, bitmap);

                        if (swatch != null) {
                            holder.itemInfo.setBackgroundColor(swatch.getRgb());
                            holder.textTitle.setTextColor(swatch.getTitleTextColor());
                            holder.textReleaseDate.setTextColor(swatch.getBodyTextColor());
                        }
                        else {
                            holder.itemInfo.setBackgroundColor(
                                    ContextCompat.getColor(mContext, R.color.colorPrimary));
                            holder.textTitle.setTextColor(Color.WHITE);
                            holder.textReleaseDate.setTextColor(Color.WHITE);
                        }
                    }
                });

        holder.textTitle.setText(MovieData.getTitle(mCursor));
        holder.textReleaseDate.setText(Utilities.formatYear(MovieData.getReleaseDate(mCursor)));
        holder.imageFavorite.setImageResource(Utilities.toggleImageResource(MovieData.getIsFavorite(mCursor),
                R.drawable.ic_favorite_full, R.drawable.ic_favorite_empty));
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? Math.min(mCursor.getCount(), MAX_ITEM_COUNT) : 0;
    }

    /** Represents a ViewHolder for a RecyclerView item */
    public final class ViewHolder extends RecyclerView.ViewHolder {
        protected final ImageView imageCover;
        protected final RelativeLayout itemInfo;
        protected final TextView textTitle;
        protected final TextView textReleaseDate;
        protected final ImageView imageFavorite;

        public ViewHolder(View view) {
            super(view);
            imageCover = (ImageView)view.findViewById(R.id.image_cover);
            itemInfo = (RelativeLayout)view.findViewById(R.id.item_info);
            textTitle = (TextView)view.findViewById(R.id.text_title);
            textReleaseDate = (TextView)view.findViewById(R.id.text_release_date);
            imageFavorite = (ImageView)view.findViewById(R.id.image_favorite);

            imageFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mCursor.moveToPosition(getAdapterPosition());
                        mListener.onFavoriteClick(v, Utilities.getLong(mCursor, MovieEntry._ID));
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mCursor.moveToPosition(getAdapterPosition());
                        mListener.onItemClick(v, MoviesProvider.buildMovieUri(
                                Utilities.getLong(mCursor, MovieEntry._ID)));
                    }
                }
            });
        }
    }
}