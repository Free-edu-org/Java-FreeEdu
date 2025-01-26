cp .env.dev .env
mvn clean package -DskipTests
docker-compose down 
docker volume rm java-byt-freeedu_mysql_data
docker-compose up

