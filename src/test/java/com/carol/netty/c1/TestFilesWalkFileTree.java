package com.carol.netty.c1;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 遍历文件目录
 */
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
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
