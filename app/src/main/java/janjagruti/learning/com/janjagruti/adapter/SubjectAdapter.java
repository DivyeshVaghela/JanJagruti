package janjagruti.learning.com.janjagruti.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import janjagruti.learning.com.janjagruti.R;
import janjagruti.learning.com.janjagruti.config.AppController;
import janjagruti.learning.com.janjagruti.entity.Subject;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private LayoutInflater _layoutInflator;
    private ImageLoader _imageLoader;

    private List<Subject> subjectList = new ArrayList<>();

    public class SubjectViewHolder extends RecyclerView.ViewHolder{

        public CardView cv;
        public SubjectViewHolder(CardView cv){
            super(cv);
            this.cv = cv;
        }
    }

    public SubjectAdapter() {}

    public SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (_layoutInflator == null)
            _layoutInflator = LayoutInflater.from(parent.getContext());

        CardView subjectCardView = (CardView) _layoutInflator.inflate(R.layout.card_subject, parent, false);
        return new SubjectViewHolder(subjectCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {

        if (_imageLoader == null)
            _imageLoader = AppController.getInstance().getImageLoader();

        CardView cv = holder.cv;
        NetworkImageView img_logo = cv.findViewById(R.id.img_logo);
        TextView txtview_title = cv.findViewById(R.id.txtview_title);
        TextView txtview_subtitle = cv.findViewById(R.id.txtview_subtitle);

        final Subject subject = subjectList.get(position);

        img_logo.setImageUrl(subject.getLogoUrl(), _imageLoader);
        img_logo.setContentDescription(subject.getTitle());

        txtview_title.setText(subject.getTitle());
        txtview_subtitle.setText(subject.getSubtitle());

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subjectClickListener != null)
                    subjectClickListener.onSubjectClicked(subject.getId(), subject.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    /**
     * Helper methods
     */
    public void addAllItems(List<Subject> subjectList){
        int positionStart = this.subjectList.size() == 0 ? 0 : this.subjectList.size() - 1;
        this.subjectList.addAll(subjectList);
        notifyItemRangeInserted(positionStart, subjectList.size());
    }

    /**
     * Interfaces
     */
    public interface SubjectClickListener{
        void onSubjectClicked(int subjectId, String subjectTitle);
    }

    private SubjectClickListener subjectClickListener;

    public SubjectClickListener getSubjectClickListener() {
        return subjectClickListener;
    }

    public void setSubjectClickListener(SubjectClickListener subjectClickListener) {
        this.subjectClickListener = subjectClickListener;
    }
}
