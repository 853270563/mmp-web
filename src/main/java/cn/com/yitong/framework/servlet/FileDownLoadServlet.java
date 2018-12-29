package cn.com.yitong.framework.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.UriUtils;

import cn.com.yitong.consts.Properties;

/**
 * @ClassName: FileDownLoadServlet
 * @Description: 文件下载
 * @author: sunw@yitong.com.cn
 * @date: 2017年3月28日 上午9:14:14
 */
public class FileDownLoadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 文件路径
     */
    private String fileDir;

    /**
     * 服务路径
     */
    private String serverPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            serverPath = config.getInitParameter("serverPath");
            fileDir = Properties.getString("upload_files_path");
        } catch (Exception e) {
            logger.error("FileDownLoadServlet init param Exception:", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        getFile(req, resp);
    }

    public void getFile(HttpServletRequest req, HttpServletResponse resp) {
        String filepath = req.getRequestURI();
        int index = filepath.indexOf(serverPath);
        if (index > -1) {
            filepath = filepath.substring(index + serverPath.length() - 1);
        }
        try {
            filepath = UriUtils.decode(filepath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        //输入流初始化
        InputStream inputStream = null;
        //获取下载文件
        try {
            //获取文件
            File file = new File(fileDir + filepath);
            
            logger.info("文件路径：" + file.getAbsolutePath());
            System.out.println("文件路径：" + file.getAbsolutePath());
            
            if (!file.exists()) {
            	return;
            }
            //获取文件流
            inputStream = new FileInputStream(file);
            //写入响应流
            FileCopyUtils.copy(inputStream, resp.getOutputStream());
            
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            resp.setHeader("Content-Type", "application/octet-stream");
            resp.setContentLength((int) file.length());
            
            /*//-----------------设置响应头控制浏览器浏览器以图片的方式打开-------------------
            resp.setContentType(req.getContentType());
            //-----------------设置响应头控制浏览器不缓存图片数据-----------------
            resp.setDateHeader("expries", -1);
            resp.setHeader("Cache-Control", "no-cache");
            resp.setHeader("Pragma", "no-cache");*/
            return;
        } catch (Exception e) {
            logger.error("获取文件错误，路径：{}，错误：{}", filepath, e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
