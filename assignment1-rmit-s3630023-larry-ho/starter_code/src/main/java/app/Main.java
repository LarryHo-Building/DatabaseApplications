package app;

import java.util.ArrayList;
import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2021. email: halil.ali@rmit.edu.au
 */
public class Main implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/main.html";

    @Override
    public void handle(Context context) throws Exception {


        // Create a simple HTML webpage in a String
        String html = "<html>\n";

        // Add some Header information
        html = html + "<head>" + 
               "<title>Main Page</title>\n";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

        // Add the body
        html = html + "<body>\n";

        JDBCConnection jdbc = JDBCConnection.getConnection();

        html = html + "<div class='main'>";
        // Textbox to submit posts
        html = html + "<form action ='/main.html' method ='post'>\n";
        html = html + "<input type='text' id='post' name='post'>\n";
        html = html + "<button id='submitpostbutton' type='submit'>Post</button>\n";
        html = html + "</form>\n";


        String username = context.sessionAttribute("username");

        String post = context.formParam("post");

        // Stores post to database
        if (post != null) {
            String uniquepostID = UUID.randomUUID().toString();
            jdbc.addPost(uniquepostID, post, username);
        }
        html = html + outputPosts(username);

        String likedPost = context.queryParam("likedpost");
        
        // Check if a post was liked by a button
        if (likedPost != null) {
            jdbc.addLike(likedPost, username);
        }

        String originalPost = context.formParam("originalpost");
        String response = context.formParam("response");
        String responsereply = context.formParam("responsereply");

        // Check if a response was submitted
        if (originalPost != null) {
            String uniquepostID = UUID.randomUUID().toString();
            if (response != null) {
                jdbc.addResponse(uniquepostID, response, originalPost, username);
            }
            else if (responsereply != null)
            {
                jdbc.addResponse(uniquepostID, responsereply, originalPost, username);
            }
            uniquepostID = null;
            response = null;
            originalPost = null;
            responsereply = null;
        }

        html = html + "</div>";
        // Left Pane
        //Maintenance Page
        html = html + "<div id='leftpane'>\n";
        html = html + "<h2>Account Maintenance</h2>\n";
        html = html + "<a href='/maintenance.html'><button id='maintenancebutton' type='button'>Edit Profile</button></a>\n";
        html = html + "</div>\n";
        // Delete logged in account

        // Right Pane for friendship management
        html = html + "<div id='rightpane'>\n";
        html = html + "<h2>Friend Management</h2>\n";
        html = html + "<h3>Friend Requests</h3>\n";
        // Get pending friend requests from database
        ArrayList<String> friendrequests = jdbc.getFriendRequest(username);
        // Output pending friend requests
        html = html + outputFriendRequest(friendrequests);

        // Check if friend request has been accepted
        String accept = context.queryParam("friendreqaccept");
        String decline = context.queryParam("friendreqdecline");

        if (accept != null) {
            // Insert friendship and update friend invite from pending to accepted
            jdbc.addFriendship(username, accept);
            jdbc.acceptFriendRequest(username, accept);
            accept = null;
        }
        else if (decline != null) {
            // Delete friend request invite
            jdbc.deleteFriendRequest(username, decline);
            decline = null;
        }
        // Searches for friendship request table and show if status is pending
        html = html + "<h3>Search for Friend</h3>\n";
        // Search other users and send friendship request
        html = html + "<form method='post' action='/main.html'>\n";
        html = html + "<input type='text' id='friendsearch' name='friendsearch'>\n";
        html = html + "<button id='searchbutton' type='submit'>Search</button>\n";
        html = html + "</form>\n";

        String friendEmail = context.formParam("friendsearch");

        // Output user with searched username
        if (friendEmail != null) {
            String friendsearchResults = outputFriendSearch(friendEmail);
            html = html + friendsearchResults;
        }
        // Send a friend request
        String recipient = context.formParam("friendsearchresult");
        if (recipient != null) {
            jdbc.addFriendRequest(username, recipient);
            html = html + "Friend Request sent\n";
        }
        html = html + "</div>\n";

        html = html + "</body>" + "</html>\n";

        context.html(html);

    }

    public String outputPosts(String email) throws SQLException {
        String html = "";
        JDBCConnection jdbc = JDBCConnection.getConnection();
        
        ResultSet results = jdbc.getPost(email);    

        if (results != null) {
            while (results.next()) {
                String id = results.getString("id");
                String postbody = results.getString("body");
                String timestamp = results.getString("timestamp");
                String response = results.getString("post_id");
                String memberemail = results.getString("member_email");

                if (response == null) {
                    html = html + "<div class='posts'> \n";
                    html = html + "<div>\n";
                    html = html + memberemail + "\n";
                    html = html + "</div>\n";
                    html = html + "<div class='postbody'>\n";
                    html = html + postbody + "\n";
                    html = html + "</div>\n";
                    html = html + "<div>\n";
                    html = html + timestamp + "\n";
                    html = html + "</div>\n";
                    // Outputs how many likes a post has
                    ArrayList<String> likes = jdbc.getLike(id);
                    int postLikes = likes.size();
                    html = html + "<div>\n";
                    html = html + postLikes + " like(s) this post";
                    html = html + "</div>\n";
                    // Like Section
                    html = html + "<form action ='/main.html' method='get'>\n";
                    html = html + "<input type='hidden' id='likedpost' name='likedpost' value='" + id + "'>\n";
                    html = html + "<button class='like' type='submit'>Like</button>\n";
                    html = html + "</form>\n";
                    // Response Section
                    html = html + "<form action ='/main.html' method='post'>\n";
                    html = html + "<input type='hidden' id='originalpost' name='originalpost' value='" + id + "'>\n";
                    html = html + "<input type='text' class='response' name='response'>\n" ;
                    html = html + "<button class='responsesubmit' type='submit'>Comment</button>\n";
                    html = html + "</form> \n";
                    html = html + outputResponse(id, email);
                    html = html + "</div> \n";
                }
            }
        }
        results.close();

        return html;
    }

    public String outputResponse(String originalPost, String email) throws SQLException {
        String html = "";
        JDBCConnection jdbc = JDBCConnection.getConnection();
        
        ResultSet results = jdbc.getResponse(originalPost);

        if (results != null) {
            while (results.next()) {
                html = html + "<div class = 'responsediv'> \n";
                String id = results.getString("id");
                String postbody = results.getString("body");
                String timestamp = results.getString("timestamp");
                //String response = results.getString("post_id");
                String memberemail = results.getString("member_email");
                html = html + "<div>\n";
                html = html + memberemail + "\n";
                html = html + "</div>\n";
                html = html + "<div>\n";
                html = html + postbody + "\n";
                html = html + "</div>\n";
                html = html + "<div>\n";
                html = html + timestamp + "\n";
                html = html + "</div>\n";

                // Outputs how many likes a post has
                ArrayList<String> likes = jdbc.getLike(id);
                int postLikes = likes.size();
                html = html + "<div>\n";
                html = html + postLikes + " like(s) this post \n";
                html = html + "</div>\n";
                // Like Section
                html = html + "<form action ='/main.html' method='get'>\n";
                html = html + "<input type='hidden' id='likedpost' name='likedpost' value='" + id + "'>\n";
                html = html + "<button class='like' type='submit'>Like</button>\n";
                html = html + "</form> \n";
                // Response Section (Different Div and smaller)
                html = html + "<form action ='/main.html' method='post'>\n";
                html = html + "<input type='hidden' id='originalpost' name='originalpost' value='" + id + "'>\n";
                html = html + "<input type='text' class='responsereply' name='responsereply'>\n" ;
                html = html + "<button class='responsereplysubmit' type='submit'>Reply</button>\n";
                html = html + "</form> \n";
                html = html + "</div>\n";
                html = html + outputReplyResponse(id);

            }
        }
        results.close();

        return html;
    }
    
    public String outputReplyResponse(String originalPost) throws SQLException {
        JDBCConnection jdbc = JDBCConnection.getConnection();
        ResultSet results = jdbc.getResponse(originalPost);
        String html = "";

        if (results != null) {
            while (results.next()) {
                html = html + "<div class = 'responsereplydiv'>\n";
                String id = results.getString("id");
                String postbody = results.getString("body");
                String timestamp = results.getString("timestamp");
                //String response = results.getString("post_id");
                String memberemail = results.getString("member_email");
                html = html + "<div>\n";
                html = html + memberemail + "\n";
                html = html + "</div>\n";
                html = html + "<div>\n";
                html = html + postbody + "\n";
                html = html + "</div>\n";
                html = html + "<div>\n";
                html = html + timestamp + "\n";
                html = html + "</div>\n";
                // Outputs how many likes a post has
                ArrayList<String> likes = jdbc.getLike(id);
                int postLikes = likes.size();
                html = html + "<div>\n";
                html = html + postLikes + " like(s) this post";
                html = html + "</div>\n";
                // Like Section
                html = html + "<form action ='/main.html' method='get'>\n";
                html = html + "<input type='hidden' id='likedpost' name='likedpost' value='" + id + "'>\n";
                html = html + "<button class='like' type='submit'>Like</button>\n";
                html = html + "</form> \n";
                html = html + "</div>\n";
            }
        }
        results.close();
        return html;
    }
    public String outputFriendRequest(ArrayList<String> friendrequests) {
        String html = "";
        for (String request : friendrequests) {
            html = html + request;

            html = html + "<form action ='/main.html' method='get'> \n";
            html = html + "<input type='hidden' id='friendreqaccept' name='friendreqaccept' value='" + request + "'>\n";
            html = html + "<button class='confirm' type='submit'>Confirm</button> \n";
            html = html + "</form> \n";

            html = html + "<form action ='/main.html' method='get'> \n";
            html = html + "<input type='hidden' id='friendreqdecline' name='friendreqdecline' value='" + request + "'>\n";
            html = html + "<button class='delete' type='submit'>Delete</button> \n";
            html = html + "</form> \n";
        }
    
        return html;
    }
    
    public String outputFriendSearch(String friendEmail) {
        JDBCConnection jdbc = JDBCConnection.getConnection();
        ArrayList <String> friendDetails = new ArrayList<String>();
        String friendVisibility = null;
        String html = "";
        friendDetails = jdbc.getMember(friendEmail);
        if (!friendDetails.isEmpty()) {
            friendVisibility = friendDetails.get(7);
            html = html + "<form action ='/main.html' method='post'>";
            switch (friendVisibility) {
                case "everyone":
                    int i = 1;
                    html = html + "<input type='text' id='friendsearchresult' name='friendsearchresult' value='" + friendDetails.get(0) + "' readonly>"  + "\n";
                    while (i < 6) {
                        html = html + friendDetails.get(i) + "\n";
                        i++;
                    }
                    html = html + "<button class='addfriend' type ='submit'>Add Friend</button> \n";
                    break;
                case "friends-only":
                    html = html + "<input type='text' id='friendsearchresult' name='friendsearchresult' value='" + friendDetails.get(0) + "' readonly>"  + "\n";
                    
                    html = html + "<button class='addfriend' type ='submit'>Add Friend</button> \n"; 
                    break;
                case "private":
                    html = html + "<input type='text' id='friendsearchresult' name='friendsearchresult' value='" + friendDetails.get(0) + "' readonly>"  + "\n";
                    html = html + "<button class='addfriend' type ='submit'>Add Friend</button> \n";
                    break;  
            }
            html = html + "</form>";
        }

        // Searches for user with same name
        return html;
    }
}