def transc = "http://purl.uniprot.org/uniprot/transcribedInto"
def transl = "http://purl.uniprot.org/uniprot/tranlatedInto"

def uniprot2ensembl = [:]
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
	uniprot2ensembl[uniprot] = protein
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




def uniprot2rhea = [:].withDefault { new TreeSet() }
/*
 SELECT DISTINCT
  ?protein
  ?rhea 
WHERE {
  ?protein up:reviewed true .
  ?protein up:annotation ?a .
  ?a a up:Catalytic_Activity_Annotation .
  ?a up:catalyzedPhysiologicalReaction ?rhea .
}
 */
first = true
new File("data/uniprot2rhea.csv").splitEachLine(",") { line ->
    if (first) 
	first = false
    else 
	uniprot2rhea[line[0]].add(line[1])
}

def rhea2chebiin = [:].withDefault { new TreeSet() }
/*
SELECT ?chem ?chemname ?rhea ?reactionSide1 ?equation
WHERE {
  ?chem up:name ?chemname .
  ?rhea rh:substrates ?reactionSide1 .
  ?rhea rh:products ?reactionSide2 .
  ?reactionSide1  rh:contains / rh:compound / rh:chebi ?chem .
  ?reactionSide1 rh:transformableTo ?reactionSide2 .
  ?rhea rh:equation ?equation .
}

 */
first = true
new File("data/rhea-inputs.txt").splitEachLine(",") { line ->
    if (first)
	first = false
    else {
	def chebi = line[0]
	def rhea = line[2]
	rhea2chebiin[rhea].add(chebi)
    }
}

def rhea2chebiout = [:].withDefault { new TreeSet() }
/* 
SELECT ?chem ?chemname ?rhea ?reactionSide2 ?equation
WHERE {
  ?chem up:name ?chemname .
  ?rhea rh:substrates ?reactionSide1 .
  ?rhea rh:products ?reactionSide2 .
  ?reactionSide1  rh:contains / rh:compound / rh:chebi ?chem .
  ?reactionSide1 rh:transformableTo ?reactionSide2 .
  ?rhea rh:equation ?equation .
}
 */
first = true
new File("data/rhea-outputs.txt").splitEachLine(",") { line ->
    if (first)
	first = false
    else {
	def chebi = line[0]
	def rhea = line[2]
	rhea2chebiout[rhea].add(chebi)
    }
}

uniprot2rhea.each { uni, rheas ->
    def ensembl = uniprot2ensembl[uni]
    if (ensembl) {
	rheas.each { rhea ->
	    def inputs = rhea2chebiin[rhea]
	    def outputs = rhea2chebiout[rhea]
	    inputs.each {
		println "<$it> <http://2023.biohackathon.org/properties#input> <$ensembl>"
	    }
	    outputs.each {
		println "<$ensembl> <http://2023.biohackathon.org/properties#output> <$it>"
	    }
	}
    }
}


