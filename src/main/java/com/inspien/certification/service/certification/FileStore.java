package com.inspien.certification.service.certification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@Slf4j
public class FileStore {
  @Value("${file.dir}")
  public String fileDir;

  public String getFullPath(String fileName) {
	return fileDir + fileName;
  }

  /*
		파일 저장 메소드
	 */
  public String storeFile(MultipartFile multipartFile) throws IOException {
	String storeFileName = createStoreFileName(multipartFile.getOriginalFilename());
	multipartFile.transferTo(new File(fileDir+storeFileName));
	return storeFileName;
  }

  public String createStoreFileName(String orginalFileName) {
	String uuid = UUID.randomUUID().toString();
	return uuid + "." + extract(orginalFileName);
  }

  public String extract(String orginalFileName) {
	int pos = orginalFileName.lastIndexOf(".");
	return  orginalFileName.substring(pos+1);
  }

  public void deleteFile(String path) {
	String fullPath = getFullPath(path);
	try{
	  Files.delete(Path.of(fullPath));
	  log.info("파일 삭제 완료! path : " + fullPath);
	}catch (Exception e) {
	  log.info("파일이 존재하지 않습니다 PATH : " + fullPath);
	}
  }

}
