DROP TABLE IF EXISTS Stations CASCADE;
DROP TABLE IF EXISTS RailLines CASCADE;
DROP TABLE IF EXISTS Routes CASCADE;
DROP TABLE IF EXISTS Trains CASCADE;
DROP TABLE IF EXISTS Schedules CASCADE;
DROP TABLE IF EXISTS Customers CASCADE;
DROP TABLE IF EXISTS Reservations CASCADE;

DROP TYPE IF EXISTS int_result CASCADE;
DROP TYPE IF EXISTS int_result_combination CASCADE;
DROP TYPE IF EXISTS route_combination CASCADE;
DROP TYPE IF EXISTS route_id CASCADE;
DROP TYPE IF EXISTS station_id CASCADE;
DROP TYPE IF EXISTS train_id CASCADE;
DROP TYPE IF EXISTS type_121 CASCADE;
DROP TYPE IF EXISTS type_138 CASCADE;
DROP TYPE IF EXISTS route_combination_helper CASCADE;
DROP TYPE IF EXISTS type_121_b CASCADE;
DROP TYPE IF EXISTS two_routes CASCADE;

CREATE TYPE route_combination AS (id BIGINT, route_id_1 INTEGER, route1_station INTEGER, route_id_2 INTEGER, route2_station INTEGER, stop_order1 INTEGER,
stop_order2 INTEGER, trainid INTEGER, cost INTEGER, minutes INT);
CREATE TYPE route_id AS (route_id INTEGER);
CREATE TYPE station_id AS (station_id INTEGER);
CREATE TYPE train_id AS (train_id INTEGER);
CREATE TYPE type_121 AS (route_ID INTEGER, station_id INTEGER, stop_id INTEGER, train_id INTEGER, cost INTEGER, minutes INT, day VARCHAR(9));
CREATE TYPE type_121_b AS (route_ID INTEGER, station_id INTEGER, stop_id INTEGER, train_id INTEGER, cost INTEGER, minutes INT, day VARCHAR(9), next_order VARCHAR(10));
CREATE TYPE type_138 AS (stop_id INTEGER, day VARCHAR(9), time TIME, seats INTEGER);
CREATE TYPE int_result AS (route_id INTEGER, result BIGINT);
CREATE TYPE route_combination_helper AS (id BIGINT, route_id_1 INTEGER, station INTEGER, stop INTEGER, route_id_2 INTEGER, cost INTEGER, minutes INT, next_order VARCHAR(10) );
CREATE TYPE int_result_combination AS (id BIGINT, route1 INTEGER, route2 INTEGER, result BIGINT);
CREATE TYPE two_routes AS (route1 INTEGER, route2 INTEGER);

CREATE TABLE Stations
(
    stationID   INTEGER NOT NULL,
    name        VARCHAR(3),
    openTime    TIME,
    closeTime   TIME,
    stopDelay   INTEGER,
    street      VARCHAR(100),
    town        VARCHAR(100),
    zip         VARCHAR(100),
    CONSTRAINT Stations_PK
    PRIMARY KEY(stationID),
    CONSTRAINT Stations_UN_stationID
    UNIQUE(stationID)
);

CREATE TABLE RailLines
(
    railLineID   INTEGER NOT NULL,
    speedLimit   INTEGER,
    station      INTEGER,
    distance     INTEGER,
    next_order   VARCHAR(10)
);

CREATE TABLE Routes
(
    routeID    INTEGER NOT NULL,
    station    INTEGER,
    stop       INTEGER,
    stop_order INTEGER,
    next_order VARCHAR(10)
);

CREATE TABLE Trains
(
    trainID       INTEGER NOT NULL,
    name          VARCHAR(4),
    description   VARCHAR(100),
    seats         INTEGER,
    speed         INTEGER,
    cost          INTEGER,
    CONSTRAINT Trains_PK
    PRIMARY KEY(trainID),
    CONSTRAINT Trains_UN_trainID
    UNIQUE(trainID)
);

CREATE TABLE Schedules
(
    routeID   INTEGER NOT NULL,
    day       VARCHAR(9),
    time      TIME,
    trainID   INTEGER NOT NULL,
    CONSTRAINT Schedules_PK
    PRIMARY KEY(routeID, trainID),
    CONSTRAINT Schedules_FK_trainID
    FOREIGN KEY(trainID) REFERENCES Trains(trainID)
);

CREATE TABLE Customers
(
    customerID   INTEGER NOT NULL,
    firstName    VARCHAR(20),
    lastName     VARCHAR(20),
    street       VARCHAR(100),
    town         VARCHAR(100),
    zip          VARCHAR(20),
    CONSTRAINT Customers_PK
    PRIMARY KEY(customerID),
    CONSTRAINT Customers_UN_customerID
    UNIQUE(customerID)
);

CREATE TABLE Reservations
(
    customerID INTEGER NOT NULL,
    route      INTEGER,
    day        VARCHAR(9),
    time       TIME,
    trainID    INTEGER NOT NULL,
    CONSTRAINT Reservations_FK_customerID
        FOREIGN KEY (customerID) REFERENCES Customers (customerID),
    CONSTRAINT Reservations_FK_trainID
        FOREIGN KEY (trainID) REFERENCES Trains (trainID)
);