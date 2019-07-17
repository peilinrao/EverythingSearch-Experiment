import csv
import re

with open('KD.csv','r') as f, open('temp.csv','w+') as t:
    reader=csv.reader(f)
    writer=csv.writer(t)
    for row in reader:
        kid = row[0]
        did = row[1].replace("{",'').replace('}','').split(",")
        tf = row[2].replace("{",'').replace('}','').split(",")

        for i in range(len(did)):
            writer.writerow([kid, did[i], tf[i]])
