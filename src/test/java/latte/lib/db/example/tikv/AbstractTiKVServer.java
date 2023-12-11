package latte.lib.db.example.tikv;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractTiKVServer {
    public final String tikv_script_dir = "src/test/resources/db/tikv/";
    private void execAndWaitComplete(String command) throws InterruptedException, IOException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }

    public void startPd() throws Exception {
        startPd(true, 0);
    }

    /**
     *
     *
     */
    enum ServerStatus {
        UNKNOW,
        SUCCESS,
        Fail
    }
    List<String> fileFindStr(String filename, String str) throws Exception {
        Stream<String> lines = getFileStream(filename);
        return lines.filter(line -> {
            return line.contains(str);
        }).collect(Collectors.toList());
    }
    Stream<String> getFileStream(String fileName) throws Exception {
        File file = new File(tikv_script_dir + "pd.log");
        while(!file.exists()) {
            Thread.sleep(1000);
        }
        Path path = Paths.get(fileName);
        return Files.lines(path);
    }

    ServerStatus getPdServerStats() throws Exception {


        String logFile = tikv_script_dir + "pd.log";
        if (fileFindStr(logFile, "run server failed").size() > 0) {
            throw new RuntimeException("run server fail");
        }
        if (fileFindStr(logFile, "PD leader is ready to serve").size() > 0) {
            return ServerStatus.SUCCESS;
        }
        return ServerStatus.UNKNOW;
    }
    void waitPdStartSuccess(int count) throws Exception {
        int i = 0;
        while(i < count) {
            if(getPdServerStats().equals(ServerStatus.SUCCESS))  {
                return;
            }
            i++;
            Thread.sleep(3000);
        }
        throw new TimeoutException("start timeout");
    }
    public void startPd(boolean isClear, int count) throws Exception {
        if (isClear) execAndWaitComplete(tikv_script_dir+"clear_pd.sh");
        execAndWaitComplete(tikv_script_dir+"start_pd.sh");
        waitPdStartSuccess(5);
    }

    public void stopPd() throws IOException, InterruptedException {
        execAndWaitComplete(tikv_script_dir+"stop_pd.sh");
    }


    public final int TIKV_PORT_1 = 40160;
    public final String PD_ADDR = "127.0.0.1:22379";

    Map<Integer, Object> tiKvServers = new ConcurrentHashMap<>();


    public void startServer() throws Exception {
        startPd();
        //默认启动一个tikv-server
        startTiKVServer(TIKV_PORT_1);
    }

    @Before
    public void stopServer() throws IOException, InterruptedException {
        stopTiKVServers();
        stopPd();
    }

    public void clearTiKVServer(int port) throws IOException, InterruptedException {
        execAndWaitComplete(String.format(tikv_script_dir+"clear_tikv_server.sh %d", port));
    }

    public void startTiKVServer(int port) throws IOException, InterruptedException {
        startTiKVServer(port, true);
    }

    public void startTiKVServer(int port, boolean isClear) throws IOException, InterruptedException {
        if (tiKvServers.get(port) != null) {
            return;
        }
        if(isClear) clearTiKVServer(port);
        execAndWaitComplete(String.format(tikv_script_dir + "start_tikv_server.sh %d", port));
        tiKvServers.put(port, true);
    }
    public void stopTiKVServer(int port) throws IOException, InterruptedException {
        if (tiKvServers.get(port) == null) {
            return;
        }
        execAndWaitComplete(String.format(tikv_script_dir+"stop_tikv_server.sh %d", port));
        tiKvServers.remove(port);
    }

    public void stopTiKVServers() throws IOException, InterruptedException {
        Iterator<Integer> iter = tiKvServers.keySet().iterator();
        while (iter.hasNext()) {
            Integer element = iter.next();
            stopTiKVServer(element);
        }
    }

    public void restartTiKVServer(int port) throws IOException, InterruptedException {
        stopTiKVServer(port);
        startTiKVServer(port, false);
    }
}

