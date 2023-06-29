import numpy as np
import pickle
import torch
from sklearn.manifold import TSNE
import matplotlib.pyplot as plt
from openTSNE import TSNE
#from MulticoreTSNE import MulticoreTSNE as TSNE
from sklearn.metrics.pairwise import cosine_similarity, euclidean_distances
from tqdm import tqdm

def find_most_similar_vector(query_vector, vector_dict):
    max_similarity = -1
    most_similar_id = None
    query_vector = query_vector.cpu().numpy()
    for id, vector in vector_dict.items():
        vector = vector.cpu().numpy()
        similarity = cosine_similarity([query_vector], [vector])[0][0]
        #similarity = euclidean_distances([query_vector], [vector], squared=True)[0][0]
        if similarity > max_similarity:
            max_similarity = similarity
            most_similar_id = id

    return most_similar_id, max_similarity

outfile = 'def_embeddings_ears.pkl'
with open(outfile, 'rb') as file:
    data = pickle.load(file)
    mp = dict()
    hp = dict()
    for key in data:
        vec = data[key]
        if key.startswith("MP:"):
            mp[key] = vec
        elif key.startswith("HP:"):
            hp[key] = vec
        else:
            print("Could not find ",key)
    for key in hp:
        vec = hp[key]
        matchid, matchsim = find_most_similar_vector(vec, mp)
        print("Match found: ", key, matchid, matchsim)
            
        
