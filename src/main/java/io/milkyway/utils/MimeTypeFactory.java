package io.milkyway.utils;

import javax.activation.MimetypesFileTypeMap;

/**
 * Store a collection of mimetypes that are supported by the application.
 * The most common mime types are automatically preloaded.
 * Any specific mime types can be added.
 *
 * @author jvmvik
 */
public class MimeTypeFactory
{
  static MimetypesFileTypeMap mimeTypesMap;

  /**
   * @return Mime types supported
   */
  public static MimetypesFileTypeMap get()
  {
    if (mimeTypesMap == null)
    {
      mimeTypesMap = new MimetypesFileTypeMap();
      mimeTypesMap.addMimeTypes("text/html html htm");
      mimeTypesMap.addMimeTypes("text/javascript js javascript");
      mimeTypesMap.addMimeTypes("text/plain txt text TXT");
      mimeTypesMap.addMimeTypes("text/css css");
      mimeTypesMap.addMimeTypes("image/gif ico");
      mimeTypesMap.addMimeTypes("image/png png");
      mimeTypesMap.addMimeTypes("image/jpeg jpg");
      mimeTypesMap.addMimeTypes("image/svg svg");
      //TODO Add more file type
    }
    return mimeTypesMap;
  }

  /**
   * Add a new mimetypes
   *
   * @param mimetype
   */
  public static void addMimetype(String mimetype)
  {
    get().addMimeTypes(mimetype);
  }
}
