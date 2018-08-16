package com.example.webtest;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.webtest.base.Constant;
import com.example.webtest.base.MyWebView;
import com.example.webtest.base.WA_YundaFragment;
import com.example.webtest.io.LogUtil;
import com.example.webtest.io.SharedPreferencesUtils;
import com.example.webtest.io.WA_Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 自动化Fragment主调页面
 * @author z.h
 */
public class WA_MainFragment extends WA_YundaFragment implements View.OnClickListener {
	private static final String ARG_CODE = "WebAutoFragment";
	private static final String BASIC_JS_PATH = "basic_inject.js";
	private static final String LOGIC_JS_PATH = "logic_inject.js";


	/**  通过静态方法实例化自动化Fragment*/
	public static void start(Activity mContext, int containerRsID, WA_Parameters parameter)
	{
		WA_MainFragment mCurrentFragment = WA_MainFragment.getInstence(parameter);
		mContext.getFragmentManager().beginTransaction().replace(containerRsID, mCurrentFragment).commit();
	}

	private static WA_MainFragment getInstence(WA_Parameters parameter)
	{
		WA_MainFragment webAutoFragment = new WA_MainFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(ARG_CODE, parameter);
		webAutoFragment.setArguments(bundle);
		return webAutoFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		instrumentation = new Instrumentation();
		if (null != bundle)
		{
			parameter = (WA_Parameters) bundle.getSerializable(ARG_CODE);
		}
		Resources res = getResources();
		shops = res.getStringArray(R.array.classify);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.wa_fragment_main, container, false);
		findViews(view);
		initData();
		setListener(view);
		return view;
	}

	private void findViews(View view)
	{
		listWeb = (MyWebView) view.findViewById(R.id.wa_webview_list);
		wv_upload = (WebView) view.findViewById(R.id.wv_upload);
		btnRefresh = (Button) view.findViewById(R.id.btn_refresh);
		btnBack = (Button) view.findViewById(R.id.btn_back);
		btnSearch = (Button) view.findViewById(R.id.btn_search);
		btnGosearch = (Button) view.findViewById(R.id.btn_gosearch);
		btnGosearchworld = (Button) view.findViewById(R.id.btn_gosearchworld);
		btnGetchecked = (Button) view.findViewById(R.id.btn_getchecked);
		btn_check = (Button) view.findViewById(R.id.btn_check);
		btn_biao1 = (Button) view.findViewById(R.id.btn_biao1);
		btn_str_result = (Button) view.findViewById(R.id.btn_str_result);
	}

	/** 初始化两个不同功用的WebView */
	private void initData()
	{
		initWebview(listWeb);
		initWebview(wv_upload);

		//deleteLog();
		injectJS = getJsFromFile(getActivity(), BASIC_JS_PATH) + getJsFromFile(getActivity(), LOGIC_JS_PATH);
	}



	private void setListener(View view)
	{

		btnRefresh.setOnClickListener(this);


		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				listWeb.goBack();
			}
		});
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				TAOBAO = shops[0];
				goSearch(shops[index],randomtime);
			}
		});
		btnGosearch.setOnClickListener(this);
		btnGosearchworld.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goSearchWord();
			}
		});
		btnGetchecked.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goGetChecked();
			}
		});
		btn_check.setOnClickListener(this);
		btn_biao1.setOnClickListener(this);
		btn_str_result.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				for (int i = 0; i < shops.length; i++) {
					String str = SharedPreferencesUtils.getValue(getActivity(),TAOBAO,shops[i]+"zjl","");
					String rcstr = SharedPreferencesUtils.getValue(getActivity(),TAOBAO,shops[i]+"rc","");
					String titlestr = SharedPreferencesUtils.getValue(getActivity(),TAOBAO,shops[i]+"title","");
					Log.e("resultStr!!! ",str );
					Log.e("rcstr!!! ",rcstr );
					Log.e("titlestr!!! ",titlestr );
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_biao1:
				String str = "575292124604###575292616306";
				handlerJs("selectXiajia(\"" + str + "\");");

				break;
			case R.id.btn_check:
				handlerJs("xiajia();");
				break;
			case R.id.btn_refresh:

				listWeb.loadUrl(Constant.URL);

//				String url = listWeb.getUrl() + "&userType=0&jpmj=1&startBiz30day=200&startPrice=0&endPrice=100&level=1";
//				loadUrl(url);
				break;
			case R.id.btn_gosearch:

				handlerJs("shangjiaNow();", wv_upload);

//				handlerJs("tblmShopList();");
				break;
		}


	}


}
