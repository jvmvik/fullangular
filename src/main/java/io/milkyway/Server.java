package io.milkyway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.objects.NativeArray;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Simple web server built with Netty 4
 */
public class Server
{
  private final ServerInitializer initializer;

  public Server()
  {
    initializer = new ServerInitializer();
  }

  /***
   * Rest http handler
   *
   * @param url url intercepted by the URL
   * @param function that must executed
   */
  public void rest(String url, Function function) throws RouterException
  {
    initializer.serverHandler.router.add(url, function);
  }

  /***
   * Get http handler
   *
   * @param url url intercepted by the URL
   * @param function that must executed
   */
  public void get(String url, Function function) throws RouterException
  {
    initializer.serverHandler.router.add(url, function);
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
   * Start server on port
   *
   * @param port number (default: 8080)
   */
  public void start(int port)
  {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(initializer);

      System.out.println("Server is started: http://localhost:" + port);

      Channel ch = b.bind(port).sync().channel();
      ch.closeFuture().sync();
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
}
