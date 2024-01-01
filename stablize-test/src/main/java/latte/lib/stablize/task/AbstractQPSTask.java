package latte.lib.stablize.task;

public class AbstractQPSTask extends AbstractTask {
    int maxQps;
    long interval;
    long lastRequestTime;
    int tokens;

    public AbstractQPSTask(int maxQps) {
        this.maxQps = maxQps;
        this.interval = 1000/maxQps;
        this.lastRequestTime = System.currentTimeMillis();
        this.tokens = maxQps;
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRequestTime;
        lastRequestTime = currentTime;

        // 计算应该生成的令牌数量
        int generatedTokens = (int) (elapsedTime / interval);

        // 重新计算当前可用的令牌数量
        tokens = Math.min(tokens + generatedTokens, maxQps);

        // 判断是否有足够的令牌可用
        if (tokens > 0) {
            tokens--;
            return true;
        } else {
            return false;
        }
    }
}
