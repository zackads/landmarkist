CREATE TABLE listed_building
(
    id    UUID NOT NULL,
    name  VARCHAR(255),
    grade INTEGER,
    CONSTRAINT pk_building PRIMARY KEY (id)
);