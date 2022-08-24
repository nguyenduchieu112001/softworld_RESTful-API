package com.softworld.app1.controller.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ImageProcessing {

	public static Code_Message uploadImage(MultipartFile file) throws IOException {
		String[] photoTail = file.getOriginalFilename().split("\\.");
		String[] imageTails = { "JPG", "GIF", "PNG" };
		for (String tail : imageTails) {
			if (photoTail[1].equalsIgnoreCase(tail)) {

				String Path_Directory = "src\\main\\resources\\static\\image";
				Files.copy(file.getInputStream(),
						Paths.get(Path_Directory + File.separator + file.getOriginalFilename()),
						StandardCopyOption.REPLACE_EXISTING);

				String fileDownloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/downloadFile/")
						.path(file.getOriginalFilename()).toUriString();
//				System.out.println(file.getName());
//				System.out.println(file.getOriginalFilename());
//				System.out.println(file.getContentType());
				return ErrorMessage.OK(fileDownloadUrl);
			}
		}
		return ErrorMessage.notAcceptable("The image does not have the same format as requested (PNG, GIF, JPG)");
	}

	public static Object downloadImage(String fileName) throws IOException {
		String Path_Directory = "src\\main\\resources\\static\\image\\";
		try {
			MultipartFile multipartFile = new MockMultipartFile(fileName,
					new FileInputStream(new File(Path_Directory + fileName)));
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment, file=\"" + fileName)
					.body(new ByteArrayResource(multipartFile.getBytes()));
		} catch (Exception e) {
			return ErrorMessage.notFount("Your image file does not exist");
		}

	}
}
