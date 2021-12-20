package com.skillbox.javapro21.api.response;

import com.skillbox.javapro21.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadResponse {

    private static Integer id = 0;
    private Long ownerId;
    private String fileName;
    private String relativeFilePath;
    private String rawFileURL;
    private String fileFormat;
    private Long bytes;
    private static final Constants.FileType fileType = Constants.FileType.IMAGE;
    private Long createdAt;

    public FileUploadResponse() {
        id++;
    }

    public FileUploadResponse(Long ownerId, String fileName, String relativeFilePath, String rawFileURL, String fileFormat, Long bytes, Long createdAt) {
        id++;
        this.ownerId = ownerId;
        this.fileName = fileName;
        this.relativeFilePath = relativeFilePath;
        this.rawFileURL = rawFileURL;
        this.fileFormat = fileFormat;
        this.bytes = bytes;
        this.createdAt = createdAt;
    }
//
//
//    public Integer getId() {
//        return id;
//    }
//
//    public Integer getOwnerId() {
//        return ownerId;
//    }
//
//    public void setOwnerId(Integer ownerId) {
//        this.ownerId = ownerId;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//
//    public String getRelativeFilePath() {
//        return relativeFilePath;
//    }
//
//    public void setRelativeFilePath(String relativeFilePath) {
//        this.relativeFilePath = relativeFilePath;
//    }
//
//    public String getRawFileURL() {
//        return rawFileURL;
//    }
//
//    public void setRawFileURL(String rawFileURL) {
//        this.rawFileURL = rawFileURL;
//    }
//
//    public String getFileFormat() {
//        return fileFormat;
//    }
//
//    public void setFileFormat(String fileFormat) {
//        this.fileFormat = fileFormat;
//    }
//
//    public Long getBytes() {
//        return bytes;
//    }
//
//    public void setBytes(Long bytes) {
//        this.bytes = bytes;
//    }
//
//    public FileType getFileType() {
//        return fileType;
//    }
//
//    public void setFileType(FileType fileType) {
//        this.fileType = fileType;
//    }
//
//    public Long getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Long createdAt) {
//        this.createdAt = createdAt;
//    }
}
