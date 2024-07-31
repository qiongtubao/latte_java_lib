package latte.lib.common.monitor;

import latte.lib.api.monitor.Monitor;
import latte.lib.api.monitor.Transaction;

import java.util.LinkedList;
import java.util.List;

public class ComplexMonitor implements Monitor {
    List<Monitor> monitors = new LinkedList<>();
    public ComplexMonitor(Monitor... ms) {
        for (Monitor monitor : ms) {
            this.monitors.add(monitor);
        }
    }
    @Override
    public Transaction getTransaction(String s) {
        return new ComplexTransaction(this.monitors.stream().map(monitor -> {
            return monitor.getTransaction(s);
        }).toArray(Transaction[]::new));
    }
}
