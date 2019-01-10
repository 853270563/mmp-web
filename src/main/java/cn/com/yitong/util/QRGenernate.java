package cn.com.yitong.util;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 
 * 二维码生成/解析工具类
 * 
 * @author Alvin
 * 
 */
public class QRGenernate {
	
	
	
	
	 /**
	    * 生成二维码图片
	    * @param contents 字符串信息
	    * @param width 大小
	    * @param height
	    * @param imgPath 生成存放路径
	    */
		public byte[] Encode(String contents, int width, int height) {
			Hashtable<Object, Object> hints = new Hashtable<Object, Object>();

			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			byte[] result=null;
			try {
				ByteArrayOutputStream ba = new ByteArrayOutputStream();
				
				BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
						BarcodeFormat.QR_CODE, width, height, hints);

				MatrixToImageWriter.writeToStream(bitMatrix, "png", ba);
				
				result=ba.toByteArray();

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
	
   /**
    * 生成二维码图片
    * @param contents 字符串信息
    * @param width 大小
    * @param height
    * @param imgPath 生成存放路径
    */
	public void Encode(String contents, int width, int height, String imgPath) {
		Hashtable<Object, Object> hints = new Hashtable<Object, Object>();

		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
					BarcodeFormat.QR_CODE, width, height, hints);

			MatrixToImageWriter
					.writeToFile(bitMatrix, "png", new File(imgPath));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	 /**
	    * 生成二维码图片
	    * @param contents 字符串信息
	    * @param width 大小
	    * @param height
	    * @param imgPath 生成存放路径
	    */
		public void Encode(String contents, int width, int height, OutputStream out) {
			Hashtable<Object, Object> hints = new Hashtable<Object, Object>();

			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			try {
				BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
						BarcodeFormat.QR_CODE, width, height, hints);
				
				MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	
    /**
     * 解析二维码图片
     * @param imgPath
     * @return
     */
	public String Decode(String imgPath) {
		BufferedImage image = null;
		Result result = null;
		try {
			image = ImageIO.read(new File(imgPath));
			if (image == null) {
				System.out.println("the decode image may be not exit.");
			}
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			Hashtable<Object, Object> hints = new Hashtable<Object, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			result = new MultiFormatReader().decode(bitmap, hints);
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * 解析二维码图片
     * @param imgPath
     * @return
     */
	public String Decode(InputStream in) {
		/*if(file==null||!file.exists()){
			return null;
		}*/
		BufferedImage image = null;
		Result result = null;
		try {
			image = ImageIO.read(in);
			if (image == null) {
				System.out.println("the decode image may be not exit.");
			}
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			Hashtable<Object, Object> hints = new Hashtable<Object, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			result = new MultiFormatReader().decode(bitmap, hints);
			return result.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	


    public static void createPreviewImage(String srcFile, String destFile) {   
    	int IMAGE_SIZE=120;
            try {   
                File fi = new File(srcFile); // src   
                File fo = new File(destFile); // dest   
                BufferedImage bis = ImageIO.read(fi);   
      
                int w = bis.getWidth();   
                int h = bis.getHeight();   
                double scale = (double) w / h;   
                int nw = IMAGE_SIZE; // final int IMAGE_SIZE = 120;   
                int nh = (nw * h) / w;   
                if (nh > IMAGE_SIZE) {   
                    nh = IMAGE_SIZE;   
                    nw = (nh * w) / h;   
                }   
                double sx = (double) nw / w;   
                double sy = (double) nh / h;   
      
                java.awt.geom.AffineTransform transform=new java.awt.geom.AffineTransform();
                transform.setToScale(sx, sy);   
                AffineTransformOp ato = new AffineTransformOp(transform, null);   
                BufferedImage bid = new BufferedImage(nw, nh,   
                        BufferedImage.TYPE_3BYTE_BGR);   
                ato.filter(bis, bid);   
                ImageIO.write(bid, " jpeg ", fo);   
            } catch (Exception e) {   
                e.printStackTrace();   
                throw new RuntimeException(   
                        " Failed in create preview image. Error:  "  
                                + e.getMessage());   
            }   
        }  


	

	public static void main(String args[]) {
		// 生成的文件名
		String imgPath = "D:/33.jpg";
		String contents = "{\"PAY_ACCT_NO\":\"0000010511005611\",\"PAY_CURR\":\"000\",\"PAY_CURR_NME\":\"MOP\",\"RECV_ACCT_NO\":\"0000010511005891\",\"RECV_CURR\":\"000\",\"RECV_CURR_NME\":\"MOP\",\"PAY_AMT\":\"233\",\"PAY_TYP\":\"1\",\"TRANS_MEMO\":\"23\",\"IS_ORD\":\"0\",\"ORD_DEAL\":\"1\",\"ORD_DATE\":\"2013-05-25\",\"ORD_BGN_DATE\":\"2013-05-25\",\"ORD_END_DATE\":\"2013-05-25\",\"TRAN_FREQ\":\"M\",\"ORD_WEEK_DAY\":\"1\",\"ORD_DAY\":\"1\"}";
		int width = 300, height = 300;
		QRGenernate handler = new QRGenernate();
		// 对以上信息进行编码生成图片
		handler.Encode(contents, width, height, imgPath);

		System.out.println("SUCCESS to generate QRcode");

		// 对编码后的图片进行解析，确保编码后的文件没有问题
		String result = handler.Decode(imgPath);

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(result);
	}
}
