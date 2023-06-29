def first = true
def idxmap = [:]
def map = [:] // tissue/sample -> transcript -> value
new File("data/E-MTAB-5214-query-results.fpkms.tsv").splitEachLine("\t") { line ->
    if (line[0].startsWith("#")) {

    } else if (first) {
	first = false
	(2..54).each { k ->
	    idxmap[line[k]] = k
	    idxmap[k] = line[k]
	    map[line[k]] = [:]
	}
    } else {
	def transcript = line[0] // whatever id, could be gene or protein
	(2..54).each { k ->
	    // works only if "transcript" is actually a gene identifier, otherwise URL is wrong :(
	    def val = line[k]
	    def urlfrag = java.net.URLEncoder.encode(idxmap[k], "UTF-8")
	    println "<http://rdf.ebi.ac.uk/resource/ensembl/$transcript> <http://2023.biohackathon.org/datatypes#expression_value> \"$val\"^^<http://www.w3.org/2001/XMLSchema#double> <http://2023.biohackathon.org/graph/$urlfrag> ."
	}
    }
}

