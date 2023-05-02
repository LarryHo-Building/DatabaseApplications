/* DDL */
-- Generated by Oracle SQL Developer Data Modeler 21.2.0.183.1957
--   at:        2021-10-27 12:13:41 AEDT
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g



-- predefined type, no DDL - MDSYS.SDO_GEOMETRY

-- predefined type, no DDL - XMLTYPE

CREATE TABLE bid (
    bidno          INTEGER NOT NULL,
    amount         NUMBER,
    "date"         DATE,
    item_itemid    INTEGER NOT NULL,
    buyer_username VARCHAR2(50) NOT NULL
);

ALTER TABLE bid ADD CONSTRAINT bid_pk PRIMARY KEY ( bidno,
                                                    item_itemid );

CREATE TABLE business (
    businessno      INTEGER NOT NULL,
    address         VARCHAR2(50),
    seller_username VARCHAR2(50) NOT NULL
);

CREATE UNIQUE INDEX business__idx ON
    business (
        seller_username
    ASC );

ALTER TABLE business ADD CONSTRAINT business_pk PRIMARY KEY ( businessno );

CREATE TABLE buyer (
    username VARCHAR2(50) NOT NULL,
    address  VARCHAR2(50)
);

ALTER TABLE buyer ADD CONSTRAINT buyer_pk PRIMARY KEY ( username );

CREATE TABLE category (
    title          VARCHAR2(50) NOT NULL,
    category_title VARCHAR2(50) NOT NULL
)
/* Partition */
PARTITION BY LIST(category_title)
(
    PARTITION electronics VALUES ('robotics'),
    PARTITION book VALUES ('fiction'),
    PARTITION sports_and_fitness VALUES ('outdoor_equipment'),
    PARTITION outdoor_equipment VALUES ('climbing equipment', 'safety equipment'),
    PARTITION other_category VALUES(DEFAULT)
);

ALTER TABLE category ADD CONSTRAINT category_pk PRIMARY KEY ( title );

CREATE TABLE feedback (
    rating          INTEGER,
    "comment"       VARCHAR2(50),
    bid_bidno       INTEGER NOT NULL,
    bid_item_itemid INTEGER NOT NULL
);

ALTER TABLE feedback ADD CONSTRAINT feedback_pk PRIMARY KEY ( bid_bidno,
                                                              bid_item_itemid );

CREATE TABLE item (
    itemid          INTEGER NOT NULL,
    description     VARCHAR2(200),
    startdate       DATE,
    enddate         DATE,
    seller_username VARCHAR2(50) NOT NULL
);

ALTER TABLE item ADD CONSTRAINT item_pk PRIMARY KEY ( itemid );

CREATE TABLE itemcategory (
    item_itemid    INTEGER NOT NULL,
    category_title VARCHAR2(50) NOT NULL
);

ALTER TABLE itemcategory ADD CONSTRAINT itemcategory_pk PRIMARY KEY ( item_itemid,
                                                                      category_title );

CREATE TABLE seller (
    username VARCHAR2(50) NOT NULL,
    address  VARCHAR2(50)
);

ALTER TABLE seller ADD CONSTRAINT seller_pk PRIMARY KEY ( username );

ALTER TABLE bid
    ADD CONSTRAINT bid_buyer_fk FOREIGN KEY ( buyer_username )
        REFERENCES buyer ( username );

ALTER TABLE bid
    ADD CONSTRAINT bid_item_fk FOREIGN KEY ( item_itemid )
        REFERENCES item ( itemid );

ALTER TABLE business
    ADD CONSTRAINT business_seller_fk FOREIGN KEY ( seller_username )
        REFERENCES seller ( username );

ALTER TABLE category
    ADD CONSTRAINT category_category_fk FOREIGN KEY ( category_title )
        REFERENCES category ( title );

ALTER TABLE feedback
    ADD CONSTRAINT feedback_bid_fk FOREIGN KEY ( bid_bidno,
                                                 bid_item_itemid )
        REFERENCES bid ( bidno,
                         item_itemid );

