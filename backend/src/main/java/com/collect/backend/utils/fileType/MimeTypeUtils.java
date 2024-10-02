package com.collect.backend.utils.fileType;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.common.enums.FileTypeEnum;

import java.util.Objects;

/**
 * 文件类型工具类
 * 
 * @author ruoyi
 */
public class MimeTypeUtils
{
    public static String getSuffix(String fileName){  //获取后缀名，带点.
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static void main(String[] args) {
        System.out.println(getSuffix("qwe.qwe"));
    }
    public static String getFileNameByPath(String path){  //在文件路径中获取文件名+后缀名
        return path.substring(path.lastIndexOf("/")+1);
    }
    public static String getSuffixNoPoint(String fileName){  //获取文件后缀名不带点 .
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    public static String getPrefix(String fileName){  //获取文件名
    return fileName.substring(0,fileName.lastIndexOf("."));
}
    //图片类型
    public static final String[] IMAGE_EXTENSION = { "bmp", "gif", "jpg", "jpeg", "png" };
    //音频格式
    public static final String[] MEDIA_EXTENSION = { "mp3", "wav", "wma", "wmv", "mid" };
    //视频格式
    public static final String[] VIDEO_EXTENSION = {"mp4", "avi", "mov", "wmv", "asf", "navi", "3gp", "mkv", "f4v", "rmvb", "webm","flv","ts","m3u8"};
    //文档格式
    public static final  String[] DOCUMENT_EXTENSION = { "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt","css","vue","java","pdf","md"};
    //其他格式举例
    public static final String [] OTHER_EXTENSION = {"rar", "zip", "gz", "bz2","jar"}; //压缩文件
    //根据后缀返回文件类型
    public  static FileTypeEnum getFileTypeByFileSuffix(String suffix){
        if(StringUtils.isBlank(suffix)) return null;
        for (String video : VIDEO_EXTENSION) {
            if(video.equals(suffix)) return FileTypeEnum.MOVIE_TYPE;
        }
        for (String image : IMAGE_EXTENSION) {
            if(image.equals(suffix)) return FileTypeEnum.IMG_TYPE;
        }
        for (String document : DOCUMENT_EXTENSION) {
            if(document.equals(suffix)) return FileTypeEnum.DOCUMENT_TYPE;
        }
        for (String media : MEDIA_EXTENSION) {
            if(media.equals(suffix)) return FileTypeEnum.AUDIO_TYPE;
        }
        return FileTypeEnum.OTHER_TYPE;
    }

    public static String getFileDefaultByFileType(String suffix){  //根据文件类型返回默认图片，图片和视频不用
        FileTypeEnum fileTypeByFileSuffix = getFileTypeByFileSuffix(suffix);
        if(Objects.requireNonNull(fileTypeByFileSuffix).getType().equals(FileTypeEnum.MOVIE_TYPE.getType()) || fileTypeByFileSuffix.getType().equals(FileTypeEnum.IMG_TYPE.getType())){
            return null;  //null代表是缩略图即可
        }
        String defaultString = "default";
        if(suffix.equals("doc") || suffix.equals("docx")){
            return  defaultString + "/word-"+defaultString+".png";
        }
        if(suffix.equals("xls") || suffix.equals("xlsx")){
            return  defaultString + "/excel-"+defaultString+".png";
        }
        if(suffix.equals("ppt") || suffix.equals("pptx")){
            return  defaultString + "/ppt-"+defaultString+".png";
        }
        if(suffix.equals("txt")){
            return  defaultString + "/txt-"+defaultString+".png";
        }
        if(suffix.equals("md")){
            return  defaultString + "/markdown-"+defaultString+".png";
        }
        if(suffix.equals("pdf")){
            return  defaultString + "/pdf-"+defaultString+".png";
        }
        if(fileTypeByFileSuffix.getType().equals(FileTypeEnum.AUDIO_TYPE.getType())){
            return  defaultString + "/audio-"+defaultString+".png";
        }
        if(fileTypeByFileSuffix.getType().equals(FileTypeEnum.OTHER_TYPE.getType())){
            for (String other : OTHER_EXTENSION) {  //压缩文件
                if(other.equals(suffix)){
                    return  defaultString + "/density-"+defaultString+".png";
                }
            }
        }
        return defaultString + "/unknow-"+defaultString+".png";
    }



}
