The project (weblogs-demo) demonstrage how network storage can be used for weblog storage.

Requirements: 
- install Neuro4J Studio (It's Eclipse based UI). More here: http://www.neuro4j.org/f/welcome-downloads 


Application shows how to network data storage can be used for logs gathering and and further usage in analysis, recommendations, etc.
Benefits of such storage is that it allows some data post-processing what makes data more prepared for recommendations, analysis, pattern detection, debugging, etc.   

Storage setup & config

1. Setup storage with Solr based persistence (org.neuro4j.storage.solr.SolrNeuroStorage)
     - Setup Solr. Solr configs are in ./storage/solr.configs/conf
     - Config Solr index URL in storage.properties (org.neuro4j.nms.client.server_url key)
	 
2. Add weblog-engine.jar to storage's lib directory (${STORAGE-HOME/lib})


Web app integreation

1. Add weblog-client.jar to your web application's lib directory

2. add weblog-client.properties to you class path. Example in conf-sample/weblog-storage.properties

3. add filter to your web.xml. You can specify filter config parameters. They will be posted with each request.


   <!-- start Weblog request logger -->
   <filter>
     <filter-name>WeblogFilter</filter-name>
     <filter-class>org.neuro4j.weblog.client.WeblogFilter</filter-class>
     <init-param>
       <param-name>site</param-name>
       <param-value>your-site.com</param-value>
     </init-param>
     <init-param>
       <param-name>init-param2</param-name>
       <param-value>init-value2</param-value>
     </init-param>
   </filter>
    
   <filter-mapping>
      <filter-name>WeblogFilter</filter-name>
      <url-pattern>/*</url-pattern>
   </filter-mapping>
   <!-- end Weblog request logger -->

4. Run your application, do some clicks and check data in storage.
   
5. Extend it to make more advanced request post-processing exactly for your application to make recommendations and data analysis more advanced.



Have questions, issues ? 
 - check forum http://www.neuro4j.org/forum
 - write message http://www.neuro4j.org/about/contacts
 
 