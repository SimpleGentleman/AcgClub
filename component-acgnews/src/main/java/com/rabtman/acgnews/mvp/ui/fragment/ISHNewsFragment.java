package com.rabtman.acgnews.mvp.ui.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener;
import com.rabtman.acgnews.R;
import com.rabtman.acgnews.R2;
import com.rabtman.acgnews.base.constant.IntentConstant;
import com.rabtman.acgnews.di.component.DaggerISHNewsItemComponent;
import com.rabtman.acgnews.di.module.ISHNewsItemModule;
import com.rabtman.acgnews.mvp.contract.ISHNewsContract.View;
import com.rabtman.acgnews.mvp.model.entity.SHPostItem;
import com.rabtman.acgnews.mvp.presenter.ISHNewsItemPresenter;
import com.rabtman.acgnews.mvp.ui.activity.ISHNewsDetailActivity;
import com.rabtman.acgnews.mvp.ui.adapter.ISHNewsItemAdapter;
import com.rabtman.common.base.BaseFragment;
import com.rabtman.common.base.widget.CommonItemDecoration;
import com.rabtman.common.di.component.AppComponent;
import java.util.List;

/**
 * @author Rabtman 鼠绘资讯
 */
public class ISHNewsFragment extends BaseFragment<ISHNewsItemPresenter> implements
    View {

  @BindView(R2.id.rcv_news_item)
  RecyclerView rcvNewsItem;
  @BindView(R2.id.swipe_refresh_news)
  SwipeRefreshLayout swipeRefresh;
  private ISHNewsItemAdapter mAdapter;

  @Override
  protected void setupFragmentComponent(AppComponent appComponent) {
    DaggerISHNewsItemComponent.builder()
        .appComponent(appComponent)
        .iSHNewsItemModule(new ISHNewsItemModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected int getLayoutId() {
    return R.layout.acgnews_fragment_news_item;
  }

  @Override
  protected boolean useLoadSir() {
    return true;
  }

  @Override
  protected void onPageRetry(android.view.View v) {
    mPresenter.getAcgNewsList();
  }

  @Override
  protected void initData() {
    mAdapter = new ISHNewsItemAdapter(getAppComponent().imageLoader());
    mAdapter.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, android.view.View view, int position) {
        SHPostItem shPostItem = (SHPostItem) adapter.getData().get(position);
        Intent intent = new Intent(getContext(), ISHNewsDetailActivity.class);
        intent.putExtra(IntentConstant.ISH_NEWS_ITEM, shPostItem);
        startActivity(intent);
      }
    });

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    rcvNewsItem.addItemDecoration(new CommonItemDecoration(2, CommonItemDecoration.UNIT_DP));
    rcvNewsItem.setLayoutManager(layoutManager);
    mAdapter.setOnLoadMoreListener(new RequestLoadMoreListener() {
      @Override
      public void onLoadMoreRequested() {
        mPresenter.getMoreAcgNewsList();
      }
    }, rcvNewsItem);
    rcvNewsItem.setAdapter(mAdapter);

    swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        mPresenter.getAcgNewsList();
      }
    });
    setSwipeRefreshLayout(swipeRefresh);
    mPresenter.getAcgNewsList();
  }

  @Override
  public void showAcgNews(List<SHPostItem> ISHNewsList) {
    mAdapter.setNewData(ISHNewsList);
  }

  @Override
  public void showMoreAcgNews(List<SHPostItem> ISHNewsList, boolean canLoadMore) {
    mAdapter.addData(ISHNewsList);
    mAdapter.loadMoreComplete();
    if (!canLoadMore) {
      mAdapter.loadMoreEnd();
    }
  }

  @Override
  public void showError(String message) {
    if (swipeRefresh.isRefreshing()) {
      swipeRefresh.setRefreshing(false);
    }
    super.showError(message);
  }

  @Override
  public void onLoadMoreFail() {
    mAdapter.loadMoreFail();
  }
}
