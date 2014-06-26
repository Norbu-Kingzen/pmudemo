package com.example.pmudemo.helper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import android.util.Log;

/**
 * Message upload helper class
 * 
 * @author weishijie
 *
 */
public class UploadHelper {

	private static final String TAG = "UploadHelper";
	/**
	 * Timeout
	 */
	private static final int TIME_OUT = 5 * 1000;
	/**
	 * Charset
	 */
	private static final String CHARSET = "utf-8";

	/**
	 * Upload file to server
	 * 本方法采用“混编格式”实现上传
	 * 混编格式，即混合多种资料格式并一次传送，当然非文字资料必须要编码为二进制字符串
	 * 参考：
	 * http://www.cnblogs.com/shanyou/archive/2013/06/07/3123155.html
	 * http://www.faqs.org/rfcs/rfc2388.html
	 * 
	 * @param file file need to upload
	 * @param RequestURL request url
	 * @return response string
	 */
	public static String uploadFile(File file, String requestURL)
	{
		String result = null;
		/**UUID(Universally Unique Identifier)全局唯一标识符,是指在一台机器上生成的数字，
		它保证对在同一时空(3240年)中的所有机器都是唯一的。UUID一般形如下例：
		550E8400-E29B-11D4-A716-446655440000*/
		// 边界标识 随机生成
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--";
		String LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";

		try
		{
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			// Allow input
			conn.setDoInput(true);
			// Allow output
			conn.setDoOutput(true);
			// Forbidden cache
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			// Set request header field
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);

			/**
			 * Upload only if file exists
			 */
			if (file != null)
			{
				// Get output stream
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				
				/** Initialize a StringBuffer instance to store some header info */
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key,只有这个key,才可以得到对应的文件
				 * filename是文件的名字(包含后缀名),比如:abc.mp3
				 */
				sb.append("Content-Disposition: form-data; name=\"mp3\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				// application/octet-stream, 即 .*（ 二进制流，不知道下载文件类型）
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				// Write the header info to output stream
				dos.write(sb.toString().getBytes());
				
				/** Initialize an input stream to read file content */
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				// Write file content into output stream
				while ((len = is.read(bytes)) != -1)
				{
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				
				/** Write end data to output stream */
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				
				/** Get response code. 200=successful */
				int res = conn.getResponseCode();
				Log.i(TAG, "response code:" + res);
				Log.i(TAG, "request success");
				// Initialize an input stream to read response date out
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1)
				{
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.i(TAG, "result : " + result);
			}else{
				return null;
			}
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
