package latte.lib.tikv.impl;

import latte.lib.tikv.api.TransactionalCommands;
import latte.lib.tikv.api.transactional.TransactionEvent;
import latte.lib.tikv.api.transactional.TransactionalCommonCommand;
import latte.lib.tikv.api.transactional.TransactionalHashCommand;
import latte.lib.tikv.api.transactional.TransactionalStringCommand;
import org.tikv.shade.com.google.protobuf.ByteString;

public abstract class AbstractTransactionCommands implements  TransactionalStringCommand, TransactionalHashCommand, TransactionalCommonCommand,TransactionalCommands {


}
