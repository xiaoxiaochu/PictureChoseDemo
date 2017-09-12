package com.go.picturechosedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.common.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by go on 2017/9/5.
 */

public class TweetSelectImageAdapter extends RecyclerView.Adapter<TweetSelectImageAdapter.TweetSelectImageHolder> implements TweetPicturesPreviewerItemTouchCallback.ItemTouchHelperAdapter{

    private final int MAX_SIZE = 9;
    private final int TYPE_NONE = 0;
    private final int TYPE_ADD = 1;
    private Callback mCallback;
    private final List<Model> mModels = new ArrayList<>();

    public TweetSelectImageAdapter(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public TweetSelectImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_publish_selecter,parent,false);



         if (viewType == TYPE_NONE){
             return new TweetSelectImageHolder(view, new TweetSelectImageHolder.HolderListener() {
                 @Override
                 public void onDelete(Model model) {
                        Callback callback = mCallback;
                        if (callback != null){
                            int pos = mModels.indexOf(model);
                            if (pos == -1){
                                return;
                            }
                            mModels.remove(pos);
                            if (mModels.size() > 0)
                                notifyItemRemoved(pos);
                            else
                                notifyDataSetChanged();

                        }
                 }

                 @Override
                 public void onDrag(TweetSelectImageHolder holder) {
                     Callback callback = mCallback;
                     if (callback != null) {
                         // Start a drag whenever the handle view it touched
                         mCallback.onStartDrag(holder);
                     }

                 }

                 @Override
                 public void onClick(Model model) {
                    // ImageGalleryActivity.show(mCallback.getContext(), model.path, false);
                 }
             });
         }else {
             return new TweetSelectImageHolder(view, new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Callback callback = mCallback;
                     if (callback != null) {
                         callback.onLoadMoreClick();
                     }
                 }
             });
         }


    }


    @Override
    public void onViewRecycled(TweetSelectImageHolder holder) {
        Glide.clear(holder.mImage);
    }

    @Override
    public void onBindViewHolder(TweetSelectImageHolder holder, int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE || size != position) {
            Model model = mModels.get(position);
            holder.bind(position, model, mCallback.getImgLoader());
        }

    }

    @Override
    public int getItemCount() {
        int size = mModels.size();
        if (size == MAX_SIZE) {
            return size;
        } else if (size == 0) {
            return 0;
        } else {
            return size + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE)
            return TYPE_NONE;
        else if (position == size) {
            return TYPE_ADD;
        } else {
            return TYPE_NONE;
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Collections.swap(mModels, fromPosition, toPosition);
        if (fromPosition == toPosition)
            return false;

        // Move fromPosition to toPosition
        CollectionUtil.move(mModels, fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mModels.remove(position);
        notifyItemRemoved(position);
    }

    public static class Model {
        public Model(String path) {
            this.path = path;
        }

        public String path;
        public boolean isUpload;
    }


    public void clear() {
        mModels.clear();
    }

    public void add(Model model) {
        if (mModels.size() >= MAX_SIZE)
            return;
        mModels.add(model);
    }

    public void add(String path) {
        add(new Model(path));
    }

    public String[] getPaths() {
        int size = mModels.size();
        if (size == 0)
            return null;
        String[] paths = new String[size];
        int i = 0;
        for (Model model : mModels) {
            paths[i++] = model.path;
        }
        return paths;
    }


    public interface Callback {
        void onLoadMoreClick();

        RequestManager getImgLoader();

        Context getContext();

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }



    static class TweetSelectImageHolder extends RecyclerView.ViewHolder{
        private ImageView mImage;
        private ImageView mDelete;
        private ImageView mGifMask;
        private HolderListener mListener;

        private TweetSelectImageHolder(View itemView, final HolderListener listener) {
            super(itemView);
            mListener = listener;
            mImage = itemView.findViewById(R.id.iv_content);
            mDelete = itemView.findViewById(R.id.iv_delete);
            mGifMask = itemView.findViewById(R.id.iv_is_gif);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object obj =  view.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof TweetSelectImageAdapter.Model){
                        holderListener.onDelete((Model) obj);
                    }


                }
            });

            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object obj = mDelete.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof TweetSelectImageAdapter.Model) {
                        holderListener.onClick((TweetSelectImageAdapter.Model) obj);
                    }
                }
            });

            mImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final HolderListener holderListener = mListener;
                    if (holderListener != null) {
                        holderListener.onDrag(TweetSelectImageHolder.this);
                    }
                    return true;
                }
            });
            mImage.setBackgroundColor(0xffdadada);

        }

        private TweetSelectImageHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);

            mImage = itemView.findViewById(R.id.iv_content);
            mDelete = itemView.findViewById(R.id.iv_delete);

            mDelete.setVisibility(View.GONE);
            mImage.setImageResource(R.mipmap.ic_tweet_add);
            mImage.setOnClickListener(clickListener);
            mImage.setBackgroundDrawable(null);
        }


        public void bind(int position,TweetSelectImageAdapter.Model model,RequestManager loader){

            mDelete.setTag(model);
            Glide.clear(mImage);
            if (model.path.toLowerCase().endsWith("gif")){
                loader.load(model.path)
                        .asBitmap()
                        .centerCrop()
                        .error(R.mipmap.ic_split_graph)
                        .into(mImage);
                mGifMask.setVisibility(View.VISIBLE);
            }else {
                loader.load(model.path)
                        .centerCrop()
                        .error(R.mipmap.ic_split_graph)
                        .into(mImage);
                mGifMask.setVisibility(View.GONE);
            }

        }

        /**
         * Holder 与Adapter之间的桥梁
         */
        interface HolderListener {

            void onDelete(TweetSelectImageAdapter.Model model);

            void onDrag(TweetSelectImageHolder holder);

            void onClick(TweetSelectImageAdapter.Model model);
        }
    }

}
