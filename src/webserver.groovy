/**
 * Created by Reedholm on 7/2/16.
 * Actually, this code is by Renato Athaydes, from
 * a blog post he wrote. Copies of this can also be
 * found on Github.
 * I added the server.join() call after reading Jetty
 * documentation stating it would make sure the server
 * would join the current thread.
 *
 * After installing Jetty, I first simply added the Jetty
 * jar files to the project, which got rid of the eclipse
 * and Server multiple choices messages (red text) in the IntelliJ IDE,
 * but the server couldn't find the files. I then removed the
 * individual jar files from the project and added the Jetty
 * folder as a library - IDE red text came back, but the code
 * ran correctly and found the files.
 *
 * I used this instead of apache server because it let me use
 * Groovy easily and use the IntelliJ debugger.
 *
 * To run from the command line, type:
 * "groovy webserver.groovy"
 *
 */


import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.*
import groovy.servlet.*

@Grab(group='org.eclipse.jetty.aggregate', module='jetty-all', version='7.6.15.v20140411')
def startJetty() {
    def server = new Server(8080)

    def handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    handler.contextPath = '/'
    handler.resourceBase = '.'
    handler.welcomeFiles = ['index.html']
    handler.addServlet(GroovyServlet, '/scripts/*')
    def filesHolder = handler.addServlet(DefaultServlet, '/')
    filesHolder.setInitParameter('resourceBase', './public')

    server.handler = handler
    server.start()
    server.join()
}

println "Starting Jetty, press Ctrl+C to stop."
startJetty()

