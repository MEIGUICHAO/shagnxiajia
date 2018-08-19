package com.example.webtest.base;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.webtest.WA_MainFragment;
import com.example.webtest.io.LogUtil;
import com.example.webtest.io.SharedPreferencesUtils;
import com.example.webtest.io.WA_Parameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * @author z.h
 * @desc 存放基本业务逻辑&Js调用本地方法的接口函数
 */
public class WA_YundaFragment extends WA_BaseFragment
{



	protected Instrumentation instrumentation;
	private HashMap<String, Float> jzlMap;
	private HashMap<String, Float> rcMap;
	private HashMap<String, Float> titleMap;
	protected String[] shops;
	protected int index = 0;
	protected int randomtime = 1000;
	protected String resultStr = "";
	protected String rcresultStr = "";
	protected String titleresultStr = "";
	protected String TAOBAO = "TAOBAO";
	protected String TAOBAOJZL = "TAOBAOJZL";
	protected String TAOBAORC = "TAOBAORC";
	protected String TAOBAOTITLE = "TAOBAOTITLE";

	protected LocalMethod mLocalMethod;
	protected WA_Parameters parameter;
	protected String injectJS;
	protected int templeIndex;
	protected int SWITCH_UPLOAD_METHOD = -1;
	protected ArrayList<String> xiajiaRecordList;
	protected int shangjiaIndex;
	protected String shangjiaRecordStr;
    private boolean XIAJIA_STOP = false;
	private int SWITCH_DEFAULT_METHOD;
	private boolean FIRST_SHANGJIA = true;
	private boolean SHANGJIA_STOP = false;


	protected enum SearchType
	{
		All, Shop, Mall
	}

	public Handler handler = new Handler();

	protected void goSearchClick() {

		listWeb.loadUrl("https://sycm.taobao.com/mq/words/search_words.htm");
//		handlerJs("goSearchClick();");
	}
	protected void goSearchWord() {

		handlerJs("titleCombination();");
	}
	public void biao1() {
		listWeb.reload();
		handlerJs("relativeTitle();",3000+randomtime);
//		handlerJs("relativeTitle();");
	}
	protected void goGetChecked() {

		handlerJs("goGetChecked();");
	}
	protected void check() {

		handlerJs("check();");
	}

	protected void goSearch(final String search,int randomtime) {
//		handlerJs("setSearchWord(\""+search+"\",\""+randomtime+"\");");
		handlerJs("setSearchWord(\"" + search + "\");");
	}

