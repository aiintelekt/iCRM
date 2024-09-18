package org.fio.admin.portal.barcode;

import org.fio.admin.portal.barcode.constants.BarCodeConstants.BarCodeType;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBCBean;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMailBean;
import org.krysalis.barcode4j.impl.int2of5.ITF14Bean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.postnet.POSTNETBean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;

/**
 * 
 * @author Mahendran T
 *
 */
public class FioBarCodeFactory {
	private FioBarCodeFactory() {}
	
	public static AbstractBarcodeBean getInstance(String barcodeType) {
		if(barcodeType == null)
			return null;
		if(barcodeType.equalsIgnoreCase(BarCodeType.INTER_LEAVED_2OF5)){
			return new Interleaved2Of5Bean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.ITF_14)){
			return new ITF14Bean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.CODE_39)){
			return new Code39Bean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.CODE_128)){
			return new Code128Bean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.CODAEBAR)){
			return new CodabarBean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.UPC_A)){
			return new UPCABean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.UPC_E)){
			return new UPCEBean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.EAN_13)){
			return new EAN13Bean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.EAN_8)){
			return new EAN8Bean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.POSTNET)){
			return new POSTNETBean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.RMCB)){
			return new RoyalMailCBCBean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.USPSIMAIL)){
			return new USPSIntelligentMailBean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.PDF_417)){
			return new PDF417Bean();
		} else if(barcodeType.equalsIgnoreCase(BarCodeType.DATA_MATRIX)){
			return new DataMatrixBean();
		}
		return null;
	}
}
