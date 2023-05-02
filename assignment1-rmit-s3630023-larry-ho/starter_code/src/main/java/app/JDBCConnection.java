package app;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database. Allows SQL
 * queries to be used with the SQLLite Databse in Java.
 * 
 * This is an example JDBC Connection that has a single query for the Movies
 * Database This is similar to the project workshop JDBC examples.
 *
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Halil Ali, 2021. email halil.ali@rmit.edu.au
 */
public class JDBCConnection {

   // the default oracle account uses the the read only MOVIES database
   // once you create a set of tables in your own account, update this to your RMIT
   // Oracle account details
   private static final String DATABASE_USERNAME = "s3630023";
   private static final String DATABASE_PASSWORD = "vX6AnL4n";

   private static final String DATABASE_URL = "jdbc:oracle:thin:@//localhost:9922/CSAMPR1.its.rmit.edu.au";
   private static JDBCConnection jdbc = null;
   private static Connection connection;

   /**
   * Singleton function to return single copy of this class to other classes
   **/
   public static JDBCConnection getConnection(){

      //check that ssh session is still open (if not reopen)
      SSHTunnel.getSession();

      //check that JDBCconnection is available (if not establish)
      if(jdbc==null){
         jdbc = new JDBCConnection();
      }
      return jdbc;
   }

   /**
   * Hidden constructor to establish Database connection (once)
   **/
   private JDBCConnection() {
      System.out.println("Created JDBC Connection Object");
      
      try {
         // Connect to JDBC data base
         connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
      } catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }

   /**
   * Closes the database connection - called only when server shutdown
   **/
   public static void closeConnection(){
      try {
         if (connection != null) {
            connection.close();
            System.out.println("Database Connection closed");
         }
      } catch (SQLException e) {
         // connection close failed.
         System.err.println(e.getMessage());
      }
   }

