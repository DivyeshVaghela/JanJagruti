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
import janjagruti.learning.com.janjagruti.entity.Material;
import janjagruti.learning.com.janjagruti.util.GeneralUtil;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private static final int MATERIAL = 0;
    private static final int LOADING = 1;

    private LayoutInflater _layoutInflator;
    private ImageLoader _imageLoader;

    private List<Material> materialList = new ArrayList<>();

    public class MaterialViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        public MaterialViewHolder(CardView cv) {
            super(cv);
            this.cv = cv;
        }
    }

    public MaterialAdapter() {}

    public MaterialAdapter(List<Material> materialList) {
        this.materialList = materialList;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cv = null;

        if (_layoutInflator == null){
            _layoutInflator = LayoutInflater.from(parent.getContext());
        }

        switch (viewType){
            case MATERIAL:
                cv = (CardView) _layoutInflator.inflate(R.layout.card_material, parent, false);
                break;

            case LOADING:
                cv = (CardView) _layoutInflator.inflate(R.layout.card_loading, parent, false);
                break;
        }

        return new MaterialViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {

        switch (getItemViewType(position)){

            case MATERIAL:

                if (_imageLoader == null){
                    _imageLoader = AppController.getInstance().getImageLoader();
                }

                CardView cv = holder.cv;
                NetworkImageView img_materialLogo = cv.findViewById(R.id.img_materialLogo);
                TextView txtview_title = cv.findViewById(R.id.txtview_title);
                TextView txtview_subtitle = cv.findViewById(R.id.txtview_subtitle);
                TextView txtview_postedDateTime = cv.findViewById(R.id.txtview_postedDateTime);

                final Material material = getItem(position);
                img_materialLogo.setImageUrl(material.getType().getLogoUrl(), _imageLoader);
                img_materialLogo.setContentDescription(material.getTitle());

                txtview_title.setText(material.getTitle());
                txtview_subtitle.setText(material.getSubtitle());
                txtview_postedDateTime.setText(GeneralUtil.getDateFormat().format(material.getCreatedAt()));

                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (materialClickInterface != null)
                            materialClickInterface.onMaterialClicked(material.getId(), material.getTitle());
                    }
                });

                break;

            case LOADING:
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position == materialList.size() - 1 && isLoadingAdded) ? LOADING : MATERIAL;
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    /**
     * Helper methods
     */
    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }

    public void addItem(Material material){
        this.materialList.add(material);
        notifyItemInserted(materialList.size() - 1);
    }

    public void addAllItems(List<Material> materialList){
        int positionStart = this.materialList.size() == 0 ? 0 : this.materialList.size() - 1;

        if (isLoadingAdded){
            this.materialList.addAll(positionStart, materialList);
        } else {
            this.materialList.addAll(materialList);
        }
        notifyItemRangeInserted(positionStart, materialList.size());
    }

    public void removeAt(int position){
        materialList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear(){
        int itemCount = materialList.size();
        materialList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public Material getItem(int position){
        return materialList.get(position);
    }

    private boolean isLoadingAdded = false;

    public void addLoadingFooter(){
        if (!isLoadingAdded){
            addItem(new Material());
            isLoadingAdded = true;
        }
    }

    public void removeLoadingFooter(){
        if (isLoadingAdded) {
            int lastPosition = materialList.size() - 1;
            Material item = getItem(lastPosition);

            if (item != null){
                removeAt(lastPosition);
                isLoadingAdded = false;
            }
        }
    }

    /**
     * event interfaces
     */
    public interface MaterialClickInterface{
        void onMaterialClicked(int materialId, String materialTitle);
    }

    private MaterialClickInterface materialClickInterface;

    public MaterialClickInterface getMaterialClickInterface() {
        return materialClickInterface;
    }

    public void setMaterialClickInterface(MaterialClickInterface materialClickInterface) {
        this.materialClickInterface = materialClickInterface;
    }
}
