# Test Task for PioneerPixel
To run the application, build it with Maven.

If you want to start it in IDEA, just run:
    docker-compose up -d --build

If you need to start a specific container, run:
    docker-compose --profile app up -d --build

In application.properties, comment out these lines:
    spring.redis.host=localhost
    spring.datasource.url=jdbc:postgresql://localhost:5432/postgres_db_test

Uncomment these lines:
    spring.datasource.url=jdbc:postgresql://db:5432/postgres_db_test
    spring.redis.host=redis_test

The database will be created on startup with tables from the init script.

Reasoning behind these decisions:
I was unable to start Redis in the test container, so it was mocked.
