CREATE EXTENSION Postgis;

CREATE TABLE listed_building
(
    id          UUID NOT NULL,
    name        VARCHAR(255),
    grade       INTEGER,
    location    GEOGRAPHY,
    listEntry   VARCHAR(255) UNIQUE,
    CONSTRAINT pk_building PRIMARY KEY (id)
);