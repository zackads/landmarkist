CREATE EXTENSION Postgis;

ALTER TABLE listed_building
    ADD location GEOGRAPHY;