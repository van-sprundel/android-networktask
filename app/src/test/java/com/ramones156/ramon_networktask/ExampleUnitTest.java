package com.ramones156.ramon_networktask;

import com.ramones156.networktask.NetworkTask;
import com.ramones156.networktask.RequestMethod;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private String url;
    private String actualUrl = "https://static1.e621.net/data/sample/a5/f8/a5f8cb40370cbdc6a1571d38cfa8b298.jpg";

    @Before
    public void testConnection() {
        NetworkTask nt = new NetworkTask(RequestMethod.GET, "https://e621.net/posts/2422363.json");
        nt.executeAsync(nt, (data, success) -> {
            if (success) {
                try {
                    JSONObject json = data.toJSONObject();
                    JSONObject post = json.getJSONObject("post");
                    JSONObject sample = post.getJSONObject("sample");
                    url = sample.optString("url");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Test
    public void test() {
        while (true) {
            if (url != null) {
                assertEquals(url, actualUrl);
                break;
            }
        }
    }

}