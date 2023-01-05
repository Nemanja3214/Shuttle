package com.shuttle.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.shuttle.common.exception.InvalidBase64Exception;

public class FileUploadUtil {
	public final static String profilePictureUploadDir = "user-photos/";
	
	public static void saveFile(String uploadDir, String fileName, String imageBase64) throws IOException , InvalidBase64Exception{
        Path uploadPath = Paths.get(uploadDir);
         
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
         
        BufferedImage image = null;
        byte[] imageByte = Base64.getDecoder().decode(imageBase64);
        image = ImageIO.read(new ByteArrayInputStream(imageByte));
        
        File outputfile = new File(uploadDir + fileName);
        if(image == null) {
        	throw new InvalidBase64Exception();
        }
        ImageIO.write(image, "png", outputfile);
    }

	public static String getImageBase64(String uploadDir,String pictureName) throws IOException {
		File inputFile = new File(uploadDir + pictureName);
		
		byte[] fileContent;
		try {
			fileContent = Files.readAllBytes(Paths.get(inputFile.getPath()));
		} catch (IOException e) {
			return getDefaultImageBase64();
		}
        String base64 = Base64
          .getEncoder()
          .encodeToString(fileContent);

		return base64;
	}
	
	public static String getDefaultImageBase64() throws IOException {
		return getImageBase64(profilePictureUploadDir, "default.png");
	}

}
