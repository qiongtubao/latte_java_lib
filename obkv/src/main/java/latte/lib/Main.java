package latte.lib;

// 按两次 Shift 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
public class Main {
    public static void main(String[] args) throws Exception {
        KVClient kvClient = new KVClient();
        kvClient.initial();
        kvClient.put("key", "v0");
        System.out.println(kvClient.get("key"));

    }
}