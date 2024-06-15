package latte.lib.tikv.api;

import com.google.protobuf.ByteString;
import org.junit.Test;
import org.tikv.common.codec.Codec;
import org.tikv.common.codec.CodecDataInput;

public class HashCommandTest {

  private static final byte[] HASH_PREFIX = new byte[]{'h'};

  private static final byte[] HASH_FIELD_SEP = new byte[]{':'};


  @Test
  public void get_time() {
    System.out.println(1712728258334398675L >> 18);
  }
  @Test
  public void decode() {
//    ByteString rawKey = ByteString.copyFromUtf8("h2193015+\\377240413\\000\\000\\375:105194543_v");
//    CodecDataInput cdi = new CodecDataInput(rawKey.toByteArray());
//    byte[] decodeBytes;
//    cdi.skipBytes(HASH_PREFIX.length);
//    decodeBytes = Codec.BytesCodec.readBytes(cdi);
//    System.out.println(decodeBytes.toString());
  }

  @Test
  public void encode() {

  }

}
