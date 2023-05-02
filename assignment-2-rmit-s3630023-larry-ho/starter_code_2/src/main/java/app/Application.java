package app;

import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Application implements Handler {
    public static final String URL = "/";
    @Override
    public void handle(Context ctx) throws Exception {
        // TODO Login function
        MongoDBConnection mongodb = MongoDBConnection.getConnection();
        
        String html = "<html>\n";
        String user = "";

        // Add some Header information
        html = html + "<head>" + "<title>Homepage</title>\n";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

        // Add the body
        html = html + "<body>\n";

        // Add HTML for the logo.png image
        html = html + "<div class='logo'>\n";
        html = html + "<img id='airbnb' src='logo.png' height='200px'/>\n";
        html = html + "</div>\n";

        user = Util.getLoggedInUser(ctx);
        //display login form if user is not logged in
        if (user == null) {
         // get username and password (and hidden field to identify login button pressed)
         String name_textbox = ctx.formParam("name_textbox");
         String id_textbox = ctx.formParam("id_textbox");
         String login_hidden = ctx.formParam("login_hidden");
         
        if (name_textbox == null){
            name_textbox = "";
        }
        html = html + "<div class='login-group'>\n";
         html = html + "<form action='/' method='post'>\n";
         html = html + "   <div class='login-form'>\n";
         html = html + "      <label for='name_textbox'>Name</label>\n";
         html = html + "      <input class='form-control' id='username_textbox' name='name_textbox' value='" + name_textbox + "'>\n";
         html = html + "   </div>\n";
         html = html + "   <div class='login-form'>\n";
         html = html + "      <label for='id_textbox'>ID</label>\n";
         html = html + "      <input class='form-control' id='id_textbox' name='id_textbox'>\n";
         html = html + "   </div>\n";
         html = html + "<div class='submit-button'>\n";
         html = html + "   <input type='hidden' id='login_hidden' name='login_hidden' value='true'>"; 
         html = html + "   <button type='submit' class='btn btn-primary'>Login</button>\n";
         html = html + "</form>\n";
         html = html + "</div>\n";
         html = html + "</div>\n";
         // if login button pressed proceess username and password fields
         if(login_hidden != null && login_hidden.equals("true")){
            if (name_textbox == null || name_textbox == "" || id_textbox == null || id_textbox == "") {
               // If username or password NULL/empty, prompt for authorisation details
               html = html + "Enter a name and id\n";
            } else {
               // If NOT NULL, then test password for match against array of users (in your code you will run a query)!
               if(checkUser(ctx, name_textbox, id_textbox)) {
                  //matching password found, reload page
                  ctx.redirect("/");
               } else {
                  // no password match found 
                  html = html + "invalid name/id";
               }
            }
         }
      }
      else {
         // user is logged in - check if logout button pressed
         String logout_hidden = ctx.formParam("logout_hidden");

         if(logout_hidden != null && logout_hidden.equals("true")){
            // logout clicked
            logout(ctx);
         } else {
            // logout not clicked - show logout button
            html = html + "<div class='login-group'>\n";
            html = html + "<div>Welcome, " + user + "</div>";
            html = html + "<a href='/reviews'>My Reviews</a>";
            html = html + "<form action='/' method='post'>\n";
            html = html + "   <input type='hidden' id='logout_hidden' name='logout_hidden' value='true'>"; 
            html = html + "   <button type='submit' class='btn btn-primary'>Logout</button>\n";
            html = html + "</form>\n";
            html = html + "</div>\n";
            }
        }
        int bedrooms = 0;
        int beds = 0;
        int accommodates = 0;
        int price = 0;
        int rating = 0;
        boolean superhost;

        String location_textbox = ctx.queryParam("location_textbox");
        String accommodate_textbox = ctx.queryParam("accommodate_textbox");
        String bedroom_textbox = ctx.queryParam("bedroom_textbox");
        String bed_textbox = ctx.queryParam("bed_textbox");
        String property_textbox = ctx.queryParam("property_textbox");
        List<String> amenitiesCheckbox = ctx.queryParams("amenities");
        String superhost_checkbox = ctx.queryParam("superhost_checkbox");
        String price_textbox = ctx.queryParam("price_textbox");
        String rating_textbox = ctx.queryParam("rating_textbox");
        String summary_textbox = ctx.queryParam("summary_textbox");
        String search_hidden = ctx.queryParam("search_hidden");

        if (location_textbox == null) {
            location_textbox = "";
        }
        if (property_textbox == null) {
            property_textbox = "";
        }
        if (summary_textbox == null) {
            summary_textbox = "";
        }
        html = html + "<h1>Search for places</h1>\n";
        // Form to enter criteria for accommodation
        html = html + "<div id='search'>\n";
        html = html + "<form action='/' method='get'>\n";
        // Location
        html = html + "<div class='searchrow'>\n";
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='location_textbox'>Location</label>\n";
        html = html + "      <input class='form-control' id='location_textbox' name='location_textbox' placeholder='Where are you going?' value='" + location_textbox + "'>\n";
        html = html + "   </div>\n";
        // Accomodates
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='accommodate_textbox'>Guests</label>\n";
        html = html + "      <input class='form-control' type='number' id='accommodate_textbox' name='accommodate_textbox' placeholder='Add Guests' value='" + accommodate_textbox + "'>\n";
        html = html + "   </div>\n";
        // Bedrooms
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='bedroom_textbox'>Bedrooms</label>\n";
        html = html + "      <input class='form-control' type='number' id='bedroom_textbox' name='bedroom_textbox' value='" + bedroom_textbox + "'>\n";
        html = html + "   </div>\n";
        // Beds
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='bed_textbox'>Beds</label>\n";
        html = html + "      <input class='form-control' type='number' id='bed_textbox' name='bed_textbox' value='" + bed_textbox + "'>\n";
        html = html + "   </div>\n";
        // Property Type
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='property_textbox'>Property Type</label>\n";
        html = html + "      <input class='form-control' id='property_textbox' name='property_textbox' value='" + property_textbox + "'>\n";
        html = html + "   </div>\n";
        html = html + "</div>\n";
        // Available Amenities
        html = html + "   <div class='form-group'>\n";
        html = html + "     <div id='amenities_list'>\n";
        html = html + "      <label for='amenities_textbox'>Amenities</label><br>\n";
        html = html + "      <input type='checkbox' id='TV' name='amenities' value='TV'>\n";
        html = html + "      <label for='TV'>TV</label>\n";
        html = html + "      <input type='checkbox' id='cableTV' name='amenities' value='Cable TV'>\n";
        html = html + "      <label for='cableTV'>Cable TV</label>\n";
        html = html + "      <input type='checkbox' id='internet' name='amenities' value='Internet'>\n";
        html = html + "      <label for='internet'>Internet</label>\n";
        html = html + "      <input type='checkbox' id='wifi' name='amenities' value='Wifi'>\n";
        html = html + "      <label for='wifi'>Wifi</label>\n";
        html = html + "      <input type='checkbox' id='airconditioning' name='amenities' value='Air conditioning'>\n";
        html = html + "      <label for='airconditioning'>Air conditioning</label>\n";
        html = html + "      <input type='checkbox' id='kitchen' name='amenities' value='Kitchen'>\n";
        html = html + "      <label for='kitchen'>Kitchen</label>\n";
        html = html + "      <input type='checkbox' id='paidparking' name='amenities' value='Paid parking off premises'>\n";
        html = html + "      <label for='paidparking'>Paid parking off premises</label>\n";
        html = html + "      <input type='checkbox' id='smoking' name='amenities' value='Smoking allowed'>\n";
        html = html + "      <label for='smoking'>Smoking allowed</label><br>\n";
        html = html + "      <input type='checkbox' id='doorman' name='amenities' value='Doorman'>\n";
        html = html + "      <label for='doorman'>Doorman</label>\n";
        html = html + "      <input type='checkbox' id='elevator' name='amenities' value='Elevator'>\n";
        html = html + "      <label for='elevator'>Elevator</label>\n";
        html = html + "      <input type='checkbox' id='intercom' name='amenities' value='Buzzer/wireless intercom'>\n";
        html = html + "      <label for='intercom'>Buzzer/wireless intercom</label>\n";
        html = html + "      <input type='checkbox' id='family' name='amenities' value='Family/kid friendly'>\n";
        html = html + "      <label for='family'>Family/kid friendly</label>\n";
        html = html + "      <input type='checkbox' id='washer' name='amenities' value='Washer'>\n";
        html = html + "      <label for='washer'>Washer</label>\n";
        html = html + "      <input type='checkbox' id='fireextinguisher' name='amenities' value='Fire extinguisher'>\n";
        html = html + "      <label for='fireextinguisher'>Fire extinguisher</label><br>\n";
        html = html + "      <input type='checkbox' id='essentials' name='amenities' value='Essentials'>\n";
        html = html + "      <label for='essentials'>Essentials</label>\n";
        html = html + "      <input type='checkbox' id='hangers' name='amenities' value='Hangers'>\n";
        html = html + "      <label for='kitchen'>Hangers</label>\n";
        html = html + "      <input type='checkbox' id='hairdryer' name='amenities' value='Hair dryer'>\n";
        html = html + "      <label for='hairdryer'>Hair dryer</label>\n";
        html = html + "      <input type='checkbox' id='iron' name='amenities' value='Iron'>\n";
        html = html + "      <label for='iron'>Iron</label>\n";
        html = html + "      <input type='checkbox' id='laptop' name='amenities' value='Laptop friendly workspace'>\n";
        html = html + "      <label for='laptop'>Laptop friendly workspace</label>\n";
        html = html + "      <input type='checkbox' id='hotwater' name='amenities' value='Hot water'>\n";
        html = html + "      <label for='hotwater'>Hot water</label>\n";
        html = html + "      <input type='checkbox' id='bedlinens' name='amenities' value='Bed linens'>\n";
        html = html + "      <label for='bedlinens'>Bed linens</label><br>\n";
        html = html + "      <input type='checkbox' id='extrapillowsblankets' name='amenities' value='Extra pillows and blankets'>\n";
        html = html + "      <label for='extrapillowsblankets'>Extra pillows and blankets</label>\n";
        html = html + "      <input type='checkbox' id='microwave' name='amenities' value='Microwave'>\n";
        html = html + "      <label for='microwave'>Microwave</label>\n";
        html = html + "      <input type='checkbox' id='coffeemaker' name='amenities' value='Coffee maker'>\n";
        html = html + "      <label for='coffeemaker'>Coffee maker</label>\n";
        html = html + "      <input type='checkbox' id='refrigerator' name='amenities' value='Refrigerator'>\n";
        html = html + "      <label for='refrigerator'>Refrigerator</label>\n";
        html = html + "      <input type='checkbox' id='dishessilverware' name='amenities' value='Dishes and silverware'>\n";
        html = html + "      <label for='dishessilverware'>Dishes and silverware</label>\n";
        html = html + "      <input type='checkbox' id='oven' name='amenities' value='Oven'>\n";
        html = html + "      <label for='oven'>Oven</label><br>\n";
        html = html + "      <input type='checkbox' id='stove' name='amenities' value='Stove'>\n";
        html = html + "      <label for='stove'>Stove</label>\n";
        html = html + "      <input type='checkbox' id='longstay' name='amenities' value='Long term stays allowed'>\n";
        html = html + "      <label for='longstay'>Long term stays allowed</label>\n";
        html = html + "      <input type='checkbox' id='widehallway' name='amenities' value='Wide hallway clearance'>\n";
        html = html + "      <label for='widehallway'>Wide hallway clearance</label>\n";
        html = html + "      <input type='checkbox' id='hostgreet' name='amenities' value='Host greets you'>\n";
        html = html + "      <label for='hostgreet'>Host greets you</label>\n";
        html = html + "</div>\n";
        html = html + "   </div>\n";
        // Maximum Price
        html = html + "<div class='searchrow'>\n";
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='price_textbox'>Price</label>\n";
        html = html + "      <input class='form-control' type='number' id='price_textbox' name='price_textbox' value='" + price_textbox + "'>\n";
        html = html + "   </div>\n";
        // Minimum Review Score Rating
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='rating_textbox'>Rating</label>\n";
        html = html + "      <input class='form-control' type='number' id='rating_textbox' name='rating_textbox' min='0' max='100' value='" + rating_textbox + "'>\n";
        html = html + "   </div>\n";
        // Host Status
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='superhost'>Only show superhosts?</label>\n";
        html = html + "      <input class='form-control' type='checkbox' id='superhost_checkbox' name='superhost_checkbox' value='true'>\n";
        html = html + "   </div>\n";
        // Summary keyword match
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='summary_textbox'>Summary</label>\n";
        html = html + "      <input class='form-control' id='summary_textbox' name='summary_textbox' placeholder='Describe your ideal experience' value='" + summary_textbox + "'>\n";
        html = html + "   </div>\n";
        html = html + "</div>\n";
        // End form
        html = html + "<div id='search_button'>\n";
        html = html + "   <input type='hidden' id='search_hidden' name='search_hidden' value='true'>"; 
        html = html + "   <button type='submit' class='btn btn-primary'>Search</button>\n";
        html = html + "</form>\n";
        html = html + "</div>\n";
        html = html + "</div>\n";
        String pageno_textbox = ctx.formParam("pageno_textbox");

        // Turn string into integers
        if (accommodate_textbox != null && accommodate_textbox != "") {
            accommodates = Integer.parseInt(accommodate_textbox);
        }
        if (bedroom_textbox != null && bedroom_textbox != "") {
            bedrooms = Integer.parseInt(bedroom_textbox);
        }
        if (bed_textbox != null && bed_textbox != "") {
            beds = Integer.parseInt(bed_textbox);
        }
        if (price_textbox != null && price_textbox != "") {
            price = Integer.parseInt(price_textbox);
        }
        if (rating_textbox != null && rating_textbox != "") {
            rating = Integer.parseInt(rating_textbox);
        }
        // Change into null if empty values
        if (location_textbox == "") {
            location_textbox = null;
        }
        if (property_textbox == "") {
            property_textbox = null;
        }
        if (summary_textbox == "") {
            summary_textbox = null;
        }
        superhost = Boolean.parseBoolean(superhost_checkbox);

        if(search_hidden != null && search_hidden.equals("true")){
            html = html + "<h2>Results: </h2>\n";
            html = html + "<div class='search-results-div'>\n";
            String results = mongodb.getAccommodationBySearch(location_textbox, accommodates, bedrooms, beds, property_textbox, amenitiesCheckbox, price, rating, superhost, summary_textbox, 0);
            if (results != null && results != "") {
                html = html + results;
                html = html + "</div>\n";
                html = html + "<div id='select-page'>\n";
                html = html + "<form action='/' method='post'>\n";
                html = html + "   <div class='form-group'>\n";
                html = html + "      <label for='pageNo'>Enter Page Number</label>\n";
                html = html + "      <input class='form-control' id='pageno_textbox' name='pageno_textbox'>\n";
                html = html + "   </div>\n";
                html = html + "   <button type='submit' class='btn btn-primary'>Submit</button>\n";
                html = html + "</form>\n";
                html = html + "</div>\n";
            }
            else {
                html = html + "<h2>No results</h2>\n";
            }
        }
        else if (pageno_textbox != null && pageno_textbox != "") {
            int pageNo =Integer.parseInt(pageno_textbox);
            html = html + "<h2>Results: </h2>\n";
            html = html + "<div class='search-results-div'>\n";
            String results = mongodb.getAccommodationBySearch(location_textbox, accommodates, bedrooms, beds, property_textbox, amenitiesCheckbox, price, rating, superhost, summary_textbox, pageNo);
            if (results != null && results != "") {
                html = html + results;
                html = html + "</div>\n";
                html = html + "<div id='select-page'>\n";
                html = html + "<form action='/' method='post'>\n";
                html = html + "   <div class='form-group'>\n";
                html = html + "      <label for='pageNo'>Enter Page Number</label>\n";
                html = html + "      <input class='form-control' id='pageno_textbox' name='pageno_textbox'>\n";
                html = html + "   </div>\n";
                html = html + "   <button type='submit' class='btn btn-primary'>Submit</button>\n";
                html = html + "</form>\n";
                html = html + "</div>\n";
            }
            else {
                html = html + "<h2>No more results</h2>\n";
                html = html + results;
                html = html + "</div>\n";
                html = html + "<div id='select-page'>\n";
                html = html + "<form action='/' method='post'>\n";
                html = html + "   <div class='form-group'>\n";
                html = html + "      <label for='pageNo'>Enter Page Number</label>\n";
                html = html + "      <input class='form-control' id='pageno_textbox' name='pageno_textbox'>\n";
                html = html + "   </div>\n";
                html = html + "   <button type='submit' class='btn btn-primary'>Submit</button>\n";
                html = html + "</form>\n";
                html = html + "</div>\n";
            }

    }

        // Finish the HTML webpage
        html = html + "</body>" + "</html>\n";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        ctx.html(html);
    }
    public boolean checkUser(Context ctx, String name, String id){
        MongoDBConnection mongodb = MongoDBConnection.getConnection();
        boolean passwordMatchFound = false;
        // 
        passwordMatchFound = mongodb.getUser(name, id);
              // match found - login the user
        if (passwordMatchFound == true) {
            ctx.sessionAttribute(id, name);
            ctx.sessionAttribute("id", id); 
            ctx.cookie("id",id);
        }
        return passwordMatchFound;
     }
    public void logout(Context ctx) {
        String id = ctx.cookie("id");
        ctx.sessionAttribute(id);
        ctx.removeCookie("id");
        ctx.sessionAttribute(id, null);
        // reload the page
        ctx.redirect("/");
     }
}