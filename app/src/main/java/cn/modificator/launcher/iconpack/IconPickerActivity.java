package cn.modificator.launcher.iconpack;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;

import java.util.Collections;
import java.util.List;

import cn.modificator.launcher.R;
import cn.modificator.launcher.Utilities;
import cn.modificator.launcher.blur.BlurWallpaperProvider;
import cn.modificator.launcher.config.FeatureFlags;

public class IconPickerActivity extends AppCompatActivity implements IconGridAdapter.Listener {

    private EditIconActivity.IconPackInfo mIconPackInfo;
    private IconCategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FeatureFlags.INSTANCE.applyDarkTheme(this);
        Utilities.setupPirateLocale(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_picker);

        if (!loadIconPack()) {
            finish();
            return;
        }

        setTitle(mIconPackInfo.label);

        RecyclerView recyclerView = findViewById(R.id.categoryRecyclerView);
        mAdapter = new IconCategoryAdapter();
        mAdapter.setCategoryList(Collections.<IconPack.IconCategory>emptyList());
        mAdapter.setListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);
        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        BlurWallpaperProvider.Companion.applyBlurBackground(this);
        new LoadIconTask(this).execute();
    }

    private boolean loadIconPack() {
        if (getIntent() == null) return false;

        ResolveInfo resolveInfo = getIntent().getParcelableExtra("resolveInfo");
        String packageName = getIntent().getStringExtra("packageName");

        mIconPackInfo = new EditIconActivity.IconPackInfo(
                IconPackProvider.loadAndGetIconPack(this, packageName),
                resolveInfo,
                getPackageManager()
        );

        return true;
    }

    @Override
    public void onSelect(IconPack.IconEntry iconEntry) {
        Intent data = new Intent();
        data.putExtra("packageName", iconEntry.getPackageName());
        data.putExtra("resource", iconEntry.resourceName);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class LoadIconTask extends AsyncTask<Void, Void, List<IconPack.IconCategory>> {

        private final IconPickerActivity mActivity;

        LoadIconTask(IconPickerActivity activity) {
            mActivity = activity;
        }

        @Override
        protected List<IconPack.IconCategory> doInBackground(Void... voids) {
            return mActivity.mIconPackInfo.iconPack.getIconList();
        }

        @Override
        protected void onPostExecute(List<IconPack.IconCategory> iconCategories) {
            mActivity.mAdapter.setCategoryList(iconCategories);
        }
    }
}
