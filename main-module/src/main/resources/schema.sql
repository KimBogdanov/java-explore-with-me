DROP TABLE IF EXISTS compilations_events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id                      BIGINT                          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name                    VARCHAR(255)                    NOT NULL,
    email                   VARCHAR(255)                    NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS categories (
    id                      BIGINT                          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name                    VARCHAR(255)                    NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS locations (
    id                      BIGINT                          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name                    VARCHAR(255),
    description             VARCHAR(7000),
    latitude                DOUBLE PRECISION                NOT NULL,
    longitude               DOUBLE PRECISION                NOT NULL
    );

CREATE TABLE IF NOT EXISTS events (
    id                      BIGINT                          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation              VARCHAR(2000)                   NOT NULL,
    category_id             BIGINT                          NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    created_on              TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    description             VARCHAR(7000)                   NOT NULL,
    event_date              TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    initiator_id            BIGINT                          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    location_id             BIGINT                          NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    is_paid                 BOOLEAN                         NOT NULL,
    participant_limit       INT                             NOT NULL,
    published_on            TIMESTAMP WITHOUT TIME ZONE,
    is_request_moderation   BOOLEAN                         NOT NULL,
    state                   VARCHAR(20)                     NOT NULL,
    title                   VARCHAR(120)                    NOT NULL
    );

CREATE TABLE IF NOT EXISTS requests (
    id                      BIGINT                          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created                 TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    event_id                BIGINT                          NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    requester_id            BIGINT                          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status                  VARCHAR(20)                     NOT NULL,
    CONSTRAINT requester_unique UNIQUE (event_id, requester_id)
    );

CREATE TABLE IF NOT EXISTS compilations (
    id                      BIGINT                          GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned                  BOOLEAN                         NOT NULL,
    title                   VARCHAR(50)                     NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS compilations_events (
    compilations_id         BIGINT                          NOT NULL REFERENCES compilations (id) ON DELETE CASCADE,
    events_id               BIGINT                          NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT compilations_events_unique UNIQUE (compilations_id, events_id)
    );