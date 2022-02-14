# HIDE
This repository contains resrourses associated with the HIDE linking procedure described in:
Frode Eika Sandnes. 2021. HIDE: Short IDs for Robust and Anonymous Linking of Users Across Multiple Sessions in Small HCI Experiments. CHI '21 Conference on Human Factors in Computing Systems Extended Abstracts Proceedings .ACM. https://doi.org/10.1145/3411763.3451794

The resources include
1) The brower tool implemented in javascript.  A live version is available at https://frode-sandnes.github.io/HIDE/
2) A java implementation for testing and evaluation purposes (research-ware).
3) The list of names used in the experiments.

The browser tool consists of:
1) HIDE.css - a simple CSS-stylesheet for HIDE.
2) HIDE.js - JavaScript logic for HIDE.
3) index.html - the html-page for HIDE.

The code for experimentation consists of:
1) HIDE.java - a javafile with various routines to conduct experiments with HIDE.
2) freqWords.txt - a list of the 3,000 most frequent English words used as salts.

The list of names includes:
1) longlist-original.txt - the original list of names extracted from the dataset of: John PA Ioannidis, Jeroen Baas, Richard Klavans, and Kevin W. Boyack. 2019. A standardized citation metrics author database annotated for scientific field." PLoS biology 17, no. 8: e3000384.
2) longlist-unique.txt - the the original list with duplicates removed.
3) longlist.txt - the list of names with phonetic collisions removed.
