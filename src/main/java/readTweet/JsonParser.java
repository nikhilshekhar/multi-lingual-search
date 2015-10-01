package readTweet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

 

public class JsonParser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	final static String SOLR_DATE_FORMAT = "yyyy'-'MM-dd'T'HH:mm:ss'Z'";
	final static String LATLONG = "coordinates";
	static Integer numOfTweets = 0;


	public static void main(String args[]){
		
			Path dir = Paths.get("/Users/nikhilshekhar/Documents/workspace/Twitter/Tweets_test");
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			    for (Path file: stream) {
			        System.out.println(file.toString());
			        read(file);
			    }
			} catch (IOException e) {
			    System.err.println(e);
			}catch(NullPointerException e){
				e.printStackTrace();
			}
	}



@SuppressWarnings("unchecked")
public static void read(Path file) throws FileNotFoundException,NullPointerException {
	FileInputStream fileInputStream= new FileInputStream (file.toString());
	ObjectInputStream twitterInputStream;
	List<TwitterRead>tweets;
	try {
		twitterInputStream = new ObjectInputStream(fileInputStream);
		tweets= (ArrayList<TwitterRead>)twitterInputStream.readObject();
		System.out.println(tweets.size());
		filterAndWriteToFile(tweets,file);
		//System.out.println("Mesaage read:"+ tweets);
		twitterInputStream.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}



private static void filterAndWriteToFile(List<TwitterRead> tweets,Path file) throws NullPointerException{
	// TODO Auto-generated method stub
	int index = 0;
	Object tweet;
	JsonElement jsonElement;
	JsonObject jsonObject;
	JsonElement createdAt,id,idStr,text,source,geo,coordinates,lang,retweeted,tweetHashTag = null,tweetUrl=null;
	JsonArray tweetUrlTemp,tweetHashTagTemp;
	ArrayList<String> hashTags = null;
	ArrayList<String> tweetUrlList = null;
//	int countOfTweetsRead = 0, countOfTweetsWritten = 0;
	int german =0, russian =0 , english =0 ;
	ArrayList<SolrPojo> listOfSolrPojo = new ArrayList<SolrPojo>();
	String language = null;
	String languageRun = "de";
	
	while(index!=tweets.size()){
//		countOfTweetsRead++;
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		tweet = tweets.get(index);
		index++;
		jsonElement = parser.parse(tweet.toString());
		jsonObject = jsonElement.getAsJsonObject();	
		
		//Extracting fields to be indexed
		createdAt = jsonObject.get("created_at");
		id = jsonObject.get("id");
		idStr = jsonObject.get("id_str");
		text = jsonObject.get("text");
		//source = jsonObject.get("source");
		geo = jsonObject.get("geo");
		coordinates = jsonObject.get("coordinates");
		lang = jsonObject.get("lang");
		retweeted = jsonObject.get("retweeted");
		tweetUrlTemp = jsonObject.getAsJsonObject("entities").getAsJsonArray("urls");
		
		if(tweetUrlTemp.size() > 0){
			int i = 0;
			tweetUrlList = new ArrayList<String>();
			while(i!= tweetUrlTemp.size()){
				tweetUrl = tweetUrlTemp.get(i).getAsJsonObject().get("expanded_url");
				tweetUrlList.add(tweetUrl.getAsString());
				System.out.println("test:"+tweetUrlList);
				i++;
			}
		}
		
		tweetHashTagTemp = jsonObject.getAsJsonObject("entities").getAsJsonArray("hashtags");
		
		if(tweetHashTagTemp.size() > 0){
			int j = 0;
			hashTags = new ArrayList<String>();
			while(j!= tweetHashTagTemp.size()){
				tweetHashTag = tweetHashTagTemp.get(j).getAsJsonObject().get("text");
					hashTags.add(tweetHashTag.getAsString());
				if(j>1){
					System.out.println("test2:"+hashTags);
				}
				j++;
			}
		}
		
		
		//created_at,id,id_str,text,source,geo,coordinates,lang,retweeted
		
//		System.out.println("Tweet:"+tweet.toString());
		String createdAtSolr = null,idStrSolr = null,textSolr = null,sourceSolr=null,geoSolr=null,coordinatesSolr=null,langSolr=null,retweetedSolr=null,formattedDate = null;
		ArrayList<String> tweetHashTagSolr = new ArrayList<String>();
		ArrayList<String> tweetUrlListSolr = new ArrayList<String>();
		
		Long idSolr = 0L;
		if(!createdAt.isJsonNull()){
			createdAtSolr = createdAt.getAsString();
			try {
				formattedDate = FormatDate(createdAtSolr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!id.isJsonNull()){
			idSolr = id.getAsLong();
		}
		
		if(!idStr.isJsonNull()){
			idStrSolr = idStr.getAsString();
		}
		
		if(!text.isJsonNull()){
			//textSolr = text.getAsString();
			textSolr = text.getAsString().replace(",", " ");
		}
		
//		if(!source.isJsonNull()){
//			sourceSolr = source.getAsString();
//		}
		
		if(!geo.isJsonNull()){
			geoSolr = geo.getAsJsonObject().get(LATLONG).toString();
		}
		
		if(!coordinates.isJsonNull()){
			coordinatesSolr = coordinates.getAsJsonObject().get(LATLONG).toString();
		}
		
		if(!lang.isJsonNull()){
			langSolr = lang.getAsString();
			if(langSolr.compareTo("de")==0){
				german++;
			}else if(langSolr.compareTo("ru")==0){
				russian++;
			}else if(langSolr.compareTo("en")==0){
				english++;
			}
		}
		
		if(!retweeted.isJsonNull()){
			retweetedSolr = retweeted.getAsString();
		}
		
		if(tweetUrl!=null){
			tweetUrlListSolr = tweetUrlList;
		}
		
		if(hashTags!=null){
			tweetHashTagSolr = hashTags;
		}
		
		//Create the json to be indexed in solr
		if(langSolr.compareTo(languageRun)==0){
			SolrPojo solrPojo = new SolrPojo(formattedDate, idStrSolr, textSolr, geoSolr, coordinatesSolr, langSolr, retweetedSolr, idSolr,tweetUrlListSolr,tweetHashTagSolr);
			listOfSolrPojo.add(solrPojo);
			language = langSolr;
			numOfTweets++;
		}
		
//		countOfTweetsWritten++;
		
	}
	
	
		Gson gson = new Gson();
		String jsonSolr = gson.toJson(listOfSolrPojo);
		System.out.println("Size of pojo:"+listOfSolrPojo.size());
		System.out.println("number of tweets:"+tweets.size());
		System.out.println("German:"+german);
		System.out.println("Russian:"+russian);
		System.out.println("English:"+english);
		System.out.println("Tweets written:"+numOfTweets);
		
		if(!jsonSolr.isEmpty()){	
		//Write the new JSON to file
		try {	
			
			String pathSplit[] = file.toString().split("/");
			String tempPath = pathSplit[pathSplit.length-2]+"/"+pathSplit[pathSplit.length-1];
			int lengthOfTempPath = tempPath.length();
			String formattedFileName = file.toString().substring(0, file.toString().length() - lengthOfTempPath) + "FormattedTweets_de1_fieldname/" + file.getFileName() +"_lang_"+languageRun+"_solr.json";
			FileWriter writer = new FileWriter(formattedFileName);
			writer.write(jsonSolr);
			writer.close();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		}

}


private static String FormatDate(String createdAtSolr) throws ParseException {
	// TODO Auto-generated method stub
	SimpleDateFormat twitterDate = new SimpleDateFormat(TWITTER_DATE_FORMAT);
    twitterDate.setLenient(true);
    Date d = twitterDate.parse(createdAtSolr);
    SimpleDateFormat solrDate = new SimpleDateFormat(SOLR_DATE_FORMAT);
    solrDate.setLenient(true);
    String dateToBeIndexed = solrDate.format(d);
	return dateToBeIndexed;
}

}