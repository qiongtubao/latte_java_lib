package latte.lib.tikv.plugin;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Shell {
    static Map<JarResourceDir, String>  unzipDirs = new LinkedHashMap<>();
    static void deleteDir(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    public static void createFolder(File folder) {
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new RuntimeException("无法创建文件夹：" + folder.getAbsolutePath());
            }
        }

        File[] subFolders = folder.listFiles(); // 获取文件夹中的所有子文件夹

        if (subFolders != null) {
            for (File subFolder : subFolders) {
                if (subFolder.isDirectory()) {
                    createFolder(subFolder); // 递归创建子文件夹
                }
            }
        }
    }
    static String unzipJarResource(Class<?> glass, String dir) throws IOException {
        String jarPath = glass.getProtectionDomain().getCodeSource().getLocation().getPath();
        String jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1);
        JarResourceDir jarResourceDir = new JarResourceDir(jarPath, dir);
        String unzipDir = unzipDirs.get(jarResourceDir);
        if (unzipDir != null) return unzipDir;
        synchronized (unzipDirs) {
            unzipDir = unzipDirs.get(jarResourceDir);
            if (unzipDir != null) return unzipDir;
            try {
                JarFile jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();
                String resourceDir = "/tmp/" + jarName + "/" + dir ;
                Path folder = Paths.get(resourceDir);
                if (Files.isDirectory(folder)) {
                    deleteDir(folder.toFile());
                }
                createFolder(folder.toFile());
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        String entryName = entry.getName();
                        if (entryName.startsWith(dir)) { // 替换为你的资源文件夹路径
                            String fileName = entryName.substring(entryName.lastIndexOf("/") + 1);
                            File outputFile = new File(resourceDir + "/" + fileName);
                            InputStream inputStream = jarFile.getInputStream(entry);
                            FileOutputStream outputStream = new FileOutputStream(outputFile);
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            outputStream.close();
                            inputStream.close();
                        }
                    }
                }
                jarFile.close();
                unzipDirs.put(jarResourceDir, resourceDir);
                return resourceDir;
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }

    }
    static public String execResourceShellFile(Class<?> glass, String dir, String scriptName, String... command) throws Exception {
        URL resourceUrl = glass.getResource(  "/" + dir + "/" + scriptName);
        Path folderPath = null;
        if (resourceUrl != null) {
            String protocol = resourceUrl.getProtocol();
            if (protocol.equals("jar")) {
                String tmpDir = unzipJarResource(glass, dir);
                folderPath = Paths.get(tmpDir  + "/" + scriptName);
            } else if (protocol.equals("file")) {
                folderPath = Paths.get(resourceUrl.getPath());
            } else {
                throw new RuntimeException(String.format("%s resource path wrong", dir + "/" + scriptName));
            }
        }
        String[] newCommands = new String[command.length + 2];
        newCommands[0] = "sh";
        newCommands[1] = folderPath.toString();
        System.arraycopy(command, 0, newCommands, 2, command.length);
        // 执行命令
        ProcessBuilder processBuilder = new ProcessBuilder(newCommands);
        Process process = processBuilder.start();

        // 获取命令的输入流
        InputStream inputStream = process.getInputStream();

        // 读取输入流并打印输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // 等待命令执行完成
        process.waitFor();
        return sb.toString();
    }

}