   /** 
    *  Adds post submitted to post database
    */
   public void addPost(String postid, String postBody, String email) {
      try {
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Adds post to database
         String query = "INSERT into post (id, body, timestamp, member_email) " + "\n"
                        + "VALUES ('" + postid + "', '" + postBody + "', (TO_DATE('" + LocalDate.now() + "', 'YYYY-MM-DD')), LOWER('" + email + "'))";

         statement.executeQuery(query);

         statement.close();
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
   /** 
    * Add response to post
   */
  public void addResponse(String postID, String postBody, String originalpostID, String email) {
   try {
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);

      // Adds post to database
      String query = "INSERT into post (id, body, timestamp, post_id, member_email) " + "\n"
                     + "VALUES ('" + postID + "', '" + postBody + "', (TO_DATE('" + LocalDate.now() + "', 'YYYY-MM-DD')), '" + originalpostID + "', LOWER('" + email + "'))" ;

      statement.executeQuery(query);

      statement.close();
   }
   catch (SQLException e) {
      // If there is an error, lets just print the error
      System.err.println(e.getMessage());
   }
}
   /**
    * Get all posts from logged in user
    */
    

   /** 
    * Gets all posts from friends added
   */
    public ResultSet getPost(String email) {
      ResultSet results = null;
      try {
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Select posts query
         String query = "SELECT *"                            + "\n" + 
                        "FROM post"             + "\n" +
                        "WHERE member_email IN (SELECT member_emailone FROM friendship WHERE member_emailtwo = LOWER('" + email + "')) \n" +
                        "OR member_email IN (SELECT member_emailtwo FROM friendship WHERE member_emailone = LOWER('" + email + "'))" + "\n" + 
                        "OR member_email = LOWER('" + email + "')" +
                        "ORDER BY timestamp DESC \n";

         // Get Result
         results = statement.executeQuery(query);

         //statement.close();
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      return results;
   }
   /**
    * Update post in database
   */
   public void updatePost(String body, String username, String id) {
      try {
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Update the selected post by the logged in user by modifying the body text and timestamp
         String query = "UPDATE post "                            + "\n" + 
                        "SET body = '" + body + "', timestamp = '(TO_DATE('" + LocalDate.now() + "', 'YYYY-MM-DD')) \n" +
                        "WHERE id = '" + id + "' AND member_email = '" + username + "';";
         
         statement.execute(query);
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
   /** 
    * Delete post from database
   */
   public void deletePost(String id, String username) {
      try {
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Query deletes posts made by selected post and logged in user
         String query = "DELETE FROM post WHERE id = '" + id + "' AND member_email = '" + username  + "'";

         statement.execute(query);
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
   /** 
    * Get response for post
    * @return 
    */
   public ResultSet getResponse(String originalPost) {
      ResultSet results = null;
      try {
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Select posts made by friends and self
         String query = "SELECT *"                            + "\n" + 
                        "FROM post"             + "\n" +
                        "WHERE post_id = LOWER('" + originalPost + "')" + "\n" +
                        "ORDER BY timestamp DESC \n";

         // Get Result
         results = statement.executeQuery(query);

         //statement.close();
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      return results;
   }
   /** 
    * Insert user into the database
    * @param password
   */
   public void insertMember(String email, String fullname, String screenname, Date dob, String gender,
   String status, String location, String visibility, String password) {
      try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Insert the variables into the database
            String query = "INSERT into member " + "\n" +
                           "VALUES (LOWER('" + email + "'), LOWER('" + fullname + "'), LOWER('" + screenname + "'), (TO_DATE('" + dob + "', 'YYYY-MM-DD')), LOWER('" + gender + "'), " 
                           + "LOWER('" + status + "'), LOWER('" + location + "'), LOWER('" + visibility + "'), LOWER('" + password + "'))\n" ;
            // (email, fullname, screen_name, date_of_birth, gender, status, location, visibility_name)
            statement.executeQuery(query);

            statement.close();
         }
         catch (SQLException e) {
            // If there is an error, lets just print the error
            System.err.println(e.getMessage());
      }
   }
   /**
    * Check if username field matches with email database
    */
   public boolean checkUsername(String username) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

      // The Query
         String query = "SELECT *"                                        + "\n" +
                        "FROM member"                                      + "\n" +
                        "WHERE LOWER(email) = LOWER('" + username + "')";

         ResultSet results = statement.executeQuery(query);
         // If there are no results, there is no match in the database
         if (results == null){
            statement.close();
            return false;
         }
         else {
            statement.close();
            return true;
         }
      } catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      return false;
   }
   /**
    * Get user information from database
    */
   public ArrayList<String> getMember(String username) {
      ArrayList<String> user = new ArrayList<String>(); 
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Query the selected username
         String query = "SELECT *"                                        + "\n" +
                        "FROM member"                                      + "\n" +
                        "WHERE LOWER(email) = LOWER('" + username + "')";

         ResultSet results = statement.executeQuery(query);

         while (results.next()) {
            String email = results.getString("email");
            String fullname = results.getString("fullname");
            String screenname = results.getString("screen_name");
            String dob = results.getString("date_of_birth");
            String gender = results.getString("gender");
            String status = results.getString("status");
            String location = results.getString("location");
            String visibility = results.getString("visibility_name");
            String password = results.getString("password");

            user.add(email);
            user.add(fullname);
            user.add(screenname);
            user.add(dob);
            user.add(gender);
            user.add(status);
            user.add(location);
            user.add(visibility);
            user.add(password);
         }
         statement.close();
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      return user;
   }
   /**
    * Update user data in database
    */
    public void updateMember(String email, String screenname, String status, String location, String visibility) {
      try {
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Query only the current user information and only screen name, status, location and visibility level
         String query = "UPDATE member " + "\n" +
                        "SET screen_name = LOWER('" + screenname + "'), status = LOWER('" + status + "'), location = LOWER('" + location + "'), " 
                        + " visibility_name = LOWER('" + visibility + "') \n" + 
                        "WHERE email = LOWER('" + email + "')";
         statement.executeQuery(query);

         statement.close();
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }

   /** 
    * Delete user from database
   */
   public void deleteMember(String username) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // 
         // Delete the current logged in or selected user
         String query = "DELETE FROM member WHERE email = '" + username + "'";

         statement.executeQuery(query);

         statement.close();
      }  catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }

   }
   /** 
    * Create a friendship record with the two users in the friend request record
   */
   public void addFriendship(String memberemail1, String memberemail2) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         String query =  "INSERT into friendship " + "\n" +
         "VALUES (LOWER('" + memberemail1 + "'), LOWER('" + memberemail2 + "'), TO_DATE('" + LocalDate.now() + "', 'YYYY-MM-DD')) \n" ;

         statement.executeQuery(query);

         statement.close();
      }  catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
   /**
    * Get the friendship record so friend posts will appear on the user
    */
   public ArrayList<String> getFriendship(String email) {
      ArrayList<String> friendships = new ArrayList<String>();
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);
         String query = "SELECT * FROM friendship" + "\n" +
                        "WHERE member_emailone = '" + email + "' OR member_emailtwo = '" + email + "'\n";

         ResultSet results = statement.executeQuery(query);

         while (results.next()) {
            String emailone = results.getString("member_emailone");
            String emailtwo = results.getString("member_emailtwo");

            if (emailone == email) {
               friendships.add(emailtwo);
            }
            else if (emailtwo == email) {
               friendships.add(emailone);
            }
         }
         statement.close();
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      return friendships;
   }
   /**
    * Send a friend request to the user searched
    */
   public void addFriendRequest(String memberemail1, String memberemail2) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Adds friendship 
         String query =  "INSERT into friendrequest " + "\n" +
         "VALUES (LOWER('" + memberemail1 + "'), LOWER('" + memberemail2 + "'), (TO_DATE('" + LocalDate.now() + "', 'YYYY-MM-DD')), LOWER('pending'))" ;

