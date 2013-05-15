package org.neuro4j.web.console.controller.form;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadItem {
	private String filename;
	private String storage;
	
    private CommonsMultipartFile fileData;
    
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getStorage() {
		return storage;
	}
	public void setStorage(String storage) {
		this.storage = storage;
	}    
    
}
