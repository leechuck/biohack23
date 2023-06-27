def map = [:] // ensg to gene id and inverse
new File("data/custom").splitEachLine("\t") { line ->
    def ncbi = line[4]
    def ensembl = line[5]
    map[ncbi] = ensembl
    map[ensembl] = ncbi
}

def gmap = [:].withDefault { new TreeSet() } // gene id to phenotype
new File("data/genes_to_phenotype.txt").splitEachLine("\t") { line ->
    def id = line[0]
    def pheno = line[2]
    gmap[id].add(pheno)
}

def m = [:]
def lmap = [:]
new File("data/E-MTAB-5214-query-results.fpkms.tsv").splitEachLine("\t") { line ->
    if (line[0].startsWith("Gene")) {
	line.eachWithIndex { k, v ->
	    lmap[v] = k
	}
    }
}
def fmap = [:]
(2..54).each { k ->
    def fname = lmap[k]
    fname = fname.replaceAll(" ","_").replaceAll("\\(","").replaceAll("\\)","")
    def fout = new PrintWriter(new FileWriter("enrichment/"+fname+".txt"))
    fmap[k] = fout
}
new File("data/E-MTAB-5214-query-results.fpkms.tsv").splitEachLine("\t") { line ->
    if (line[0].startsWith('ENS')) {
	def geneid = map[line[0]]
	(2..54).each { k ->
	    def fout = fmap[k]
	    def spinal = line[k]
	    if (!spinal || spinal.length()==0)
		spinal = 0
	    if (geneid) {
		gmap[geneid].each { pheno ->
		    fout.println("GENE_$geneid\t$pheno\t$spinal")
		}
	    }
	}
    }
}
fmap.each { k, v ->
    v.flush()
    v.close()
}
