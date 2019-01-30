package janjagruti.learning.com.janjagruti.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import janjagruti.learning.com.janjagruti.LoginActivity;
import janjagruti.learning.com.janjagruti.R;
import janjagruti.learning.com.janjagruti.entity.Notice;
import janjagruti.learning.com.janjagruti.util.GeneralUtil;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    private static final int NOTICE = 0;
    private static final int LOADING = 1;

    private List<Notice> noticeList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cv;
        public ViewHolder(CardView cv){
            super(cv);
            this.cv = cv;
        }
    }

    public NoticeAdapter() {}

    public NoticeAdapter(List<Notice> noticeList) {
        this.noticeList = noticeList;
    }

    public List<Notice> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(List<Notice> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cv = null;

        switch (viewType){
            case NOTICE:
                cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notice, parent, false);
                break;

            case LOADING:
                cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_loading, parent, false);
                break;
        }

        return new ViewHolder(cv);
        /*CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notice, parent, false);
        return new ViewHolder(cv);*/
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notice notice = noticeList.get(position);

        if (getItemViewType(position) == NOTICE){

            CardView cv = holder.cv;
            TextView title = cv.findViewById(R.id.txtview_title);
            TextView details = cv.findViewById(R.id.txtview_details);
            TextView postedDateTime = cv.findViewById(R.id.txtview_postedDateTime);

            title.setText(notice.getTitle());
            details.setText(notice.getDetails() + "..");
            postedDateTime.setText(GeneralUtil.getDateFormat().format(notice.getPostedDateTime()));

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LoginActivity.TAG, "Notice Clicked");
                    if (noticeClickListener != null)
                        noticeClickListener.onNoticeClicked(notice.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == noticeList.size() - 1 && isLoadingAdded) ? LOADING : NOTICE;
    }

    /*
     * Helper methods
     */
    public void addItem(Notice notice){
        noticeList.add(notice);
        notifyItemInserted(noticeList.size() - 1);
    }

    public void addAllItems(List<Notice> noticeList){
        int positionStart = this.noticeList.size() - 1;
        if (isLoadingAdded){
            this.noticeList.addAll(positionStart, noticeList);
        } else {
            this.noticeList.addAll(noticeList);
        }
        notifyItemRangeInserted(positionStart, noticeList.size());
    }

    public void removeItem(Notice notice){
        int position = noticeList.indexOf(notice);
        if (position > -1){
            removeAt(position);
        }
    }

    public void removeAt(int position){
        noticeList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear(){
        int itemCount = noticeList.size();
        noticeList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public boolean isEmpty(){
        return getItemCount() == 0;
    }

    private boolean isLoadingAdded = false;

    public void addLoadingFooter(){
        if (!isLoadingAdded) {
            addItem(new Notice());
            isLoadingAdded = true;
        }
    }

    public void removeLoadingFooter(){
        int lastPosition = noticeList.size() - 1;
        Notice item = getItem(lastPosition);
        if (isLoadingAdded && item != null){
            noticeList.remove(lastPosition);
            notifyItemRemoved(lastPosition);
            isLoadingAdded = false;
        }
    }

    public Notice getItem(int position){
        return noticeList.get(position);
    }

    /*
     * Interface for click on the card view
     */
    private NoticeClickListener noticeClickListener = null;

    public interface NoticeClickListener{
        void onNoticeClicked(int noticeId);
    }

    public NoticeClickListener getNoticeClickListener() {
        return noticeClickListener;
    }

    public void setNoticeClickListener(NoticeClickListener noticeClickListener) {
        this.noticeClickListener = noticeClickListener;
    }
}
