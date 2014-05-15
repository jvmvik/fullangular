function check_html5_storage() {
  try
  {
    return 'localStorage' in window && window['localStorage'] !== null;
  }
  catch (e)
  {
    return false;
  }
}

if(!check_html5_storage())
{
    alert("Please update your web browser !! This application is built with HTML5 which is not supported by older web browser...")
}
