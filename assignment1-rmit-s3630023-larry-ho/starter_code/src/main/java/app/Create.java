package app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;


import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Temporary HTML as an example page.
 * 
 * Based on the Project Workshop code examples.
 * This page currently:
 *  - Provides a link back to the index page
 *  - Displays the list of movies from the Movies Database using the JDBCConnection
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2021. email: halil.ali@rmit.edu.au
 */
public class Create implements Handler {

   // URL of this page relative to http://localhost:7000/
   public static final String URL = "/create.html";

   @Override
   public void handle(Context context) throws Exception {
      // Create a simple HTML webpage in a String
      String html = "<html>\n";

      // Add some Header information
      html = html + "<head>" + "<title>Sign Up</title>\n";

      // Add some CSS (external file)
      html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

      html = html + "<div id='registration>";
      // Add the body
      html = html + "<body>\n";

      // Add HTML for link back to the homepage

      html = html + "<a href='/'><button class='return' type='button'>Link to Homepage</button></a>\n";
      html = html + "<h1>Sign Up</h1>\n";
      // Look up some information from JDBC
      // First we need to use your JDBCConnection class
      JDBCConnection jdbc = JDBCConnection.getConnection();

      // Next we will ask this *class* for the movies
      // ArrayList<String> movies = jdbc.getMovies();

      // Add form to create account
      html = html + "<form action='/create.html' method='post'>\n";
      html = html + "<label for='email'>Email:</label>\n";
      html = html + "<input class='register' type='text' id='email' name='email' required>\n";
      html = html + "<label for='fullname'>Full Name:</label>\n";
      html = html + "<input class='register' type='text' id='fullname' name='fullname' required>\n";
      html = html + "<label for='screenname'>Screen Name:</label>\n";
      html = html + "<input class='register' type='text' id='screenname' name='screenname'>\n";
      html = html + "<label for='dob'>Date of Birth:</label>\n";
      html = html + "<input class='register' type='date' id='dob' name='dob' max='" + LocalDate.now() + "' required>\n";
      html = html + "<label for='gender'>Gender:</label>\n";
      html = html + "<select class='register' id='gender_drop' name='gender_drop'>\n";
      html = html + "         <option value='male'>Male</option>\n";
      html = html + "         <option value='female'>Female</option>\n";
      html = html + "         <option value='unspecified'>Unspecified</option>\n";
      html = html + " </select>\n";
      html = html + "<label for='status'>Status:</label>\n";
      html = html + "<select class='register' id='status_drop' name='status_drop'>\n";
      html = html + "         <option value='' selected disabled hidden>Status</option>\n"; 
      html = html + "         <option>Single</option>\n";
      html = html + "         <option>In a relationship</option>\n";
      html = html + "         <option>Engaged</option>\n";
      html = html + "         <option>Married</option>\n";
      html = html + "         <option>In a civil partnership</option>\n";
      html = html + "         <option>In a domestic partnership</option>\n";
      html = html + "         <option>In an open relationship</option>\n";
      html = html + "         <option>It's complicated</option>\n";
      html = html + "         <option>Separated</option>\n";
      html = html + "         <option>Divorced</option>\n";
      html = html + "         <option>Widowed</option>\n";
      html = html + "</select>\n"; 
      html = html + "<label for='location'>Location:</label>\n";
      html = html + "<input class='register' type='text' id='location' name='location'>\n";
      html = html + "<label for='visibility_drop'>Visibility:</label>\n";
      html = html + "      <select id='visibility_drop' name='visibility_drop'>\n";
      html = html + "         <option>Private</option>\n";
      html = html + "         <option>Friends-Only</option>\n";
      html = html + "         <option>Everyone</option>\n";
      html = html + "      </select>\n";
      html = html + "<label for 'password'>Password:<label>\n";
      html = html + "<input class='register' type='password' id='password' name='password' required>\n";

      //html = html + "<input type='password' id='pwd' name='pwd'>\n";
      html = html + "<button id='createbutton' type='submit'>Create Account</button>\n";

      // Close form
      html = html + "</form>\n";

   
      // Submitted form data into variables
      String email = context.formParam("email");   
      String fullname = context.formParam("fullname");
      String screenname = context.formParam("screenname");
      String dobForm = context.formParam("dob");
      Date dob = null;
      if (dobForm != null) {
         //dob = new SimpleDateFormat("yyyy-MM-dd").parse(dobForm);
         dob = java.sql.Date.valueOf(dobForm);
      }
      String gender = context.formParam("gender_drop"); 
      String status = context.formParam("status_drop");
      String location = context.formParam("location");
      String visibility = context.formParam("visibility_drop");
      String password = context.formParam("password");
      // Validation for fields
   
      String generatedPassword = null;
      // Hash password
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

    // Checks if email is unique 
        if (email != null) {
        boolean unique = uniqueUser(email);
        if (unique == true) {
            // Registers user and adds to database
            jdbc.insertMember(email, fullname, screenname, dob, gender, status, location, visibility, generatedPassword);
            context.sessionAttribute("username", email);
            context.redirect("/main.html");
        }  
            else if (unique == false) {
            // error message email is already taken
            context.redirect("/create.html");
        }
    }

      html = html + "</div>";
      // Finish the HTML webpage
      html = html + "</body>" + "</html>\n";

      // DO NOT MODIFY THIS
      // Makes Javalin render the webpage
      context.html(html);


   }

   public boolean uniqueUser(String email) {
      JDBCConnection jdbc = JDBCConnection.getConnection();
      ArrayList<String> user = jdbc.getMember(email);
      boolean unique = false;

      if (user.isEmpty()){
          unique = true;
      }
      else { 
          return false;
      }
      return unique;   
  }
}
