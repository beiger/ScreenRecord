package com.bing.example.otherdetails;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;

import com.bing.example.R;
import com.bing.mvvmbase.utils.UiUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.header.FlyRefreshHeader;
import com.scwang.smartrefresh.header.flyrefresh.FlyView;
import com.scwang.smartrefresh.header.flyrefresh.MountainSceneView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

public class AboutActivity extends AppCompatActivity {
        private static boolean isFirstEnter = false;
        private RefreshLayout mRefreshLayout;
        private FlyView mFlyView;
        private FlyRefreshHeader mFlyRefreshHeader;
        private CollapsingToolbarLayout mToolbarLayout;
        private FloatingActionButton mActionButton;
        private View.OnClickListener mThemeListener;
        private NestedScrollView mScrollView;

        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_about);
                UiUtil.setBarColorAndFontWhite(this, 0);
                final Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(v -> finish());
                UiUtil.setPaddingSmart(this, toolbar);
                MountainSceneView mSceneView = findViewById(R.id.mountain);
                mFlyView = findViewById(R.id.flyView);
                mFlyRefreshHeader = findViewById(R.id.flyRefresh);
                mFlyRefreshHeader.setUp(mSceneView, mFlyView);
                mRefreshLayout = findViewById(R.id.refreshLayout);
                mRefreshLayout.setReboundInterpolator(new ElasticOutInterpolator());
                mRefreshLayout.setReboundDuration(800);
                mRefreshLayout.setOnRefreshListener(refreshLayout -> {
                        updateTheme();
                        mRefreshLayout.getLayout().postDelayed(() -> mFlyRefreshHeader.finishRefresh(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                }
                        }), 2000L);
                });
                final AppBarLayout appBar = findViewById(R.id.appbar);
                mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
                        public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                                appBar.setTranslationY((float) offset);
                                toolbar.setTranslationY((float) (-offset));
                        }
                });
                if (isFirstEnter) {
                        isFirstEnter = false;
                        mRefreshLayout.autoRefresh();
                }

                mToolbarLayout = findViewById(R.id.toolbarLayout);
                mActionButton = findViewById(R.id.fab);
                mScrollView = findViewById(R.id.scrollView);
                mActionButton.setOnClickListener(v -> {
                        updateTheme();
                        mRefreshLayout.autoRefresh();
                });
                appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                        boolean misAppbarExpand = true;

                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                int scrollRange = appBarLayout.getTotalScrollRange();
                                float fraction = 1.0F * (float) (scrollRange + verticalOffset) / (float) scrollRange;
                                ValueAnimator animator;
                                if ((double) fraction < 0.1D && misAppbarExpand) {
                                        misAppbarExpand = false;
                                        mActionButton.animate().scaleX(0.0F).scaleY(0.0F);
                                        mFlyView.animate().scaleX(0.0F).scaleY(0.0F);
                                        animator = ValueAnimator.ofInt(new int[]{mScrollView.getPaddingTop(), 0});
                                        animator.setDuration(300L);
                                        animator.addUpdateListener(animation -> mScrollView.setPadding(0, (Integer) animation.getAnimatedValue(), 0, 0));
                                        animator.start();
                                }

                                if ((double) fraction > 0.8D && !misAppbarExpand) {
                                        misAppbarExpand = true;
                                        mActionButton.animate().scaleX(1.0F).scaleY(1.0F);
                                        mFlyView.animate().scaleX(1.0F).scaleY(1.0F);
                                        animator = ValueAnimator.ofInt(new int[]{mScrollView.getPaddingTop(), DensityUtil.dp2px(25.0F)});
                                        animator.setDuration(300L);
                                        animator.addUpdateListener(animation -> mScrollView.setPadding(0, (Integer) animation.getAnimatedValue(), 0, 0));
                                        animator.start();
                                }

                        }
                });
        }

        private void updateTheme() {
                if (mThemeListener == null) {
                        mThemeListener = new View.OnClickListener() {
                                int index = 0;
                                int[] ids;

                                {
                                        ids = new int[]{R.color.colorPrimary, 17170452, 17170454, 17170456, 17170459};
                                }

                                public void onClick(View v) {
                                        int color = ContextCompat.getColor(getApplication(), ids[index % ids.length]);
                                        mRefreshLayout.setPrimaryColors(new int[]{color});
                                        mActionButton.setBackgroundColor(color);
                                        mActionButton.setBackgroundTintList(ColorStateList.valueOf(color));
                                        mToolbarLayout.setContentScrimColor(color);
                                        ++index;
                                }
                        };
                }

                mThemeListener.onClick(null);
        }

        public class ElasticOutInterpolator implements Interpolator {
                public ElasticOutInterpolator() {
                }

                public float getInterpolation(float t) {
                        if (t == 0.0F) {
                                return 0.0F;
                        } else if (t >= 1.0F) {
                                return 1.0F;
                        } else {
                                float p = 0.3F;
                                float s = p / 4.0F;
                                return (float) Math.pow(2.0D, (double) (-10.0F * t)) * (float) Math.sin((double) ((t - s) * 6.2831855F / p)) + 1.0F;
                        }
                }
        }
}
