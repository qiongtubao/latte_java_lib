package latte.lib.kv.tikv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import latte.lib.api.kv.KVClient;
import latte.lib.kv.tikv.impl.DefaultRawTikvClient;
import lombok.Getter;
import lombok.Setter;
import org.tikv.common.TiConfiguration;
import org.tikv.common.TiSession;
import org.tikv.raw.RawKVClient;

@Setter
@Getter
public class TikvClientInfo {
    String pdAddr;
    static public  enum TikvType {
        RAW("raw") {
            @Override
            TiSession createTiSession(String pdAddr, int timeout) {
                TiConfiguration conf = TiConfiguration.createRawDefault(pdAddr);
                conf.setApiVersion(TiConfiguration.ApiVersion.V2);
                conf.setEnableAtomicForCAS(true);
                //tikv.grpc.timeout_in_ms
                conf.setTimeout(timeout);
                TiSession session = TiSession.create(conf);
                return session;
            }

            @Override
            KVClient getClient(TiSession tiSession) {
                RawKVClient client = tiSession.createRawClient();
                return new DefaultRawTikvClient(client);
            }
        },
        CTX("ctx") {
            @Override
            TiSession createTiSession(String pdAddr, int timeout) {
                return null;
            }

            @Override
            KVClient getClient(TiSession session) {
                return null;
            }
        };
        String name;
        TikvType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
        abstract TiSession createTiSession(String pdAddr, int timeout);
        abstract KVClient getClient(TiSession session);
        @JsonCreator
        public static TikvType fromString(String name) {
            for (TikvType type : TikvType.values()) {
                if (type.name.equalsIgnoreCase(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid type name: " + name);
        }

        @JsonValue
        public String getName() {
            return name;
        }
    }

    TikvType type;

    int timeout;

}
