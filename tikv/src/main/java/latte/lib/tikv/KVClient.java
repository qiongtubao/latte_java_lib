package latte.lib.tikv;

import latte.lib.tikv.api.CommonCommand;
import latte.lib.tikv.api.HashCommand;
import latte.lib.tikv.api.StringCommand;
import latte.lib.tikv.api.Transaction;

public interface KVClient extends StringCommand, HashCommand, CommonCommand, Transaction {

}
