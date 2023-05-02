package app;

import java.util.ArrayList;

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
public class Maintenance implements Handler {

   // URL of this page relative to http://localhost:7000/

   public static final String URL = "/maintenance.html";

   @Override
   public void handle(Context context) throws Exception {
      // Create a simple HTML webpage in a String
      String html = "<html>\n";

      // Add some Header information
      html = html + "<head>" + "<title>User Maintenance</title>\n";

      // Add some CSS (external file)
      html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

      // Add the body
      html = html + "<body>\n";

      JDBCConnection jdbc = JDBCConnection.getConnection();

      // Get logged in user information
      String username = context.sessionAttribute("username");
      ArrayList<String> userDetails = jdbc.getMember(username);

      String screenname = userDetails.get(2);
      //String status = userDetails.get(5);
      String location = userDetails.get(6);
      //String visibility = userDetails.get(7);

      // Form to update screen name, status, location and visibility level
      // Obtained user information is used as placeholder
      html = html + "<a href='/'><button class='return' type='button'>Return to Main</button></a>\n";
      html = html + "<h1>Update Profile</h1>";
      html = html + "<form action='/maintenance.html' method='post'>\n";
      html = html + "<label for='screenname'>Screen Name:</label>\n";
      html = html + "<input class='maintenance' type='text' id='screenname' name='screenname' value='" + screenname + "' required>\n";
      html = html + "<label for='status'>Status:</label>\n";
      html = html + "<select class='maintenance' id='status_drop' name='status_drop'>\n";
      html = html + "         <option value='' disable hidden>Status</option>\n"; 
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
      html = html + "<input class='maintenance' type='text' id='location' name='location' value=' " + location + "'>\n";
      html = html + "<label for='visibility_drop'>Visibility:</label>\n";
      html = html + "<select class='maintenance' id='visibility_drop' name='visibility_drop'>\n";
      html = html + "         <option value='' disable hidden>Visibility</option>\n"; 
      html = html + "         <option>Private</option>\n";
      html = html + "         <option>Friends-Only</option>\n";
      html = html + "         <option>Everyone</option>\n";
      html = html + "</select>\n";
      html = html + "<button id='updatebutton' type='submit'>Update</button>\n";
      html = html + "</form>\n";

      String newScreenname = context.formParam("screenname");
      String newStatus = context.formParam("status_drop");
      String newLocation = context.formParam("location");
      String newVisibility = context.formParam("visibility_drop");

      // Updates information given by logged in user
      if (newStatus != null) {
         jdbc.updateMember(username, newScreenname, newStatus, newLocation, newVisibility);
         context.redirect("/main.html");
      }

      // Delete account
      html = html + "<form method='post'>";
      html = html + "<input type='hidden' id='deleteuser' name='deleteuser' value='" + username + "'>\n";
      html = html + "<button id='deleteuser' type='submit'>Delete</button>\n";
      html = html + "</form>";

      String deleteUser = context.formParam("deleteuser");

      if (deleteUser != null) {
         jdbc.deleteMember(username);
         context.redirect("/");
      }
      
      // Finish the HTML webpage
      html = html + "</body>" + "</html>\n";

      // DO NOT MODIFY THIS
      // Makes Javalin render the webpage
      context.html(html);
   }

}
