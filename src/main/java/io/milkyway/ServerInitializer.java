package io.milkyway;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel>
{

  final ServerHandler serverHandler;

  public ServerInitializer()
  {
    serverHandler = new ServerHandler();
  }

  @Override
  public void initChannel(SocketChannel ch) throws Exception {
    // Create a default pipeline implementation.
    ChannelPipeline p = ch.pipeline();

    // Uncomment the following line if you want HTTPS
    //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
    //engine.setUseClientMode(false);
    //p.addLast("ssl", new SslHandler(engine));

    p.addLast("decoder", new HttpRequestDecoder());
    // Uncomment the following line if you don't want to handle HttpChunks.
    //p.addLast("aggregator", new HttpObjectAggregator(1048576));
    p.addLast("encoder", new HttpResponseEncoder());
    // Remove the following line if you don't want automatic content compression.
    //p.addLast("deflater", new HttpContentCompressor());
    p.addLast("handler", serverHandler);
  }
}
