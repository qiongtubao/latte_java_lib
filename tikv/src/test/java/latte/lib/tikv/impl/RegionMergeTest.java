package latte.lib.tikv.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import latte.lib.tikv.AbstractKVServer;

import latte.lib.tikv.plugin.impl.PdServer;
//import latte.lib.tikv.plugin.impl.PdServer.RegionInfo;
//import latte.lib.tikv.plugin.impl.PdServer.RegionInfos;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;



public class RegionMergeTest  {
//  @Setter
//  @Getter
//  @JsonIgnoreProperties({"epoch", "leader", "peers","cpu_usage","written_bytes","read_bytes", "written_keys", "read_keys", "approximate_size", "approximate_keys"})
//  public static class RegionInfo {
//    int id;
//    String start_key;
//    String end_key;
//
//  }
//  @Setter
//  @Getter
//  public static  class RegionInfos {
//    int count;
//    List<RegionInfo> regions;
//
//  }

  @Test
  public void set_test() throws Exception{
//    String result = getRegions();
//    String[] newCommands = new String[4];
//
//    newCommands[0] = "/home/dong/Documents/latte/latte_java_lib/tikv_launcher/target/classes/scripts/pd-ctl";
//    newCommands[1] = "-u";
//    newCommands[2] = "http://127.0.0.1:12379";
//    newCommands[3] = "region";
//
//// 执行命令
//    ProcessBuilder processBuilder = new ProcessBuilder(newCommands);
//    processBuilder.environment().put("PD_ADDR", "http://127.0.0.1:12379");
//    processBuilder.redirectErrorStream(true);
//    Process process = processBuilder.start();
//    process.getOutputStream().write("exit".getBytes());
//    process.getOutputStream().flush();
//
//    // 获取命令的输入流
//    InputStream inputStream = process.getInputStream();
//    InputStream errorStream = process.getErrorStream();
//    StringBuilder sb = new StringBuilder();
//    new Thread(() -> {
//      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//        String line;
//        while ((line = reader.readLine()) != null) {
//          sb.append(line);
//        }
//      } catch (IOException e) {
//        e.printStackTrace();
//      } finally {
//        try {
//          inputStream.close();
//        } catch (IOException e){
//          e.printStackTrace();
//        }
//
//      }
//    }).start();
//    // 读取输入流并打印输出
//    new Thread(() -> {
//      try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
//        String line;
//        while ((line = reader.readLine()) != null) {
//          System.out.println(line);
//        }
//      } catch (IOException e) {
//        e.printStackTrace();
//      } finally {
//        try {
//          errorStream.close();
//        } catch (IOException e){
//          e.printStackTrace();
//        }
//
//      }
//
//    }).start();
//    // 等待命令执行完成
//    process.waitFor();
//    process.destroy();

//    System.out.println(sb.toString());
//    PdServer pdServer = new PdServer(12379);
//    RegionInfos infos = pdServer.getRegions();
//    System.out.println(infos.getCount());
//    for(int i = 0; i < infos.getCount(); i++) {
//      RegionInfo info1 = infos.getRegions().get(i);
//      for(int j = i; j < infos.getCount(); j++) {
//        RegionInfo info2 = infos.getRegions().get(j);
//        if (info1.getStart_key().equals(info2.getEnd_key())
//            || info1.getEnd_key().equals(info2.getStart_key())) {
//          System.out.println(info1.getId() + " + " + info2.getId());
//          if (pdServer.mergeRegion(info1.getId(), info2.getId())){
//             return;
//          }
//        }
//      }
//    }
//  }
//
//  @Test
//  public void set_test2() throws Exception{
//    Process  process = Runtime.getRuntime().exec("/home/dong/Documents/latte/latte_java_lib/tikv_launcher/target/classes/scripts/pd-ctl -u http://127.0.0.1:12379 region");
//    // 获取命令的输入流
//    InputStream inputStream = process.getInputStream();
//
//    // 读取输入流并打印输出
//    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//    StringBuilder sb = new StringBuilder();
//    String line;
//    while ((line = reader.readLine()) != null) {
//      sb.append(line);
//    }
//    process.waitFor();
//    System.out.println(sb.toString());
//
  }
}
