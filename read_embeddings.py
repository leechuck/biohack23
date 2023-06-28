import numpy as np
import pickle
from sklearn.manifold import TSNE
import matplotlib.pyplot as plt
from openTSNE import TSNE

def apply_tsne_parallel(dictionary):
    # Extract vectors and IDs from the dictionary
    vectors = list(dictionary.values())
    ids = list(dictionary.keys())

    # Convert vectors to numpy array
    vectors = np.array(vectors)

    # Apply t-SNE in parallel on GPU
    tsne = TSNE(n_jobs=-1, n_components=2, initialization="random", random_state=42)
    embedded_vectors = tsne.fit(vectors)

    return ids, embedded_vectors


outfile = 'def_embeddings.pkl'
with open(outfile, 'rb') as file:
    data = pickle.load(file)
    ids, embedded_vectors = apply_tsne(data)
    # Plot the embedded vectors
    plt.scatter(embedded_vectors[:, 0], embedded_vectors[:, 1])
    for i, ID in enumerate(ids):
        plt.annotate(ID, (embedded_vectors[i, 0], embedded_vectors[i, 1]))
    plt.show()

