package cn.com.yitong.modules.hbbProvider;

import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.modules.hbbProvider.support.AbstractMsgSupport;
import cn.com.yitong.util.JsonFormat;
import cn.com.yitong.util.SpringContextHolder;
import cn.com.yitong.util.StringUtil;
import cn.com.yitong.util.YTLog;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 
 * @author shyt_huangqiang
 *
 */
public class ProviderHandler implements Runnable {

	private Logger logger = YTLog.getLogger(this.getClass());

	private Socket client;

	public ProviderHandler(Socket socket) {
		this.client = socket;
	}

	@Override
	public void run() {
		BufferedOutputStream out = null;
		BufferedInputStream in = null;

		try {
			out = new BufferedOutputStream(client.getOutputStream());
			in = new BufferedInputStream(client.getInputStream());
			String result = "";
			// 取前8位为报文长度
			byte[] buf = new byte[8];
			in.read(buf, 0, 8);
			String head = new String(buf, "UTF-8");
			if (StringUtil.isNotEmpty(head)) {
				logger.debug("RemoteSocketAddress:"
						+ client.getRemoteSocketAddress());
				logger.debug("head:" + head);
			}
			if (StringUtil.isEmpty(head) || 8 > head.length()) {
				result = "error content...";
				out.write(result.getBytes("UTF-8"));
				out.flush();
				return;
			}
			int msgLen = Integer.parseInt(head);
			logger.info("receve msg len :" + msgLen);

			byte[] msgContent = new byte[msgLen];
			ReadFromSocketInput(in, msgContent, 0, msgLen);
			String request = new String(msgContent, "UTF-8");
			logger.info("请求报文:" +JsonFormat.formatJson(request));

			if (StringUtil.isEmpty(request) || 8 >= request.length()) {
				logger.error("empty content...");
				result = "empty content...";
				out.write(result.getBytes("UTF-8"));
				out.flush();
				return;
			}

//			 String body = request.substring(8);
			JSONObject rcvMsgJson = JSONObject.fromObject(request);
			if (null == rcvMsgJson) {
				logger.error("json transconf failed!");
				result = "json transconf failed!";
				out.write(result.getBytes("UTF-8"));
				out.flush();
				return;
			}

			String transCode = rcvMsgJson.getJSONObject("head").getString("transCode");
			if (null == transCode || transCode == "") {
				logger.error("transCode is empty!");
				result = "transCode is empty!";
				out.write(result.getBytes("UTF-8"));
				out.flush();
				return;
			}
			String beanName = transCode + "Support";

			// 业务处理
			AbstractMsgSupport support = SpringContextHolder.getBean(beanName);
			BusinessContext ctx = new BusinessContext(
					IBusinessContext.PARAM_TYPE_MAP);
			ctx.setRequestEntry(rcvMsgJson);
			support.execute(ctx);

			result = (String) ctx.getResponseEntry();
			logger.info("响应报文: " + JsonFormat.formatJson(result));
			// 生成响应报文
			byte[] resp = result.getBytes("UTF-8");
			if (null != resp) {
				out.write(resp);
				out.flush();
			}
		} catch (Exception e) {
			logger.error("---交易发生异常---", e);
			String resp = "handler exception";
			logger.debug("响应报文: " + resp);
			try {
				if (null != resp) {
					out.write(resp.getBytes());
					out.flush();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != client) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				client = null;
			}
		}
	}

	/**
	 * 读取报文
	 *
	 * @param in
	 * @param readBuffer
	 * @param offset
	 * @param len
	 * @throws IOException 
	 */
	private void ReadFromSocketInput(BufferedInputStream in, byte[] readBuffer,
			int offset, int len) throws IOException {
		int realLen = in.read(readBuffer, offset, len);
		// logger.info("要读取的长度:" + len + ";实际读取的长度:" + realLen);
		if (realLen != len) {
			ReadFromSocketInput(in, readBuffer, offset + realLen, len - realLen);
		}
	}
}
