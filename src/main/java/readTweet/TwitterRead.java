package readTweet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterRead implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main( String[] args ) throws InterruptedException{
	String token = "224181906-ovUJNIX9kKfS9Q8e5R4RoUUoqnNgnouyQIqKMXGJ";
	String tokenSecret = "OANNkOugcdJGCJYYMlxFasVvMbHZYAKlGroYIkbWGC92H";
	String consumerKey = "bnHjCOCWkiOBoEauur2iuMkXv";
	String consumerSecret = "IHUb9XpUh19qh3doDqkvMuhghi8UqWU3irmvhT0B4EBLwGrLiT";
	
	Hosts hosts = new HttpHosts("https://stream.twitter.com");
	BlockingQueue<String> tweetQueue = new LinkedBlockingQueue<String>(100000);
	StatusesFilterEndpoint twitterEndpoint = new StatusesFilterEndpoint();
	
	//List<String> trackTerms = Lists.newArrayList("politics", "election","donald trump","hillary clinton", "bobby jindal");
	
	List<String> trackTerms = Lists.newArrayList("политика","выборы","Хиллари Клинтон","Politik" , "Wahl" , " Donald Trump ", " Hillary Clinton ", " Bobby Jindal ");
	trackTerms.add("Wahl");
	trackTerms.add("Donald Trump");
	trackTerms.add("Hillary Clinton");
	trackTerms.add("Bobby Jindal");
	trackTerms.add("politics");
	trackTerms.add("elections");
	
	
	//List<String> languages = Lists.newArrayList("en","de","ru");//de german,ru - russian
	List<String> languages = Lists.newArrayList("de");
	//List<String> languages = Lists.newArrayList("en");
	twitterEndpoint.trackTerms(trackTerms);
	twitterEndpoint.languages(languages);
	
	Authentication twitterAuthentication = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);
	ClientBuilder twitterConnection = new ClientBuilder()
			.name("Twitter data read")
			.hosts(hosts)
			.authentication(twitterAuthentication)
			.endpoint(twitterEndpoint)
			.processor(new StringDelimitedProcessor(tweetQueue));
	
	Client twitterClient = twitterConnection.build();
	twitterClient.connect();
	
	String msg = null;
	int tweetCounter = 0;
	int n = 200;
	final ArrayList<String> tweets = new ArrayList<String>();
	while (!twitterClient.isDone() && tweetCounter <= n) {
		  msg = tweetQueue.take();
		  
		  //Move the tweets from queue and store it in an ArrayList before flushing
		  //it out on to disk after every 1000 tweets collected
		  
		  {
		  tweets.add(tweetQueue.take());
		  tweetCounter++;
		  }
		  
		  FileOutputStream file;
		  ObjectOutputStream fileStream = null;
		  
		  if(tweets.size() >= 1000){
			  String twitterFileName = "/Users/nikhilshekhar/Documents/workspace/Twitter/Tweets/"+new Date().getTime();
			  try {
					file = new FileOutputStream(twitterFileName);
					fileStream = new ObjectOutputStream(file);
					fileStream.writeObject(tweets);
					System.out.println("Written to file");
					tweets.clear();//Clearing the content of the ArrayList into which tweets are populated
					System.out.println("Clearing the list");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
		  
		  System.out.println("Message:"+msg);
		  System.out.println("Tweet counter:"+ tweetCounter);
		  System.out.println("List size:"+tweets.size());
		}
	
	if(tweetCounter >= n){
		FileOutputStream file;
		  ObjectOutputStream fileStream = null;
		String twitterFileName = "/Users/nikhilshekhar/Documents/workspace/Twitter/Tweets/"+new Date().getTime();
		  try {
				file = new FileOutputStream(twitterFileName);
				fileStream = new ObjectOutputStream(file);
				fileStream.writeObject(tweets);
				System.out.println("Written to file");
				tweets.clear();//Clearing the content of the ArrayList into which tweets are populated
				System.out.println("Clearing the list");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	twitterClient.stop();
	
	}

}
