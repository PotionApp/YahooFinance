import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

public class OkHttpCaller {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?region=US&symbol=MSFT")
                .get()
                .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "ef638f9c7amsh7d46ee19ad0dfbcp15c2a1jsn698ed6888232")
                .build();

        try {
            Gson gson = new Gson();
            ResponseBody responseBody = client.newCall(request).execute().body();
            System.out.println(responseBody.string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