         statement.executeQuery(query);

         statement.close();

      }  catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
   /**
    * Decline the friend request
    */
   public void deleteFriendRequest(String memberemail1, String memberemail2) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Adds friendship 
         String query = "DELETE FROM friendrequest WHERE (member_emailrequester = '" + memberemail1 + "' AND member_emailrecepient = '" + memberemail2  + "')" + 
                        "OR (member_emailrequester = '" + memberemail2 + "' AND member_emailrecepient = '" + memberemail1 + "')" ;

         statement.executeQuery(query);

         statement.close();

      }  catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
   /**
    * Get the friend requests sent to a user
    */
   public ArrayList<String> getFriendRequest(String memberemail) {
      ArrayList<String> friendRequests = new ArrayList<String>();
      try {
            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * from friendrequest " + "\n" + 
            "WHERE member_emailrecepient = LOWER('" + memberemail + "') AND status = LOWER('pending')";

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
               String emailrequester = results.getString("member_emailrequester");
   
               friendRequests.add(emailrequester);
            }

            statement.close();
      } catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      return friendRequests;
   }
/**
 * Accept the friend request and update the status to accept
 */
   public void acceptFriendRequest (String memberemail, String friendEmail) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         String query = "UPDATE friendrequest" + "\n" +
         "SET status = LOWER('accepted')" + 
         "WHERE member_emailrecepient = LOWER('" + memberemail + "') AND member_emailrequester = LOWER('" + friendEmail + "')";

      statement.executeQuery(query);

      statement.close();
      } catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
   }
   }
   /**
    * Add a like to a post or response
    */
   public void addLike(String postID, String email) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Logged in user likes selected post
         String query =  "INSERT into postlike " + "\n" +
         "VALUES (LOWER('" + email + "'), LOWER('" + postID + "'))" ;

         statement.executeQuery(query);

         statement.close();

      }  catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      
   }
/**
 * Remove a like on a post or response
 */
   public void removeLike(String postID, String email) {
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // Delete liked post by the logged in user
         String query = "DELETE FROM postlike WHERE email = '" + email + "' AND postID = '" + postID + "';";

         statement.executeQuery(query);

         statement.close();
      }  catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
/**
 * Get information on who liked the posts
 */
   public ArrayList<String> getLike(String post) {
      ArrayList<String> postLikes = new ArrayList<String>();
      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);
         String query = "SELECT * FROM postlike" + "\n" +
                        "WHERE post_id = '" + post + "'";

         ResultSet results = statement.executeQuery(query);

         while (results.next()) {
            String email = results.getString("member_email");

            postLikes.add(email);
         }
         statement.close();
      }
      catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
      return postLikes;
   }
}
