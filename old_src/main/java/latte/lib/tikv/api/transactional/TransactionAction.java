package latte.lib.tikv.api.transactional;

import latte.lib.tikv.api.TransactionalCommands;

public interface TransactionAction {
    void doTxnAction(TransactionalCommands cmd);
}
