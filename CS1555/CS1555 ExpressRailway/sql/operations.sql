-- 1. Passenger Service Operations

---- 1.1. Update Customer List

------ 1.1.1. Customer Form

-------- Add Customer
DROP FUNCTION IF EXISTS add_customer(f_name VARCHAR, l_name VARCHAR, st VARCHAR, twn VARCHAR, zip_code VARCHAR);
CREATE OR REPLACE FUNCTION add_customer(f_name VARCHAR(20), l_name VARCHAR(20), st VARCHAR(100), twn VARCHAR(100), zip_code VARCHAR(20))
RETURNS VOID
AS $$
DECLARE
  num_customers INTEGER;
  new_customer_id INTEGER;
BEGIN
  num_customers = (SELECT COUNT(*) FROM Customers);
  IF num_customers = 0 THEN
      new_customer_id = 100000;
  ELSE
      new_customer_id = (SELECT MAX(customerID) FROM Customers) + 1;
  END IF;
  INSERT INTO Customers
    VALUES(new_customer_id, f_name, l_name, st, twn, zip_code);
  RETURN;
END;
$$ LANGUAGE plpgsql;

-------- Edit Customer

CREATE OR REPLACE FUNCTION edit_customer(customer_id INTEGER, to_edit VARCHAR(100), substitute_value VARCHAR(100))
RETURNS VOID
AS $$
BEGIN
    IF (SELECT customerID FROM Customers WHERE customerID = customer_id) IS NOT NULL THEN
        IF to_edit = 'firstName' THEN
            UPDATE Customers
            SET firstName = substitute_value
            WHERE customerID = customer_id;
        ELSIF to_edit = 'lastName' THEN
            UPDATE Customers
            SET lastName = substitute_value
            WHERE customerID = customer_id;
        ELSIF to_edit = 'street' THEN
            UPDATE Customers
            SET street = substitute_value
            WHERE customerID = customer_id;
        ELSIF to_edit = 'town' THEN
            UPDATE Customers
            SET town = substitute_value
            WHERE customerID = customer_id;
        ELSIF to_edit = 'zip' THEN
            UPDATE Customers
            SET zip = substitute_value
            WHERE customerID = customer_id;
        END IF;
    ELSE
        RAISE NOTICE 'The given customer ID is not in the database';
    END IF;
    RETURN;
END;
$$ LANGUAGE plpgsql;

-------- View Customer

CREATE OR REPLACE FUNCTION view_customer(customer_id INTEGER) RETURNS SETOF Customers
AS $$
BEGIN
    RETURN QUERY
    SELECT *
    FROM Customers
    WHERE customerID = customer_id;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM view_customer(1);

---- 1.2. Finding a Trip Between Two Stations

------ 1.2.1. Single Route Trip Search

CREATE OR REPLACE FUNCTION single_route_search(arrival INTEGER, dest INTEGER, day_choice VARCHAR(9))
RETURNS SETOF type_121
AS $$
BEGIN
    RETURN QUERY
    SELECT r.routeid, r.station, r.stop, big.trainid, big.cost, (EXTRACT(EPOCH FROM big.time::TIME)/60)::INT as time, big.day
    FROM Routes r JOIN
      (SELECT Result_3.routeID, Result_3.start, Result_3.end, Result_3.trainID, seats, Trains.cost as cost, Result_3.time as time, Result_3.day as day
    FROM
        (SELECT Result_1.routeID, Result_1.stop_order as start, Result_1.trainID, Result_2.stop_order as end, Result_1.time as time, Result_1.day as day
         FROM (SELECT Routes.routeID, station, stop, stop_order, day, time, trainID
               FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
               WHERE day = day_choice AND stop = arrival) AS Result_1
         JOIN
              (SELECT Routes.routeID, station, stop, stop_order, day, time, trainID
               FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
               WHERE day = day_choice AND stop = dest) AS Result_2
         ON Result_1.routeID = Result_2.routeID
         WHERE Result_1.trainID = Result_2.trainID AND Result_1.stop_order < Result_2.stop_order) AS Result_3
    JOIN Trains ON Result_3.trainID = Trains.trainID
    WHERE seats > 0) AS big
      ON r.routeid = big.routeid
      WHERE r.stop_order >= big.start AND r.stop_order <= big.end;
