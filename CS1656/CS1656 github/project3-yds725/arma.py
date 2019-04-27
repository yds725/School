# argparse
# collections
# csv
# glob
# itertools
# math 
# os
# pandas
# re
# requests
# string
# sys

import sys
import csv
import collections
from pprint import pprint


def generate_cfi_vfi_dict(dic_cfi, dic_vfi, values_stored, input_file, item_list):
    dic_cfi = collections.OrderedDict(sorted(dic_cfi.items()))
    dic_vfi = collections.OrderedDict(sorted(dic_vfi.items()))
    dic_vfi = {}
# content

    # if cfi dictionary is empty fill it in
    if len(dic_cfi) == 0:
        with open(input_file, newline='') as file:
            csv_data = csv.reader(file)
            # reader returns reader object that can iterate
            for item_set in csv_data:
                #print(item_set)
                for idx, item in enumerate(item_set):
                    item_set[idx] = item.strip() # remove trailing whitespace

                trans_id = item_set.pop(0) # remove transcation id we dont need it
                #print(item_set)

                item_list.append(item_set)
                # print(item_list)

                for index, item in enumerate(item_set):
                    item = (item.strip(),) # create tuples which represent item subsets
                    #print(item)

                    if item not in dic_cfi:
                        dic_cfi[item] = [] # store number of occruence of each subset
                        # initialize dic
                        dic_cfi[item].append(0) # support percentage
                        dic_cfi[item].append(1) # support count
                        # pprint(dic_cfi)
                    else:
                        dic_cfi[item][1] += 1 #increment counter

            # pprint(dic_cfi)

            for item in dic_cfi.keys():
                # support percentage = frequency / total number
                sup_percentage = dic_cfi[item][1] / len(item_list)

                dic_cfi[item][0] = sup_percentage
                if sup_percentage >= values_stored['msp']:
                    # if sup percentage is lower than minimum support percentage
                    dic_vfi[item] = [sup_percentage, dic_cfi[item][1]] # store percentage and count into verified dict
    else:
        for item in dic_cfi.keys():
            # support percentage = frequency / total number

            if dic_cfi[item][0] >= values_stored['msp']:
                dic_vfi[item] = [dic_cfi[item][0], dic_cfi[item][1]]
    print('dic_vfi: ')
    pprint(dic_vfi)

    return [dic_cfi, dic_vfi, item_list]

def write_freqset_on_file(dic_vfi, output_file):
    ordered_dic = collections.OrderedDict(sorted(dic_vfi.items()))
    for item, value in ordered_dic.items():
        output_file.write('S,')
        output_file.write(str('%0.4f' % value[0]))
        output_file.write(',')
        # item(subset) can be more than
        for index in range(len(item)):
            output_file.write(item[index])
            if index == len(item) - 1:
                output_file.write('\n')
            else:
                output_file.write(',')

def produce_larger_cfi(dic_cfi, dic_vfi, item_list):
    dic_cfi = collections.OrderedDict(sorted(dic_cfi.items()))
    dic_vfi = collections.OrderedDict(sorted(dic_vfi.items()))

    itemsets = list(dic_vfi.keys())
    # print(itemsets)

    if len(itemsets) < 2:
        return {} # return empty CFI to terminate process

    for i in range(len(itemsets)):
        for j in range(len(itemsets)):
            tupleA = itemsets[i]
            tupleB = itemsets[j]
            # print(tupleA)

            candidate_items = set()
            for item in tupleA:
                candidate_items.add(item)
            for item in tupleB:
                candidate_items.add(item)

            candidate_items = sorted(candidate_items)
            # pprint(candidate_items)

            if len(candidate_items) == len(itemsets[0]) + 1:
                # making subsets increasingly (i+1)
                new_tuple = () # create new tuple
                sup_count = 0
                for item in candidate_items:
                    new_tuple = new_tuple + (item,) # append items to tuples so that (A,B,)
                    # print('new tuple: ')
                    # pprint(new_tuple)

                dic_cfi[new_tuple] = [] # store values for each new tuples (subsets)

                for transaction in item_list:
                    # if tuple (item subsets) are in actual transaction
                    if set(new_tuple).issubset(transaction):
                       sup_count += 1
                    # for idx, item in enumerate(new_tuple):
                    #     if idx == len(new_tuple) - 1:

                sup_percentage = sup_count / len(item_list)
                dic_cfi[new_tuple].append(sup_percentage)
                dic_cfi[new_tuple].append(sup_count)

                # pprint(dic_cfi)
    return dic_cfi

