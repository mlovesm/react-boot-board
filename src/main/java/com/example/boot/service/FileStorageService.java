package com.example.boot.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.boot.exception.FileStorageException;
import com.example.boot.exception.MyFileNotFoundException;
import com.example.boot.file.FileStorageProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir() + calcPath(""))
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    
	//���� ���� �Լ�
	@SuppressWarnings("unused")
	private static String calcPath(String uploadPath) {
		Calendar cal = Calendar.getInstance();
		
		String order = "VOD";
		String yearPath = order + File.separator + cal.get(Calendar.YEAR);
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
		
		makeDir(uploadPath, order, yearPath, monthPath, datePath);
		
		log.info(datePath);
		
		return datePath;
	}//calcPath
	
	//���� ���� �Լ�
	private static void makeDir(String uploadPath, String... paths) {
		System.out.println(uploadPath + paths[paths.length -1]);
		if(new File(uploadPath + paths[paths.length -1]).exists()) {
			return;
		}//if
		
		for(String path : paths) {
			
			Path dirPath = Paths.get(uploadPath + path);
			
			try {
				Files.createDirectories(dirPath);
			} catch (IOException e) {
				e.printStackTrace();
				throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
			}
			
		}//for
		
	}//makeDir

    public String storeFile(MultipartFile file) { 
    	//calcPath("");
    	System.out.println("fileStorageLocation="+fileStorageLocation);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // ���� �̸��� �߸��� ���ڰ� ���ԵǾ� �ִ���
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // ��� ��ġ�� ���� ����(������ �̸����� ���� ���� ��ü)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            System.out.println(targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
