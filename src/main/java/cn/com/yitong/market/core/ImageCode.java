package cn.com.yitong.market.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.yitong.consts.AppConstants;
import cn.com.yitong.consts.SessConsts;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.tools.codec.Base64;
import cn.com.yitong.util.ParamUtil;
import cn.com.yitong.util.YTLog;

@Controller
public class ImageCode extends BaseControl {
	@Autowired
	@Qualifier("requestBuilder4db")
	IRequstBuilder requestBuilder;// 请求报文生成器
	@Autowired
	@Qualifier("responseParser4db")
	IResponseParser responseParser;// 响应报文解析器
	@Autowired
	@Qualifier("EBankConfParser4db")
	IEBankConfParser confParser;// 报文装载器
	private Logger logger = YTLog.getLogger(this.getClass());

	private final int width = 60;
	private final int height = 20;
	private final int codeCount = 4;

	private final int x = width / (codeCount + 1);
	private final int fontHeight = height - 2;
	private final int codeY = height - 4;

	final char[] codeSequence = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9' };
 
	@RequestMapping("/common/ImageCode.do")
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession();
			// 语言
			if(null == session.getAttribute(SessConsts.LANGUAGE) || "".equals(session.getAttribute(SessConsts.LANGUAGE))){
				session.setAttribute(SessConsts.LANGUAGE, AppConstants.ZH_HK);
			}
			// 登录成功标记
			String isLogin = session.getAttribute(SessConsts.ISLOGIN)+"";
			session.setAttribute(SessConsts.ISLOGIN, ParamUtil.isEmpty(isLogin) ? "false" : isLogin);
			// 网银登录编号
//			session.setAttribute(NS.IBS_LGN_ID, "0001");
			// 核心客户号
			String cifNo = session.getAttribute(SessConsts.CIF_NO)+"";
			session.setAttribute(SessConsts.CIF_NO, ParamUtil.isEmpty(cifNo) ? "0001" : cifNo);
			
			// 创建具有可访问图像数据缓冲区的Image
			BufferedImage buffImg = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();
			
			// 创建一个随机数生成器
			Random random = new Random();
			
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			
			// 创建字体，字体的大小应该根据图片的高度来定
			//Font font = new Font("Fixedsys", Font.ITALIC, 18);
			Font font=null;
			// 设置字体
			
			// 画边框
			g.setColor(Color.BLUE);
			g.drawRect(0, 0, width - 1, height - 1);
			int red = 0, green = 0, blue = 0;
			red = random.nextInt(110);
			green = random.nextInt(80);
			blue = random.nextInt(180);
			// 随机产生10条干扰线
			g.setColor(new Color(red, green, blue));
			for (int i = 0; i < 10; i++) {
				int x = random.nextInt(width);
				int y = random.nextInt(height);
				int x1 = random.nextInt(22);
				int y1 = random.nextInt(22);
				g.drawLine(x, y, x + x1, y + y1);
			}
			
