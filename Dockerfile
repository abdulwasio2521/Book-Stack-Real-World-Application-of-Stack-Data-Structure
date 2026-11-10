FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY src ./src
RUN mkdir out && javac -d out src/*.java

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/out ./out
COPY web ./web
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-cp", "out", "Server"]
