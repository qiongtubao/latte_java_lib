package latte.lib.tikv.api;

import latte.lib.tikv.api.transactional.TransactionAction;

public interface Transaction {
    void exec(TransactionAction action);

}
