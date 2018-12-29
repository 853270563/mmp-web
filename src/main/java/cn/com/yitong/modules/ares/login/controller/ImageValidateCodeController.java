package cn.com.yitong.modules.ares.login.controller;

import cn.com.yitong.consts.NS;
import cn.com.yitong.core.session.Session;
import cn.com.yitong.core.util.SecurityUtils;
import cn.com.yitong.framework.base.IBusinessContext;
import cn.com.yitong.framework.core.bean.BaseControl;
import cn.com.yitong.framework.core.bean.BusinessContext;
import cn.com.yitong.framework.net.IEBankConfParser;
import cn.com.yitong.framework.net.IRequstBuilder;
import cn.com.yitong.framework.net.IResponseParser;
import cn.com.yitong.framework.util.CtxUtil;
import cn.com.yitong.tools.codec.Base64;
import cn.com.yitong.util.YTLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author zhanglong
 * @date 17/8/18
 */
@Controller
public class ImageValidateCodeController extends BaseControl {

    private Logger logger = YTLog.getLogger(this.getClass());

    @Autowired
    @Qualifier("requestBuilder4db")
    IRequstBuilder requestBuilder;// 请求报文生成器
    @Autowired
    @Qualifier("responseParser4db")
    IResponseParser responseParser;// 响应报文解析器
    @Autowired
    @Qualifier("EBankConfParser4db")
    IEBankConfParser confParser;// 报文装载器

    private final int width = 60;
    private final int height = 20;

    @RequestMapping("/ares/login/imageValidateCode.do")
    @ResponseBody
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response) {
        String transCode = "ares/login/imageValidateCode";
        Map<String, Object> rst = new HashMap<String, Object>();
        // 初始化数据总线
        IBusinessContext ctx = new BusinessContext(request, IBusinessContext.PARAM_TYPE_MAP);
        // 检查报文定义
        if (!CtxUtil.transPrev(ctx, transCode, json2MapParamCover, requestBuilder, confParser, rst)) {
            return rst;
        }
        boolean status = false;
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

            session.removeAttribute(NS.IMG_CODE);
            session.setAttribute(NS.IMG_CODE, randomCode.toString());
            logger.info("IMG_CODE:" + session.getAttribute(NS.IMG_CODE));

            ByteArrayOutputStream nyteOs = new ByteArrayOutputStream();
            ImageIO.write(buffImg, "png", nyteOs);
            String base64 = Base64.encodeToString(nyteOs.toByteArray());
            rst.put("imageBase64", "data:image/jpg;base64," + base64);
            status = true;
        } catch (Exception e) {
            logger.error("image validate code error", e);
        }
        CtxUtil.transAfter(ctx, transCode, rst, status, responseParser, confParser);
        return rst;
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
