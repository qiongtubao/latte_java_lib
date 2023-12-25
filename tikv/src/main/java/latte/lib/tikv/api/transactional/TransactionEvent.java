package latte.lib.tikv.api.transactional;

import lombok.Getter;
import lombok.Setter;
import org.tikv.shade.com.google.protobuf.ByteString;


@Setter
@Getter
public class TransactionEvent {
    static public enum TransactionEventType {
        Cached,
        Locked,
        PUT,
        DELETE,
        Insert,
        CheckNotExist
    }

    TransactionEventType type;
    ByteString value = ByteString.EMPTY;

}
