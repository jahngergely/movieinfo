# Readme

### Architecture Overview
The architecture builds up from a Redis cache, MySQL server and the Spring Boot application.

### Starting the architecture 

#### For Development
1. Run Redis and MySql services
docker-compose -f docker-compose-redis-mysql.yaml up

#### For production
1. Build the jar file
./mvnw clean install
2. Start the architecture
docker-compose up
3. Endpoint available at:
http://localhost:8080/movies

### Configuration
````
omdb.apiKey=[YOUR_API_KEY]          
omdb.apiBaseUrl=http://www.omdbapi.com
omdb.pageSize=10            # page size as omdb does not provide paging information in the response

themoviedb.apiKey=[YOUR_API_KEY] 
themoviedb.apiBaseUrl=http://api.themoviedb.org/3

# 0 means we don't have a threshold, we query all pages
movieinfo.pagesThreshold=0
````

### API description
#### Services:
- movies: returns with a list of movies(title, year if presented, director if presented) matching the search patter using the selected provider
    - ```
        { 
            movies: [
                {
                    "Title": "Dennis the menace",
                    "Year": 1993,
                    "Director": "Nick Castle"
                },
                {
                    "Title": "Dennis the menace 2",
                    "Year": 1995,
                    "Director": [ "Nick Castle", "Other Director" ]
                },
                {
                    "Title": "Dennis the menace 300",
                }
            ] 
        }
#### Parameters: 
- api: type of provider to use [omdb, themoviedb]
#### Path variables
- searchTerm: search string movie should match

#### Pattern:
````
http://localhost:8080/movies/{searchTerm}?api={api}
````

#### Examples:
````
http://localhost:8080/movies/Dennis?api=omdb
http://localhost:8080/movies/Dennis?api=themoviedb
http://localhost:8080/movies/Dennis%20the%20meanace?api=themoviedb
http://localhost:8080/movies/Dennis+the+menace?api=themoviedb
````
