# finding and make inverted index for txt files in input folder
# some useable library:
# argparse
# collections
# csv
# json
# glob
# math
# nltk
# os
# pandas
# re
# requests
# string
# sys
# time
# xml
import glob
import os
import nltk
# nltk.download() # download lookup error punkt
import json
from pprint import pprint


def create_inverted_idx(filePath):
    # filePath = './input/'

    # returning list of path name that matches pattern
    files = glob.glob(os.path.join(filePath, '*.txt'))
    # files = glob.glob(filePath)

    Inverted_Index = {}
    docs_counter = 0

    for eachFile in files:
        docs_counter+=1 # increment total docs counter
        try:
            with open(eachFile, 'r') as text:
                porter = nltk.PorterStemmer()

                file_name = eachFile.split('\\')[-1:][0] # gets file name

                # read each line in file
                for each_line in text:
                    # tokenizer divides strings into substrings
                    # word_tokenize splits word removing punctuation except period
                    tokens = nltk.word_tokenize(each_line)

                    # convert to lowercase
                    words = [token.lower() for token in tokens if token.isalpha()]

                    for word in words:
                        stemmed = porter.stem(word) # stemming word

                        # if stemmed word is in index dictionary
                        if stemmed in Inverted_Index:

                            # check if name of file is already in stemmed word dict inside inverted index dict
                            if file_name not in Inverted_Index[stemmed]:
                                # actually another dict data structure in dict
                                Inverted_Index[stemmed][file_name] = 1 # counting word per doc
                                Inverted_Index[stemmed]['DocFrequency'] += 1 # increment count of how many docs that word appears

                            else:
                                Inverted_Index[stemmed][file_name] += 1 # increment counter

                        # if key (stemmed word) is not created; initialize with default value
                        else:
                            Inverted_Index[stemmed] = {}
                            # Inverted_Index[stemmed][file_name] = 1
                            Inverted_Index[stemmed]['DocFrequency'] = 1

                            # increment count of total number of docs; this value is same for all key
                            Inverted_Index[stemmed]['TotalNumberOfDocs'] = 1
                            Inverted_Index[stemmed][file_name] = 1

            for stem in Inverted_Index:
                Inverted_Index[stem]['TotalNumberOfDocs'] = docs_counter # set value (total counter of docs) for all keys

        except IOError:
            print("Error occurred when opening file {}! Please check file and directory again.".format(eachFile))

    # pprint(Inverted_Index)
    return Inverted_Index
    # with open('inverted-index.json', 'w') as json_data:
    #     json.dump(Inverted_Index, json_data)


def create_json_file(inv_idx_dict):
    try:
        with open('inverted-index.json', 'w') as json_data:
            json.dump(inv_idx_dict, json_data)
    except IOError as err:
        print("Unable to write file! I/O error({0}): {1}".format(err.errno, err.strerror))


def main():
    filePath = './input/'
    inv_idx_dict = create_inverted_idx(filePath)
    create_json_file(inv_idx_dict)


if __name__ == '__main__':
    main()







