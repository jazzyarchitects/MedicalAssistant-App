package architect.jazzy.medicinereminder.MedicalAssistant.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.Window;

import architect.jazzy.medicinereminder.MedicalAssistant.Adapters.NewsCategoryAdapter;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 15-Aug-15.
 */
public class NewsFeedCategoryPopup extends Dialog {

    View v;
    RecyclerView recyclerView;
    Context context;
    public NewsFeedCategoryPopup(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_news_category);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        NewsCategoryAdapter adapter=new NewsCategoryAdapter(context);
        adapter.setCategoryClickListener(new NewsCategoryAdapter.CategoryClickListener() {
            @Override
            public void onCategoryClick(Pair<String, String> category) {
                categorySelectListener.onCategorySelect(category);
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);


    }

    CategorySelectListener categorySelectListener;
    public interface CategorySelectListener{
        void onCategorySelect(Pair<String, String> category);
    }

    public void setCategorySelectListener(CategorySelectListener categorySelectListener){
        this.categorySelectListener=categorySelectListener;
    }

}
