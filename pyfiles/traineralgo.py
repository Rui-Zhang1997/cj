import os, sys
import requests as req
from bs4 import BeautifulSoup as bsoup

ciphers = [
        "Advanced_Encryption_Standard",
        "Triple_DES",
        "Data_Encryption_Standard",
        "Blowfish_(cipher)",
        "Serpent",
        "FROG",
        "KeeLoq",
        "Threefish"
]

WIKIPEDIA_URL = 'https://en.wikipedia.org/wiki'

def getarticle(article):
    r = req.get('{}/{}'.format(WIKIPEDIA_URL, article))
    soup = bsoup(r.text, 'html.parser')
    mainbody = soup.find('div', {'class': 'mw-parser-output'})
    paragraphs = mainbody.findAll('p')
    for p in paragraphs:
        print(p)
        print()

getarticle(ciphers[0])
