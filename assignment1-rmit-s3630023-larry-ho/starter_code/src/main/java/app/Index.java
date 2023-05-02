package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.ArrayList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin by writing the raw HTML into a Java
 * String object
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class Index implements Handler {

   // URL of this page relative to http://localhost:7000/
   public static final String URL = "/";

   @Override
   public void handle(Context context) throws Exception {
      // Create a simple HTML webpage in a String
      String html = "<html>\n";

      // Add some Header information
      html = html + "<head>" + "<title>Login</title>\n";

      // Add some CSS (external file)
      html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

      // Add the body
      html = html + "<body>\n";

      html = html + "<div id='main'>";
      // Add HTML for the logo.png image
      html = html + "<div id='logo'>";
      html = html + "<img src='logo.png' height='200px'/>\n";
      html = html + "</div>";
      // Add HTML for the list of pages
      html = html + "<h1>Homepage</h1>" + "<h2>Log In</h2>" + "\n";

      // Log In form
         // direct to main page once form is submitted and correct
      html = html + "<form action ='/' method ='post'>\n";
      html = html + "<label for='username'>Username:</label>\n";
      html = html + "<input type='text' id='username' name='username' class='login'required>\n";
      html = html + "<label for 'password'>Password:<label>\n";
      html = html + "<input type='password' id='pwd' name='pwd' class='login' required>\n";
      html = html + "<button id='loginbutton' type='submit'>Log In</button>\n";
      html = html + "</form>\n";
      // Button for new users to create account
      html = html + "<a href='/create.html'><button id='createbutton' type='button'>Create New Account</button></a>\n";

      // Login check
      String username = context.formParam("username");
      String password = context.formParam("pwd");

      String generatedPassword = null;
      if (password != null) {
         try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            // Get the hash's bytes 
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
               sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
         } 
         catch (NoSuchAlgorithmException e) 
         {
               e.printStackTrace();
         }
       }
      boolean authentication = authenticateUser(username, generatedPassword);
      if (authentication == true) {
         // Stores username details 
         context.sessionAttribute("username", username);
         context.redirect("/main.html");
      }
      html = html + "</div>";
      // Finish the HTML webpage
      html = html + "</body>" + "</html>\n";

      // DO NOT MODIFY THIS
      // Makes Javalin render the webpage
      context.html(html);
   }

   public boolean authenticateUser(String username, String password) {
      JDBCConnection jdbc = JDBCConnection.getConnection();
      ArrayList<String> users = jdbc.getMember(username);
      String email = "";
      String databasePassword = "";
      boolean confirmed = false;

      if (!users.isEmpty()){
          // User email and password hash element in the array
          email = users.get(0);
          databasePassword = users.get(8);
          // Checks if the entered email is the same as in the database
          if (email.equals(username)) {
              // Checks if the entered password is the same in the database
              if (databasePassword.equals(password)) {
                  confirmed = true;
              }
          }
      }
  return confirmed;
  }
}

