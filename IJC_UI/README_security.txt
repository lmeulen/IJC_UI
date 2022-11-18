Some certificates are based on cacert that are not commonly distributed in Java.
(As you might know Java holds its on cacerts, and does not rely on the OS cacerts).
Therefore be assured to add commonly used CA certs like Sectigo.
For Windows:
- Run a Administrator to gain acces to the java cacert file
- Add the cacert(s) to this java cacerts: keytool -import -trustcacerts -alias Sectigo -file "c:\Users\<User>\Downloads\<CaCert>.crt" -cacerts
