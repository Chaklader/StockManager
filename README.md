

    STOCK SALES MANAGER 
    ———————————————————
    
        This is stock sales manager app to store, retrieve items and sales statistics for a 
        provided time range. We can run the app from the IDE as regular Spring Boot app after
        configuring the database info in the "application.properties" file. Otherwise, we can 
        package it and run as JAR file. 
        
        Please, run the command of "$ mvn clean package" in the terminal which will create an 
        executable JAR file in the "/target" directory. Afterward, run the JAR file with a 
        similar command of "$  java -jar target/StockManager-0.0.1-SNAPSHOT.jar" and the app 
        will be started.
        
        
     
        The respective questions are answered below, 
        
            a. How you handle concurrent request against one endpoint?
            
                Spring boot can handle simultaneously requests. We limit the number of concurrent requests by adding 
                server.tomcat.max-threads to the "application.properties" file. Spring will manage a pool of connections 
                and handle the distribution of entity managers according to the (minimum and) maximum of connections 
                specified in the properties. Besides, RestControllers in Spring are Stateless by default. That means 
                - no state inside the controller itself, and everything will be fine.

            b. Is the backend always in a valid state? - What about Race conditions?
            
                The Hibernate supports optimistic locking via versioning. So it can track concurrent updates.
                We have also used the synchronization mechanism to avoid the race conditions.  

            c. Did you thought about some edge cases?
            
                Yes, the product ID and Stock ID can't be null and the stock value less than zero will not be 
                accepted.  

            d. Can your project be build without any errors?
            
                Yes, please, run the command of "$ mvn clean package" in the terminal.   
               
