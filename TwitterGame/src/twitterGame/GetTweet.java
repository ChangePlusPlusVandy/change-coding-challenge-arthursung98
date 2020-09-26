package twitterGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class GetTweet {
	private static HttpURLConnection connection;
	
	private static String bearerToken = "AAAAAAAAAAAAAAAAAAAAAJ60HwEAAAAA0KmN9vBNqN2SitrZlyd%2FT7WawVo%3DRk99wVri"
			+ "XK11cVXfmWzHv7s9VvRa7yl06fDDgVWRVEDhf8MXI5";
	private static String elonEndpoint = "https://api.twitter.com/1.1/statuses/user_timeline.json?"
			+ "screen_name=elonmusk&count=3200&exclude_replies=true&include_rts=false";
	private static String kanyeEndpoint = "https://api.twitter.com/1.1/statuses/user_timeline.json?"
			+ "screen_name=kanyewest&count=3200&exclude_replies=true&include_rts=false";
			
	public static void main(String[] args) {
		Scanner scnr = new Scanner(System.in);
		double userAccuracy = 0.0;
		
		Tweet[] elonTweets = getTweets(elonEndpoint);
		Tweet[] kanyeTweets = getTweets(kanyeEndpoint);
		
		int elonSize, kanyeSize;
		elonSize = elonTweets.length;
		kanyeSize = kanyeTweets.length;
		
		Tweet[] fullTweetCol = new Tweet[elonSize + kanyeSize];
		
		for(int i = 0; i < elonSize; ++i) {
			fullTweetCol[i] = elonTweets[i];
		}
		
		for(int i = 0; i < kanyeSize; ++i) {
			fullTweetCol[elonSize + i] = kanyeTweets[i];
		}
		
		userAccuracy = playGame(fullTweetCol, scnr);
		System.out.printf("\nTwitter Game Over, you were correct %.2f %% of the time\n", userAccuracy * 100);
	}
	
	public static Tweet[] getTweets(String endpoint) {
		BufferedReader reader;
		StringBuffer tweetCollection = new StringBuffer();
		int connStatus;
		Tweet tweetArrays[] = null;
		
		// use HttpURLConnection
		try {
			URL url = new URL(endpoint);
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10000);
			connection.setRequestProperty("Host", "api.twitter.com");
			connection.setRequestProperty("User-Agent", "TwitterGame");
			connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
			connection.setUseCaches(false);
			
			connStatus = connection.getResponseCode();
			
			if(connStatus == 200) {
				String tweet;
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				tweet = reader.readLine();
				
				while(tweet != null) {
					tweetCollection.append(tweet);
					tweet = reader.readLine();
				}
				
				reader.close();
			} else {
				System.out.println("HttpRequest did not respond correctly, Status : " + connStatus);
				
				return null;
			}
			
			tweetArrays = parseTweetJSON(tweetCollection.toString());

		} catch (MalformedURLException e) {
			System.out.println("Malformed URL Exception");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
		
		return tweetArrays;
	}
	
	public static Tweet[] parseTweetJSON(String tweetCollection) {
		JSONArray unfilteredTweetArray = new JSONArray(tweetCollection);
		Tweet tweetArrays[] = null;
		int j = 0;

		// first find out how many tweets are original to determine size
		for(int i = 0; i < unfilteredTweetArray.length(); ++i) {
			JSONObject tweet = unfilteredTweetArray.getJSONObject(i);
			
			String text = tweet.getString("text");
			
			if(!text.contains("https://")) {  // only include tweets that dont
											  // include a link or a url
				j++;
			}
		}
		
		tweetArrays = new Tweet[j];
		j = 0;
		
		// copy only original tweets from the full tweet list and create a new array
		for(int i = 0; i < unfilteredTweetArray.length(); ++i) {
			JSONObject tweet = unfilteredTweetArray.getJSONObject(i);
			
			String userId = tweet.getJSONObject("user").getString("screen_name");
			String text = tweet.getString("text");
			
			if(!text.contains("https://")) {
				Tweet oneTweet = new Tweet(userId, text);
				tweetArrays[j] = oneTweet;
				j++;
			}
		}
		
		
		return tweetArrays;
	}
	
	public static double playGame(Tweet fullTweetCol[], Scanner scnr) {
		int playTimes = 0, correctTimes = 0;
		double userStat = 0.0;
		char replay = 'y';
		
		System.out.println("Welcome to the Twitter Game!\nPlease enter your answers without spaces in between.");
		
		while(replay == 'y') {
			++playTimes;
			
			if(playOneRound(fullTweetCol, scnr)) {
				++correctTimes;
			}
			
			System.out.print("\nPlay again? y/n");
			System.out.println();
			
			replay = scnr.nextLine().toLowerCase().charAt(0);
		}
		
		userStat = (double)correctTimes / playTimes;
		return userStat;
	}
	
	public static boolean playOneRound(Tweet fullTweetCol[], Scanner scnr) {
		Random rand = new Random();
		
		int index = rand.nextInt(fullTweetCol.length);
		String correctUser = fullTweetCol[index].getUserId();
		String textQuestion = fullTweetCol[index].getText();
		String userInput;
		
		System.out.println("\nGuess whose tweet this is : " + textQuestion);
		
		userInput = scnr.nextLine();
		
		if(userInput.equals(correctUser)) {
			System.out.println("CORRECT !");
			return true;
		}
		
		System.out.println("wrong...");
		return false;
	}
}