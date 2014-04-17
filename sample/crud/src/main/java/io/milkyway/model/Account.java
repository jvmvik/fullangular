package io.milkyway.model;

import com.sun.xml.internal.ws.developer.Serialization;

import java.io.Serializable;
import java.util.UUID;

/**
 * Account
 */
public class Account implements Serializable
{

  UUID uuid;
  String title;
  String login;
  String password;
  String url;
  String description;

  public Account(String title)
  {
    this.title = title;
    this.uuid = UUID.randomUUID();
  }

  public UUID getUuid()
  {
    return uuid;
  }

  public void setUuid(UUID uuid)
  {
    this.uuid = uuid;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getLogin()
  {
    return login;
  }

  public void setLogin(String login)
  {
    this.login = login;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}
