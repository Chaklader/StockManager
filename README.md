

    STOCK MANAGER APP


How you handle concurrent request against one endpoint?
RestControllers in Spring are Stateless by default. That means - no state inside the controller itself, and everything will be fine.


Is the backend always in a valid state? - What about Race conditions?
Spring uses Hibernate, which supports optimistic locking via versioning. So it can track concurrent updates. Besides, you use timestamps in your requests, which also can be used to check the last update time for a record.


Less than zero in stock, for example. And some validations like non-null productID and all that stuff. No data for the report.





############

Spring boot can handle simultaneously requests! You can limit the number of concurrent requests by adding server.tomcat.max-threads to your application.properties. Spring will manage a pool of connections and handle the distribution of entity managers (according to the minimum and maximum of connections you specify in your properties).
I will use synchronization mechanisms
i will start the app in backround in server which is state always working
4)yep just maven ("clean compile package")

there will be also logging system which saves and send log by email if there is an error
i will write bash script for auto start and auto deployment