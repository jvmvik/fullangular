package io.milkyway;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.io.IOException;

/**
 * StringEncoder
 *
 * @author victor@milkyway.io
 * @date 4/16/14
 */
public class StringEncoder
{
  /*
  * Convert String to ByteBuf or reverse.
  */
  public static ByteBuf encode(String s)
  {
    return Unpooled.copiedBuffer(s, CharsetUtil.UTF_8);
  }

  public static String decode(ByteBuf byteBuf)
  {
    ByteBufInputStream stream = new ByteBufInputStream(byteBuf);
    try
    {
      return stream.readLine();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
