package app;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Reviews implements Handler{
    public static final String URL = "reviews";

    @Override
    public void handle(Context ctx) throws Exception {
        // TODO Auto-generated method stub
        MongoDBConnection mongodb = MongoDBConnection.getConnection();
        
        String html = "<html>\n";
        String user = "";

        user = Util.getLoggedInUser(ctx);
        String id = ctx.sessionAttribute("id"); 
        // Add some Header information
        html = html + "<head>" + "<title>Homepage</title>\n";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

        // Add the body
        html = html + "<body>\n";

        // Return to search page
        html = html + "<a href='/'>Return to Page</a>";
        
        html = html + "<h1>My reviews</h1>\n";
        if (user != null) {
            String reviews = mongodb.getReviews(id, null); 
            if (reviews != null && reviews != "") {
                html = html + reviews;
            }
            else {
                html = html + "<h2> You have no reviews</h2>\n";
            }
            String edit_hidden = ctx.formParam("edit_hidden");
            String delete_hidden = ctx.formParam("delete_hidden");
            String listing_hidden = ctx.formParam("listing_hidden");
            String editComment = ctx.formParam("editcomment_textbox");
            String reviewID = ctx.formParam("reviewid_hidden");

                if (edit_hidden != null && edit_hidden.equals("true")) {
                    mongodb.editReview(listing_hidden, id, editComment);
                    ctx.redirect("/reviews");
                }
                else if (delete_hidden != null && delete_hidden.equals("true")) {
                    mongodb.deleteReview(listing_hidden, id, reviewID);
                    ctx.redirect("/reviews");
                }
            }
        // Finish the HTML webpage
        html = html + "</body>" + "</html>\n";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        ctx.html(html);
    }
}
