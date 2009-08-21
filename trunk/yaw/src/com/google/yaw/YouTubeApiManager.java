package com.google.yaw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.FormUploadToken;
import com.google.gdata.data.youtube.UserProfileEntry;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.util.ServiceException;

/**
 * Class to handle interfacing with the Google Data Java Client Library's
 * YouTube support.
 */
public class YouTubeApiManager {

	private YouTubeService service = null;
	private static final String userEntry = "http://gdata.youtube.com/feeds/api/users/default";
	private static final String uploadToken = "http://gdata.youtube.com/action/GetUploadToken";
	private static final Logger log = Logger.getLogger(YouTubeApiManager.class.getName());

	/**
	 * Create a new instance of the class, initializing a YouTubeService object
	 * with parameters specified in appengine-web.xml
	 */
	public YouTubeApiManager() {
		String clientId = System.getProperty("com.google.yaw.YTClientID");
		String developerKey = System.getProperty("com.google.yaw.YTDeveloperKey");

		if (Util.isNullOrEmpty(clientId)) {
			log.warning("com.google.yaw.YTClientID property is not set.");
		}

		if (Util.isNullOrEmpty(developerKey)) {
			log.warning("com.google.yaw.YTDeveloperKey property is not set.");
		}

		service = new YouTubeService(clientId, developerKey);
	}

	/**
	 * Sets the AuthSub token to use for API requests.
	 * 
	 * @param token
	 *          The token to use.
	 */
	public void setToken(String token) {
		service.setAuthSubToken(token);
	}

	/**
	 * Gets the username for the authenticated user, assumes that setToken() has
	 * already been called to provide authentication.
	 * 
	 * @return The current username for the authenticated user.
	 * @throws ServiceException
	 * @throws IOException
	 */
	public String getCurrentUsername() throws IOException, ServiceException {
		try {
			UserProfileEntry profile = service.getEntry(new URL(userEntry), UserProfileEntry.class);
			return profile.getUsername();
		} catch (MalformedURLException e) {
			log.warning(e.toString());
		}

		return null;
	}

	/**
	 * Submits video metadata to YouTube to get an upload token and URL.
	 * 
	 * @param newEntry
	 *          The VideoEntry containing all video metadata for the upload
	 * @return A FormUploadToken used when uploading a video to YouTube.
	 * @throws ServiceException
	 *           Error with the API
	 * @throws IOException
	 *           Error talking to the API
	 */
	public FormUploadToken getFormUploadToken(VideoEntry newEntry) throws ServiceException,
			IOException {

		FormUploadToken token = null;

		try {
			URL uploadUrl = new URL(uploadToken);
			token = service.getFormUploadToken(uploadUrl, newEntry);
		} catch (Exception e) {
			log.warning("upload token error: " + e.toString());
		}

		return token;
	}

	public static List<String> getCategoryCodes() {
		// TODO: parse data from
		// http://gdata.youtube.com/schemas/2007/categories.cat
		// instead of returning a static array.

		List<String> categories = new ArrayList<String>();
		categories.add("News");
		categories.add("Sports");
		categories.add("Music");

		return categories;
	}
}
