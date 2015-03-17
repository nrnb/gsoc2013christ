End result of this GSOC project were two software packages instead of one.
The decision was made to create a separate package for implementing layout-algorithms. This was done so that future pathvisio plugins could also easily make use of this functionality.
The plugin is called "Paver", the Layout package is called "PathLayout".


Paver
==========
Automated Pathway creation plugin for PathVisio

Installation:
There are 2 options for installation:
1. use the released .jar file. For a tutorial on the installation see “Get it!” tutorial here.

2. compile the source code from the online repository directly(https://github.com/GSOCPathbuilder/PathBuilder)

Using PathBuilder:
Pathbuilder is made up of two parts: building a pathway and changing the layout.

Building a pathway:
Building a pathway can be done using the menu item Plugins>>PathBuilder>>Build Pathway. 

Settings:

Input Source: This can either be by a tab delimited text file, or by using the text box for manual input.

Input Type: This can either be connections or nodes. Connections are 2 data nodes connected by a line, nodes are individual data nodes which can later be connected by hand.

Input syntax:

connections:

SystemCode[:]Id [tab]  LineType [tab] SystemCode[:]Id
e.g: En:12340     normal      S:P123456
For connections, the syntax has to be exactly right, it has to contain all elements. It has to be a start node, followed by the line type, followed by the end node. A node contains a system code and an id, separated by colons.

The system code is the abbreviation of a public data source used to identify it. A full list of data sources with system codes can be found here. The id is the identifier used by the data source to identify the biological element. The line type can be a normal line with a straight line and ending with an arrow. Or a MIM-type interaction, all MIM-types available in the main PathVisio program are also available in PathBuilder. For more information on mim-types, see the PathVisio documentation here.

nodes:

Label [tab] Id [tab] DataSource
e.g: HSP90     3324      Entrez Gene
The syntax for individual nodes normally contains 3 elements, a label, an identifier and a data source.  A data source can be given by it’s full name, or by system code. An individual data node does not require all 3 elements to be given. Just 2 elements, the label and the identifier can also be used as input. Or a data node can be created based on just a label, or just an identifier. If just an identifier is given, this identifier will be the label as well.

Layout:
The layout can be changed by using Plugins>>PathBuilder>>Layout. The layout of the pathway is then reorganized with an algorithm. The algorithm used is the Force-Directed layout algorithm from the Prefuse software package.


PathLayout
==========

seperate package for layout managment in PathVisio

Currently the following layout algorithms are supported:

Balloon:
  A Layout implementation that assigns positions to vertices using associations with nested circles. (source: jung)
Fruchterman-Reingold:
  The Fruchterman-Rheingold algorithm. (source: jung)
ISOM:
  Meyer’s “Self-Organizing Map” layout. (source: jung)
Kamada-Kawai:
  The Kamada-Kawai algorithm for node layout. (source: jung)
Spring:
  A simple force-directed spring-embedder. (source: jung)
Prefuse:
  Force-Directed layout algorithm of the Prefuse software package. (source: prefuse)
 

build the jar file, or download the jar file at: https://docs.google.com/file/d/0BwqNKFmHlwYkUmhPM0UzU3Vxakk/edit. 
copy the following jar files, depending on the source, from the lib folder of PathLayout to the lib folder of your own project:
jung:
- jung-api-2.0.1.jar
- jung-graph-impl-2.0.1.jar
- jung-algorithms-2.0.1.jar
- collections-generic-4.01.jar
prefuse:
- prefuse.jar
Add the downloaded jars to your lib folder
Use the layout enumerator from the LayoutManager class to select and instantiate your layouts



More Information
==========

Website: http://plugins.pathvisio.org/paver/
Blog: http://plugins.pathvisio.org/paver/blog/
Github: https://github.com/GSOCPaver/
Javadoc Paver: http://gsocpaver.github.io/Paver/
Javadoc PathLayout: http://gsocpaver.github.io/PathLayout/
Contact: christleemans@gmail.com
