package latte.lib.tikv.api.transactional;

import latte.lib.tikv.api.CommonCommand;
import latte.lib.tikv.api.StoreType;

import java.util.Iterator;

public interface TransactionalCommonCommand extends CommonCommand,TransactionTiKVCommand {
    @Override
    default Iterator<String> scan(StoreType type, String start, String end) {
        return null;
    }

    @Override
    default void del(StoreType type, String key) {
        //delete range
    }
}
