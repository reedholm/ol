<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>OwnLocal REST API</title>
</head>
<body>
<p>
This project is of an ownlocal REST API code test by applicant Jon Reedholm,
written in Groovy, using Apache Jetty for the server and copying GitHub example of
Groovy/Jetty server as starting point.
</p>
<p>
Development was done in IntelliJ CE on a Mac. To run you will also have to download Groovy and Jetty.
I did using homebrew:
<ul>
<li>brew install groovy</li>
<li> brew install jetty</li>
</ul>
<p>The API is called via the ownlocalapi.groovy script in the scripts folder, using curl:</p>
<li>curl -H "businesses:{} http://localhost:8080/scripts/ownlocalapi.groovy <b>(default call, 50 businesses per page, all records)</b></li>
<li>curl -H "businesses:{id:[500]} http://localhost:8080/scripts/ownlocalapi.groovy <b>(get business with id=500)</b></li>
<li>curl -H "businesses:{id:[500, 10]} http://localhost:8080/scripts/ownlocalapi.groovy <b>(get businesses with ids=500 & 10)</b></li>
<li>curl -H "businesses:{perpg:10} http://localhost:8080/scripts/ownlocalapi.groovy <b>(return a total of 10 businesses per page)</b></li>
<li>curl -H "businesses:{total:70} http://localhost:8080/scripts/ownlocalapi.groovy <b>(return a total of 70 businesses)</b></li>
<li>curl -H "businesses:{sort:name} http://localhost:8080/scripts/ownlocalapi.groovy <b>(return businesses sorted by name)</b></li>
<li>curl -H "businesses:{perpg:10, total:70} http://localhost:8080/scripts/ownlocalapi.groovy <b>(use multiple params in hmap)</b></li>

<p>The data can be sorted by any CSV column:</p>
<ul>
<li>id </b>(default)</b></li>
<li>uuid</li>
<li>name</li>
<li>address</li>
<li>address2</li>
<li>city</li>
<li>state</li>
<li>zip</li>
<li>country</li>
<li>phone</li>
<li>website</li>
<li>created_at</li>
</ul>
</body>
</html>
