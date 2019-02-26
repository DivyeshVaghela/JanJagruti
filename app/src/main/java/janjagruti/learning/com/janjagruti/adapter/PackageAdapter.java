package janjagruti.learning.com.janjagruti.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import janjagruti.learning.com.janjagruti.R;
import janjagruti.learning.com.janjagruti.entity.Package;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    private LayoutInflater _layoutInflator;
    private List<Package> packageList = new ArrayList<>();

    public class PackageViewHolder extends RecyclerView.ViewHolder{

        public CardView cv;
        public PackageViewHolder(CardView cv) {
            super(cv);
            this.cv = cv;
        }
    }

    public PackageAdapter() {
    }

    public PackageAdapter(List<Package> packageList) {
        this.packageList = packageList;
    }

    public List<Package> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<Package> packageList) {
        this.packageList = packageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (_layoutInflator == null)
            _layoutInflator = LayoutInflater.from(parent.getContext());

        CardView packageCardView = (CardView)_layoutInflator.inflate(R.layout.card_package, parent, false);
        return new PackageViewHolder(packageCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {

        CardView cv = holder.cv;
        TextView txtview_title = cv.findViewById(R.id.txtview_title);
        TextView txtview_subtitle = cv.findViewById(R.id.txtview_subtitle);
        TextView txtview_rateLine = cv.findViewById(R.id.txtview_rateLine);

        final Package aPackage = packageList.get(position);
        txtview_title.setText(aPackage.getTitle());
        txtview_subtitle.setText(aPackage.getSubtitle());
        txtview_rateLine.setText(String.format(cv.getResources().getString(R.string.rate_format), aPackage.getRate(), aPackage.getDuration(), aPackage.getDurationScale()));

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (packageClickListener != null)
                    packageClickListener.onPackageClicked(aPackage.getId(), aPackage.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public interface PackageClickListener{
        void onPackageClicked(int packageId, String packageTitle);
    }

    private PackageClickListener packageClickListener;

    public PackageClickListener getPackageClickListener() {
        return packageClickListener;
    }

    public void setPackageClickListener(PackageClickListener packageClickListener) {
        this.packageClickListener = packageClickListener;
    }
}
