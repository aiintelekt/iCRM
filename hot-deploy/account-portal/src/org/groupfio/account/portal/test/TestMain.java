package org.groupfio.account.portal.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.ofbiz.base.util.Debug;

import com.google.gson.Gson;

/**
 * @author Sharif
 *
 */
public class TestMain {
	public static final String MODULE = TestMain.class.getName();

	private String method1(String var1, String var2){
		return null;
	}
	
	private String method1(String var1, int var2){
		return null;
	}
	
	private String method1(String var1, String var2, String var3){
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			
			
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);

		}
				
		// rest call test with https [start]
		
		try {
			
			List<String> articleTitleList = new ArrayList<String>();
			
			getArticleTitles(0, "patricktomas", 1, articleTitleList);
			
			for (String articleTitle : articleTitleList) {
				System.out.println(articleTitle);
			}
			
			
			
			
			
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		
		
		// rest call test with https [end]
		
	}
	
	class ResultData {
		
		private String page;
		private int per_page;
		private int total;
		private int total_pages;
		
		private List<Article> data;
		
		void Result() {}

		public String getPage() {
			return page;
		}

		public void setPage(String page) {
			this.page = page;
		}

		public int getPer_page() {
			return per_page;
		}

		public void setPer_page(int per_page) {
			this.per_page = per_page;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public int getTotal_pages() {
			return total_pages;
		}

		public void setTotal_pages(int total_pages) {
			this.total_pages = total_pages;
		}

		public List<Article> getData() {
			return data;
		}

		public void setData(List<Article> data) {
			this.data = data;
		}
		
	}
	
	class Article {
		
		private String title;
		private String story_title;
		private String author;
		private int num_comments;
		private int created_at;
		
		void Article() {}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStory_title() {
			return story_title;
		}

		public void setStory_title(String story_title) {
			this.story_title = story_title;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public int getNum_comments() {
			return num_comments;
		}

		public void setNum_comments(int num_comments) {
			this.num_comments = num_comments;
		}

		public int getCreated_at() {
			return created_at;
		}

		public void setCreated_at(int created_at) {
			this.created_at = created_at;
		}

	}
	
	public static List<String> getArticleTitles(int threshold, String author, int pageNumber, List<String> articleTitleList) {
		
		try {
			String https_url = "https://jsonmock.hackerrank.com/api/articles?author="+author+"&page="+pageNumber;
			
			URL url;
			
			HttpsURLConnection con = null;
			
			url = new URL(https_url);
			con = (HttpsURLConnection) url.openConnection();
			
			con.setRequestMethod("GET");
			con.setRequestProperty("user-agent","Mozilla/5.0");
			con.setRequestProperty("Content-Type","text/plain");
			// Send post request
			con.setDoOutput(true);
			
			String responseJson = printContent(con);
			
			Gson g = new Gson();
			ResultData resultData = g.fromJson(responseJson, ResultData.class);
			
			if (resultData.getData().size() == 0) {
				return articleTitleList;
			}
			
			List<Article> data = resultData.getData();
			
			for (Article article : data) {
				String title = article.getTitle();
				String storyTitle = article.getStory_title();
				
				if (title != null) {
					articleTitleList.add(title);
				} else if (storyTitle != null) {
					articleTitleList.add(storyTitle);
				}
				
			}
			
			return getArticleTitles(threshold, author, ++pageNumber, articleTitleList);
			
		} catch (Exception e) {
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return articleTitleList;
		
	}
	
	
	
	
	
	public static String printContent(HttpsURLConnection con) {
		if (con != null) {

			try {

				
				InputStream inputStream = con.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));

				String input;
				String response = "";

				while ((input = br.readLine()) != null) {
					response += input;
				}
				br.close();
				
				return response;
						
			} catch (IOException e) {
	    		Debug.logError(e.getMessage(), MODULE);

			}
			
		}
		
		return null;

	}
	
	public static boolean isSuccessResponse(int responseCode) {
    	if (responseCode >= 200 && responseCode <= 300) {
    		return true;
    	}
    	return false;
    }

}


