import torch
from transformers import BertTokenizer, BertModel
from torch.nn.utils.rnn import pad_sequence
import numpy as np
import pickle

def read_file_into_map(file_path):
    data_map = {}

    with open(file_path, 'r') as file:
        for line in file:
            line = line.strip()
            if line:
                key, value = line.split('\t')
                data_map[key] = value

    return data_map

def extract_sentence_embeddings(sentences):
    # Load pre-trained BERT model and tokenizer
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')    
    model_name = 'bert-base-uncased'
    tokenizer = BertTokenizer.from_pretrained(model_name)
    model = BertModel.from_pretrained(model_name).to(device)

    # Tokenize input sentences
    tokenized_sentences = [tokenizer.tokenize(sentence) for sentence in sentences]
    token_ids = [tokenizer.convert_tokens_to_ids(tokens) for tokens in tokenized_sentences]
    tokens_tensor = pad_sequence([torch.tensor(ids) for ids in token_ids], batch_first=True).to(device)

    # Generate sentence embeddings
    with torch.no_grad():
        model.eval()
        tokens_tensor = tokens_tensor.to(device)
        outputs = model(tokens_tensor)
        sentence_embeddings = torch.mean(outputs.last_hidden_state, dim=1)

    return sentence_embeddings

outfile = 'def_embeddings_ears.pkl'

definitions_file = 'definitions.txt'
definitions = read_file_into_map(definitions_file)
embeddings = dict()
llist = []
klist = []
count = 0
keys = ["MP:0000017", "HP:0040080", "HP:0009894", "HP:0000400", "HP:0000133", "HP:5201013", "HP:4000135"]
for key in definitions:
    if key not in keys:
        continue
    else:
        if key.startswith("HP"):
            definitions[key] = definitions[key][:-1]
        print(key, definitions[key])
        llist = [ definitions[key] ]
        embedding = extract_sentence_embeddings(llist)
        embeddings[key] = embedding[0]
        count += 1

print(embeddings)
fp = open(outfile, 'wb')
pickle.dump(embeddings, fp)
