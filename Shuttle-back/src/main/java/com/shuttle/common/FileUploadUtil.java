package com.shuttle.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.shuttle.common.exception.InvalidBase64Exception;
import com.shuttle.common.exception.NonExistantImageException;

public class FileUploadUtil {
	public final static String profilePictureUploadDir = "user-photos/";
	public final static String documentPictureUploadDir = "driver-documents/";
	
	public static void saveFile(String uploadDir, String fileName, String imageBase64) throws IOException, InvalidBase64Exception {
        Path uploadPath = Paths.get(uploadDir);
         
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
         
        if(imageBase64 == null) {
        	return;
        }
        byte[] imageByte;
        try {
        	imageByte = Base64.getDecoder().decode(imageBase64);
        }
        catch (IllegalArgumentException e) {
          	throw new InvalidBase64Exception();
		}

        try (OutputStream stream = new FileOutputStream(uploadDir + fileName + ".png")) {
            stream.write(imageByte);
        }
        catch(Exception e) {
        	throw new InvalidBase64Exception();
        }
    }

	public static String getImageBase64(String uploadDir,String pictureName) throws IOException, NonExistantImageException {
		File inputFile = new File(uploadDir + pictureName);
		
		byte[] fileContent;
		try {
			fileContent = Files.readAllBytes(Paths.get(inputFile.getPath()));
		} catch (IOException e) {
			throw new NonExistantImageException();
		}
        String base64 = Base64
          .getEncoder()
          .encodeToString(fileContent);

		return base64;
	}
	
	public static String getDefaultImageBase64(){
		try {
			return getImageBase64(profilePictureUploadDir, "default.png");
		} catch (IOException | NonExistantImageException e) {
			return null;
		}
	}

	public static void deleteFile(String uploadDir, String fileName) {
		 File pictureFile = new File(uploadDir + fileName);
		 pictureFile.delete();
	}
	
	public static int calculateImageSize(String imageBase64) {
		String encoding = System.getProperty("file.encoding");
		int imageSize;
		try {
			imageSize = imageBase64.getBytes(encoding).length;
		} catch (UnsupportedEncodingException e1) {
			imageSize = imageBase64.getBytes(StandardCharsets.UTF_16).length;
		}
		imageSize /= (1000 * 1000);
		return imageSize;
	}

}
