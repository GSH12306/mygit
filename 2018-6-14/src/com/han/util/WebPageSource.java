package com.han.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebPageSource {

	public static void main(String[] args) {
		WebPageSource wps = new WebPageSource();
		System.out.println(wps.getSource("http://www.w3school.com"));
	}
	public String getSource(String strUrl) {

		URL url;

		int responsecode;

		HttpURLConnection urlConnection;

		BufferedReader reader;

		String result = "";

		try {

			// 生成一个URL对象，要获取源代码的网页地址为：http://www.sina.com.cn
			// String strUrl = "http://www.w3school.com.cn/js/js_howto.asp";
			url = new URL(strUrl);

			// 打开URL

			urlConnection = (HttpURLConnection) url.openConnection();

			// 获取服务器响应代码

			responsecode = urlConnection.getResponseCode();

			if (responsecode == 200) {

				// 得到输入流，即获得了网页的内容
				String line;
				File destFile = new File(
						"d://" + strUrl.substring(strUrl.lastIndexOf("/"), strUrl.lastIndexOf(".")) + ".html");
				reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "GBK"));
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\r\n");

				}
				// System.out.println(sb);
				int s = sb.indexOf("div id=\"tpn\">");
				int s2 = sb.indexOf("<div", s + 1);
				int e = sb.indexOf("<div id=\"bpn\">");
				result = sb.substring(s2, e).toString();
			}

			else {

				System.out.println("获取不到网页的源码，服务器响应代码为：" + responsecode);

			}

		}

		catch (Exception e) {

			System.out.println("获取不到网页的源码,出现异常：" + e);

		}

		return result;

	}

}