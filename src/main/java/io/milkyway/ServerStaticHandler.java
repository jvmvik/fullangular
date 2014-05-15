package io.milkyway;

import io.milkyway.utils.MimeTypeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author victor@milkyway.io
 * @date 4/8/14
 */
public class ServerStaticHandler
{
  public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
  public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
  public static final int HTTP_CACHE_SECONDS = 60;
  private static final String INDEX_PAGE = "index.html";

  private Path resource;

  public ServerStaticHandler()
  {
  }

  //TODO Enable cache or not if debug mode

  /***
   * Set static web content location
   * @param path
   */
  public void setWeb(String path)
  {
    resource = Paths.get(path);

    if(!Files.isDirectory(resource))
      Log.error("Resource: " + resource.toAbsolutePath() + " does not exist !");

    Log.info("Set resource to: " + resource.toAbsolutePath());
  }

  public void messageReceived(ChannelHandlerContext ctx, HttpRequest request)
      throws Exception
  {
    if(!request.getDecoderResult().isSuccess())
    {
      sendError(ctx, BAD_REQUEST);
      return;
    }

    if(request.getMethod() != GET)
    {
      sendError(ctx, METHOD_NOT_ALLOWED);
      return;
    }

    final String uri = request.getUri();
    final String path = sanitizeUri(uri);
    if(path == null)
    {
      sendError(ctx, FORBIDDEN);
      return;
    }

    if(resource == null)
    {
      sendError(ctx, NOT_FOUND);
      return;
    }

    Path file = resource.resolve(path);
    if(Files.isHidden(file) || !Files.exists(file))
    {
      sendError(ctx, NOT_FOUND);
      return;
    }

    // Show directory content
    if(Files.isDirectory(file))
    {
      if(uri.endsWith("/"))
      {
        if(Files.isReadable(file.resolve(INDEX_PAGE)))
        {
          file = file.resolve(INDEX_PAGE);
        }
        else
          sendListing(ctx, file);
      }
      else
      {
        sendRedirect(ctx, '/' + uri + '/');
        return;
      }
    }

    if(!Files.isRegularFile(file))
    {
      sendError(ctx, FORBIDDEN);
      return;
    }

    // Cache Validation
      /*String ifModifiedSince = request.headers().get(IF_MODIFIED_SINCE);
      if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

        // Only compare up to the second because the datetime format we send to the client
        // does not have milliseconds
        long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
        long fileLastModifiedSeconds = file.lastModified() / 1000;
        if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
          sendNotModified(ctx);
          return;
        }
      }*/

    RandomAccessFile raf;
    try
    {
      raf = new RandomAccessFile(file.toFile(), "r");
    }
    catch(FileNotFoundException fnfe)
    {
      sendError(ctx, NOT_FOUND);
      return;
    }
    long fileLength = raf.length();

    HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
    setContentLength(response, fileLength);
    setContentTypeHeader(response, file);
    setDateAndCacheHeaders(response, file);
    if(isKeepAlive(request))
    {
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }

    // Write the initial line and the header.
    ctx.write(response);

    // Write the content.
    ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());

    // Write the end marker
    ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

    // Decide whether to close the connection or not.
    if(!isKeepAlive(request))
    {
      // Close the connection when the whole content is written out.
      lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    }
  }

  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
  {
    cause.printStackTrace();
    if(ctx.channel().isActive())
    {
      sendError(ctx, INTERNAL_SERVER_ERROR);
    }
  }

  private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

  private static String sanitizeUri(String uri)
  {
    // Decode the path.
    try
    {
      uri = URLDecoder.decode(uri, "UTF-8");
    }
    catch(UnsupportedEncodingException e)
    {
      try
      {
        uri = URLDecoder.decode(uri, "ISO-8859-1");
      }
      catch(UnsupportedEncodingException e1)
      {
        throw new Error();
      }
    }

    if(!uri.startsWith("/"))
    {
      return null;
    }

    // Convert file separators.
    uri = uri.replace('/', File.separatorChar);

    // Simplistic dumb security check.
    // You will have to do something serious in the production environment.
    if(uri.contains(File.separator + '.') ||
        uri.contains('.' + File.separator) ||
        uri.startsWith(".") || uri.endsWith(".") ||
        INSECURE_URI.matcher(uri).matches())
    {
      return null;
    }

    int i;
    if((i = uri.indexOf("?")) > -1)
      return uri.substring(1, i);

    // Convert to absolute path.
    return uri.substring(1);
  }

  private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

  private static void sendListing(ChannelHandlerContext ctx, Path dir) throws IOException
  {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

    StringBuilder buf = new StringBuilder();
    String dirPath = dir.toAbsolutePath().toString();

    buf.append("<!DOCTYPE html>\r\n");
    buf.append("<html><head><title>");
    buf.append("Listing of: ");
    buf.append(dirPath);
    buf.append("</title></head><body>\r\n");

    buf.append("<h3>Listing of: ");
    buf.append(dirPath);
    buf.append("</h3>\r\n");

    buf.append("<ul>");
    buf.append("<li><a href=\"../\">..</a></li>\r\n");

    Files.list(dir).forEach(f -> {
      if(Files.isRegularFile(f))
      {
        String name = f.getFileName().toString();
        //if(!ALLOWED_FILE_NAME.matcher(name).matches())
        //{
          buf.append("<li><a href=\"");
          buf.append(name);
          buf.append("\">");
          buf.append(name);
          buf.append("</a></li>\r\n");
        //}
      }
    });

    buf.append("</ul></body></html>\r\n");
    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();

    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  private static void sendRedirect(ChannelHandlerContext ctx, String newUri)
  {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
    response.headers().set(LOCATION, newUri);

    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  public static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
  {
    FullHttpResponse response = new DefaultFullHttpResponse(
        HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
    response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  /**
   * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
   *
   * @param ctx Context
   */
  private static void sendNotModified(ChannelHandlerContext ctx)
  {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
    setDateHeader(response);

    // Close the connection as soon as the error message is sent.
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  /**
   * Sets the Date header for the HTTP response
   *
   * @param response HTTP response
   */
  private static void setDateHeader(FullHttpResponse response)
  {
    SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
    dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

    Calendar time = new GregorianCalendar();
    response.headers().set(DATE, dateFormatter.format(time.getTime()));
  }

  /**
   * Sets the Date and Cache headers for the HTTP Response
   *
   * @param response    HTTP response
   * @param fileToCache file to extract content type
   */
  private static void setDateAndCacheHeaders(HttpResponse response, Path fileToCache)
  {
    SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
    dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

    // Date header
    Calendar time = new GregorianCalendar();
    response.headers().set(DATE, dateFormatter.format(time.getTime()));

    // Add cache headers
    time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
    response.headers().set(EXPIRES, dateFormatter.format(time.getTime()));
    response.headers().set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
    try
    {
      response.headers().set(
          LAST_MODIFIED, dateFormatter.format(new Date(Files.getLastModifiedTime(fileToCache).toMillis())));
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Sets the content type header for the HTTP Response
   *
   * @param response HTTP response
   * @param file     file to extract content type
   */
  private static void setContentTypeHeader(HttpResponse response, Path file)
  {
    MimetypesFileTypeMap mimeTypesMap = MimeTypeFactory.get();
    response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(file.toFile()));
  }

}
