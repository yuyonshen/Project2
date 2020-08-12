package com.bit.java_image_server.dao;

public class Image {
    private int imageId;
    private String imageName;
    private int size;
    private String uploadTime;
    private String md5;
    private String contentType;
    private String path;

    public Image() {
    }

    public Image(String imageName, int size,
                 String uploadTime, String md5,
                 String contentType, String path) {
        this.imageName = imageName;
        this.size = size;
        this.uploadTime = uploadTime;
        this.md5 = md5;
        this.contentType = contentType;
        this.path = path;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", imageName='" + imageName + '\'' +
                ", size=" + size +
                ", uploadTime='" + uploadTime + '\'' +
                ", md5='" + md5 + '\'' +
                ", contentType='" + contentType + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
