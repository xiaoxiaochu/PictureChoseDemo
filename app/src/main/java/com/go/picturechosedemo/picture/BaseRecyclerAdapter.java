package com.go.picturechosedemo.picture;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.go.picturechosedemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by go on 2017/9/6.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    protected List<T> mItems;

    protected Context mContext;

    protected LayoutInflater mInflater;

    protected String mSystemTime;

    public static final int STATE_NO_MORE = 1;
    public static final int STATE_LOAD_MORE = 2;
    public static final int STATE_INVALID_NETWORK = 3;
    public static final int STATE_HIDE = 5;
    public static final int STATE_REFRESHING = 6;
    public static final int STATE_LOAD_ERROR = 7;
    public static final int STATE_LOADING = 8;

    public final int BEHAVIOR_MODE;

    protected int mState;

    public static final int NEITHER = 0;
    public static final int ONLY_HEADER = 1;
    public static final int ONLY_FOOTER = 2;
    public static final int BOTH_HEADER_FOOTER = 3;

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_HEADER = -1;
    public static final int VIEW_TYPE_FOOTER = -2;


    public OnLoadingHeaderCallBack headerCallBack;


    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;


    public BaseRecyclerAdapter(Context context, int mode) {
        mItems = new ArrayList<>();
        this.mContext = context;
        this.BEHAVIOR_MODE = mode;
        this.mInflater = LayoutInflater.from(context);
        mState = STATE_HIDE;
        initListener();
    }

    /**
     * 初始化listener
     */
    protected void initListener(){
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position, long itemId) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(position,itemId);
                }
            }
        };


        onLongClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, long itemId) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onLongClick(position, itemId);
                    return true;
                }
                return false;
            }
        };

    };

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_HEADER:
                if (headerCallBack != null)
                    headerCallBack.onCreateHeaderHolder(parent);
                throw new IllegalArgumentException("you have to impl the interface when using this viewType");
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(mInflater.inflate(R.layout.recycler_footer_view,parent,false));
            default:
                final RecyclerView.ViewHolder holder = onCreateDefaultViewHolder(parent,viewType);
                    if (holder != null){
                        holder.itemView.setTag(holder);
                        holder.itemView.setOnLongClickListener(onLongClickListener);
                        holder.itemView.setOnClickListener(onClickListener);
                    }
                    return holder;

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
         switch (holder.getItemViewType()){
             case VIEW_TYPE_HEADER:
                 if (headerCallBack != null){
                     headerCallBack.onBindHeaderHolder(holder,position);
                 }
                 break;
             case VIEW_TYPE_FOOTER:
                 FooterViewHolder fvh = (FooterViewHolder) holder;
                 fvh.itemView.setVisibility(View.VISIBLE);
                 switch (mState){
                     case STATE_INVALID_NETWORK:
                         fvh.tv_footer.setText("网络错误");
                         fvh.pb_footer.setVisibility(View.GONE);
                         break;
                     case STATE_LOAD_MORE:
                     case STATE_LOADING:
                         fvh.tv_footer.setText("正在加载...");
                         fvh.pb_footer.setVisibility(View.VISIBLE);
                         break;
                     case STATE_NO_MORE:
                         fvh.tv_footer.setText("没有更多的数据");
                         fvh.pb_footer.setVisibility(View.GONE);
                         break;
                     case STATE_REFRESHING:
                         fvh.tv_footer.setText("正在刷新");
                         fvh.pb_footer.setVisibility(View.GONE);
                         break;
                     case STATE_LOAD_ERROR:
                         fvh.tv_footer.setText("加载失败");
                         fvh.pb_footer.setVisibility(View.GONE);
                         break;
                     case STATE_HIDE:
                         fvh.itemView.setVisibility(View.GONE);
                         break;
                 }
                 break;
             default:
                 onBindDefaultViewHolder(holder, getItems().get(getIndex(position)), position);
                 break;

         }

    }



    @Override
    public int getItemCount() {
      if (BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == ONLY_HEADER) {
            return mItems.size() + 1;
        } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {
            return mItems.size() + 2;
        } else return mItems.size();
    }

    public final List<T> getItems() {
        return mItems;
    }


    public void addAll(List<T> items){
        if (items != null){
            this.mItems.addAll(items);
            notifyItemRangeInserted(mItems.size(),items.size());
        }
    }

    public void addItem(int position,T item){
        if (item != null){
            this.mItems.set(getIndex(position),item);

        }
    }

    public final void addItem(T item) {
        if (item != null) {
            this.mItems.add(item);
            notifyItemChanged(mItems.size());
        }
    }


    public void replaceItem(int position, T item) {
        if (item != null) {
            this.mItems.set(getIndex(position), item);
            notifyItemChanged(position);
        }
    }

    public void updateItem(int position) {
        if (getItemCount() > position) {
            notifyItemChanged(position);
        }
    }


    public final void removeItem(T item) {
        if (this.mItems.contains(item)) {
            int position = mItems.indexOf(item);
            this.mItems.remove(item);
            notifyItemRemoved(position);
        }
    }

    public final void removeItem(int position) {
        if (this.getItemCount() > position) {
            this.mItems.remove(getIndex(position));
            notifyItemRemoved(position);
        }
    }

    public final T getItem(int position) {
        int p = getIndex(position);
        if (p < 0 || p >= mItems.size())
            return null;
        return mItems.get(getIndex(position));
    }

    public final void resetItem(List<T> items) {
        if (items != null) {
            clear();
            addAll(items);
        }
    }

    public final void clear() {
        this.mItems.clear();
        setState(STATE_HIDE, false);
        notifyDataSetChanged();
    }

    public void setState(int mState, boolean isUpdate) {
        this.mState = mState;
        if (isUpdate)
            updateItem(getItemCount() - 1);
    }

    public int getState() {
        return mState;
    }

    protected int getIndex(int position) {
        return BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER ? position - 1 : position;
    }


    protected abstract RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type);

    protected abstract void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, T item, int position);

     public interface  OnItemClickListener{
        void onItemClick(int position,long itemId);
    }


    public interface  OnItemLongClickListener{
        void onLongClick(int position,long itemId);
    }


    public interface OnLoadingHeaderCallBack{
        RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent);

        void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position);
    }

    /**
     * 添加项点击事件
     *
     * @param onItemClickListener the RecyclerView item click listener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 添加项点长击事件
     *
     * @param onItemLongClickListener the RecyclerView item long click listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }




    /**
     * 当添加到RecyclerView时获取GridLayoutManager布局管理器，修正header和footer显示整行
     *
     * @param recyclerView the mRecyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == VIEW_TYPE_HEADER || getItemViewType(position) == VIEW_TYPE_FOOTER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 当RecyclerView在windows活动时获取StaggeredGridLayoutManager布局管理器，修正header和footer显示整行
     *
     * @param holder the RecyclerView.ViewHolder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            if (BEHAVIOR_MODE == ONLY_HEADER) {
                p.setFullSpan(holder.getLayoutPosition() == 0);
            } else if (BEHAVIOR_MODE == ONLY_FOOTER) {
                p.setFullSpan(holder.getLayoutPosition() == mItems.size() + 1);
            } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {
                if (holder.getLayoutPosition() == 0 || holder.getLayoutPosition() == mItems.size() + 1) {
                    p.setFullSpan(true);
                }
            }
        }
    }


    /**
     * 可以共用同一个listener，相对高效
     */
    public static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int position, long itemId);
    }


    /**
     * 可以共用同一个listener，相对高效
     */
    public static abstract class OnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            return onLongClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract boolean onLongClick(int position, long itemId);
    }



    public static class FooterViewHolder extends RecyclerView.ViewHolder{

        public ProgressBar pb_footer;
        public TextView tv_footer;

        public FooterViewHolder(View itemView) {
            super(itemView);

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
