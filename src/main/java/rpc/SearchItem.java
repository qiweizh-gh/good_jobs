package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db_MySQL.MySQLConnection;
import entity.Item;
import external.GitHubClient;

/**
 * Servlet implementation class SearchItem
 */
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public SearchItem() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 1. check if login status is valid; Return void if not.
	 * 2. read user's user_id and get geographic position.
	 * 3. read jobs the user favorited before.
	 * 4. list all jobs(items), and meanwhile add a new property for each
	 *	"favorite"
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if (!RpcHelper.isLoggedIn(request, response)) return;
        
		String userId = request.getParameter("user_id");
		double lon = -122.08;
		double lat = 37.38;
//		double lat = Double.parseDouble(request.getParameter("lat"));
//        double lon = Double.parseDouble(request.getParameter("lon"));
//		double lat = Double.parseDouble(request.getParameter("lat"));
//        double lon = Double.parseDouble(request.getParameter("lon"));

        GitHubClient client = new GitHubClient();
        List<Item> items = client.search(lat, lon, null);
        
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        connection.close();
        
        JSONArray array = new JSONArray();
        for (Item item : items) {
        	JSONObject obj = item.toJSONObject();
            obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
            array.put(obj);
        }
        RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