def generate_association_rules(complete_dict, csv_output, msp_mc_dic):
    dic_rules = {}

    for idx, set1 in enumerate(complete_dict.keys()):
        for idx, set2 in enumerate(complete_dict.keys()):

            isContained = False
            for item in set1:
                if item in set2:
                    isContained = True
                    break

            if isContained: # if character is contained in other item set
                continue
            else:
                freq_set = set()

                for val in set1:
                    freq_set.add(val)
                for val in set2:
                    freq_set.add(val)

                freq_set = sorted(freq_set)
                # pprint(freq_set)

                temp_tuple = ()
                for val in freq_set:
                    temp_tuple = temp_tuple + (val,)

                #print('temp_tuple: ')
                #pprint(temp_tuple)

                if temp_tuple in complete_dict.keys():

                    sup_percentage = complete_dict[temp_tuple][0]
                    # confidence = sup(iUj)/sup(i) i=>j
                    confidence = complete_dict[temp_tuple][1] / complete_dict[set1][1]

                    if sup_percentage >= msp_mc_dic['msp'] and confidence >= msp_mc_dic['mc']:

                        dic_rules[temp_tuple] = {}
                        dic_rules[temp_tuple]['percent'] = sup_percentage
                        dic_rules[temp_tuple]['conf'] = confidence
                        dic_rules[temp_tuple]['LHS'] = set1
                        dic_rules[temp_tuple]['RHS'] = set2
                        #print('rules dict: ')
                        # pprint(dic_rules)

                        csv_output.write('R,')
                        csv_output.write(str('%0.4f' % sup_percentage))
                        csv_output.write(',')
                        csv_output.write(str('%0.4f' % confidence))
                        csv_output.write(',')

                        for item in set1:
                            csv_output.write(str(item))
                            csv_output.write(',')
                        csv_output.write("'=>'")
                        csv_output.write(',')

                        for idx, item in enumerate(set2):
                            csv_output.write(str(item))
                            if not idx == len(set2) - 1:
                                csv_output.write(',')
                        csv_output.write('\n') # if writing one association rule is finished go to next line
    return dic_rules

def main():
    input_list = sys.argv[1:]

    # reading input file name
    input_file = input_list[0]

    # reading output file name
    output_file = input_list[1]

    # reading min support percentage
    msp = float(input_list[2])

    # reading min_confidence
    mc = float(input_list[3])

    dic_cfi = {}
    dic_vfi = {}
    values_stored = {}
    complete_dict = {}
    item_list = []

    values_stored['msp'] = msp
    values_stored['mc'] = mc
    #pprint(values_stored)

    with open(output_file, 'w') as csv_output:
        while True: # use loop to consistently changes vfi
            list_of_datas = generate_cfi_vfi_dict(dic_cfi, dic_vfi, values_stored, input_file, item_list)

            dic_cfi = list_of_datas[0]
            dic_vfi = list_of_datas[1]
            item_list = list_of_datas[2]

            write_freqset_on_file(dic_vfi, csv_output) # write only sets for now
            complete_dict = {**complete_dict, **dic_vfi} # extract vfi dictionary so to gather all infos
            # print('complete dict:')
            # pprint(complete_dict)

            dic_cfi = {} # reinitialize dic_cfi

            dic_cfi = produce_larger_cfi(dic_cfi, dic_vfi, item_list)

            if len(dic_cfi) == 0:
                break
        generate_association_rules(complete_dict, csv_output, values_stored)

main()

