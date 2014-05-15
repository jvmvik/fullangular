package io.milkyway.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.UUID;

/**
 * Account domain object
 */
public class Account implements Serializable
{

  UUID uuid;
  String title;
  String category; // Group name
  String login;
  String password;
  String url;
  String description;

  public Account()
  {
  }

  public Account(String title)
  {
    this.title = title;
    this.uuid = UUID.randomUUID();
  }

  /*
   * Convert a
   */
  public Account make(Gson gson, String json)
  {
    Account account = gson.fromJson(json, Account.class);
    if(account.uuid != null)
      account.uuid = UUID.randomUUID();
    return account;
    //TODO Add some validation...
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

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }
}
