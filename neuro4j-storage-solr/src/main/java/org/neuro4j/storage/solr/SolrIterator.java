package org.neuro4j.storage.solr;

import java.net.URLDecoder;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.neuro4j.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Iterates over Solr query - send multiple queries (iterate over pages) if request return more then PAGE_SIZE
 * 
 * 
 *
 * 
 */
public class SolrIterator<SolrDocument> implements Iterator<SolrDocument> {

	public static final int SOLR_QUERY_PAGE_SIZE = 100;

	/**
	 * if sorl query is short - GET method is used
	 * if solr query longer -> POST method is used
	 */
	private final int MAX_QUERY_SIZE_FOR_GET_METHOD = 3000; 
	
//	private final int QUERIED_ROWS_LIMIT = 10000; 

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private SolrServer solrServer = null;
	
	private SolrQuery solrQuery = null;
	
	private QueryResponse queryResponse = null;
	
	private long totalItemsCnt = -1;
	
	private int currentPage = -1;
	
	private int currentPositionInPage = -1;
	
	public SolrIterator(SolrServer solrServer, SolrQuery solrQuery) {
		super();
		this.solrServer = solrServer;
		this.solrQuery = solrQuery;
		
		// do first request
		this.queryResponse = query();
		
		totalItemsCnt = queryResponse.getResults().getNumFound(); 
	}

	public boolean hasNext() {
		
//		if (currentPage * PAGE_SIZE + currentPositionInPage >= QUERIED_ROWS_LIMIT - 1) 
//			return false;
		
		if (currentPage * SOLR_QUERY_PAGE_SIZE + currentPositionInPage < totalItemsCnt - 1) // totalItemsCnt - 1 - because of idx
			return true;

		return false;
	}

	public SolrDocument next() {
		
		if (!hasNext())
			return null;
		
		if (currentPositionInPage >= SOLR_QUERY_PAGE_SIZE - 1)
			this.queryResponse = query();
			
		currentPositionInPage++;
		
		SolrDocument doc = (SolrDocument) queryResponse.getResults().get(currentPositionInPage);

		return doc;
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
	private QueryResponse query()
	{
		long start = System.currentTimeMillis();
        try
        {
            // set page size - iterator is pageable. we may not need all raws.
        	solrQuery.setRows(SOLR_QUERY_PAGE_SIZE);
        	solrQuery.setStart((currentPage + 1) * SOLR_QUERY_PAGE_SIZE);
        	
        	int qLenght = solrQuery.getQuery().length();
        	if (qLenght > MAX_QUERY_SIZE_FOR_GET_METHOD)
        		queryResponse = solrServer.query(solrQuery, METHOD.POST);
        	else
        		queryResponse = solrServer.query(solrQuery);
        		
        	String sq = solrQuery.getQuery();
        	logger.info("QTime " + (System.currentTimeMillis() - start) + " ms. "  + queryResponse.getResults().size() + " docs. q = " + StringUtils.getShortStr(URLDecoder.decode(sq), 100) + " " + sq.length() + " chars " );
        	
//        	if (queryResponse.getResults().getNumFound() > MAX_QUERIED_ROWS)
//        		throw new RuntimeException("Solr response has too much rows:" + queryResponse.getResults().getNumFound() + ", max is " + MAX_QUERIED_ROWS); 
        		
        	currentPage++;
        	currentPositionInPage = -1;
        	
        	return queryResponse;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	String q = solrQuery.getQuery();
        	logger.error(" Error executing Solr query q = " + URLDecoder.decode(q), e);
    		
//        	query2file(q);
        } 
        return null;
		
	}	

}
