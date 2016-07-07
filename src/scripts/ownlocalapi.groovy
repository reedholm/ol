package scripts

/**
 * Created by Reedholm on 7/5/16.
 * Api for ownlocal code test.
 */

@Grab('org.apache.commons:commons-csv:1.2')
import org.apache.commons.csv.CSVParser
import static org.apache.commons.csv.CSVFormat.*
import java.util.zip.GZIPInputStream;
import groovy.json.*

/**
 * Outputs text value
 * @param value text or string to output
 * @param prtline True if prtline to be used
 * @return nothing
 */
def outputtext(value, prtline) {
    response.setContentType("application/text")
    PrintWriter out = response.getWriter()
    if (prtline) {
        out.println(value)
    } else {
        out.print(value)
    }

    out.flush()
    return
}

/**
 * Outputs a comma ","
 * @return nothing
 */
def outputcomma() {
    outputtext(",", false)
    return
}

/**
 * Takes object and outputs as json
 * @param json object to be output as json
 * @return nothing
 */
def outputjson(json) {
    response.setContentType("application/json")
    PrintWriter out = response.getWriter()
    def jout = JsonOutput.toJson(json)
    out.print(jout)
    out.flush()
    return
}

/**
 * Sets response error code and outputs object message as json
 * @param err_code Http error code to set
 * @param err_msg object message to output as json
 * @return err_code
 */
def outputerror(err_code, err_msg) {
    response.setStatus(err_code)
    outputjson(err_msg)
    outputtext('', true)
    return err_code
}

/**
 * Parses http header string (param=value) for set value
 * @param header Http header string to parse
 * @param defvalue default integer value to set
 * @return integer found
 */
def getreqint(header, defvalue) {
    def retvalue = defvalue
    if (header == null) {
        return retvalue
    }
    if (header.toString().isInteger()) {
        retvalue = header as int
    }
    return retvalue
}

/**
 * Gets an integer list of header values
 * @param header Http header to parse
 * @param defvalue Default list to set
 * @return List of integers
 */
def getreqintlist(header, defvalue) {
    def retvalue = defvalue
    if (header == null) {
        return retvalue
    }
    // remove outer brackets if they exist before split
    def listvalue = ''
    if ((header[0] == '[') && (header[-1] == ']')) {
        listvalue = header[1..-2].toString().split(',')
    } else {
        listvalue = header.toString().split(',')
    }
    // build list and check that they are all integers
    listokay = true

    listvalue.each { value ->
        if (!value.toString().isInteger()) {
            listokay = false
        }
    }
    if (listokay) {
        retvalue = listvalue as List
    }
    return retvalue
}

/**
 * Returns string from Http header parameter
 * @param header Http header to parse
 * @param defvalue Default string to set
 * @return String value found
 */
def getreqstr(header, defvalue) {
    def retvalue = defvalue
    if (header == null) {
        return retvalue
    }
    retvalue = header as String
    return retvalue
}

//check if GET method was called
def method = request.getMethod()

if (!method.equals('GET')) {
    //not GET, return error of method not allowed.
    outputerror(405, { "ERROR" "Only GET requests supported!" })
    return
}

def busperpage = 50 //default # businesses per JSON page.
def busid = [] as List //default return all businesses
def bustotal = null //default return all records
def bussort = 'id' //default value to sort

// get the http header values
def headers = request.getHeaders("businesses")
if (!headers) {
    //bad request
    outputerror(400, { "ERROR" "Must have 'businesses' http header." })
    return
}

headers.each() { header ->
    if (header.toString().contains('{')) {
        //create map object of input string {}
        def map =
                header[1..-2]
                        .split(', ')
                        .collectEntries { entry ->
                    def pair = entry.split(':')
                    [(pair.first()): pair.last()]
                }
        if (map.containsKey('id')) {
            busid = getreqintlist(map.id, busid)
        }
        if (map.containsKey('perpg')) {
            busperpage = getreqint(map.perpg, busperpage)
        }
        if (map.containsKey('total')) {
            bustotal = getreqint(map.total, bustotal)
        }
        if (map.containsKey('sort')) {
            bussort = getreqstr(map.sort, bussort)
        }
    }
}

/**
 * Class used to store CSV records
 */
class Business {
    def id
    def uuid
    def name
    def address
    def address2
    def city
    def state
    def zip
    def country
    def phone
    def website
    def created_at
}

if (!session) {
    session = request.getSession(true)
    /* Read in the GZIP CSV file and parse into List class.
       Put into session attribute object so that it is only
       read in once per session.
     */
    fileurl = new URL("https://s3.amazonaws.com/ownlocal-engineering/engineering_project_businesses.csv.gz")
    URLConnection urlConnection = fileurl.openConnection()
    InputStream gzipStream = new GZIPInputStream(urlConnection.getInputStream())
    Reader decoder = new InputStreamReader(gzipStream, "UTF-8")
    BufferedReader bfile = new BufferedReader(decoder)
    def businesses = []
    bfile.withReader { reader ->
        CSVParser csv = new CSVParser(reader, DEFAULT.withHeader())
        csv.iterator().each { record ->
            businesses << ([id        : record.id as int,
                            uuid      : record.uuid,
                            name      : record.name,
                            address   : record.address,
                            address2  : record.address2,
                            city      : record.city,
                            state     : record.state,
                            zip       : record.zip,
                            country   : record.country,
                            phone     : record.phone,
                            website   : record.website,
                            created_at: record.created_at] as Business)
        }
    }
    session.setAttribute("businesses", businesses)

}

if (!session.counter) {
    session.counter = 1

}
// get CSV records from session
def blist = session.getAttribute("businesses")

//set total number of records to output
if ((bustotal == null) || (bustotal > blist.size())) {
    bustotal = blist.size()
}

// sort list
blist.sort { a, b ->
    // Compare by requested sort value
    switch (bussort) {
        case 'id': a.id <=> b.id; break;
        case 'uuid': a.uuid <=> b.uuid; break;
        case 'name': a.name <=> b.name; break;
        case 'address': a.address <=> b.address; break;
        case 'address2': a.address2 <=> b.address2; break;
        case 'city': a.city <=> b.city; break;
        case 'state': a.state <=> b.state; break;
        case 'country': a.country <=> b.country; break;
        case 'zip': a.zip <=> b.zip; break;
        case 'phone': a.phone <=> b.phone; break;
        case 'website': a.website <=> b.website; break;
        default: a.id <=> b.id;

    }
}


if (busid != []) {
    //output businesses in list, one JSON at a time
    busid.each { outid ->
        def value = blist.findAll({ it.id == outid.toString().toInteger() })
        if (value) {
            // value is list, so output each item separately (should be one item)
            value.each { v ->
                outputjson(v)
            }
        } else {
            outputjson({ "ERROR" "Business id ${outid} not found." })
        }
        if (outid != busid[busid.size() - 1]) {
            outputcomma()
        }
    }
    outputtext('', true)
    return
}

//prevent zero error
if (busperpage <= 0) {
    //send empty, since that is what they asked for
    outputjson({ "businesses"[] })
    outputtext('', true)
    return
}

//output all the requested JSONs
for (def totalout = 0; totalout < bustotal; totalout = totalout + busperpage) {
    def jlist = []
    for (i = 0; (i < busperpage) && (totalout + i < bustotal); i++) {
        jlist << blist[i + totalout]
    }

    outputjson({ "businesses" jlist })
    if (totalout + busperpage < bustotal) {
        outputcomma()
    }
}

//send printline at end
outputtext('', true)
return