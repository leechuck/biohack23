import groovy.json.*
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

def fout = new PrintWriter(new FileWriter("genelist.txt"))

def geneid(String id) {
    String server = "https://rest.ensembl.org";
    def jsonSlurper = new JsonSlurper()
    String ext = "/xrefs/id/$id?external_db=EntrezGene";
    URL url = new URL(server + ext);
 
    URLConnection connection = url.openConnection();
    HttpURLConnection httpConnection = (HttpURLConnection)connection;

    httpConnection.setRequestProperty("Content-Type", "application/json");

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
    return json[0].primary_id
}

new File("data/E-MTAB-5214-query-results.fpkms.tsv").splitEachLine("\t") { line ->
    if (line[0].startsWith('ENS')) {
	try {
	    def id = geneid(line[0])
	    fout.println(line[0]+"\t"+id)
	} catch (Exception E) {
	    println "Missing "+line[0]
	}
    }
}
fout.flush()
fout.close()
