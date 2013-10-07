The project (storage-demo) is created to provide basic understanding for Network database.

Requirements: 
- install Neuro4J Studio (It's Eclipse based UI). More here: http://www.neuro4j.org/f/welcome-downloads 


The project creates network storage and populate it with data in different ways (queries, imports, java client update, etc).

How to use it:
- import it to Eclipse
- see code
- run code (Main class: org.neuro4j.nms.demo.roger.Main)
- open created storage with Neuro4J Studio Network Client.
    - open ./data/storage.mns with Text editor and check/set absolute path to xml file
    - open ./data/storage.mns with Neuro4J NMS Query Editor (Neuro4J NMS Query perspective)
    - run query: select () / [depth='1']
    - you should get something like on screenshot ./data/screenshots/demo-storage-in-nms-client.png
    
- practice with some NQL queries with your just created network storage. More about querying here: http://www.neuro4j.org/projects/nms/nql

!!
Demo storage persistence is based on org.neuro4j.storage.xml.XMLNeuroStorage implementation.
That implementation is suitable for small amount of data (< 1000 objects).

For large storages use different implementations (e.g. org.neuro4j.storage.solr.SolrStorage)

Have questions, issues ? 
 - check forum http://www.neuro4j.org/forum
 - write message http://www.neuro4j.org/about/contacts
 
 