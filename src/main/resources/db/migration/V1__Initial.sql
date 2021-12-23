CREATE EXTENSION Postgis;
CREATE EXTENSION "uuid-ossp";

CREATE TYPE grade AS ENUM (1, 2, 3);

CREATE TABLE listed_building
(
    id          uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    grade       grade NOT NULL,
    location    VARCHAR(255) NOT NULL,
    geometry    POINT NOT NULL,
    hyperlink   VARCHAR(255) NOT NULL UNIQUE,
    list_entry  VARCHAR(255) UNIQUE NOT NULL UNIQUE
);