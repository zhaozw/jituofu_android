package com.jituofu.base;

import com.jituofu.R;

/**
 * 常量类
 * 
 * @author zhuqi
 * 
 */
public final class C {
	public static final class DIRS {
		public static final String rootdir = "/jituofu";
		public static final String productDir = "/product";
		public static final String productFileName = "p.png";// 临时的上传商品的文件名
		public static final String feedbackDir = "/feedback";
		public static final String waitUploadFeedbackDir = "/wufd";// 存储等待上传的截图文件
		public static final String feedbackFileName = "screenshot.png";// 临时的截图文件名
	    public static final String userInfoFileName = "usr";
	    public static final String userIdFileName = "uid";
	    public static final String userCookieFileName = "uck";
	    public static final String feedbackCacheFileName = "fd";
	    public static final String storeSettingsCacheFileName = "ss";
	    public static final String apksDirName = "/apks";//APK升级包目前
	    public static final String tipUpdate = "tu";//是否有提示升级
	    public static final String tipPL = "pl";//是否有提示过评论
	}

	public static final class COMMON {
		public static final String isLaunch = "isLaunch";//是否第一次运行
		public static final int TAG = R.string.app_name;
		public static final String channelId = "官方";
		public static final String versionName = "2.0.0";
		public static final String localKey = "JITUOFU.COM";// 必须与服务端的localKey对应
		public static final int camera = 10;// 从相机上传图片
		public static final int gallery = 11;// 从图库片上传图片
		public static final String productSubmit = "productSubmit";
		public static final String productEdit = "productEdit";
		public static final String warehouse = "warehouse";
		public static final String cashier = "cashier";
		public static final String aboutUrl = "http://jituofu.com/about";
	}

	public static final class API {
		// public static final String host = "http://10.0.2.2";
		public static final String host = "http://115.29.39.106:1111";//后面没有反斜线
		public static final String register = "/users/create";
		public static final String getuser = "/users/getInfo";
		public static final String login = "/users/login";
		public static final String logout = "/users/logout";
		public static final String update = "/users/update";
		public static final String gethelp = "/help/index";
		public static final String helpyes = "/help/yes";
		public static final String helpno = "/help/no";
		public static final String fileadd = "/files/add";
		public static final String forgotpw = "/users/forgot";
		public static final String feedbackcreate = "/feedback/create";
		public static final String gettypes = "/types/index";
		public static final String typeadd = "/types/create";
		public static final String typesdeletepmc = "/types/deletepmc";
		public static final String typesdeletepcp = "/types/deletepcp";
		public static final String typesupdate = "/types/update";
		public static final String typesdeletecp = "/types/deletecp";
		public static final String typesdeletemp = "/types/deletemp";
		public static final String productcreate = "/products/create";
		public static final String productquery = "/products/query";
		public static final String productupdate = "/products/update";
		public static final String productsdelete = "/products/delete";
		public static final String productsearch = "/products/search";
		public static final String parenttypedetail = "/types/queryparentdetail";
		public static final String productquerybytype = "/products/querybytype";
		public static final String cashiercreate = "/cashier/create";
		public static final String salesreport = "/salesReport/index";
		public static final String returnsale = "/salesReport/returnsale";
		public static final String storesettingsget = "/storeSettings/get";
		public static final String storesettingsupdate = "/storeSettings/update";
		public static final String softwareupdate = "/softwareVersion/get";
		public static final String updatelsid = "/users/updatelsid";//更新最后登录时间
		public static final String noticeget = "/notice/index";//
	}

	public static final class ERROR {
		public static final String networkTimeout = "网络请求超时,请重试";
		public static final String networkException = "网络请求异常,请重试";
		public static final String networkNone = "请连接网络";
		public static final String serverException = "服务器发生异常,请重试";
	}

	public static final class TASK {
		public static final int register = 1001;
		public static final int getuser = 1002;
		public static final int login = 1003;
		public static final int logout = 1004;
		public static final int update = 1005;
		public static final int gethelp = 1006;
		public static final int helpyes = 1007;
		public static final int helpno = 1008;
		public static final int fileadd = 1009;
		public static final int forgotpw = 1010;
		public static final int feedbackcreate = 1011;
		public static final int gettypes = 1012;
		public static final int typeadd = 1013;
		public static final int typesdeletepmc = 1014;
		public static final int typesdeletepcp = 1015;
		public static final int typesupdate = 1016;
		public static final int typesdeletecp = 1017;
		public static final int typesdeletemp = 1018;
		public static final int productcreate = 1019;
		public static final int productquery = 1020;
		public static final int productupdate = 1021;
		public static final int productsdelete = 1022;
		public static final int productsearch = 1023;
		public static final int parenttypedetail = 1024;
		public static final int productquerybytype = 1025;
		public static final int cashiercreate = 1026;
		public static final int salesreport = 1027;
		public static final int salesreportlr = 1028;
		public static final int salesreportcb = 1028;
		public static final int returnsale = 1029;
		public static final int salesreporttypelist = 1030;
		public static final int storesettingsget = 1031;
		public static final int storesettingsupdate = 1032;
		public static final int softwareupdate = 1033;
		public static final int updatelsid = 1034;
		public static final int noticeget = 1035;
	}
	
	public static final class PASSWORDLENGTH{
		public static final int MIN = 6;
		public static final int MAX = 50;
	}
	
	public static final class USERNAMELENGTH{
		public static final int MIN = 2;
		public static final int MAX = 20;
	}
}
