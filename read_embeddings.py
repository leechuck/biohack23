import numpy as np
import pickle
import torch
from sklearn.manifold import TSNE
import matplotlib.pyplot as plt
from openTSNE import TSNE
#from MulticoreTSNE import MulticoreTSNE as TSNE
from sklearn.metrics.pairwise import cosine_similarity
from tqdm import tqdm


def apply_tsne_parallel(dictionary):
    vectors = list(dictionary.values())
    ids = list(dictionary.keys())

    vectors_np = np.array([vector.cpu().numpy() for vector in vectors])    
    vectors_cuda = torch.tensor(vectors_np).cuda()
    
    tsne = TSNE(n_jobs=-1, n_components=2, random_state=42, verbose=True)
    embedded_vectors = tsne.fit(vectors_np)

    return ids, embedded_vectors



outfile = 'def_embeddings_ears.pkl'
with open(outfile, 'rb') as file:
    data = pickle.load(file)
    ids, embedded_vectors = apply_tsne_parallel(data)
    for i, ID in enumerate(ids):
        if ID.startswith("MP"):
            plt.scatter(embedded_vectors[i, 0], embedded_vectors[i, 1], c='blue', label=ID)
        elif ID.startswith("HP"):
            plt.scatter(embedded_vectors[i, 0], embedded_vectors[i, 1], c='red', label=ID)
        else:
            plt.scatter(embedded_vectors[i, 0], embedded_vectors[i, 1], c='gray', label=ID)
    #plt.scatter(embedded_vectors[:, 0], embedded_vectors[:, 1])
    #for i, ID in enumerate(ids):
    #    plt.annotate(ID, (embedded_vectors[i, 0], embedded_vectors[i, 1]))
    plt.show()

