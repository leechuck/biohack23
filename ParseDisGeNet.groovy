def uni2ensp = [:]
def uni2ensg = [:]
def first = true
new File("data/gene-transcript-protein.txt").splitEachLine(",") { line ->
    if (first)
	first = false
    else {
	def uni = line[0].replaceAll(/.*\//,"")
	def ensp = line[2]?.replaceAll(/\./,"").replaceAll(/.*\//,"")
	def ensg = line[3]?.replaceAll(/\./,"").replaceAll(/.*\//,"")
	uni2ensp[uni] = ensp
	uni2ensg[uni] = ensg
    }
}

first = true
new File("data/gene-disease.txt").splitEachLine(",") { line ->
    if (first)
	first = false
    else {
	def gene = line[0].replaceAll(/.*\//,"")
	def disease = line[1].replaceAll("\"","")
	if (line[2].indexOf("linkedlifedata")>-1) {
	    def protein = line[2].replaceAll(/.*\//,"")
	    protein = protein.substring(0,protein.length()-1)
	    if (protein && protein!="null" && uni2ensg[protein]) {
		println "<http://rdf.ebi.ac.uk/resource/ensembl/" + uni2ensg[protein]+"> <http://2013.biohackathon.org/properties#associated_with> <$disease> ."
	    }
	}
    }
}
