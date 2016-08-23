# Corref-Visual

This tool aims for easy reading of documents along with the highlight of coreferences and allowing the manipulation of these entities, for correcting the output of a correference resolution system, thus bringing improvements in coreference resolution process. The solution we provided is a tool using the Java programming language to create a Graphical User Interface for visualization and manipulation. 

The result is to a tool that facilitates the reading and identification of correference chains, the tool also keeps the changes made on a text so that the correference resolution system can be evaluated.

## Using the program

### Setting the input up

Executing the program requires Java 8. To import a text first, click on “Configurações > Entrada de Dados” and navigate to the root directory of your input data, which needs to be structured such as the example available [here](https://www.dropbox.com/s/sr0jy47fzu8mjb5/Corref%20Visual%20example.tar.gz?dl=0). The compressed root directory should contain three additional folders (the “logs” subdirectory may be ignored): **sentences** and **binaries**, containing the binary files for sentences and noun phrases; and **texts**, containing raw text. The raw text and both binary files **__must__** have the same filename.

After configuring the input source, click on “Arquivo > Importar Texto” and choose the raw text file you wish to import.

### Manipulating the chains

If the importing process was done correctly, there should be the raw text in the left panel. The boxes immediately to the right of the raw text should each contain a different chain of coreferent noun phrases and the right panel displays lone (non-coreferent) noun phrases.

Noun phrases may be dragged from one chain to the other. It is possible to manually create new coreference chains by clicking on button labelled “Novo grupo”.

## Future work

[ ] Flexibilization of input format to accept XML files instead.
