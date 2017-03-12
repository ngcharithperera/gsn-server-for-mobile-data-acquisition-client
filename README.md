
* DAM4GSN_SERVER is a modified version of GSN.
* DAM4GSN_CLENT is used to collect data from sensors built into mobile sensors.
* There is a separate Android wrapper that is capable of communicating with mobile devices through IP communication. 
* Android wrapper is also added to the conf/wrapper.properties.

Android wrapper waits for DAM4GSN_CLENT to connect to it. DAM4GSN_CLENT should know the IP of DAM4GSN_SERVER.


Related Paper: *Charith Perera, Arkady Zaslavsky, Peter Christen, Ali Salehi, Dimitrios Georgakopoulos, Capturing Sensor Data from Mobile Phones using Global Sensor Network Middleware, Proceedings of the IEEE 23rd International Symposium on Personal Indoor and Mobile Radio Communications (PIMRC), Sydney, Australia, September, 2012, Pages 24-29.*

Objective of the solution:

* No web-servers or services used in the client side.
* Client originate the pushing of sensor data without prior negotiation.
* Client need to know the server's IP and the password

![enter image description here](https://i.imgur.com/LACBcRp.png)
