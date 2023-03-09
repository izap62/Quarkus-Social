CREATE DATABASE quarkus-social;

CREATE TABLE Users(
                    id bigserial not null primary key,
                    name varchar(80) not null,
                    age integer not null
)

CREATE TABLE Posts(
                    id bigserial not null primary key,
                    post_text varchar(180) not null,
                    dateTime timestamp not null,
                    user_Id bigInt not null references Users(id)

)

CREATE TABLE Followers(

                     id bigserial not null primary key,
                    user_id bigint not null references users (id),
                    follower_id bigint not null references users(id)
)