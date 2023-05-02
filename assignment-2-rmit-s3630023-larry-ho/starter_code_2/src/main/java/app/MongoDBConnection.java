package app;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Projections.*;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;


import static com.mongodb.client.model.Filters.*;

public class MongoDBConnection {
   /** TODO update the DATABASE_URL value below 
    *  format: mongodb+srv://username:password@atlas.server.address/database
    *  Goto https://cloud.mongodb.com/ 
    *  click connect > connect your application > Driver Java > Version 4.3 or later
    *  copy connection string
    *  if you have time out issues (due to firewalls) choose verion 3.3 or earlier instead
    **/
   private static final String DATABASE_URL = "mongodb+srv://s3630023:doSRhmvOTatQOcVn@dba-cluster.caiiv.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
   static MongoClient client;
   MongoDatabase database;
   MongoCollection<Document> allListings;
   private static MongoDBConnection mongodb = null;

   public static MongoDBConnection getConnection(){
      //check that MongoDBConnection is available (if not establish)
      if(mongodb==null){
         mongodb = new MongoDBConnection();
      }
      return mongodb;
   }
   public MongoDBConnection() {
      System.out.println("Creating MongoDB Connection Object");
      
      try {
         client = MongoClients.create(DATABASE_URL);
         database = client.getDatabase("sample_airbnb");
         allListings = database.getCollection("listingsAndReviews");
         } catch (Exception e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }
   
   public static void closeConnection(){
      try {
         if (client != null) {
            client.close();
            System.out.println("Database Connection closed");
         }
      } catch (Exception e) {
         // connection close failed.
         System.err.println(e.getMessage());
      }
   }

   public ArrayList<String> getAllApartmentNames(){
      MongoCursor<Document> cursor = allListings.find(eq("property_type", "Apartment")).projection(fields(include("name"), exclude("_id"))).iterator();
       ArrayList<String> apartments = new ArrayList<String>();
       try {
          while (cursor.hasNext()) {
            apartments.add(cursor.next().get("name").toString());
          }
       }
       finally {
          cursor.close();
       }
       return apartments;
    }

   public ArrayList<String> getApartmentNamesByAccommodates(int accommodates){
     MongoCursor<Document> cursor = allListings.find(and(eq("accommodates", accommodates), eq("property_type", "Apartment"))).projection(fields(include("name", "address"), exclude("_id"))).iterator();
      ArrayList<String> apartments = new ArrayList<String>();
      try {
         while (cursor.hasNext()) {
            Document record = cursor.next();
            String name = record.get("name").toString();
            //String address = record.get("address").toString();
            apartments.add(name);
//            apartments.add(cursor.next().toJson());
         }
      }
      finally {
         cursor.close();
      }
      return apartments;
   }

   public String getAccommodationBySearch(String location, int accommodates, int bedrooms, int beds, String propertyType, List<String> amenities, 
   int price, int ratingscore, boolean superhost, String summary, int pageNo) {
      String html = "";

      int recordsPerPage = 10;
      int skip = 0;
      if (pageNo != 0) {
         skip = (pageNo-1) * recordsPerPage;
      }


      // Declare Bson variables
      Bson accommodateFilter = Filters.eq("accommodates", accommodates);
      Bson bedroomFilter = Filters.eq("bedrooms", bedrooms);
      Bson bedFilter = Filters.eq("beds", beds);
      Bson propertyFilter = Filters.eq("property_type", propertyType);
      Bson amenitiesFilter = Filters.all("amenities", amenities);
      Bson priceFilter = Filters.lte("price", price);
      Bson ratingFilter =  Filters.gte("review_scores.review_scores_rating", ratingscore);
      Bson superHostFilter = Filters.eq("host.host_is_superhost", superhost);
      Bson locationFilter = Filters.or(Filters.eq("address.country", location), Filters.eq("address.market", location));
      Bson summaryFilter = null;
      if (summary != null) {
         summaryFilter = Filters.regex("summary", summary);
      }
      // Check if any variables are empty
      if (accommodates == 0) {
         accommodateFilter = Filters.exists("accommodates", true);
      }
      if (bedrooms == 0) {
         bedroomFilter = Filters.exists("bedrooms", true);
      }
      if (beds == 0) {
         bedFilter = Filters.exists("beds", true);
      }
      if (propertyType == null) {
         propertyFilter = Filters.exists("property_type", true);
      }
      if (amenities.isEmpty()) {
         amenitiesFilter = Filters.exists("amenities", true);
      }
      if (price == 0) {
         priceFilter = Filters.or(Filters.exists("price", true));
      }
      if (ratingscore == 0) {
         ratingFilter =  Filters.or(Filters.exists("review_scores.review_scores_rating", true), (Filters.exists("review_scores.review_scores_rating", false)));
      }
      if (superhost == false) {
         superHostFilter = Filters.exists("host.host_is_superhost", true);
      }
      if (location == null) {
         locationFilter = Filters.or(Filters.exists("address.country", true), Filters.exists("address.market", true));
      }
      if (summary == null) {
         summaryFilter = Filters.exists("summary", true);
      }
      // Execute query
      MongoCursor<Document> cursor = allListings.find(Filters.and(accommodateFilter, bedroomFilter, bedFilter, propertyFilter, amenitiesFilter, 
                                                 priceFilter,ratingFilter, superHostFilter, locationFilter, summaryFilter
                                                ))
                                                .skip(skip)
                                                .limit(10)
                                                .projection(fields(include("name", "bedrooms", "beds", "price", "_id")))
                                                .iterator();
      try {
         while (cursor.hasNext()) {
            Document record = cursor.next();
            String id = record.get("_id").toString();
            String listingName = record.get("name").toString();
            String listingBedrooms = record.get("bedrooms").toString();
            String listingBeds = record.get("beds").toString();
            String listingPrice = record.get("price").toString();
            
            // Form for each accommodation searched
            html = html + "<div class='search-result'>\n";
            html = html + "<form action='/accommodation' method='get'>\n";
            html = html + "<input type='hidden' id='" + id + "' name='accommodation' value='" + id + "'><br>\n";
            html = html + listingName + "<br>\n";
            html = html + listingBedrooms + " bedroom(s)<br>\n";
            html = html + listingBeds + " bed(s)<br>\n";
            html = html + "$" + listingPrice + "<br>\n";
            html = html + "<button type='submit' name='view' class='btn btn-primary'>View</button>\n";
            html = html + "</form>\n";
            html = html + "</div>\n";
         }
      }
      finally {
         cursor.close();
      }
      return html;
   }
   
   public String getAccommodationDetails(String id){
      String html = "";
      MongoCursor<Document> cursor = allListings.find((eq("_id", id)))
                                                .projection(fields(include("name", "summary", "address", "review_scores", "price", "property_type",
                                                "amenities", "bedrooms", "accommodates", "host", "images", "reviews"), exclude("_id")))
                                                .iterator();

      try {
         while (cursor.hasNext()) {
            Document record = cursor.next();
            String name = record.get("name").toString();
            String summary = record.get("summary").toString();
            // Address
            Document address = (Document)record.get("address");
            String street = address.get("street").toString();
            String suburb = address.get("suburb").toString();
            String market = address.get("market").toString();
            String country = address.get("country").toString();
            Document reviewScore = (Document)record.get("review_scores");
            Object reviewScoreRatingObj = reviewScore.get("review_scores_rating");
            String reviewScoreRating = "";
            if (reviewScoreRatingObj != null) {
               reviewScoreRating = reviewScoreRatingObj.toString();
            }
            String price = record.get("price").toString();
            String property_type = record.get("property_type").toString();
            List<?> amenities = (List<?>)record.get("amenities");
            String bedrooms = record.get("bedrooms").toString();
            String accommodates = record.get("accommodates").toString();
            Document host = (Document)record.get("host");
            String hostName = host.get("host_name").toString();
            String hostAbout = host.get("host_about").toString();
            Object hostResponseTimeObj = host.get("host_response_time");
            String hostResponseTime = "";
            if (hostResponseTimeObj != null) {
               hostResponseTime = hostResponseTimeObj.toString();
            }
            String hostResponseRate = "";
            Object hostResponseRateObj = host.get("host_response_rate");
            if (hostResponseRateObj != null) {
               hostResponseRate = hostResponseRateObj.toString();
            }
            String hostSuperhost = host.get("host_is_superhost").toString();
            String hostIdentityVerify = host.get("host_identity_verified").toString();
            Document image = (Document)record.get("images");
            String imageURL = image.get("picture_url").toString();
            List<?> reviews = (List<?>)record.get("reviews");

            int i = 0;
            // Create HTML page with selected accommodation
            html = html + "<div id='accommodation'>\n";
            html = html + "<h1>" + name + "</h1>\n";
            html = html + "<div id='accommodation-image-div'>\n";
            html = html + "<image id='accommodation-image' src='" + imageURL + "' alt='accommodation' />\n";
            html = html + "</div>\n";
            html = html + "<h2>About the accommodation</h2>\n";
            html = html + summary + "<br>\n";
            html = html + "Located at " + street + "\n";
            html = html + suburb + "\n";
            html = html + market + "\n";
            html = html + country + "<br>\n";
            html = html + reviewScoreRating + " Rating Score<br>\n";
            html = html + "$" + price + " per night<br>\n";
            html = html + property_type + "\n";
            html = html + "<div id='amenities-list'>\n";
            html = html + "<h2>What this place offers</h2>\n";
            html = html + "<ul>\n";
            while (i < amenities.size()) {
               html = html + "<li>" + amenities.get(i) + "</li>\n";
               i++;
            }
            html = html + "</ul>\n";
            html = html + "</div>\n";
            html = html + "<h2>How many</h2>\n";
            html = html + bedrooms + " bedrooms\n";
            html = html + accommodates + " guests \n";
            html = html + "<div id='host'>\n";
            html = html + "<h2>Hosted by " + hostName + "</h2>\n";
            html = html + hostAbout + "<br>\n";
            if (hostResponseRate != null && hostResponseRate != "") {
               html = html + "Response rate: " + hostResponseRate + "%<br>\n";
            }
            if (hostResponseTime != null && hostResponseTime != "") {
               html = html + "Response time: " + hostResponseTime + "<br>\n";
            }
            if (hostSuperhost == "true") {
               html = html + "Superhost<br>\n";
            }
            if (hostIdentityVerify == "true") {
               html = html + "Identity verified<br>\n";
            }
            html = html + "</div>\n";
            // Reviews
            html = html + "<h2>Reviews</h2>\n";
            html = html + "</div>\n";
            i = 0;
            if (!reviews.isEmpty()) {
               while (i < reviews.size()) {
                  Document review = (Document)reviews.get(i);
                  String reviewDate = review.get("date").toString();
                  String reviewerName = review.get("reviewer_name").toString();
                  String comments = review.get("comments").toString();
                  html = html + "<div class='reviews'>\n";
                  html = html + "<b>" + reviewerName + "</b><br>\n";
                  html = html + reviewDate + "<br>\n";
                  html = html + comments + "\n";
                  html = html + "</div>\n";
                  i++;  
            }
         }
         }
      }
      finally {
         cursor.close();
      }
      return html;
   }
   public boolean getUser(String name, String id) {
      boolean match = false;
      MongoCursor<Document> cursor = allListings.find(Filters.and(Filters.eq("reviews.reviewer_id", id), eq("reviews.reviewer_name", name)))
                                                .projection(fields(include("reviews"),
                                                exclude("_id")))
                                                .iterator();
      try {
         while (cursor.hasNext()) {
            Document record = cursor.next();
            List<?> reviews = (List<?>)record.get("reviews");
            int i = 0;
            if (!reviews.isEmpty()) {
               while (i < reviews.size()) {
                  Document review = (Document)reviews.get(i);
                  String reviewerName = review.get("reviewer_name").toString();
                  String recordReviewerID = review.get("reviewer_id").toString();
                  if (recordReviewerID.equals(id) && reviewerName.equalsIgnoreCase(name)) {
                     return true;
                  }
                  i++;  
               }
            }

         }
      } finally {
         cursor.close();
      }
      return match;
   }

   public void addReview(String listingid, String reviewer_id, String reviewer_name, String comment) {
      // Current date in date format
      LocalDate date = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      date.format(formatter);
      ObjectId objId = new ObjectId();
      String id = objId.toString();
      Document review = new Document("_id", id)
                                    .append("date", date)
                                    .append("listing_id", listingid)
                                    .append("reviewer_id", reviewer_id)
                                    .append("reviewer_name", reviewer_name)
                                    .append("comments", comment); 

      allListings.updateOne(eq("_id", listingid),Updates.addToSet("reviews", review));                         
   }

   public String getReviews(String reviewer_id, String listing_id) {
      String html = "";
      String lastID = "";
      Bson idFilter;
      // Declare Bson variables
      if (lastID != null && lastID != "") {
         idFilter = Filters.gt("reviews._id", lastID);
      }
      else {
         idFilter = Filters.exists("reviews._id", true);
      }
      Bson listingidFilter = Filters.eq("reviews.listing_id", listing_id);
      if (listing_id == null) {
         listingidFilter = Filters.exists("reviews.listing_id", true);
      }
      MongoCursor<Document> cursor = allListings.find(Filters.and(idFilter, Filters.eq("reviews.reviewer_id", reviewer_id), listingidFilter))
                                                .limit(10)
                                                .projection(fields(include("reviews", "name"),
                                                exclude("_id")))
                                                .iterator();
      try {
         while (cursor.hasNext()) {
            Document record = cursor.next();
            List<?> reviews = (List<?>)record.get("reviews");
            int i = 0;
            if (!reviews.isEmpty()) {
               while (i < reviews.size()) {
                  Document review = (Document)reviews.get(i);
                  String recordReviewerID = review.get("reviewer_id").toString();
                  if (recordReviewerID.equalsIgnoreCase(reviewer_id)) {
                     String id = review.get("_id").toString();
                     String name = record.get("name").toString();
                     String reviewDate = review.get("date").toString();
                     String listingID = review.get("listing_id").toString();
                     String reviewerName = review.get("reviewer_name").toString();
                     String comments = review.get("comments").toString();
                     html = html + "<div class='reviews'>\n";
                     html = html + "<b>" + name + "</b> Review <br>\n";
                     html = html + "<b>" + reviewerName + "</b><br>\n";
                     html = html + reviewDate + "<br>\n";
                     html = html + comments + "\n";
                     // Edit button
                     html = html + "<form method='post'>\n";
                     html = html + "   <input type='hidden' id='edit_hidden' name='edit_hidden' value='true'>\n"; 
                     html = html + "    <input class='form-control' id='editcomment_textbox' name='editcomment_textbox' required>\n";
                     html = html + "   <input type='hidden' id='listing_hidden' name='listing_hidden' value='" + listingID + "'>\n"; 
                     html = html + "   <input type='hidden' id='reviewid_hidden' name='reviewid_hidden' value='" + id + "'>\n";
                     html = html + "   <button type='submit' class='btn btn-primary'>Edit review</button>\n";
                     html = html + "</form>\n";
                     // Delete button
                     html = html + "<form method='post'>\n";
                     html = html + "   <input type='hidden' id='delete_hidden' name='delete_hidden' value='true'>\n"; 
                     html = html + "   <input type='hidden' id='listing_hidden' name='listing_hidden' value='" + listingID + "'>\n"; 
                     html = html + "   <input type='hidden' id='reviewid_hidden' name='reviewid_hidden' value='" + id + "'>\n";
                     html = html + "   <button type='submit' class='btn btn-primary'>Delete review</button>\n";
                     html = html + "</form>\n";
                     html = html + "</div>\n";
                  }
                  i++;  
               }
            }
         }
      } finally {
         cursor.close();
      }
      return html;
   }
   public void deleteReview(String listingid, String reviewer_id, String reviewID) {
      Bson filter = and(eq("_id", listingid),eq("reviews.reviewer_id", reviewer_id));
      Bson update = Updates.pull("reviews", new Document("_id", reviewID));
      allListings.updateOne(filter, update);

   }

   public void editReview(String listingid, String reviewer_id, String comment) {
      LocalDate date = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      date.format(formatter);
 
      Bson filter = and(eq("_id", listingid),eq("reviews.reviewer_id", reviewer_id));
      Bson set1 = Updates.set("reviews.$.date", date);
      Bson set2 = Updates.set("reviews.$.comments", comment);

      Bson update = Updates.combine(set1, set2);

      allListings.updateOne(filter, update); 
   }
}

