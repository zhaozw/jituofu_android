package cn.com.jizhangbao.base;

import cn.com.jizhangbao.R;

public final class C {
	public static final class JZB {
		public static final int TAG = R.string.app_name;
		public static final String channelId = "官方";
		public static final String versionName = "2.0.0";
		public static final String localKey = "JZB_ANDROID";
		public static final String rootdir = "/jituofu";
		public static final String feedbackDir = "/feedback";
		public static final String waitUploadFeedbackDir = "/wufd";
		public static final String feedbackFileName = "screenshot.png";
		
		public static final int camera = 0;
		public static final int gallery = 1;
	}

	public static final class API {
		public static final String host = "http://192.168.1.102";
		//public static final String host = "http://10.0.2.2";
		public static final String register = "/users/create";
		public static final String getuser = "/users/getInfo";
		public static final String login = "/users/login";
		public static final String logout = "/users/logout";
		public static final String update = "/users/update";
		public static final String gethelp = "/help/index";
		public static final String helpyes = "/help/yes";
		public static final String helpno = "/help/no";
		public static final String fileadd = "/files/add";
	}

	public static final class ERROR {
		public static final String networkTimeout = "网络请求超时,请重试";
		public static final String networkException = "网络请求异常,请重试";
		public static final String networkNone = "请连接网络";
		public static final String serverException = "服务器发生异常,请重试";
	}

	public static final class FEEDBACK {
		public static final String wechat = "公众账号:xiaodianjizhangbao";
		public static final String weibo = "@";
		public static final String qq = "QQ服务号:2421042542";
		public static final String email = "service@jizhangbao.com.cn";
		public static final String phone = "客服电话:1213213213213";
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
	}
}