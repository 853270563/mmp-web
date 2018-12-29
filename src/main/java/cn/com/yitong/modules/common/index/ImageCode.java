package cn.com.yitong.modules.common.index;

import cn.com.yitong.consts.NS;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Controller
public class ImageCode {

	private Logger logger = YTLog.getLogger(this.getClass());

	private final int width = 60;
	private final int height = 20;

	@RequestMapping("/common/ImageCode.do")
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		try {
			Session session = SecurityUtils.getSessionRequired();
			// 创建具有可访问图像数据缓冲区的Image
			BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();

			// 创建一个随机数生成器
			Random random = new Random();

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);

			// 创建字体，字体的大小应该根据图片的高度来定
			Font font = null;

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

			session.removeAttribute(NS.IMG_CODE);
			session.setAttribute(NS.IMG_CODE, randomCode.toString());
			logger.info("IMG_CODE:" + session.getAttribute(NS.IMG_CODE));

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

	private Font getFont() {
		Random s = new Random();
		int i = s.nextInt(10);
		if (i % 2 == 0 && i < 6) {
			return new Font("Times New Roman", Font.PLAIN, 19);
		} else if (i % 3 == 0) {
			return new Font("Fixedsys", Font.PLAIN, 18);
		} else if (i % 7 == 0) {
			return new Font("Times New Roman", Font.ITALIC, 17);
		} else {
			return new Font("Fixedsys", Font.ITALIC, 19);
		}
	}
}
