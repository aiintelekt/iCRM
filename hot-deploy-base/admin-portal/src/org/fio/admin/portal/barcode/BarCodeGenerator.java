package org.fio.admin.portal.barcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.fio.admin.portal.barcode.constants.BarCodeConstants.BarCodeType;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.ofbiz.base.util.UtilValidate;

/**
 * 
 * @author Mahendran T
 *
 */
public class BarCodeGenerator {
	
	public static BufferedImage generateBarCode(String barcodeValue) {
		BarcodeGenerator barcodeGenerator = FioBarCodeFactory.getInstance(BarCodeType.CODE_128);
		return generateBarCode(barcodeGenerator, barcodeValue);
	}
	public static BufferedImage generateBarCode(BarcodeGenerator barcodeGenerator, String barcodeValue) {
		try {
			barcodeGenerator = barcodeGenerator == null ? FioBarCodeFactory.getInstance(BarCodeType.CODE_128) : barcodeGenerator;
			if(barcodeGenerator != null && UtilValidate.isNotEmpty(barcodeValue)) {
			
			    BitmapCanvasProvider canvas = new BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0);
			    //BitmapCanvasProvider provider = new BitmapCanvasProvider(out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
			    final int dpi = 160;
			    if (barcodeGenerator instanceof AbstractBarcodeBean) {
			        AbstractBarcodeBean bean1 = (AbstractBarcodeBean) barcodeGenerator;
			        bean1.setModuleWidth(UnitConv.in2mm(2.8f / dpi));
			        bean1.doQuietZone(false);
			        bean1.generateBarcode(canvas, barcodeValue);
			      }
			      else {
			    	  barcodeGenerator.generateBarcode(canvas, barcodeValue);
			      }
			    canvas.finish();
			    return canvas.getBufferedImage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getImageBase64EncodeData(BufferedImage image, String format) {
		String imageString = null;
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    try {
	    	if(UtilValidate.isEmpty(format)) format = "png";
	        ImageIO.write(image, format, bos);
	        byte[] imageBytes = bos.toByteArray();

	        Base64.Encoder encoder = Base64.getEncoder();
	        imageString = encoder.encodeToString(imageBytes);

	        bos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return imageString;
	}
}
