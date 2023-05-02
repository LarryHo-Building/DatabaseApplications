package app;

import io.javalin.http.Context;


public class Util {
   public static String getLoggedInUser(Context context) {
      String id = context.cookie("id");
      String user = null;
      if(id != null && context.sessionAttribute(id) != null) {
         user = context.sessionAttribute(id).toString();;
      } 
      return user;
   }
}
