package com.carol.nio.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 遍历文件目录
 */
@Slf4j
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
        String source = "D:\\Netty学习\\Netty教程源码资料 - 副本";
        String target = "D:\\Netty学习\\Netty教程源码资料 - 副本1";

        Files.walk(Paths.get(source)).forEach(dir->{
            String targetName = dir.toString().replace(source,target);//要操作的目录或文件名
            Path path = Paths.get(targetName);
            if (Files.isDirectory(dir)) {
                // 是一个目录
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (Files.isRegularFile(dir)){
                // 是一个普通文件
                try {
                    Files.copy(dir, path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }
    /**
     * 删除多级目录测试
     * @param args
     */
    public static void main3(String[] args) throws IOException {
        //无法直接删除
        // Files.delete(Paths.get("D:\\Netty学习\\Netty教程源码资料 - 副本"));
        Files.walkFileTree(Paths.get("D:\\Netty学习\\Netty教程源码资料 - 副本"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.debug("进入目录==>:"+dir);


                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug(file.toString());
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                log.debug("退出目录<==:"+dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }
    public static void main2(String[] args) throws IOException {
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\Netty学习\\Netty教程源码资料"),
                new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (file.toString().endsWith(".pdf")){
                                System.out.println("pdf===>"+file.getFileName());
                                fileCount.incrementAndGet();
                            }
                        return super.visitFile(file, attrs);
                    }
                });
        System.out.println("fileCount"+fileCount);
    }

    public static void m1(String[] args) throws IOException {
        // 计数器统计文件数量
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\Netty学习\\Netty教程源码资料"),
                new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        System.out.println("====>"+dir);
                        dirCount.incrementAndGet();
                        return super.preVisitDirectory(dir, attrs);
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        fileCount.incrementAndGet();
                        if (file.endsWith(".pdf")){
                            System.out.println("pdf===>"+file.getFileName());
                            fileCount.incrementAndGet();
                        }
                        return super.visitFile(file, attrs);
                    }
                });
        System.out.println("dir count"+dirCount);
        System.out.println("file count"+fileCount);
    }
}