END;
$$ LANGUAGE plpgsql;

-- single route search different version but needed; this is same as above one
-- only difference is that below function exclude only arrival station. (for calculating time and distance you don't include first beginning station)

CREATE OR REPLACE FUNCTION single_route_searchB(arrival INTEGER, dest INTEGER, day_choice VARCHAR(9))
RETURNS SETOF type_121_b
AS $$
BEGIN
    RETURN QUERY
    SELECT r.routeid, r.station, r.stop, big.trainid, big.cost, (EXTRACT(EPOCH FROM big.time::TIME)/60)::INT as time, big.day, r.next_order
    FROM Routes r JOIN
      (SELECT Result_3.routeID, Result_3.start, Result_3.end, Result_3.trainID, seats, Trains.cost as cost, Result_3.time as time, Result_3.day as day, Result_3.next_order
    FROM
        (SELECT Result_1.routeID, Result_1.stop_order as start, Result_1.trainID, Result_2.stop_order as end, Result_1.time as time, Result_1.day as day, Result_1.next_order
         FROM (SELECT Routes.routeID, station, stop, stop_order, day, time, trainID, routes.next_order
               FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
               WHERE day = day_choice AND stop = arrival) AS Result_1
         JOIN
              (SELECT Routes.routeID, station, stop, stop_order, day, time, trainID
               FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
               WHERE day = day_choice AND stop = dest) AS Result_2
         ON Result_1.routeID = Result_2.routeID
         WHERE Result_1.trainID = Result_2.trainID AND Result_1.stop_order < Result_2.stop_order) AS Result_3
    JOIN Trains ON Result_3.trainID = Trains.trainID
    WHERE seats > 0) AS big
      ON r.routeid = big.routeid
      WHERE r.stop_order > big.start AND r.stop_order <= big.end;
END;
$$ LANGUAGE plpgsql;

-- Sort Options

---- 1.2.4.1. Fewest Stops

CREATE OR REPLACE FUNCTION sort_fewest_stops(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
BEGIN
  RETURN QUERY
    SELECT  route_id, sum( case when stop_id != -1 then 1 else 0 end) as number_of_stops FROM single_route_search(arrival, dest, day)
    GROUP BY route_id
    ORDER BY number_of_stops ASC;
END;
$$ LANGUAGE plpgsql;

---- 1.2.4.2. Runs Through Most Stations

CREATE OR REPLACE FUNCTION sort_most_stations(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
 BEGIN
   RETURN QUERY
    SELECT distinct route_id, count(distinct station_id) as number_of_stations FROM single_route_search(arrival, dest, day)
    GROUP BY route_id
    ORDER BY number_of_stations DESC;
  END;
$$ LANGUAGE plpgsql;

---- 1.2.4.3. Lowest Price

CREATE OR REPLACE FUNCTION sort_lowest_price(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
BEGIN
    RETURN QUERY
    SELECT distinct res.route_id, res.cost * sum(raillines.distance) as total_price
    FROM raillines INNER JOIN (SELECT distinct t.station_id, t.route_id, t.next_order, t.cost FROM single_route_searchB(arrival, dest, day) as t) as res
    ON raillines.station =  res.station_id AND raillines.next_order = res.next_order
    GROUP BY res.route_id, res.cost
    ORDER BY total_price ASC;
END;
$$ LANGUAGE plpgsql;

---- 1.2.4.4. Highest Price

CREATE OR REPLACE FUNCTION sort_highest_price(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
BEGIN
    RETURN QUERY
    SELECT distinct res.route_id, res.cost * sum(raillines.distance) as total_price
    FROM raillines INNER JOIN (SELECT distinct t.station_id, t.route_id, t.next_order, t.cost FROM single_route_searchB(arrival, dest, day) as t) as res
    ON raillines.station =  res.station_id AND raillines.next_order = res.next_order
    GROUP BY res.route_id, res.cost
    ORDER BY total_price DESC;
END;
$$ LANGUAGE plpgsql;

---  1.2.4.5 Least Total Time

CREATE OR REPLACE FUNCTION sort_least_time(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
  BEGIN
    RETURN QUERY
    SELECT distinct res.route_id, res.minutes + sum(stations.stopdelay)  as duration
    FROM stations INNER JOIN (SELECT distinct t.station_id, t.route_id, t.minutes FROM single_route_searchB(arrival, dest, day) as t) as res ON stations.stationid =  res.station_id
    GROUP BY res.route_id, res.minutes
    ORDER BY duration ASC;
  END;
$$ LANGUAGE plpgsql;

--- 1.2.4.6 Most Total Time

CREATE OR REPLACE FUNCTION sort_most_time(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
BEGIN
    RETURN QUERY
    SELECT distinct res.route_id, res.minutes + sum(stations.stopdelay)  as duration
    FROM stations INNER JOIN (SELECT distinct t.station_id, t.route_id, t.minutes FROM single_route_searchB(arrival, dest, day) as t) as res ON stations.stationid =  res.station_id
    GROUP BY res.route_id, res.minutes
    ORDER BY duration DESC;
END;
$$ LANGUAGE plpgsql;

---- 1.2.4.7. Least Total Distance

CREATE OR REPLACE FUNCTION sort_least_distance(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
BEGIN
    RETURN QUERY
    SELECT distinct res.route_id, sum(raillines.distance) as total_distance
    FROM raillines INNER JOIN (SELECT distinct t.station_id, t.route_id, t.next_order FROM single_route_searchB(arrival, dest, day) as t) as res
    ON raillines.station =  res.station_id AND raillines.next_order = res.next_order
    GROUP BY res.route_id
    ORDER BY total_distance ASC;
END;
$$ LANGUAGE plpgsql;

---- 1.2.4.8 Most Total Distance

CREATE OR REPLACE FUNCTION sort_most_distance(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result
AS $$
BEGIN
    RETURN QUERY
    SELECT distinct res.route_id, sum(raillines.distance) as total_distance
    FROM raillines INNER JOIN (SELECT distinct t.station_id, t.route_id, t.next_order FROM single_route_searchB(arrival, dest, day) as t) as res
    ON raillines.station =  res.station_id AND raillines.next_order = res.next_order
    GROUP BY res.route_id
    ORDER BY total_distance DESC;
END;
$$ LANGUAGE plpgsql;

------ 1.2.2. Combination Route Trip Search

DROP FUNCTION IF EXISTS combination_route_search(arrival INTEGER, dest INTEGER, day_choice VARCHAR(9));
CREATE OR REPLACE FUNCTION combination_route_search(arrival INTEGER, dest INTEGER, day_choice VARCHAR(9))
RETURNS SETOF route_combination
AS $$
BEGIN
      RETURN QUERY
      SELECT ROW_NUMBER() OVER (ORDER BY 1) AS id, Result_3.rid_1,route1_station, Result_3.rid_2, route2_station, stop_order1, stop_order2,
             Result_3.trainid, trains.cost, (EXTRACT(EPOCH FROM Result_3.minutes::TIME)/60)::INT as time
      FROM (SELECT Result_1.routeID AS rid_1,Result_1.station as route1_station, Result_2.routeID AS rid_2, Result_2.station as route2_station, Result_1.trainID,
                   Result_1.stop_order as stop_order1, Result_2.stop_order as stop_order2, Result_1.time as minutes
            FROM (SELECT Routes.routeID, stop, trainID, station, stop_order, time
                  FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
                  WHERE day = day_choice AND stop = arrival) AS Result_1
      CROSS JOIN
                 (SELECT Routes.routeID, stop, trainID, station, stop_order
                  FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
                  WHERE day = day_choice AND stop = dest) AS Result_2
            ) AS Result_3
      JOIN Trains ON Result_3.trainID = Trains.trainID
      WHERE seats > 0 AND rid_1 != rid_2;
END;
$$ LANGUAGE plpgsql;

--helper function

DROP FUNCTION IF EXISTS combination_route_search_helper(arrival INTEGER, dest INTEGER, day_choice VARCHAR(9));
CREATE OR REPLACE FUNCTION combination_route_search_helper(arrival INTEGER, dest INTEGER, day_choice VARCHAR(9))
RETURNS SETOF route_combination_helper
AS $$
  BEGIN
    RETURN QUERY
    SELECT crs.id, crs.route_id_1, routes.station, routes.stop, crs.route_id_2, crs.cost, crs.minutes, routes.next_order FROM
    routes JOIN (SELECT id, route_id_1, stop_order1 ,FLOOR(abs(stop_order2-stop_order1)/2) + abs(stop_order1) as median, route_id_2, stop_order2, cost, minutes
                 FROM combination_route_search(arrival, dest, day_choice)) as crs
    ON routes.routeid = crs.route_id_1
    WHERE routes.stop_order > crs.stop_order1 AND routes.stop_order <= crs.stop_order2;
  END;

$$ LANGUAGE plpgsql;

--1.2.2.1 Sort fewest stop combination

DROP FUNCTION IF EXISTS sort_fewest_stops_combination(arrival INTEGER, dest INTEGER, day VARCHAR);
CREATE OR REPLACE FUNCTION sort_fewest_stops_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$

BEGIN
  RETURN QUERY
    SELECT  id, route_id_1, route_id_2, sum(case when stop != -1 then 1 else 0 end) as number_of_stops FROM combination_route_search_helper(arrival, dest, day)
    GROUP BY id, route_id_1, route_id_2
    ORDER BY number_of_stops ASC;

END;
$$ LANGUAGE plpgsql;

---- 1.2.4.2. Runs Through Most Stations

CREATE OR REPLACE FUNCTION sort_most_stations_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$

 BEGIN

   RETURN QUERY
    SELECT  id, route_id_1, route_id_2, count(distinct station) as number_of_stations FROM combination_route_search_helper(arrival, dest, day)
    GROUP BY id, route_id_1, route_id_2
    ORDER BY number_of_stations DESC;
  END;
$$ LANGUAGE plpgsql;

---- 1.2.4.3. Lowest Price Sort for Combination

DROP FUNCTION IF EXISTS  sort_lowest_price_combination(arrival INTEGER, dest INTEGER, day VARCHAR);
CREATE OR REPLACE FUNCTION sort_lowest_price_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$
  BEGIN
    RETURN QUERY
    SELECT distinct id, route_id_1, route_id_2, res.cost * sum(raillines.distance) as total_distance
    FROM raillines INNER JOIN (SELECT t.id, t.route_id_1, t.route_id_2, t.station, t.next_order, t.cost FROM combination_route_search_helper(arrival, dest, day) as t) as res
    ON raillines.station =  res.station AND raillines.next_order = res.next_order
    GROUP BY res.id, res.route_id_1, res.route_id_2, res.cost
    ORDER BY total_distance ASC;
  END;
$$ LANGUAGE plpgsql;

---- 1.2.4.4 Highest Price Sort for Combination

DROP FUNCTION IF EXISTS  sort_highest_price_combination(arrival INTEGER, dest INTEGER, day VARCHAR);
CREATE OR REPLACE FUNCTION sort_highest_price_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$
  BEGIN
    RETURN QUERY
    SELECT distinct id, route_id_1, route_id_2, res.cost * sum(raillines.distance) as total_distance
    FROM raillines INNER JOIN (SELECT t.id, t.route_id_1, t.route_id_2, t.station, t.next_order, t.cost FROM combination_route_search_helper(arrival, dest, day) as t) as res
    ON raillines.station =  res.station AND raillines.next_order = res.next_order
    GROUP BY res.id, res.route_id_1, res.route_id_2, res.cost
    ORDER BY total_distance DESC;
  END;
$$ LANGUAGE plpgsql;

---- 1.2.4. Least Total Time Combination

CREATE OR REPLACE FUNCTION sort_least_time_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$
 BEGIN
   RETURN QUERY
     SELECT id, route_id_1, route_id_2, res.minutes + sum(stations.stopdelay) as duration
    FROM stations INNER JOIN (SELECT t.id, t.route_id_1, t.route_id_2, t.station, t.minutes FROM combination_route_search_helper(arrival, dest, day) as t) as res
      ON stations.stationid = res.station
    GROUP BY res.id, res.route_id_1, res.route_id_2, res.minutes
    ORDER BY duration ASC;
  END;
$$ LANGUAGE plpgsql;

---- l.2.4.6 sort most total time combination

CREATE OR REPLACE FUNCTION sort_most_time_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$
 BEGIN
   RETURN QUERY
     SELECT id, route_id_1, route_id_2, res.minutes + sum(stations.stopdelay) as duration
    FROM stations INNER JOIN (SELECT t.id, t.route_id_1, t.route_id_2, t.station, t.minutes FROM combination_route_search_helper(arrival, dest, day) as t) as res
      ON stations.stationid = res.station
    GROUP BY res.id, res.route_id_1, res.route_id_2, res.minutes
    ORDER BY duration DESC;
  END;
$$ LANGUAGE plpgsql;

---- 1.2.4.7. Least Distance for Combination

DROP FUNCTION IF EXISTS  sort_least_distance_combination(arrival INTEGER, dest INTEGER, day VARCHAR);
CREATE OR REPLACE FUNCTION sort_least_distance_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$
  BEGIN
    RETURN QUERY
    SELECT distinct id, route_id_1, route_id_2, sum(raillines.distance) as total_distance
    FROM raillines INNER JOIN (SELECT t.id, t.route_id_1, t.route_id_2, t.station, t.next_order FROM combination_route_search_helper(arrival, dest, day) as t) as res
    ON raillines.station =  res.station AND raillines.next_order = res.next_order
    GROUP BY res.id, res.route_id_1, res.route_id_2
    ORDER BY total_distance ASC;
  END;
$$ LANGUAGE plpgsql;

---- 1.2.4.8. Most Distance for Combination

DROP FUNCTION IF EXISTS  sort_most_distance_combination(arrival INTEGER, dest INTEGER, day VARCHAR);
CREATE OR REPLACE FUNCTION sort_most_distance_combination(arrival INTEGER, dest INTEGER, day VARCHAR(9)) RETURNS SETOF int_result_combination
AS $$
  BEGIN
    RETURN QUERY
    SELECT distinct id, route_id_1, route_id_2, sum(raillines.distance) as total_distance
    FROM raillines INNER JOIN (SELECT t.id, t.route_id_1, t.route_id_2, t.station, t.next_order FROM combination_route_search_helper(arrival, dest, day) as t) as res
    ON raillines.station =  res.station AND raillines.next_order = res.next_order
    GROUP BY res.id, res.route_id_1, res.route_id_2
    ORDER BY total_distance DESC;
  END;
$$ LANGUAGE plpgsql;

------ 1.2.5. Add Reservation

CREATE OR REPLACE FUNCTION add_reservation (cid INTEGER, rid INTEGER, day_choice VARCHAR(9), time_choice TIME)
RETURNS VOID
AS $$
DECLARE
  train_id INTEGER;
BEGIN
  IF (SELECT routeID
      FROM Schedules
      WHERE routeID = rid AND day = day_choice AND time = time_choice) IS NOT NULL
  THEN
     train_id = (SELECT Schedules.trainID
                 FROM Schedules JOIN Trains ON Schedules.trainID = Trains.trainID
                 WHERE routeID = rid AND day = day_choice AND time = time_choice);
     INSERT INTO Reservations
        VALUES(cid, rid, day_choice, time_choice, train_id);
  ELSE
      RAISE NOTICE 'The reservation cannot be made, because the given schedule does not exist';
  END IF;
  RETURN;
END;
$$ LANGUAGE plpgsql;

---- 1.3. Advanced Searches

------ 1.3.1.

CREATE OR REPLACE FUNCTION search_131(station_choice INTEGER, day_choice VARCHAR(9), time_choice TIME)
RETURNS SETOF train_id
AS $$
BEGIN
    RETURN QUERY
    SELECT trainID
    FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
    WHERE station = station_choice AND day = day_choice AND time = time_choice;
END;
$$ LANGUAGE plpgsql;

------ 1.3.2.

DROP VIEW IF EXISTS railline_stations;
CREATE VIEW railline_stations AS
  SELECT raillineid, array_agg(station) as station_arr
  FROM raillines
  GROUP BY raillineid;

DROP VIEW IF EXISTS route_stations;
CREATE VIEW route_stations AS
  SELECT routeid, array_agg(station) as station_arr
  FROM routes
  GROUP BY routeid;

DROP VIEW IF EXISTS route_overlap;
CREATE VIEW route_overlap AS
  SELECT v1.routeid, v1.station_arr, counter.ct
  FROM  route_stations v1, railline_stations v2
     , LATERAL (
   SELECT count(*) AS ct
   FROM   unnest(v1.station_arr) stations
   WHERE  stations = ANY(v2.station_arr::int[])
   ) counter
   WHERE counter.ct >= 2
ORDER BY routeid;

CREATE OR REPLACE FUNCTION search_132() RETURNS SETOF route_id
AS $$
BEGIN
  RETURN QUERY
  SELECT routeid
  FROM route_overlap
  GROUP BY routeid
  HAVING count(routeid) > 1;
END;
$$ LANGUAGE plpgsql;

------ 1.3.3.

CREATE OR REPLACE FUNCTION search_133() RETURNS SETOF two_routes
AS $$
BEGIN
  RETURN QUERY
  SELECT r1.routeid Route1, r2.routeid Route2
  FROM Routes r1 CROSS JOIN Routes r2
  WHERE r1.routeid < r2.routeid
  GROUP BY r1.routeid, r2.routeid
  HAVING
    count(distinct r1.station) = count(distinct r2.station) AND
    sum(CASE WHEN r1.station = r2.station THEN 1 ELSE 0 END) = count(distinct r1.station) AND
    sum(CASE WHEN r1.station= r2.station AND r1.stop = r2.stop THEN 1 ELSE 0 END) != count(distinct r1.station);
END;
$$ LANGUAGE plpgsql;

------ 1.3.4.

DROP VIEW IF EXISTS routes_schedules;
CREATE VIEW routes_schedules AS
  SELECT station, array_agg(distinct trainid order by trainid) as trainids
  FROM routes NATURAL JOIN schedules
  GROUP BY station;

DROP VIEW IF EXISTS train_arr;
CREATE VIEW train_arr AS
  SELECT array_agg(trainid) as train_arr FROM trains;

CREATE OR REPLACE FUNCTION search_134() RETURNS SETOF station_id
AS $$
BEGIN
  RETURN QUERY
  SELECT station as stationID
  FROM routes_schedules
  WHERE trainids @> (SELECT train_arr FROM train_arr) AND trainids <@ (SELECT train_arr FROM train_arr);
END
$$ LANGUAGE plpgsql;

SELECT * FROM search_134();
------ 1.3.5.

DROP VIEW IF EXISTS stop_schedules;
CREATE VIEW stop_schedules AS
  SELECT stop, array_agg(distinct trainid ) as trainids
  FROM routes NATURAL JOIN schedules
  WHERE stop != -1
  GROUP BY stop;

CREATE OR REPLACE FUNCTION search_135(station_id INTEGER) RETURNS SETOF train_id
AS $$
BEGIN
    RETURN QUERY
      SELECT distinct t.trainid
      FROM trains t
      WHERE t.trainid NOT IN (SELECT unnest(trainids) as trainid
      FROM stop_schedules
      WHERE stop = station_id)
      ORDER BY t.trainid;
END
$$ LANGUAGE plpgsql;

------ 1.3.6.

DROP VIEW IF EXISTS stop_percentage;
CREATE VIEW stop_percentage AS
  SELECT routeid as route, (sum(case when stop != -1 then 1 else 0 end)::float / count(station)::float) * 100 as percentage
  FROM routes
  GROUP BY routeid;

CREATE OR REPLACE FUNCTION search_136(percentage_input float) RETURNS SETOF route_id
AS $$
BEGIN
  RETURN QUERY
  SELECT route
  FROM stop_percentage
  WHERE percentage >= percentage_input
  ORDER BY route;
END;
$$ LANGUAGE plpgsql;

------ 1.3.7.

CREATE OR REPLACE FUNCTION search_137(route_id INTEGER) RETURNS SETOF Schedules
AS $$
BEGIN
  RETURN QUERY
  SELECT *
  FROM Schedules
  WHERE routeID = route_id;
END;
$$ LANGUAGE plpgsql;

------ 1.3.8.

CREATE OR REPLACE FUNCTION search_138(route_id INTEGER, day_choice VARCHAR(9), time_choice TIME)
RETURNS SETOF type_138
AS $$
BEGIN
    RETURN QUERY
    SELECT stop, day, time, seats
    FROM Trains JOIN (SELECT *
                      FROM Routes JOIN Schedules ON Routes.routeID = Schedules.routeID
                      WHERE Routes.routeID = route_id AND day = day_choice AND time = time_choice AND stop != -1)
                AS Result_1 ON Trains.trainID = Result_1.trainID;
END;
$$ LANGUAGE plpgsql;

-- 2. Database Administrator

---- 2.3. Delete Database

CREATE OR REPLACE FUNCTION delete_database() RETURNS VOID
AS $$
BEGIN
  TRUNCATE Stations, RailLines, Routes, Trains, Schedules, Customers, Reservations CASCADE;
  RETURN;
END;
$$ LANGUAGE plpgsql;

-- Triggers

-- This trigger decrements the available seat count of a trip's train upon making a reservation for that trip

CREATE OR REPLACE FUNCTION decrement_seats() RETURNS TRIGGER
AS $$
DECLARE
    new_reservation_tid INTEGER := NEW.trainID;
BEGIN
    UPDATE Trains
    SET seats = seats - 1
    WHERE trainID = new_reservation_tid;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS seat_trig ON Reservations;
CREATE TRIGGER seat_trig
    AFTER INSERT
    ON Reservations
    FOR EACH ROW
    EXECUTE PROCEDURE decrement_seats();
 
-- This trigger blocks a reservation attempt if there are no open seats on the train for the requested trip

CREATE OR REPLACE FUNCTION reservation_blocker() RETURNS trigger
AS $$
DECLARE
  new_reservation_tid INTEGER := NEW.trainID;
BEGIN
  IF EXISTS (SELECT *
             FROM Trains
             WHERE trainID = new_reservation_tid AND seats = 0)
  THEN
    RAISE NOTICE 'Unable to make the reservation. The train for the requested trip has no open seats.';
    RETURN NULL;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS check_seats ON Reservations;
CREATE TRIGGER check_seats
  BEFORE INSERT
  ON Reservations
  FOR EACH ROW
EXECUTE PROCEDURE reservation_blocker();

COPY Stations FROM 'C:\Users\5years\Desktop\data\new_data\Stations.txt' DELIMITER ';';
  COPY RailLines FROM 'C:\Users\5years\Desktop\data\new_data\RailLines.txt' DELIMITER ';';
  COPY Routes FROM 'C:\Users\5years\Desktop\data\new_data\Routes.txt' DELIMITER ';';
  COPY Trains FROM 'C:\Users\5years\Desktop\data\new_data\Trains.txt' DELIMITER ';';
  COPY Schedules FROM 'C:\Users\5years\Desktop\data\new_data\RouteSched.txt' DELIMITER ';';
  COPY Customers FROM 'C:\Users\5years\Desktop\data\new_data\Customers.txt' DELIMITER ';';