import groovy.json.*
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;


def toensembl(def id) {
    String server = "https://api.togoid.dbcls.jp/convert?";
    def jsonSlurper = new JsonSlurper()
    String ext = "ids=$id&route=ncbigene,ensembl_gene&format=json"
    URL url = new URL(server + ext);
    URLConnection connection = url.openConnection();
    HttpURLConnection httpConnection = (HttpURLConnection)connection;
    
    //httpConnection.setRequestProperty("Content-Type", "application/json");
    
    InputStream response = connection.getInputStream()
    int responseCode = httpConnection.getResponseCode()
    
    String output
    Reader reader = null
    try {
	reader = new BufferedReader(new InputStreamReader(response, "UTF-8"))
	StringBuilder builder = new StringBuilder();
	char[] buffer = new char[8192];
	int read;
	while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
            builder.append(buffer, 0, read);
	}
	output = builder.toString()
    } 
    finally {
	if (reader != null) try {
            reader.close(); 
	} catch (IOException logOrIgnore) {
            logOrIgnore.printStackTrace();
	}
    }
    def json = jsonSlurper.parseText(output)
    return json.results[0]
}

def gset = new LinkedHashSet()
new File("data/miRTarBase_MTI.csv").splitEachLine("\t") { line ->
    if (line[2].indexOf("Homo sapiens")>-1) {
	gset.add(line[4])
    }
}
def map = [:]
gset.each {
    println "$it\t"+toensembl(it)
//    map[it] = toensembl(it)
}

