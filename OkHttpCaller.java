import com.google.gson.*;
import com.google.gson.JsonElement;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import netscape.javascript.JSObject;

import java.io.IOException;

import java.util.*;

public class OkHttpCaller {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?region=US&symbol=MRNA")
                .get()
                .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "635b42af5emshb39964800077642p12a021jsndaeeabe69322")
                .build();

        try
        {
            ResponseBody responseBody = client.newCall(request).execute().body();
            String json = responseBody.string();
            Gson gson = new Gson();

            JsonParser parser = new JsonParser();
            JsonElement jsonTree = parser.parse(json);

            NiceTrie trie = NiceTrie.getTrieFrom(jsonTree);
//            trie.printAllChildren();
//            System.out.println(trie.getAllKeys());
//            printTrie(trie);
//            String[] keyMap = {"earnings", "earningsChart", "currentQuarterEstimate", "raw"};
//            System.out.println(GsonMethods.getJsonElement(keyMap, 0, jsonTree));
            printJsonTree(jsonTree, "");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printTrie(NiceTrie root)
    {
        if(root.children == null)
        {
            return;
        }
        else
        {
            Set<String> keys = root.getAllKeys();
            System.out.println(keys);
            Iterator<String> iter = keys.iterator();
            NiceTrie next = new NiceTrie();
            while(iter.hasNext())
            {
                String nextKey = iter.next();
                System.out.println("Next Key: " + nextKey);
                next = root.children.get(nextKey);
//                System.out.println(next.getAllKeys());
                printTrie(next);
            }
        }
    }

    public static void printJsonTree(JsonElement root, String key)
    {
        if(root.isJsonNull())
        {
            return;
        }
        else if(root.isJsonPrimitive())
        {
            System.out.println("Value: " + root);
        }
        else if (root.isJsonObject())
        {
            JsonObject object = root.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for(Map.Entry<String, JsonElement> entry: entries)
            {
                key = key + "." + entry.getKey();
                System.out.println("Key: " + key);
                printJsonTree(entry.getValue(), key);
            }
        }
        else
        {
            JsonArray array = root.getAsJsonArray();
            for(JsonElement element: array)
            {
                if(element.isJsonObject())
                {
                    JsonObject object = element.getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
                    for (Map.Entry<String, JsonElement> entry : entries)
                    {
                        key = key + "." + entry.getKey();
                        System.out.println("Key: " + key);
                        printJsonTree(entry.getValue(), key);
                    }
                }
                else
                {
                    System.out.println("Value: " + element);
                }
            }
        }
    }
}
