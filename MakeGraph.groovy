def transc = "http://purl.uniprot.org/uniprot/transcribedInto"
def transl = "http://purl.uniprot.org/uniprot/tranlatedInto"

def first = true
new File("data/gene-transcript-protein.txt").splitEachLine(",") { line ->
    if (first)
	first = false
    else {
	// protein, transcript, ensprotein, gene
	def uniprot = line[0]
	def transcript = line[1].replaceAll(/\.\d+/,"")
	def protein = line[2].replaceAll(/\.\d+/,"")
	def gene = line[3].replaceAll(/\.\d+/,"")
	println "<$gene> <$transc> <$transcript> ."
	println "<$transcript> <$transl> <$protein> ."
    }
}

def mirnaname2id = [:]
first = true
new File("data/miRNA.csv").splitEachLine("\t") { line ->
    if (first)
	first = false
    else {
	def id = line[0]
	def name = line[1]
	mirnaname2id[name] = id
	id = line[4]
	name = line[5]
	mirnaname2id[name] = id
	id = line[7]
	name = line[8]
	mirnaname2id[name] = id
    }
}
first = true
new File("data/hsa_MTI_ENSEMBLE.csv").splitEachLine("\t") { line ->
    if (first)
	first = false
    else {
	if (line[2].indexOf("Homo sapiens")>-1) {
	    def name = line[1]
	    def id = mirnaname2id[name]
	    def ensembl = line[-1]
	    if (id && name && ensembl && ensembl!="NA") {
		println "<http://2013.biohackathon.org/$id> <http://2013.biohackathon.org/targets> <http://rdf.ebi.ac.uk/resource/ensembl/"+ensembl+"> ."
	    }
	}
    }
}

first = true
new File("data/rhea-inputs.txt").splitEachLine(",") { line ->
    if (first)
	first = false
    else {
	
    }
}
