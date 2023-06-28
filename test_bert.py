
import torch
from transformers import BertTokenizer, BertModel
from torch.nn.utils.rnn import pad_sequence

def extract_sentence_embeddings(sentences):
    # Load pre-trained BERT model and tokenizer
    model_name = 'bert-base-uncased'
    tokenizer = BertTokenizer.from_pretrained(model_name)
    model = BertModel.from_pretrained(model_name)

    # Tokenize input sentences
    tokenized_sentences = [tokenizer.tokenize(sentence) for sentence in sentences]
    token_ids = [tokenizer.convert_tokens_to_ids(tokens) for tokens in tokenized_sentences]
    tokens_tensor = pad_sequence([torch.tensor(ids) for ids in token_ids], batch_first=True)

    # Generate sentence embeddings
    with torch.no_grad():
        model.eval()
        outputs = model(tokens_tensor)
        sentence_embeddings = torch.mean(outputs.last_hidden_state, dim=1)

    return sentence_embeddings

# Example usage
sentences = ["Hello, how are you?", "I am doing great!", "What's your name?"]
embeddings = extract_sentence_embeddings(sentences)
print(embeddings)