	protected void handlerJs(final String strlogic) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				String logicStr = strlogic;
				String completeJs = doAutoTest(logicStr);
				loadUrl(listWeb, completeJs);
			}
		});
	}

	protected void handlerJs(final String strlogic,final WebView webView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				String logicStr = strlogic;
				String completeJs = doAutoTest(logicStr);
				loadUrl(webView, completeJs);
			}
		});
	}

	public void handlerJs(final String strlogic,long time) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String logicStr = strlogic;
				String completeJs = doAutoTest(logicStr);
				loadUrl(listWeb, completeJs);
			}
		},time);
	}

	public void handlerJs(final String strlogic, long time, final WebView webView) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String logicStr = strlogic;
				String completeJs = doAutoTest(logicStr);
				loadUrl(webView, completeJs);
			}
		},time);
	}

	/** Function：选择商品所在的商铺类型(天猫或淘宝) */
	protected String selectSearchType(boolean isTMall)
	{
		String str = "var sortType= doGetTextByCN(\"s-input-tab-txt\");" + "if(!" + isTMall + "){" + "if(sortType!=\"天猫\"){" + "doClickByCN(\"s-input-tab-txt\",1);" + "doClickByCN(\"all\",2);" + "doClickByCN(\"s-input-tab-txt\",2);" + "}}else{" + "if(sortType!=\"宝贝\"){" + "doClickByCN(\"s-input-tab-txt\",1);" + "doClickByCN(\"mall\",2);" + "doClickByCN(\"s-input-tab-txt\",2);" + "}}";
		return str;
	}

	/** Function：点击进入搜索(BelongTo Step1) */
	protected void doEnterSearchPage()
	{
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				enterSearchPage(listWeb);
			}
		});
	}

	/** Function：选择商铺类型(BelongTo Step2) */
	protected void doSelectStoreType(final WA_Parameters parameter)
	{
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				selectStoreType(listWeb, parameter.getIsTMall());
			}
		});
	}

	/** Function：进行商品搜索(BelongTo Step2) */
	protected void doSearch(final WA_Parameters parameter)
	{
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				searchFor(listWeb, parameter.getKeywordStr());
			}
		});
	}

	/** Function：首次进行商品浏览(BelongTo Step3) */
	protected void doScan(final WA_Parameters parameter)
	{
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				scanGoods(listWeb, parameter.getTitleStr());
			}
		}, 4000);

	}

	/** Function：根据销量排序(BelongTo Step4) */
	protected void doOrderBySellAmount()
	{

		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				orderBySellAmount(listWeb);
			}
		});
	}

	/** Function：若当前页中不存在该商铺则翻页，同时另一个页面进行随机商品浏览，浏览时长随机(BelongTo Step5) */
	protected void doFlipAndScan(final WA_Parameters parameter, final int randomTime)
	{
		// 跳转到下一页
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				getNextPage(listWeb);
			}
		}, 2000);

		// 在当前页查找，若没查到则翻到下一页递归查找
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				scanGoods(listWeb, parameter.getTitleStr());
			}
		}, 5000 + randomTime * 1000);
	}

	/** Function：不翻页，在当前页进行随机商品浏览，浏览时长随机(BelongTo Step5) */
	protected void doScanForLongTime(final WA_Parameters parameter, final int randomTime)
	{
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				scanGoods(listWeb, parameter.getTitleStr());
			}
		}, 5000 + randomTime * 1000);

	}


	/** Function：选择商品SKU */
	protected void doSelectSku()
	{

	}

	/** 点击进入搜索页面(主页面) */
	private void enterSearchPage(WebView webView)
	{
		// 拼接业务逻辑
//		String logicStr = "doClickByRI(\"search-placeholder\",2);";
		//侧滑菜单
//		String logicStr = "doClickByCN(\"button button-icon button-clear\",2);";
		String logicStr = "selectNumRange(3,2);";
//		String logicStr = "selectNumRange(\"col col-50 bet ok\",2);";
//		String logicStr = "selectNumRange(2);";
		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 选择店铺类型 */
	private void selectStoreType(WebView webView, boolean isTMall)
	{
		// 拼接业务逻辑
//		String logicStr = selectSearchType(isTMall);
		String logicStr = "doComfir();";
		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 输入搜索内容，然后查找 */
	private void searchFor(WebView webView, String keywordStr)
	{
		// 拼接业务逻辑
		String logicStr = "doInputByCN(\"J_autocomplete\",\"" + keywordStr + "\",2);" + "doClickByCN(\"icons-search\",4);";

		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 点击筛选按钮 */
	private void filterGoods(WebView webView)
	{
		// 拼接业务逻辑
		String logicStr = "doTapByRI(\"J_Sift\");";

		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 确定筛选条件 */
	private void confirmFilter(WebView webView)
	{
		// 拼接业务逻辑
		String logicStr = "doClickByRI(\"J_SiftCommit\",2);";

		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 按销量优先排序 */
	private void orderBySellAmount(WebView webView)
	{
		// 拼接业务逻辑
		String logicStr = "doTapByParentCN(\"sort-tab\",\"sort\");";

		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 浏览商铺 */
	private void scanGoods(WebView webView, String titleStr)
	{
		// 拼接业务逻辑
		String logicStr = "var currentPage=doGetTextByCNByInner(\"currentPage\");" + "var totalSize=getSize(\"list-item\");"
				// +"localMethod.JI_showToast(totalSize);"
				// + "localMethod.JI_showToast(currentPage);"
				+ "doTapForScanGoodsByTitle(\"list-item\",\"d-title\",\"" + titleStr + "\",currentPage,totalSize);";

		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 关闭提示框 */
	private void alertHide(WebView webView)
	{
		// 拼接业务逻辑
		String logicStr = "doClickByCN(\"btn-hide\",2);";

		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 进入目标商铺 */
	private void enterShop(WebView webView, String url)
	{
		webView.loadUrl("https:" + url);
	}

	private void skuSelect(WebView webView)
	{
		// 拼接业务逻辑
		String logicStr = "doTapByCN02(); ";

		String completeJs = doAutoTest(logicStr);
		loadUrl(webView, completeJs);
	}

	/** 翻页 */
	private void getNextPage(WebView mWebView)
	{
		String logicStr = "doTapByCN(\"J_PageNext\"); ";

		String completeJs = doAutoTest(logicStr);
		loadUrl(mWebView, completeJs);
	}

	/* 暴露给JavaScript脚本调用的方法* */
	public class LocalMethod
	{
		Context mContext;
		private WA_Parameters parameter;

		public LocalMethod(Context c, WA_Parameters parameter)
		{
			this.mContext = c;
			this.parameter = parameter;
		}

		public WA_Parameters getParameter()
		{
			return parameter;
		}

		public void setParameter(WA_Parameters parameter)
		{
			this.parameter = parameter;
		}

		@JavascriptInterface
		public void showKeyboard()
		{
//			listWeb.requestFocus(View.FOCUS_DOWN);
//			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			showGuide();

		}


		@JavascriptInterface
		public void titleResult(String name,String count)
		{

			if (null == titleMap) {
				titleMap = new HashMap<String, Float>();
			}

			if (getWordCount(name)>2){
				titleMap.put(name,Float.parseFloat(count));
			}

		}

		@JavascriptInterface
		public void shopResult(String name,String jzl,String rc)
		{

			if (null == jzlMap) {
				jzlMap = new HashMap<String, Float>();
			}
			if (null == rcMap) {
				rcMap = new HashMap<String, Float>();
			}
			if (Float.parseFloat(jzl)>Float.parseFloat("0.2")){
				jzlMap.put(name, Float.parseFloat(jzl));
			}
			if (Float.parseFloat(rc)>Float.parseFloat("0.2")){
				rcMap.put(name, Float.parseFloat(rc));
			}



		}


		@JavascriptInterface
		public void getHotShopResult()
		{
			Log.e(TAG, "------------------------------------------------");
			sortMap(jzlMap,"---------------------zjl---------------------------"+shops[index]+"\n");
			Log.e(TAG, "*************************************************");
			sortMap(rcMap,"---------------------rc---------------------------"+shops[index]+"\n");
			mapClear();

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					biao1();
				}
			},randomtime);
//			listWeb.reload();
//			handlerJs("relativeTitle();",3000);

		}

		@JavascriptInterface
		public void getTitleResult()
		{
			Log.e(TAG, "--------------------title----------------------------");
			sortTitleMap(titleMap,"---------------------title---------------------------"+shops[index]+"\n");
			titleMap.clear();
			index++;
			randomtime =3000+(int)(Math.random()*2000);		//返回大于等于m小于m+n（不包括m+n）之间的随机数
			if (index<shops.length){
				goSearch(shops[index],randomtime);
			}

		}




		@JavascriptInterface
		public void foreachShangJia(String content)
		{
			final int nums = Integer.parseInt(content.trim());
			LogUtil.e("~~~~~~~~~~~~" + nums);
			templeIndex = 0;
			handlerJs("clickAllSelect();", 5000);


		}

//TODO
		@JavascriptInterface
		public void xiaJiaRecord(String xiajiaRecord)
		{
			if (null == xiajiaRecordList) {
				xiajiaRecordList = new ArrayList<String>();
				shangjiaIndex = 0;
			}
			final String[] split = xiajiaRecord.split("###");
			for (int i = 0; i < split.length; i++) {
				xiajiaRecordList.add(split[i]);
			}
			goEditDetailsUrl();

		}



		@JavascriptInterface
		public void xiajiaContinueOrNot()
		{
			SWITCH_DEFAULT_METHOD = Constant.XIAJIA_CONTINUE_OR_NOT;

		}


		@JavascriptInterface
		public void shangjiaItemRecord(String record)
		{
			shangjiaRecordStr = record;
			SWITCH_UPLOAD_METHOD = Constant.SHANGJIA_CONTINUE;
		}

		@JavascriptInterface
		public void xiajiaRecordOccur()
		{
            XIAJIA_STOP = true;
		}


		@JavascriptInterface
		public void shangjiaStop()
		{
            SHANGJIA_STOP = true;
		}

		@JavascriptInterface
		public void xiajiaClick()
		{
			LogUtil.e("JI_LOG:localClick!!!!!!");
			handlerJs("xiajia();");
		}


		@JavascriptInterface
		public void JI_showToast(String content)
		{
			Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
		}

		@JavascriptInterface
		public void JI_LOG(String content)
		{
//			Log.e(TAG, "JI_LOG: " + content);
			LogUtil.e(TAG, "JI_LOG: " + content);
//			Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
		}

		@JavascriptInterface
		public void JI_scrollView()
		{
			listWeb.scrollBy(0, 1800);
		}

		@JavascriptInterface
		public void JI_doGetNextPage(int randomTime)
		{
			doFlipAndScan(parameter, randomTime);
		}

		@JavascriptInterface
		public void JI_doScanCurrentPage(int randomTime)
		{

			doScanForLongTime(parameter, randomTime);
		}

		@JavascriptInterface
		public void JI_createLog(String infoStr) throws IOException
		{
			createLog(infoStr);
		}


		@JavascriptInterface
		public void getTargetIndex() throws IOException
		{
			LogUtil.e("------------getTargetIndex------------");
			handlerJs("operaSearch();");
		}
	}

	private void goEditDetailsUrl() {
//		String itemId = xiajiaRecordList.get(shangjiaIndex);
//		shangjiaRecordStr = shangjiaRecordStr+"###"+itemId.split("@@@")[0];
//		final String url = Constant.uploadUrl_CATID + itemId.split("@@@")[1] + Constant.uploadUrl_ITEMID + itemId.split("@@@")[0];
//
//		getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                SWITCH_UPLOAD_METHOD = Constant.EDIT_DETAIL;
//                wv_upload.loadUrl(url);
//            }
//        });
	}

	public int getWordCount(String s)
	{
		int length = 0;
		for(int i = 0; i < s.length(); i++)
		{
			int ascii = Character.codePointAt(s, i);
			if(ascii >= 0 && ascii <=255)
				length++;
			else
				length += 2;

		}
		return length;

	}

	private void sortMap(Map map,String str) {
		List<Map.Entry<String,Float>> list = new ArrayList<Map.Entry<String,Float>>(map.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<String,Float>>() {
            //升序排序
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });

		for(Map.Entry<String,Float> mapping:list){
			str = str + mapping.getKey()+"######"+mapping.getValue()+"\n";
        }
		putSp(str);
			Log.e("sortMap: ",str);
	}

	private void putSp(String str) {
		if (str.contains("------title")){
//			titleresultStr = titleresultStr + str;
//			Log.e("titlestr: ",titleresultStr);

			SharedPreferencesUtils.putValue(getActivity(), TAOBAO, shops[index]+"title", str);
		} else if (str.contains("------zjl")){
//			resultStr = resultStr + str;
//			SharedPreferencesUtils.putValue(getActivity(), TAOBAO, TAOBAOJZL, resultStr);
//			Log.e("resultStr: ",resultStr);
			SharedPreferencesUtils.putValue(getActivity(), TAOBAO, shops[index]+"zjl", str);
		} else if (str.contains("------rc")){
//			rcresultStr = rcresultStr + str;
//			SharedPreferencesUtils.putValue(getActivity(), TAOBAO, TAOBAORC, rcresultStr);
//			Log.e("rcresultStr: ",rcresultStr);
			SharedPreferencesUtils.putValue(getActivity(), TAOBAO, shops[index]+"rc", str);
		}
	}

	private void sortTitleMap(Map map,String str) {
		List<Map.Entry<String,Float>> list = new ArrayList<Map.Entry<String,Float>>(map.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<String,Float>>() {
            //升序排序
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });

		for(Map.Entry<String,Float> mapping:list){
			str = str + mapping.getKey()+"\n";
        }
		putSp(str);
			Log.e("sortMap: ",str);
	}

	public void mapClear()
	{

		if (null != jzlMap) {
			jzlMap.clear();
		}
		if (null != rcMap) {
			rcMap.clear();
		}
	}


	private void showGuide( ){
		new Thread( new Runnable( ) {
			@Override
			public void run() {
				try {
					Thread.sleep( 1000 );
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				// “旋转”的拼音
//				int[] keyCodeArray = new int[]{KeyEvent.KEYCODE_X,KeyEvent.KEYCODE_U,KeyEvent.KEYCODE_A,KeyEvent.KEYCODE_N,KeyEvent.KEYCODE_SPACE,KeyEvent.KEYCODE_Z,KeyEvent.KEYCODE_H,KeyEvent.KEYCODE_U,KeyEvent.KEYCODE_A,KeyEvent.KEYCODE_N};
				int[] keyCodeArray = new int[]{KeyEvent.KEYCODE_X,KeyEvent.KEYCODE_DEL};
				for( int keycode : keyCodeArray ){
					try {
						typeIn( keycode );
						Thread.sleep( 200 );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start( );
	}

	private void typeIn( final int KeyCode ){
		try {
			Instrumentation inst = new Instrumentation();
			inst.sendKeyDownUpSync( KeyCode );
		} catch (Exception e) {
			Log.e("Exception：", e.toString());
		}
	}


	public void initWebview(final WebView listWeb) {

		WebSettings webSetting = listWeb.getSettings();


		// 支持获取手势焦点
		listWeb.requestFocusFromTouch();
		listWeb.setHorizontalFadingEdgeEnabled(true);
		listWeb.setVerticalFadingEdgeEnabled(false);
		listWeb.setVerticalScrollBarEnabled(false);
		// 支持JS
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setDisplayZoomControls(true);
		webSetting.setLoadWithOverviewMode(true);
		// 支持插件
		webSetting.setPluginState(WebSettings.PluginState.ON);
		webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		// 自适应屏幕
		webSetting.setUseWideViewPort(true);
		// 支持缩放
		webSetting.setSupportZoom(true
		);//就是这个属性把我搞惨了，
		// 隐藏原声缩放控件
		// 支持内容重新布局
//		webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

		webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.supportMultipleWindows();
		webSetting.setSupportMultipleWindows(true);
		// 设置缓存模式
		webSetting.setDomStorageEnabled(true);
		webSetting.setDatabaseEnabled(true);
		webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSetting.setAppCacheEnabled(true);
		webSetting.setAppCachePath(listWeb.getContext().getCacheDir().getAbsolutePath());
		// 设置可访问文件
		webSetting.setAllowFileAccess(true);
		webSetting.setNeedInitialFocus(true);
		// 支持自定加载图片
		if (Build.VERSION.SDK_INT >= 19) {
			webSetting.setLoadsImagesAutomatically(true);
		} else {
			webSetting.setLoadsImagesAutomatically(false);
		}
		webSetting.setNeedInitialFocus(true);
		// 设定编码格式
		webSetting.setDefaultTextEncodingName("UTF-8");


		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
//支持js


		webSetting.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");

		webSetting.setBuiltInZoomControls(true);
		webSetting.setSupportZoom(true);
		webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


		if (listWeb instanceof MyWebView) {
			SWITCH_DEFAULT_METHOD = Constant.DEFAULT_LOAD_COMPLETE;
			listWeb.loadUrl(Constant.URL);
		}
		listWeb.setWebViewClient(new WA_MainFragment.MyListWebViewClient());
		mLocalMethod = new WA_YundaFragment.LocalMethod(getActivity(), parameter);
		listWeb.addJavascriptInterface(mLocalMethod, "localMethod");
//		webSetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
	}



	/** ListWebView加载完注入基本JS函数 */
	public class MyListWebViewClient extends WebViewClient
	{
		@Override
		public void onPageFinished(WebView view, String url)
		{
			view.loadUrl("javascript:" + injectJS);
			if (view == wv_upload) {
				LogUtil.e("wv_upload!!!!!!!!!!~~~~~~~");
				switch (SWITCH_UPLOAD_METHOD) {
					case Constant.SHANGJIA_CONTINUE:
						SWITCH_UPLOAD_METHOD = -1;
						if (!SHANGJIA_STOP){
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									handlerJs("clickAllSelect(\"" + shangjiaRecordStr + "\");", 2000, wv_upload);
								}
							});
						}
						break;



						//下面两方法作废
					case Constant.EDIT_DETAIL:
						SWITCH_UPLOAD_METHOD = Constant.EDIT_UPLOAD_COMPLETE;
						handlerJs("shangjiaNow();", 1000, wv_upload);
						break;
					case Constant.EDIT_UPLOAD_COMPLETE:
						shangjiaIndex++;
						if (shangjiaIndex < xiajiaRecordList.size()) {
							SWITCH_UPLOAD_METHOD = Constant.EDIT_DETAIL;
							goEditDetailsUrl();
						} else {
							SWITCH_UPLOAD_METHOD = -1;
						}
						break;
				}
			} else {
				switch (SWITCH_DEFAULT_METHOD) {
					case Constant.DEFAULT_LOAD_COMPLETE:
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								wv_upload.loadUrl(Constant.CANGKU_URL);
							}
						});
						break;
					case Constant.XIAJIA_CONTINUE_OR_NOT:
						if (FIRST_SHANGJIA) {
							FIRST_SHANGJIA = false;
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									wv_upload.loadUrl(Constant.CANGKU_URL);
									handlerJs("clickAllSelect(\"" + shangjiaRecordStr + "\");", 7000, wv_upload);
								}
							});
						}

						if (!XIAJIA_STOP) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									handlerJs("selectXiajia(\"" + shangjiaRecordStr + "\");", 3000);
								}
							});

						}
						break;
				}
				SWITCH_DEFAULT_METHOD = -1;

			}

			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
		}
	}
}