ALTER TABLE item
    ADD CONSTRAINT item_seller_fk FOREIGN KEY ( seller_username )
        REFERENCES seller ( username );

ALTER TABLE itemcategory
    ADD CONSTRAINT itemcategory_category_fk FOREIGN KEY ( category_title )
        REFERENCES category ( title );

ALTER TABLE itemcategory
    ADD CONSTRAINT itemcategory_item_fk FOREIGN KEY ( item_itemid )
        REFERENCES item ( itemid );

/* Insert Data */
INSERT INTO seller VALUES ('Buzz', '234 Elm Street, Anaheim, CA, USA');
INSERT into item VALUES (1, 'New Metallic Toy rocket', TO_TIMESTAMP('2021-01-01 23:25:00','YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2021-01-02 23:25:00','YYYY-MM-DD HH24:MI:SS'), 'Buzz');
INSERT into category VALUES ('Diecast', 'Toys');
INSERT into category (title) VALUES ('Toys');
INSERT into itemcategory VALUES (1, 'Toys');
INSERT into business VALUES (123456, '1313 South Harbor Boulevard, Anaheim, CA, USA', 'Buzz');
INSERT into item (itemid, description, startdate, seller_username) VALUES (2, 'New copy of Twilight', TO_TIMESTAMP('2021-03-03 11:40:00','YYYY-MM-DD HH24:MI:SS'), 'Buzz');
INSERT into category (title) VALUES ('Book');
INSERT into category VALUES ('Fiction', 'Book');
INSERT into itemcategory VALUES (2, 'Book');
INSERT into seller VALUES ('Johnny5', '200 Station St, Sydney, NSW, Australia');
INSERT into item (itemid, description, seller_username) VALUES (3, 'Used Robot arm', 'Johnny5');
INSERT into category (title) VALUES ('Electronics');
INSERT into category VALUES ('Robotics', 'Electronics');
INSERT into itemcategory VALUES (3, 'Electronics');
INSERT into buyer VALUES ('JackD', '30 Rockefellar Plaza, New York, NY, USA');
INSERT into bid VALUES (1, 10.2, TO_TIMESTAMP('2021-04-01 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'JackD');
INSERT into buyer VALUES ('BillyBob', '123 Fake St, Melbourne, VIC, Australia');
INSERT into bid VALUES (2, 10.3, TO_TIMESTAMP('2021-04-01 13:30:00','YYYY-MM-DD HH24:MI:SS'), 3, 'BillyBob');
INSERT into bid VALUES (3, 100.3, TO_TIMESTAMP('2021-04-02 14:30:00','YYYY-MM-DD HH24:MI:SS'), 3, 'JackD');
INSERT into feedback VALUES (5, 'Excellent service and delivery', 3, 3);
INSERT into seller VALUES ('LaraC', '55 Ashbourne Rd, Derby, UK');
INSERT into item VALUES (4, 'Used Climbing Harness', TO_TIMESTAMP('2021-04-01 12:25:00','YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2021-05-01 12:25:00','YYYY-MM-DD HH24:MI:SS'), 'LaraC');
INSERT into category (title) VALUES ('Sport and Fitness');
INSERT into category VALUES ('Outdoor Equipment', 'Sport and Fitness');
INSERT into itemcategory VALUES (4, 'Sport and Fitness');
INSERT into category VALUES ('Climbing Equipment', 'Outdoor Equipment');
INSERT into category VALUES ('Safety Equipment', 'Outdoor Equipment');
INSERT into itemcategory VALUES (4, 'Outdoor Equipment');
INSERT into bid (bidno, amount, item_itemid, buyer_username) VALUES (1, 20.1, 4, 'JackD');
INSERT into buyer VALUES ('DannyB', '22 Full Crt, Frankston, VIC, Australia');
INSERT into seller VALUES ('HomerS', '742 Evergreen Terr, Springfield, OR, USA');
INSERT into business VALUES (55555, '10201 Pico Blvd, Los Angeles, CA, USA', 'HomerS');
INSERT into seller VALUES ('StewieG', '31 Spooner Street, Quahog, RI, USA');

/* Queries */
SELECT I.description, I.startdate, I.enddate, C.title, C.category_title 
FROM  item I INNER JOIN itemcategory IC
ON I.itemid = ic.item_itemid
INNER JOIN category C
ON IC.category_title = C.title;

SELECT i.description, b1.username, b2.amount
FROM buyer b1 INNER JOIN bid b2
ON b1.username = b2.buyer_username
INNER JOIN item i
ON b2.item_itemid = i.itemid
WHERE b2.amount < 50;

SET TRANSACTION NAME 'bid';
INSERT INTO bid VALUES (1, 200, CURRENT_TIMESTAMP, 2, "DannyB");
UPDATE item SET enddate = CURRENT_TIMESTAMP WHERE itemid = 2;
ROLLBACK;
/* Indexes */
CREATE INDEX itemcategory_index
ON itemcategory(item_itemid, category_title);

CREATE INDEX category_index
ON category(title);

CREATE INDEX bidamount_index
ON bid(amount);

/* Trigger */
CREATE OR REPLACE TRIGGER highest_bid
AFTER INSERT ON bid FOR EACH ROW
BEGIN
    IF :NEW.amount > :OLD.amount THEN
        INSERT INTO bid(amount)
        VALUES (:NEW.amount);
    END IF;
END;

CREATE OR REPLACE TRIGGER end_date
AFTER INSERT ON item FOR EACH ROW
BEGIN
    IF :OLD.enddate > CURRENT_TIMESTAMP THEN
        DELETE FROM bid
        WHERE amount = :OLD.amount; 
    END IF;
END;

/* Stored Procedure */
CREATE OR REPLACE PROCEDURE winner (itemid IN item.itemid%type)
AS
    winning_username VARCHAR(50);
    winning_amount NUMBER;
    auction_status VARCHAR(20);
BEGIN
    SELECT max(bi.amount) INTO winning_amount
    FROM bid bi
    WHERE bi.item_itemid = itemid;
    
    SELECT bi.buyer_username INTO winning_username
    FROM bid bi
    WHERE bi.amount = winning_amount;
    
    DBMS_OUTPUT.PUT_LINE('Winner:');
    DBMS_OUTPUT.PUT_LINE(winning_username);
    DBMS_OUTPUT.PUT_LINE('Amount:');
    DBMS_OUTPUT.PUT_LINE(winning_amount);
    
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('None');
            DBMS_OUTPUT.PUT_LINE('0');
END;

EXEC winner(3);
/* View */
CREATE VIEW AuctionView AS
SELECT b.username AS "Buyer Username", b.address AS "Buyer Address", bid.bidno AS "Bid No", bid.amount AS "Bid Amount", 
bid."date" AS "Bid Date", f.rating AS "Rating", f."comment" AS "Comment", i.itemid AS "Item ID", i.description AS "Description", i.startdate AS "Item Start Date", i.enddate AS "Item End Date", 
ic.category_title AS "Item Category Title", c.category_title AS "Item Sub Category Title", s.username AS "Seller Username", s.address AS "Seller Address", sb.businessno AS "Seller Business No", 
sb.address AS "Seller Business Address"
FROM seller s
LEFT JOIN business sb
ON s.username = sb.seller_username
LEFT JOIN item i
ON s.username = i.seller_username
LEFT JOIN itemcategory ic
ON i.itemid = ic.item_itemid
LEFT JOIN category c
ON ic.category_title = c.title
LEFT JOIN bid
ON i.itemid = bid.item_itemid
LEFT JOIN feedback f
ON bid.bidno = f.bid_bidno
LEFT OUTER JOIN buyer b
ON bid.buyer_username = b.username
ORDER BY i.itemid, bid.bidno ASC;