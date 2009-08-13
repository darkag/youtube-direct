package com.google.yaw.admin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.yaw.Util;
import com.google.yaw.model.VideoSubmission;

public class GetAllSubmissions extends HttpServlet {

    private static final Logger log = Logger.getLogger(GetAllSubmissions.class
                    .getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {

        PersistenceManagerFactory pmf = Util.getPersistenceManagerFactory();
        PersistenceManager pm = pmf.getPersistenceManager();


		Query query = pm.newQuery(VideoSubmission.class);
		query.declareImports("import java.util.Date");
		query.setOrdering("updated desc");
		List<VideoSubmission> list = (List<VideoSubmission>) query.execute();

        try {
            JSONArray jsonArray = new JSONArray();

            for (VideoSubmission entry : list) {
                String videoId = entry.getVideoId();
                String assignmentId = entry.getAssignmentId();
                String articleUrl = entry.getArticleUrl();
                String title = entry.getVideoTitle();
                String description = entry.getVideoDescription();
                String tagList = entry.getVideoTagList();
                String uploader = entry.getYouTubeName();
                long updated = entry.getUpdated().getTime();
                int status = entry.getStatus().ordinal();

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("videoId", videoId);
                jsonObj.put("articleUrl", articleUrl);
                jsonObj.put("assignmentId", assignmentId);
                jsonObj.put("title", title);
                jsonObj.put("description", description);
                jsonObj.put("tags", tagList);
                jsonObj.put("uploader", uploader);
                jsonObj.put("updated", updated);
                jsonObj.put("status", status);

                jsonArray.put(jsonObj);
            }

            resp.setContentType("text/javascript");
            resp.getWriter().println(jsonArray);
        } catch(JSONException e) {
            log.warning(e.toString());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            pm.close();
        }
    }
}