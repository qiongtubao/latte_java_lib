package latte.lib.common.monitor;

import latte.lib.api.monitor.Monitor;
import latte.lib.api.monitor.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ComplexTransaction implements Transaction {
    List<Transaction> transactions = new ArrayList<>();
    public ComplexTransaction(Transaction... ts) {
        for (Transaction monitor : ts) {
            this.transactions.add(monitor);
        }
    }

    @Override
    public Transaction addTag(String s, String s1) {
        this.transactions.forEach(t-> {
            t.addTag(s, s1);
        });
        return this;
    }

    @Override
    public Transaction setSuccess() {
        this.transactions.forEach(t-> {
            t.setSuccess();
        });
        return this;
    }

    @Override
    public Transaction setFail(Throwable throwable) {
        this.transactions.forEach(t-> {
            t.setFail(throwable);
        });
        return this;
    }

    @Override
    public Transaction complete() {
        this.transactions.forEach(t-> {
            t.complete();
        });
        return this;
    }
}
