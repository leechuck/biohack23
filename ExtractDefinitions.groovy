def id = ""
def map = [:]
new File("ontologies/hp.obo").eachLine { line ->
    if (line.startsWith("id:")) {
	id = line.substring(3).trim()
    }
    if (line.startsWith("def:")) {
	def d = line.substring(4).trim()
	def start = d.indexOf("\"") + 1
	def end = d.lastIndexOf("\"")
	d = d.substring(start,end)
	map[id] = d
    }
}
def mmap = [:]
new File("ontologies/mp.obo").eachLine { line ->
    if (line.startsWith("id:")) {
	id = line.substring(3).trim()
    }
    if (line.startsWith("def:")) {
	def d = line.substring(4).trim()
	def start = d.indexOf("\"") + 1
	def end = d.lastIndexOf("\"")
	d = d.substring(start,end)
	if (d.indexOf("\"")>-1) {
	    d = d.substring(0, d.indexOf("\""))
	}
	mmap[id] = d
    }
}

PrintWriter fout = new PrintWriter(new FileWriter("definitions.txt"))
map.each { k, v ->
    fout.println("$k\t$v")
}
mmap.each { k, v ->
    fout.println("$k\t$v")
}
fout.flush()
fout.close()
