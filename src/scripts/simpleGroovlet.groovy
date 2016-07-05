package scripts

/**
 * Created by Reedholm on 7/2/16.
 * Actually, this code is by Renato Athaydes, from
 * a blog post he wrote.  I left this file in the
 * project as test & example file.
 *
 * Added url and querystring display as debug steps
 * to writing REST API
 */

if (!session) {
    session = request.getSession(true);

}
url = request.getRequestURL()
qrs = request.getQueryString()
if (!session.counter) {
    session.counter = 1


}

html.html {
    head {
        title 'Simple Groovlet'
    }
    body {
        h1 'Welcome to my Groovlet'
        p "The current time at this server is ${new Date()}"
        p "The session counter is now at ${session.counter}"
        p "URL: ${url}"
        p "Query String: ${qrs}"
        br()
        p "System properties:"
        ul {
            for ( prop in System.properties.keySet()) {
                li "$prop: ${System.getProperty(prop)}"
            }
        }
    }
}
