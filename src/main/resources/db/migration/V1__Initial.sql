CREATE EXTENSION Postgis;
CREATE EXTENSION "uuid-ossp";

CREATE TABLE listed_building
(
    id              uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    grade           VARCHAR(255) NOT NULL,
    location_name   VARCHAR(255) NOT NULL,
    -- 4326 is the PostGIS id for the WGS84 coordinate system
    location        geography(POINT, 4326) NOT NULL,
    hyperlink       VARCHAR(255) NOT NULL UNIQUE,
    list_entry      VARCHAR(255) UNIQUE NOT NULL UNIQUE
);

CREATE INDEX listed_building_location ON listed_building USING GIST (location);