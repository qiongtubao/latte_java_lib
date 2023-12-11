package latte.lib.tikv.impl;

import latte.lib.tikv.KVClient;
import latte.lib.tikv.api.normal.NormalCommonCommand;
import latte.lib.tikv.api.normal.NormalHashCommand;
import latte.lib.tikv.api.normal.NormalStringCommand;
import latte.lib.tikv.api.transactional.TransactionAction;
import org.tikv.shade.com.google.protobuf.ByteString;

import java.util.List;
import java.util.Map;

public abstract class AbstractKVClient implements NormalStringCommand, NormalHashCommand, NormalCommonCommand, KVClient {



}
