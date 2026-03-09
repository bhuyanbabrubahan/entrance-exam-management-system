package com.jee.publicapi.helper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

public class BarcodeGenerator {

    public static String generateBarcodeBase64(String text) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, 300, 100);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
