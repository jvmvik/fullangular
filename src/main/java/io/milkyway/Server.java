package io.milkyway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpMethod;

import java.util.function.Function;

/**
 * Simple web server built with Netty 4
 */
public class Server
{
  private final ServerInitializer initializer;
  private NioEventLoopGroup bossGroup;
  private NioEventLoopGroup workerGroup;
  private Channel ch;
  private int port;
  private ChannelFuture channelFuture;
  private ServerBootstrap b;
  private ChannelFuture serverBind;

  public Server()
  {
    initializer = new ServerInitializer();
    port = 8080;
  }

  /***
   * Register handler for get
   *
   * @param url url intercepted by the URL
   * @param function that must executed
   */
  public void get(String url, Function function) throws RouterException
  {
    initializer.serverHandler.router.add(url, HttpMethod.GET.name(), function);
  }

  /**
   * Register handler for post
   *
   * @param url
   * @param function
   * @throws RouterException
   */
  public void post(String url, Function function) throws RouterException
  {
    initializer.serverHandler.router.add(url, HttpMethod.POST.name(), function);
  }

  /***
   * Register handler for put
   *
   * @param url
   * @param function
   * @throws RouterException
   */
  public void put(String url, Function function) throws RouterException
  {
    initializer.serverHandler.router.add(url, HttpMethod.PUT.name(), function);
  }

  /***
   * Register handler for delete
   *
   * @param url
   * @param function
   * @throws RouterException
   */
  public void delete(String url, Function function) throws RouterException
  {
    initializer.serverHandler.router.add(url, HttpMethod.DELETE.name(), function);
  }

  /***
   * Add resources which contains static that must serve to the outside world
   *
   * @param path web static content location
   */
  //TODO rename to resource
  public void resource(String path)
  {
    initializer.serverHandler.serverStaticHandler.setWeb(path);
  }


  /***
   * Add socket handler for a topic
   *
   * @param topic name of topic used to bind websocket client/server
   * @param function executed on communication is established
   */
  public void wire(String topic, Function function)
  {
    //TODO Must return an object that can connected and disconnected
    //TODO implement this features
  }

  /***
   * Set port number
   *
   * @param port number (default: 8080)
   */
  public void port(int port)
  {
    this.port = port;
  }

  /**
   * Start server on specific port
   *
   * @param port number
   */
  public void start(int port)
  {
    port(port);
    start();
  }

  /***
   * Start server on port
   */
  public void start()
  {
    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup();
    try {
      b = new ServerBootstrap();
      b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(initializer);

      Log.info("Server is started on http://localhost:" + port);

      serverBind = b.bind(port);
      ch = serverBind.sync().channel();
      channelFuture = ch.closeFuture().sync();
    }
    catch(InterruptedException e)
    {
      Log.error(e.getMessage());
      e.printStackTrace();
    }
    finally
    {
      Log.info("Server shutdown...");
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
      Log.info("Bye now...");
    }
  }

  public void stop()
  {
    Log.info("Stopping server...");
    ch.disconnect();
    ch.close();
    Log.info("Server stopped...");
  }
}
