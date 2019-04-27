# read JSON file and keywords.txt
# find relevance score (total sum of weight of term) and produce output

import json
import nltk
import math
from collections import OrderedDict
from pprint import pprint


def calculate_weight(freq, numDoc, totalDoc):
    term_frequency = 1+ math.log2(freq)
    IDF = math.log2(totalDoc/numDoc)
    weight = term_frequency * IDF
    return weight


def get_json_data(path):
    # keyword_path = 'keywords.txt'
    # json_path = 'inverted-index.json'

    try:
        with open(path, 'r') as file:
            json_data = json.load(file) # load takes file object
    except IOError:
        print("Error occurred when opening file {}! Please check file and directory again.".format(path))

    # pprint(json_data)
    return json_data # dictionary structure


def get_keyword_data(path):
    keyword_dict = OrderedDict()
    # keyword_index = 0
    try:
        with open(path, 'r') as file:

            porter = nltk.PorterStemmer()
            # read each line in file keywords.txt
            for each_line in file:
                # keyword_index += 1 # increment index number
                # pprint(each_line)
                # tokenizing lines
                tokens = nltk.word_tokenize(each_line) # returns substring removing puntuation

                words = [token.lower() for token in tokens if token.isalpha()] # lowercase
                keyword_index = " ".join(words)
                # stemmed = [porter.stem(word) for word in words]  # return list of stemmed words
                keyword_dict[keyword_index] = {}

               #  pprint(keyword_index)

                for word in words:
                    keyword_dict[keyword_index][word] = porter.stem(word)

                # stemmed = [porter.stem(word) for word in words] # return list of stemmed words

                # keyword_dict[keyword_index] = stemmed # keywords as list stored in dict

        # pprint(keyword_dict)
        # pprint(keyword_dict[1])
    except IOError:
        print("Error occurred when opening file {}! Please check file and directory again.".format(path))

    return keyword_dict # keyword dict


def get_score_term_weight(inv_dict, keyword_dict):
    score_dict = OrderedDict()
    # score_dict_key_idx = 0
    for keyword, keyword_list in keyword_dict.items():

        # score_dict_key_idx += 1 # incremet key index
        score_dict_key_idx = keyword
        score_dict[score_dict_key_idx] = {}
        # score_dict[score_dict_key_idx]['score'] = 0.00

        for word, stem in keyword_list.items():

            for key, value in inv_dict[stem].items():
                if key == 'DocFrequency':
                    numDoc = value
                elif key == 'TotalNumberOfDocs':
                    totalDoc = value
                else:

                    if key not in score_dict[score_dict_key_idx]:
                        score_dict[score_dict_key_idx][key] = {}
                        score_dict[score_dict_key_idx][key]['score'] = 0.00
                        score_dict[score_dict_key_idx][key][word] = 0.00 # would this work??
                    freq = value

                    score_dict[score_dict_key_idx][key][word] = calculate_weight(freq, numDoc, totalDoc)
                    score_dict[score_dict_key_idx][key]['score'] += score_dict[score_dict_key_idx][key][word]

        for word in keyword_list.keys():
            for doc, info_dict in score_dict[score_dict_key_idx].items():
                if word not in info_dict:
                    info_dict[word] = 0.000000

    # for keyword, keyword_list in keyword_dict.items():
    #     # score_dict_key_idx += 1 # incremet key index
    #     score_dict_key_idx = keyword
    #     for word in keyword_list.keys():
    #         for doc, info_dict in score_dict[score_dict_key_idx].items():
    #             if word not in info_dict:
    #                 info_dict[word] = 0.000000

    # pprint(score_dict)

    return score_dict


def sort_score_dictionary(score_dict):

    new_sorted_dict = OrderedDict()

    for key, val in score_dict.items():
        new_sorted_dict[key] = OrderedDict(sorted(val.items(), key=lambda x: -x[1]['score']))

    rank = 0
    maxScore = 0
    for key, doc_dict in new_sorted_dict.items(): # iterate big doc dict
        rank = 1
        first_doc = list(doc_dict.keys())[0]
        # pprint(first_doc)
        maxScore = new_sorted_dict[key][first_doc]['score']

        for doc, info in doc_dict.items(): # iterate info dict
            # first_doc = list(doc_dict.keys())[0]
            if maxScore == new_sorted_dict[key][doc]['score']:
                new_sorted_dict[key][doc]['rank'] = rank
            # if previous score is bigger than current iterating score
            elif maxScore > new_sorted_dict[key][doc]['score']:
                rank += 1
                new_sorted_dict[key][doc]['rank'] = rank
                maxScore = new_sorted_dict[key][doc]['score']

    # pprint(new_sorted_dict)

    return new_sorted_dict


# def print_output(keyword_dict, sorted_dict):
#
#     for keyword, keyword_list in keyword_dict.items():
#         # score_dict_key_idx = keyword
#         print("\nKeywords = {} \n".format(keyword))
#
#         for doc, info_dict in sorted_dict[keyword].items():
#             print("[{}] file={} score={:.6f}".format(info_dict['rank'], doc, info_dict['score']))
#
#             for word in keyword_list.keys():
#                 print("\tweight({}) = {:.6f}".format(word, info_dict[word]))
#
#             print()

def print_output(sorted_dict):

    for key, doc_dict in sorted_dict.items():
        # score_dict_key_idx = keyword
        print("\nKeywords = {} \n".format(key))

        words = key.split()

        for doc, info_dict in doc_dict.items():
            print("[{}] file={} score={:.6f}".format(info_dict['rank'], doc, info_dict['score']))

            for word in words:
                print("\tweight({}) = {:.6f}".format(word, info_dict[word]))

            print()


def main():
    keyword_path = 'keywords.txt'
    json_path = 'inverted-index.json'

    json_dict = get_json_data(json_path)
    keyword_dict = get_keyword_data(keyword_path)

    score_dict = get_score_term_weight(json_dict, keyword_dict)

    sorted_dict = sort_score_dictionary(score_dict)

    print_output(sorted_dict)


if __name__== '__main__':
    main()
