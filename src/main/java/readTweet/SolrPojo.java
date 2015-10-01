package readTweet;

import java.util.ArrayList;

public class SolrPojo {
	
	//createdAt,id,idStr,text,source,geo,coordinates,lang,retweeted
	//created_at,id,id_str,text,source,geo,coordinates,lang,retweeted
	private String created_at,id_str,text_de,geo,coordinates,lang,retweeted;
	private ArrayList<String> tweet_hashtags;
	private ArrayList<String> tweet_urls;
	private Long id ;
	public SolrPojo(String created_at, String id_str, String text,
			String geo, String coordinates, String lang, String retweeted,
			Long id, ArrayList<String> tweet_url,ArrayList<String> tweet_hashtag) {
		super();
		this.created_at = created_at;
//		this.id_str = id_str;
		this.text_de = text;
//		this.geo = geo;
//		this.coordinates = coordinates;
		this.lang = lang;
//		this.retweeted = retweeted;
//		this.id = id;
		this.tweet_urls = tweet_url;
		this.tweet_hashtags = tweet_hashtag;
	}
	@Override
	public String toString() {
		return "SolrPojo [created_at=" + created_at + ", id_str=" + id_str
				+ ", text=" + text_de + ", geo=" + geo + ", coordinates="
				+ coordinates + ", lang=" + lang + ", retweeted=" + retweeted
				+ ", tweet_hashtags=" + tweet_hashtags + ", tweet_urls="
				+ tweet_urls + ", id=" + id + "]";
	}
	
	
	

}
