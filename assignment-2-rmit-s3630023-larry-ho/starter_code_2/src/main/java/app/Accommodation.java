package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Accommodation implements Handler{

    public static final String URL = "accommodation";

    @Override
    public void handle(Context ctx) throws Exception {
        MongoDBConnection mongodb = MongoDBConnection.getConnection();
        // TODO Auto-generated method stub
        String html = "<html>\n";
        String user = "";


        String userID = ctx.cookie("id");
        String review_hidden = ctx.formParam("review_hidden");
        String comments = ctx.formParam("comment_textbox");

        user = Util.getLoggedInUser(ctx);
        // Add some Header information
        html = html + "<head>" + "<title>Accommodation</title>\n";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

        // Add the body
        html = html + "<body>\n";

        user = Util.getLoggedInUser(ctx);
        // Return to search page
        html = html + "<a href='/'>Return to Page</a>";

        // Gets id from accommodation selected
        String id = ctx.queryParam("accommodation");

        String accommodationgetURL = "/accommodation?accommodation=" + id + "&view=";

        html = html + mongodb.getAccommodationDetails(id);

        if (user != null) {
            html = html + "<form method='post' action='" + accommodationgetURL + "'>\n";
            html = html + "   <div class='form-group'>\n";
            html = html + "      <label for='review_textbox'>Write Review</label>\n";
            html = html + "      <input class='form-control' id='comment_textbox' name='comment_textbox' required>\n";
            html = html + "   </div>\n";
            html = html + "   <input type='hidden' id='review_hidden' name='review_hidden' value='true'>\n"; 
            html = html + "   <button type='submit' class='btn btn-primary'>Post review</button>\n";
            html = html + "</form>\n";

            html = html + "<div id='review-div'>\n";
            String userReview = mongodb.getReviews(userID, id);
            if (userReview != null && userReview != "") {
                html = html + "<div id='modify-review'>\n";
                html = html + "<h2>Your Review</h2>\n";
                html = html + userReview;
                html = html + "</div>\n";
            }
            html = html + "</div>\n";
            String edit_hidden = ctx.formParam("edit_hidden");
            String delete_hidden = ctx.formParam("delete_hidden");
            String listing_hidden = ctx.formParam("listing_hidden");
            String editComment = ctx.formParam("editcomment_textbox");
            String reviewID = ctx.formParam("reviewid_hidden");

            if (edit_hidden != null && edit_hidden.equals("true")) {
                mongodb.editReview(listing_hidden, userID, editComment);
                ctx.redirect(accommodationgetURL);
            }
            else if (delete_hidden != null && delete_hidden.equals("true")) {
                mongodb.deleteReview(listing_hidden, userID, reviewID);
                ctx.redirect(accommodationgetURL);
            }
        }
        if (review_hidden != null && review_hidden.equals("true")){
            mongodb.addReview(id, userID, user, comments);
            ctx.redirect(accommodationgetURL);
        }
        // Finish the HTML webpage
        html = html + "</body>" + "</html>\n";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        ctx.html(html);
    }
    
}
