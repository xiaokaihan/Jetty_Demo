package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用Java自带的URL和URLConnection类发送get和post请求，获取字符串回复。
 * 
 * @author ian
 * @version 1.0
 */
public class HttpRequestHelper {

	private static final Logger logger = LoggerFactory.getLogger(HttpRequestHelper.class);
	private static long lastRecordError = 0;
	private static long lastRecordInfo = 0;

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param token
	 *            请求中的token，以header parameter方式传输
	 * @param param
	 *            请求参数，格式形如： name1=value1&name2=value2
	 * @return URL所代表远程资源的响应结果,未获取到时，返回null
	 */
	public static String sendGet(String url, String token, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestProperty("authorization", token);
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (SocketTimeoutException e) {
			logError("Web response timeout: " + e.getMessage(), url, token, e);
		} catch (Exception e) {
			logError("Web response abnormal: " + e.getMessage(), url, token, e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求，获取回复。
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param token
	 *            请求中的token，以header parameter方式传输
	 * @param param
	 *            请求参数，格式形如： name1=value1&name2=value2
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String token, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性(Header参数)
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("authorization", token);
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (SocketTimeoutException e) {
			logError("Web response timeout: " + e.getMessage(), url, token, e);
		} catch (Exception e) {
			logError("Web response abnormal: " + e.getMessage(), url, token, e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		// 发送 GET 请求
		long start = System.nanoTime();
		String str = HttpRequestHelper.sendGet("http://192.168.7.135:2223/system/jwt/decoded", "token_233",
				"sctyID=3&intvl=1m&stTime=201702170900&enTime=");
		System.out.println(str);
		System.out.println("Time cost: " + (System.nanoTime() - start) / 1000000 + " ms.");

		// 发送 POST 请求
		/*String sr = HttpRequestHelper.sendPost("http://192.168.7.132:2222/streaming/sctyIntvlQuote/subscribe",
				"token_233", "sctyID=3&intvl=1m&stTime=201702170900&enTime=");
		System.out.println(sr);*/
	}

	private static void logError(String message, String url, String token, Exception e) {
		long current = System.currentTimeMillis();
		if (current - lastRecordError > 15000) {
			logger.warn(message + ", url: " + url + ", token: " + token, e);
			lastRecordError = current;
			lastRecordInfo = current;
		} else if (current - lastRecordInfo > 5000) {
			logger.info(split(message, url, token), e);
			lastRecordInfo = current;
		} else {
			logger.info(split(message, url, token));
		}
	}

	private static String split(String message, String url, String token) {
		return message + ", url:" + url.substring(0, Math.min(url.length(), 24)) + "...., token: ...."
				+ token.substring(Math.max(token.length() - 15, 0));
	}
}
