# Book Stack: Real World Application of the Stack Data Structure

Java backend (array-based stack, capacity 10 per stack) + Three.js 3D page.

## Files
- src/BookStack.java  array-based stack: push, pop, peek, updateTop, overflow/underflow
- src/Book.java       book data
- src/Server.java     HTTP server + API (push, peek, update, pop). Uses PORT env var, default 8080
- web/index.html      3D room (Three.js) that calls the Java API
- Dockerfile          used for online deployment

## Run locally (needs a JDK, Java 11+)
    javac -d out src/*.java
    java -cp out Server
Open http://localhost:8080 (keep the terminal open; Ctrl+C stops it)