			// randomCode 用于保存随机产生的验证码
			StringBuffer randomCode = new StringBuffer();
			
			
			// 随机产生4位数字的验证码
			for (int i = 0; i < 4; i++) {
				// 得到随机产生的验证码数字
				String strRand = String.valueOf(random.nextInt(10));
				font=getFont();
				// 产生随机的颜色分量来构造颜色值
				red = random.nextInt(110);
				green = random.nextInt(80);
				blue = random.nextInt(180);
				// 用随机产生的颜色将验证码绘制到图像中
				g.setFont(font);
				g.setColor(new Color(red, green, blue));
				g.drawString(strRand, 13 * i + 6, 16);
				
				//g.RotateTransform(12);
				
				randomCode.append(strRand);
			}

			
			session.removeAttribute(SessConsts.IMG_CODE);
			session.setAttribute(SessConsts.IMG_CODE, randomCode.toString());
			logger.info("IMG_CODE:"+session.getAttribute(SessConsts.IMG_CODE));
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);

			response.setContentType("image/png");
			ServletOutputStream sos = response.getOutputStream();
			ImageIO.write(buffImg, "png", sos);
			sos.flush();
			sos.close();
		} catch (Exception e) {
			logger.error("client stream close error");
		} 
	}
	
	private Font getFont()
	 {
	  //获得随机字体;
	//设置font :字体名称:Monotype Corsiva 华文彩云 方正舒体 华文行楷,隶书
	  
	  Random s=new Random();
	  int i=s.nextInt(10);
	  if(i%2==0 && i<6)
	  {
	   return new Font("Times New Roman", Font.PLAIN, 19);
	  }
	  else if(i%3==0)
	  {
	   return new Font("Fixedsys", Font.PLAIN, 18);
	  }
	  else if(i%7==0)
	  {
	   return new Font("Times New Roman", Font.ITALIC, 17);
	  }
	  else
	  {
	   return new Font("Fixedsys", Font.ITALIC, 19);
	  }
	 }

	@RequestMapping("/common/Base64ImageCode.do")
	@ResponseBody
	public Map<String, Object> execute2(HttpServletRequest request) {
		String transCode = "common/Base64ImageCode";
		Map<String, Object> rst = new HashMap<String, Object>();
		// 初始化数据总线
		IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
		// 检查报文定义
		if (!transPrev(ctx, transCode, rst)) {
			return rst;
		}
		boolean ok = false;
		try {
			// 创建具有可访问图像数据缓冲区的Image
			HttpSession session = request.getSession();
			// 语言
			if (null == session.getAttribute(SessConsts.LANGUAGE) || "".equals(session.getAttribute(SessConsts.LANGUAGE))) {
				session.setAttribute(SessConsts.LANGUAGE, AppConstants.ZH_HK);
			}
			// 登录成功标记
			String isLogin = session.getAttribute(SessConsts.ISLOGIN) + "";
			session.setAttribute(SessConsts.ISLOGIN, ParamUtil.isEmpty(isLogin) ? "false" : isLogin);
			// 网银登录编号
			//			session.setAttribute(NS.IBS_LGN_ID, "0001");
			// 核心客户号
			String cifNo = session.getAttribute(SessConsts.CIF_NO) + "";
			session.setAttribute(SessConsts.CIF_NO, ParamUtil.isEmpty(cifNo) ? "0001" : cifNo);

			// 创建具有可访问图像数据缓冲区的Image
			BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();

			// 创建一个随机数生成器
			Random random = new Random();

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);

			// 创建字体，字体的大小应该根据图片的高度来定
			//Font font = new Font("Fixedsys", Font.ITALIC, 18);
			Font font = null;
			// 设置字体

			// 画边框
			g.setColor(Color.BLUE);
			g.drawRect(0, 0, width - 1, height - 1);
			int red = 0, green = 0, blue = 0;
			red = random.nextInt(110);
			green = random.nextInt(80);
			blue = random.nextInt(180);
			// 随机产生10条干扰线
			g.setColor(new Color(red, green, blue));
			for (int i = 0; i < 10; i++) {
				int x = random.nextInt(width);
				int y = random.nextInt(height);
				int x1 = random.nextInt(22);
				int y1 = random.nextInt(22);
				g.drawLine(x, y, x + x1, y + y1);
			}

			// randomCode 用于保存随机产生的验证码
			StringBuffer randomCode = new StringBuffer();

			// 随机产生4位数字的验证码
			for (int i = 0; i < 4; i++) {
				// 得到随机产生的验证码数字
				String strRand = String.valueOf(random.nextInt(10));
				font = getFont();
				// 产生随机的颜色分量来构造颜色值
				red = random.nextInt(110);
				green = random.nextInt(80);
				blue = random.nextInt(180);
				// 用随机产生的颜色将验证码绘制到图像中
				g.setFont(font);
				g.setColor(new Color(red, green, blue));
				g.drawString(strRand, 13 * i + 6, 16);
				randomCode.append(strRand);
			}
			session.removeAttribute(SessConsts.IMG_CODE);
			session.setAttribute(SessConsts.IMG_CODE, randomCode.toString());
			logger.info("IMG_CODE:" + session.getAttribute(SessConsts.IMG_CODE));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(buffImg, "png", output);
			String encodeToString = Base64.encodeToString(output.toByteArray());
			output.close();
			rst.put("IMG_CODE", encodeToString);
			ok = true;
		} catch (Exception e) {
			logger.error("client stream close error");
		}
		transAfter(ctx, transCode, rst, ok);
		return rst;
	}

	/**
	 * 事务前置处理
	 *
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @return
	 */
	private boolean transPrev(IBusinessContext ctx, String transCode, Map<String, Object> rst) {
		return CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst);
	}

	/**
	 * 事务之后处理
	 *
	 * @param ctx
	 * @param transCode
	 * @param rst
	 * @param ok
	 */
	private void transAfter(IBusinessContext ctx, String transCode, Map<String, Object> rst, boolean ok) {
		CtxUtil.transAfter(ctx, transCode, rst, ok, responseParser, confParser);
	}
}
