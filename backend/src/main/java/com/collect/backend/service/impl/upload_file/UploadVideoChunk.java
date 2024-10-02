package com.collect.backend.service.impl.upload_file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class UploadVideoChunk {
    @Value("${myfile.path}")
    private String filePath;

    /**
     * 源视频路径
     * 目标缩略图路径
     * 生成缩略图宽度
     * 生成缩略图高度
     * @param inputVideoPath
     * @param outputImagePath
     * @param width
     * @param height
     * @throws IOException
     * @throws InterruptedException
     */
    public  String generateVideoThumbnail(String inputVideoPath, String outputImagePath, int width, int height) throws IOException, InterruptedException {
            // FFmpeg命令，用于从视频中提取第一帧作为缩略图，并指定尺寸
            String command = String.format("ffmpeg -i %s -vf \"thumbnail\" -frames:v 1 -c:v mjpeg -vf \"scale=%d:%d\" %s", inputVideoPath, width, height, outputImagePath);

            // 使用ProcessBuilder执行FFmpeg命令
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取命令执行过程中的输出信息
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "fail";
            }
            return null;
        }

    /**
     *
     * @param inputImagePath
     * @param outputImagePath
     * @param width
     * @param height
     * @throws IOException
     * @throws InterruptedException
     */
    public  String generateImageThumbnail(String inputImagePath, String outputImagePath, int width, int height) throws IOException, InterruptedException {
            // FFmpeg命令，用于将图片转换为缩略图，并指定尺寸
            String command = String.format("ffmpeg -i %s -vf \"thumbnail\" -frames:v 1 -c:v mjpeg -vf \"scale=%d:%d\" %s", inputImagePath, width, height, outputImagePath);

            // 使用ProcessBuilder执行FFmpeg命令
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

//            // 读取命令执行过程中的输出信息
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {  //失败
                return "fail";
            }
            return null;
    }

    public static void main(String[] args) {
//        // 创建一个文件实例对象
        File video = new File("E:/软件杯/复活赛提交/02-产品介绍视频.mp4");
        if (!video.exists()) {
            video.mkdirs();
        }
        File m3u8 = new File("E:/collectUrl/2024-08-16/e189c4989f6544039583209654f29e16/" + "e189c4989f6544039583209654f29e16" + ".m3u8");
        try {
            m3u8.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //m3u8分片编码
        // 使用FFmpeg将视频分成多个小块

        //被注释的这个快一点
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-codec", "copy", "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
//        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", video.getPath(), "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        // 将标准错误流和标准输出流合并
        pb.redirectErrorStream(true);

        Process p = null;
        try {
            p = pb.start();

            // 处理FFmpeg的输出信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        try {
//            String inputImagePath = "C:\\Users\\Administrator\\Downloads\\test.jpg";
//            String outputImagePath = "C:\\Users\\Administrator\\Downloads\\new.jpg";
//            int width = 40; // 缩略图宽度
//            int height = 40; // 缩略图高度
//            generateImageThumbnail(inputImagePath, outputImagePath, width, height);
//            System.out.println("Thumbnail generated successfully!");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 源视频文件
     * 目标m3u8格式目录
     * 目标m3u8文件名
     * @param videoSourcePath
     * @param targetDirectory
     * @return
     */
    public String uploadVideoToM3U8(String videoSourcePath,String targetDirectory,String fileNamePrefix) {
        String m3u8File =  targetDirectory  + "/" + fileNamePrefix  + ".m3u8";
        File m3u8 = new File(filePath + targetDirectory);
        try {
            if(!m3u8.exists()){
                m3u8.mkdirs();
            }
            m3u8 = new File(filePath + m3u8File);
            m3u8.createNewFile();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //m3u8分片编码
        // 使用FFmpeg将视频分成多个小块

        //被注释的这个快一点
        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", videoSourcePath, "-codec", "copy", "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        //慢
//        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", videoSourcePath, "-force_key_frames", "expr:gte(t,n_forced*1)", "-strict", "-2", "-c:a", "aac","-c:v", "libx264", "-hls_time", "1", "-hls_list_size", "0", "-f", "hls", m3u8.getPath());
        // 将标准错误流和标准输出流合并
        pb.redirectErrorStream(true);

        Process p = null;
        try {
            p = pb.start();

            // 处理FFmpeg的输出信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return m3u8File;
    }
//        InputStream inputStream = p.getInputStream();
//        InputStream errorStream = p.getErrorStream();
//        InputStream combinedStream = new SequenceInputStream(inputStream, errorStream);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(combinedStream));
//        String line;
//
//        try {
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

}
